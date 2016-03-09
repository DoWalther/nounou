package nounou.analysis.spikes

import breeze.linalg.{max, min, DenseMatrix}
import breeze.numerics._

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks


/**
  * Created by Dominik on 02.03.2016.
  */
object fastDTWscala {


  def fastDTW (template: Array[Double], data: Array[Double],  radius: Int = 1): (Double, Array[(Int, Int)]) = {
    val min_time_size: Double = radius + 2
    val sizeTemplate: Int = template.length
    val sizeData: Int = data.length
    var template_shrinked: Array[Double] = Array()
    var data_shrinked: Array[Double] = Array()

    if(sizeTemplate < min_time_size || sizeData < min_time_size){
      return dtw(template, data)
    }

    data_shrinked = reduced_by_half(data)
    template_shrinked = reduced_by_half(template)
    val (dist, path) = fastDTW(template_shrinked, data_shrinked, radius = radius)
    val window: mutable.HashSet[(Int, Int)] = expand_window( mutable.HashSet[(Int, Int)]() ++= path, sizeTemplate, sizeData, radius)
    println("window" + window.toList)
    dtw(template, data, window.toArray)
  }

  def dtw( template: Array[Double], data: Array[Double]): (Double, Array[(Int, Int)]) =  {
    val window = new ArrayBuffer[(Int, Int)]()

//    if window is None:
//      window = [(i, j) for i in xrange(len_x) for j in xrange(len_y)]
//    window = ((i + 1, j + 1) for i, j in window)

    for(i <- 0 until template.length; j <- 0 until data.length){
      window.append((i,j))
    }

    dtw(template, data, window.toArray)

  }

