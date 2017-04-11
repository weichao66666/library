package io.weichao.view;

import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by WeiChao on 2016/8/5.
 */
public class Sphere {
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mNormalBuffer;
    private FloatBuffer mTextureBuffer;
    private ShortBuffer mIndexBuffer;
    private int mIndexCount;

    /**
     * 生成球面
     * 官方Demo中有bug：
     * （1）顶点坐标和顶点坐标索引不匹配，画出来的图像左右颠倒。
     * （2）mTextureBuffer.put(texIndex + 1, (1.0f - (float) i) / (sliceCount - 1))有问题，画出来的图像缺少下半部分，且有一个极点缺少一圈贴图。
     *
     * @param sliceCount
     * @param radius
     * @return
     */
    public Sphere(int sliceCount, float radius) {
        mIndexCount = sliceCount * sliceCount * 6;
        int vertexCount = (sliceCount + 1) * (sliceCount + 1);
        float arcStep = ((2.0f * (float) Math.PI) / sliceCount);// 弧度阶越

        mVertexBuffer = ByteBuffer.allocateDirect(vertexCount * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mNormalBuffer = ByteBuffer.allocateDirect(vertexCount * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextureBuffer = ByteBuffer.allocateDirect(vertexCount * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mIndexBuffer = ByteBuffer.allocateDirect(mIndexCount * 2).order(ByteOrder.nativeOrder()).asShortBuffer();

        // 从北极到南极，沿任意经线遍历
        for (int i = 0; i < sliceCount + 1; i++) {
            float y = (float) (radius * Math.cos(arcStep * i));
            // 从北极点看，绕任意纬线逆时针遍历
            for (int j = 0; j < sliceCount + 1; j++) {
                int vertex = (i * (sliceCount + 1) + j) * 3;
                float x = (float) (radius * Math.sin(arcStep * i) * Math.sin(arcStep * j));
                float z = (float) (radius * Math.sin(arcStep * i) * Math.cos(arcStep * j));

                mVertexBuffer.put(vertex, x);
                mVertexBuffer.put(vertex + 1, y);
                mVertexBuffer.put(vertex + 2, z);

                mNormalBuffer.put(vertex, x / radius);
                mNormalBuffer.put(vertex + 1, y / radius);
                mNormalBuffer.put(vertex + 2, z / radius);

                int texIndex = (i * (sliceCount + 1) + j) * 2;
                mTextureBuffer.put(texIndex, (float) j / sliceCount);
                mTextureBuffer.put(texIndex + 1, 2 - (float) i * 2 / sliceCount); //2-：上下颠倒
                //*2：纹理有下半部分
            }
        }

        for (int i = 0, index = 0; i < sliceCount; i++) {
            for (int j = 0; j < sliceCount; j++) {
                /*第1个三角形的顶点索引*/
                mIndexBuffer.put(index++, (short) (i * (sliceCount + 1) + j));
                mIndexBuffer.put(index++, (short) ((i + 1) * (sliceCount + 1) + (j + 1)));
                mIndexBuffer.put(index++, (short) ((i + 1) * (sliceCount + 1) + j));
                /*第2个三角形的顶点索引*/
                mIndexBuffer.put(index++, (short) (i * (sliceCount + 1) + j));
                mIndexBuffer.put(index++, (short) (i * (sliceCount + 1) + (j + 1)));
                mIndexBuffer.put(index++, (short) ((i + 1) * (sliceCount + 1) + (j + 1)));
            }
        }
    }

    public FloatBuffer getVertexBuffer() {
        return mVertexBuffer;
    }

    public FloatBuffer getNormalBuffer() {
        return mNormalBuffer;
    }

    public FloatBuffer getTextureBuffer() {
        return mTextureBuffer;
    }

    public ShortBuffer getIndexBuffer() {
        return mIndexBuffer;
    }

    public int getIndexCount() {
        return mIndexCount;
    }
}
