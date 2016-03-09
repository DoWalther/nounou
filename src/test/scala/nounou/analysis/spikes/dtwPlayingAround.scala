package nounou.analysis.spikes

import org.scalatest.FunSuite
import breeze.linalg.{min, DenseMatrix}
import breeze.numerics._
import scala.collection.mutable.ArrayBuffer
import scala.util.control._

/**
  * Created by Dominik on 07.03.2016.
  */
class dtwPlayingAround extends FunSuite {

  val template = Array(-2d,5d,21d,-45d,-2d,6d)
  val data = Array(6d,5d,4d,3d,2d,1d)
  var radius = 1
  var window = new Array[(Int, Int)]( template.length * data.length)
  var count = -1
  for(i <- 0 until template.length; j <- 0 until data.length){
    count += 1
    window(count) = (i,j)
  }
  //window.toList
  val result = new  Array[((Double, Double), (Double, Double, Double)) ](template.length * data.length)
  val costMatrix = DenseMatrix.zeros[Double](template.length, data.length)
  val cumulativeCostMatrix: DenseMatrix[Double] = DenseMatrix.zeros[Double](template.length, data.length)
  val distance = (a: Double, b: Double) => abs(a - b)
  var dt: Double = 0
  var count2 = -1
  for ((i,j)<-window) {
    costMatrix(i, j) = Math.abs(template(i) - data(j))
  }
  for ((i,j) <- window) {
    cumulativeCostMatrix(i, j) = (i, j) match {
      case (0, 0) => costMatrix(i, j)
      case (0, jx) => costMatrix(i, j) + cumulativeCostMatrix(i, j - 1)
      case (ix, 0) => costMatrix(i, j) + cumulativeCostMatrix(i - 1, j)
      case _ => costMatrix(i, j) + min(cumulativeCostMatrix(i - 1, j - 1), cumulativeCostMatrix(i - 1, j), cumulativeCostMatrix(i, j - 1))
    }
  }


  for((i,j) <- window) {
    count2 += 1
    dt = distance(template(i), data(j))
    result(count2) = (i, j) match {
      case (0, 0) => ((i, j), (costMatrix(i, j), i, j))
      case (0, jx) => ((i, j), (cumulativeCostMatrix(i, j - 1) + dt, i, j - 1))
      case (ix, 0) => ((i, j), (cumulativeCostMatrix(i - 1, j) + dt, i - 1, j))
      case _ if cumulativeCostMatrix(i - 1, j - 1) == min(cumulativeCostMatrix(i - 1, j - 1), cumulativeCostMatrix(i - 1, j), cumulativeCostMatrix(i, j-1)) => ((i, j), (cumulativeCostMatrix(i - 1, j - 1) + dt, i - 1, j - 1))
      case _ if cumulativeCostMatrix(i, j - 1) == min(cumulativeCostMatrix(i - 1, j - 1), cumulativeCostMatrix(i - 1, j), cumulativeCostMatrix(i, j-1)) => ((i, j), (cumulativeCostMatrix(i, j - 1) + dt, i, j - 1))
      case _ if cumulativeCostMatrix(i - 1, j) == min(cumulativeCostMatrix(i - 1, j - 1), cumulativeCostMatrix(i - 1, j), cumulativeCostMatrix(i, j-1)) => ((i, j), (cumulativeCostMatrix(i - 1, j) + dt, i - 1, j))
    }
  }


  //result.toList
  var path = new ArrayBuffer[(Double,Double)]()
  path.append((template.length - 1, data.length - 1))

  var totalCost: Double = cumulativeCostMatrix(5,5)
  var count3: Int =  template.length * data.length
  while(path.last != (0.0, 0.0)){
    count3 -= 1
    if(result(count3)._2 ==(totalCost, path.last._1 - 1, path.last._2 - 1)||
       result(count3)._2 ==(totalCost, path.last._1 - 1, path.last._2)||
       result(count3)._2 ==(totalCost, path.last._1 , path.last._2 - 1)){
      path.append((result(count3)._2._2, result(count3)._2._3 ))
      totalCost = cumulativeCostMatrix(result(count3)._2._2.toInt, result(count3)._2._3.toInt)
      count3 = template.length * data.length
    }
  }

  path = path.reverse
  var path_new = path.clone()
  for((i,j)<- path_new;a <- -radius to radius; b <- -radius to radius ){
    if (path_new.contains((i+a, j+b)) == false){
      path_new.append((i+a, j+b))
    }
  }
  var window_new = new ArrayBuffer[(Double, Double)]()
  var temp = new ArrayBuffer[(Double, Double)]()
  for ((i,j) <- path_new) {
    temp.append((i * 2, j * 2))
    temp.append((i * 2, j * 2 + 1))
    temp.append((i * 2 + 1, j * 2))
    temp.append((i * 2 + 1, j * 2 + 1))
    for ((a, b) <- temp) {
      if (window_new.contains((a, b)) == false) {
        window_new.append((a, b))
      }
    }
  }

  var window_temp = new ArrayBuffer[(Double, Double)]()
  var start_j = 0
  val loop = new Breaks;
  loop.breakable {
    for (i <- 0 until template.length) {
      var new_start_j = -99999
      for (j <- start_j until data.length) {
        if (window_new.contains((i, j))) {
          window_temp.append((i, j))
          if (new_start_j == -99999) {
            new_start_j = j
          }
        }
        else if (new_start_j != -99999) {
          loop.break
        }
      }
      start_j = new_start_j
    }
    println(window_temp.toList)
  }

}
