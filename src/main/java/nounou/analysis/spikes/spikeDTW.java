package nounou.analysis.spikes;

/**
 * Created by Dominik on 25.02.2016.
 */
public class spikeDTW {
    public static double spikeCost(double[] template, double[] data) {
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


        int sizeTemplate = template.length;
        int sizeData = data.length;
        int maxValuePositionTemplate = maxIndex(template);
        int maxValuePositionData = maxIndex(data);
        double costMatrix[][] = new double[sizeTemplate][sizeData];
        double cumulativeCostMatrix[][] = new double[sizeTemplate][sizeData];

        // Calculate the costMatrix from upper left to the position of the maximum value.
        for (int i = 0; i < maxValuePositionTemplate + 1; i++) {
            for (int j = 0; j < maxValuePositionData + 1; j++) {
                costMatrix[i][j] = Math.abs(template[i] - data[j]);
            }
        }
        // Calculate the costMatrix from the position of the maximum values to the lower right.
        for (int i = maxValuePositionTemplate + 1; i < sizeTemplate; i++) {
            for (int j = maxValuePositionData + 1; j < sizeData; j++) {
                costMatrix[i][j] = Math.abs(template[i] - data[j]);
            }

        }

        //Calculate the cumulativeCostMatrix. Starting from the position of the maximum values  to the upper left.
        for (int i = maxValuePositionTemplate; i > -1; i--) {
            for (int j = maxValuePositionData; j > -1; j--) {
                if (i == maxValuePositionTemplate && j == maxValuePositionData) {
                    cumulativeCostMatrix[i][j] = costMatrix[i][j];
                } else if (i == maxValuePositionTemplate) {
                    cumulativeCostMatrix[i][j] = costMatrix[i][j] + cumulativeCostMatrix[i][j + 1];
                } else if (j == maxValuePositionData) {
                    cumulativeCostMatrix[i][j] = costMatrix[i][j] + cumulativeCostMatrix[i + 1][j];
                } else {
                    cumulativeCostMatrix[i][j] = costMatrix[i][j] + Math.min(Math.min(cumulativeCostMatrix[i + 1][j], cumulativeCostMatrix[i][j + 1]),
                                                                             cumulativeCostMatrix[i + 1][j + 1]);
                }
            }
        }
        //Calculate the cumulativeCostMatrix. Starting from the position of the maximum values  to the lower right.
        for (int i = maxValuePositionTemplate; i < sizeTemplate; i++) {
            for (int j = maxValuePositionData; j < sizeData; j++) {
                if (i == maxValuePositionTemplate && j == maxValuePositionData) {
                    cumulativeCostMatrix[i][j] = costMatrix[i][j];
                } else if (i == maxValuePositionTemplate) {
                    cumulativeCostMatrix[i][j] = costMatrix[i][j] + cumulativeCostMatrix[i][j - 1];
                } else if (j == maxValuePositionData) {
                    cumulativeCostMatrix[i][j] = costMatrix[i][j] + cumulativeCostMatrix[i - 1][j];
                } else {
                    cumulativeCostMatrix[i][j] = costMatrix[i][j] + Math.min(Math.min(cumulativeCostMatrix[i - 1][j], cumulativeCostMatrix[i][j - 1]),
                                                                             cumulativeCostMatrix[i - 1][j - 1]);
                }
            }

        }
        // Total cost between input and template is the sum of the value in the upper left and lower right.
        double totalCost = cumulativeCostMatrix[0][0] + cumulativeCostMatrix[sizeTemplate - 1][sizeData - 1];

        return totalCost;
    }

    public static int maxIndex(double[] template){
        /** Return the position of the maximum value in an array
         */

        int maximumIndex = 0;
        for(int i = 0; i < template.length; i++){
            double newNumber = template[i];
            if ((newNumber > template[maximumIndex])){
                maximumIndex = i;
            }
        }
        return maximumIndex;
    }

}
