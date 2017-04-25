package io.weichao.domain;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import io.weichao.util.MathUtil;

public class IconMarker extends Marker {
    private Bitmap bitmap = null;

    public IconMarker(String name, double latitude, double longitude, double altitude, int color, Bitmap bitmap) {
        super(name, latitude, longitude, altitude, color);
        this.bitmap = bitmap;
    }

    @Override
    public void drawIcon(Canvas canvas) {
        if (canvas == null || bitmap == null) {
            throw new NullPointerException();
        }

        if (gpsSymbol == null) {
            gpsSymbol = new PaintableIcon(bitmap, 96, 96);
        }

        textXyzRelativeToCameraView.get(textArray);
        symbolXyzRelativeToCameraView.get(symbolArray);

        float currentAngle = MathUtil.getAngle(symbolArray[0], symbolArray[1], textArray[0], textArray[1]);
        float angle = currentAngle + 90;

        if (symbolContainer == null)
            symbolContainer = new PaintablePosition(gpsSymbol, symbolArray[0], symbolArray[1], angle, 1);
        else symbolContainer.set(gpsSymbol, symbolArray[0], symbolArray[1], angle, 1);

        symbolContainer.paint(canvas);
    }
}