package nounou.elements.data.filters

import nounou.io.neuralynx.NNDataTest

/**
* @author ktakagaki
*/
class NNFilterBufferTest extends NNDataTest {

  override val dataObj = new NNFilterBuffer( super.dataObj )

  //Tests are exactly the same as in NNDataTest, only buffered

  println( dataObj.bufferPageLength )

}
