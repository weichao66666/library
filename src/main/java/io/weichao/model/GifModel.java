package io.weichao.model;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.io.IOException;

import io.weichao.library.R;
import io.weichao.util.SharedPreferenceUtil;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2016/10/11.
 */

public class GifModel extends BaseModel {
    private static final String POSITION = "GifDrawable_CurrentPosition";

    public RelativeLayout view;

    private Activity mActivity;
    private GifDrawable mGifDrawable;

    public GifModel(Activity activity) {
        mActivity = activity;

        view = new RelativeLayout(activity);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        GifImageView gifImageView = new GifImageView(activity);
        try {
            mGifDrawable = new GifDrawable(mActivity.getResources(), R.drawable.run);
        } catch (IOException e) {
            e.printStackTrace();
        }
        gifImageView.setImageDrawable(mGifDrawable);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        gifImageView.setLayoutParams(layoutParams);

        view.addView(gifImageView);
    }

    @Override
    public void onResume() {
        if (mGifDrawable != null) {
            mGifDrawable.seekTo(SharedPreferenceUtil.getInt(mActivity, POSITION));
            if (!mGifDrawable.isRunning()) {
                mGifDrawable.start();
            }
        }
    }

    @Override
    public void onPause() {
        if (mGifDrawable != null) {
            SharedPreferenceUtil.put(mActivity, POSITION, mGifDrawable.getCurrentPosition());
            if (mGifDrawable.canPause()) {
                mGifDrawable.pause();
            }
        }
    }

    @Override
    public void onDestroy() {
        SharedPreferenceUtil.put(mActivity, POSITION, 0);
        if (mGifDrawable != null) {
            mGifDrawable.stop();
            if (!mGifDrawable.isRecycled()) {
                mGifDrawable.recycle();
            }
        }
    }
}
