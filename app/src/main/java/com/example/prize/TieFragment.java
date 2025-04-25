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

public class TieFragment extends Fragment {

    TextView playAgainButton;
    TextView mainmenu;
    TextView scoreTextView;

    private String username;
    private int updatedScore;
    private DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tie, container, false);

        playAgainButton = view.findViewById(R.id.playAgainButton);
        mainmenu = view.findViewById(R.id.MainMenuButton);
        scoreTextView = view.findViewById(R.id.scoreTextView);

        // Get username from SharedPreferences
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        username = sharedPref.getString("username", "defaultUser");

        // Fetch score from DB
        dbHelper = new DBHelper(requireContext());
        updatedScore = dbHelper.getScore(username);

        // Display the score
        scoreTextView.setText("Score: " + updatedScore);

        // Play Again button → Go to BetAmount fragment
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

        return view;
    }
}


