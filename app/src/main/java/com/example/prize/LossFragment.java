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

public class LossFragment extends Fragment {

    private TextView playAgainButton, mainmenu, scoreTextView;
    private String username;
    private int updatedScore;
    private DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loss, container, false);

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

        // כפתור "שחק שוב" → מעבר לפרגמנט בחירת הימור
        playAgainButton.setOnClickListener(v -> {
            BetAmount betAmountFragment = new BetAmount();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, betAmountFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // כפתור "תפריט ראשי" → מעבר ל-Activity הראשי
        mainmenu.setOnClickListener(v -> startActivity(new Intent(getActivity(), Main_menu.class)));

        // עדכון סטטיסטיקות אם המשתמש מחובר
        if (!username.equals("defaultUser")) {
            updateStatistics();
        }

        return view;
    }

    // פונקציה לעדכון הסטטיסטיקות במסד (משחקים ששוחקו, הפסדים ואיפוס רצף ניצחונות)
    private void updateStatistics() {
        int gamesPlayed = dbHelper.getGamesPlayed(username);
        dbHelper.updateGamesPlayed(username, gamesPlayed + 1);

        int gamesLost = dbHelper.getGamesLost(username);  // תיקון: שליפת הפסדים (ולא ניצחונות)
        dbHelper.updateGamesLost(username, gamesLost + 1);

        dbHelper.updateCurrentWinStreak(username, 0);  // איפוס רצף ניצחונות נוכחי
    }
}




