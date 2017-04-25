package io.weichao.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

/**
 * Created by Administrator on 2016/11/1.
 */

public class LanguageUtil {
    private LanguageUtil() {
    }

    public static final String SIMPLIFIED_CHINESE = "zh";
    public static final String ENGLISH = "en";

    public static void changeLanguage(Context context, String language) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.locale = new Locale(language);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
