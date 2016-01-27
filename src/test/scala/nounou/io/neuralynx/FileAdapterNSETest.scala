package nounou.io.neuralynx

import nounou.NN
import org.scalatest.FunSuite

/**
  * Created by ktakagaki on 16/01/27.
  */
class FileAdapterNSETest extends FunSuite with NNTestLoaderNSE_STet4a {

  test("load"){

    assert( dataObj.getTiming.sampleRate==32000d)
    val database: Array[NNSpikeNeuralynx] = dataObj.iterator().toArray.map( _.asInstanceOf[NNSpikeNeuralynx])
    assert( database(0).timestamp == 2790151667L )
    assert( database(0).getSnData(dataObj.scaling).toList ==
      List(799, 5633, 6946, 5439, 4846, 8034, 14052, 18830, 18801, 14145, 8402, 5038, 4563, 4803, 3628, 1346, 138, 1050, 2531, 2070, -1236, -5523, -7625, -5998, -2106, 921, 850, -1880, -4603, -4812, -2045, 2172)
    )
    assert( dataObj.scaling.absolutePerShort == 0.015259300000000002 )
    assert( dataObj.scaling.convertShortToAbsolute( Array[Short](-1929, -689, 1586)).toList ==
      List(-29.435189700000002, -10.513657700000001, 24.201249800000003)
    )

    assert( database(2).waveformAbsMax == 259.71328600000004 )
    assert( database(2).waveformMax == 259.71328600000004 )
    assert( database(2).waveformMin == -69.73500100000001)


  }

  test("save"){
    NN.save("C:/temp/nounou/NSE.nse", dataObj)
    println( s" FileAdapterNSETest: check the file C:/temp/nounou/NSE.nse against $testFileSTet4a ")
  }

}
