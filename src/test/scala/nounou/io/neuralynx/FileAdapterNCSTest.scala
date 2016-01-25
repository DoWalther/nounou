package nounou.io.neuralynx

import breeze.linalg.DenseVector
import nounou._
import nounou.elements.data.NNDataChannel
import org.scalatest.FunSuite

/**
* @author ktakagaki
* //@date 1/30/14.
*/
class FileAdapterNCSTest extends FunSuite {

  //ToDo 2: consolidate the following loading code to one parent class for all tests using this real data
  //ToDo ?: make test data load from online?
  val testFileE04LC_CSC1 = getClass.getResource("/nounou/Neuralynx/E04LC/CSC1.ncs").getPath()
  val data = NN.load(testFileE04LC_CSC1).apply(0)
  assert( data.isInstanceOf[NNDataChannel] )
  assert( data.isInstanceOf[NNDataChannelFileReadNCS] )
  val dataObj = data.asInstanceOf[NNDataChannelFileReadNCS]

  test("readInfo"){

    assert( dataObj.scale.absolutePerShort - 0.015259300 < 1E-9 )
    assert( dataObj.scale.unit.contentEquals("microV") )

    //trait XFrames
    assert( dataObj.timing.segmentLength(0) == 2546176 )
    assert( dataObj.timing.segmentLength(8) == 3902976 )
    assert( dataObj.timing.segmentStartFrame(1) == 2546176 )
    assert( dataObj.timing.segmentCount == 94 )

    assert( dataObj.header.getHeaderDspFilterDelay == 484 )
    val filterDel = dataObj.header.getHeaderDspFilterDelay

    assert( dataObj.timing.endTs == 27500375120L - filterDel   )
    assert( dataObj.timing.startTs == 10237373715L - filterDel )

    assert(dataObj.timing.sampleRate == 32000D)
    //assert(dataObj.timing.factorTSperFR == 1000000D/dataObj.timing.sampleRate)

    assert( dataObj.timing.segmentStartTss(0) == 10237373715L - filterDel )
    assert( dataObj.timing.segmentEndTss(0)   == (dataObj.timing.segmentStartTss(0) + (2546176L-1) *1000L /32L ) )
    assert( dataObj.timing.convertTsToFrsg( 10245373715L - filterDel ) == (500*512, 0) )
    assert( dataObj.timing.segmentStartTss(1) == (10664246433L - filterDel) )
    assert( dataObj.timing.convertTsToFrsg( 10664246433L - filterDel ) == (0, 1) )

  }

  test("readPoint") {

    //assert(dataObj.scale.xBits==1024)
    assert(dataObj.timing.segmentLength(0)==2546176)
//    assert(dataObj.readPoint(0,0) == dataObj.scale.convertShortToAbsolute( -1528.toShort ) )
//    assert(dataObj.readPoint(512,0) == dataObj.scale.convertShortToAbsolute( -1908.toShort ) )

  }

  test("readTrace") {

//    assert( dataObj.readTrace( NN.NNRange(0, 0, 1, 0) )(0) ==
//      dataObj.scale.convertShortToAbsolute( -1528.toShort ) )

    assert( dataObj.readTraceDV( NN.NNRange(0, 4, 1, 0) ) ==
      dataObj.scale.convertShortToAbsolute(  DenseVector(-1528, -1841, -1282, -670, -500).map(_ toShort) )
    )

    assert( dataObj.readTraceDV( NN.NNRange(0, 4, 2, 0) ) ==
      dataObj.scale.convertShortToAbsolute(  DenseVector(-1528, -1282, -500).map(_ toShort) )
    )

    assert( dataObj.readTraceDV( NN.NNRange(-2, 4, 2, 0) ) ==
      dataObj.scale.convertShortToAbsolute(  DenseVector(0, -1528, -1282, -500).map(_ toShort) )
    )

  }

}
