package com.example.prize;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class winFragment extends Fragment {

    private TextView playAgainButton, mainmenu, scoreTextView;
    private String username;
    private int updatedScore;
    private DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_win, container, false);

        // הצגת הפרגמנט במסך מלא (ללא סטטוס בר)
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        // קישור רכיבי ה-XML
        playAgainButton = view.findViewById(R.id.playAgainButton);
        mainmenu = view.findViewById(R.id.MainMenuButton);
        scoreTextView = view.findViewById(R.id.scoreTextView);

        // שליפת שם המשתמש מ-SharedPreferences
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        username = sharedPref.getString("username", "defaultUser");

        dbHelper = new DBHelper(requireContext());  // אתחול DBHelper
        updatedScore = dbHelper.getScore(username);  // שליפת ניקוד עדכני מהמסד

        scoreTextView.setText(String.valueOf(updatedScore));  // הצגת הניקוד

        // כפתור "שחק שוב" → מעבר לפרגמנט בחירת הימור (BetAmount)
        playAgainButton.setOnClickListener(v -> {
            BetAmount betAmountFragment = new BetAmount();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, betAmountFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // כפתור "תפריט ראשי" → מעבר ל-Activity הראשי
        mainmenu.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), Main_menu.class));
        });

        // עדכון סטטיסטיקות במידה והמשתמש מחובר
        if (!username.equals("defaultUser")) {
            updateStatistics();
        }

        return view;
    }

    // פונקציה לעדכון הסטטיסטיקות במסד
    private void updateStatistics() {
        int gamesPlayed = dbHelper.getGamesPlayed(username);
        dbHelper.updateGamesPlayed(username, gamesPlayed + 1);

        int gamesWon = dbHelper.getGamesWon(username);
        dbHelper.updateGamesWon(username, gamesWon + 1);

        int currentWinStreak = dbHelper.getCurrentWinStreak(username);
        dbHelper.updateCurrentWinStreak(username, currentWinStreak + 1);

        int longestWinStreak = dbHelper.getLongestWinStreak(username);
        if (currentWinStreak + 1 > longestWinStreak) {
            dbHelper.updateLongestWinStreak(username, currentWinStreak + 1);
        }
    }
}


