package io.weichao.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import io.weichao.util.GLES30Util;
import io.weichao.util.MatrixStateUtil;

/**
 * Created by admin on 2016/11/14.
 */
@SuppressLint("ClickableViewAccessibility")
public class GLES30CompassSV extends GLSurfaceView {
    private SceneRenderer mRenderer;//场景渲染器

    private float yAngle = -60;//太阳灯光绕y轴旋转的角度
    private float yozAngle = 45;//摄像机绕x轴旋转的角度

    private float xozAngle = 0;//地球自传的角度

    public GLES30CompassSV(Context context) {
        super(context);
        this.setEGLContextClientVersion(3);//设置使用OPENGL ES3.0
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mRenderer = new SceneRenderer(context);//创建场景渲染器
        setRenderer(mRenderer);//设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为持续渲染
    }

    private class SceneRenderer implements Renderer {
        Context context;
        Arrow arrow;
        Compass compass;
        int texIdCompass;

        public SceneRenderer(Context context) {
            this.context = context;
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //创建地球对象
            arrow = new Arrow(0.05f, context);
            compass = new Compass(1f, context);
            texIdCompass = GLES30Util.loadTexture(context, "model/compass/texture/compass.png");
            //设置屏幕背景颜色RGBA
            GLES30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //初始化变换矩阵
            MatrixStateUtil.setInitStack();
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置
            GLES30.glViewport(0, 0, width, height);
            //计算GLSurfaeVIew的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixStateUtil.setProjectFrustum(-ratio, ratio, -1, 1, 4f, 100);

            //设置太阳灯光的初始位置
//            float sunx = (float) (Math.cos(Math.toRadians(yAngle)) * 100);
//            float sunz = -(float) (Math.sin(Math.toRadians(yAngle)) * 100);
//            MatrixStateUtil.setLightLocationSun(sunx, 5, sunz);
            //设置相机9参数
            float cy = (float) (7.2 * Math.sin(Math.toRadians(yozAngle)));
            float cz = (float) (7.2 * Math.cos(Math.toRadians(yozAngle)));
            float upy = (float) Math.cos(Math.toRadians(yozAngle));
            float upz = -(float) Math.sin(Math.toRadians(yozAngle));
            MatrixStateUtil.setCamera(0, cy, cz, 0, 0, 0, 0, upy, upz);
        }

        public void onDrawFrame(GL10 gl) {
            //清楚屏幕深度缓冲与颜色缓冲
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

            //保护现场
            MatrixStateUtil.pushMatrix();

            //设置太阳灯光的位置
            float sunx = (float) (Math.sin(Math.toRadians(xozAngle)) * 100);
            float sunz = (float) (Math.cos(Math.toRadians(xozAngle)) * 100);
            MatrixStateUtil.setLightLocationSun(sunx, 50, sunz);

            MatrixStateUtil.translate(0, 0, 1f);
            MatrixStateUtil.rotate(xozAngle, 0, 1, 0);
            arrow.drawSelf();

            MatrixStateUtil.translate(0, -0.5f, 0f);
            compass.drawSelf(texIdCompass);

            //恢复现场
            MatrixStateUtil.popMatrix();
        }
    }

    public void onSensorChanged(float orientation, float x, float y, float z) {
        xozAngle = orientation;
    }
}
