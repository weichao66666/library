package io.weichao.opengl_obj;

import android.content.Context;
import android.opengl.GLES30;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import io.weichao.util.GLES30Util;
import io.weichao.util.MatrixStateUtil;

/**
 * Created by admin on 2016/11/14.
 */
public class Compass {
    private ArrayList<Float> mPositionList = new ArrayList<>();
    private ArrayList<Float> mNormalList = new ArrayList<>();
    private ArrayList<Short> mIndexList = new ArrayList<>();
    private ArrayList<Float> mTexCoordList = new ArrayList<>();

    private FloatBuffer mPositionBuffer;//顶点坐标数据缓冲
    private FloatBuffer mNormalBuffer;
    private ShortBuffer mIndexBuffer;
    private FloatBuffer mTexCoordBuffer;//顶点纹理坐标数据缓冲

    private int mProgram;//自定义渲染管线程序id
    private int muMVPMatrixHandle;//总变换矩阵引用
    private int muMMatrixHandle;//位置，旋转变换矩阵
    private int maCameraHandle;//摄像机位置属性引用
    private int maPositionHandle;//顶点位置属性引用
    private int maNormalHandle;//顶点法向量属性引用
    private int maTexCoordHandle; //顶点纹理坐标属性引用
    private int maSunLightLocationHandle;//光源位置属性引用
    private int mTextureId;

    public Compass(Context context, float scaleSize) {
        //初始化顶点数据
        initVertexData(context, scaleSize);
        cacheData();
        //初始化着色器
        initScript(context);
    }

    private void initVertexData(Context context, float scaleSize) {
        BufferedReader br = null;
        try {
            InputStream is = context.getAssets().open("model/compass/data.txt");
            InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String str;
            String mode = null;
            String[] splitedStr;
            while ((str = br.readLine()) != null) {
//                Log.d(ConstantUtil.TAG, str + "");
                if ("Position".equals(str) || "Normal".equals(str) || "Index".equals(str) || "TexCoord".equals(str)) {
                    mode = str;
                } else {
                    splitedStr = str.split(" ");
                    switch (mode) {
                        case "Position":
                            mPositionList.add(Float.parseFloat(splitedStr[0]) * scaleSize);
                            mPositionList.add(Float.parseFloat(splitedStr[1]) * scaleSize);
                            mPositionList.add(Float.parseFloat(splitedStr[2]) * scaleSize);
                            break;
                        case "Normal":
                            mNormalList.add(Float.parseFloat(splitedStr[0]) * scaleSize);
                            mNormalList.add(Float.parseFloat(splitedStr[1]) * scaleSize);
                            mNormalList.add(Float.parseFloat(splitedStr[2]) * scaleSize);
                            break;
                        case "Index":
                            mIndexList.add(Short.parseShort(splitedStr[0]));
                            mIndexList.add(Short.parseShort(splitedStr[1]));
                            mIndexList.add(Short.parseShort(splitedStr[2]));
                            break;
                        case "TexCoord":
                            mTexCoordList.add(Float.parseFloat(splitedStr[0]) * scaleSize);
                            mTexCoordList.add(Float.parseFloat(splitedStr[1]) * scaleSize);
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void cacheData() {
        mPositionBuffer = ByteBuffer.allocateDirect(mPositionList.size() * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mNormalBuffer = ByteBuffer.allocateDirect(mNormalList.size() * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mIndexBuffer = ByteBuffer.allocateDirect(mIndexList.size() * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        mTexCoordBuffer = ByteBuffer.allocateDirect(mTexCoordList.size() * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        float[] fArray1 = new float[mPositionList.size()];
        for (int i = 0; i < mPositionList.size(); i++) {
            fArray1[i] = mPositionList.get(i);
        }
        mPositionBuffer.put(fArray1).position(0);

        float[] fArray2 = new float[mNormalList.size()];
        for (int i = 0; i < mNormalList.size(); i++) {
            fArray2[i] = mNormalList.get(i);
        }
        mNormalBuffer.put(fArray2).position(0);

        short[] sArray = new short[mIndexList.size()];
        for (int i = 0; i < mIndexList.size(); i++) {
            sArray[i] = mIndexList.get(i);
        }
        mIndexBuffer.put(sArray).position(0);

        float[] fArray3 = new float[mTexCoordList.size()];
        for (int i = 0; i < mTexCoordList.size(); i++) {
            fArray3[i] = mTexCoordList.get(i);
        }
        mTexCoordBuffer.put(fArray3).position(0);
    }

    public void initScript(Context context) {
        mProgram = GLES30Util.loadProgram(context, "model/compass/script/vertex_shader.sh", "model/compass/script/fragment_shader.sh");
        //获取程序中顶点位置属性引用
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点法向量属性引用
        maNormalHandle = GLES30.glGetAttribLocation(mProgram, "aNormal");
        //获取程序中顶点经纬度属性引用
        maTexCoordHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoord");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取程序中摄像机位置引用
        maCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera");
        //获取程序中光源位置引用
        maSunLightLocationHandle = GLES30.glGetUniformLocation(mProgram, "uLightLocationSun");
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
    }

    public void setTextureId(int textureId) {
        mTextureId = textureId;
    }

    //绘制
    public void draw() {
        //指定使用某套着色器程序
        GLES30.glUseProgram(mProgram);

        //将最终变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixStateUtil.getFinalMatrix(), 0);
        //将位置，旋转变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixStateUtil.getMMatrix(), 0);
        //将摄像机位置传入渲染管线
        GLES30.glUniform3fv(maCameraHandle, 1, MatrixStateUtil.cameraFB);
        //将光源位置传入到渲染管线中
        GLES30.glUniform3fv(maSunLightLocationHandle, 1, MatrixStateUtil.lightPositionFBSun);

        // 将顶点位置数据传入渲染管线
        GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 0, mPositionBuffer);
        // 将顶点法向量数据送入渲染管线
        GLES30.glVertexAttribPointer(maNormalHandle, 3, GLES30.GL_FLOAT, false, 0, mPositionBuffer);
        // 将顶点纹理数据送入渲染管线
        GLES30.glVertexAttribPointer(maTexCoordHandle, 2, GLES30.GL_FLOAT, false, 0, mTexCoordBuffer);
        //启用顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maPositionHandle);
        //启用顶点法向量数据数组
        GLES30.glEnableVertexAttribArray(maNormalHandle);
        //启用顶点纹理数据数组
        GLES30.glEnableVertexAttribArray(maTexCoordHandle);

        //绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureId);

        GLES30.glDisable(GLES30.GL_CULL_FACE);

        //以三角形方式执行绘制
//        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, mPositionList.size());
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, mIndexList.size(), GLES30.GL_UNSIGNED_SHORT, mIndexBuffer);
//        GLES30.glDrawElements(GLES30.GL_LINE_LOOP, mIndexList.size(), GLES30.GL_UNSIGNED_SHORT, mIndexBuffer);
    }
}

