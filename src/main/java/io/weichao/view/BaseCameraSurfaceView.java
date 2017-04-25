package io.weichao.view;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import io.weichao.util.CameraUtil;

public class BaseCameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    protected Activity mActivity;
    protected Camera mCamera;

    public BaseCameraSurfaceView(Activity activity) {
        super(activity);

        mActivity = activity;

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        holder.setKeepScreenOn(true);
//        requestLayout();
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
//            try {
//                mCamera.setPreviewCallback(null);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
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
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //http://blog.csdn.net/libinfei8848/article/details/51375374 surfaceCreated不被调用
        if (mCamera == null) {
            try {
                // 开启后置摄像头
                mCamera = Camera.open(0);
                mCamera.setPreviewDisplay(holder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Parameters parameters = CameraUtil.getCameraParameters(mCamera, mActivity.getIntent(), width);
//        Parameters parameters = getCameraParameters(1920, 1080);
        try {
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        onDestroy();
        if (holder != null) {
            holder.removeCallback(this);
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
