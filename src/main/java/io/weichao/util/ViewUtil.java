package io.weichao.util;

import android.content.Context;
import android.view.View;

public class ViewUtil {
    private ViewUtil() {
    }

    public static int dp2px(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int px2dp(Context context, int px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public static int sp2px(Context context, float sp) {
        float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }

    public static int px2sp(Context context, float px) {
        float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (px / scale + 0.5f);
    }

    public static void toggleVisibility(View view) {
        view.setVisibility(view.isShown() ? View.INVISIBLE : View.VISIBLE);
    }
}
