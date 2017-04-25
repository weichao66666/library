package io.weichao.util;

/**
 * Created by Administrator on 2016/10/18.
 */

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BluetoothUtil {
    private BluetoothUtil() {
    }

    /**
     * 与设备配对 参考源码：platform/packages/apps/Settings.git
     * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java
     */
    static public boolean createBond(Class btClass, BluetoothDevice btDevice)
            throws Exception {
        Method createBondMethod = btClass.getMethod("createBond");
        return (Boolean) createBondMethod.invoke(btDevice);
    }

    /**
     * 与设备解除配对 参考源码：platform/packages/apps/Settings.git
     * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java
     */
    static public boolean removeBond(Class<?> btClass, BluetoothDevice btDevice)
            throws Exception {
        Method removeBondMethod = btClass.getMethod("removeBond");
        return (Boolean) removeBondMethod.invoke(btDevice);
    }

    static public boolean setPin(Class<? extends BluetoothDevice> btClass, BluetoothDevice btDevice,
                                 String str) throws Exception {
        try {
            Method removeBondMethod = btClass.getDeclaredMethod("setPin", new Class[]{byte[].class});
            return (Boolean) removeBondMethod.invoke(btDevice, new Object[]{str.getBytes()});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    // 取消用户输入
    static public boolean cancelPairingUserInput(Class<?> btClass,
                                                 BluetoothDevice device) throws Exception {
        Method createBondMethod = btClass.getMethod("cancelPairingUserInput");
//        cancelBondProcess(btClass, device);
        return (Boolean) createBondMethod.invoke(device);
    }

    // 取消配对
    static public boolean cancelBondProcess(Class<?> btClass,
                                            BluetoothDevice device) throws Exception {
        Method createBondMethod = btClass.getMethod("cancelBondProcess");
        return (Boolean) createBondMethod.invoke(device);
    }

    //确认配对
    static public void setPairingConfirmation(Class<?> btClass, BluetoothDevice device, boolean isConfirm) throws Exception {
        Method setPairingConfirmation = btClass.getDeclaredMethod("setPairingConfirmation", boolean.class);
        setPairingConfirmation.invoke(device, isConfirm);
    }

    static public void printAllInform(String tag, Class cls) {
        try {
            // 取得所有方法
            Method[] methods = cls.getMethods();
            int i = 0;
            for (; i < methods.length; i++) {
                Log.d(tag, i + " " + methods[i].getName());
            }
            // 取得所有常量
            Field[] fields = cls.getFields();
            for (i = 0; i < fields.length; i++) {
                Log.d(tag, i + " " + fields[i].getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
