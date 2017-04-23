package io.weichao.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import io.weichao.model.NFTModel;

public class NFTActivity extends Activity {
//  <uses-permission android:name="android.permission.CAMERA" />
//	<uses-permission android:name="android.permission.INTERNET" />
//	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
//	<uses-feature android:name="android.hardware.camera.any" />
//	<uses-feature android:name="android.hardware.camera" android:required="false" />
//	<uses-feature android:name="android.hardware.camera.autofocus" android:required="false" />

    static {
        System.loadLibrary("nft");
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

    private RelativeLayout mRootView;

    private NFTModel mNFTModel;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
}