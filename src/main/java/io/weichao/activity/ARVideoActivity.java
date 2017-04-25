package io.weichao.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import cn.easyar.engine.EasyAR;
import io.weichao.library.R;
import io.weichao.model.ARVideoModel;

/**
 * Created by WEI CHAO on 2017/4/11.
 */

public class ARVideoActivity extends BaseFragmentActivity {
    /**
     * <uses-permission android:name="android.permission.INTERNET" />
     */

    // PackageName：Summary1704
    private static final String KEY = "dYoURxWsoFT4BoPSQWAVk9apIEIl7faJJVTTYWchPyHek03zYUlBVKQkK4vAtQM4zGjmPWQW6Cs3AWN36nEMJ9lbMOb79A3qX0I3cc4835734cfdfa3fc59356d6f8759815O5RFsNNSiEz12BmD4AeufSYl3U3MTsT25JIXjcCEtj3e8yMw2DfUxt6unv3HkAWZb9kp";

    static {
        System.loadLibrary("helloarvideo");
    }

    private ARVideoModel mARVideoModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.top_in, R.anim.bottom_out);

        RelativeLayout rootView = new RelativeLayout(this);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(rootView);

        EasyAR.initialize(this, KEY);
        nativeInit(); // KEY 和 PackageName 不对应会报错：EasyAR Initialize Fail: Invalid Key or Package Name

        mARVideoModel = new ARVideoModel(this);
        rootView.addView(mARVideoModel.view);

        nativeRotationChange(getWindowManager().getDefaultDisplay().getRotation() == android.view.Surface.ROTATION_0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EasyAR.onResume();
        if (mARVideoModel != null) {
            mARVideoModel.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (mARVideoModel != null) {
            mARVideoModel.onPause();
        }
        EasyAR.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mARVideoModel != null) {
            mARVideoModel.onDestroy();
        }
        nativeDestory();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        nativeRotationChange(getWindowManager().getDefaultDisplay().getRotation() == android.view.Surface.ROTATION_0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public void onFlingDown() {
        finish();
        overridePendingTransition(R.anim.bottom_in, R.anim.top_out);
    }

    private native boolean nativeInit();

    private native void nativeDestory();

    private native void nativeRotationChange(boolean portrait);

    public static native void nativeInitGL();

    public static native void nativeResizeGL(int w, int h);

    public static native void nativeRender();
}
