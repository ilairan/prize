package com.example.prize;

import android.content.SharedPreferences;  // נוסיף ספרייה ל-SharedPreferences
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class Main_menu extends AppCompatActivity {

    FrameLayout frameLayout;
    TextView settingsbutton;
    TextView startbutton;
    TextView button3;
    TextView textView;
    TextView scoreTextView;
    DBHelper dbHelper;

    // משתנים ל-SharedPreferences
    SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // קבלת רכיבי ה-XML
        frameLayout = findViewById(R.id.frameLayout);
        settingsbutton = findViewById(R.id.Settingsbutton);
        startbutton = findViewById(R.id.startbutton);
        button3 = findViewById(R.id.button3);
        textView = findViewById(R.id.main_menu);
        scoreTextView = findViewById(R.id.scoreTextView);

        // אתחול מסד נתונים
        dbHelper = new DBHelper(this);

        // אתחול SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // קבלת המשתמש המחובר (המייל)
        String username = sharedPreferences.getString(KEY_USERNAME, null);

        // בדיקה אם יש משתמש מחובר
        if (username != null) {
            int score = dbHelper.getScore(username);  // קבלת הניקוד מהמסד
            scoreTextView.setText("Score: " + score);  // הצגת הניקוד במסך הראשי
        } else {
            scoreTextView.setText("Score: N/A");  // אין משתמש מחובר
        }

        // כפתור הגדרות
        settingsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SettingsFragment());
                toggleVisibility(false);  // הסתרת התפריט הראשי
            }
        });

        // כפתור התחל משחק
        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BetAmount betAmount = new BetAmount();
                Bundle args = new Bundle();

                // בדיקה שוב למקרה שהמשתמש קיים
                String username = sharedPreferences.getString(KEY_USERNAME, null);
                if (username != null) {
                    args.putString("username", username);  // שליחת שם המשתמש
                    args.putInt("score", dbHelper.getScore(username));  // שליחת הניקוד
                }

                betAmount.setArguments(args);
                loadFragment(betAmount);
                toggleVisibility(false);  // הסתרת התפריט הראשי
            }
        });

        // כפתור נוסף (סתם דוגמה)
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ShakeToMixFragment());
                toggleVisibility(false);  // הסתרת התפריט הראשי
            }
        });
    }

    // טעינת פרגמנט למסך
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .addToBackStack(null)
                .commit();
    }

    // פונקציה לשינוי הנראות של המסכים
    private void toggleVisibility(boolean showMainMenu) {
        frameLayout.setVisibility(showMainMenu ? View.GONE : View.VISIBLE);
        settingsbutton.setVisibility(showMainMenu ? View.VISIBLE : View.GONE);
        startbutton.setVisibility(showMainMenu ? View.VISIBLE : View.GONE);
        button3.setVisibility(showMainMenu ? View.VISIBLE : View.GONE);
        textView.setVisibility(showMainMenu ? View.VISIBLE : View.GONE);
        scoreTextView.setVisibility(showMainMenu ? View.VISIBLE : View.GONE);  // ADD THIS!
    }
}

