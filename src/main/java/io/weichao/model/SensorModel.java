package io.weichao.model;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.RelativeLayout;

import io.weichao.listener.OnSensorChangedListener;

/**
 * Created by Administrator on 2016/11/10.
 */

public class SensorModel extends BaseModel implements SensorEventListener {
    public RelativeLayout view;

    private Context mContext;
    private SensorManager mSensorManager;
    private OnSensorChangedListener mOnSensorChangedListener;
    private float mOrientation;

    public SensorModel(Activity activity) {
        mContext = activity;

        mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        onResume();

    }

    @Override
    public void onResume() {
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }


    @Override
    public void onPause() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onDestroy() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
            mSensorManager = null;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            //方向传感器
            case Sensor.TYPE_ORIENTATION:
                //x表示手机指向的方位，0表示北,90表示东，180表示南，270表示西
                mOrientation = event.values[0];
                break;
            case Sensor.TYPE_ACCELEROMETER:
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
//                Log.d(ConstantUtil.TAG, "Orientation & Accelerometer:" + mOrientation + "," + x + "," + y + "," + z);
                if (mOnSensorChangedListener != null) {
                    mOnSensorChangedListener.onSensorChanged(mOrientation, x, y, z);
                }
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void setOnSensorChangedListener(OnSensorChangedListener onSensorChangedListener) {
        mOnSensorChangedListener = onSensorChangedListener;
    }
}
