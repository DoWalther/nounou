package nounou._tutorials

import org.scalatest.FunSuite

/**
  * Created by Dominik on 08.03.2016.
  */
class CallByReferenceTutorial extends FunSuite {

  var aPrim: Int = 1
  var bPrim: Int = aPrim
  bPrim = 3
  println( s"aPrim is $aPrim, because we are using primitives" )

  var aObj = Array(1,2,3)
  val bObj = aObj
  bObj(0) = 1000
  println( s"aObj(0) is ${aObj(0)}, since bObj is only a reference to an object, the same object that aObj points to!" )


  val aArray = Array(1,2,3,4)
  println( aArray(0) )
  aArray(0) = 100

  val aList = List(1,2,3,4)
  println( aList(0) )
  val newAList: List[Int] = 100 :: aList.tail

}
