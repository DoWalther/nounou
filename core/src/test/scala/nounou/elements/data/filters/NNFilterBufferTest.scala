package nounou.elements.data.filters

import nounou.io.neuralynx.NNDataTestNCSTet4

/**
* @author ktakagaki
*/
class NNFilterBufferTest extends NNDataTestNCSTet4 {

  override val dataObj = new NNFilterBuffer( super.dataObj )

  //Tests are exactly the same as in NNDataTest, only buffered

  println( dataObj.bufferPageLength )

}
