package io.weichao.view;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

import io.weichao.activity.BaseFragmentActivity;
import io.weichao.util.IntentUtil;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private Activity mActivity;

    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;

    public CameraPreview(Activity activity) {
        super(activity);
        mActivity = activity;

        initSurfaceHolder();
        requestLayout();
    }

    public void onResume() {
        if (mCamera != null) {
            try {
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onPause() {
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onDestroy() {
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
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
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //http://blog.csdn.net/libinfei8848/article/details/51375374 surfaceCreated不被调用
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
                mCamera.setPreviewDisplay(mSurfaceHolder);
                setCameraParams(1920, 1080);
                mCamera.startPreview();
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

    private void setCameraParams() {
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
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        if (supportedPreviewSizes.size() > 0) {
            int[] previewSizeArray = IntentUtil.getIntArrayExtra(mActivity.getIntent(), "ParametersPreviewSize", new int[]{BaseFragmentActivity.width, BaseFragmentActivity.height});
            Camera.Size size = mCamera.new Size(previewSizeArray[0], previewSizeArray[1]);
            if (!supportedPreviewSizes.contains(size)) {
                size = supportedPreviewSizes.get(0);
            }
            parameters.setPreviewSize(size.width, size.height);
        }
        // 设置图片大小
        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
        if (supportedPictureSizes.size() > 0) {
            int[] pictureSizeArray = IntentUtil.getIntArrayExtra(mActivity.getIntent(), "ParametersPictureSize", new int[]{BaseFragmentActivity.width, BaseFragmentActivity.height});
            Camera.Size size = mCamera.new Size(pictureSizeArray[0], pictureSizeArray[1]);
            if (!supportedPictureSizes.contains(size)) {
                size = supportedPictureSizes.get(0);
            }
            parameters.setPictureSize(size.width, size.height);
        }
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
    }

    private void setCameraParams(int width, int height) {
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
//        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
//        if (supportedPreviewSizes.size() > 0) {
//            int[] previewSizeArray = IntentUtil.getIntArrayExtra(mActivity.getIntent(), "ParametersPreviewSize", new int[]{BaseFragmentActivity.width, BaseFragmentActivity.height});
//            Camera.Size size = mCamera.new Size(previewSizeArray[0], previewSizeArray[1]);
//            if (!supportedPreviewSizes.contains(size)) {
//                size = supportedPreviewSizes.get(0);
//            }
//            parameters.setPreviewSize(size.width, size.height);
//        }
        // TODO 有虚拟按键影响，暂不处理，强制指定。
        parameters.setPreviewSize(width, height);
        // 设置图片大小
//        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
//        if (supportedPictureSizes.size() > 0) {
//            int[] pictureSizeArray = IntentUtil.getIntArrayExtra(mActivity.getIntent(), "ParametersPictureSize", new int[]{BaseFragmentActivity.width, BaseFragmentActivity.height});
//            Camera.Size size = mCamera.new Size(pictureSizeArray[0], pictureSizeArray[1]);
//            if (!supportedPictureSizes.contains(size)) {
//                size = supportedPictureSizes.get(0);
//            }
//            parameters.setPictureSize(size.width, size.height);
//        }
        // TODO 有虚拟按键影响，暂不处理，强制指定。
        parameters.setPictureSize(width, height);
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
    }

    /*private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
        }
    };

    private ShutterCallback mShutterCallback = new ShutterCallback() {
        @Override
        public void onShutter() {
        }
    };

    private PictureCallback mRawCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
        }
    };

    private PictureCallback mPostViewCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
        }
    };

    private PictureCallback mJpegCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
        }
    };*/
}
