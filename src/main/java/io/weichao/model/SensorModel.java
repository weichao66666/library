package io.weichao.model;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import io.weichao.bean.SensorDataBean;
import io.weichao.util.ConstantUtil;

/**
 * Created by Administrator on 2016/11/10.
 */

public class SensorModel extends BaseModel implements SensorEventListener {
    private Activity mActivity;

    private SensorManager mSensorManager;

    private float mOrientation;
    private float mAx;
    private float mAy;
    private float mAz;
//    private float mMx;
//    private float mMy;
//    private float mMz;

    public SensorModel(Activity activity) {
        mActivity = activity;

        mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void onResume() {
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
//        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
//        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
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
            //加速度传感器
            case Sensor.TYPE_ACCELEROMETER:
                mAx = event.values[0];
                mAy = event.values[1];
                mAz = event.values[2];
                break;
            //磁力传感器
//            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
//                mMx = event.values[0];
//                mMy = event.values[1];
//                mMz = event.values[2];
//                break;
            default:
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public SensorDataBean getSensorData() {
        Log.d(ConstantUtil.TAG, "getSensorData--mOrientation:" + mOrientation);
        Log.d(ConstantUtil.TAG, "getSensorData--mAx:" + mAx);
        Log.d(ConstantUtil.TAG, "getSensorData--mAy:" + mAy);
        Log.d(ConstantUtil.TAG, "getSensorData--mAz:" + mAz);
//        Log.d(ConstantUtil.TAG, "getSensorData--mMx:" + mMx);
//        Log.d(ConstantUtil.TAG, "getSensorData--mMy:" + mMy);
//        Log.d(ConstantUtil.TAG, "getSensorData--mMz:" + mMz);
        SensorDataBean sensorDataBean = new SensorDataBean();
        sensorDataBean.orientation = mOrientation;
        sensorDataBean.ax = mAx;
        sensorDataBean.ay = mAy;
        sensorDataBean.az = mAz;
//        sensorDataBean.mx = mMx;
//        sensorDataBean.my = mMy;
//        sensorDataBean.mz = mMz;
        return sensorDataBean;
    }
}
