package io.weichao.listener;

import android.os.SystemClock;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;

import io.weichao.activity.BaseFragmentActivity;
import io.weichao.callback.GestureCallback;
import io.weichao.util.ConstantUtil;

public class BaseOnTouchListener implements OnTouchListener {
    public GestureCallback callback;

    private int mScrollDistanceWidthLimit = (int) (BaseFragmentActivity.width * ConstantUtil.ACTIVITY_SCROLL_DISTANCE_PERCENT);
    private int mScrollDistanceHeightLimit = (int) (BaseFragmentActivity.height * ConstantUtil.ACTIVITY_SCROLL_DISTANCE_PERCENT);
    private int mDownX;
    private int mDownY;
    private long mDownTime;
    private int mOffsetX;
    private int mOffsetY;

    //速度+位移 判断是否滑动的阈值来自ViewPager

    /**
     * ID of the active pointer. This is used to retain consistency during
     * drags/flings if multiple pointers are used.
     */
    private int mActivePointerId = INVALID_POINTER;
    /**
     * Sentinel value for no current active pointer.
     * Used by {@link #mActivePointerId}.
     */
    private static final int INVALID_POINTER = -1;

    /**
     * Determines speed during touch scrolling
     */
    private VelocityTracker mVelocityTracker;
    private static final int MIN_DISTANCE_FOR_FLING = 25; // dips
    private static final int MIN_FLING_VELOCITY = 400; // dips
    private int mFlingDistanceX = (int) (MIN_DISTANCE_FOR_FLING * BaseFragmentActivity.density);
    private int mFlingDistanceY = (int) (mFlingDistanceX * 9 * 1.0F / 16);
    private int mMinimumVelocity = (int) (MIN_FLING_VELOCITY * BaseFragmentActivity.density);

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                callback.onDown();
                mDownX = (int) event.getX();
                mDownY = (int) event.getY();
                mOffsetX = 0;
                mOffsetY = 0;
                mDownTime = SystemClock.elapsedRealtime();
                mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                break;
            case MotionEvent.ACTION_MOVE:
                int currentX = (int) event.getX();
                int currentY = (int) event.getY();
                int offset = Math.abs(currentX - mDownX);
                if (offset > mOffsetX) {
                    mOffsetX = offset;
                }
                offset = Math.abs(currentY - mDownY);
                if (offset > mOffsetY) {
                    mOffsetY = offset;
                }
                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int initialVelocity = (int) VelocityTrackerCompat.getXVelocity(velocityTracker, mActivePointerId);
                int upX = (int) event.getX();
                int upY = (int) event.getY();
                // 左滑
                if (mDownX - upX > mFlingDistanceX && (-initialVelocity > mMinimumVelocity || mDownX - upX > mScrollDistanceWidthLimit)) {
                    callback.onFlingLeft();
                } else
                    // 右滑
                    if (upX - mDownX > mFlingDistanceX && (initialVelocity > mMinimumVelocity || upX - mDownX > mScrollDistanceWidthLimit)) {
                        callback.onFlingRight();
                    } else
                        // 上滑
                        if (mDownY - upY > mFlingDistanceY && (-initialVelocity > mMinimumVelocity || mDownY - upY > mScrollDistanceHeightLimit)) {
                            callback.onFlingUp();
                        } else
                            // 下滑
                            if (upY - mDownY > mFlingDistanceY && (initialVelocity > mMinimumVelocity || upY - mDownY > (mScrollDistanceHeightLimit << 1))) {
                                callback.onFlingDown();
                            } else
                                // 单击
                                if (SystemClock.elapsedRealtime() - mDownTime < ConstantUtil.SINGLE_TAP_TIMEOUT && mOffsetX < (mScrollDistanceWidthLimit >> 4) && mOffsetY < (mScrollDistanceHeightLimit >> 4)) {
                                    callback.onSingleTap();
                                }
                break;
            default:
                break;
        }

        return false;
    }
}
