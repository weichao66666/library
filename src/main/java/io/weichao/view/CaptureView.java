package io.weichao.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;

import java.util.LinkedList;

import io.weichao.library.R;

public class CaptureView extends View {
    private class PossiblePoint {
        private float x, y;
        private long foundTime;
    }

    private static final int POSSIBLE_POINT_COLOR = 0xC0FFFF00;

    private static final int POSSIBLE_POINT_ALIVE_MS = 200;
    private static final int SCANNER_DURATION = 2000;
    private long startTime = -1;

    private Rect frame;
    private Paint paint;
    private LinkedList<PossiblePoint> possiblePoints;
    private Drawable frameDrawable, scannerDrawable;
    private int scannerHeight = 0;

    public CaptureView(Context context) {
        this(context, null, 0);
    }

    public CaptureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        frame = new Rect();
        paint = new Paint();
        paint.setAntiAlias(true);
        possiblePoints = new LinkedList<>();
        frameDrawable = getResources().getDrawable(R.drawable.qrcode_scan_frame);
        scannerDrawable = getResources().getDrawable(R.drawable.qrcode_scan_scaner);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int length = (int) (width * 0.6);
        frame.left = width / 2 - length / 2;
        frame.right = width / 2 + length / 2;
        frame.top = height / 2 - length / 2;
        frame.bottom = height / 2 + length / 2;
        frameDrawable.setBounds(frame.left - 10, frame.top - 10, frame.right + 10, frame.bottom + 10);
        scannerHeight = scannerDrawable.getIntrinsicHeight() * frame.width() / scannerDrawable.getIntrinsicWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw possible points
        paint.setColor(POSSIBLE_POINT_COLOR);
        paint.setStyle(Style.FILL);
        long current = System.currentTimeMillis();
        while (possiblePoints.size() > 0 && current - possiblePoints.peek().foundTime >= POSSIBLE_POINT_ALIVE_MS) {
            possiblePoints.poll();
        }
        for (int i = 0; i < possiblePoints.size(); i++) {
            PossiblePoint point = possiblePoints.get(i);
            int radius = (int) (5 * (POSSIBLE_POINT_ALIVE_MS - current + point.foundTime) / POSSIBLE_POINT_ALIVE_MS);
            if (radius > 0) {
                canvas.drawCircle(frame.left + point.x, frame.top + point.y, radius, paint);
            }
        }

        // Draw scanner
        long now = System.currentTimeMillis();
        if (startTime < 0) {
            startTime = now;
        }
        int timePast = (int) ((now - startTime) % SCANNER_DURATION);
        if (timePast >= 0 && timePast <= SCANNER_DURATION / 2) {
            int scannerShift = frame.height() * 2 * timePast / SCANNER_DURATION;
            canvas.save();
            canvas.clipRect(frame);
            scannerDrawable.setBounds(frame.left, frame.top + scannerShift, frame.right, frame.top + scannerHeight + scannerShift);
            scannerDrawable.draw(canvas);
            canvas.restore();
        }
        // Draw frame
        frameDrawable.draw(canvas);

        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        PossiblePoint pp = new PossiblePoint();
        pp.foundTime = System.currentTimeMillis();
        pp.x = point.getX();
        pp.y = point.getY();
        if (possiblePoints.size() >= 10) {
            possiblePoints.poll();
        }
        possiblePoints.add(pp);
    }
}
