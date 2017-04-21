package io.weichao.pagetransformer;

import android.os.Build;
import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewPagerPageTransformer implements PageTransformer {
    public int backgroundId;
    public int frontImageId;
    public int textId;

    private static final float BACKGROUND_SPEED = 0.4F;
    private static final float FRONT_IMAGE_SPEED = 0.1F;
    private static final float SCALE_START = 0.9F;
    private static final float SCALE_END = 1.0F;

    @Override
    public void transformPage(View view, float position) {
        ImageView backgroundIv = (ImageView) view.findViewById(backgroundId);
        ImageView frontImageIv = (ImageView) view.findViewById(frontImageId);
        TextView textTv = (TextView) view.findViewById(textId);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB && backgroundIv != null) {
            if (position > -1 && position < 1) {
                float offset = -position * backgroundIv.getWidth();
                float scale = (1 - Math.abs(position)) * (SCALE_END - SCALE_START) + SCALE_START;

                backgroundIv.setTranslationX(offset * BACKGROUND_SPEED);

                if (frontImageIv != null) {
                    frontImageIv.setTranslationX(offset * FRONT_IMAGE_SPEED);
                    frontImageIv.setScaleX(scale);
                    frontImageIv.setScaleY(scale);
                }

                if (textTv != null) {
                    textTv.setScaleX(scale);
                    textTv.setScaleY(scale);
                }
            }
        }
    }
}