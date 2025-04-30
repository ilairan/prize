package com.example.prize;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

public class SoundEffectsManager {

    private SoundPool soundPool;        /* ניהול השמעת אפקטים */
    private int cardDrawSound, winSound, loseSound;  /* מזהים של הקבצים */
    private float sfxVolume = 1.0f;     /* ווליום ברירת מחדל לאפקטים */
    private static float globalVolume = 1.0f;  /* ווליום גלובלי (אפשר לשנות בעתיד) */

    // בנאי - טוען אפקטים ומביא את הווליום מהמסד
    public SoundEffectsManager(Context context) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(audioAttributes)
                .build();

        /* טעינת קבצי האפקטים */
        cardDrawSound = soundPool.load(context, R.raw.card_draw, 1);
        winSound = soundPool.load(context, R.raw.win, 1);
        loseSound = soundPool.load(context, R.raw.lose, 1);

        // === שליפת הווליום מהמסד ===
        DBHelper dbHelper = new DBHelper(context);
        String userEmail = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                .getString("username", null);
        if (userEmail != null) {
            int volume = dbHelper.getSoundEffectsVolume(userEmail);
            sfxVolume = volume / 100f;  /* נורמליזציה (0.0 עד 1.0) */
        }
    }

    // שינוי ווליום אפקטים ידני
    public void setSfxVolume(int volume) {
        this.sfxVolume = volume / 100f;
    }

    // שינוי ווליום גלובלי לכל האפקטים (לא בשימוש כרגע, אך מוכן)
    public static void setGlobalVolume(float volume) {
        globalVolume = volume;
    }

    // השמעת אפקט שליפת קלף
    public void playCardDraw() {
        soundPool.play(cardDrawSound, sfxVolume * globalVolume, sfxVolume * globalVolume, 0, 0, 1);
    }

    // השמעת אפקט ניצחון
    public void playWin() {
        soundPool.play(winSound, sfxVolume * globalVolume, sfxVolume * globalVolume, 0, 0, 1);
    }

    // השמעת אפקט הפסד
    public void playLose() {
        soundPool.play(loseSound, sfxVolume * globalVolume, sfxVolume * globalVolume, 0, 0, 1);
    }

    // שחרור המשאבים של ה-SoundPool
    public void release() {
        soundPool.release();
    }
}

