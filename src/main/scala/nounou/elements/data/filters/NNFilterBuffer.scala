package nounou.elements.data.filters

import breeze.linalg.DenseVector
import nounou.elements.data.NNData
import nounou.ranges.{NNRangeInstantiated, NNRangeValid}
import nounou.elements.traits.NNTiming

import scala.collection.mutable.{ArrayBuffer, WeakHashMap}


//ToDo 1: Debug!!!
//ToDo 3: HashMap to Int or Long Hash key
//ToDo 4: parallelize?
//ToDo 4: anticipate?

/**
  * Buffer filter, which will save intermediate calculation results for an XData object.
  */
class NNFilterBuffer(private var _parent: NNData ) extends NNFilter(_parent) {

  override def timing(): NNTiming = _parent.timing()

  var buffer: WeakHashMap[Long, DenseVector[Double]] = new ReadingHashMapBuffer()
  var garbageQue: ArrayBuffer[Long] = new ArrayBuffer[Long]()

  val bufferPageLength: Int = (32768 / 2) //default page length will be 32 kB
  lazy val garbageQueBound: Int = 1024 // * 16 //32MB in data + //1073741824 / 8 / (bufferPageLength * 2)  //default buffer maximum size will be 128 MB
  //val maxInt64: Long = Long.MaxValue // pow(2d, 64d).toLong
  val maxChannel = 131072L
  val maxSegment = 1073741824L / maxChannel
  val maxPage = Long.MaxValue / maxChannel / maxSegment
  val maxPageChannel = maxPage * maxChannel

  def bufferHashKey(channel: Int, startPage: Int, segment: Int): Long = startPage + maxPage*channel + maxPageChannel*segment
  def bufferHashKeyToPage( hashKey: Long ): Int = (hashKey % maxPage).toInt
  def bufferHashKeyToChannel( hashKey: Long ): Int = ((hashKey / maxPage) % maxChannel).toInt
  def bufferHashKeyToSegment( hashKey: Long ): Int = (hashKey / maxPageChannel).toInt

  logger.debug("initialized XDataFilterTrBuffer w/ bufferPageLength={} and garbageQueBound={}", bufferPageLength.toString, garbageQueBound.toString)

  override def toStringImpl() = s"pageLen=${bufferPageLength}, queBound=${garbageQueBound}, "
  override def toStringFullImpl() = ""

  // <editor-fold defaultstate="collapsed" desc=" changes (NNData related) and flushing ">

  override def changedDataImpl() = flushBuffer()

  override def changedDataImpl(channel: Int) = flushBuffer( channel )

  override def changedDataImpl(channels: Array[Int]) = flushBuffer( channels )

  def flushBuffer(): Unit = {
    //The following null clauses are for initialization
    //they should not be necessary if the class variables were fully initialized before these calls, but...
    if( buffer == null ) buffer = new ReadingHashMapBuffer() else buffer.clear()
    if( garbageQue == null ) garbageQue = new ArrayBuffer[Long]() else garbageQue.clear()
    //logger.debug("flushBuffer() pre, buffer.size={}, garbageQue.length={}", buffer.size.toString, garbageQue.length.toString)
//    buffer.clear()
//    garbageQue.clear()
  }

  def flushBuffer(channel: Int): Unit = {
    logger.debug( "flushBuffer({}) conducted", channel.toString )
    buffer = buffer.filter( ( p:(Long, DenseVector[Double]) ) => ( bufferHashKeyToChannel(p._1) != channel ) )
    garbageQue = garbageQue.filter( ( p: Long ) => (bufferHashKeyToChannel(p) != channel) )
  }

