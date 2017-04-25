package io.weichao.util;

import java.util.List;

/**
 * Created by WEI CHAO on 2016/12/8.
 */

public class ListUtil {
    private ListUtil() {
    }

    public static int getAverage(List<Integer> list) {
        if (list == null || list.size() <= 0) {
            return 0;
        }

        int temp = 0;
        for (int value : list) {
            temp += value;
        }
        return temp / list.size();
    }
}
