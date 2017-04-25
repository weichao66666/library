package io.weichao.model;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.text.DecimalFormat;

import io.weichao.activity.BaseFragmentActivity;
import io.weichao.domain.ARData;
import io.weichao.domain.LocalDataSource;
import io.weichao.domain.Marker;
import io.weichao.view.AugmentedView;

/**
 * Created by WEI CHAO on 2017/4/25.
 */
public class ARPOIModel extends ARPOISensorModel implements View.OnTouchListener {
    /**
     * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
     * <uses-permission android:name="android.permission.ACCESS_CORSE_LOCATION" />
     * <uses-permission android:name="android.permission.WAKE_LOCK" />
     */

    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");

    public RelativeLayout view;

    private final BaseFragmentActivity mActivity;

    protected static PowerManager.WakeLock wakeLock = null;
    protected static AugmentedView augmentedView = null;

    public ARPOIModel(BaseFragmentActivity activity) {
        super(activity);

        mActivity = activity;

        view = new RelativeLayout(activity);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        augmentedView = new AugmentedView(activity);
        augmentedView.setOnTouchListener(this);
        view.addView(augmentedView);

        PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "DimScreen");

        ARData.setRadius(11);
        ARData.setZoomLevel(FORMAT.format(11));
        ARData.setZoomProgress(50);
        LocalDataSource localData = new LocalDataSource(activity);
        ARData.addMarkers(localData.getMarkers());
    }

    @Override
    public void onResume() {
        wakeLock.acquire();
    }

    @Override
    public void onPause() {
        wakeLock.release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent evt) {
        super.onSensorChanged(evt);

        if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER ||
                evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            augmentedView.postInvalidate();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        for (Marker marker : ARData.getMarkers()) {
            if (marker.handleClick(event.getX(), event.getY())) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Toast t = Toast.makeText(mActivity, marker.getName(), Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
                }
                return true;
            }
        }
        return false;
    }
}
