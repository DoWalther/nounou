package nounou.analysis

import nounou.options._
import nounou.elements.data.{NNDataChannel, NNDataChannelArray}
import nounou.io.neuralynx.{NNTestLoaderNCS_Tet4, NNChannelFileReadNCS}
import nounou.ranges.NNRange
import org.scalatest.FunSuite

/**
* @author ktakagaki
* //@date 1/30/14.
*/
class spikeDetectTest extends FunSuite with NNTestLoaderNCS_Tet4 {

//  val testFileE04LC_CSC1 = getClass.getResource("/nounou/Neuralynx/E04LC/CSC1.ncs").getPath()
//  val dataTet4 = NN.load(testFileE04LC_CSC1).apply(0)
//  assert( dataTet4.isInstanceOf[NNDataChannel] )
//  assert( dataTet4.isInstanceOf[NNDataChannelFileReadNCS] )
//  val dataObjTet4 = dataTet4.asInstanceOf[NNDataChannelFileReadNCS]

  test("xxx"){

    val spikeTimestamps = nounou.analysis.spikes.SpikeDetect(
      dataObjTet4, new NNRange(0, 3200000, 1, 0), Array(0,1,2,3),
      OptMedianFactorDouble(3d), OptPeakWindowInt(32)
    )
    println(spikeTimestamps.toList)

  }
}
