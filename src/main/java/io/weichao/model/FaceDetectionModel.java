package io.weichao.model;

import android.view.View;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;

import io.weichao.activity.BaseFragmentActivity;
import io.weichao.bean.DetectionBean;
import io.weichao.library.R;
import io.weichao.util.OpenCVUtil;

/**
 * Created by WEI CHAO on 2017/3/14.
 */

public class FaceDetectionModel extends BaseModel implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final Scalar HUMAN_RECT_COLOR = new Scalar(0, 255, 0, 255);
    private static final Scalar PANDA_RECT_COLOR = new Scalar(255, 0, 0, 255);

    public View view;

    private BaseFragmentActivity mActivity;

    private CameraBridgeViewBase mCameraView;

    /**
     * 默认最小识别区域占预览窗口比例
     */
    private float mRelativeFaceSize = 0.5f;
    private int mAbsoluteFaceSize = 0;

    private DetectionBean mPandaDetectionBean;
    private DetectionBean mHumanDetectionBean;

    private Mat mRgba;
    private Mat mGray;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(mActivity) {
        /* 在OpenCV库初始化后调用 */
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                /* OpenCV初始化成功 */
                case LoaderCallbackInterface.SUCCESS:
                    System.loadLibrary("detection_based_tracker");

				    /* 加载级联分类器文件。 */
                    InputStream is = null;
                    try {
                        is = mActivity.getResources().openRawResource(R.raw.lbpcascade_panda);
                        mPandaDetectionBean = OpenCVUtil.createDetector(mActivity, is);

                        is = mActivity.getResources().openRawResource(R.raw.lbpcascade_human);
                        mHumanDetectionBean = OpenCVUtil.createDetector(mActivity, is);
                    } catch (Exception e) {
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
                    /* 连接camera */
                    mCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    public FaceDetectionModel(BaseFragmentActivity activity) {
        mActivity = activity;

        view = View.inflate(activity, R.layout.layout_face_detection, null);

        mCameraView = (CameraBridgeViewBase) view.findViewById(R.id.java_camera_view);
        mCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCameraView != null) {
            mCameraView.disableView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCameraView != null) {
            mCameraView.disableView();
        }
    }

	/* CvCameraViewListener2 start */

    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        // TODO 前置摄像头需要翻转图像

		/* 计算最小识别区域 */
        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            mPandaDetectionBean.detection.setMinFaceSize(mAbsoluteFaceSize);
            mHumanDetectionBean.detection.setMinFaceSize(mAbsoluteFaceSize);
        }

		/* 检测 */
        MatOfRect pandaFaces = new MatOfRect();
        if (mPandaDetectionBean.detection != null) {
            mPandaDetectionBean.detection.detect(mGray, pandaFaces);
        }
        Rect[] pandaFacesArray = pandaFaces.toArray();
//        if (pandaFacesArray.length > 0) {
//            mActivity.detectSuccess(0);
//        }
        /* 将检测结果用矩形包围并画出 */
        for (int i = 0; i < pandaFacesArray.length; i++) {
            Imgproc.rectangle(mRgba,        // 输入要显示的rgb图像
                    pandaFacesArray[i].tl(),// 矩形左上角坐标
                    pandaFacesArray[i].br(),// 矩形右下角坐标
                    PANDA_RECT_COLOR,       // 边框颜色
                    3);                     // 边框宽度
        }
        /* 检测 */
        MatOfRect humanFaces = new MatOfRect();
        if (mHumanDetectionBean.detection != null) {
            mHumanDetectionBean.detection.detect(mGray, humanFaces);
        }
        /* 将检测结果用矩形包围并画出 */
        Rect[] humanFacesArray = humanFaces.toArray();
        for (int i = 0; i < humanFacesArray.length; i++) {
            Imgproc.rectangle(mRgba, humanFacesArray[i].tl(), humanFacesArray[i].br(), HUMAN_RECT_COLOR, 3);
        }
//        if (humanFacesArray.length > 0) {
//            mActivity.detectSuccess(1);
//        }

        return mRgba;
    }

	/* CvCameraViewListener2 stop */
}
