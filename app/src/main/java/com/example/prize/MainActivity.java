package com.example.prize;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    TextView continue_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start Music Service if not already running
        if (!isMyServiceRunning(MusicService.class)) {
            Intent serviceIntent = new Intent(this, MusicService.class);
            startService(serviceIntent);
        }
 // Don't use startForegroundService here

        // Request notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        // Schedule daily notifications
        scheduleDailyNotification(this, "Don’t forget to collect your daily bonus!", 10, 0, 100);
        scheduleDailyNotification(this, "You always quit right before you win.", 18, 0, 101);

        // Continue button logic
        continue_button = findViewById(R.id.continue_button);
        continue_button.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            String username = sharedPreferences.getString("username", null);

            if (username != null) {
                // Auto-login to Main_menu
                Intent intent = new Intent(MainActivity.this, Main_menu.class);
                startActivity(intent);
                finish();
            } else {
                // Go to Login_activity
                Intent intent = new Intent(MainActivity.this, Login_activity.class);
                startActivity(intent);
            }
        });
    }

    // Start MusicService (if not running)


    // Schedule a daily notification at the given time
    private void scheduleDailyNotification(Context context, String message, int hour, int minute, int requestCode) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("notification_message", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // If the time has already passed today, schedule for tomorrow
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

    // Handle notification permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied (optional: show a toast)
            }
        }
    }



    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}



