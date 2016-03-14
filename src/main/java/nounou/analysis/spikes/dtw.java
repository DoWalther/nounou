package nounou.analysis.spikes;

/**
 * Created by Dominik on 24.02.2016.
 */
public class dtw {


    public static double cost(double[] template, double[] data){
        /**
          * Calculate the difference between two sequences.*
          *
          * @return Total cost between input sequence and templae.
          */
        int sizeTemplate = template.length;
        int sizeData = data.length;
        double costMatrix [][] = new double[sizeTemplate][sizeData];
        double cumulativeCostMatrix [][] = new double[sizeTemplate][sizeData];

        // Calculate the costMatrix, each entry of the costMatrix is the absolute difference between template[i] and data[j]
        for(int i = 0; i < sizeTemplate; i++) {
            for (int j = 0; j < sizeData; j++) {
                costMatrix[i][j] = Math.abs(template[i] - data[j]);
            }
        }
        // Calculate the cumulativeCostMatrix from the upper left to the lower right.
        for(int i = 0; i < sizeTemplate; i++) {
            for (int j = 0; j < sizeData; j++) {
                if( i == 0 && j == 0 ){
                    cumulativeCostMatrix[i][j] = costMatrix[i][j];
                }
                else if( i == 0){
                    cumulativeCostMatrix[i][j] = costMatrix[i][j] + cumulativeCostMatrix[i][j-1];
                }
                else if( j == 0){
                    cumulativeCostMatrix[i][j] = costMatrix[i][j] + cumulativeCostMatrix[i-1][j];
                }
                else{
                    cumulativeCostMatrix[i][j] = costMatrix[i][j] + Math.min(cumulativeCostMatrix[i-1][j-1],Math.min(cumulativeCostMatrix[i-1][j], cumulativeCostMatrix[i][j-1]));
                }
            }
        }
        return cumulativeCostMatrix[sizeTemplate -1][sizeData - 1];
    }
}
