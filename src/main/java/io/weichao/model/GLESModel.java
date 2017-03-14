package io.weichao.model;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import io.weichao.listener.OnSensorChangedListener;
import io.weichao.util.ConstantUtil;
import io.weichao.util.GLES30Util;
import io.weichao.widget.GLES30CompassSV;
import io.weichao.widget.GLES30RouteSV;

public class GLESModel extends BaseModel implements OnSensorChangedListener {
    public RelativeLayout view;

    private GLES30CompassSV mGLES30CompassSV;
    private GLES30RouteSV mGLES30RouteSV;

    @Override
    public void onResume() {
        if (mGLES30CompassSV != null) {
            mGLES30CompassSV.onResume();
        }
        if (mGLES30RouteSV != null) {
            mGLES30RouteSV.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mGLES30CompassSV != null) {
            mGLES30CompassSV.onPause();
        }
        if (mGLES30RouteSV != null) {
            mGLES30RouteSV.onPause();
        }
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
    }

    public GLESModel(Activity activity) {
        view = new RelativeLayout(activity);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        if (GLES30Util.detectOpenGLES30(activity)) {
//            mGLES30CompassSV = new GLES30CompassSV(activity);
            mGLES30RouteSV = new GLES30RouteSV(activity);
        } else {
            Log.e(ConstantUtil.TAG, "OpenGL ES 3.0 not supported on device.  Exiting...");
        }
//        view.addView(mGLES30CompassSV);
        view.addView(mGLES30RouteSV);
    }

    @Override
    public void onSensorChanged(float orientation, float x, float y, float z) {
//        mGLES30CompassSV.onSensorChanged(orientation, x, y, z);
        mGLES30RouteSV.onSensorChanged(orientation, x, y, z);
    }
}
