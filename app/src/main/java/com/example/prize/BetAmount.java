package com.example.prize;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;  /* שימוש ב-SharedPreferences */
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.fragment.app.FragmentTransaction;

public class BetAmount extends Fragment {

    private int playerScore;  /* ניקוד השחקן */
    private int selectedBet = 10;  /* ההימור המינימלי */

    private SharedPreferences sharedPreferences;
    private DBHelper dbHelper;

    private static final String PREF_NAME = "user_prefs";  /* שם קובץ ה-SharedPreferences */
    private static final String KEY_USERNAME = "username"; /* מפתח לשם המשתמש */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bet_amount, container, false);

        /* התחלת שירות המוזיקה (במידה והוא לא רץ) */
        Intent musicIntent = new Intent(requireActivity(), MusicService.class);
        requireActivity().startService(musicIntent);

        /* אתחול DBHelper ו-SharedPreferences */
        dbHelper = new DBHelper(getContext());
        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(KEY_USERNAME, "defaultUser");  /* שליפת שם המשתמש */

        /* שליפת ניקוד השחקן */
        playerScore = dbHelper.getScore(username);

        /* קישור רכיבי ה-XML */
        TextView scoreTextView = view.findViewById(R.id.scoreTextView);       /* תצוגת ניקוד */
        SeekBar seekBar = view.findViewById(R.id.seekbar);                    /* סרגל בחירת הימור */
        TextView scoredisplay = view.findViewById(R.id.scoredisplay);         /* תצוגת סכום ההימור */
        TextView continueButton = view.findViewById(R.id.continue_button);    /* כפתור המשך */

        /* הצגת ניקוד השחקן */
        scoreTextView.setText(String.valueOf(playerScore));

        /* הגדרת סרגל ההימור */
        int maxBet = Math.max(playerScore - 10, 0);  /* הגבלת ההימור המקסימלי */
        seekBar.setMax(maxBet);
        seekBar.setProgress(0);
        scoredisplay.setText(String.valueOf(selectedBet));  /* תצוגה ראשונית */

        /* מאזין לשינויים בסרגל */
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedBet = progress + 10;  /* הוספת ערך מינימלי */
                scoredisplay.setText(String.valueOf(selectedBet));  /* עדכון תצוגה */
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        /* לחיצה על כפתור המשך */
        continueButton.setOnClickListener(v -> {
            /* עדכון ההימור הגבוה ביותר במסד */
            int largestBet = dbHelper.getLargestBet(username);
            if (selectedBet > largestBet) {
                dbHelper.updateLargestBet(username, selectedBet);
            }

            /* מעבר לפרגמנט ShakeToMix עם העברת ההימור והניקוד */
            ShakeToMixFragment shakeFragment = new ShakeToMixFragment();
            Bundle args = new Bundle();
            args.putInt("betAmount", selectedBet);  /* סכום ההימור */
            args.putInt("score", playerScore);      /* ניקוד נוכחי */
            shakeFragment.setArguments(args);

            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, shakeFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }
}



