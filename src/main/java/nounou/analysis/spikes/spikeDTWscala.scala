package nounou.analysis.spikes

import breeze.linalg.{DenseMatrix, min}

/**
  * Created by Dominik on 29.02.2016.
  */
class spikeDTWScala {

  def spikeCost(template: Array[Double], data: Array[Double]): Double = {

    val sizeTemplate: Int = template.length
    val sizeData: Int = data.length
    val maxValuePositionTemplate: Int = template.indexOf(template.max)
    val maxValuePositionData: Int = data.indexOf(data.max)
    val costMatrix = DenseMatrix.zeros[Double](sizeTemplate, sizeData)
    val cumulativeCostMatrix = DenseMatrix.zeros[Double](sizeTemplate, sizeData)

    for (i <- 0 to maxValuePositionTemplate; j <- 0 to maxValuePositionData){
      costMatrix(i, j) = Math.abs(template(i) - data(j))
    }
    for (i <- maxValuePositionTemplate until sizeTemplate; j <- maxValuePositionData until sizeData){
      costMatrix(i, j) = Math.abs(template(i) - data(j))
    }

    for (i <- maxValuePositionTemplate + 1 to 0 by -1; j <- maxValuePositionData + 1 to 0 by -1){
      cumulativeCostMatrix(i, j) = (i,j) match{
        case(maxValuePositionTemplate, maxValuePositionData) => costMatrix(i,j)
        case(maxValuePositionTemplate, jx) => costMatrix(i,j) + cumulativeCostMatrix(i, j+1)
        case(ix,maxValuePositionData) => costMatrix(i,j) + cumulativeCostMatrix(i+1, j)
        case(ix, jx) => costMatrix(i,j) + min(cumulativeCostMatrix(i+1, j), cumulativeCostMatrix(i, j+1), cumulativeCostMatrix(i+1, j+1))
      }
    }
    for (i <- maxValuePositionTemplate until sizeTemplate; j <- maxValuePositionData until sizeData){
      cumulativeCostMatrix(i, j) = (i,j) match{
        case(maxValuePositionTemplate, maxValuePositionData) => costMatrix(i,j)
        case(maxValuePositionTemplate, jx) => costMatrix(i,j) + cumulativeCostMatrix(i, j-1)
        case(ix,maxValuePositionData) => costMatrix(i,j) + cumulativeCostMatrix(i-1, j)
        case(ix, jx) => costMatrix(i,j) + min(cumulativeCostMatrix(i-1, j), cumulativeCostMatrix(i, j-1), cumulativeCostMatrix(i-1, j-1))

      }

    }

    val totalCost: Double = cumulativeCostMatrix(0,0) + cumulativeCostMatrix(sizeTemplate -1, sizeData -1)
    return totalCost

  }

}
