package nounou.elements.data.traits

/**
  * Created by ktakagaki on 16/01/10.
  */
trait NNDataNode {

  protected val _children = scala.collection.mutable.ArrayBuffer[NNDataNode]()

  // <editor-fold defaultstate="collapsed" desc=" setting, getting and clearing children ">

  /**
    * '''[NNData: data source]''' Adds a new child to this data source, which will be notified for changes.
    * Be sure to clear children if the children are to be garbage collected.
    */
  final def setChild(x: NNDataNode): Unit = _children.+=(x)

  /**
    * '''[NNData: data source]''' Adds new children to this data source, which will be notified for changes.
    * Be sure to clear children if the children are to be garbage collected.
    */
  final def setChildren(xs: TraversableOnce[NNDataNode]): Unit = xs.map( setChild(_) )

  /**
    * '''[NNData: data source]''' Direct children of this data source which should be notified upon changes.
    */
  final def getChildren() = _children

  /**
    * '''[NNData: data source]''' Direct children of this data source which should be notified upon changes.
    */
  final def getChild(indexArray: Array[Int]): NNDataNode = {
    if( indexArray.length == 0 ) this
    else _children( indexArray.head ).getChild( indexArray.tail )
  }

  /**
    * '''[NNData: data source]''' Clear all children of this data source.
    */
  final def clearChildren(): Unit = _children.clear()

  /**
    * '''[NNData: data source]''' Clear specified child of this data source.
    */
  final def clearChild(x: NNDataNode): Unit = _children.-=(x)

  /**
    * '''[NNData: data source]''' Clear specified children of this data source.
    */
  final def clearChildren(xs: TraversableOnce[NNDataNode]): Unit = xs.map( clearChild(_) )

  // </editor-fold>

  // <editor-fold defaultstate="collapsed" desc=" changedXXX() functions ">

  /**
    * Call when all data has changed (use more specific commands such as [[changedData(channel:* changedData(channel)]]
    * to minimize calculations).
    * Command will run changedDataImpl(), and then map to changedData()
    * on all children.
    *
    */
  final def changedData(): Unit = {
    changedDataImpl()
    _children.map(_.changedData())
  }

  /**
    * '''__SHOULD OVERRIDE__''' for buffering functions
    * and display functions which have an internal state to update.
    * Use via [[changedData()]].
    */
  protected def changedDataImpl(): Unit = {}


  /**
    * Call when specific channel of data has changed.
    * Command will run changedDataImpl(Int), and then map to changedData(Int)
    * on all children.
    *
    */
  final def changedData(channel: Int): Unit = {
    changedDataImpl(channel)
    _children.map(_.changedData(channel))
  }

  /**
    * '''__SHOULD OVERRIDE__''' for buffering functions
    * and display functions which have an internal state to update.
    * Use via [[changedData()]].
    */
  protected def changedDataImpl(channel: Int): Unit = {}

  /**
    * Call when specific channels of data have changed.
    * Command will run changedDataImpl(Array[Int]), and then map to changedData(Array[Int])
    * on all children.
    */
  final def changedData(channels: Array[Int]): Unit = {
    changedDataImpl( channels )
    _children.map(_.changedData(channels))
  }

  /**
    * '''__CAN OVERRIDE__''' for buffering functions
    * and display functions which have an internal state to update.
    * Default implementation will map changedDataImpl(Int) over all channels.
    * Use via [[changedData()]].
    */
  protected def changedDataImpl(channels: Array[Int]): Unit = {
    channels.map( changedDataImpl(_) )
  }

  /**
    * Call when timing of data has changed.
    * Command will run changedTiming(), and then map to changedTiming()
    * on all children.
    */
  final def changedTiming(): Unit = {
    changedTimingImpl()
    _children.map(_.changedTiming())
  }

  /**
    * '''__SHOULD OVERRIDE__''' for buffering functions
    * and display functions which have an internal state to update.
    * Use via [[changedTiming()]].
    */
  def changedTimingImpl(): Unit = {}

  /**
    * Call when timing of data has changed.
    * Command will run changedLayout(), and then map to changedLayout()
    * on all children.
    */
  final def changedLayout(): Unit = {
    changedLayoutImpl()
    _children.map(_.changedLayout())
  }

  /**
    * '''__SHOULD OVERRIDE__''' for buffering functions
    * and display functions which have an internal state to update.
    * Use via [[changedLayout()]].
    */
  def changedLayoutImpl(): Unit = {}

  // </editor-fold>

  /** Provides a textual representation of the child hierarchy starting from this data object.
    * If multiple NNDataFilter objects (e.g. an [[nounou.elements.data.filters.NNDataFilterFIR]] object)
    * are chained after this data, this method will show the chained objects and their tree hierarchy.
    * @return
    */
  final def toStringChain(indentLevel: Int): String = {
    var output =
      if(indentLevel == 0) toString()
      else " | " * (indentLevel-1) + "+--" + toString() + "/n"

    for( child <- getChildren ){
      output = output + child.toStringChain(indentLevel + 1)
    }

    if(indentLevel == 0) output.dropRight(1) else output
  }

}
