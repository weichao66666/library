package io.weichao.controller;

import android.content.Context;
import android.widget.MediaController;

/**
 * Created by Administrator on 2016/6/13.
 */
public class ExoPlayerMediaController extends MediaController {
    public MediaPlayerControl playerControl;

    public ExoPlayerMediaController(Context context) {
        super(context);
    }

    @Override
    public void setMediaPlayer(MediaPlayerControl playerControl) {
        super.setMediaPlayer(playerControl);
        this.playerControl = playerControl;
    }
}
