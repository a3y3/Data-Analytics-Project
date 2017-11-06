import java.io.*;

/**
 * Created by Soham on 11-Oct-17.
 */
public class QuakeDetector {
    //Neural net variables.
    int mDatasetsPerEpoch = 1;
    int mInputLayerNeurons = 100;
    int mHiddenFirstLayerNeurons = 60;
    int mHiddenSecondLayerNeurons = 30;
    double learningRate = 1.5;

    double error;

    double mFirstLayerWeights[][] = new double[mInputLayerNeurons][mHiddenFirstLayerNeurons];
    double mFirstLayerSummation[][] = new double[mDatasetsPerEpoch][mHiddenFirstLayerNeurons];
    double mSecondLayerWeights[][] = new double[mHiddenFirstLayerNeurons][mHiddenSecondLayerNeurons];
    double mSecondLayerSummation[][] = new double[mDatasetsPerEpoch][mHiddenSecondLayerNeurons];
    double mOutputLayerWeights[][] = new double[mHiddenSecondLayerNeurons][mDatasetsPerEpoch];
    double mOutputLayerSummation[][] = new double[mDatasetsPerEpoch][mDatasetsPerEpoch];

    double deltaSecondLayer[] = new double[mHiddenSecondLayerNeurons];
    double deltaFirstLayer[] = new double[mHiddenFirstLayerNeurons];

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

        //initializeArrayWithZeroes(dataArray, mDatasetsPerEpoch, mInputLayerNeurons);


