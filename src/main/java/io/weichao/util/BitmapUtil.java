package io.weichao.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtil {
    private BitmapUtil() {
    }

    /**
     * 获取drawable中的图片
     *
     * @param context
     * @param id
     * @return
     */
    public static Bitmap getBitmap(Context context, int id) {
        InputStream inputStream = context.getResources().openRawResource(id);
        BitmapFactory.Options options = getDecodeOptions();
        return BitmapFactory.decodeStream(inputStream, null, options);
    }

    /**
     * 获取手机存储中的图片
     *
     * @param path
     * @return
     */
    public static Bitmap getBitmap(String path) {
        return BitmapFactory.decodeFile(path);
    }

    /**
     * 获取手机存储或网络中的图片
     *
     * @param contentResolver
     * @param uri
     * @return
     */
    public static Bitmap getBitmap(ContentResolver contentResolver, Uri uri) {
        Bitmap bitmap = null;

        InputStream inputStream = null;
        try {
            inputStream = contentResolver.openInputStream(uri);
            BitmapFactory.Options options = getDecodeOptions(inputStream);
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }

    /**
     * 获取手机存储或网络中的图片
     *
     * @param contentResolver
     * @param uri
     * @return
     */
    public static Bitmap getBitmap2(ContentResolver contentResolver, Uri uri) {
        Bitmap bitmap = null;

        InputStream inputStream = null;
        try {
            inputStream = contentResolver.openInputStream(uri);
            BitmapFactory.Options options = getDecodeOptions(inputStream);
            byte[] byteArray = toByteArray(inputStream);
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }

    private static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n;
        while ((n = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, n);
        }
        return baos.toByteArray();
    }

    public static int getBitmapRealWidth(ContentResolver contentResolver, Uri uri) {
        int width = 0;

        InputStream inputStream = null;
        try {
            inputStream = contentResolver.openInputStream(uri);
            BitmapFactory.Options options = getDecodeOptions(inputStream);
            width = options.outWidth / options.inSampleSize;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return width;
    }

    private static BitmapFactory.Options getDecodeOptions() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return options;
    }

    private static BitmapFactory.Options getDecodeOptions(InputStream inputStream) {
        //https://github.com/johnnylambada/WorldMap    取消硬件加速，加载高清大图的指定区域，实际测试移动和缩放时抖动剧烈，所以还是保留硬件加速
        //TODO 强制指定最大宽和高都为4096(硬件加速最大值，android:hardwareAccelerated="true"开启硬件加速，移动和缩放更平滑；有些机器不是4096，暂不知道获取该值方式)
        return getDecodeOptions(4096, 4096, inputStream);
    }

    private static BitmapFactory.Options getDecodeOptions(int maxWidth, int maxHeight, InputStream inputStream) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            options.inSampleSize = 1;
            if (options.outWidth > maxWidth && maxWidth != 0) {
                options.inSampleSize = (int) Math.ceil((float) options.outWidth / (float) maxWidth);
            }
            if (options.outHeight > maxHeight && maxHeight != 0) {
                int inSampleSize = (int) Math.ceil((float) options.outHeight / (float) maxHeight);
                if (inSampleSize > options.inSampleSize) {
                    options.inSampleSize = inSampleSize;
                }
            }
            options.inJustDecodeBounds = false;

            options.inPreferredConfig = Bitmap.Config.RGB_565;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return options;
    }

    public static int getSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getRowBytes() * bitmap.getHeight();
        } else {
            return bitmap.getByteCount();
        }
    }

    public static Bitmap getSpecifiedResolutionBitmap(Bitmap bitmap, int width, int height) {
        return ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
    }

    public static Bitmap getSpecifiedResolutionBitmap(Bitmap bitmap, int width, int height, File bitmapFile) {
        Bitmap newBitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        newBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        try {
            FileOutputStream fos = new FileOutputStream(bitmapFile);
            fos.write(byteArray);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newBitmap;
    }

    public static Bitmap getUpsideDownBitmap(Bitmap bitmap) {
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
    }

    public static Bitmap getUpsideDownBitmap(Bitmap bitmap, int height) {
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        return Bitmap.createBitmap(bitmap, 0, bitmapHeight - height, bitmapWidth, height, matrix, false);
    }

    public static Bitmap getLinearGradientBitmap(Bitmap bitmap) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new LinearGradient(0, 0, 0, bitmap.getHeight(), 0x70FFFFFF, 0x00FFFFFF, Shader.TileMode.MIRROR));
        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_IN));

        new Canvas(bitmap).drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);

        return bitmap;
    }
}
