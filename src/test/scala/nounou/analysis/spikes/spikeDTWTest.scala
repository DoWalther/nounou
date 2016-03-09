package nounou.analysis.spikes

import nounou.analysis.spikes.spikeDTW.spikeCost
import org.scalatest.FunSuite


/**
  * Created by Dominik on 25.02.2016.
  */
class spikeDTWTest extends FunSuite{
  val test01 = Array(1d, 1d, 1d, 1d, 3d, 1d, 1d, 1d, 1d, 1d)
  val test02 = Array(1d, 1d, 1d, 1d, 3d, 1d, 1d, 1d, 1d, 1d)
  val test03 = Array(1d, 2d, 3d, 1d, 2d)
  val test04 = Array(4d, 3d, 5d, 1d, 2d)

  test("spikeCost"){

    assert(spikeCost(test01, test01) == 0d)
    assert(spikeCost(test03, test04) == 8d)
    assert(spikeCost(test01, test04) == 14d)
    assert(spikeCost(test04, test01) == 14d)
    assert(spikeCost(test01, test03) == 2d)
  }
}