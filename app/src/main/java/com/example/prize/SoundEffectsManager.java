// SoundEffectsManager.java
package com.example.prize;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

public class SoundEffectsManager {
    private SoundPool soundPool;
    private int cardDrawSound, winSound, loseSound;

    public SoundEffectsManager(Context context) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(audioAttributes)
                .build();

        cardDrawSound = soundPool.load(context, R.raw.card_draw, 1);
        winSound = soundPool.load(context, R.raw.win, 1);
        loseSound = soundPool.load(context, R.raw.lose, 1);
    }

    public void playCardDraw() {
        soundPool.play(cardDrawSound, 1, 1, 0, 0, 1);
    }

    public void playWin() {
        soundPool.play(winSound, 1, 1, 0, 0, 1);
    }

    public void playLose() {
        soundPool.play(loseSound, 1, 1, 0, 0, 1);
    }

    public void release() {
        soundPool.release();
    }
}
