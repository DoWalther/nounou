package nounou.io.neuralynx

import breeze.linalg.DenseVector
import nounou._
import org.scalatest.FunSuite




class NNDataTestNCSTet4 extends FunSuite with NNTestLoaderNCS_Tet4 {

  test("readInfo"){

    //println(dataObj.timing.toStringFull )

    //trait NNTiming
    assert(dataObjTet4.timing.filterDelay == 484)
    assert( dataObjTet4.timing.segmentCount == 3 )
    assert( dataObjTet4.timing.segmentLength(0) == 3339264 )
    assert( dataObjTet4.timing.segmentLength(1) == 123392 )
    assert( dataObjTet4.timing.segmentLength(2) == 6754816 )

    assert( dataObjTet4.timing.segmentStartFrame(1) == 3339264 )

    assert( dataObjTet4.timing.segmentStartTss(0) == 2592255824L )
    assert( dataObjTet4.timing.segmentStartTss(1) == 4596169824L )
    assert( dataObjTet4.timing.segmentStartTss(2) == 4627405824L )

    assert( dataObjTet4.timing.sampleRate == 32000d)
    assert( dataObjTet4.timing.segmentEndTss(0)
      == (dataObjTet4.timing.segmentStartTss(0) + (3339264-1) *1000L /32L ) )
    assert( dataObjTet4.timing.convertTsToFrsg( 2592255824L ) == (0, 0) )

  }

  test("readPoint") {

    assert(dataObjTet4.readPoint(0, 0, segment = 0) == -27.100428000000004)
    assert(dataObjTet4.readPoint(3, 0, segment = 1) == -62.13566600000001)

  }

  test("readTrace") {

//    println(dataObj.readTraceDV( 0, NN.NNRange(0, 50, 5, segment = 0)))
    println(dataObjTet4.readTraceDV( 2, NN.NNRange(55, 70, 5, segment = 2)))

    assert(dataObjTet4.readTraceDV( 0, NN.NNRange(0, 4, 1, segment = 0)) ==
      DenseVector(-27.100428000000004, -18.4026555, -3.2654795000000005, 0.030518500000000004, -3.1434055000000005)
    )

    assert( (NN.NNRange(0, 4, 2, segment = 0)).length( dataObjTet4.timing() ) == 3 )

    assert(dataObjTet4.readTraceDV( 0, NN.NNRange(0, 4, 2, segment = 0)) ==
      DenseVector(-27.100428000000004, -3.2654795000000005, -3.1434055000000005)
    )
    assert(dataObjTet4.readTraceDV( 0, NN.NNRange(0, 5, 2, segment = 0)) ==
      DenseVector(-27.100428000000004, -3.2654795000000005, -3.1434055000000005)
    )
    assert(dataObjTet4.readTraceDV( 0, NN.NNRange(0, 6, 2, segment = 0)) ==
      DenseVector(-27.100428000000004, -3.2654795000000005, -3.1434055000000005, -4.486219500000001)
    )

    assert( dataObjTet4.readTraceDV( 0, NN.NNRange(16382, 16388, 1, segment = 0)) ==
      DenseVector(-48.829600000000006, -46.174490500000005, -44.2823435, -49.80619200000001,
                  -62.83759150000001, -68.48351400000001, -54.017745000000005)
    )
    assert( dataObjTet4.readTraceDV( 0, NN.NNRange(16382, 16388, 2, segment = 0)) ==
      DenseVector(-48.829600000000006, -44.2823435, -62.83759150000001, -54.017745000000005)
    )
    assert( dataObjTet4.readTraceDV( 0, NN.NNRange(16383, 16387, 2, segment = 0)) ==
      DenseVector(-46.174490500000005, -49.80619200000001, -68.48351400000001)
    )

  }

}

