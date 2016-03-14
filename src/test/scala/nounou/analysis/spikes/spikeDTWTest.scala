package nounou.analysis.spikes

import nounou.analysis.spikes.spikeDTW.spikeCost
import org.scalatest.FunSuite


/**
  * Created by Dominik on 25.02.2016.
  */
class spikeDTWTest extends FunSuite{
  val test1 = Array.tabulate[Double](5000)( _ + 2d ) //Array(6d,5d,4d,3d,3d,4d,5d,6d)
  val test2 = Array.tabulate[Double](10000)( _ * 5d )
  val test03 = Array(1d, 2d, 3d, 1d, 2d)
  val test04 = Array(4d, 3d, 5d, 1d, 2d)

  test("spikeCost"){

    val result = spikeCost(test1, test2)
    println(result)

  }
}