  def flushBuffer(channels: Array[Int]): Unit = {
    logger.debug( "flushBuffer({}) conducted", channels.toString )
    buffer = buffer.filterNot( ( p:(Long, DenseVector[Double]) ) => ( channels.contains( bufferHashKeyToChannel(p._1) ) ) )
    garbageQue = garbageQue.filterNot( ( p:Long ) => ( channels.contains( bufferHashKeyToChannel(p) ) ) )
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" data reading ">

  def getBufferPage(frame: Int) = frame/bufferPageLength
  def getBufferIndex(frame: Int) = frame%bufferPageLength

  override def readPointImpl(channel: Int, frame: Int, segment: Int): Double = {
    loggerRequire(channel<maxChannel, "Cannot buffer more than {} channels!", maxChannel.toString)
    loggerRequire(segment<maxSegment, "Cannot buffer more than {} segments!", maxSegment.toString)
    buffer( bufferHashKey(channel, getBufferPage(frame), segment) )( getBufferIndex(frame) )
  }

  override def readTraceDVImpl(channel: Int, range: NNRangeValid): DenseVector[Double] = {
    loggerRequire(channel<maxChannel, "Cannot buffer more than {} channels!", maxChannel.toString)
    loggerRequire(range.segment<maxSegment, "Cannot buffer more than {} segments!", maxSegment.toString)

      val startPage =  getBufferPage( range.start )
      val startIndex = getBufferIndex( range.start )
      val endPage =    getBufferPage( range.last )
      val endIndex =   getBufferIndex( range.last )

      if(startPage == endPage) {
        //if only one buffer page is involved...
        buffer( bufferHashKey(channel, startPage, range.segment) ).slice(startIndex, endIndex + range.step, range.step)

      } else {

        //ToDo 3: buffer for step !=1 differently?

        //if more than one buffer page is involved...
        val tempret = DenseVector( new Array[Double](
          new NNRangeInstantiated(range.start, range.last, step = 1, range.segment).length
        ) )  //initialize return array, with step = 1!!!
        var currentIndex = 0 //index within tempret

        //first, deal with startPage separately... startPage will always run to end given startPage != endPage
        var increment = (bufferPageLength-startIndex)
        tempret(currentIndex until currentIndex + increment ) :=
                      buffer( bufferHashKey(channel, startPage, range.segment) ).slice(startIndex, bufferPageLength)
        currentIndex += increment

        var page = startPage + 1

        //read full pages until right before endPage
        increment = bufferPageLength
        while( page < endPage ) {
          tempret(currentIndex until currentIndex + increment) :=
                      buffer(bufferHashKey(channel, page, range.segment)).slice(0, bufferPageLength)
          currentIndex += increment
          page += 1
        }

        //deal with endPage separately
        increment = endIndex + 1
        tempret(currentIndex until currentIndex + increment ) :=
                      buffer( bufferHashKey(channel, endPage, range.segment) ).slice(0, increment)

        tempret(0 until tempret.length by range.step)
      }

  }

  // </editor-fold>


  // <editor-fold defaultstate="collapsed" desc=" ReadingHashMapBuffer ">

  //redirection function to deal with scope issues regarding super
  private def tempTraceReader(ch: Int, rangeFrValid: NNRangeValid) = _parent.readTraceDVImpl(ch, rangeFrValid)

  /**
    * This class implements a WeakHashMap that allows loading if the relevant key is not present
    */
  private class ReadingHashMapBuffer extends WeakHashMap[Long, DenseVector[Double]] {

    //do not use applyOrElse!
    override def apply( key: Long ): DenseVector[Double] = {
      val index = garbageQue.indexOf( key )
      if( index == -1 ){
        if(garbageQue.size >= garbageQueBound ){
          this.remove( garbageQue(1) )
          garbageQue.drop(1)
        }
        garbageQue.append( key )
        default( key )
      }else{
        garbageQue.remove( index )
        garbageQue.append( key )
        super.apply(key)
      }
    }

    override def default( key: Long  ): DenseVector[Double] = {
      val startFrame = bufferHashKeyToPage(key) * bufferPageLength
      val endFramePlusOne: Int =
        scala.math.min( startFrame + bufferPageLength, timing.segmentLength( bufferHashKeyToSegment(key) ) )
      val returnValue =
        tempTraceReader(
            bufferHashKeyToChannel(key),
            new NNRangeValid(startFrame, endFramePlusOne-1, 1, bufferHashKeyToSegment(key))
        )
      this.+=( key -> returnValue )
      returnValue
    }
  }

  // </editor-fold>

}
