package nounou.analysis

import nounou._
import nounou.elements.data.filters.NNFilterBuffer
import nounou.elements.data.{NNDataChannel, NNDataChannelArray}
import nounou.io.neuralynx.NNDataChannelFileReadNCS
import org.scalatest.FunSuite

/**
* @author ktakagaki
* //@date 1/30/14.
*/
class spikeDetectTest extends FunSuite {

  val testFileE04LC_CSC1 = getClass.getResource("/nounou/Neuralynx/E04LC/CSC1.ncs").getPath()
  val data = NN.load(testFileE04LC_CSC1).apply(0)
  assert( data.isInstanceOf[NNDataChannel] )
  assert( data.isInstanceOf[NNDataChannelFileReadNCS] )
  val dataObj = data.asInstanceOf[NNDataChannelFileReadNCS]

  test("xxx"){


  }

}
