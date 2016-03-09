package nounou.analysis.spikes

import nounou.analysis.spikes.dtwScala.cost
import org.scalatest.FunSuite

/**
 * Created by ktakagaki on 15/09/15.
 */
class dtwTest extends FunSuite {

  val test01 = Array(0d,0d,0d,0d,0d)
  val test02 = Array(1d,1d,1d,1d,1d)

  val test03 = Array(4d, -19d, 5d, -9d)
  val test04 = Array(23d, 14d)

  test("cost"){

    assert( cost(test01, test02) == 5d )
    assert( cost(test03, test04) == 84d)
    assert( cost(test01, test03) == 41d)
    assert( cost(test02, test03) == 40d)
    //DW: noch eins komplizierter

  }

}
