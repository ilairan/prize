package com.example.prize;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import androidx.annotation.Nullable;

public class MusicService extends Service {

    private static MediaPlayer mediaPlayer;  /* נגן המוזיקה */
    private static final String CHANNEL_ID = "MusicServiceChannel";  /* ערוץ נוטיפיקציות (לא בשימוש כרגע) */

    @Override
    public void onCreate() {
        super.onCreate();
        /* אתחול הנגן אם הוא לא קיים */
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.background_music);  /* מוזיקה מהריסורסס */
            mediaPlayer.setLooping(true);  /* חזרה בלופ */
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /* הפעלת המוזיקה אם היא לא פועלת */
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        return START_STICKY;  /* ממשיך לרוץ גם אם המערכת סוגרת את השירות */
    }

    // שינוי ווליום המוזיקה (לשני הערוצים)
    public static void setMusicVolume(float volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }

    @Override
    public void onDestroy() {
        /* עצירה ושחרור הנגן כשמפסיקים את השירות */
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;  /* השירות לא תומך ב-Binding */
    }
}


