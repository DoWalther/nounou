package nounou.analysis.spikes

import org.scalatest.FunSuite
import nounou.analysis.spikes.fastDTWscala
/**
  * Created by Dominik on 08.03.2016.
  */
class fastDTWscala$Test extends FunSuite {
  val test01 = Array(1d,1d,1d,1d,1d,1d)
  val test02 = Array(6d,5d,4d,3d,2d,1d)

  test("basic functions"){

  }

  println( fastDTWscala.reduced_by_half(test01).toList )
  println( fastDTWscala.reduced_by_half(test02).toList )

  val result = fastDTWscala.fastDTW(test01,test02)
  println(result)




}