  def dtw( template: Array[Double], data: Array[Double], window: Array[(Int, Int)]): (Double, Array[(Int, Int)]) = {

    //Dictionary of position -> (cumCost, prior pos x, prior pos y)
    //val result = new Array[((Int, Int), (Double, Int, Int)) ](template.length * data.length)
    val result = new mutable.HashMap[(Int, Int), (Double, Int, Int)]{
      override def default(key: (Int, Int))= (Double.PositiveInfinity, -100, -100 )
    }
    result.+=( (0,0) -> (0d, 0, 0) )

//    println( "window " + window.toList )

    for( (i,j) <- (window.map( (f: (Int, Int)) => (f._1 + 1, f._2 + 1))) ) {
      val dt = Math.abs(template(i-1) - data(j-1))
      result += ( (i,j) ->  {
        val temp = Array( (result( (i-1, j) )._1 + dt, i-1, j), (result( (i, j-1) )._1 + dt, i, j-1), (result( (i-1, j-1) )._1 + dt, i-1, j-1) )
        var tempReturn = temp(0)
        if( temp(1)._1 < tempReturn._1 ) tempReturn = temp(1)
        if( temp(2)._1 < tempReturn._1 ) tempReturn = temp(2)
        tempReturn
      } )
    }
//    //val costMatrix = DenseMatrix.zeros[Double](template.length, data.length)
//    val cumulativeCostMatrix = DenseMatrix.zeros[Double](template.length, data.length)
//    val distance = (a: Double, b: Double) => abs(a - b)
//    var dt: Double = 0
//
//    //println("window" + window.toList)
//    for( (i,j) <- window ) costMatrix(i, j) = Math.abs(template(i) - data(j))
//
//    for ((i,j) <- window){
//      cumulativeCostMatrix(i, j) = (i, j) match {
//        case (0, 0) => costMatrix(i, j)
//        case (0, jx) => costMatrix(i, j) + cumulativeCostMatrix(i, j - 1)
//        case (ix, 0) => costMatrix(i, j) + cumulativeCostMatrix(i - 1, j)
//        case _ => costMatrix(i, j) + min(cumulativeCostMatrix(i - 1, j - 1), cumulativeCostMatrix(i - 1, j), cumulativeCostMatrix(i, j - 1))
//      }
//    }

//    println("result" + result.toList)
//    var count2 = -1
//    for((i,j) <- window) {
//      count2 += 1
//      dt = distance(template(i), data(j))
//      result(count2) = (i, j) match {
//        case (0, 0) => {results.+=((i, j) -> (costMatrix(i, j), i, j))}
//        case (0, jx) => ((i, j), (cumulativeCostMatrix(i, j - 1) + dt, i, j - 1))
//        case (ix, 0) => ((i, j), (cumulativeCostMatrix(i - 1, j) + dt, i - 1, j))
//        case _ if cumulativeCostMatrix(i - 1, j - 1) == min(cumulativeCostMatrix(i - 1, j - 1), cumulativeCostMatrix(i - 1, j), cumulativeCostMatrix(i, j-1)) => ((i, j), (cumulativeCostMatrix(i - 1, j - 1) + dt, i - 1, j - 1))
//        case _ if cumulativeCostMatrix(i, j - 1) == min(cumulativeCostMatrix(i - 1, j - 1), cumulativeCostMatrix(i - 1, j), cumulativeCostMatrix(i, j-1)) => ((i, j), (cumulativeCostMatrix(i, j - 1) + dt, i, j - 1))
//        case _ if cumulativeCostMatrix(i - 1, j) == min(cumulativeCostMatrix(i - 1, j - 1), cumulativeCostMatrix(i - 1, j), cumulativeCostMatrix(i, j-1)) => ((i, j), (cumulativeCostMatrix(i - 1, j) + dt, i - 1, j))
//      }
//
      //      result(count2) = (i, j) match {
//        case (0, 0) => ((i, j), (costMatrix(i, j), i, j))
//        case (0, jx) => ((i, j), (cumulativeCostMatrix(i, j - 1) + dt, i, j - 1))
//        case (ix, 0) => ((i, j), (cumulativeCostMatrix(i - 1, j) + dt, i - 1, j))
//        case _ if cumulativeCostMatrix(i - 1, j - 1) == min(cumulativeCostMatrix(i - 1, j - 1), cumulativeCostMatrix(i - 1, j), cumulativeCostMatrix(i, j-1)) => ((i, j), (cumulativeCostMatrix(i - 1, j - 1) + dt, i - 1, j - 1))
//        case _ if cumulativeCostMatrix(i, j - 1) == min(cumulativeCostMatrix(i - 1, j - 1), cumulativeCostMatrix(i - 1, j), cumulativeCostMatrix(i, j-1)) => ((i, j), (cumulativeCostMatrix(i, j - 1) + dt, i, j - 1))
//        case _ if cumulativeCostMatrix(i - 1, j) == min(cumulativeCostMatrix(i - 1, j - 1), cumulativeCostMatrix(i - 1, j), cumulativeCostMatrix(i, j-1)) => ((i, j), (cumulativeCostMatrix(i - 1, j) + dt, i - 1, j))
//      }
//    }
//    println("result" + result.toList)

    var path = new ArrayBuffer[(Int,Int)]()
    path.append( (template.length - 1, data.length - 1) )

    var totalCostMax: Double = max( result.valuesIterator.map( _._1 ) )
    println(result)
//    assert( totalCostMax == result( (template.length - 1, data.length - 1) )._1 )

    var i = template.length
    var j = data.length
println( result.toList )
    while( i != 0 || j != 0 ){
      path.append( (i-1, j-1))
      val temp = result(i, j)
      i = temp._2
      j = temp._3
    }

    (totalCostMax, path.toArray.reverse)

//    //var totalCost: Double = cumulativeCostMatrix(template.length - 1,data.length - 1)
//    var count3: Int =  template.length * data.length
//    while(path.last != (0.0, 0.0)){
//      count3 -= 1
//      if(result(count3)._2 ==(totalCost, path.last._1 - 1, path.last._2 - 1)||
//        result(count3)._2 ==(totalCost, path.last._1 - 1, path.last._2)||
//        result(count3)._2 ==(totalCost, path.last._1 , path.last._2 - 1)){
//        path.append((result(count3)._2._2, result(count3)._2._3 ))
//        totalCost = cumulativeCostMatrix(result(count3)._2._2.toInt, result(count3)._2._3.toInt)
//        count3 = template.length * data.length
//      }
//    }
//    path = path.reverse
//    (cumulativeCostMatrix(template.length - 1, data.length - 1), path.toArray)
  }


