package nounou.analysis.spikes;

/**
 * Created by Dominik on 24.02.2016.
 */
public class dtw {


    public static double cost(double[] template, double[] data){
        int sizeTemplate = template.length;
        int sizeData = data.length;
        double costMatrix [][] = new double[sizeTemplate][sizeData];
        double cumulativeCostMatrix [][] = new double[sizeTemplate][sizeData];

        for(int i = 0; i < sizeTemplate; i++) {
            for (int j = 0; j < sizeData; j++) {
                costMatrix[i][j] = Math.abs(template[i] - data[j]);
            }
        }

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
    /*public static void main(String[] args){
        double[] test03 = {0d, 0d, 0d, 0d};
        double[] test04 = {4d, -19d, 5d, -9d};
        double totalCost;
        totalCost = cost(test03, test04);
        System.out.println(totalCost);
    }*/



}
