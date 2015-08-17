package nounou.elements

import breeze.numerics.round
import nounou.elements.ranges.SampleRangeSpecifier

/** This class encapsulates the following timing information about electrophysiological/imaging data:
  *   + sample rate (Double, in Hz)
  *   + segment lengths (Array[Int]): many data formats have multiple "segments" (i.e. the recording was
  *           started or stopped during the session). The val segmentLengths contains an Array[Int]
  *           of the number of data samples per segment. Segments can also result from data being
  *           "segmented," for example by an EEG technician going through a long recording and
  *           extracting parts which may be interesting.
  *   + timestamps (Array[BigInt]): many data formats also have "timestamps" (e.g. microsecond timer
  *           values for each sample). This class will keep track of the beginning timestamp of each
  *           segment. From this information, it can calculate the timestamp of any event in between
  *
  * This class should be used within [[nounou.elements.NNElement]] children by expanding the
  * [[nounou.elements.NNDataTimingElement]] trait, which will allow one NNDataTiming
  * object to be set for each NNDataTimingElement.
  *
  * Envisioned uses are for the following children of [[nounou.elements.NNElement]]:
  *   + [[nounou.elements.data.NNData]]
  *   + [[nounou.elements.layouts.NNDataLayout]]
  *   + [[nounou.elements.spikes.NNSpikes]]
  *
  * @param sampleRate Sample rate in Hz.
  * @param _segmentLengths Total number of frames in each segment. Must be specified and non-null.
  * @param _segmentStartTimestamps  List of starting timestamps for each segment.
  *                                 Null will result in warning and this value set to defaults,
  *                                 which start at zero timestamp and assume no timestamp gaps between segments
  */
