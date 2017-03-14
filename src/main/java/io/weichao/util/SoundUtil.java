package io.weichao.util;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;

/**
 * Created by Administrator on 2016/11/8.
 */

public class SoundUtil {
    public static void raiseMusicAudio(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE,
                AudioManager.FLAG_SHOW_UI);
    }

    public static void lowerMusicAudio(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_LOWER,
                AudioManager.FLAG_SHOW_UI);
    }
}
