package io.weichao.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewTreeObserver;

import io.weichao.callback.GestureCallback;
import io.weichao.runnable.CountDownRunnable;
import io.weichao.runnable.ZoomImageViewAutoScaleRunnable;
import io.weichao.util.ConstantUtil;


/**
 * Created by WeiChao on 2016/6/15.
 */
public class ZoomImageView extends AppCompatImageView implements GestureCallback, ViewTreeObserver.OnGlobalLayoutListener, ScaleGestureDetector.OnScaleGestureListener {
    public float scaleInit = 1.0F;
    public float scaleMax = 4.0F;

    public ZoomImageViewAutoScaleRunnable autoScaleRunnable;
    public CountDownRunnable countDownRunnable;
    public ScaleGestureDetector scaleGestureDetector;
    public GestureCallback imageActivityCallback;
    public Matrix scaleMatrix = new Matrix();

    private boolean mNeedLayout = true;
    private int mScrollDistanceWidthLimit;
    private int mScrollDistanceHeightLimit;
    // 用于存放矩阵的9个值
    private float[] mMatrixValues = new float[9];
    private boolean mIsDraggableToLeftAndRight = true;
    private boolean mIsDraggableToUpAndDown = true;
    private float mLastX;
    private float mLastY;
    private long mDownTime;
    private int mOffsetX;
    private int mOffsetY;
    private long mLastTapUpTime;
    private boolean mIsSingleTap;

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // ScaleType.MATRIX使图片可以被缩放
        super.setScaleType(ScaleType.MATRIX);
        // 设置单击回调
        countDownRunnable = new CountDownRunnable();
        countDownRunnable.callback = this;
        // 设置双击动画
        autoScaleRunnable = new ZoomImageViewAutoScaleRunnable(this);
        // 设置多指操作
        scaleGestureDetector = new ScaleGestureDetector(getContext(), this);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            // 触摸点个数多于1，交给多指操作处理
            return scaleGestureDetector.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsSingleTap = false;
                // 按下时记录初始位置
                mLastX = event.getX();
                mLastY = event.getY();
                mOffsetX = 0;
                mOffsetY = 0;
                mDownTime = SystemClock.elapsedRealtime();
                break;
            case MotionEvent.ACTION_MOVE:
                float currentX = event.getX();
                float currentY = event.getY();

                int offset = (int) Math.abs(currentX - mLastX);
                if (offset > mOffsetX) {
                    mOffsetX = offset;
                }
                offset = (int) Math.abs(currentY - mLastY);
                if (offset > mOffsetY) {
                    mOffsetY = offset;
                }

                float deltaX = currentX - mLastX;
                float deltaY = currentY - mLastY;

                RectF rectF = getMatrixRectF();
                if (getDrawable() != null) {
                    mIsDraggableToLeftAndRight = mIsDraggableToUpAndDown = true;
                    // 如果宽度小于屏幕宽度，则禁止左右移动
                    if (rectF.width() < getWidth()) {
                        deltaX = 0;
                        mIsDraggableToLeftAndRight = false;
                    }
                    // 如果高度小于屏幕高度，则禁止上下移动
                    if (rectF.height() < getHeight()) {
                        deltaY = 0;
                        mIsDraggableToUpAndDown = false;
                    }
                    scaleMatrix.postTranslate(deltaX, deltaY);
                    checkMatrixBounds();
                    setImageMatrix(scaleMatrix);
                }

