package nounou.analysis.spikes

import org.scalatest.FunSuite
import nounou.analysis.spikes.fastDTWscala.expand_window

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Created by Dominik on 09.03.2016.
  */
class fastDTWscala$Test3 extends FunSuite {

  val test01 = mutable.HashSet((0,0), (1,1), (2,2))
  val length1 = 3
  val length2 = 3
  val radius = 1

  test("test expand_window") {
    var result = expand_window(test01, length1, length2, radius)
    assert(result == Set((0, 0), (0, 1), (0, 2), (1, 0), (1, 1), (1, 2), (2, 0), (2, 1), (2, 2)))
  }

  test("test reduced_by_half") {
    val test1 = Array(1d,2d,3d,4d,5d,6d)
    val result = fastDTWscala.reduced_by_half(test1)
    println(result.toList)
  }


}
