package com.example.prize;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

public class SoundEffectsManager {
    private SoundPool soundPool;
    private int cardDrawSound, winSound, loseSound;
    private float sfxVolume = 1.0f; // Default full volume

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

        // === Load volume from DB ===
        DBHelper dbHelper = new DBHelper(context);
        String userEmail = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                .getString("username", null);
        if (userEmail != null) {
            int volume = dbHelper.getSoundEffectsVolume(userEmail);
            sfxVolume = volume / 100f;  // Normalize (0.0 to 1.0)
        }
    }

    public void setSfxVolume(int volume) {
        this.sfxVolume = volume / 100f; // Allow manual override
    }

    public void playCardDraw() {
        soundPool.play(cardDrawSound, sfxVolume, sfxVolume, 0, 0, 1);
    }

    public void playWin() {
        soundPool.play(winSound, sfxVolume, sfxVolume, 0, 0, 1);
    }

    public void playLose() {
        soundPool.play(loseSound, sfxVolume, sfxVolume, 0, 0, 1);
    }

    public void release() {
        soundPool.release();
    }
}