                mLastX = currentX;
                mLastY = currentY;
                break;
            case MotionEvent.ACTION_UP:
                long time = SystemClock.elapsedRealtime();
                if (mOffsetX < mScrollDistanceWidthLimit && mOffsetY < mScrollDistanceHeightLimit) {
                    if (time - mLastTapUpTime < ConstantUtil.DOUBLE_TAP_TIMEOUT) {
                        onDoubleTap();
                    } else if (time - mDownTime < ConstantUtil.SINGLE_TAP_TIMEOUT) {
                        mIsSingleTap = true;
                        countDownRunnable.countDown(ConstantUtil.SINGLE_TAP_TIMEOUT << 1);
                    }
                }
                mLastTapUpTime = time;
                break;
            default:
                break;
        }

        return true;
    }

    // BaseGestureCallback

    @Override
    public void onFlingUp() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onFlingDown() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onFlingLeft() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onFlingRight() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDown() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onLongPress() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSingleTap() {
        if (mIsSingleTap) {
            imageActivityCallback.onSingleTap();
        }
    }

    @Override
    public void onDoubleTap() {
        if (!autoScaleRunnable.isRun()) {
            autoScaleRunnable.start();
        }
    }

    // ViewTreeObserver.OnGlobalLayoutListener

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        // 图片位置初始化为屏幕中心（只设置一次）
        if (mNeedLayout) {
            Drawable drawable = getDrawable();
            if (drawable == null) {
                return;
            }

            int displayWidth = getWidth();
            int displayHeight = getHeight();
            mScrollDistanceWidthLimit = displayWidth >> 8;
            mScrollDistanceHeightLimit = displayHeight >> 8;
            // 拿到图片的宽和高
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();
            float scale = 1.0f;
            // 如果图片的宽大于屏幕，则缩至屏幕的宽
            if (drawableWidth > displayWidth && drawableHeight <= displayHeight) {
                scale = displayWidth * 1.0f / drawableWidth;
            } else
                // 如果图片的高大于屏幕，则缩至屏幕的高
                if (drawableHeight > displayHeight && drawableWidth <= displayWidth) {
                    scale = displayHeight * 1.0f / drawableHeight;
                } else
                    // 如果宽和高都大于屏幕，则按比例适应屏幕大小
                    if (drawableWidth > displayWidth && drawableHeight > displayHeight) {
                        scale = Math.min(displayWidth * 1.0f / drawableWidth, displayHeight * 1.0f / drawableHeight);
                    }
            scaleInit = scale;
            // 图片移动至屏幕中心
            scaleMatrix.postTranslate((displayWidth - drawableWidth) >> 1, (displayHeight - drawableHeight) >> 1);
            scaleMatrix.postScale(scale, scale, displayWidth >> 1, displayHeight >> 1);
            setImageMatrix(scaleMatrix);
            mNeedLayout = false;
        }
    }

    // ScaleGestureDetector.OnScaleGestureListener

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        // 一定要返回true才会进入onScale()
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if (getDrawable() == null) {
            return false;
        }

        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();
        if ((scale < scaleMax && scaleFactor > 1.0f) || (scale > scaleInit && scaleFactor < 1.0f)) {
            // 控制缩放的范围
            float size = scaleFactor * scale;
            if (size < scaleInit) {
                scaleFactor = scaleInit / scale;
            } else if (size > scaleMax) {
                scaleFactor = scaleMax / scale;
            }
            // 放大中心固定设置为屏幕中心
//            scaleMatrix.postScale(scaleFactor, scaleFactor, getWidth() >> 1, getHeight() >> 1);
            // 放大中心动态设置为两指中间
            scaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            // 缩小到最后保证在屏幕中间
            controlScaleCenter();
            setImageMatrix(scaleMatrix);
        }

        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        // TODO Auto-generated method stub
    }

    /**
     * 获得当前的缩放比例
     *
     * @return
     */
    public float getScale() {
        scaleMatrix.getValues(mMatrixValues);
        return mMatrixValues[Matrix.MSCALE_X];
    }

    /**
     * 在缩放时，进行图片显示范围的控制
     */
    public void controlScaleCenter() {
        RectF rectF = getMatrixRectF();
        float deltaX = 0, deltaY = 0;
        int displayWidth = getWidth();
        int displayHeight = getHeight();
        // 如果宽大于屏幕，则控制范围
        if (rectF.width() > displayWidth) {
            if (rectF.left > 0) {
                deltaX = -rectF.left;
            } else if (rectF.right < displayWidth) {
                deltaX = displayWidth - rectF.right;
            }
        } else {
            // 如果宽小于屏幕，则让其居中
            deltaX = (displayWidth >> 1) - rectF.right + 0.5f * rectF.width();
        }
        // 如果高大于屏幕，则控制范围
        if (rectF.height() > displayHeight) {
            if (rectF.top > 0) {
                deltaY = -rectF.top;
            } else if (rectF.bottom < displayHeight) {
                deltaY = displayHeight - rectF.bottom;
            }
        } else {
            // 如果高小于屏幕，则让其居中
            deltaY = (displayHeight >> 1) - rectF.bottom + 0.5f * rectF.height();
        }
        scaleMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 移动时，进行边界判断，主要判断宽或高大于屏幕的
     */
    private void checkMatrixBounds() {
        RectF rectF = getMatrixRectF();
        float deltaX = 0, deltaY = 0;
        float displayWidth = getWidth();
        float displayHeight = getHeight();
        // 判断移动或缩放后，图片显示是否超出屏幕边界
        if (mIsDraggableToLeftAndRight) {
            if (rectF.left > 0) {
                deltaX = -rectF.left;
            } else if (rectF.right < displayWidth) {
                deltaX = displayWidth - rectF.right;
            }
        }
        if (mIsDraggableToUpAndDown) {
            if (rectF.top > 0) {
                deltaY = -rectF.top;
            } else if (rectF.bottom < displayHeight) {
                deltaY = displayHeight - rectF.bottom;
            }
        }
        scaleMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 根据当前图片的Matrix获得图片的范围
     *
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix = scaleMatrix;
        RectF rectF = new RectF();
        Drawable drawable = getDrawable();
        if (drawable != null) {
            rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            matrix.mapRect(rectF);
        }
        return rectF;
    }
}