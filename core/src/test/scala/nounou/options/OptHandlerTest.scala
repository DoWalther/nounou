//package nounou.options
//
//import nounou.options.Options.{PeakWindow, MedianFactor, SpikeDetectMethod}
//import org.scalatest.FunSuite
//
///**
//  * Created by ktakagaki on 16/03/12.
//  */
//class OptHandlerTest extends FunSuite {
//
//  class optHandlerTest extends OptHandler {
//
//  }
//  val testObj = new optHandlerTest
//
//  test("readOptString"){
//    val testOptList = Seq( SpikeDetectMethod("test"), MedianFactor(777), PeakWindow(32) )
//    val optValue = testObj.readOptString[SpikeDetectMethod]( testOptList, "Default!" )
//    println(optValue)
//  }
//
//
//}
