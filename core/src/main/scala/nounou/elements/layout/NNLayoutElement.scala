package nounou.elements.layout

import nounou.elements.traits.NNChannelsElement

/**
 * Created by ktakagaki on 15/03/12.
 */
trait NNLayoutElement extends NNChannelsElement {

  protected var _layout: NNLayout = null

  def layout(): NNLayout = getLayout()
  def getLayout(): NNLayout = {
    if( _layout == null ) throw loggerError(
      s"Cannot use layout-related functions in this ${this.getClass.getCanonicalName} because layout is not set! Call setLayout(xxx) first!")
    else _layout
  }
  def setLayout(layout: NNLayout) = {
    loggerRequire( layout.channelCount == this.channelCount(),
      s"Channel count ${layout.channelCount} of new layout does not match channel count ${this.channelCount()} for ${this.getClass.toString}" )
    _layout= layout
  }


}
