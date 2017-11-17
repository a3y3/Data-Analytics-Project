package lab_4;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class QuakeDetector {
	
	public static void main(String [] args) throws Exception
	{
		Configuration c=new Configuration();
		String[] files=new GenericOptionsParser(c,args).getRemainingArgs();
		Path input=new Path(files[0]);
		Path output=new Path(files[1]);
		Job j=new Job(c,"QuakeDetector");
		j.setJarByClass(QuakeDetector.class);
		j.setMapperClass(MapForWordCount.class);
		j.setReducerClass(ReduceForWordCount.class);
		j.setOutputKeyClass(Text.class);
		j.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(j, input);
		FileOutputFormat.setOutputPath(j, output);
		System.exit(j.waitForCompletion(true)?0:1);
	}

	public static class MapForWordCount extends Mapper<LongWritable, Text,
	Text, IntWritable>{
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
	    double threshold_value = 0.5;
	    public void map(LongWritable key, Text value, Context con) throws
		IOException, InterruptedException
		{
		double sum = 0.0;
	        int ctr = 0;                            //Amount of times the loop will run.
	        int tremors = 0;                        //The number of tremors in one quake.
	        int quake_counter = 0;                  //The number of quakes.

	        String number = value.toString();
	        String[] numbers_string = number.split(",");
	        double numbers[] = new double[numbers_string.length];
	        int number_counter = 0;
	        for(String x:numbers_string){
	        	numbers[number_counter++] = Double.parseDouble(x);
	        }
	        number_counter = 0;

	        initializeArray(dataArray, mDatasetsPerEpoch, mInputLayerNeurons);
	         try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("D:\\weights_backup\\firstLayerWeights.ser"));
            double[][] array = (double[][]) in.readObject();
            mFirstLayerWeights = array;
            in.close();
            ObjectInputStream in2 = new ObjectInputStream(new FileInputStream("D:\\weights_backup\\secondLayerWeights.ser"));
            double[][] array2 = (double[][]) in2.readObject();
            mSecondLayerWeights = array2;
            in.close();
            ObjectInputStream in3 = new ObjectInputStream(new FileInputStream("D:\\weights_backup\\outputLayerWeights.ser"));
            double[][] array3 = (double[][]) in3.readObject();
            mOutputLayerWeights = array3;
            in.close();
            //dataArray = array;
        }
        catch (Exception e){
            System.out.println("IOException:"+e);
        }
	        //initializeArray(mFirstLayerWeights, mInputLayerNeurons, mHiddenFirstLayerNeurons);
	        //initializeArray(mSecondLayerWeights, mHiddenFirstLayerNeurons, mHiddenSecondLayerNeurons);
	        //initializeArray(mOutputLayerWeights, mHiddenSecondLayerNeurons, mDatasetsPerEpoch);
	        //int trainCounterInner = 0;
	        //int trainCounterOuter = 0;
	        /*while (trainCounterOuter != 70) {
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
	        }*/

	        /*try {
	            ObjectInputStream in = new ObjectInputStream(new FileInputStream("D:\\dataArray.ser"));
	            double[][] array = (double[][]) in.readObject();
	            in.close();
	            //dataArray = array;
	        }
	        catch (Exception e){
	            System.out.println("IOException:"+e);
	        }*/
	        
	        //================LOAD WEIGHTS HERE=====================
	        
	        /*try {
	            ObjectInputStream in = new ObjectInputStream(new FileInputStream("D:\\weights_backup\\firstLayerWeights.ser"));
	            double[][] array = (double[][]) in.readObject();
	            mFirstLayerWeights = array;
	            in.close();
	            ObjectInputStream in2 = new ObjectInputStream(new FileInputStream("D:\\weights_backup\\secondLayerWeights.ser"));
	            double[][] array2 = (double[][]) in2.readObject();
	            mSecondLayerWeights = array2;
	            in.close();
	            ObjectInputStream in3 = new ObjectInputStream(new FileInputStream("D:\\weights_backup\\outputLayerWeights.ser"));
	            double[][] array3 = (double[][]) in3.readObject();
	            mOutputLayerWeights = array3;
	            in.close();
	            //dataArray = array;
	        }
	        catch (Exception e){
	            System.out.println("IOException:"+e);
	        } */
	        while (true) {
	            for (int i = mInputLayerNeurons - 1; i >= 0; i--) {
	                if (i == 0) dataArray[0][0] = 0;
	                else dataArray[0][i] = dataArray[0][i - 1];
	            }
	            if(number_counter == numbers.length -1) number_counter = 0;
	            dataArray[0][0] = numbers[number_counter++];
	            for (int i = 0 ; i < mInputLayerNeurons; i++) {
	                sum += dataArray[0][i];
	            }
	            sum -= Math.floor(sum);                         //Average
	            if (sum >= calculateDynamicThreshold(dataArray)) {     //threshold value will come from the net.
	                System.out.println("Value recorded : " + sum);
	                tremors++;
	               	Text outputKey = new Text("Quake"+quake_counter);
	               	IntWritable outputValue = new IntWritable(1);
	               	con.write(outputKey, outputValue);
	            }
	            else {
	                System.out.println();
	                System.out.println("Value recorded : " + sum);
	                System.out.println();
	                if (tremors >= 3) {
	                    quake_counter++;
	                    //System.out.println("Quake" + quake_counter + " has been recorded with " + tremors + "tremors");
	                    //System.out.println();
	                }
	                tremors = 0;
	            }

	            sum = 0.0;
	            ctr++;                              
	            if (ctr >= 100) break;
	        }
	}
	    
	private void initializeArray(double[][] array, int m, int n) {
		for (int i = 0; i < m; i++) {
		    for (int j = 0; j < n; j++) {
		        array[i][j] = Math.random();
		    }
		}
	}
	
    private double calculateDynamicThreshold(double [][] array){
    	return forwardPropogate();
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
    private double forwardPropogate(){
    	mFirstLayerSummation = matrixMultiply(dataArray, mFirstLayerWeights, mDatasetsPerEpoch, mInputLayerNeurons, mInputLayerNeurons, mHiddenFirstLayerNeurons);
        mSecondLayerSummation = matrixMultiply(mFirstLayerSummation, mSecondLayerWeights, mDatasetsPerEpoch, mHiddenFirstLayerNeurons, mHiddenFirstLayerNeurons, mHiddenSecondLayerNeurons);
        mOutputLayerSummation = matrixMultiply(mSecondLayerSummation, mOutputLayerWeights, mDatasetsPerEpoch, mHiddenSecondLayerNeurons, mHiddenSecondLayerNeurons, mDatasetsPerEpoch); mOutputLayerSummation[0][0] = (Math.random() > 0.5? mOutputLayerSummation[0][0] - 0.0113234: mOutputLayerSummation[0][0] - 0.313234);
        double desiredOutput = getDesiredOutput(dataArray);
        error = desiredOutput - mOutputLayerSummation[0][0];
        return mOutputLayerSummation[0][0];
    	
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
    
    private int getDesiredOutput(double[][] dataArray) {
        double arraySum = 0.0;
        for (int i = 0; i < mInputLayerNeurons; i++) {
            arraySum += dataArray[0][i];
        }
        arraySum -= Math.floor(arraySum);
        if (arraySum >= 0.5) return 1;
        else return 0;
    }
}
public static class ReduceForWordCount extends Reducer<Text,
	IntWritable, Text, IntWritable>
	{
		public void reduce(Text word, Iterable<IntWritable> values, Context
		con) throws IOException, InterruptedException
		{
			int sum = 0;
			for(IntWritable value : values)
			{
				sum += value.get();
			}
			con.write(word, new IntWritable(sum));
		}
	}
}
