package io.weichao.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import io.weichao.library.R;
import io.weichao.model.NFTModel;

public class NFTActivity extends BaseFragmentActivity {
//  <uses-permission android:name="android.permission.CAMERA" />
//	<uses-permission android:name="android.permission.INTERNET" />
//	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
//	<uses-feature android:name="android.hardware.camera.any" />
//	<uses-feature android:name="android.hardware.camera" android:required="false" />
//	<uses-feature android:name="android.hardware.camera.autofocus" android:required="false" />

    static {
        System.loadLibrary("nft");
    }

    private RelativeLayout mRootView;

    private NFTModel mNFTModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.top_in, R.anim.bottom_out);

        updateNativeDisplayParameters();

        mRootView = new RelativeLayout(this);
        mRootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(mRootView);

        nativeCreate(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        nativeStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        nativeSetInternetState(isConnected ? 1 : 0);

        mNFTModel = new NFTModel(this);
        mRootView.setBackgroundColor(Color.BLACK);
        mRootView.addView(mNFTModel.view);
    }

    @Override
    public void onPause() {
        if (mNFTModel != null) {
            mRootView.removeView(mNFTModel.view);
        }

        super.onPause();
    }

    @Override
    public void onStop() {
        nativeStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        nativeDestroy();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateNativeDisplayParameters();
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

    private void updateNativeDisplayParameters() {
        Display d = getWindowManager().getDefaultDisplay();
        int orientation = d.getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        d.getMetrics(dm);
        int w = dm.widthPixels;
        int h = dm.heightPixels;
        int dpi = dm.densityDpi;
        nativeDisplayParametersChanged(orientation, w, h, dpi);
    }

    // Lifecycle functions.
    public static native boolean nativeCreate(Context ctx);

    public static native boolean nativeStart();

    public static native boolean nativeStop();

    public static native boolean nativeDestroy();

    // Camera functions.
    public static native boolean nativeVideoInit(int w, int h, int cameraIndex, boolean cameraIsFrontFacing);

    public static native void nativeVideoFrame(byte[] image);

    // OpenGL functions.
    public static native void nativeSurfaceCreated();

    public static native void nativeSurfaceChanged(int w, int h);

    public static native void nativeDrawFrame();

    // Other functions.
    public static native void nativeDisplayParametersChanged(int orientation, int w, int h, int dpi); // 0 = portrait, 1 = landscape (device rotated 90 degrees ccw), 2 = portrait upside down, 3 = landscape reverse (device rotated 90 degrees cw).

    public static native void nativeSetInternetState(int state);
}