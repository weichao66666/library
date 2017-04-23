package io.weichao.model;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import io.weichao.activity.NFTActivity;
import io.weichao.view.CameraSurface;

public class NFTModel extends BaseModel {
    private final Activity mActivity;

    public RelativeLayout view;

    private CameraSurface mCameraSurface;
    private GLSurfaceView mGLSurfaceView;

    public NFTModel(Activity activity) {
        mActivity = activity;

        view = new RelativeLayout(activity);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mCameraSurface = new CameraSurface(activity);
        view.addView(mCameraSurface);

        mGLSurfaceView = new GLSurfaceView(activity);
        //mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Do we actually need a transparent surface? I think not, (default is RGB888 with depth=16) and anyway, Android 2.2 barfs on this.
        mGLSurfaceView.setRenderer(new SceneRenderer());
        mGLSurfaceView.setZOrderMediaOverlay(true); // Request that GL view's SurfaceView be on top of other SurfaceViews (including CameraPreview's SurfaceView).
        view.addView(mGLSurfaceView);
    }

//    @Override
//    public void onResume() {
//        if (mGLSurfaceView != null) {
//            mGLSurfaceView.onResume();
//        }
//    }
//
//    @Override
//    public void onPause() {
//        if (mGLSurfaceView != null) {
//            mGLSurfaceView.onPause();
//        }
//    }

    private class SceneRenderer implements GLSurfaceView.Renderer {
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            NFTActivity.nativeSurfaceCreated();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int w, int h) {
            NFTActivity.nativeSurfaceChanged(w, h);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            NFTActivity.nativeDrawFrame();
        }
    }
}
