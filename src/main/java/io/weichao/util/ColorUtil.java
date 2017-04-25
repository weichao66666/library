package io.weichao.util;

import java.util.Random;

/**
 * Created by WEI CHAO on 2017/4/25.
 */
public class ColorUtil {
    private ColorUtil() {
    }

    public static String getRandomColor() {
        int sum = 1;

        for (int i = 0; i < 4; i++) {
            sum *= 255;
            sum += new Random().nextInt(255);
        }

        return "#" + Integer.toHexString(sum);
    }
}
