package io.weichao.util;

/**
 * Created by WEI CHAO on 2016/12/7.
 */

public class StringUtil {
    private StringUtil() {
    }

    public static String toString(int[] array) {
        return toString("(", ",", ")", array);
    }

    public static String toString(String prefixString, String splitString, String suffixString, int[] array) {
        String text = prefixString;
        if (array != null && array.length > 0) {
            int i = 0;
            for (; i < array.length - 1; i++) {
                text += array[i] + splitString;
            }
            text += array[i];
        }
        text += suffixString;

        return text;
    }

    public static String toString(float[] array) {
        return toString("(", ",", ")", array);
    }

    public static String toString(String prefixString, String splitString, String suffixString, float[] array) {
        String text = prefixString;
        if (array.length > 0) {
            int i = 0;
            for (; i < array.length - 1; i++) {
                text += array[i] + splitString;
            }
            text += array[i];
        }
        text += suffixString;

        return text;
    }
}
