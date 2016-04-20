package nounou.elements.data.filters

import nounou._
import nounou.elements.data.{NNDataChannel, NNDataChannelArray}
import nounou.io.neuralynx.NNChannelFileReadNCS
import org.scalatest.FunSuite

/**
* @author ktakagaki
* //@date 1/30/14.
*/
class NNFilterMedianSubtractTest extends FunSuite {

  val testFileE04LC_CSC1 = getClass.getResource("/nounou/Neuralynx/E04LC/CSC1.ncs").getPath()
  val data = NN.load(testFileE04LC_CSC1).apply(0)
  assert( data.isInstanceOf[NNDataChannel] )
  assert( data.isInstanceOf[NNChannelFileReadNCS] )
  val dataObj = data.asInstanceOf[NNChannelFileReadNCS]

  test("read from NNFilterMedianSubtract"){
    val dataObjArray = new NNDataChannelArray(Array(dataObj, dataObj, dataObj))
    val dataMS = new NNFilterMedianSubtract(dataObjArray)
    dataMS.setWindowLength(81)

    println( dataObjArray.readPoint(0,0,0))
    println( dataMS.readPoint(0,0,0) )

  }

}
