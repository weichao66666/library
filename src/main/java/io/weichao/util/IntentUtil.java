package io.weichao.util;

import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.text.TextUtils;

public class IntentUtil {
    private IntentUtil() {
    }

    public static String getStringExtra(Intent intent, String name, String defaultValue) {
        String string = intent.getStringExtra(name);
        if (TextUtils.isEmpty(string)) {
            string = defaultValue;
        }
        return string;
    }

    public static int[] getIntArrayExtra(Intent intent, String name, int[] defaultIntArray) {
        int[] array = intent.getIntArrayExtra(name);
        if (array == null) {
            array = defaultIntArray;
        }
        return array;
    }

    public static CompressFormat getSerializableExtra(Intent intent, String name, CompressFormat defaultCompressFormat) {
        CompressFormat compressFormat = (CompressFormat) intent.getSerializableExtra(name);
        if (compressFormat == null) {
            compressFormat = defaultCompressFormat;
        }
        return compressFormat;
    }
}
