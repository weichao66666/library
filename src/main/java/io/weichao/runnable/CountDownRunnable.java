package io.weichao.runnable;

import io.weichao.activity.BaseFragmentActivity;
import io.weichao.callback.GestureCallback;

/**
 * Created by WeiChao on 2016/6/17.
 */
public class CountDownRunnable implements Runnable {
    public GestureCallback callback;

    @Override
    public void run() {
        callback.onSingleTap();
    }

    public void countDown(long delayMillis) {
        BaseFragmentActivity.handler.postDelayed(this, delayMillis);
    }
}
