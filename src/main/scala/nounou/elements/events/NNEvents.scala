package nounou.elements.events

import java.math.BigInteger
import nounou.elements.NNElement
import nounou.elements.data.traits.NNElementCompatibilityCheck
import nounou.elements.headers.NNHeader
import nounou.util.{leftPadSpace, leftPadZero}
import scala.collection.immutable.TreeMap
import scala.collection.mutable

/**
  * Mutable database object to encapsulate marked events in data recordings.
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
final class NNEvents(private var _database: TreeMap[Int, mutable.TreeSet[NNEvent]])
  extends NNElement with NNElementCompatibilityCheck {

  // <editor-fold defaultstate="collapsed" desc=" Default constructor ">

  def this() = this(new TreeMap[Int, mutable.TreeSet[NNEvent]]())

  var header: NNHeader = null
//  private var _header: NNHeader = null
//  override def header(): NNHeader = _header
//  def setHeader(header: NNHeader): Unit = {_header = header}

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" accessors ">

  /**
    * Gets the underlying TreeSet representing the list of events for a given port
    */
  def getPort(port: Int): mutable.TreeSet[NNEvent] = {
    if( _database.contains(port) ) _database(port)
    else new mutable.TreeSet[NNEvent]()
  }

  /**
    * Gets a TreeSet subset representing the list of events for a given port
    */
  def getPortFilteredByCode(port: Int, code: Int): mutable.TreeSet[NNEvent] = {
    getPort(port).filter(p => p.code == code )
  }

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

  // </editor-fold>

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
      _database = _database.+(port -> new mutable.TreeSet[NNEvent]())
    }
  }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" readPortEventArrayXXX ">

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
  def readPortEventArrayCodes(port: Int ): Array[Int] = getPort(port).toArray.map( _.code )

  def readPortEventArrayTimestamp(port: Int ): Array[BigInteger] =
    getPort(port).toArray.map((e: NNEvent) => e.timestamp.bigInteger )

  def readPortEventArrayDuration(port: Int ): Array[BigInteger] =
    getPort(port).toArray.map((e: NNEvent) => e.duration.bigInteger )

  def readPortEventArrayComments(port: Int): Array[String] = getPort(port).toArray.map( _.comment )

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" readPortCodeEventArray ">

  def readPortCodeEventArray( port: Int, code: Int): Array[Array[BigInteger]] = {
    getPortFilteredByCode(port, code).toArray.map( (e: NNEvent) => Array( e.timestamp.bigInteger, e.duration.bigInteger ) )
  }

  // </editor-fold>


  // <editor-fold defaultstate="collapsed" desc=" expandZeroEvents ">

  /** Takes each event with !0 duration and expands them to two 0 duration events,
   * one with the port code for the beginning, and one resetting it to port code=0.
   */
  def expandDurationEventsToStartAndReset(): Unit = {
    _database = _database.map( ( ev:(Int, mutable.TreeSet[NNEvent]) ) => ( ev._1, {
      var tempTreeSet = mutable.TreeSet[NNEvent]()
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

  //ToDo: expand Events... is this object immutable or not???
  override def clone(): NNEvents = new NNEvents(_database)

  // <editor-fold desc="NNElementCompatibilityCheck">

  override def isCompatible(that: NNElement): Boolean =
    that match {
      case x: NNEvents => true
      case _ => false
    }

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" toString related ">

  override def toStringImpl() = s"ports=${getPortCount}, events=${_database.map( _._2.size ).toList}"

  override def toStringFullImpl() = {
    var output = ""
    for( p <- getPorts ){
      output = output + s"\nPort $p\n"
      for( c <- getPortCodes(p) ){
        output = output + "    Code " + f"${c}%5d" + " ("+ {
          val binstr = c.toBinaryString
          if (binstr.length <= 8) leftPadZero(c.toBinaryString, 8)
          else if (binstr.length <= 16) leftPadZero(c.toBinaryString, 16)
          else c.toBinaryString
        } + s"): " +
          leftPadSpace(getPortFilteredByCode(p, c).size.toString, 8) +" events"
        output = output + "\n"
      }
      output = output.dropRight(1)
    }
    if(output.length >= 1) output.drop(1) else output
  }

  // </editor-fold>

}