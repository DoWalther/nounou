package nounou.io.neuralynx

import breeze.linalg.DenseVector
import nounou._
import org.scalatest.FunSuite



class NNDataChannelNCSTest extends FunSuite with NNTestLoaderNCS_E04LC_NNDataChannel {

  test("NNDataChannelNCS Specific"){
    assert( dataChannelNCSObj.scaling.absolutePerShort - 0.015259300 < 1E-9 )
    assert( dataChannelNCSObj.header.getHeaderDspFilterDelay == 484 )

    assert( dataChannelNCSObj.readTraceDV( NN.NNRange(0, 4, 1, 0) ) ==
      dataChannelNCSObj.scaling.convertShortToAbsolute(  DenseVector(-1528, -1841, -1282, -670, -500).map(_ toShort) )
    )

    assert( dataChannelNCSObj.readTraceDV( NN.NNRange(0, 4, 2, 0) ) ==
      dataChannelNCSObj.scaling.convertShortToAbsolute(  DenseVector(-1528, -1282, -500).map(_ toShort) )
    )

    assert( dataChannelNCSObj.readTraceDV( NN.NNRange(-2, 4, 2, 0) ) ==
      dataChannelNCSObj.scaling.convertShortToAbsolute(  DenseVector(0, -1528, -1282, -500).map(_ toShort) )
    )

  }

}

class NNDataChannelTest extends FunSuite with NNTestLoaderNCS_E04LC_NNDataChannel {

  test("readInfo"){

    //assert( dataChannelObj.scale.absolutePerShort - 0.015259300 < 1E-9 )
    assert( dataChannelObj.scaling.unit.contentEquals("microV") )

    //trait NNTiming
    assert( dataChannelObj.timing.segmentLength(0) == 2546176 )
    assert( dataChannelObj.timing.segmentLength(8) == 3902976 )
    assert( dataChannelObj.timing.segmentStartFrame(1) == 2546176 )
    assert( dataChannelObj.timing.segmentCount == 94 )
    assert( dataChannelObj.timing.endTs == 27500375120L - filterDel   )
    assert( dataChannelObj.timing.startTs == 10237373715L - filterDel )
    assert( dataChannelObj.timing.sampleRate == 32000D)

    assert( dataChannelObj.timing.segmentStartTss(0) == 10237373715L - filterDel )
    assert( dataChannelObj.timing.segmentEndTss(0)   == (dataChannelNCSObj.timing.segmentStartTss(0) + (2546176L-1) *1000L /32L ) )
    assert( dataChannelObj.timing.convertTsToFrsg( 10245373715L - filterDel ) == (500*512, 0) )
    assert( dataChannelObj.timing.segmentStartTss(1) == (10664246433L - filterDel) )
    assert( dataChannelObj.timing.convertTsToFrsg( 10664246433L - filterDel ) == (0, 1) )

  }

  test("readPoint") {

    assert(dataChannelObj.timing.segmentLength(0)==2546176)

  }

  test("readTrace") {

    assert( dataChannelObj.readTraceDV(NN.NNRange(0, 4, 1, 0)) ==
      DenseVector(-23.316210400000003, -28.092371300000003, -19.5624226, -10.223731, -7.629650000000001) )

    assert( dataChannelObj.readTrace( NN.NNRange(0, 4, 2, 0) ) ==
      Array(-23.316210400000003, -19.5624226, -7.629650000000001))

  }

}

