package nounou.analysis.spikes

import breeze.linalg.min
import org.scalatest.FunSuite
import nounou.analysis.spikes.fastDTWscala
/**
  * Created by Dominik on 08.03.2016.
  */
class fastDTWscala$Test2 extends FunSuite {

  test("testReduced_by_half") {
    val test1 = Array(1d,2d,3d,4d,5d,6d)
    val result = fastDTWscala.reduced_by_half(test1)
    println(result.toList)
  }

}