class NNDataTiming( val sampleRate: Double,
                    private val _segmentLengths: Array[Int],
                    private val _segmentStartTimestamps: Array[BigInt]
                    ) extends NNElement {
  
  // <editor-fold defaultstate="collapsed" desc=" variable checks ">
  
  if(sampleRate <= 0d ) throw loggerError("Sample rate must be non-negative!")

  /**How many data points each segment contains. If the data is single-segment (e.g. many imaging formats),
   * The array will have length = 1.
   */
  val segmentLengths: Array[Int] = {
    if(_segmentLengths == null) throw loggerError("Must specify a non-null segmentLengths as Array[Int]!")
    else _segmentLengths
  }

  /** Number of segments in data. Lazily calculated from [[segmentLengths]].length
    */
  lazy val segmentCount: Int = segmentLengths.length

  /** At what timestamp each segment starts. This information can be used to correlate
    * timestamp-based data (for example, event port codes) with continuous electrophysiology/imaging data.
    */
  val segmentStartTimestamps: Array[BigInt] = {
    if( _segmentStartTimestamps == null ){
        logger.warn("segmentStartTimestamps was not specified (or null). It will be set to defaults (starting with zero, no segment gaps).")
        _segmentLengths.scanLeft( BigInt(0) )( (x: BigInt, y: Int) => x + ( factorTsPerFr * y ).toLong ).dropRight(1)
    } else if (_segmentStartTimestamps.length != _segmentLengths.length ){
          throw loggerError(
            s"_segmentStartTimestamps.length=${_segmentStartTimestamps.length} is not equal to " +
            s"_segmentLengths.length=${_segmentLengths.length}!")
    } else  _segmentStartTimestamps //Check for increasing timestamps is not done
  }
  

  // <editor-fold defaultstate="collapsed" desc=" utility error function (private) ">

  /** Throws IllegalArgumentException if segmentCount != 1... use as check for functions which assume segment = 0.
    * @param func name of current function/signature called (which assumes segment = 0 )
    * @param altFunc  name of function/signature which should be called instead, with explicit specification of segment = 0
    */
  @throws[IllegalArgumentException]
  private def errorIfMultipleSegments(func: String, altFunc: String): Unit = {
    loggerRequire(
      segmentCount == 1,
      func + " should not be used if the file has more than one segment. Use " + altFunc + " instead"
    )
  }

  // </editor-fold>

  /** This is the inverse of [[sampleRate]], lazily buffered for convenience.
    */
  final lazy val sampleInterval = 1.0/sampleRate

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" segmentStartFrames  ">

  private final lazy val segmentStartFrames: Array[Int] = {
    var sum = 0
    ( for(seg <- 0 until segmentCount) yield {sum += segmentLength(seg); sum} ).toArray.+:(0).dropRight(1)
  }
  /**Cumulative frame numbers for the start of a given segment.
    */
  final def segmentStartFrame(segment: Int) = segmentStartFrames.apply(segment)

  /** The cumulative frame number of a given frame in a given segment.
    * Useful for data formats which store data from multiple segments in a flat array structure.
    */
  final def cumulativeFrame(frame: Int, segment: Int) = segmentStartFrame(segment) + frame

  // </editor-fold>


  // <editor-fold defaultstate="collapsed" desc=" functions for reading segment lengths ">

  /** Length of a given segment. If you are dealing with 1 segment data,
    * a value of -1 (default) is also possible.
    */
  final def segmentLength( segment: Int ): Int = segmentLengthImpl( getRealSegment(segment))
  /** Length of the one and only segment. This signature can
    * only be used if the data only contains one segment.
    */
  final def segmentLength(): Int = segmentLength( -1 )
//  final def segmentLengthReal( segment: Int ): Int =
//    segmentLengthImpl( segmentLength(segment) )

  /** Implementation of [[segmentLength(Int)]]. Segment number must be a valid number
    * in the range [0, segmentCount).
    */
  def segmentLengthImpl( segment: Int ): Int = segmentLengths( segment )

  final def getRealSegment( segment: Int ): Int =
    if(segment == -1){
      loggerRequire( segmentCount == 1, "You must always specify a segment when reading from data with multiple segments!")
      0
    } else {
      loggerRequire( segment < segmentCount, s"Segment specified ${segment} does not exist in data object!")
      segment
    }

  /**Total length in frames of data. Use
    * [[NNDataTiming.segmentLength(seg* segmentLength(Int)]] to
    * get the length of specific segments, for data which have more than one segment.
    */
  lazy val totalLength: Int = segmentLengths.foldLeft(0)( _ + _ )

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="segment timestamp buffered arrays: segmentStartTss/startTs/segmentEndTss/lastTs ">

  /** Timestamp at which the recording starts. Only valid for data with one segment!
    */
  lazy val startTimestamp: BigInt = {
    errorIfMultipleSegments("startTs", "segmentStartTS(segment: Int)")
    _segmentStartTimestamps(0)
  }

  /** End timestamp for each segment.
    */
  final lazy val segmentEndTimestamps: Array[BigInt] = {
    ( for(seg <- 0 until segmentCount) yield
      _segmentStartTimestamps(seg) + ((segmentLength(seg)-1)*factorTsPerFr).toLong ).toArray
  }

  /** Timestamp at which the recording ends. Only valid for data with one segment!
    */
  lazy val endTimestamp: BigInt = {
    errorIfMultipleSegments("lastTs", "segmentEndTS(segment: Int)")
    segmentEndTimestamps(0)
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="isValidFrsg/isRealisticFrsg">

  /** Is this frame valid within a segment?
    */
  final def isValidFrsg(frame: Int, segment: Int): Boolean =
    (0 <= frame && frame < segmentLength(segment))

  /** Is this frame realistic within a segment (not more than 100000 away from the beginning or end)?
    * This is useful when reading data outside of the specified range, for example, for FIR filters
    * with overhang.
    */
  final def isRealisticFrsg(frame: Int, segment: Int): Boolean =
    (-100000 <= frame && frame < segmentLength(segment) + 100000)

  /** Is this range realistic within a segment?
    * @see isRealisticFrsg
    */
  final def isRealisticRange(range: SampleRangeSpecifier): Boolean = {
    val seg = range.getRealSegment(this)
    val ran = range.getSampleRangeReal(this)
    isRealisticFrsg(ran.start, seg) && isRealisticFrsg(ran.last, seg)
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc="Time specification: conversion between frame/segment and Ts">

  private final lazy val factorTsPerFr = sampleInterval * 1000000D
  private final lazy val factorFrPerTs = 1D/factorTsPerFr

  final def convertFrToTs(frame:Int): BigInt = {
    errorIfMultipleSegments("convertFrToTs(frame: Int)", "convertFrsgToTs(frame: Int, segment: Int)")
    convertFrsgToTs(frame, 0)
  }

  /** Absolute timestamp of the given data frame index (in microseconds).
    */
  final def convertFrsgToTs(frame:Int, segment: Int): BigInt = {
    loggerRequire( isValidFrsg(frame, segment), "Not valid frame/segment specification!" )
    _segmentStartTimestamps(segment) + ((frame/*-1*/).toDouble * factorTsPerFr).round
  }

  /** Closest frame/segment index to the given absolute timestamp. Will give frames which are out of range (i.e. negative, etc)
    * if necessary.
    *
    * @param timestamp in Long
    * @return
    */
  final def convertTsToFrsg(timestamp: BigInt): (Int, Int) = {

    var tempret: (Int, Int) = (0 , 0)
    var changed = false
    def convertImpl(startTs: BigInt) = ((timestamp-startTs).toDouble * factorFrPerTs- 0.00001).round.toInt

    //timestamp is before the start of the first segment
    if( timestamp <= segmentStartTimestamps(0) ){
      tempret = ( convertImpl(segmentStartTimestamps(0)), 0)
    } else {
      //loop through segments to find appropriate segment which (contains) given timestamp
      var seg = 0
      while(seg < segmentCount - 1 && !changed ){
        if( timestamp <= segmentEndTimestamps(seg) ){
          // if the timestamp is smaller than the end of the current segment, it fits in the current segment
          tempret = ( convertImpl(segmentStartTimestamps(seg)), seg)
          changed = true
        } else if( timestamp < segmentStartTimestamps(seg+1) ) {
          //The timestamp is between the end of the current segment and the beginning of the next segment...
          if( timestamp - segmentEndTimestamps(seg) < segmentStartTimestamps(seg+1) - timestamp){
            //  ...timestamp is closer to end of current segment than beginning of next segment
            tempret = ( convertImpl(segmentEndTimestamps(seg)), seg)
            changed = true
          } else {
            //  ...timestamp is closer to beginning of next segment than end of current segment
            tempret = ( convertImpl(segmentStartTimestamps(seg + 1)), seg + 1)
            changed = true
          }
        } else {
          //go on to next segment
          seg += 1
        }
      }

      //deal with the lastValid segment separately
      if( !changed ){
        if(timestamp <= segmentEndTimestamps(segmentCount -1)){
          // if the timestamp is smaller than the end of the current segment, it fits in the current segment
          tempret = ( convertImpl(_segmentStartTimestamps(segmentCount-1)), segmentCount - 1 )
        } else {
          // if the timestamp is larger than the end of the lastValid segment
          tempret = ( convertImpl(segmentEndTimestamps(segmentCount-1)), segmentCount - 1 )
        }
      }

    }

    tempret
  }
  final def convertTsToFrsgArray(timestamp: BigInt): Array[Int] = {
    val tempret = convertTsToFrsg(timestamp)//, false)
    Array[Int]( tempret._1, tempret._2 )
  }
  final def convertTsToFr(timestamp: BigInt): Int = {
    errorIfMultipleSegments("convertTsToFr", "convertTsToFrsg")
    convertTsToFrsg(timestamp)._1
  }

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc="Time specification: conversion between frame/segment and ms">

  /** Time of the given data frame and segment (in milliseconds, with t=0 being the time for frame 0 within the segment).
    */
  final def convertFrToMs(frame: Int): Double = {
    frame.toDouble * sampleInterval * 1000d
    //(frameSegmentToTS(frame, segment)-frameSegmentToTS(0, segment)).toDouble / 1000d
  }
  final def convertFrToMs(frame: Double): Double = convertFrToMs(round(frame).toInt)

  /** Closest frame/segment index to the given timestamp in ms (frame 0 within segment being time 0). Will give beginning or lastValid frames, if timestamp is
    * out of range.
    */
  final def convertMsToFr(ms: Double): Int = (ms*sampleRate*0.001).toInt

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc="Time specification: conversion between ts and ms">

  final def convertTsToMs(timestamp: Long): Double = convertFrToMs( convertTsToFr(timestamp) )
  final def convertMsToTs(ms: Double): BigInt = convertFrToTs( convertMsToFr(ms) )

  // </editor-fold>
  // <editor-fold defaultstate="collapsed" desc="Time specification: convertTsToClosestSegment">

  /** Closest segment index to the given timestamp.
    */
  final def convertTsToClosestSegment(timestamp: BigInt): Int = {
    if(timestamp <= _segmentStartTimestamps(0) ){
      0
    } else {
      var tempret = -1
      var seg = 0
      while(seg < segmentCount - 1 && tempret == -1){
        if( timestamp < segmentEndTimestamps(seg) ){
          tempret = seg
        } else if(timestamp < _segmentStartTimestamps(seg+1)) {
          tempret = if(timestamp - segmentEndTimestamps(seg) < _segmentStartTimestamps(seg+1) - timestamp) seg else seg + 1
        } else {
          seg += 1
        }
      }
      if(tempret == -1){
        tempret = segmentCount - 1
      }
      tempret
    }
  }

  // </editor-fold>

  override def isCompatible(that: NNElement): Boolean = {
    that match {
      case x: NNDataTiming => {
        (this.segmentCount == x.segmentCount) &&
          //ToDo 2: removed for corrupt page drop at end, like E04LC. Add better error code and tests
          //(this.segmentLength.corresponds(x.segmentLength)(_ == _ )) &&
          (this._segmentStartTimestamps.corresponds(x._segmentStartTimestamps)(_ == _ )) &&
          (this.sampleRate == x.sampleRate)
      }
      case _ => false
    }
  }

  override def toString(): String =
    this.getClass.getName + s"(fs=$sampleRate, segmentCount=$segmentCount, $gitHeadShort)"

  override def toStringFull(): String = {
    var tempout = toString()+ "\n"    +
    "   seg#\t\tsegLen\t\tsegStartTs\n"

    for( seg <- 0 until segmentCount) {
      tempout = tempout + s"   $seg\t\t	${segmentLength(seg)}\t" +
        s"${segmentStartTimestamps(seg)}\n"
    }
    tempout.dropRight(1)
  }


}