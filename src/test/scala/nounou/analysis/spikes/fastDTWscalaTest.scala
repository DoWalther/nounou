package nounou.analysis.spikes

import org.scalatest.FunSuite

/**
  * Created by Dominik on 09.03.2016.
  */
class fastDTWscalaTest extends FunSuite {


 /* test("test reduced_by_half") {



    val test1 = Array(1d,2d,3d,4d,5d,6d)
    val result1 = fastDTWscala.reduced_by_half(test1)
    assert(result1.toList == List(1.5, 2.5, 3.5))

    val test2 = Array(6d,5d,4d,3d,2d)
    val result2 = fastDTWscala.reduced_by_half(test2)
    assert(result2.toList == List(5.5, 4.5, 3.5))

  }

  test("test expand_window") {

    //Thread.sleep(10000)

    val test01 = mutable.HashSet((0,0), (1,1), (2,2))
    val length1 = 3
    val length2 = 3
    val radius = 1

    val result = expand_window(test01, length1, length2, radius)
    assert(result == Set((0, 0), (0, 1), (0, 2), (1, 0), (1, 1), (1, 2), (2, 0), (2, 1), (2, 2)))
  }*/

  test("fastDTW"){

    val test1 = Array.tabulate[Double](5000)( _ + 2d ) //Array(6d,5d,4d,3d,3d,4d,5d,6d)
    val test2 = Array.tabulate[Double](10000)( _ * 5d ) //Array(1d,2d,3d,4d,5d)
    val result3 = fastDTWscala.fastDTW(test1, test2)
    println(result3)
    //assert(result3 == (10, List((0, 0), (1, 1), (2, 2), (3, 2), (4, 2), (5, 3), (6, 4), (7, 4))))
    /*val test3 = Array(-1d,-23422d, 34d, 23d, 1d, 2d, 5d)
    val test4 = Array(1d,2d,3d,4d,4d,5d)
    val result4 = fastDTWscala.fastDTW( test3, test4)
    assert(result4 == (23481, List((0, 0), (1, 1), (2, 2), (3, 3), (4, 4), (5, 4), (6, 5))))
    //val test3 = Array(-1d,-23422d, 34d, 23d, 1d, 2d, 5d)
    //val test4 = Array(1d,2d,3d,4d,4d,5d)*/
  }



}
