package com.example.prize;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;  // נוסיף ספרייה לשמירת משתמש
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class Login_activity extends AppCompatActivity {

    TextView login_button;
    EditText email_login;
    EditText password_login;
    TextView register_login;
    DBHelper dbHelper;

    // הגדרת SharedPreferences
    SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // קבלת רכיבים מה-XML
        email_login = findViewById(R.id.email_login);
        password_login = findViewById(R.id.password_login);
        register_login = findViewById(R.id.register_login);
        login_button = findViewById(R.id.login_button);

        // יצירת מופע של DBHelper
        dbHelper = new DBHelper(this);

        // אתחול SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // האזנה ללחיצה על כפתור התחברות
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // קבלת המייל והסיסמה מהשדות
                String email = email_login.getText().toString();
                String password = password_login.getText().toString();

                // בדיקה אם המייל ריק
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login_activity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                // בדיקה אם הסיסמה ריקה
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login_activity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean found = false;

                // בדיקה אם המשתמש קיים במסד הנתונים
                List<User> users = dbHelper.getAllUsers();
                for (User user : users) {
                    if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                        found = true;
                        break;
                    }
                }

                if (found) {
                    Toast.makeText(Login_activity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    // שמירת המשתמש המחובר ב-SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KEY_USERNAME, email);  // שמירה לפי המייל
                    editor.apply();

                    // מעבר לתפריט הראשי
                    Intent intent = new Intent(Login_activity.this, Main_menu.class);
                    startActivity(intent);
                    finish();  // סגירת מסך ההתחברות כך שלא יחזור אחורה
                } else {
                    Toast.makeText(Login_activity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // האזנה ללחיצה על "להירשם"
        register_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // מעבר למסך ההרשמה
                Intent intent = new Intent(Login_activity.this, Register_activity.class);
                startActivity(intent);
            }
        });
    }
}

