package nounou.io.neuralynx

import nounou.NN
import nounou.elements.data.{NNData, NNDataChannelArray}
import nounou.elements.spikes.NNSpikes
import org.scalatest.FunSuite

/**
  * Trait to import test file "/nounou/Neuralynx/t130911/STet4a.nse" as NNDataChannel
  */
trait NNTestLoaderNSE_STet4a extends FunSuite {

  //ToDo ?: make test data load from online?
  protected[nounou] val testFileSTet4a = getClass.getResource("/nounou/Neuralynx/t130911/STet4a.nse").getPath()

  val data = NN.load( testFileSTet4a ).apply(0)
  assert(data.isInstanceOf[NNSpikes/*Neuralynx*/])
  private val _dataObj = data.asInstanceOf[NNSpikes/*Neuralynx*/]
  def dataObj: NNSpikes/*Neuralynx*/ = _dataObj


}
