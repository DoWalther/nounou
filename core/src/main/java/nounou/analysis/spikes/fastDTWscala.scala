package nounou.analysis.spikes

import breeze.linalg.{max, min, DenseMatrix}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Created by Dominik on 02.03.2016.
  * https://github.com/slaypni/fastdtw
  */
object fastDTWscala {


  def fastDTW (template: Array[Double], data: Array[Double],  radius: Int = 1): (Double, List[(Int, Int)]) = {
    /**
      * Calculate the total cost and warp path between input sequence and template.
      *
      * @return (total cost,[warp path])
      */
    val min_time_size: Double = radius + 2
    val sizeTemplate: Int = template.length
    val sizeData: Int = data.length
    var template_shrinked: Array[Double] = Array()
    var data_shrinked: Array[Double] = Array()

    // If template or data is already shrinked enough, calculate the DTW
    if(sizeTemplate < min_time_size || sizeData < min_time_size){
      return dtw(template, data)
    }

    // Shrink template and input array
    data_shrinked = reduced_by_half(data)
    template_shrinked = reduced_by_half(template)
    val (dist, path) = fastDTW(template_shrinked, data_shrinked, radius = radius)
    val window: mutable.HashSet[(Int, Int)] = expand_window( mutable.HashSet[(Int, Int)]() ++= path, sizeTemplate, sizeData, radius)
    dtw(template, data, window.toArray)
  }

  def dtw( template: Array[Double], data: Array[Double]): (Double, List[(Int, Int)]) =  {
    val window = new ArrayBuffer[(Int, Int)]()

    for(i <- 0 until template.length; j <- 0 until data.length)  window.append((i,j))

    dtw(template, data, window.toArray)

  }

  def dtw( template: Array[Double], data: Array[Double], window: Array[(Int, Int)]): (Double, List[(Int, Int)]) = {

    //Dictionary of position -> (cumCost, prior pos x, prior pos y)
    val result = new mutable.HashMap[(Int, Int), (Double, Int, Int)]{
      override def default(key: (Int, Int))= (Double.PositiveInfinity, 0, 0 )
    }
    result.+=( (0,0) -> (0d, 0, 0) )
    for( (i,j) <- (window.sorted.map( (f: (Int, Int)) => (f._1 + 1, f._2 + 1))) ) {
      val dt = Math.abs(template(i-1) - data(j-1))
      result += ( (i,j) ->  {
        val temp = Array( (result( (i-1, j) )._1 + dt, i-1, j), (result( (i, j-1) )._1 + dt, i, j-1), (result( (i-1, j-1) )._1 + dt, i-1, j-1) )
        var tempReturn = temp(0)
        if( temp(1)._1 < tempReturn._1 ) tempReturn = temp(1)
        if( temp(2)._1 < tempReturn._1 ) tempReturn = temp(2)
        tempReturn
      } )
    }

    val path = new ArrayBuffer[(Int,Int)]()
    val totalCostMax: Double = result(template.length -1, data.length -1)._1
    var i = template.length
    var j = data.length
    while( i != 0 || j != 0 ){
      path.append( (i-1, j-1))
      val temp = result(i, j)
      i = temp._2
      j = temp._3

    }
    (totalCostMax, path.toList.reverse)
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

  def expand_window(path: mutable.HashSet[(Int,Int)], sizeTemplate: Int, sizeData: Int, radius: Int ): mutable.HashSet[(Int, Int)] = {

    val path_ : mutable.HashSet[(Int, Int)] = path.clone()
    for ((i, j) <- path; a <- -radius to radius; b <- -radius to radius) path_.+=((i + a, j + b))

    val window_ = new mutable.HashSet[(Int, Int)]()
    for ((i, j) <- path_) window_.++=(List((i * 2, j * 2), (i * 2, j * 2 + 1), (i * 2 + 1, j * 2), (i * 2 + 1, j * 2 + 1)))
    val window = new mutable.HashSet[(Int, Int)]()
    var start_j = 0
    for (i <- 0 until sizeTemplate) {
      var new_start_j: Int = -1 //valid values of new_start_j will always be >=0
      var j = start_j
      do {
        if (window_.contains((i, j))) {
          window.+=((i, j))
          if (new_start_j == -1) new_start_j = j
        }
        j += 1
      } while (j < sizeData)
      start_j = new_start_j
    }
    window
  }
}
