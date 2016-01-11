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

  //val testFileTet4a = getClass.getResource("/_testFiles/Neuralynx/t130911/Tet4a.ncs").getPath()
  val testFileE04LC_CSC1 = getClass.getResource("/nounou/Neuralynx/E04LC/CSC1.ncs").getPath()
  //new File( "C:\\prog\\_gh\\_kt\\nounou.testfiles\\Neuralynx\\E04LC\\CSC1.ncs" )
  val data = NN.load(testFileE04LC_CSC1).apply(0)
  assert( data.isInstanceOf[NNDataChannel] )
  assert( data.isInstanceOf[NNDataChannelFileReadNCS] )
  val dataObj = data.asInstanceOf[NNDataChannelFileReadNCS]

  test("readInfo"){

    assert( dataObj.scale.absGain == 1.4901660156250002E-5 ) //1.0E6* 3.05185E-8 / 1024d )
    assert( dataObj.scale.absOffset == 0d )
    assert( dataObj.scale.unit.contentEquals("microV") )

    //trait XFrames
    assert( dataObj.timing.segmentLength(0) == 2546176 )
    assert( dataObj.timing.segmentLength(8) == 3902976 )
    assert( dataObj.timing.segmentStartFrame(1) == 2546176)
    assert( dataObj.timing.segmentCount == 94 )

    assert( dataObj.timing.endTs == 27500375120L )
    assert( dataObj.timing.startTs == 10237373715L )
//    intercept[IllegalArgumentException] {
//      dataObj.timing.length
//    }

    assert(dataObj.timing.sampleRate == 32000D)
    //assert(dataObj.timing.factorTSperFR == 1000000D/dataObj.timing.sampleRate)

    assert(dataObj.timing.segmentStartTss(0) == 10237373715L)// - 9223372036854775807L-1/*2^63*/))
    assert(dataObj.timing.convertTsToFrsg(10245373715L/* - 9223372036854775807L-1*//*2^63*/) == (500*512, 0))
    assert(dataObj.timing.segmentStartTss(1) == (10664246433L))// - 9223372036854775807L-1/*2^63*/))
    assert(dataObj.timing.convertTsToFrsg(10664246433L /*- 9223372036854775807L-1*//*2^63*/) == (0, 1))
    assert(dataObj.timing.segmentEndTss(0)   == (dataObj.timing.segmentStartTss(0) + (2546176L-1) *1000 /32 ) )

  }

  test("readPoint") {

    assert(dataObj.scale.xBits==1024)
    assert(dataObj.timing.segmentLength(0)==2546176)
    assert(dataObj.readPoint(0,0) == dataObj.scale.convertIntToAbsolute( -1528*dataObj.scale.xBits ) )
    assert(dataObj.readPoint(512,0) == dataObj.scale.convertIntToAbsolute( -1908*dataObj.scale.xBits) )

  }

  test("readTrace") {

    assert( dataObj.readTraceInt( NN.NNRange(0, 0, 1, 0) )(0) ==
      -1528*dataObj.scale.xBits )

    assert( dataObj.readTraceDV( NN.NNRange(0, 4, 1, 0) ) ==
      dataObj.scale.convertIntToAbsolute(  DenseVector(-1528, -1841, -1282, -670, -500).map(_ * dataObj.scale.xBits) )
    )

    assert( dataObj.readTraceDV( NN.NNRange(0, 4, 2, 0) ) ==
      dataObj.scale.convertIntToAbsolute(  DenseVector(-1528, -1282, -500).map(_ * dataObj.scale.xBits) )
    )

    assert( dataObj.readTraceDV( NN.NNRange(-2, 4, 2, 0) ) ==
      dataObj.scale.convertIntToAbsolute(  DenseVector(0, -1528, -1282, -500).map(_ * dataObj.scale.xBits) )
    )

  }

}
