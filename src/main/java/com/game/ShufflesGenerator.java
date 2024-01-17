package com.game;

import static com.game.sets.JsonMapper.getOrCreateFileJson;
import static com.game.sets.JsonMapper.writeToFile;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

public class ShufflesGenerator {

    public static void main(String[] args) {
        int count = 100;
//        int count = 38;
        int[] ints = new int[count];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = i;
        }

//        int shufflesCount = 10000;
        int shufflesCount = 100000;
//        int shufflesCount = 100;
//        int shufflesCount = 5;
        int[][] shuffles = new int[shufflesCount][count];
        for (int a = 0; a < shufflesCount; a++) {
            ArrayUtils.shuffle(ints);
            shuffles[a] = Arrays.copyOf(ints, count);
            System.out.println(ArrayUtils.toString(ints));
        }

//        String jsonFileName = "blue_shuffles.json";
        String jsonFileName = "blue_shuffles_large.json";
//        String jsonFileName = "blue_shuffles_medium.json";
//        String jsonFileName = "green_shuffles.json";
        writeToFile(getOrCreateFileJson(jsonFileName), Arrays.deepToString(shuffles));
    }
}
