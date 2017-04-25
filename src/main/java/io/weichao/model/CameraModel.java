package io.weichao.model;

import android.support.v4.app.FragmentActivity;
import android.widget.RelativeLayout;

import io.weichao.view.BaseCameraSurfaceView;

public class CameraModel extends BaseModel {
//    private static final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA};

//    <uses-permission android:name="android.permission.CAMERA" />
//
//    <uses-feature android:name="android.hardware.camera" />
//    <uses-feature android:name="android.hardware.camera.autofocus" />
//    <uses-feature android:name="android.hardware.camera.any" />


    public RelativeLayout view;

    private FragmentActivity mActivity;
    private BaseCameraSurfaceView mSurfaceView;

    public CameraModel(FragmentActivity activity) {
        mActivity = activity;

        view = new RelativeLayout(activity);

        mSurfaceView = new BaseCameraSurfaceView(activity);
        view.addView(mSurfaceView);
    }

    @Override
    public void onResume() {
        if (mSurfaceView != null) {
            mSurfaceView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mSurfaceView != null) {
            mSurfaceView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        if (mSurfaceView != null) {
            mSurfaceView.onDestroy();
        }
    }

    public void show() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(layoutParams);
    }

    public void disappear() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(1, 1);
        view.setLayoutParams(layoutParams);
    }
}
