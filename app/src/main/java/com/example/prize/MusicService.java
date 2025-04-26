package com.example.prize;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MusicService extends Service {

    private static MediaPlayer mediaPlayer;
    private static final String CHANNEL_ID = "MusicServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
            mediaPlayer.setLooping(true);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        return START_STICKY;
    }

    public static void setMusicVolume(float volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);  // Apply volume to both channels
        }
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}

