package com.example.prize;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Register_activity extends AppCompatActivity {

    private EditText full_name, email, phone_num, password, repeat_password;
    private TextView advance_button, register_button, back_register;
    private DBHelper DB;
    private ImageView mute;
    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_USERNAME = "username";
    private String username;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // קישור רכיבי ה-XML
        full_name = findViewById(R.id.fullname_register);
        email = findViewById(R.id.email_register);
        phone_num = findViewById(R.id.phone_register);
        password = findViewById(R.id.password_register);
        repeat_password = findViewById(R.id.password_repeat);
        register_button = findViewById(R.id.register_button);
        back_register = findViewById(R.id.back_register);
        advance_button = findViewById(R.id.advance_buttom);

        DB = new DBHelper(this);  // אתחול DBHelper

        // מעבר למסך זמני (לרשימת משתמשים לדוגמה)

        dbHelper = new DBHelper(this);  // אתחול DBHelper
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        // לחיצה על כפתור הרשמה
        register_button.setOnClickListener(v -> registerUser());

        // לחיצה על כפתור חזרה
        back_register.setOnClickListener(v -> startActivity(new Intent(this, Login_activity.class)));
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

    // פונקציה לרישום משתמש
    private void registerUser() {
        boolean trust = true;  // משתנה לבדיקה אם כל השדות תקינים

        // === בדיקות תקינות שדות ===
        if (TextUtils.isEmpty(full_name.getText().toString())) {
            full_name.setError("You must fill your name");
            trust = false;
        }
        if (TextUtils.isEmpty(repeat_password.getText().toString())) {
            repeat_password.setError("You must repeat the password");
            trust = false;
        }
        if (TextUtils.isEmpty(phone_num.getText().toString())) {
            phone_num.setError("You must fill your phone number");
            trust = false;
        } else if (!TextUtils.isDigitsOnly(phone_num.getText().toString())) {
            phone_num.setError("Invalid phone number");
            trust = false;
        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("You must fill the password");
            trust = false;
        } else if (!password.getText().toString().equals(repeat_password.getText().toString())) {
            password.setError("Passwords are not identical");
            password.setText("");
            repeat_password.setError("Passwords are not identical");
            repeat_password.setText("");
            trust = false;
        } else if (password.getText().toString().length() < 8) {
            password.setError("Password must be at least 8 characters");
            password.setText("");
            repeat_password.setText("");
            trust = false;
        }

        if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError("You must fill your email");
            trust = false;
        } else if (!EmailUtils.isValidEmail(email.getText().toString())) {
            email.setError("Invalid email address");
            email.setText("");
            trust = false;
        }

        // === אם הכל תקין, רישום המשתמש למסד ===
        if (trust) {
            User user = new User(
                    full_name.getText().toString(),
                    email.getText().toString(),
                    phone_num.getText().toString(),
                    password.getText().toString()
            );

            boolean checkInsertData = DB.insertUserdata(user);
            if (checkInsertData) {
                Toast.makeText(this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, Login_activity.class));  // מעבר להתחברות
            } else {
                Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
