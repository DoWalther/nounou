package nounou.analysis.spikes

import org.scalatest.FunSuite
import nounou.analysis.spikes.fastDTWscala.reduced_by_half

/**
  * Created by Dominik on 03.03.2016.
  */
class fastDTWscalaTest extends FunSuite {
  val test00 = Array(1d,2d,3d,4d)
  test("testReduced_by_half") {
    assert(reduced_by_half(test00) == Array(1.5d,2.5d))


  }

}
