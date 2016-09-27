package nounou.analysis.spikes

import breeze.linalg.{DenseMatrix, min}

/**
  * Created by Dominik on 29.02.2016.
  */
class spikeDTWScala {

  def spikeCost(template: Array[Double], data: Array[Double]): Double = {
    /**
      * Calculate the total cost between two sequences. In contrast to normal DTW the cost matrix ist just partly
      * calculated. Normally the whole cost matrix is calculated from upper left to lower right. Now we determine
      * the position of the maximum value and calculate the cost matrix outgoing from this position to the upper left
      * and lower right.
      *
      * Best case: Maximum position is in the middle of the cost matrix
      * Worst case: Maximum position is located in a corner of the cost matrix
      *
      * @return Total cost between input sequence and template
      */

    val sizeTemplate: Int = template.length
    val sizeData: Int = data.length
    val maxValuePositionTemplate: Int = template.indexOf(template.max)
    val maxValuePositionData: Int = data.indexOf(data.max)
    val costMatrix = DenseMatrix.zeros[Double](sizeTemplate, sizeData)
    val cumulativeCostMatrix = DenseMatrix.zeros[Double](sizeTemplate, sizeData)

    // Calculate the costMatrix from upper left to the position of the maximum value.
    for (i <- 0 to maxValuePositionTemplate; j <- 0 to maxValuePositionData){
      costMatrix(i, j) = Math.abs(template(i) - data(j))
    }

    // Calculate the costMatrix from the position of the maximum values to the lower right.
    for (i <- maxValuePositionTemplate until sizeTemplate; j <- maxValuePositionData until sizeData){
      costMatrix(i, j) = Math.abs(template(i) - data(j))
    }

    //Calculate the cumulativeCostMatrix. Starting from the position of the maximum values  to the upper left
    for (i <- maxValuePositionTemplate + 1 to 0 by -1; j <- maxValuePositionData + 1 to 0 by -1){
      cumulativeCostMatrix(i, j) = (i,j) match{
        case(maxValuePositionTemplate, maxValuePositionData) => costMatrix(i,j)
        case(maxValuePositionTemplate, jx) => costMatrix(i,j) + cumulativeCostMatrix(i, j+1)
        case(ix,maxValuePositionData) => costMatrix(i,j) + cumulativeCostMatrix(i+1, j)
        case(ix, jx) => costMatrix(i,j) + min(cumulativeCostMatrix(i+1, j), cumulativeCostMatrix(i, j+1), cumulativeCostMatrix(i+1, j+1))
      }
    }

    //Calculate the cumulativeCostMatrix. Starting from the position of the maximum values  to the lower right.
    for (i <- maxValuePositionTemplate until sizeTemplate; j <- maxValuePositionData until sizeData){
      cumulativeCostMatrix(i, j) = (i,j) match{
        case(maxValuePositionTemplate, maxValuePositionData) => costMatrix(i,j)
        case(maxValuePositionTemplate, jx) => costMatrix(i,j) + cumulativeCostMatrix(i, j-1)
        case(ix,maxValuePositionData) => costMatrix(i,j) + cumulativeCostMatrix(i-1, j)
        case(ix, jx) => costMatrix(i,j) + min(cumulativeCostMatrix(i-1, j), cumulativeCostMatrix(i, j-1), cumulativeCostMatrix(i-1, j-1))

      }

    }

    // Total cost between input and template is the sum of the value in the upper left and lower right.
    val totalCost: Double = cumulativeCostMatrix(0,0) + cumulativeCostMatrix(sizeTemplate -1, sizeData -1)
    totalCost

  }

}
