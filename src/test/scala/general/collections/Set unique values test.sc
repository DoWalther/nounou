import nounou.elements.spikes.NNSpike

import scala.collection.mutable

class TestClass(val a: Int, val b: String)
val testSet = mutable.TreeSet[TestClass]()( Ordering.by[TestClass, Int]( (x: TestClass) => x.a ) )

testSet.add( new TestClass(1, "Hello"))
testSet
testSet.add( new TestClass(2, "Hello 2"))
testSet
testSet.add( new TestClass(3, "Hello 3"))
testSet
testSet.add( new TestClass(1, "Hello Kenta"))
testSet //What will the set look like with ambiguous ordering?


val testList = List(1,1,2,3,4,5,6)
testList.toSet
testList.toSet.toArray
testList.toSet.toArray.sorted