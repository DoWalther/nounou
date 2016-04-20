trait TestOpt
trait TestOptA extends TestOpt
trait TestOptB extends TestOpt
class DoubleOpt(value: Double)
class IntOpt(value: Int)
trait TestOptA1 extends TestOptA
case class TestOptA1Double(value: Double)
  extends DoubleOpt(value) with TestOptA1
case class TestOptA1Int(value: Int)
  extends DoubleOpt(value) with TestOptA1
case class TestOptA2Double(value: Double)
  extends DoubleOpt(value) with TestOptA

val optsSeq = Seq(TestOptA1Double(7d), TestOptA1Int(3), TestOptA2Double(1d))


optsSeq(1) match {
  case TestOptA2Double(x: Double) => println(x)
  case x: TestOptA1 => println(x.toString)
}



