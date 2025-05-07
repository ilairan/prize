package com.example.prize;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView continue_button;  // כפתור המשך
    private ImageView mute;  // כפתור מנחה

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // התחלת שירות המוזיקה אם הוא לא כבר רץ
        if (!isMyServiceRunning(MusicService.class)) {
            startService(new Intent(this, MusicService.class));  // התחלת השירות
        }

        // בקשת הרשאה להודעות (באנדרואיד 13 ומעלה)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        // אתחול SharedPreferences ו-DBHelper
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);  // שליפת שם המשתמש
        DBHelper dbHelper = new DBHelper(this);

        // טיפול בלחיצה על כפתור "המשך"
        continue_button = findViewById(R.id.continue_button);
        continue_button.setOnClickListener(v -> {
            if (username != null) {  // אם המשתמש מחובר
                // טעינת העדפות ווליום של המשתמש מהמאגרים
                int musicVolume = dbHelper.getMusicVolume(username);
                int sfxVolume = dbHelper.getSoundEffectsVolume(username);

                // הגדרת עוצמת המוזיקה בשירות
                MusicService.setMusicVolume(musicVolume / 100f);  // המרה לאחוזים (0.0-1.0)

                // מעבר למסך הראשי (Main_menu)
                startActivity(new Intent(MainActivity.this, Main_menu.class));
                finish();  // סגירת ה-Activity הנוכחי
            } else {
                // אם המשתמש לא מחובר, מעבר למסך התחברות (Login_activity)
                startActivity(new Intent(MainActivity.this, Login_activity.class));
            }
        });

        mute = findViewById(R.id.mute);
        mute.setOnClickListener(v -> {
            // המרה לאחוזים (0.0-1.0)
            if (mute.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.soundoff).getConstantState()) {
                mute.setImageDrawable(getResources().getDrawable(R.drawable.soundon));
                if (username != null) {  // אם המשתמש מחובר
                    // טעינת העדפות ווליום של המשתמש מהמאגרים
                    int musicVolume = dbHelper.getMusicVolume(username);
                    // הגדרת עוצמת המוזיקה בשירות
                    MusicService.setMusicVolume(musicVolume / 100f);
                } else {
                    MusicService.setMusicVolume(100 / 100f);
                }
            }
            else {
                mute.setImageDrawable(getResources().getDrawable(R.drawable.soundoff));
                MusicService.setMusicVolume(0 / 100f);  // המרה לאחוזים (0.0-1.0)
            }
        });



    }

    // בדיקה אם שירות רץ ברקע
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;  // השירות רץ
                }
            }
        }
        return false;  // השירות לא רץ
    }

    // טיפול בתוצאה של בקשת ההרשאות
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // ההרשאה ניתנה (אין צורך בפעולה נוספת כרגע)
        } else {
            // ההרשאה נדחתה (אפשר להוסיף הודעה למשתמש אם רוצים)
        }
    }
}




