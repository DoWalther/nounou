package nounou.io.neuralynx

import nounou.NN
import nounou.elements.data.NNDataChannel
import org.scalatest.FunSuite

/**
  * Trait to import test file "/nounou/Neuralynx/E04LC/CSC1.ncs" as NNDataChannel
  */
trait NNTestLoaderNCS_E04LC_NNDataChannel extends FunSuite {

  //ToDo ?: make test data load from online?
  private val testFileE04LC_CSC1 = getClass.getResource("/nounou/Neuralynx/E04LC/CSC1.ncs").getPath()
  private val data = NN.load(testFileE04LC_CSC1).apply(0)
  assert(data.isInstanceOf[NNDataChannel])
  assert(data.isInstanceOf[NNChannelFileReadNCS])
  val dataChannelNCSObj = data.asInstanceOf[NNChannelFileReadNCS]
  val dataChannelObj: NNDataChannel = dataChannelNCSObj

  assert(dataChannelNCSObj.header.getHeaderDspFilterDelay == 484)
  val filterDel = dataChannelNCSObj.header.getHeaderDspFilterDelay

}
