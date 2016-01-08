package nounou.elements.events

import java.math.BigInteger

import nounou.elements.NNElement
import nounou.elements.headers.NNHeader
import nounou.elements.traits.NNConcatenableElement
import nounou.util.{leftPadSpace, leftPadZero}

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
class NNEvents extends NNConcatenableElement {

  private var _database: TreeMap[Int, TreeSet[NNEvent]] = new TreeMap[Int, TreeSet[NNEvent]]()

  var header: NNHeader = null
//  private var _header: NNHeader = null
//  override def header(): NNHeader = _header
//  def setHeader(header: NNHeader): Unit = {_header = header}

  def getPortEventCounts: Array[Int] = _database.values.map( _.size ).toArray

  /** Returns a list of the ports that are registered in this database.
    * Ports with no events can be registered as well.
    */
  def getPorts: Array[Int] = _database.keys.toArray

  /** Returns the number of ports that are registered in this database
    * Ports with no events can be registered as well.
    */
  def getPortCount: Int = _database.size

  def getPortCodes( port: Int ): Array[Int] = getPort(port).map( _.code ).toArray

  // <editor-fold defaultstate="collapsed" desc=" add events ">

  /** Alias for [[addEvent(port:Int* addEvent(Int,NNEvent)]]
    */
  final def addEvent( portEvent: (Int, NNEvent) ): Unit = addEvent(portEvent._1, portEvent._2)

  def addEvent( port: Int, event: NNEvent ): Unit = {
    addPort(port)
    _database(port).+=(event)
  }

  /** Adds a port and initializes the TreeSet[NNEvent] data structure for it.
    */
  private def addPort( port: Int ): Unit = {
    if( !_database.contains(port) ){
      loggerRequire(port >= 0, "port specification {} must be >= zero!", port.toString)
      _database = _database.+(port -> new TreeSet[NNEvent]())
    }
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" readPort/readPortCode ">

  /**
    * Gets the underlying TreeSet representing the list of events for a given port
    */
  def getPort(port: Int): TreeSet[NNEvent] = {
    if( _database.contains(port) ) _database(port)
    else new TreeSet[NNEvent]()
  }

  /**
    * Gets a TreeSet subset representing the list of events for a given port
    */
  def getPortFilteredByCode(port: Int, code: Int): TreeSet[NNEvent] = {
    getPort(port).filter(p => p.code == code )
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" read as array ">

  def readPortEventArray(): Array[(Int, NNEvent)] = readPortEventArray( true )

  def readPortEventArray(expandDuration: Boolean ): Array[(Int, NNEvent)] = {
    val temp = _database.toArray.flatMap((pEventSet: (Int, mutable.TreeSet[NNEvent])) => pEventSet._2.map( (pEventSet._1, _) ) )
    (if(expandDuration){
      temp.flatMap((pEvent: (Int, NNEvent)) => {
        pEvent._2.expandDuration().map( (pEvent._1, _) )
      })
    }else{
      temp
    }).sortBy( _._2.timestamp )
  }

  //ToDo 2: Expand the following for expandDuration?
  def readPortCodeArray( port: Int ): Array[Int] = getPort(port).toArray.map( _.code )

  def readPortTimestampArray( port: Int ): Array[BigInteger] =
    getPort(port).toArray.map((e: NNEvent) => e.timestamp.bigInteger )

  def readPortDurationArray( port: Int ): Array[BigInteger] =
    getPort(port).toArray.map((e: NNEvent) => e.duration.bigInteger )

  def readPortCommentArray(port: Int): Array[String] = getPort(port).toArray.map( _.comment )

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

  override def toStringImpl() = s"ports=${getPortCount}, events=${_database.map( _._2.size ).toList}"

  override def toStringFullImpl() = {
    var output = ""
    for( p <- getPorts ){
      output = output + s"Port $p"
      for( c <- getPortCodes(p) ){
        output = output + "\n    Code " + f"${c}%5d" + " ("+ {
          val binstr = c.toBinaryString
          if (binstr.length <= 8) leftPadZero(c.toBinaryString, 8)
          else if (binstr.length <= 16) leftPadZero(c.toBinaryString, 16)
          else c.toBinaryString
        } + s"): " +
          leftPadSpace(getPortFilteredByCode(p, c).size.toString, 8) +" events"
        output = output + "\n"
      }
      output=output.dropRight(1)
    }
    output
  }

}



//  def readPortTimestamps( port: Int ): Array[Array[BigInt]] =
//    readPort(port).toArray.map((e: NNEvent) => Array(e.timestamp, e.duration) )


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
