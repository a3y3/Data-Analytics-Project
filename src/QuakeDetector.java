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

    double mInputLayerWeights[][] = new double[mInputLayerNeurons][mHiddenFirstLayerNeurons];
    double mHiddenFirstLayerSummation[][] = new double[mDatasetsPerEpoch][mHiddenFirstLayerNeurons];
    double mHiddenSecondLayerWeights[][] = new double[mHiddenFirstLayerNeurons][mHiddenSecondLayerNeurons];
    double mHiddenSecondLayerSummation[][] = new double[mDatasetsPerEpoch][mHiddenSecondLayerNeurons];
    double mOutputLayerWeights[][] = new double[mDatasetsPerEpoch][mDatasetsPerEpoch];

    double dataArray[] = new double[100];

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

        initializeDataArray();

        while (true) {
            for (int i = dataArray.length - 1; i >= 0; i--) {
                if (i == 0) dataArray[0] = 0;
                else dataArray[i] = dataArray[i - 1];
            }
            dataArray[0] = returnNextValueFromFile();
            for (double i : dataArray) {
                sum += i;
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
        }
    }

    private void initializeDataArray() {
        for (int i = 0; i < dataArray.length; i++) {
            dataArray[i] = Math.random();
        }
    }

    private double returnNextValueFromFile() {
        return Math.random();                   //TODO: Prod will return next value from data file
    }

    private double calculateDynamicThreshold(double dataArray[]) {
        return threshold_value;
    }
}
