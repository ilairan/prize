package com.example.prize;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LossFragment extends Fragment {
    TextView playAgainButton;
    TextView mainmenu;
    TextView scoreTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loss, container, false);

        playAgainButton = view.findViewById(R.id.playAgainButton);
        mainmenu = view.findViewById(R.id.MainMenuButton);
        scoreTextView = view.findViewById(R.id.score_text_view);

        // Retrieve the scores from the arguments
        Bundle bundle = getArguments();
        if (bundle != null) {
            int playerScore = bundle.getInt("playerScore", 0); // Ensure key name matches
            int dealerScore = bundle.getInt("dealerScore", 0); // Ensure key name matches

            // Log the received scores to debug
            Log.d("LossFragment", "Received Player Score: " + playerScore);
            Log.d("LossFragment", "Received Dealer Score: " + dealerScore);

            // Display the scores
            scoreTextView.setText(dealerScore + " - " + playerScore);
        } else {
            Log.d("LossFragment", "Bundle is null");
        }

        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShakeToMixFragment fragment = new ShakeToMixFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                if (fragmentManager != null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

        mainmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Moving to Main_menu activity
                Intent intent = new Intent(getActivity(), Main_menu.class);
                startActivity(intent);
            }
        });

        return view;
    }
}


