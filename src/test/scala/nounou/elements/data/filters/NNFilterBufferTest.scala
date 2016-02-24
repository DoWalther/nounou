package nounou.elements.data.filters

import nounou.io.neuralynx.NNDataTestNCSTet4

/**
* @author ktakagaki
*/
class NNFilterBufferTest extends NNDataTestNCSTet4 {

  override val dataObjTet4 = new NNFilterBuffer( super.dataObjTet4 )

  //Tests are exactly the same as in NNDataTest, only buffered

  println( dataObjTet4.bufferPageLength )

}
