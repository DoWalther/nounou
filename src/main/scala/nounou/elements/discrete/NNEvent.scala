package nounou.elements

object NNEvent {
  implicit object NNEventOrdering extends Ordering[NNEvent] {
    override def compare(a: NNEvent, b: NNEvent) = a.timestamp compare b.timestamp
  }
  def overrideDuration(xEvent: NNEvent, duration: Long) = new NNEvent(xEvent.timestamp, duration, xEvent.code, xEvent.comment)
  def overrideCode(xEvent: NNEvent, code: Int) = new NNEvent(xEvent.timestamp, xEvent.duration, code, xEvent.comment)
}

/**An immutable class to encapsulate a single event in a neurophysiological recording.
 */
class NNEvent(val timestamp: BigInt, val duration: Long, val code: Int, val comment: String) {

  def this(timestamp: BigInt, duration: Long, code: Int) = this(timestamp, duration, code, "")

  override def toString = s"NNEvent(ts=$timestamp, dur=$duration, code=$code, $comment)"

}
