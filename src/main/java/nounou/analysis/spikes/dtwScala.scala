package nounou.analysis.spikes

import breeze.linalg
import breeze.linalg.{min, DenseMatrix}

/**
  * Created by Dominik on 24.02.2016.
  */
object dtwScala {

  def cost(template: Array[Double], data: Array[Double]): Double = {
    /**
      * Calculate the difference between two sequences.*
      *
      * @return Total cost between template and data.
      */

    val sizeTemplate: Int = template.length
    val sizeData: Int = data.length
    val costMatrix = DenseMatrix.zeros[Double](sizeTemplate, sizeData)
    val cumulativeCostMatrix = DenseMatrix.zeros[Double](sizeTemplate, sizeData)

    // Calculate the costMatrix, each entry of the costMatrix is the absolute difference between template[i] and data[j]
    for (i <- 0 until sizeTemplate; j <- 0 until sizeData) {
      costMatrix(i, j) = Math.abs(template(i) - data(j))
    }

    // Calculate the cumulativeCostMatrix from the upper left to the lower right.
    for (i <- 0 until sizeTemplate; j <- 0 until sizeData) {
      cumulativeCostMatrix(i, j) = (i, j) match {
        case (0, 0) => costMatrix(i, j)
        case (0, jx) => costMatrix(i, j) + cumulativeCostMatrix(i, jx - 1)
        case (ix, 0) => costMatrix(i, j) + cumulativeCostMatrix(ix - 1, j)
        case _ => costMatrix(i, j) + min(cumulativeCostMatrix(i - 1, j - 1), cumulativeCostMatrix(i - 1, j), cumulativeCostMatrix(i, j - 1))
      }
    }

    cumulativeCostMatrix(sizeTemplate - 1, sizeData - 1)
  }

}