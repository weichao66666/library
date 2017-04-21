package io.weichao.runnable;

import io.weichao.activity.BaseFragmentActivity;
import io.weichao.view.ZoomImageView;

/**
 * Created by WeiChao on 2016/6/16.
 */
public class ZoomImageViewAutoScaleRunnable implements Runnable {
    public long delayMillis = 10L;
    public float zoomOutVelocity = 1.07f;
    public float zoomInVelocity = 0.93f;

    // 缩放倍数
    public float mScale;
    // 缩放速率
    private float mScaleVelocity;
    private ZoomImageView mZoomImageView;
    private boolean mIsScaling;
    private boolean mIsZoomIn;

    public ZoomImageViewAutoScaleRunnable(ZoomImageView zoomImageView) {
        mZoomImageView = zoomImageView;
    }

    @Override
    public void run() {
        if (mIsScaling) {
            float currentScale = mZoomImageView.getScale();
            // 如果值在目标范围内，进行缩放
            if (((mScaleVelocity > 1f) && (currentScale < mScale)) || ((mScaleVelocity < 1f) && (currentScale > mScale))) {
                mZoomImageView.scaleMatrix.postScale(mScaleVelocity, mScaleVelocity, mZoomImageView.getWidth() >> 1, mZoomImageView.getHeight() >> 1);
                mZoomImageView.controlScaleCenter();
                mZoomImageView.setImageMatrix(mZoomImageView.scaleMatrix);
                //循环缩放
                BaseFragmentActivity.handler.postDelayed(this, delayMillis);
            } else
            // 结束缩放动画
            {
                mIsScaling = false;
                mIsZoomIn = !mIsZoomIn;
            }
        }
    }

    public void start() {
        if (!mIsScaling) {
            mIsScaling = true;
            //设置缩放速率
            mScaleVelocity = mIsZoomIn ? zoomInVelocity : zoomOutVelocity;
            //设置最大缩放倍数
            mScale = mIsZoomIn ? mZoomImageView.scaleInit : mZoomImageView.scaleMax;
            BaseFragmentActivity.handler.postDelayed(this, delayMillis);
        }
    }

    public boolean isRun() {
        return mIsScaling;
    }
}
