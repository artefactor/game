package com.game;

import static com.game.sets.JsonMapper.getOrCreateFileJson;
import static com.game.sets.JsonMapper.writeToFile;

import java.util.Arrays;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

public class GreenRandomGenerator {

    public static void main(String[] args) {
        int count = 100;
        //        int shufflesCount = 10000;
        int shufflesCount = 100;
        Random random = new Random();

        double sumTotal = 0;
        int[][] shuffles = new int[shufflesCount][count];
        for (int a = 0; a < shufflesCount; a++) {
            int[] ints = new int[count];
            double sum = 0;
            for (int i = 0; i < ints.length; i++) {
                int value = (int) (1.0 + random.nextDouble() * 6);
                if (value == 0) {
                    value = 1;
                }
                if (value > 6) {
                    value = 6;
                }
                ints[i] = value;
                sum += value;
            }
            shuffles[a] = ints;
            double avg = sum / count;
            sumTotal += avg;
            System.out.println(avg
                + ": " + ArrayUtils.toString(ints)
            );
        }
        System.out.println("TOTAL AVG: " + (sumTotal / shufflesCount));

                        String jsonFileName = "green_event_shuffles.json";
//        String jsonFileName = "green_shuffles_event_small.json";
        writeToFile(getOrCreateFileJson(jsonFileName), Arrays.deepToString(shuffles));
    }
}
