package nounou.io.neuralynx

import nounou.NN
import nounou.elements.data.filters.{NNFilterBuffer, NNFilterDecimate, NNFilterFIR}
import org.scalatest.FunSuite

/**
  * Created by ktakagaki on 16/01/31.
  */
class Profile_NlxNcsToFIRToDecimate extends FunSuite with NNTestLoaderNCS_Tet4 {

//  test("profile"){
//
//    val tempFilterObj = new NNFilterFIR ( new NNFilterBuffer( dataObjTet4 ) )
//    tempFilterObj.setFilterHz(1d, 200d)
//    val tempDecimateObj = new NNFilterDecimate(new NNFilterBuffer(tempFilterObj), 16)
//
//    println(tempDecimateObj.timing.toStringFull())
//    for(i <- 0 to 100){
//      val temp = tempDecimateObj.readPage( NN.NNRange(i*4000, i*4000+10000, 1, 2) )
//      print(temp.length + " ")
//    }
//
//
//  }

}
