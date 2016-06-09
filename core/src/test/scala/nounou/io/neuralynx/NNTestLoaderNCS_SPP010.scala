package nounou.io.neuralynx

import nounou.NN
import nounou.elements.data.{NNData, NNDataChannelArray}
import org.scalatest.FunSuite

/**
  * Trait to import test file "/nounou/Neuralynx/E04LC/CSC1.ncs" as NNDataChannel
  */
trait NNTestLoaderNCS_SPP010 extends FunSuite {

  //ToDo ?: make test data load from online?
  private val testFileTet4a = getClass.getResource("/nounou/Neuralynx/SPP010/2013-12-02_17-07-31/Tet4a.ncs").getPath()
  private val testFileTet4b = getClass.getResource("/nounou/Neuralynx/SPP010/2013-12-02_17-07-31/Tet4b.ncs").getPath()
  private val testFileTet4c = getClass.getResource("/nounou/Neuralynx/SPP010/2013-12-02_17-07-31/Tet4c.ncs").getPath()
  private val testFileTet4d = getClass.getResource("/nounou/Neuralynx/SPP010/2013-12-02_17-07-31/Tet4d.ncs").getPath()

  val dataSPP010 = NN.load( Array(testFileTet4a, testFileTet4b, testFileTet4c, testFileTet4d) ).apply(0)
  assert(dataSPP010.isInstanceOf[NNDataChannelArray])
  private val _dataObj = dataSPP010.asInstanceOf[NNDataChannelArray]
  def dataObjSPP010: NNData = _dataObj


}
