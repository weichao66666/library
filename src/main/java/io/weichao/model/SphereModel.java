package io.weichao.model;

import android.app.Activity;
import android.widget.RelativeLayout;

public class SphereModel extends BaseModel {
    public RelativeLayout view;

//    private GLES30SphereSV mGLES30SphereSV;

    public SphereModel(Activity activity) {
        view = new RelativeLayout(activity);
//        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//
//        if (GLES30Util.detectOpenGLES30(activity)) {
//            mGLES30SphereSV = new GLES30SphereSV(activity);
//        } else {
//            Log.e(ConstantUtil.TAG, "OpenGL ES 3.0 not supported on device.  Exiting...");
//        }
//        view.addView(mGLES30SphereSV);
    }

    @Override
    public void onPause() {
//        if (mGLES30SphereSV != null) {
//            mGLES30SphereSV.onPause();
//        }
    }

    @Override
    public void onResume() {
//        if (mGLES30SphereSV != null) {
//            mGLES30SphereSV.onResume();
//        }
    }
}
