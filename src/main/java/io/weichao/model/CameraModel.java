package io.weichao.model;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import io.weichao.activity.BaseFragmentActivity;
import io.weichao.view.CameraPreview;

public class CameraModel extends BaseModel {
//    <uses-permission android:name="android.permission.CAMERA" />
//
//    <uses-feature android:name="android.hardware.camera" />
//    <uses-feature android:name="android.hardware.camera.autofocus" />
//    <uses-feature android:name="android.hardware.camera.any" />

    public RelativeLayout view;

    private Activity mActivity;
    private CameraPreview mPreview;

    public CameraModel(Activity activity) {
        mActivity = activity;

        view = new RelativeLayout(activity);
        mPreview = new CameraPreview(mActivity);
        view.addView(mPreview, new ViewGroup.LayoutParams(BaseFragmentActivity.width, BaseFragmentActivity.height));
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
}
