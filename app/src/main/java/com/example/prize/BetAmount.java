package com.example.prize;

import android.content.Context;
import android.content.SharedPreferences;  // שימוש ב-SharedPreferences
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.fragment.app.FragmentTransaction;

public class BetAmount extends Fragment {

    private int playerScore;
    private int selectedBet = 10; // ההימור המינימלי

    private SharedPreferences sharedPreferences;
    private DBHelper dbHelper;

    private static final String PREF_NAME = "user_prefs";  // שם קובץ ה-SharedPreferences
    private static final String KEY_USERNAME = "username"; // מפתח לשם המשתמש

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bet_amount, container, false);

        // אתחול DBHelper ו-SharedPreferences
        dbHelper = new DBHelper(getContext());
        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // שליפת שם המשתמש מה-SharedPreferences
        String username = sharedPreferences.getString(KEY_USERNAME, null);

        // קביעת ניקוד השחקן מהמסד לפי שם המשתמש
        if (username != null) {
            playerScore = dbHelper.getScore(username);  // שליפת הניקוד מהמסד
        } else {
            playerScore = 100;  // ברירת מחדל אם אין שם משתמש
        }

        // אתחול תצוגות
        TextView scoreTextView = view.findViewById(R.id.scoreTextView); // הצגת הניקוד בפינה הימנית
        SeekBar seekBar = view.findViewById(R.id.seekbar);              // סרגל בחירה של ההימור
        TextView scoredisplay = view.findViewById(R.id.scoredisplay);   // תצוגת סכום ההימור הנבחר
        TextView continueButton = view.findViewById(R.id.continue_button); // כפתור להמשיך

        // הצגת ניקוד השחקן
        scoreTextView.setText("" + playerScore);

        // הגדרת סרגל ההימור
        int maxBet = Math.max(playerScore - 10, 0); // הגבלת הימור מירבי
        seekBar.setMax(maxBet);
        seekBar.setProgress(0);
        scoredisplay.setText(String.valueOf(selectedBet)); // תצוגה ראשונית של 10

        // מאזין לסרגל
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedBet = progress + 10; // הוספת ערך מינימלי
                scoredisplay.setText(String.valueOf(selectedBet)); // עדכון תצוגה
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // לחיצה על כפתור ההמשך
        continueButton.setOnClickListener(v -> {
            ShakeToMixFragment shakeFragment = new ShakeToMixFragment();
            Bundle args = new Bundle();
            args.putInt("betAmount", selectedBet);   // העברת סכום ההימור
            args.putInt("score", playerScore);       // העברת הניקוד הנוכחי
            shakeFragment.setArguments(args);

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, shakeFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }
}


