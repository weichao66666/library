package io.weichao.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by WEI CHAO on 2017/4/25.
 */
public class AssetsUtil {
    private AssetsUtil() {
    }

    public static String getString(Context context, String filePath) {
        String Result = "";

        BufferedReader bufReader = null;
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getAssets().open(filePath), "GBK");
            bufReader = new BufferedReader(inputReader);
            String line;
            while ((line = bufReader.readLine()) != null) {
                Result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufReader != null) {
                try {
                    bufReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return Result;
    }

    public static Bitmap getBitmap(Context context, String fileName) {
        Bitmap bitmap = null;

        InputStream is = null;
        try {
            is = context.getAssets().open(fileName);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }
}
