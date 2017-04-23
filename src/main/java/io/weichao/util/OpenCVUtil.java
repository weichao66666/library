package io.weichao.util;

import android.content.Context;

import org.opencv.objdetect.CascadeClassifier;
import org.opencv.samples.facedetect.DetectionBasedTracker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.weichao.bean.DetectionBean;

public class OpenCVUtil {
	/**
	 * 创建识别器
	 * 
	 * @param context
	 * @param inputStream
	 * @return
	 */
	public static DetectionBean createDetector(Context context, InputStream inputStream) {
		DetectionBean detectionBean = new DetectionBean();

		FileOutputStream fos = null;
		File cascadeDir = null;
		try {
			/* 在应用程序的数据文件夹下获取或者创建name对应的子目录 */
			cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
			File cascadeFile = new File(cascadeDir, "cascade.xml");

			fos = new FileOutputStream(cascadeFile);
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				fos.write(buffer, 0, bytesRead);
			}

			/* 创建级联分类器 */
			detectionBean.classifier = new CascadeClassifier(cascadeFile.getAbsolutePath());
			if (detectionBean.classifier.empty()) {
				detectionBean.classifier = null;
			}
			/* 创建识别、跟踪器 */
			detectionBean.detection = new DetectionBasedTracker(cascadeFile.getAbsolutePath(), 0);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (cascadeDir != null) {
				cascadeDir.delete();
			}
		}

		return detectionBean;
	}
}
