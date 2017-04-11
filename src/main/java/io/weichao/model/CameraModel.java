package io.weichao.model;

import android.Manifest;
import android.app.Activity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import io.weichao.activity.PermissionActivity;
import io.weichao.util.PermissionUtil;
import io.weichao.view.CameraPreview;

public class CameraModel extends BaseModel {
    public static final int ON_CREATE_REQUEST_CODE = 4;
    public static final int ON_RESUME_REQUEST_CODE = 5;

    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA
    };

    public RelativeLayout view;

    private Activity mActivity;
    private CameraPreview mPreview;

    private int height = 1080;
    private int width = 1920;

    public CameraModel(Activity activity) {
        mActivity = activity;
        view = new RelativeLayout(activity);

        if (PermissionUtil.isLackPermissions(activity, PERMISSIONS)) {
            PermissionActivity.startActivityForResult(activity, ON_CREATE_REQUEST_CODE, PERMISSIONS);
        } else {
            onCreateContinue();
        }
    }

    @Override
    public void onCreateContinue() {
        super.onCreateContinue();
        mPreview = new CameraPreview(mActivity);
        view.addView(mPreview, new ViewGroup.LayoutParams(width, height));

        mPreview.onResume();
    }

    @Override
    public void onResume() {
        if (PermissionUtil.isLackPermissions(mActivity, PERMISSIONS)) {
            PermissionActivity.startActivityForResult(mActivity, ON_RESUME_REQUEST_CODE, PERMISSIONS);
        } else {
            onResumeContinue();
        }
    }

    @Override
    public void onResumeContinue() {
        super.onResumeContinue();
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
            mPreview = null;
        }
        mActivity = null;
        view = null;
    }
}
