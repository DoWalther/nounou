package nounou.elements.data.filters

import breeze.linalg.{DenseVector => DV}
import breeze.stats.mean
import nounou.elements.data.{NNData, NNDataNeighbor}
import nounou.ranges.{NNRangeInstantiated, NNRangeValid}

import scala.collection.mutable

/**
 * @author ktakagaki
 * //@date 2/1314.
 */
class NNFilterFudgeChannels(parentVal: NNDataNeighbor, val fudgeDetectors: mutable.HashSet[Int] )
    extends NNFilter( parentVal ) {

  def this(parentVal: NNDataNeighbor) = this(parentVal, new mutable.HashSet[Int]())

  // <editor-fold defaultstate="collapsed" desc=" accessors ">

  def setFudge( channel: Int ): Unit = setFudge(channel, true)
  def setFudge( channel: Int, set: Boolean ): Unit = {
    loggerRequire( 0 <= channel && channel < getChannelCount, s"channel $channel is outside range [0, $getChannelCount)!" )
    if( parentVal.getLayout().isEdge(channel, 1)){
      throw loggerError("Attempting to fudge a detector which is edge even with ring 1!")
    }else {
      if (set) fudgeDetectors.+=(channel)
      else fudgeDetectors.-=(channel)
    }
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" readXXX ">

  override def readPointImpl(channel: Int, frame: Int, segment: Int): Double =
    if(fudgeDetectors.contains(channel)){
      if( parentVal.getLayout().isEdge(channel, 1) ){
        throw loggerError(s"Trying to read fudged data from an edge channel $channel!")
      }else{
        mean(
          DV(parentVal.getLayout.getNeighbors(channel, 1).map( parentVal.readPointImpl(_, frame, segment) ).toArray)
        )
      }
    } else {
      parentVal.readPointImpl(channel, frame, segment)
    }

  override def readTraceDVImpl(channel: Int, range: NNRangeValid): DV[Double] =
    if(fudgeDetectors.contains(channel)){
      if( parentVal.getLayout().isEdge(channel, 1) ){
        throw loggerError(s"Trying to read fudged data from an edge channel $channel!")
      }else{
        val tempData: List[DV[Double]] =  parentVal.getLayout.getNeighbors(channel, 1).map( parentVal.readTraceDVImpl(_, range) )
        DV( (for( p <- 0 until tempData(0).length ) yield mean( tempData.map( _.apply(p) ) )).toArray )
      }
    } else {
      parentVal.readTraceDVImpl(channel, range)
    }

  // </editor-fold>

  override def toStringImpl(): String = s"${fudgeDetectors.size} channels fudged"
  override def toStringFullImpl(): String = fudgeDetectors.toString()

}