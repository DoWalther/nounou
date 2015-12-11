package nounou.elements.events

import java.math.BigInteger

import nounou.elements.NNElement
import nounou.elements.headers.NNHeader

import scala.collection.immutable.TreeMap
import scala.collection.mutable
import scala.collection.mutable.TreeSet

/** Mutable database object to encapsulate marked events in data recordings.
  *
  * Events are stored as [[NNEvent]] objects, which are immutable objects encapsulating
  * timestamp, duration, code, and comment string.
  * An [[NNEvents]] database consists of TreeSet(s) of [[NNEvent]]s.
  *
  * Each event "port" has its own TreeSet---this allows different ports to have events with exactly the same timing.
  * (TreeSet's with black-red trees cannot support keys with equivalent sorting values)
  *
  * @author ktakagaki
 */
class NNEvents extends NNElement {

  private var _database: TreeMap[Int, TreeSet[NNEvent]] = new TreeMap[Int, TreeSet[NNEvent]]()

  var header: NNHeader = null
//  private var _header: NNHeader = null
//  override def header(): NNHeader = _header
//  def setHeader(header: NNHeader): Unit = {_header = header}

  def lengths: Array[Int] = _database.values.map( _.size ).toArray

  /** Returns a list of the ports that are registered in this database.
    * Ports with no events can be registered as well.
    */
  def ports: Array[Int] = _database.keys.toArray

  /** Returns the number of ports that have events registered in this database
    * Ports with no events can be registered as well.
    */
  def portCount: Int = _database.size

  /** Alias for [[addEvent(port:Int* addEvent(Int,NNEvent)]]
    */
  def addEvent( portEvent: (Int, NNEvent) ): Unit = addEvent(portEvent._1, portEvent._2)
  def addEvent( port: Int, event: NNEvent ): Unit = {
    addPort(port)
    _database(port).+=(event)
  }

  /** Adds a port and initializes the TreeSet[NNEvent] data structure for it.
    */
  def addPort( port: Int ): Unit = {
    if( !_database.contains(port) ){
      loggerRequire(port >= 0, "port specification {} must be >= zero!", port.toString)
      _database = _database.+(port -> new TreeSet[NNEvent]())
    }
  }

  // <editor-fold defaultstate="collapsed" desc=" filterByPort/filterByPortCode ">

  def filterByPort(port: Int): TreeSet[NNEvent] = {
    if( _database.contains(port) ) _database(port)
    else new TreeSet[NNEvent]()
  }

  def filterByPortCode(port: Int, code: Int): TreeSet[NNEvent] = {
    filterByPort(port).filter( p => p.code == code )
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" expandZeroEvents ">

  /** Takes each event with !0 duration and expands them to two 0 duration events,
   * one with the port code for the beginning, and one resetting it to port code=0.
   */
  def expandDurationEventsToStartAndReset(): Unit = {
    _database = _database.map( ( ev:(Int, TreeSet[NNEvent]) ) => ( ev._1, {
      var tempTreeSet = TreeSet[NNEvent]()
      ev._2.foreach(
        (x: NNEvent) => {
          if (x.duration != 0) {
            tempTreeSet = tempTreeSet + new NNEvent(x.timestamp, 0L, x.code, x.comment)
            tempTreeSet = tempTreeSet + new NNEvent(x.timestamp + x.duration, 0L, 0, "nounou: expanded reset")
          }
          else tempTreeSet = tempTreeSet + x
        }
      )
      tempTreeSet}   )
    )
  }

  // </editor-fold>


  def getCodes(  port: Int  ): Array[Int] = filterByPort(port).toArray.map( _.code )
  def getTimestamps(  port: Int  ): Array[Array[BigInt]] =
    filterByPort(port).toArray.map( (e: NNEvent) => Array(e.timestamp, e.duration) )
  def getComments(  port: Int  ): Array[String] = filterByPort(port).toArray.map( _.comment )
  def getPortEventList(): Array[(Int, NNEvent)] = getPortEventList( true )
  def getPortEventList( expandDuration: Boolean ): Array[(Int, NNEvent)] = {
    val temp = _database.toArray.flatMap((pEventSet: (Int, mutable.TreeSet[NNEvent])) => pEventSet._2.map( (pEventSet._1, _) ) )
    (if(expandDuration){
        temp.flatMap((pEvent: (Int, NNEvent)) => {
          pEvent._2.expandDuration().map( (pEvent._1, _) )
        })
    }else{
        temp
    }).sortBy( _._2.timestamp )
  }

//  lazy val maxDuration: Long = events.map( _._2.duration).max
//  lazy val uniqueEventCodes = events.map(_._2.code).toList.distinct.toVector
//  lazy val sortedEvents = new Array[TreeMap[Long,XEvent]]( uniqueEventCodes.length )

  //def nextEvent(timeStamp: Long): XEvent
  //def nextEvent(timeStamp: Long, eventCode: Int): XEvent
  //def previousEvent(timeStamp: Long): XEvent
  //def getEvents(timeStamp0: Long, timeStamp1: Long): Vector[XEvent]
  //def getEvents(timeStamp: Long): Vector[XEvent]
  //def getEventList: Vector[XEvent]
  //def containsEvent(timeStamp1: Long, timeStamp2: Long): Boolean

  // <editor-fold desc="XConcatenatable">

  //override def :::(that: NNElement): NNEvents = ???
//    that match {
//      case x: XEvents => {
//        new XEvents( this._database ++ x._database)
//      }
//      case _ => {
//        require(false, "cannot concatenate different types!")
//        this
//      }
//    }
//  }

  override def isCompatible(that: NNElement): Boolean =
    that match {
      case x: NNEvents => true
//      {
//        (super[XDiscrete].isCompatible(x) && super[XDiscrete].isCompatible(x))
//      }
      case _ => false
    }

  // </editor-fold>

  override def toStringImpl() = s"no.=${_database.size}, "
  override def toStringFullImpl() = ""

}