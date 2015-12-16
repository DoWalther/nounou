package nounou.io.neuralynx

import nounou.elements.events.NNEvent
import nounou.util.LoggingExt

object NNEventNEV extends LoggingExt {
  implicit object NNEventNEVOrdering extends Ordering[NNEventNEV] {
    override def compare(a: NNEventNEV, b: NNEventNEV) = a.timestamp compare b.timestamp
  }
  implicit def nnEventToNNEventNEV( ev: NNEvent ): NNEventNEV =
    new NNEventNEV(ev.timestamp, ev.duration, ev.code,
      "<from NNEvent: TTL Input on AcqSystem99_99 board 99 port 99 value (" + Integer.toHexString(ev.code) + ")> " + ev.comment,
      Short.MaxValue)

//  def overrideDuration(xEvent: NNEventNEV, duration: Long) =
//    new NNEventNEV(xEvent.timestamp, duration, xEvent.code, xEvent.comment)
//  def overrideCode(xEvent: NNEventNEV, code: Int) =
//    new NNEventNEV(xEvent.timestamp, xEvent.duration, code, xEvent.comment)

  // <editor-fold defaultstate="collapsed" desc=" code for dealing with port/board information ">

  def commentInterpreter(comment: String): (Int, Int, Int, Int) = {
    val pattern = """*TTL Input on AcqSystem(\d+)_(\d+) board (\d+) port (\d+) value*""".r
    //val pattern(as1, as2, b, p) = comment //"TTL Input on AcqSystem1_2 board 200 port 4 value"
    comment match {
      case pattern(as1, as2, b, p) => (as1.toInt, as2.toInt, b.toInt, p.toInt)
      case _ => (0, 0, 0, 0)
    }

  }

  def toNEVPortValue(comment: String): Int = {
    val pattern = commentInterpreter(comment)
    toNEVPortValue(pattern._1, pattern._2, pattern._3, pattern._4)
  }

  def toNEVPortValue(as1: Int, as2: Int, b: Int, p: Int): Int = {
    loggerRequire(as1>=0 && as1<=99, "as1 is out of range: {}", as1.toString)
    loggerRequire(as2>=0 && as2<=99, "as2 is out of range: {}", as2.toString)
    loggerRequire(b  >=0 && b  <=99, "b is out of range: {}", b.toString)
    loggerRequire(p  >=0 && p  <=99, "p is out of range: {}", p.toString)
    ((as1*100 + as2)*100 + b)*100 + p
  }

  def fromNEVPortValue(portValue: Int): (Int, Int, Int, Int) = {
    loggerRequire(portValue>=0 && portValue<100000000, "portValue is out of range: {}", portValue.toString)
    var temp = portValue
    val p = temp % 100
    temp /= 100
    val b = temp % 100
    temp /= 100
    val as2 = temp % 100
    temp /= 100
    val as1 = temp % 100
    temp /= 100
    (as1, as2, b, p)
  }

  // </editor-fold>

}


/**
  * Created by ktakagaki on 15/11/26.
  */
class NNEventNEV(timestamp: BigInt, duration: BigInt, code: Int, comment: String, id: Short)
  extends NNEvent(timestamp, duration, code, comment) {

  private lazy val nevCommentTuple = NNEventNEV.commentInterpreter( comment )
  lazy val commentAcqSystem1 = nevCommentTuple._1
  lazy val commentAcqSystem2 = nevCommentTuple._2
  lazy val commentBoard = nevCommentTuple._3
  lazy val commentPort = nevCommentTuple._4

}