  def reduced_by_half(template: Array[Double]): Array[Double] ={
    val modulo: Int = template.length % 2
    var temp: Array[Double] = null
    if (modulo == 0) {
      temp = new Array[Double](template.length / 2)
    }
    else {
      temp = new Array[Double](template.length / 2 + 1)
    }
    var i: Int = 0

    var counter: Int = -1
    while (i < template.length) {
      counter += 1
      temp(counter) = (template(i / 2) + template(1 + i / 2)) / 2
      i += 2
    }
    temp
  }

//  def dtwNew( template: Array[Double], data: Array[Double]): (Double, Array[(Int, Int)]) = {
//
//    val sizeTemplate = template.length
//    val sizeData =  data.length
//    val sizeTemplateM1 = sizeTemplate - 1
//    val sizeDataM1 =  sizeData -1
//
//
//    // <editor-fold defaultstate="collapsed" desc=" buffering results ">
//
//    val buffCM = DenseMatrix.zeros[Double](template.length, data.length)
//    val isBuffCMCalculated: Array[Array[Boolean]] =  Array.tabulate[Array[Boolean]](sizeTemplate)( (fi: Int) => new Array[Boolean](sizeData) )
//    val buffCCM = DenseMatrix.zeros[Double](template.length, data.length)
//    val isBuffCCMCalculated: Array[Array[Boolean]] =  Array.tabulate[Array[Boolean]](sizeTemplate)( (fi: Int) => new Array[Boolean](sizeData) )
//
//    def getCostMatrix( i: Int, j: Int ): Double = {
//      if(!isBuffCMCalculated(i)(j)){
//        buffCM(i, j) = Math.abs(template(i) - data(j))
//        isBuffCMCalculated(i)(j) = true
//      }
//      buffCM(i,j)
//    }
//    def getCumulativeCostMatrix( i: Int, j: Int ): Double = {
//      if(!isBuffCCMCalculated(i)(j)){
//        buffCCM(i, j) = (i, j) match {
//          case (0, 0) => getCostMatrix(0, 0)
//          case (0, jx) => getCostMatrix(0, j) + getCumulativeCostMatrix(0, j - 1)
//          case (ix, 0) => getCostMatrix(i, 0) + getCumulativeCostMatrix(i - 1, 0)
//          case _ => getCostMatrix(i, j) + min(getCumulativeCostMatrix(i - 1, j - 1), getCumulativeCostMatrix(i - 1, j), getCumulativeCostMatrix(i, j - 1))
//        }
//        isBuffCCMCalculated(i)(j) = true
//      }
//      buffCCM(i,j)
//    }
//    // </editor-fold>
//
//    val radius = 2
//
//    def dtwImpl(currI: Int, currJ: Int, currPath: List[(Int,Int)]): (Int, Int, List[(Int,Int)]) = {
//      if( currI == sizeTemplateM1 ) {
//        if( currJ == sizeDataM1 ) (sizeTemplateM1, sizeDataM1, currPath)
//        else   dtwImpl(sizeTemplateM1, currJ + 1, (0, currJ + 1) :: currPath)
//      }else{
//        if( currJ == sizeDataM1 ) dtwImpl(currI + 1, sizeDataM1, (currI + 1, 0) :: currPath)
//        else {
//          val currINew = scala.math.min(currI + radius, sizeTemplateM1)
//          val currJNew = scala.math.min(currJ + radius, sizeDataM1)
//
//        }
//      }
//
//
//          val min = min(getCumulativeCostMatrix(currI - 1, currJ - 1), getCumulativeCostMatrix(currI - 1, currJ), getCumulativeCostMatrix(currI, currJ - 1))
//          if (getCumulativeCostMatrix(currI - 1, currJ - 1) == min) dtwImpl(currI - 1, currJ - 1, (currI - 1, currJ - 1) :: currPath)
//          else if (getCumulativeCostMatrix(currI, currJ - 1) == min) dtwImpl(currI, currJ - 1, (currI, currJ - 1) :: currPath)
//          else dtwImpl(currI - 1, currJ, (currI - 1, currJ) :: currPath) //getCumulativeCostMatrix(currI - 1, currJ) == min
//        }
//      }
//    }
//
//    //    @tailrec
////    def dtwImpl(currI: Int, currJ: Int, currPath: List[(Int,Int)]): List[(Int,Int)] = {
////      (currI, currJ) match {
////        case (0, 0) => currPath
////        case (0, matchedJ: Int) => dtwImpl(0, matchedJ - 1, (0, matchedJ - 1) :: currPath)
////        case (matchedI: Int, 0) => dtwImpl(matchedI - 1, 0, (matchedI - 1, 0) :: currPath)
////        case (matchedI: Int, matchedJ: Int) => {
////          val min = min(getCumulativeCostMatrix(currI - 1, currJ - 1), getCumulativeCostMatrix(currI - 1, currJ), getCumulativeCostMatrix(currI, currJ - 1))
////          if (getCumulativeCostMatrix(currI - 1, currJ - 1) == min) dtwImpl(currI - 1, currJ - 1, (currI - 1, currJ - 1) :: currPath)
////          else if (getCumulativeCostMatrix(currI, currJ - 1) == min) dtwImpl(currI, currJ - 1, (currI, currJ - 1) :: currPath)
////          else dtwImpl(currI - 1, currJ, (currI - 1, currJ) :: currPath) //getCumulativeCostMatrix(currI - 1, currJ) == min
////        }
////      }
////    }
//
//    (
//      getCumulativeCostMatrix(sizeTemplate - 1, sizeData - 1),
//      dtwImpl( sizeTemplate - 1, sizeData - 1, List( (sizeTemplate - 1, sizeData - 1) ) ).toArray
//    )
//
//  }

