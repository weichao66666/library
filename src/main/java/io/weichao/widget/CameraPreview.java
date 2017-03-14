package io.weichao.widget;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

import io.weichao.callback.LifeCycleCallback;
import io.weichao.util.IntentUtil;

public class CameraPreview extends SurfaceView implements LifeCycleCallback, SurfaceHolder.Callback {
    private Activity mActivity;
    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;

    private int height = 1080;
    private int width = 1920;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onRestart() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onCreateContinue() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStartContinue() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onRestartContinue() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onResumeContinue() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onResume() {
        if (mCamera != null) {
            try {
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCamera = null;
        }
        if (mSurfaceHolder != null) {
            mSurfaceHolder.removeCallback(this);
            mSurfaceHolder = null;
        }
        mActivity = null;
    }

    public CameraPreview(Activity activity) {
        super(activity);
        mActivity = activity;

        initSurfaceHolder();
        requestLayout();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //http://blog.csdn.net/libinfei8848/article/details/51375374 surfaceCreated不被调用
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
                mCamera.setPreviewDisplay(mSurfaceHolder);
                setCameraParamsAndStartPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mSurfaceHolder != null) {
            mSurfaceHolder.removeCallback(this);
            mSurfaceHolder = null;
        }
    }

    private void initSurfaceHolder() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setKeepScreenOn(true);
    }

    private void setCameraParamsAndStartPreview() {
        Parameters parameters = mCamera.getParameters();
        // 设置聚焦模式
        List<String> supportedFocusModes = parameters.getSupportedFocusModes();
        if (supportedFocusModes.size() > 0) {
            String focusMode = IntentUtil.getStringExtra(mActivity.getIntent(), "ParametersFocusMode", Parameters.FOCUS_MODE_AUTO);
            if (!supportedFocusModes.contains(focusMode)) {
                focusMode = supportedFocusModes.get(0);
            }
            parameters.setFocusMode(focusMode);
        }
        // 设置预览大小
        int[] previewSizeArray = IntentUtil.getIntArrayExtra(mActivity.getIntent(), "ParametersPreviewSize", new int[]{width, height});
        parameters.setPreviewSize(previewSizeArray[0], previewSizeArray[1]);
        // 设置图片大小
        int[] pictureSizeArray = IntentUtil.getIntArrayExtra(mActivity.getIntent(), "ParametersPictureSize", new int[]{width, height});
        parameters.setPictureSize(pictureSizeArray[0], pictureSizeArray[1]);
        // 设置图片保存格式
        List<Integer> supportedPictureFormatLists = parameters.getSupportedPictureFormats();
        if (supportedPictureFormatLists.size() > 0) {
            int pictureFormat = mActivity.getIntent().getIntExtra("ParametersPictureFormat", ImageFormat.NV21);
            if (!supportedPictureFormatLists.contains(pictureFormat)) {
                pictureFormat = supportedPictureFormatLists.get(0);
            }
            parameters.setPictureFormat(pictureFormat);
        }
        // 设置图片保存质量
        parameters.setJpegQuality(mActivity.getIntent().getIntExtra("ParametersJpegQuality", 100));
        try {
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }
}
