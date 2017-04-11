package io.weichao.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.support.v4.util.LruCache;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.Scroller;

import io.weichao.adapter.MultPagesAdapter;
import io.weichao.library.R;
import io.weichao.util.BitmapUtil;
import io.weichao.util.HardwareInfoUtil;

/**
 * Created by WEI CHAO on 2017/4/7.
 */

public class MultPagesView extends View {
    public enum VerticalGravity {
        TOP, BOTTOM, CENTER
    }

    private static final int BASE_FLING_VELOCITY = 1000;

    /*XML或代码配置的参数*/
    private int mVisibleChildCount;//屏幕中可见的child数量
    private int mLeftRightGapWidth;//左右相邻的两个child的间距
    private float mUpDownGapHeightPercent;//图片与其倒影的间距占图片高的比例
    private float mBottomImageHeightPercent;//图片的倒影占图片高的比例
    private VerticalGravity mGravity;//垂直方向上的对齐方式

    /*参数相关的变量*/
    private int mHalfVisibleChildCount;//mVisibleChildCount >> 1
    private int mHalfLeftRightGapWidth;//mLeftRightGapWidth >> 1
    private int mLoadChildCount;//mVisibleChildCount + 1，控件随手指滑动时，实际显示的child数量会多一个
    private int mHalfLoadchildCount;//mLoadChildCount >> 1

    /*系统测量的变量*/
    private Rect mPadding;//TODO 只是测量，未用
    private int mDisplayWidth;//屏幕宽度
    private int mDisplayHeight;//屏幕高度
    private Scroller mScroller;//获取偏移量用

    /*计算的变量*/
    //数据相关
    private BitmapLruCache mBitmapLruCache;//图片内存缓存
    private MultPagesAdapter mAdapter;//数据适配器
    private int mChildCount;//child的数量
    private int mTopImageWidth;//图片的宽度
    private int mTopImageHeight;//图片的高度
    private int mUpDownGapHeight;//图片与其倒影的间距
    private int mBottomImageWidth;//图片的倒影的宽度
    private int mBottomImageHeight;//图片的倒影的高度
    private int mChildWidth;//child的宽度
    private int mChildHeight;//child的高度
    //位置相关
    private int[] mMarginLeftArrays;//滑动定格时child距左端位置
    //缩放相关
    private float[] mScaleArrays;//滑动定格时child缩放比例
    private float mScaleOffsetTotal;//滑动定格时child缩放比例之和
    private Matrix mTopImageMatrix;//图片的缩放矩阵
    private Matrix mBottomImageMatrix;//图片的倒影的缩放矩阵
    //draw相关
    private Paint mPaint;//刷子
    private PaintFlagsDrawFilter mDrawFilter;//刷子的配置参数
    //touch相关
    private long mTouchStartTime;//触摸开始时间
    private float mTouchStartX;//触摸开始X轴位置
    private float mTouchStartY;//触摸开始Y轴位置
    private float mTouchStartScrollOffset;//触摸开始偏移量
    private float mScrollOffset;//当前偏移量
    private VelocityTracker mVelocityTracker;//计算速度用
    //动画相关
    private long mAnimationStartTime;//动画开始时间
    private float mAnimationTime;//动画执行时间
    private float mAnimationStartScrollOffset;//动画开始时偏移量
    private float mAnimationEndScrollOffset;//动画结束时偏移量
    private float mAnimationScrollOffset;//从当前偏移量到动画结束偏移量的间距
    private Runnable mAnimationRunnable;//动画执行线程

    /*用于交互的变量*/
    //可读可写
    public float scaleGradientDecrease = 0.1F;//梯度下降缩放比例
    public int childFlingVelocity = 8000;//1个child滑动所需的速度值
    public int maxFlingCount = 3;//惯性滑动时最大child数
    public float minFlingTime = 0.2F;//滑动动画最小执行时间
    public float flingSpeed = 5F;//1秒可以滑动几个child
    //只读
    private int mCurrentChildIndex;//滑动定格时处于屏幕中间的child的索引

