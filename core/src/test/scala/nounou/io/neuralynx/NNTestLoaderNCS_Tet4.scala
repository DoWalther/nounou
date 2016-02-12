package nounou.io.neuralynx

import nounou.NN
import nounou.elements.data.{NNData, NNDataChannelArray}
import org.scalatest.FunSuite

/**
  * Trait to import test file "/nounou/Neuralynx/E04LC/CSC1.ncs" as NNDataChannel
  */
trait NNTestLoaderNCS_Tet4 extends FunSuite {

  //ToDo ?: make test data load from online?
  private val testFileTet4a = getClass.getResource("/nounou/Neuralynx/t130911/Tet4a.ncs").getPath()
  private val testFileTet4b = getClass.getResource("/nounou/Neuralynx/t130911/Tet4b.ncs").getPath()
  private val testFileTet4c = getClass.getResource("/nounou/Neuralynx/t130911/Tet4c.ncs").getPath()
  private val testFileTet4d = getClass.getResource("/nounou/Neuralynx/t130911/Tet4d.ncs").getPath()

  val data = NN.load( Array(testFileTet4a, testFileTet4b, testFileTet4c, testFileTet4d) ).apply(0)
  assert(data.isInstanceOf[NNDataChannelArray])
  private val _dataObj = data.asInstanceOf[NNDataChannelArray]
  def dataObj: NNData = _dataObj


}
