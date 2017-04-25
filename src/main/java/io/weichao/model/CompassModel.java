package io.weichao.model;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import io.weichao.bean.SensorDataBean;
import io.weichao.util.ConstantUtil;
import io.weichao.util.GLES30Util;
import io.weichao.opengl_sv.GLES30CompassSV;

public class CompassModel extends BaseModel {
    public RelativeLayout view;

    private GLES30CompassSV mGLES30CompassSV;

    public CompassModel(Activity activity) {
        view = new RelativeLayout(activity);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        if (GLES30Util.detectOpenGLES30(activity)) {
            mGLES30CompassSV = new GLES30CompassSV(activity);
        } else {
            Log.e(ConstantUtil.TAG, "OpenGL ES 3.0 not supported on device.  Exiting...");
        }
        view.addView(mGLES30CompassSV);
    }

    @Override
    public void onResume() {
        if (mGLES30CompassSV != null) {
            mGLES30CompassSV.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mGLES30CompassSV != null) {
            mGLES30CompassSV.onPause();
        }
    }

    public void setSensorData(SensorDataBean sensorDataBean) {
        mGLES30CompassSV.updateAngle(sensorDataBean);
    }
}
