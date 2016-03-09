package nounou.analysis.spikes;

/**
 * Created by Dominik on 25.02.2016.
 */
public class spikeDTW {
    public static double spikeCost(double[] template, double[] data) {

        int sizeTemplate = template.length;
        int sizeData = data.length;
        int maxValuePositionTemplate = maxIndex(template);
        int maxValuePositionData = maxIndex(data);
        double costMatrix[][] = new double[sizeTemplate][sizeData];
        double cumulativeCostMatrix[][] = new double[sizeTemplate][sizeData];

        for (int i = 0; i < maxValuePositionTemplate + 1; i++) {
            for (int j = 0; j < maxValuePositionData + 1; j++) {
                costMatrix[i][j] = Math.abs(template[i] - data[j]);
            }
        }

        for (int i = maxValuePositionTemplate + 1; i < sizeTemplate; i++) {
            for (int j = maxValuePositionData + 1; j < sizeData; j++) {
                costMatrix[i][j] = Math.abs(template[i] - data[j]);
            }

        }

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
        double totalCost = cumulativeCostMatrix[0][0] + cumulativeCostMatrix[sizeTemplate - 1][sizeData - 1];

        return totalCost;
    }

    public static int maxIndex(double[] template){
        /** Return the position of the maximum value in an array */

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
