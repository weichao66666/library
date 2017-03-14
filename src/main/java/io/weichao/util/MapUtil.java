package io.weichao.util;

import java.util.Map;

/**
 * Created by WEI CHAO on 2016/12/7.
 */
public class MapUtil {
    public static int getIntKeyWithMaxValue(Map<Integer, Integer> map) {
        int key = -1, value = Integer.MIN_VALUE;

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() > value) {
                key = entry.getKey();
                value = entry.getValue();
            }
        }

        return key;
    }

    public static int getIntKeyWithMaxValue(Map<Integer, Integer> map, Integer exceptKey) {
        int key = -1, value = Integer.MIN_VALUE;

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() > value && !entry.getKey().equals(exceptKey)) {
                key = entry.getKey();
                value = entry.getValue();
            }
        }

        return key;
    }

    public static String getStringKeyWithMaxValue(Map<String, Integer> map) {
        String key = "";
        int value = Integer.MIN_VALUE;

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() > value) {
                key = entry.getKey();
                value = entry.getValue();
            }
        }

        return key;
    }

    public static String getStringKeyWithMinValue(Map<String, Integer> map) {
        String key = "";
        int value = Integer.MAX_VALUE;

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() < value) {
                key = entry.getKey();
                value = entry.getValue();
            }
        }

        return key;
    }

    public static Map.Entry<String, Integer> getEntryWithMaxValue(Map<String, Integer> map) {
        Map.Entry<String, Integer> rtnEntry = null;
        int value = Integer.MIN_VALUE;

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() > value) {
                rtnEntry = entry;
                value = entry.getValue();
            }
        }

        return rtnEntry;
    }

    public static Map.Entry<String, Integer> getEntryWithMinValue(Map<String, Integer> map) {
        Map.Entry<String, Integer> rtnEntry = null;
        int value = Integer.MAX_VALUE;

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() < value) {
                rtnEntry = entry;
                value = entry.getValue();
            }
        }

        return rtnEntry;
    }

    public static int getAverage(Map<Integer, Integer> map) {
        int sum = 0;
        int count = 0;

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            int key = entry.getKey();
            int value = entry.getValue();
            count += value;
            sum += key * value;
        }

        if (count == 0) {
            return Integer.MIN_VALUE;
        }

        return Math.round(sum * 1.0f / count);
    }

    public static int getAverage(Map<Integer, Integer> map, Integer exceptKey) {
        int sum = 0;
        int count = 0;

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            int key = entry.getKey();
            int value = entry.getValue();
            if (key != exceptKey) {
                count += value;
                sum += key * value;
            }
        }

        if (count == 0) {
            return Integer.MIN_VALUE;
        }

        return Math.round(sum * 1.0f / count);
    }
}
