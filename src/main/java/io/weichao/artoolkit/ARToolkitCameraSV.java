package io.weichao.artoolkit;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import io.weichao.activity.NFTActivity;
import io.weichao.util.CameraUtil;
import io.weichao.view.BaseCameraSurfaceView;

public class ARToolkitCameraSV extends BaseCameraSurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {
    public ARToolkitCameraSV(Activity activity) {
        super(activity);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);

        mCamera.setPreviewCallbackWithBuffer(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        int capWidth = 640;
        int capHeight = 480;
        boolean isFrontFace = CameraUtil.isFrontFace(0);

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(capWidth, capHeight);
        mCamera.setParameters(parameters);

        int pixelformat = parameters.getPreviewFormat();
        PixelFormat pixelinfo = new PixelFormat();
        PixelFormat.getPixelFormatInfo(pixelformat, pixelinfo);
        int bufSize = capWidth * capHeight * pixelinfo.bitsPerPixel / 8;
        for (int i = 0; i < 5; i++) {
            mCamera.addCallbackBuffer(new byte[bufSize]);
        }

        mCamera.startPreview();

        // TODO 大分辨率预览不识别（超过 640*480 可能不行）
        NFTActivity.nativeVideoInit(capWidth, capHeight, 0, isFrontFace);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            mCamera.setPreviewCallback(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.surfaceDestroyed(holder);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        NFTActivity.nativeVideoFrame(data);
        camera.addCallbackBuffer(data);
    }
}
