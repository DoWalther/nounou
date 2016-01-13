package nounou.elements.data.filters

import breeze.linalg.DenseVector
import nounou._
import nounou.elements.data.{NNDataChannelArray, NNDataChannel}
import nounou.io.neuralynx.NNDataChannelFileReadNCS
import org.scalatest.FunSuite

/**
* @author ktakagaki
* //@date 1/30/14.
*/
class NNFilterBufferTest extends FunSuite {

  val testFileE04LC_CSC1 = getClass.getResource("/nounou/Neuralynx/E04LC/CSC1.ncs").getPath()
  val data = NN.load(testFileE04LC_CSC1).apply(0)
  assert( data.isInstanceOf[NNDataChannel] )
  assert( data.isInstanceOf[NNDataChannelFileReadNCS] )
  val dataObj = data.asInstanceOf[NNDataChannelFileReadNCS]

  test("read from NNFilterBuffer"){
    val dataObjArray = new NNDataChannelArray(Array(dataObj, dataObj, dataObj))
    val buffer = new NNFilterBuffer(dataObjArray)

    println( dataObjArray.readPoint(0,0,0))
    println( buffer.readPoint(0,0,0) )

  }

}
