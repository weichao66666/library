package io.weichao.util;

import java.util.Map;

/**
 * Created by WEI CHAO on 2016/12/9.
 */

public class MathUtil {
    public static int getVariance(int[] ints1, int[] ints2) {
        if (ints1 == null || ints2 == null || ints1.length != ints2.length) {
            return -1;
        }

        int variance = 0;
        for (int i = 0; i < ints1.length; i++) {
            int sub = ints1[i] - ints2[i];
            variance += sub * sub;
        }

        return variance;
    }

    public static float getDeviation(int[] ints) {
        if (ints == null || ints.length == 0) {
            return -1;
        }

        int average = getAverage(ints);
        return getDeviation(ints, average);
    }

    public static float getDeviation(int[] ints, int average) {
        if (ints == null || ints.length == 0) {
            return -1;
        }

        int sum = 0;
        for (int i : ints) {
            int sub = i - average;
            sum += sub * sub;
        }

        return (float) Math.sqrt(sum * 1.0f / ints.length);
    }

    public static float getDeviation(Map<Integer, Integer> map, int average) {
        if (map == null || map.size() == 0) {
            return -1;
        }

        int sum = 0;
        int count = 0;
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            int key = entry.getKey();
            int value = entry.getValue();
            count += value;
            int sub = key - average;
            sum += sub * sub * value;
        }

        return (float) Math.sqrt(sum * 1.0f / count);
    }

    public static float getDeviation(Map<Integer, Integer> map, int average, int exceptKey) {
        if (map == null || map.size() == 0) {
            return -1;
        }

        int sum = 0;
        int count = 0;
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            int key = entry.getKey();
            int value = entry.getValue();
            if (key != exceptKey) {
                count += value;
                int sub = key - average;
                sum += sub * sub * value;
            }
        }

        return (float) Math.sqrt(sum * 1.0f / count);
    }

    public static int getAverage(int[] ints) {
        if (ints == null || ints.length == 0) {
            return Integer.MIN_VALUE;
        }

        int sum = 0;
        for (int i : ints) {
            sum += i;
        }

        return Math.round(sum * 1.0f / ints.length);
    }
}
