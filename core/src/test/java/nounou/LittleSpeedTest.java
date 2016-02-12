package nounou;

import java.util.Random;

/**
 * Created by ktakagaki on 15/11/13.
 */
public class LittleSpeedTest {

    public static void main(String[] args) {

        Random rand = new Random();

        int reps = 1000000000;
        int[] randInt = new int[100];
        for (int c = 0; c < 100; c++) randInt[c] = rand.nextInt(100000);
        double[] randDouble = new double[100];
        for (int c = 0; c < 100; c++) {
            randDouble[c] = rand.nextDouble() * java.lang.Math.pow(10, rand.nextDouble() * 5);
        }

        System.out.println("Int add subtract");
        int aInt = randInt[0];
        long ms = System.currentTimeMillis();
        for (int c = 0; c < reps; c++) {
            int mod = c % 95;
            aInt = randInt[ mod + 2];
            aInt = aInt + randInt[mod];
            aInt = aInt - randInt[mod + 1];
        }
        System.out.println(System.currentTimeMillis() - ms);
        System.out.println(aInt);
        System.out.println("===========");

        System.out.println("Int multiply divide");
        aInt = randInt[0];
        ms = System.currentTimeMillis();
        for (int c = 0; c < reps; c++) {
            int mod = c % 95;
            aInt = randInt[mod + 2];
            aInt = aInt * randInt[mod];
            aInt = aInt / randInt[mod + 1];
        }
        System.out.println(System.currentTimeMillis() - ms);
        System.out.println(aInt);
        System.out.println("===========");

        System.out.println("Double add subtract");
        double aDouble = randDouble[0];
        ms = System.currentTimeMillis();
        for (int c = 0; c < reps; c++) {
            int mod = c % 95;
            aDouble = randDouble[mod + 2];
            aDouble = aDouble + randDouble[mod];
            aDouble = aDouble - randDouble[mod + 1];
        }
        System.out.println(System.currentTimeMillis() - ms);
        System.out.println(aDouble);
        System.out.println("===========");

        System.out.println("Double multiply divide");
        aDouble = randDouble[0];
        ms = System.currentTimeMillis();
        for (int c = 0; c < reps; c++) {
            int mod = c % 95;
            aDouble = randDouble[mod + 2];
            aDouble = aDouble * randDouble[mod];
            aDouble = aDouble / randDouble[mod + 1];
        }
        System.out.println(System.currentTimeMillis() - ms);
        System.out.println(aDouble);
        System.out.println("===========");


    }

}
