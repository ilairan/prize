package com.example.prize;

import android.app.AlertDialog;
import android.content.SharedPreferences;  /* ייבוא SharedPreferences */
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class Main_menu extends AppCompatActivity {

    // משתני ממשק
    private FrameLayout frameLayout;
    private ImageView mute;
    private TextView settingsbutton, startbutton, button3, textView, scoreTextView, medals;

    private DBHelper dbHelper;  // גישה למסד הנתונים
    private SharedPreferences sharedPreferences;  // גישה להעדפות משתמש

    private static final String PREF_NAME = "user_prefs";  // שם קובץ ההעדפות
    private static final String KEY_USERNAME = "username";  // מפתח שם המשתמש

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // קישור רכיבי ה-XML
        frameLayout = findViewById(R.id.frameLayout);
        settingsbutton = findViewById(R.id.Settingsbutton);
        startbutton = findViewById(R.id.startbutton);
        button3 = findViewById(R.id.button3);
        textView = findViewById(R.id.main_menu);
        scoreTextView = findViewById(R.id.scoreTextView);
        medals = findViewById(R.id.Medals);

        dbHelper = new DBHelper(this);  // אתחול DBHelper
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);  // אתחול SharedPreferences

        String username = sharedPreferences.getString(KEY_USERNAME, null);  // שליפת שם המשתמש

        // הצגת ניקוד אם יש משתמש מחובר
        if (username != null) {
            int score = dbHelper.getScore(username);  // שליפת הניקוד
            scoreTextView.setText(String.valueOf(score));  // הצגת הניקוד
        } else {
            scoreTextView.setText("Score: N/A");  // אין משתמש מחובר
        }

        // לחיצה על כפתור הגדרות
        settingsbutton.setOnClickListener(v -> {
            loadFragment(new SettingsFragment());  // טעינת פרגמנט הגדרות
            toggleVisibility(false);  // הסתרת תפריט ראשי
        });

        // לחיצה על כפתור מדליות (הישגים)
        medals.setOnClickListener(v -> {
            loadFragment(new AchievementsFragment());  // טעינת פרגמנט הישגים
            toggleVisibility(false);  // הסתרת תפריט ראשי
        });

        // לחיצה על כפתור התחל משחק
        startbutton.setOnClickListener(v -> {
            showInstructionsDialog();
            String user = sharedPreferences.getString(KEY_USERNAME, null);  // שליפת שם המשתמש
            BetAmount betAmount = new BetAmount();
            loadFragment(betAmount);  // טעינת פרגמנט בחירת הימור
            toggleVisibility(false);  // הסתרת תפריט ראשי
        });

        // לחיצה על כפתור סטטיסטיקות
        button3.setOnClickListener(v -> {
            loadFragment(new StatisticsFragment());  // טעינת פרגמנט סטטיסטיקות
            toggleVisibility(false);  // הסתרת תפריט ראשי
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

    // פונקציה לטעינת פרגמנט
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .addToBackStack(null)
                .commit();
    }

    // פונקציה לשינוי הנראות של האלמנטים במסך
    private void toggleVisibility(boolean showMainMenu) {
        int mainMenuVisibility = showMainMenu ? View.VISIBLE : View.GONE;
        int frameVisibility = showMainMenu ? View.GONE : View.VISIBLE;

        frameLayout.setVisibility(frameVisibility);
        settingsbutton.setVisibility(mainMenuVisibility);
        startbutton.setVisibility(mainMenuVisibility);
        button3.setVisibility(mainMenuVisibility);
        textView.setVisibility(mainMenuVisibility);
        scoreTextView.setVisibility(mainMenuVisibility);
    }

    private void showInstructionsDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.instructions, null);

        TextView instructionsText = dialogView.findViewById(R.id.instructions_text);
        instructionsText.setText("Welcome to Blackjack!\n\n" +
                "Goal: Get as close as possible to 21 without going over. If you go over 21, you lose.\n\n" +
                "You start with two cards. The dealer also gets two cards (one is hidden).\n\n" +
                "Press 'Hit' if you want another card.\n" +
                "Press 'Stand' if you're happy with your total and want to end your turn.\n\n" +
                "Once you stand, the dealer will reveal their hidden card and draw more cards until reaching at least 17.\n\n" +
                "Whoever is closer to 21 without going over wins. Good luck!");

        new AlertDialog.Builder(this)
                .setTitle("How to Play")
                .setView(dialogView)
                .setPositiveButton("Continue", (dialog, which) -> {
                    String user = sharedPreferences.getString(KEY_USERNAME, null);
                    BetAmount betAmount = new BetAmount();
                    loadFragment(betAmount);
                    toggleVisibility(false);
                })
                .setCancelable(false)
                .show();
    }




}