    private class BitmapLruCache {
        LruCache<Integer, Bitmap> bitmapLruCache = new LruCache<Integer, Bitmap>(HardwareInfoUtil.getMemoryCacheSize(getContext(), 8)) {
            @Override
            protected int sizeOf(Integer key, Bitmap bitmap) {
                return BitmapUtil.getSize(bitmap);
            }

            @Override
            protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
                if (evicted && oldValue != null && !oldValue.isRecycled()) {
                    oldValue.recycle();
                }
            }
        };

        /**
         * 将图片添加到内存缓存
         *
         * @param key
         * @param bitmap
         */
        public void put(Integer key, Bitmap bitmap) {
            if (bitmap == null) {
                return;
            }

            bitmapLruCache.put(key, bitmap);
            Runtime.getRuntime().gc();
        }

        /**
         * 获取内存缓存中的图片
         *
         * @param key
         * @return
         */
        public Bitmap get(Integer key) {
            return bitmapLruCache.get(key);
        }

        /**
         * 移除内存缓存中的图片
         *
         * @param key
         * @return
         */
        public Bitmap remove(Integer key) {
            return bitmapLruCache.remove(key);
        }

        /**
         * 移除所有内存缓存中的图片
         */
        public void clear() {
            bitmapLruCache.evictAll();
            Runtime.getRuntime().gc();
        }
    }


    public MultPagesView(Context context) {
        this(context, null);
    }

    public MultPagesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultPagesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttribute(context, attrs);
        initData();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mScroller.computeScrollOffset()) {
                    mScroller.abortAnimation();
                    invalidate();
                }
                endAnimation();
                touchStarted(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                touchMoved(event);
                return true;
            case MotionEvent.ACTION_UP:
                touchEnded(event);
                return true;
        }

        return false;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mScroller.computeScrollOffset()) {
            mScrollOffset = (float) mScroller.getCurrX() / 100;
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mAdapter == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        mPadding.left = getPaddingLeft();
        mPadding.right = getPaddingRight();
        mPadding.top = getPaddingTop();
        mPadding.bottom = getPaddingBottom();

        mDisplayWidth = MeasureSpec.getSize(widthMeasureSpec);
        mDisplayHeight = MeasureSpec.getSize(heightMeasureSpec);

        //保证相邻两个child缩放后的间距相同，且静止时屏幕上显示的两端的child距离边界的距离为1/2间距
        mChildWidth = mBottomImageWidth = mTopImageWidth = (int) ((mDisplayWidth - mLeftRightGapWidth * mVisibleChildCount) / getVisibleScaleTotal());
        mTopImageHeight = mTopImageWidth;
        mUpDownGapHeight = (int) (mTopImageHeight * mUpDownGapHeightPercent);
        mBottomImageHeight = (int) (mTopImageHeight * mBottomImageHeightPercent);
        mChildHeight = mTopImageHeight + mUpDownGapHeight + mBottomImageHeight;

        mMarginLeftArrays = initMarginLeftArrays();

        setMeasuredDimension(mDisplayWidth, mDisplayHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //subclass should never override this method, because all of child will draw on the canvas directly
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mAdapter == null) {
            super.onDraw(canvas);
            return;
        }

        canvas.setDrawFilter(mDrawFilter);

//        mScrollOffset = 5.1F;

        for (int position = 0; position < mLoadChildCount; position++) {
            calculateChildMatrix(position);
            drawChild(canvas, position);
        }

//        Log.d(ConstantUtil.TAG, "mScrollOffset:" + mScrollOffset);
        if ((mScrollOffset - (int) mScrollOffset) == 0.0f) {
            mCurrentChildIndex = (getIndex2((int) mScrollOffset + mHalfLoadchildCount));
//            Log.d(ConstantUtil.TAG, "mCurrentChildIndex:" + mCurrentChildIndex);
        }

        super.onDraw(canvas);
    }

    /**
     * 设置数据的适配器
     *
     * @param adapter
     */
    public void setAdapter(MultPagesAdapter adapter) {
        if (adapter != null) {
            mAdapter = adapter;
            mChildCount = mAdapter.getCount();
            if (mBitmapLruCache != null) {
                mBitmapLruCache.clear();
            } else {
                mBitmapLruCache = new BitmapLruCache();
            }
        }
        mScrollOffset = 0;

        requestLayout();
    }

    /**
     * 获取静止时最中间的child的索引
     *
     * @return
     */
    public int getCurrentChildIndex() {
        return mCurrentChildIndex;
    }

    /**
     * 获取XML配置的参数
     *
     * @param context
     * @param attrs
     */
    private void initAttribute(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MultPagesView);

        mVisibleChildCount = typedArray.getInt(R.styleable.MultPagesView_visibleChildCount, 5);
        mHalfVisibleChildCount = mVisibleChildCount >> 1;
        mLoadChildCount = mVisibleChildCount + 1;
        mHalfLoadchildCount = mLoadChildCount >> 1;
        if (mVisibleChildCount % 2 == 0 || mVisibleChildCount < 3) {
            throw new IllegalArgumentException("visibleImageCount 必须是奇数");
        }

        mLeftRightGapWidth = typedArray.getDimensionPixelSize(R.styleable.MultPagesView_leftRightGapWidth, 10);
        mHalfLeftRightGapWidth = mLeftRightGapWidth >> 1;
        if (mLeftRightGapWidth < 0) {
            throw new IllegalArgumentException("leftRightGapWidth 必须大于0");
        }

        mUpDownGapHeightPercent = typedArray.getFraction(R.styleable.MultPagesView_upDownGapHeightPercent, 100, 0, 0.0f);
        if (mUpDownGapHeightPercent < 0) {
            throw new IllegalArgumentException("upDownGapHeightPercent 必须大于0");
        }
        mUpDownGapHeightPercent /= 100;

        mBottomImageHeightPercent = typedArray.getFraction(R.styleable.MultPagesView_bottomImageHeightPercent, 100, 0, 0.0f);
        if (mBottomImageHeightPercent < 0) {
            throw new IllegalArgumentException("bottomImageHeightPercent 必须大于0");
        }
        mBottomImageHeightPercent /= 100;

        mGravity = VerticalGravity.values()[typedArray.getInt(R.styleable.MultPagesView_gravity, VerticalGravity.CENTER.ordinal())];

        typedArray.recycle();
    }

    /**
     * 初始化
     */
    private void initData() {
        setWillNotDraw(false);
        setClickable(true);

        mTopImageMatrix = new Matrix();
        mBottomImageMatrix = new Matrix();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        mPadding = new Rect();
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mScroller = new Scroller(getContext(), new AccelerateDecelerateInterpolator());

        mScaleArrays = initScaleArrays();
    }

    /**
     * 初始化child缩放数组，中间的child不缩放，两边的child按比例递减或递增
     *
     * @return
     */
    private float[] initScaleArrays() {
        float[] floatArrays = new float[mLoadChildCount + 1];

        for (int i = 0; i < floatArrays.length; i++) {
            floatArrays[i] = 1 - scaleGradientDecrease * Math.abs(mHalfLoadchildCount - i);
        }

        return floatArrays;
    }

    /**
     * 初始化child位置数据
     *
     * @return
     */
    private int[] initMarginLeftArrays() {
        int[] floatArrays = new int[mLoadChildCount + 1];

        for (int i = 0; i < floatArrays.length; i++) {
            floatArrays[i] = (int) (-
                /*从左数显示的第一个child距屏幕左端距离*/
                    mHalfLeftRightGapWidth +
                /*该child之前的所有child的宽度和*/
                    mChildWidth * getScaleTotal(i) +
                /*该child之前的所有child之间的间隔 + 该child与前一个child之间的间隔*/
                    mLeftRightGapWidth * i);
        }

        return floatArrays;
    }

    /**
     * 画child
     *
     * @param canvas
     * @param absolutePosition
     */
    private void drawChild(Canvas canvas, int absolutePosition) {
        int index = getIndex(absolutePosition);
        Bitmap topImage = getTopImage(index);
        Bitmap bottomImage = getBottomImage(index, topImage);
        if (canvas != null) {
            if (topImage != null && !topImage.isRecycled()) {
                canvas.drawBitmap(topImage, mTopImageMatrix, mPaint);
            }
            if (bottomImage != null && !bottomImage.isRecycled()) {
                canvas.drawBitmap(bottomImage, mBottomImageMatrix, mPaint);
            }
        }
    }

    /**
     * 计算当前滑动位置下child缩放和位移矩阵
     *
     * @param absolutePosition
     */
    private void calculateChildMatrix(int absolutePosition) {
        mTopImageMatrix.reset();
        mBottomImageMatrix.reset();

        //缩放
        float scale = 1 - Math.abs(getScrollOffsetDelta(absolutePosition)) * scaleGradientDecrease;

        mTopImageMatrix.postScale(scale, scale);
        mBottomImageMatrix.postScale(scale, scale);

        //位移
        float scrollOffsetDelta = getScrollOffsetDelta2(absolutePosition);
        //用右边的child的marginLeft减去左边的child的marginLeft以获取两个child之间的距离
        int delta = mMarginLeftArrays[(int) Math.ceil(scrollOffsetDelta)] - mMarginLeftArrays[(int) scrollOffsetDelta];
        //计算偏移量占两个child的间距的比例
        float offset = scrollOffsetDelta - (int) scrollOffsetDelta;
        //初步计算位移
        int translateX = (int) (mMarginLeftArrays[getDynamicPosition(absolutePosition)] + delta * offset);
        //计算从最左端（屏幕外）到最右端（屏幕外）的距离
        int interval = mMarginLeftArrays[mMarginLeftArrays.length - 1] - mMarginLeftArrays[0];
        //如果超出边界，则进行位移补偿
        while (translateX < mMarginLeftArrays[0] || translateX > mMarginLeftArrays[mMarginLeftArrays.length - 1]) {
            if (translateX < mMarginLeftArrays[0]) {
                translateX += interval;
            } else if (translateX > mMarginLeftArrays[mMarginLeftArrays.length - 1]) {
                translateX -= interval;
            }
        }

        int topImageHeight = (int) (mTopImageHeight * scale);
        int upDownGapHeight = (int) (mUpDownGapHeight * scale);
        int bottomImageHeight = (int) (mBottomImageHeight * scale);
        int childHeight = (int) (mChildHeight * scale);
        int topImageTranslateY = 0;
        int upDownGapTranslateY = topImageHeight;
        int bottomImageTranslateY = upDownGapTranslateY + upDownGapHeight;
        int childTranslateY = 0;
        if (mGravity == VerticalGravity.TOP) {
            //TODO
        } else if (mGravity == VerticalGravity.CENTER) {
            childTranslateY = (mDisplayHeight - childHeight) >> 1;
        } else if (mGravity == VerticalGravity.BOTTOM) {
            //TODO
        }

        mTopImageMatrix.postTranslate(translateX, topImageTranslateY + childTranslateY);
        mBottomImageMatrix.postTranslate(translateX, bottomImageTranslateY + childTranslateY);
    }

    /**
     * 获取当前偏移量下实际应该显示的child的索引
     *
     * @param position
     * @return
     */
    private int getIndex(int position) {
        int index = position - mHalfLoadchildCount;

        int ceil = (int) Math.ceil(mScrollOffset);
        //滑动了几轮
        int turn = ceil / mLoadChildCount;
        //实际的便宜量
        int offset = ceil % mLoadChildCount;

        //滑动 1 轮即错位mChildCount - mLoadChildCount，需要补偿
        index -= turn * (mChildCount - mLoadChildCount);
        //与初始状态比，是向左滑动了
        if (offset > 0) {
            //对从左边＂消失＂，右边＂出现＂的child作补偿
            if (position < offset) {
                index -= mChildCount - mLoadChildCount;
            }
        } else
            //与初始状态比，是向右滑动了
            if (offset < 0) {
                //对从右边＂消失＂，左边＂出现＂的child作补偿
                offset += mLoadChildCount;
                if (position >= offset) {
                    index += mChildCount - mLoadChildCount;
                }
            }

        while (index < 0 || index >= mChildCount) {
            if (index < 0) {
                index += mChildCount;
            } else if (index >= mChildCount) {
                index -= mChildCount;
            }
        }

        return index;
    }

    /**
     * 获取当前偏移量下实际应该显示的child的索引
     *
     * @param position
     * @return
     */
    private int getIndex2(int position) {
//        Log.d(ConstantUtil.TAG, "--------------------------------------------");
//        Log.d(ConstantUtil.TAG, "position:" + position);

        int index = position - mHalfLoadchildCount;
//        Log.d(ConstantUtil.TAG, "index:" + index);

        int ceil = (int) Math.ceil(mScrollOffset);
//        Log.d(ConstantUtil.TAG, "ceil:" + ceil);
        //实际的便宜量
        int offset = ceil % mLoadChildCount;
//        Log.d(ConstantUtil.TAG, "offset:" + offset);

        //与初始状态比，是向左滑动了
        if (offset > 0) {
//            Log.d(ConstantUtil.TAG, "offset > 0");
            //对从左边＂消失＂，右边＂出现＂的child作补偿
            if (position < offset) {
//                Log.d(ConstantUtil.TAG, "position < offset");
                index -= mChildCount - mLoadChildCount;
//                Log.d(ConstantUtil.TAG, "index:" + index);
            }
        } else
            //与初始状态比，是向右滑动了
            if (offset < 0) {
//                Log.d(ConstantUtil.TAG, "offset < 0");
                //对从右边＂消失＂，左边＂出现＂的child作补偿
                offset += mLoadChildCount;
//                Log.d(ConstantUtil.TAG, "offset:" + offset);
                if (position >= offset) {
//                    Log.d(ConstantUtil.TAG, "position >= offset");
                    index += mChildCount - mLoadChildCount;
//                    Log.d(ConstantUtil.TAG, "index:" + index);
                }
            }

        while (index < 0 || index >= mChildCount) {
            if (index < 0) {
                index += mChildCount;
            } else if (index >= mChildCount) {
                index -= mChildCount;
            }
        }

        return index;
    }

    /**
     * 获取图片
     *
     * @param index
     * @return
     */
    private Bitmap getTopImage(int index) {
        Bitmap topImage = mBitmapLruCache.get(index);

        if (topImage == null || topImage.isRecycled()) {
            mBitmapLruCache.remove(index);
            topImage = BitmapUtil.getSpecifiedResolutionBitmap(mAdapter.getImage(index), mTopImageWidth, mTopImageHeight);
            if (topImage != null) {
                mBitmapLruCache.put(index, topImage);
            }
        }

        return topImage;
    }

    /**
     * 获取图片的倒影
     *
     * @param index
     * @param bitmap
     * @return
     */
    private Bitmap getBottomImage(int index, Bitmap bitmap) {
        index += 10000;
        Bitmap bottomImage = mBitmapLruCache.get(index);

        if (bottomImage == null || bottomImage.isRecycled()) {
            mBitmapLruCache.remove(index);
            bottomImage = BitmapUtil.getLinearGradientBitmap(BitmapUtil.getUpsideDownBitmap(bitmap, mBottomImageHeight));
            if (bottomImage != null) {
                mBitmapLruCache.put(index, bottomImage);
            }
        }

        return bottomImage;
    }

    /**
     * 当前位置与中间位置的偏移量(-mHalfLoadchildCount ~ mHalfLoadchildCount)
     *
     * @param absolutePosition
     * @return
     */
    private float getScrollOffsetDelta(int absolutePosition) {
        float scrollOffsetDelta = absolutePosition - mScrollOffset - mHalfLoadchildCount;

        while (scrollOffsetDelta < -mHalfLoadchildCount || scrollOffsetDelta > mHalfLoadchildCount) {
            if (scrollOffsetDelta < -mHalfLoadchildCount) {
                scrollOffsetDelta += mLoadChildCount;
            } else if (scrollOffsetDelta > mHalfLoadchildCount) {
                scrollOffsetDelta -= mLoadChildCount;
            }
        }

        return scrollOffsetDelta;
    }

    /**
     * 当前位置与中间位置的偏移量(0 ~ mLoadChildCount)
     *
     * @param absolutePosition
     * @return
     */
    private float getScrollOffsetDelta2(int absolutePosition) {
        float scrollOffsetDelta = absolutePosition - mScrollOffset;

        while (scrollOffsetDelta < 0 || scrollOffsetDelta > mLoadChildCount) {
            if (scrollOffsetDelta < 0) {
                scrollOffsetDelta += mLoadChildCount;
            } else if (scrollOffsetDelta > mLoadChildCount) {
                scrollOffsetDelta -= mLoadChildCount;
            }
        }

        return scrollOffsetDelta;
    }

    /**
     * 获取当前滑动偏移量下，child的实际位置
     *
     * @param absolutePosition
     * @return
     */
    private int getDynamicPosition(int absolutePosition) {
        int dynamicPosition = (int) (absolutePosition - Math.ceil(mScrollOffset));

        while (dynamicPosition < 0 || dynamicPosition > mLoadChildCount) {
            if (dynamicPosition < 0) {
                dynamicPosition += mLoadChildCount;
            } else if (dynamicPosition > mLoadChildCount) {
                dynamicPosition -= mLoadChildCount;
            }
        }

        return dynamicPosition;
    }

    /**
     * 获取可见的总的child的缩放比例（不计算第一个）
     *
     * @return
     */
    private float getVisibleScaleTotal() {
        if (mScaleOffsetTotal == 0.0F) {
            for (int i = 1; i < mScaleArrays.length - 1; i++) {
                mScaleOffsetTotal += mScaleArrays[i];
            }
        }
        return mScaleOffsetTotal;
    }

    /**
     * 获取所有的总的child的缩放比例（计算第一个）
     *
     * @param position
     * @return
     */
    private float getScaleTotal(int position) {
        float scaleTotal = -mScaleArrays[0];

        for (int i = 0; i < position; i++) {
            scaleTotal += mScaleArrays[i];
        }

        return scaleTotal;
    }

    /**
     * 手开始接触触摸板的回调
     *
     * @param event
     */
    private void touchStarted(MotionEvent event) {
        mAnimationStartTime = mTouchStartTime = AnimationUtils.currentAnimationTimeMillis();
        mTouchStartScrollOffset = mScrollOffset;
        mTouchStartX = event.getX();
        mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(event);
    }

    /**
     * 手保持接触触摸板的回调
     *
     * @param event
     */
    private void touchMoved(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        //需乘0.5，否则慢速全屏滑动会滑动2个child的偏移量
        float flingOffset = (mTouchStartX - event.getX()) / mDisplayWidth * maxFlingCount * 0.5f;
        mScrollOffset = mTouchStartScrollOffset + flingOffset;
        invalidate();
    }

    /**
     * 手离开触摸板的回调
     *
     * @param event
     */
    private void touchEnded(MotionEvent event) {
        if ((mScrollOffset - (int) mScrollOffset) != 0) {
            mVelocityTracker.addMovement(event);
            mVelocityTracker.computeCurrentVelocity(BASE_FLING_VELOCITY);
            initAnimation();
        }
        mVelocityTracker.clear();
        mVelocityTracker.recycle();
    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        if (mAnimationRunnable != null) {
            return;
        }

        mAnimationStartTime = AnimationUtils.currentAnimationTimeMillis();
        mAnimationStartScrollOffset = mScrollOffset;
        float alreadyScrollOffset = mAnimationStartScrollOffset - mTouchStartScrollOffset;

        float xVelocity = mVelocityTracker.getXVelocity();
        float totalScrollOffset = getTotalScrollOffset(xVelocity, 0);
//        Log.d(ConstantUtil.TAG, "----------------------------------------");
//        Log.d(ConstantUtil.TAG, "xVelocity:" + xVelocity);
//        Log.d(ConstantUtil.TAG, "totalScrollOffset:" + totalScrollOffset);
//        Log.d(ConstantUtil.TAG, "alreadyScrollOffset:" + alreadyScrollOffset);
        totalScrollOffset = (float) Math.floor(-totalScrollOffset + alreadyScrollOffset + 0.5F);
//        Log.d(ConstantUtil.TAG, "totalScrollOffset:" + totalScrollOffset);
        if (totalScrollOffset > maxFlingCount) {
            totalScrollOffset = maxFlingCount;
        } else if (totalScrollOffset < -maxFlingCount) {
            totalScrollOffset = -maxFlingCount;
        }

        mAnimationEndScrollOffset = mTouchStartScrollOffset + totalScrollOffset;
        mAnimationScrollOffset = totalScrollOffset - alreadyScrollOffset;
        mAnimationTime = Math.abs(mAnimationScrollOffset / flingSpeed);
        if (mAnimationTime < minFlingTime) {
            mAnimationTime = minFlingTime;
        }
        mAnimationRunnable = new Runnable() {
            @Override
            public void run() {
                startAnimation();
            }
        };
        post(mAnimationRunnable);
    }

    /**
     * 获取动画偏移量
     *
     * @param velocity
     * @param i
     * @return
     */
    private float getTotalScrollOffset(float velocity, int i) {
        int interval = childFlingVelocity << Math.abs(i);
        if (velocity > 0 && velocity - interval > 0) {
            i++;
            return getTotalScrollOffset(velocity - interval, i);
        } else if (velocity < 0 && velocity + interval < 0) {
            i--;
            return getTotalScrollOffset(velocity + interval, i);
        } else {
            return i + velocity / interval;
        }
    }

    /**
     * 执行动画
     */
    private void startAnimation() {
        //相邻两次重绘之间的时间间隔
        float elapsedTime = (AnimationUtils.currentAnimationTimeMillis() - mAnimationStartTime) / 1000F;
        if (elapsedTime < mAnimationTime) {
            updateAnimation(elapsedTime);
            post(mAnimationRunnable);
        } else {
            endAnimation();
        }
    }

    /**
     * 更新动画
     *
     * @param elapsedTime
     */
    private void updateAnimation(float elapsedTime) {
        if (elapsedTime > mAnimationTime) {
            elapsedTime = mAnimationTime;
        }
        mScrollOffset = mAnimationStartScrollOffset + elapsedTime / mAnimationTime * mAnimationScrollOffset;
        invalidate();
    }

    /**
     * 结束动画
     */
    private void endAnimation() {
        if (mAnimationRunnable != null) {
            removeCallbacks(mAnimationRunnable);
            mAnimationRunnable = null;
            //小数部分四舍五入
            mScrollOffset = mAnimationEndScrollOffset;
            invalidate();
        }
    }

    public void rollLeft() {
        if (mAnimationRunnable != null) {
            return;
        }

        mAnimationStartTime = AnimationUtils.currentAnimationTimeMillis();
        mAnimationStartScrollOffset = mScrollOffset;
        mAnimationScrollOffset = 1;
        mTouchStartScrollOffset = mScrollOffset;
        mAnimationEndScrollOffset = mTouchStartScrollOffset + mAnimationScrollOffset;
        mAnimationTime = Math.abs(mAnimationScrollOffset / flingSpeed);
        mAnimationRunnable = new Runnable() {
            @Override
            public void run() {
                startAnimation();
            }
        };
        post(mAnimationRunnable);
    }

    public void rollRight() {
        if (mAnimationRunnable != null) {
            return;
        }

        mAnimationStartTime = AnimationUtils.currentAnimationTimeMillis();
        mAnimationStartScrollOffset = mScrollOffset;
        mAnimationScrollOffset = -1;
        mTouchStartScrollOffset = mScrollOffset;
        mAnimationEndScrollOffset = mTouchStartScrollOffset + mAnimationScrollOffset;
        mAnimationTime = Math.abs(mAnimationScrollOffset / flingSpeed);
        mAnimationRunnable = new Runnable() {
            @Override
            public void run() {
                startAnimation();
            }
        };
        post(mAnimationRunnable);
    }
}
