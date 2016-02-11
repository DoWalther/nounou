package nounou._profiling

import java.math.BigInteger

import nounou.NN
import nounou.elements.data.filters.{NNFilterBuffer, NNFilterDecimate, NNFilterFIR}
import nounou.io.neuralynx.NNTestLoaderNCS_Tet4
import org.scalatest.FunSuite

/**
  * Created by ktakagaki on 16/01/31.
  */
class profileNlxNcsToFIRToDecimate extends FunSuite with NNTestLoaderNCS_Tet4 {

  test("profile"){

    val tempFilterObj = new NNFilterFIR ( new NNFilterBuffer( dataObj ) )
    tempFilterObj.setFilterHz(1d, 200d)
    val tempDecimateObj = new NNFilterDecimate(new NNFilterBuffer(tempFilterObj), 16)

    println(tempDecimateObj.timing.toStringFull())
    for(i <- 0 to 100){
      val temp = tempDecimateObj.readPage( NN.NNRange(i*4000, i*4000+10000, 1, 2) )
      print(temp.length + " ")
    }


  }

}
