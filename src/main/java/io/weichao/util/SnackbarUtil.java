package io.weichao.util;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by WeiChao on 2016/6/23.
 */
public class SnackbarUtil {
    public static void showText(View view, String message, int duration) {
        Snackbar.make(view, message, duration).show();
    }

    public static void showText(View view, int messageId, int duration) {
        Snackbar.make(view, messageId, duration).show();
    }
}
