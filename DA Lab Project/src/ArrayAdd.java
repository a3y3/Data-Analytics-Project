/**
 * Created by Soham on 11-Oct-17.
 */
public class ArrayAdd {
    public static void main(String args[]) {
        double dataArray[] = new double[10];
        double sum = 0.0;
        int ctr =0;
        while (true) {
            for(int i = dataArray.length -1 ; i >= 0; i--){
                    if(i == 0) dataArray[0] = 0;
                    else dataArray[i] = dataArray[i -1];
                }
            dataArray[0] = Math.random();
            for (double i : dataArray) {
                sum += i;
            }
            sum -= Math.floor(sum);
            System.out.println(sum);
            sum = 0;
            ctr++;
            if(ctr>=100) break;
        }
    }
}
