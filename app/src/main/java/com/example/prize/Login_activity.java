package com.example.prize;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;  /* ייבוא SharedPreferences */
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class Login_activity extends AppCompatActivity {

    private TextView login_button, register_login;  /* כפתורי התחברות והרשמה */
    private EditText email_login, password_login;   /* שדות קלט מייל וסיסמה */
    private DBHelper dbHelper;
    /* גישה למסד הנתונים */
    private String username;
    private ImageView mute;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // קישור רכיבי ה-XML
        email_login = findViewById(R.id.email_login);
        password_login = findViewById(R.id.password_login);
        register_login = findViewById(R.id.register_login);
        login_button = findViewById(R.id.login_button);

        dbHelper = new DBHelper(this);  // אתחול DBHelper
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);  // אתחול SharedPreferences

        // לחיצה על כפתור התחברות
        login_button.setOnClickListener(v -> attemptLogin());

        // לחיצה על "להירשם"
        register_login.setOnClickListener(v -> {
            startActivity(new Intent(Login_activity.this, Register_activity.class));  /* מעבר למסך הרשמה */
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

    // פונקציה לטיפול בתהליך ההתחברות
    private void attemptLogin() {
        String email = email_login.getText().toString();
        String password = password_login.getText().toString();

        // בדיקה אם השדות ריקים
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        // בדיקה אם המשתמש קיים במסד הנתונים
        User user = dbHelper.getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

            // שמירת שם המשתמש ב-SharedPreferences
            sharedPreferences.edit().putString(KEY_USERNAME, email).apply();

            // מעבר לתפריט הראשי
            startActivity(new Intent(this, Main_menu.class));
            finish();  // סגירת מסך ההתחברות
        } else {
            Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
        }
    }


}

