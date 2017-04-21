package io.weichao.model;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import io.weichao.adapter.ViewPagerAdapter;
import io.weichao.callback.GestureCallback;
import io.weichao.library.R;
import io.weichao.listener.BaseOnTouchListener;
import io.weichao.pagetransformer.ViewPagerPageTransformer;
import io.weichao.runnable.ViewPagerAutoRunLatterRunnable;
import io.weichao.runnable.ViewPagerAutoRunPreviousRunnable;
import io.weichao.scroller.ViewPagerScroller;
import io.weichao.util.ViewPagerUtil;

abstract public class BaseViewPagerModel extends BaseModel {
    public RelativeLayout view;
    public GestureCallback activityCallback;
    public FragmentActivity mActivity;

    protected CharSequence mId;
    protected ViewPager mViewPager;
    protected ViewPagerAdapter mAdapter;
    protected ViewPagerAutoRunPreviousRunnable mAutoRunPreviousRunnable;
    protected ViewPagerAutoRunLatterRunnable mAutoRunLatterRunnable;
    protected boolean mAutoRunPreviousRunnableIsRun;
    protected boolean mAutoRunLatterRunnableIsRun;

    public BaseViewPagerModel(FragmentActivity activity, CharSequence id) {
        mActivity = activity;
        mId = id;

        view = new RelativeLayout(activity);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mViewPager = (ViewPager) View.inflate(activity, R.layout.viewpager, null);
        // 设置适配器
        mAdapter = new ViewPagerAdapter(activity.getSupportFragmentManager(), mViewPager);
        setViewPagerDatas(mAdapter);
        mViewPager.setAdapter(mAdapter);
        // 设置当前位置（使第一个页面可往前滑，显示最后一个页面）
        mViewPager.setCurrentItem(mAdapter.fragmentLists.size() * 1000);
        // 设置滑动时动画效果
        ViewPagerPageTransformer pageTransformer = new ViewPagerPageTransformer();
        pageTransformer.backgroundId = R.id.iv_background;
        pageTransformer.frontImageId = R.id.iv_front_image;
        pageTransformer.textId = R.id.tv_text;
        mViewPager.setPageTransformer(false, pageTransformer);
        // 设置定时自动滑动
        mAutoRunPreviousRunnable = new ViewPagerAutoRunPreviousRunnable(mViewPager);
        mAutoRunLatterRunnable = new ViewPagerAutoRunLatterRunnable(mViewPager);
        // 设置自动滑动时动画执行总时间
        ViewPagerUtil.setScroller(mViewPager, new ViewPagerScroller(mViewPager.getContext(), new LinearInterpolator()));
        // 设置触摸监听
        BaseOnTouchListener onTouchListener = new BaseOnTouchListener();
        onTouchListener.callback = this;
        mViewPager.setOnTouchListener(onTouchListener);

        view.addView(mViewPager);
    }

    @Override
    public void onFlingDown() {
        if (activityCallback != null) {
            activityCallback.onFlingDown();
        }
    }

    @Override
    public void onFlingLeft() {
        // 往相反方向滑不会启动自动左滑
        if (!mAutoRunPreviousRunnableIsRun) {
            mAutoRunLatterRunnable.start();
        }
    }

    @Override
    public void onFlingRight() {
        // 往相反方向滑不会启动自动右滑
        if (!mAutoRunLatterRunnableIsRun) {
            mAutoRunPreviousRunnable.start();
        }
    }

    @Override
    public void onDown() {
        // 按下即停止自动滑动
        stopAutoRun();
    }

    @Override
    public void onSingleTap() {
        if (mAutoRunPreviousRunnableIsRun || mAutoRunLatterRunnableIsRun) {
            return;
        }

        // TODO
    }

    private void stopAutoRun() {
        mAutoRunPreviousRunnableIsRun = mAutoRunPreviousRunnable.isRun();
        mAutoRunLatterRunnableIsRun = mAutoRunLatterRunnable.isRun();
        mAutoRunPreviousRunnable.stop();
        mAutoRunLatterRunnable.stop();
    }

    abstract public void setViewPagerDatas(ViewPagerAdapter adapter);
}
