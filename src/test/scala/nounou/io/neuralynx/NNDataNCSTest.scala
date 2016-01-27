package nounou.io.neuralynx

import breeze.linalg.DenseVector
import nounou._
import nounou.elements.data.{NNDataChannelArray, NNData, NNDataChannel}
import org.scalatest.FunSuite

/**
  * Trait to import test file "/nounou/Neuralynx/E04LC/CSC1.ncs" as NNDataChannel
  */
trait NNDataNCSTet4 extends FunSuite {

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


class NNDataTest extends FunSuite with NNDataNCSTet4 {

  test("readInfo"){

    //println(dataObj.timing.toStringFull )

    //trait NNTiming
    assert(dataObj.timing.filterDelay == 484)
    assert( dataObj.timing.segmentCount == 3 )
    assert( dataObj.timing.segmentLength(0) == 3339264 )
    assert( dataObj.timing.segmentLength(1) == 123392 )
    assert( dataObj.timing.segmentLength(2) == 6754816 )

    assert( dataObj.timing.segmentStartFrame(1) == 3339264 )

    assert( dataObj.timing.segmentStartTss(0) == 2592255824L )
    assert( dataObj.timing.segmentStartTss(1) == 4596169824L )
    assert( dataObj.timing.segmentStartTss(2) == 4627405824L )

    assert( dataObj.timing.sampleRate == 32000d)
    assert( dataObj.timing.segmentEndTss(0)
      == (dataObj.timing.segmentStartTss(0) + (3339264-1) *1000L /32L ) )
    assert( dataObj.timing.convertTsToFrsg( 2592255824L ) == (0, 0) )

  }

  test("readPoint") {

    assert(dataObj.readPoint(0, 0, segment = 0) == -27.100428000000004)
    assert(dataObj.readPoint(3, 0, segment = 1) == -62.13566600000001)

  }

  test("readTrace") {

//    println(dataObj.readTraceDV( 0, NN.NNRange(0, 50, 5, segment = 0)))
    println(dataObj.readTraceDV( 2, NN.NNRange(55, 70, 5, segment = 2)))

    assert(dataObj.readTraceDV( 0, NN.NNRange(0, 4, 1, segment = 0)) ==
      DenseVector(-27.100428000000004, -18.4026555, -3.2654795000000005, 0.030518500000000004, -3.1434055000000005)
    )

    assert( (NN.NNRange(0, 4, 2, segment = 0)).length( dataObj.timing() ) == 3 )

    assert(dataObj.readTraceDV( 0, NN.NNRange(0, 4, 2, segment = 0)) ==
      DenseVector(-27.100428000000004, -3.2654795000000005, -3.1434055000000005)
    )
    assert(dataObj.readTraceDV( 0, NN.NNRange(0, 5, 2, segment = 0)) ==
      DenseVector(-27.100428000000004, -3.2654795000000005, -3.1434055000000005)
    )
    assert(dataObj.readTraceDV( 0, NN.NNRange(0, 6, 2, segment = 0)) ==
      DenseVector(-27.100428000000004, -3.2654795000000005, -3.1434055000000005, -4.486219500000001)
    )

    assert( dataObj.readTraceDV( 0, NN.NNRange(16382, 16388, 1, segment = 0)) ==
      DenseVector(-48.829600000000006, -46.174490500000005, -44.2823435, -49.80619200000001,
                  -62.83759150000001, -68.48351400000001, -54.017745000000005)
    )
    assert( dataObj.readTraceDV( 0, NN.NNRange(16382, 16388, 2, segment = 0)) ==
      DenseVector(-48.829600000000006, -44.2823435, -62.83759150000001, -54.017745000000005)
    )
    assert( dataObj.readTraceDV( 0, NN.NNRange(16383, 16387, 2, segment = 0)) ==
      DenseVector(-46.174490500000005, -49.80619200000001, -68.48351400000001)
    )

  }

}

