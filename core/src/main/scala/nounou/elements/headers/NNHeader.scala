package nounou.elements.headers

import nounou.elements.NNElement

/**Parent class to encapsulate header information. Some systems (such as Plexon, TDT) have one file (and hence
  * one header header) for each session/data file.
  * In this case, there will be one header for the single file, covering information for several NNElement objects (NNData, NNEvents, NNSpikes).
  * Other systems (such as Neuralynx) have several files (NCS, NEV, NSE, etc.), for a recording session,
  * and each file will have a header, which is encapsulated by a separate NNHeader object.
  *
 * @author ktakagaki
 */
trait NNHeader extends NNElement {

//  override def toString =
//    this.getClass.getName + s"($gitHeadShort)"
//  override def toStringFull = toString
//
//  override def isCompatible(that: NNElement): Boolean = that match {
//    case x: NNHeader => x.getClass == this.getClass
//    case _ => false
//  }

}