        int trainCounterInner = 0;
        int trainCounterOuter = 0;
        while (trainCounterOuter != 70) {
            while (trainCounterInner != 60000) {
                trainNetwork();
                trainCounterInner++;
                System.out.print("train counter inner:"+trainCounterInner);
                System.out.print(" train counter outer:"+trainCounterOuter+" ");
            }
            trainCounterOuter++;
            trainCounterInner = 0;
            initializeArray(dataArray, mDatasetsPerEpoch, mInputLayerNeurons);
        }
        checkOutput();
        checkOutput();
        checkOutput();
        checkOutput();
        checkOutput();
        checkOutput();
        checkOutput();
        initializeArrayWithZeroes(dataArray, mDatasetsPerEpoch, mInputLayerNeurons);
        trainNetwork();
        checkOutput();
        checkOutput();
        checkOutput();
        checkOutput();
        checkOutput();
        checkOutput();
        checkOutput();
        try {
            ObjectOutputStream out1 = new ObjectOutputStream(
                    new FileOutputStream("D:\\firstLayerWeights.ser")
            );
            ObjectOutputStream out2 = new ObjectOutputStream(
                    new FileOutputStream("D:\\secondLayerWeights.ser")
            );
            ObjectOutputStream out3 = new ObjectOutputStream(
                    new FileOutputStream("D:\\outputLayerWeights.ser")
            );
            out1.writeObject(mFirstLayerWeights);
            out2.writeObject(mSecondLayerWeights);
            out3.writeObject(mOutputLayerWeights);
            out1.flush();
            out1.close();
            out2.flush();
            out2.close();
            out3.flush();
            out3.close();
        }
        catch (IOException e){
            System.out.println("IOException:"+e);
        }

        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("D:\\dataArray.ser"));
            double[][] array = (double[][]) in.readObject();
            in.close();
            //dataArray = array;
        }
        catch (Exception e){
            System.out.println("IOException:"+e);
        }

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
            for (int j = 0; j < n; j++) {
                array[i][j] = Math.random();
            }
        }
    }

    private void initializeArrayWithZeroes(double[][] array, int m, int n) {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                array[i][j] = 0;
            }
        }
    }

    private void trainNetwork() {
        mFirstLayerSummation = matrixMultiply(dataArray, mFirstLayerWeights, mDatasetsPerEpoch, mInputLayerNeurons, mInputLayerNeurons, mHiddenFirstLayerNeurons);
        mSecondLayerSummation = matrixMultiply(mFirstLayerSummation, mSecondLayerWeights, mDatasetsPerEpoch, mHiddenFirstLayerNeurons, mHiddenFirstLayerNeurons, mHiddenSecondLayerNeurons);
        mOutputLayerSummation = matrixMultiply(mSecondLayerSummation, mOutputLayerWeights, mDatasetsPerEpoch, mHiddenSecondLayerNeurons, mHiddenSecondLayerNeurons, mDatasetsPerEpoch);
        double desiredOutput = getDesiredOutput(dataArray);
        error = desiredOutput - mOutputLayerSummation[0][0];
        System.out.println("y is" + mOutputLayerSummation[0][0] + ", d is " + desiredOutput);

        //Start back propagate. Output neuron is only one and hence doesn't need any separate function.
        double delta = error * (mOutputLayerSummation[0][0]) * (1 - mOutputLayerSummation[0][0]);
        for (int i = 0; i < mHiddenSecondLayerNeurons; i++) {
            mOutputLayerWeights[i][0] += learningRate * delta * mSecondLayerSummation[0][i];
        }

        //Calculate delta for second hidden layer (the one with 30 neurons)
        for (int i = 0; i < mHiddenSecondLayerNeurons; i++) {
            deltaSecondLayer[i] = mSecondLayerSummation[0][i] * (1 - mSecondLayerSummation[0][i])
                    * mOutputLayerWeights[i][0]
                    * delta;
        }

        //Update weights for second layer
        for (int i = 0; i < mHiddenSecondLayerNeurons; i++) {
            for (int j = 0; j < mHiddenFirstLayerNeurons; j++) {
                mSecondLayerWeights[j][i] += learningRate * deltaSecondLayer[i] * mFirstLayerSummation[0][j];
            }
        }

        //Calculate delta for the first hidden layer (the one with 60 neurons)
        for (int z = 0; z < mHiddenFirstLayerNeurons; z++) {
            deltaFirstLayer[z] = mFirstLayerSummation[0][z] * (1 - mFirstLayerSummation[0][z]);
            double weightDeltaSum = 0.0;
            for (int i = 0; i < mHiddenFirstLayerNeurons; i++) {
                for (int j = 0; j < mHiddenSecondLayerNeurons; j++) {
                    weightDeltaSum += deltaSecondLayer[j] * mSecondLayerWeights[i][j];
                }
            }
            deltaFirstLayer[z] *= weightDeltaSum;
        }

        //Update weights for the first layer
        for (int i = 0; i < mHiddenFirstLayerNeurons; i++) {
            for (int j = 0; j < mInputLayerNeurons; j++) {
                mFirstLayerWeights[j][i] += learningRate * deltaFirstLayer[i] * dataArray[0][j];
            }
        }

    }

    private double[][] matrixMultiply(double[][] first, double[][] second, int m, int n, int p, int q) {
        int c, d, k;
        double sum = 0.0;
        double[][] multiply = new double[m][q];
        for (c = 0; c < m; c++) {
            for (d = 0; d < q; d++) {
                for (k = 0; k < p; k++) {
                    sum = sum + first[c][k] * second[k][d];
                }

                multiply[c][d] = activationFunction(sum);
                sum = 0;
            }
        }
        return multiply;
    }

    private double activationFunction(double v) {
        return (1 / (1 + (Math.exp(-v))));
    }

    private double returnNextValueFromFile() {
        return Math.random();                   //TODO: Prod will return next value from data file
    }

    private double calculateDynamicThreshold(double[][] dataArray) {
        return threshold_value;
    }

    private int getDesiredOutput(double[][] dataArray) {
        double arraySum = 0.0;
        for (int i = 0; i < mInputLayerNeurons; i++) {
            arraySum += dataArray[0][i];
        }
        arraySum -= Math.floor(arraySum);
        if (arraySum >= 0.5) return 1;
        else return 0;
    }

    private void checkOutput() {
        System.out.println("Checking output...");
        initializeArray(dataArray, mDatasetsPerEpoch, mInputLayerNeurons);
        trainNetwork();
    }
}