package com.example.prize;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StatisticsFragment extends Fragment {

    // הגדרות טקסטים לסטטיסטיקות
    private TextView gamesPlayedText, gamesWonText, gamesLostText, gamesTiedText, winRateText,
            currentWinStreakText, longestWinStreakText, totalCardsDrawnText, largestBetText, backButton;

    private DBHelper dbHelper;  // גישה למסד הנתונים
    private String userEmail;   // כתובת המייל של המשתמש

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);  // טעינת תצוגת הפרגמנט
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DBHelper(getContext());  // אתחול DBHelper
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userEmail = sharedPreferences.getString("username", null);  // שליפת כתובת המייל של המשתמש

        // קישור רכיבי ה-XML
        gamesPlayedText = view.findViewById(R.id.gamesPlayed);
        gamesWonText = view.findViewById(R.id.gamesWon);
        gamesLostText = view.findViewById(R.id.gamesLost);
        gamesTiedText = view.findViewById(R.id.gamesTied);
        winRateText = view.findViewById(R.id.winRate);
        currentWinStreakText = view.findViewById(R.id.currentWinStreak);
        longestWinStreakText = view.findViewById(R.id.longestWinStreak);
        totalCardsDrawnText = view.findViewById(R.id.totalCardsDrawn);
        largestBetText = view.findViewById(R.id.largestBet);
        backButton = view.findViewById(R.id.backButton);

        // טעינת סטטיסטיקות מהמסד
        if (userEmail != null) {
            int gamesPlayed = dbHelper.getGamesPlayed(userEmail);
            int gamesWon = dbHelper.getGamesWon(userEmail);
            int gamesLost = dbHelper.getGamesLost(userEmail);
            int gamesTied = dbHelper.getGamesTied(userEmail);
            int currentStreak = dbHelper.getCurrentWinStreak(userEmail);
            int longestStreak = dbHelper.getLongestWinStreak(userEmail);
            int totalCardsDrawn = dbHelper.getTotalCardsDrawn(userEmail);
            int largestBet = dbHelper.getLargestBet(userEmail);

            float winRate = (gamesPlayed > 0) ? ((float) gamesWon / gamesPlayed) * 100 : 0;  // חישוב אחוזי ניצחון

            // הצגת נתונים במסך
            gamesPlayedText.setText("Games Played: " + gamesPlayed);
            gamesWonText.setText("Games Won: " + gamesWon);
            gamesLostText.setText("Games Lost: " + gamesLost);
            gamesTiedText.setText("Games Tied: " + gamesTied);
            winRateText.setText("Win Rate: " + String.format("%.1f", winRate) + "%");
            currentWinStreakText.setText("Current Win Streak: " + currentStreak);
            longestWinStreakText.setText("Longest Win Streak: " + longestStreak);
            totalCardsDrawnText.setText("Total Cards Drawn: " + totalCardsDrawn);
            largestBetText.setText("Largest Bet: " + largestBet);
        }

        // לחיצה על כפתור חזרה לתפריט הראשי
        backButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), Main_menu.class)));
    }
}


