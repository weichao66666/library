package io.weichao.model;

import android.support.v4.app.FragmentActivity;
import android.widget.RelativeLayout;

import io.weichao.view.CameraPreview;

public class CameraModel extends BaseModel {
//    private static final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA};

//    <uses-permission android:name="android.permission.CAMERA" />
//
//    <uses-feature android:name="android.hardware.camera" />
//    <uses-feature android:name="android.hardware.camera.autofocus" />
//    <uses-feature android:name="android.hardware.camera.any" />


    public RelativeLayout view;

    private FragmentActivity mActivity;
    private CameraPreview mPreview;

    public CameraModel(FragmentActivity activity) {
        mActivity = activity;

        view = new RelativeLayout(activity);

        mPreview = new CameraPreview(activity);
        view.addView(mPreview);
    }

    @Override
    public void onResume() {
        if (mPreview != null) {
            mPreview.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mPreview != null) {
            mPreview.onPause();
        }
    }

    @Override
    public void onDestroy() {
        if (mPreview != null) {
            mPreview.onDestroy();
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
