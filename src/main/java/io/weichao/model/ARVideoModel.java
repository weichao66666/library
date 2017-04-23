package io.weichao.model;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import io.weichao.view.GLVideoView;

public class ARVideoModel extends BaseModel {
    private final Activity mActivity;

    public RelativeLayout view;

    private GLVideoView mGLVideoView;

    public ARVideoModel(Activity activity) {
        mActivity = activity;

        view = new RelativeLayout(activity);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mGLVideoView = new GLVideoView(activity);
        view.addView(mGLVideoView);
    }

    @Override
    public void onResume() {
        if (mGLVideoView != null) {
            mGLVideoView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mGLVideoView != null) {
            mGLVideoView.onPause();
        }
    }
}
