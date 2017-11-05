/**
 * Created by Soham on 11-Oct-17.
 */
public class QuakeDetector {
    //Neural net variables.
    int mDatasetsPerEpoch = 1;
    int mInputLayerNeurons = 100;
    int mHiddenFirstLayerNeurons = 60;
    int mHiddenSecondLayerNeurons = 30;
    int mOutputLayerNeurons = 1;

    double error;

    double mFirstLayerWeights[][] = new double[mInputLayerNeurons][mHiddenFirstLayerNeurons];
    double mFirstLayerSummation[][] = new double[mDatasetsPerEpoch][mHiddenFirstLayerNeurons];
    double mSecondLayerWeights[][] = new double[mHiddenFirstLayerNeurons][mHiddenSecondLayerNeurons];
    double mSecondLayerSummation[][] = new double[mDatasetsPerEpoch][mHiddenSecondLayerNeurons];
    double mOutputLayerWeights[][] = new double[mHiddenSecondLayerNeurons][mDatasetsPerEpoch];
    double mOutputLayerSummation[][] = new double[mDatasetsPerEpoch][mDatasetsPerEpoch];

    double dataArray[][] = new double[1][100];

    //TODO: Hard coded threshold value, neural network should return actual value in prod.
    double threshold_value = 0.5;

    public static void main(String args[]) {
        QuakeDetector quakeDetector = new QuakeDetector();
        quakeDetector.arrayAdd();
    }

    private void arrayAdd() {
        double sum = 0.0;
        int ctr = 0;                            //Amount of times the loop will run.
        int tremors = 0;                        //The number of tremors in one quake.
        int quake_counter = 0;                  //The number of quakes.

        initializeArray(dataArray, mDatasetsPerEpoch, mInputLayerNeurons);
        initializeArray(mFirstLayerWeights, mInputLayerNeurons, mHiddenFirstLayerNeurons);
        initializeArray(mSecondLayerWeights, mHiddenFirstLayerNeurons, mHiddenSecondLayerNeurons);
        initializeArray(mOutputLayerWeights, mHiddenSecondLayerNeurons, mDatasetsPerEpoch);

        trainNetwork();

        /*while (true) {
            for (int i = dataArray.length - 1; i >= 0; i--) {
                if (i == 0) dataArray[0][0] = 0;
                else dataArray[i] = dataArray[i - 1];
            }
            dataArray[0][0] = returnNextValueFromFile();
            for (double i[] : dataArray) {
                sum += i[0];
            }
            sum -= Math.floor(sum);                         //Average
            if (sum >= calculateDynamicThreshold(dataArray)) {     //threshold value will come from the net.
                System.out.println("Value recorded : " + sum);
                tremors++;
            }
            else {
                System.out.println();
                System.out.println("Value recorded : " + sum);
                System.out.println();
                if (tremors >= 3) {
                    quake_counter++;
                    System.out.println("Quake" + quake_counter + " has been recorded with " + tremors + "tremors");
                    System.out.println();
                }
                tremors = 0;
            }

            sum = 0.0;
            ctr++;                              //TODO: Remove in prod.
            if (ctr >= 100) break;
        }*/
    }

    private void initializeArray(double[][] array, int m, int n) {
        for (int i = 0; i < m; i++) {
            for(int j = 0;  j< n; j++) {
                array[i][j] = Math.random();
            }
        }
    }

    private void trainNetwork(){
        int yes = 1;
        int no = 0;
        mFirstLayerSummation = matrixMultiply(dataArray, mFirstLayerWeights, mDatasetsPerEpoch, mInputLayerNeurons, mInputLayerNeurons, mHiddenFirstLayerNeurons);
        mSecondLayerSummation = matrixMultiply(mFirstLayerSummation, mSecondLayerWeights, mDatasetsPerEpoch, mHiddenFirstLayerNeurons, mHiddenFirstLayerNeurons, mHiddenSecondLayerNeurons);
        mOutputLayerSummation = matrixMultiply(mSecondLayerSummation, mOutputLayerWeights, mDatasetsPerEpoch, mHiddenSecondLayerNeurons, mHiddenSecondLayerNeurons, mDatasetsPerEpoch);
        System.out.println("Threshold for this array is" + mOutputLayerSummation[0][0]);
        error = 1;
    }

    private double[][] matrixMultiply(double[][] first, double[][] second, int m, int n, int p, int q ){
        int c, d, k;
        double sum = 0.0;
        double[][] multiply = new double [m][q];
        for ( c = 0 ; c < m ; c++ )
        {
            for ( d = 0 ; d < q ; d++ )
            {
                for ( k = 0 ; k < p ; k++ )
                {
                    sum = sum + first[c][k]*second[k][d];
                }

                multiply[c][d] = activationFunction(sum);
                sum = 0;
            }
        }
        return multiply;
    }

    private double activationFunction(double v){
        return (1/(1+(Math.exp(-v))));
    }

    private double returnNextValueFromFile() {
        return Math.random();                   //TODO: Prod will return next value from data file
    }

    private double calculateDynamicThreshold(double[][] dataArray) {
        return threshold_value;
    }

    private int getDesiredOutput(double [][] dataArray){
        double sum = 0.0;
        for (double i[] : dataArray) {
            sum += i[0];
        }
        sum -= Math.floor(sum);
        if(sum >= 0.5) return 1;
        else return 0;
    }
}