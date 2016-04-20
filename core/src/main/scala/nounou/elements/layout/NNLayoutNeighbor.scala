package nounou.elements.layout

import scala.collection.mutable

/**
 */
trait NNLayoutNeighbor extends NNLayout {

  /**Number of directions (square layout 4, hexagonal layout 6). */
  def directionCount(ring: Int): Int

  final def getNeighbor(channel: Int, direction: Int): Int = getNeighbor(channel, direction, 1)
  def getNeighbor(channel: Int, direction: Int, ring: Int): Int

  final def getNeighbors(channel: Int): List[Int] = getNeighbors(channel: Int, 1)
  def getNeighbors(channel: Int, ring: Int): List[Int] =
    (for(dir <- 0 until directionCount(ring)) yield getNeighbor(channel, dir, ring)).toList

  final def getNeighborVector(channel: Int, direction: Int): (Double, Double) = getNeighborVector(channel: Int, direction: Int, 1)
  def getNeighborVector(channel: Int, direction: Int, ring: Int): (Double, Double)

  final def getNeighborVectors(channel: Int): List[(Double, Double)] = getNeighborVectors(channel: Int, 1)
  def getNeighborVectors(channel: Int, ring: Int): List[(Double, Double)] =
    (for(dir <- 0 until directionCount(ring)) yield getNeighborVector(channel, dir, ring)).toList

  final def isEdge(channel: Int): Boolean = isEdge(channel: Int, 1)
  private val bufferIsEdge = new HashMapBufferIsEdge()
  def isEdge(channel: Int, ring: Int): Boolean = bufferIsEdge( chRingHashKey(channel, ring) )


  /*Generate hash keys*/
  private def chDirHashKey(channel: Int, direction: Int): Int = channel*256 + direction
  private def chDirHashKeyInverse(key: Int): (Int, Int) = (key/256, key % 256)
  private def chRingHashKey(channel: Int, ring: Int): Int = channel*256 + ring
  private def chRingHashKeyInverse(key: Int): (Int, Int) = (key/256, key % 256)


  // <editor-fold defaultstate="collapsed" desc=" HashMapBufferIsEdge ">

  /**
    * This class implements a WeakHashMap that allows loading if the relevant key is not present
    */
  private class HashMapBufferIsEdge extends mutable.HashMap[Int, Boolean] {

    //do not use applyOrElse!
    override def apply( key: Int ): Boolean = {

//      val index = garbageQue.indexOf( key )
//      if( index == -1 ){
//        if(garbageQue.size >= garbageQueBound ){
//          this.remove( garbageQue(1) )
//          garbageQue.drop(1)
//        }
//        garbageQue.append( key )
//        default( key )
//      }else{
//        garbageQue.remove( index )
//        garbageQue.append( key )
//        super.apply(key)
//      }
    ???
    }

    override def default( key: Int ): Boolean = {
      val (tempChannel, tempRing) = chRingHashKeyInverse(key)
      !( getNeighbors(tempChannel, tempRing).forall( (ch: Int) => (0 <= ch && ch < getChannelCount) ) )
    }
  }

  // </editor-fold>

}
