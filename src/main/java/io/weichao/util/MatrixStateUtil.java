package io.weichao.util;

import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Stack;

/**
 * Created by admin on 2016/11/14.
 */
//存储系统存储矩阵的类
public class MatrixStateUtil {
    public static FloatBuffer cameraFB;//相机参数的缓冲区
    public static FloatBuffer lightPositionFBSun;

    private static float[] mProjMatrix = new float[16];//4*4矩阵投影用
    private static float[] mVMatrix = new float[16];//摄像机位置朝向9参数矩阵
    private static float[] currMatrix;//当前变换矩阵
    private static float[] lightLocationSun = new float[]{0, 0, 0};//太阳定位光光源的位置
    private static Stack<float[]> mStack = new Stack<>();//保护变换矩阵的栈

    private MatrixStateUtil() {
    }

    public static void setInitStack()//获取不变初始矩阵
    {
        currMatrix = new float[16];
        Matrix.setRotateM(currMatrix, 0, 0, 1, 0, 0);//这里是把矩阵在x轴上旋转0度

    }

    public static void pushMatrix()//获取不变换初始矩阵
    {
        mStack.push(currMatrix.clone());//clone功能是复制 把这个矩阵复制后然后存进栈中

    }

    public static void popMatrix()//恢复变换矩阵
    {
        currMatrix = mStack.pop();
    }

    public static void translate(float x, float y, float z)//设置沿xyz轴移动
    {
        Matrix.translateM(currMatrix, 0, x, y, z);
    }

    public static void rotate(float angle, float x, float y, float z)//设置绕xyz轴旋转
    {
        Matrix.rotateM(currMatrix, 0, angle, x, y, z);
    }

    //设置摄像机
    public static void setCamera
    (
            float cx,    //摄像机位置x
            float cy,   //摄像机位置y
            float cz,   //摄像机位置z
            float tx,   //摄像机目标点x
            float ty,   //摄像机目标点y
            float tz,   //摄像机目标点z
            float upx,  //摄像机UP向量X分量
            float upy,  //摄像机UP向量Y分量
            float upz   //摄像机UP向量Z分量
    ) {
        Matrix.setLookAtM
                (
                        mVMatrix,
                        0,
                        cx,
                        cy,
                        cz,
                        tx,
                        ty,
                        tz,
                        upx,
                        upy,
                        upz
                );

        float[] cameraLocation = new float[3];//摄像机位置
        cameraLocation[0] = cx;
        cameraLocation[1] = cy;
        cameraLocation[2] = cz;

        ByteBuffer llbb = ByteBuffer.allocateDirect(3 * 4);
        llbb.order(ByteOrder.nativeOrder());//设置字节顺序
        cameraFB = llbb.asFloatBuffer();
        cameraFB.put(cameraLocation);
        cameraFB.position(0);
    }

    //设置透视投影参数
    public static void setProjectFrustum
    (
            float left,        //near面的left
            float right,    //near面的right
            float bottom,   //near面的bottom
            float top,      //near面的top
            float near,        //near面距离
            float far       //far面距离
    ) {
        Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    //设置正交投影参数
    public static void setProjectOrtho
    (
            float left,        //near面的left
            float right,    //near面的right
            float bottom,   //near面的bottom
            float top,      //near面的top
            float near,        //near面距离
            float far       //far面距离
    ) {
        Matrix.orthoM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    //获取总的变换矩阵
    public static float[] getFinalMatrix() {
        float[] mMVPMatrix = new float[16];
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        return mMVPMatrix;
    }

    //获取具体物体的变换矩阵
    public static float[] getMMatrix() {
        return currMatrix;
    }

    //设置太阳光源的位置的方法
    public static void setLightLocationSun(float x, float y, float z) {
        lightLocationSun[0] = x;
        lightLocationSun[1] = y;
        lightLocationSun[2] = z;

        ByteBuffer llbb = ByteBuffer.allocateDirect(3 * 4);
        llbb.order(ByteOrder.nativeOrder());//设置字节顺序
        lightPositionFBSun = llbb.asFloatBuffer();
        lightPositionFBSun.put(lightLocationSun);
        lightPositionFBSun.position(0);
    }
}
