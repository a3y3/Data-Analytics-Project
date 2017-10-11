/**
 * Created by Soham on 11-Oct-17.
 */
public class ArrayAdd {
    public static void main(String args[]) {
        double dataArray[] = new double[10];
        double sum = 0.0;
        int ctr = 0;
        int tremors = 0;
        int quake_counter = 0;
        double threshold_value = 0.0;
        while (true) {
            for (int i = dataArray.length - 1; i >= 0; i--) {
                if (i == 0) dataArray[0] = 0;
                else dataArray[i] = dataArray[i - 1];
            }
            dataArray[0] = Math.random();
            for (double i : dataArray) {
                sum += i;
            }
            sum -= Math.floor(sum);
            System.out.println("Value recorded :" + sum);
            if (sum >= threshold_value)     //threshold value will come from net (?)
                tremors++;
            else {
                System.out.println();
                if (tremors >= 3) {
                    quake_counter ++;
                    System.out.println("Quake" + quake_counter + " has been recorded with " + tremors + "tremors");
                }
                tremors = 0;
            }

            sum = 0;
            ctr++;      //TODO: Remove in final implementation
            if (ctr >= 100) break;
        }
    }
}
