package io.weichao.util;

import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Build;

import java.util.List;

/**
 * Created by WEI CHAO on 2017/4/24.
 */

public class CameraUtil {
    private CameraUtil() {
    }

    public static Camera.Parameters getCameraParameters(Camera camera, Intent intent, int width) {
        Camera.Parameters parameters = camera.getParameters();

        // 设置聚焦模式
        List<String> supportedFocusModes = parameters.getSupportedFocusModes();
        if (supportedFocusModes.size() > 0) {
            String focusMode = IntentUtil.getStringExtra(intent, "ParametersFocusMode", Camera.Parameters.FOCUS_MODE_AUTO);
            if (!supportedFocusModes.contains(focusMode)) {
                focusMode = supportedFocusModes.get(0);
            }
            parameters.setFocusMode(focusMode);
        }
        // 设置预览大小
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        if (supportedPreviewSizes.size() > 0) {
            int[] previewSizeArray = IntentUtil.getIntArrayExtra(intent, "ParametersPreviewSize", new int[]{-1, -1});
            Camera.Size size;
            if (previewSizeArray[0] != -1) {
                // intent 传进来了 PreviewSize
                size = camera.new Size(previewSizeArray[0], previewSizeArray[1]);
                if (!supportedPreviewSizes.contains(size)) {
                    size = supportedPreviewSizes.get(0);
                }
            } else {
                int bestSizeIndex = getBestSizeIndex(width, supportedPreviewSizes);
                size = supportedPreviewSizes.get(bestSizeIndex);
            }
            parameters.setPreviewSize(size.width, size.height);
        }
        // 设置图片大小
        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
        if (supportedPictureSizes.size() > 0) {
            int[] pictureSizeArray = IntentUtil.getIntArrayExtra(intent, "ParametersPictureSize", new int[]{-1, -1});
            Camera.Size size;
            if (pictureSizeArray[0] != -1) {
                // intent 传进来了 PreviewSize
                size = camera.new Size(pictureSizeArray[0], pictureSizeArray[1]);
                if (!supportedPictureSizes.contains(size)) {
                    size = supportedPictureSizes.get(0);
                }
            } else {
                int bestSizeIndex = getBestSizeIndex(width, supportedPictureSizes);
                size = supportedPictureSizes.get(bestSizeIndex);
            }
            parameters.setPictureSize(size.width, size.height);
        }
        // 设置图片保存格式
        List<Integer> supportedPictureFormatLists = parameters.getSupportedPictureFormats();
        if (supportedPictureFormatLists.size() > 0) {
            int pictureFormat = intent.getIntExtra("ParametersPictureFormat", ImageFormat.NV21);
            if (!supportedPictureFormatLists.contains(pictureFormat)) {
                pictureFormat = supportedPictureFormatLists.get(0);
            }
            parameters.setPictureFormat(pictureFormat);
        }
        // 设置图片保存质量
        parameters.setJpegQuality(intent.getIntExtra("ParametersJpegQuality", 100));

        return parameters;
    }

    /**
     * 获取最佳分辨率（高度优先，两边可能有黑边）
     *
     * @param width
     * @param sizeList
     * @return
     */
    private static int getBestSizeIndex(int width, List<Camera.Size> sizeList) {
        int index = 0;
        float maxRatio = 0;
        int bestWidth = 0;

        for (int i = 0; i < sizeList.size(); i++) {
            Camera.Size size = sizeList.get(i);
            float ratio = (float) size.width / size.height;
            if ((ratio >= maxRatio) && (size.width <= width) && (size.width >= bestWidth)) {
                index = i;
                maxRatio = ratio;
                bestWidth = size.width;
            }
        }

        return index;
    }

    /**
     * 有虚拟按键影响宽高，强制指定预览和拍照分辨率。
     *
     * @param width
     * @param height
     */
    public static Camera.Parameters getCameraParameters(Camera camera, Intent intent, int width, int height) {
        Camera.Parameters parameters = camera.getParameters();

        // 设置聚焦模式
        List<String> supportedFocusModes = parameters.getSupportedFocusModes();
        if (supportedFocusModes.size() > 0) {
            String focusMode = IntentUtil.getStringExtra(intent, "ParametersFocusMode", Camera.Parameters.FOCUS_MODE_AUTO);
            if (!supportedFocusModes.contains(focusMode)) {
                focusMode = supportedFocusModes.get(0);
            }
            parameters.setFocusMode(focusMode);
        }
        // 设置预览大小
        parameters.setPreviewSize(width, height);
        // 设置图片大小
        parameters.setPictureSize(width, height);
        // 设置图片保存格式
        List<Integer> supportedPictureFormatLists = parameters.getSupportedPictureFormats();
        if (supportedPictureFormatLists.size() > 0) {
            int pictureFormat = intent.getIntExtra("ParametersPictureFormat", ImageFormat.NV21);
            if (!supportedPictureFormatLists.contains(pictureFormat)) {
                pictureFormat = supportedPictureFormatLists.get(0);
            }
            parameters.setPictureFormat(pictureFormat);
        }
        // 设置图片保存质量
        parameters.setJpegQuality(intent.getIntExtra("ParametersJpegQuality", 100));

        return parameters;
    }

    public static boolean isFrontFace(int cameraId) {
        boolean isFrontFace = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                isFrontFace = true;
            }
        }

        return isFrontFace;
    }
}
