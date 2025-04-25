package com.example.prize;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class winFragment extends Fragment {

    TextView playAgainButton;
    TextView mainmenu;
    TextView scoreTextView;

    private String username;
    private int updatedScore;
    private DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_win, container, false);

        requireActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        playAgainButton = view.findViewById(R.id.playAgainButton);
        mainmenu = view.findViewById(R.id.MainMenuButton);
        scoreTextView = view.findViewById(R.id.scoreTextView);

        // Get username from SharedPreferences
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        username = sharedPref.getString("username", "defaultUser");

        // Get updated score from DB
        dbHelper = new DBHelper(requireContext());
        updatedScore = dbHelper.getScore(username);

        // Show the score
        scoreTextView.setText("Score: " + updatedScore);

        // Play Again → Go to BetAmount fragment
        playAgainButton.setOnClickListener(v -> {
            BetAmount betAmountFragment = new BetAmount();
            // No need to pass score, only betAmount will be bundled later
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, betAmountFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        // Main Menu → Back to Main_menu activity
        mainmenu.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Main_menu.class);
            startActivity(intent);
        });

        return view;
    }
}