  def expand_window(path: mutable.HashSet[(Int,Int)], sizeTemplate: Int, sizeData: Int, radius: Int ): mutable.HashSet[(Int, Int)] = {

    val path_ : mutable.HashSet[(Int, Int)] = path.clone()

    for( (i,j) <- path; a <- -radius to radius; b <- -radius to radius ) path_.+=( (i+a, j+b) )
    //{
      //if (path_.contains((i+a, j+b)) == false){
      //  path_.append((i+a, j+b))
      //}
    //}

    val window_ = new mutable.HashSet[(Int, Int)]()
    for( (i,j) <- path_ ) window_.++=( List(  (i * 2, j * 2), (i * 2, j * 2 + 1), (i * 2 + 1, j * 2), (i * 2 + 1, j * 2 + 1) )  )

    val window = new mutable.HashSet[(Int, Int)]()
    var start_j = 0

    for (i <- 0 until sizeTemplate) {
      var new_start_j: Int = -1 //valid values of new_start_j will always be >=0 Option[Int] = null
      var j = start_j
      while( j < sizeData && new_start_j != -1 ) {
        if (window_.contains((i, j))) {
          window.+=((i, j))
          if (new_start_j == -1) new_start_j = j
        }
        j += 1
      }
      start_j = new_start_j
    }

    window

  }



  //    val loop = new Breaks;
//    loop.breakable {
//      for (i <- 0 until sizeTemplate) {
//        var new_start_j: Int = -1 //valid values of new_start_j will always be >=0 Option[Int] = null
//        for (j <- start_j until sizeData) {
//          if (window_new.contains((i, j))) {
//            window_temp.append((i, j))
//            if (new_start_j == -1) {
//              new_start_j = j
//            }
//          }
//          else if (new_start_j != -1) {
//            loop.break
//          }
//        }
//        start_j = new_start_j
//      }
//    }
//    println(sizeData)
//    println(sizeTemplate)
//    return window_temp
//
//  }
}
