package io.weichao.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import io.weichao.callback.GestureCallback;
import io.weichao.util.ConstantUtil;
import io.weichao.util.HardwareInfoUtil;
import io.weichao.util.PermissionUtil;

public class BaseFragmentActivity extends FragmentActivity implements GestureCallback, OnGestureListener, GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener {
    public static int width;
    public static int height;
    public static float density;
    public static GestureDetector gestureDetector;
    public static ScaleGestureDetector scaleGestureDetector;
    public static int scrollDistanceWidthLimit;
    public static int scrollDistanceHeightLimit;

    /**
     * 请求码
     */
    private static final int REQUEST_CODE = 0;
    /**
     * 所需的全部权限
     */
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // app崩溃后自动重启
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();

                Intent intent = new Intent(BaseFragmentActivity.this, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                BaseFragmentActivity.this.startActivity(intent);

                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });

        DisplayMetrics displayMetrics = HardwareInfoUtil.getDisplayMetrics(this);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        density = displayMetrics.density;

        scrollDistanceWidthLimit = (int) (width * ConstantUtil.ACTIVITY_SCROLL_DISTANCE_PERCENT);
        scrollDistanceHeightLimit = (int) (height * ConstantUtil.ACTIVITY_SCROLL_DISTANCE_PERCENT);

        gestureDetector = new GestureDetector(getApplicationContext(), this);
        scaleGestureDetector = new ScaleGestureDetector(getApplicationContext(), this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= 23 && PermissionUtil.isLackPermissions(this, PERMISSIONS)) {
            //　Android 版本为6.0+，并且缺少权限, 进入权限配置页面
            PermissionActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionActivity.PERMISSION_DENIED) {
            finish();
        }
    }

    //BaseGestureCallback

    @Override
    public void onFlingUp() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onFlingDown() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onFlingLeft() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onFlingRight() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDown() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onLongPress() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSingleTap() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDoubleTap() {
        // TODO Auto-generated method stub
    }

    //GestureDetector.OnGestureListener

    @Override
    public boolean onDown(MotionEvent e) {
        onDown();
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        onLongPress();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getX() - e2.getX() > scrollDistanceWidthLimit && Math.abs(velocityX) > ConstantUtil.GESTURE_FLING_VELOCITY_LIMIT) {
            onFlingLeft();
        } else if (e2.getX() - e1.getX() > scrollDistanceWidthLimit && Math.abs(velocityX) > ConstantUtil.GESTURE_FLING_VELOCITY_LIMIT) {
            onFlingRight();
        } else if (e1.getY() - e2.getY() > scrollDistanceHeightLimit && Math.abs(velocityY) > ConstantUtil.GESTURE_FLING_VELOCITY_LIMIT) {
            onFlingUp();
        } else if (e2.getY() - e1.getY() > scrollDistanceHeightLimit && Math.abs(velocityY) > ConstantUtil.GESTURE_FLING_VELOCITY_LIMIT) {
            onFlingDown();
        }
        return true;
    }

    //GestureDetector.OnDoubleTapListener

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        onSingleTap();
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        onDoubleTap();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    //ScaleGestureDetector.OnScaleGestureListener

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        // 一定要返回true才会进入onScale()
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        // TODO Auto-generated method stub
    }
}
