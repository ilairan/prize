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
import android.widget.ImageView;
import android.widget.TextView;

public class AchievementsFragment extends Fragment {

    private DBHelper dbHelper;  // גישה למסד הנתונים
    private String userEmail;   // כתובת המייל של המשתמש
    private TextView backButton;  // כפתור חזרה

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_achievements, container, false);  // טעינת תצוגת הפרגמנט
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backButton = view.findViewById(R.id.Back_buttom);  // קישור כפתור החזרה

        dbHelper = new DBHelper(requireContext());  // אתחול DBHelper
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userEmail = sharedPref.getString("username", null);  // שליפת כתובת המייל של המשתמש

        if (userEmail != null) {
            // שליפת נתונים מהמסד
            int totalWins = dbHelper.getGamesWon(userEmail);
            int longestWinStreak = dbHelper.getLongestWinStreak(userEmail);
            int totalCardsDrawn = dbHelper.getTotalCardsDrawn(userEmail);
            int largestBet = dbHelper.getLargestBet(userEmail);

            // === פתיחת הישגים על פי תנאים ===

            // ניצחונות כוללים
            unlock(view, R.id.wins_10, totalWins >= 10);
            unlock(view, R.id.wins_100, totalWins >= 100);
            unlock(view, R.id.wins_500, totalWins >= 500);
            unlock(view, R.id.wins_1000, totalWins >= 1000);

            // רצפי ניצחונות
            unlock(view, R.id.streak_3, longestWinStreak >= 3);
            unlock(view, R.id.streak_5, longestWinStreak >= 5);
            unlock(view, R.id.streak_20, longestWinStreak >= 20);
            unlock(view, R.id.streak_50, longestWinStreak >= 50);

            // קלפים שנמשכו
            unlock(view, R.id.cards_100, totalCardsDrawn >= 100);
            unlock(view, R.id.cards_500, totalCardsDrawn >= 500);
            unlock(view, R.id.cards_1000, totalCardsDrawn >= 1000);
            unlock(view, R.id.cards_5000, totalCardsDrawn >= 5000);

            // סכום הימור הכי גבוה
            unlock(view, R.id.bet_100, largestBet >= 100);
            unlock(view, R.id.bet_500, largestBet >= 500);
            unlock(view, R.id.bet_1000, largestBet >= 1000);
            unlock(view, R.id.bet_5000, largestBet >= 5000);
        }

        // כפתור חזרה לתפריט הראשי
        backButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), Main_menu.class)));
    }

    // פונקציה לפתיחת הישג (הצגת מדליה)
    private void unlock(View view, int imageViewId, boolean condition) {
        ImageView medal = view.findViewById(imageViewId);
        if (condition) {
            medal.setVisibility(View.VISIBLE);  // אם התנאי מתקיים, הצגת המדליה
        }
    }
}
