package nounou.io.neuralynx

import nounou._
import nounou.elements.events.NNEvents
import org.scalatest.FunSuite

/**
* @author ktakagaki
*/
class FileAdapterNEVTest extends FunSuite {

  val testFilet130911_Events = getClass.getResource("/nounou/Neuralynx/t130911/Events.nev").getPath()
  val data = NN.load(testFilet130911_Events).apply(0)
  assert( data.isInstanceOf[NNEvents] )
  val dataObj = data.asInstanceOf[NNEvents]

  test("readInfo"){

    assert( dataObj.getPorts.toList == List(0, 1000001) )

  }

}
