package nounou.analysis

import nounou.options._
import org.scalatest.FunSuite

/**
 * Created by ktakagaki on 15/09/15.
 */
class thresholdTest  extends FunSuite {

  var test01 = Array.tabulate(10)( (p:Int) => 0d)
  test01(2) = 100d
  test01(4) = 100d
  test01(8) = 100d

  test("threshold"){
    assert( Threshold( test01, 50 ).toList == List(2,4,8) )
    assert( Threshold( test01, 100 ).toList == List(2,4,8) )
    assert( Threshold( test01, 50, OptBlackout(3) ).toList == List(2,8) )
    assert( Threshold( test01, 50, OptBlackout(2) ).toList == List(2,8) )
    assert( Threshold( test01, 50, OptBlackout(1) ).toList == List(2,4,8) )
  }

}
