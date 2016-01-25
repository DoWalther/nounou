package nounou.elements.events

import java.math.BigInteger

import nounou.elements.NNElement

object NNEvent {
//  implicit object NNEventOrdering extends Ordering[NNEvent] {
//    override def compare(a: NNEvent, b: NNEvent) = a.timestamp compare b.timestamp
//  }
  implicit val nnEventOrdering: Ordering[NNEvent] = Ordering.by[NNEvent, BigInt]( _.timestamp )

  def overrideDuration(xEvent: NNEvent, duration: Long) = new NNEvent(xEvent.timestamp, duration, xEvent.code, xEvent.comment)
  def overrideCode(xEvent: NNEvent, code: Int) = new NNEvent(xEvent.timestamp, xEvent.duration, code, xEvent.comment)
}

/**An immutable class to encapsulate a single event in a neurophysiological recording.
 */
case class NNEvent(val timestamp: BigInt, val duration: BigInt, val code: Int, val comment: String) extends NNElement {

  def this(timestamp: BigInt, duration: BigInt, code: Int) = this(timestamp, duration, code, "")

  private val zero = BigInt( BigInteger.ZERO )
  def expandDuration(): Array[NNEvent] = {
    if( duration == zero ) Array(this)
    else Array( new NNEvent(timestamp, zero, code, comment), new NNEvent(timestamp + duration, zero, 0, "END" + comment) )
  }

  override def toStringImpl() = s"ts=$timestamp, dur=$duration, code=$code, $comment"
  override def toStringFullImpl(): String = ""

//  override def isCompatible(that: _root_.nounou.elements.NNElement): Boolean = false

}
