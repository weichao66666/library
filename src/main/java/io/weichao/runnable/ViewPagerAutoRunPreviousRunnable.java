package io.weichao.runnable;

import android.support.v4.view.ViewPager;

import io.weichao.activity.BaseFragmentActivity;

public class ViewPagerAutoRunPreviousRunnable implements Runnable {
    public long delayMillis = 4000L;

    private ViewPager mViewPager;
    private boolean mIsChanging;

    public ViewPagerAutoRunPreviousRunnable(ViewPager viewPager) {
        mViewPager = viewPager;
    }

    @Override
    public void run() {
        if (mIsChanging) {
            BaseFragmentActivity.handler.removeCallbacks(this);
            int currentItem = mViewPager.getCurrentItem();
            currentItem--;
            mViewPager.setCurrentItem(currentItem);
            BaseFragmentActivity.handler.postDelayed(this, delayMillis);
        }
    }

    public void stop() {
        mIsChanging = false;
        BaseFragmentActivity.handler.removeCallbacks(this);
    }

    public void start() {
        if (!mIsChanging) {
            mIsChanging = true;
            BaseFragmentActivity.handler.postDelayed(this, delayMillis);
        }
    }

    public boolean isRun() {
        return mIsChanging;
    }
}
