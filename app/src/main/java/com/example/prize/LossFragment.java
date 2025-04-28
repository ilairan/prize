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

    TextView playAgainButton;
    TextView mainmenu;

    private String username;
    private int updatedScore;
    private DBHelper dbHelper;
    private TextView scoreTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loss, container, false);

        playAgainButton = view.findViewById(R.id.playAgainButton);
        mainmenu = view.findViewById(R.id.MainMenuButton);
        scoreTextView = view.findViewById(R.id.scoreTextView);

        playAgainButton.setOnClickListener(v -> {
            BetAmount betAmountFragment = new BetAmount();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, betAmountFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Main Menu button → Back to Main_menu Activity
        mainmenu.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Main_menu.class);
            startActivity(intent);
        });


        // Get username from SharedPreferences
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        username = sharedPref.getString("username", "defaultUser");

        // Get updated score from DB
        dbHelper = new DBHelper(requireContext());
        updatedScore = dbHelper.getScore(username);
        scoreTextView.setText("" + updatedScore);
        username = sharedPref.getString("username", "defaultUser");

        // === Update stats ===
        if (!username.equals("defaultUser")) {
            // Games played
            int gamesPlayed = dbHelper.getGamesPlayed(username);
            dbHelper.updateGamesPlayed(username, gamesPlayed + 1);

            // Games won
            int gameslost = dbHelper.getGamesWon(username);
            dbHelper.updateGamesLost(username, gameslost + 1);

            // Current win streak
            int currentWinStreak = dbHelper.getCurrentWinStreak(username);
            dbHelper.updateCurrentWinStreak(username, 0);

        }




        return view;
    }
}



