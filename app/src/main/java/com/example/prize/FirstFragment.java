package com.example.prize;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {

    private Deck deck;
    private List<Card> dealerHand;
    private List<Card> playerHand;
    private TextView dealerScoreTextView;
    private TextView playerScoreTextView;
    private TextView statusMessageTextView;
    private Button hitButton;
    private Button standButton;
    private Button playAgainButton;
    private TextView backButton;

    private String username;
    private int betAmount;
    private int playerScoreInDb;

    private DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        // Initialize views
        dealerScoreTextView = view.findViewById(R.id.dealerScore);
        playerScoreTextView = view.findViewById(R.id.playerScore);
        statusMessageTextView = view.findViewById(R.id.statusMessage);
        hitButton = view.findViewById(R.id.hitButton);
        standButton = view.findViewById(R.id.standButton);
        playAgainButton = view.findViewById(R.id.playAgainButton);
        backButton = view.findViewById(R.id.backButton);

        // Initialize deck and hands
        deck = new Deck();
        dealerHand = new ArrayList<>();
        playerHand = new ArrayList<>();

        // Get betAmount from Bundle
        if (getArguments() != null) {
            betAmount = getArguments().getInt("betAmount", 10);
            Log.d("FirstFragment", "Received BetAmount from Bundle: " + betAmount);
        } else {
            Log.d("FirstFragment", "No Bundle received");
        }

        // Get username from SharedPreferences
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        username = sharedPref.getString("username", "defaultUser");

        // Initialize DBHelper and get score
        dbHelper = new DBHelper(requireContext());
        playerScoreInDb = dbHelper.getScore(username);

        // Log user and score
        Log.d("FirstFragment", "Username from SharedPreferences: " + username);
        Log.d("FirstFragment", "Score from DB: " + playerScoreInDb);

        // Start game
        startGame();

        // Button listeners
        hitButton.setOnClickListener(v -> playerHit());
        standButton.setOnClickListener(v -> playerStand());
        playAgainButton.setOnClickListener(v -> playAgain());
        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    private void startGame() {
        dealerHand.clear();
        playerHand.clear();
        deck.shuffle();

        // Initial cards
        playerHand.add(deck.drawCard());
        playerHand.add(deck.drawCard());
        dealerHand.add(deck.drawCard());

        statusMessageTextView.setText("Your turn!");

        // Button visibility
        playAgainButton.setVisibility(View.GONE);
        hitButton.setEnabled(true);
        standButton.setEnabled(true);

        updateUI();
    }

    private void updateUI() {
        int dealerScore = calculateScore(dealerHand);
        int playerScore = calculateScore(playerHand);

        dealerScoreTextView.setText("Dealer: " + dealerScore);
        playerScoreTextView.setText("Player: " + playerScore);
    }

    private int calculateScore(List<Card> hand) {
        int score = 0;
        int aceCount = 0;

        for (Card card : hand) {
            score += card.getValue();
            if (card.getRank().equals("Ace")) {
                aceCount++;
            }
        }

        while (score > 21 && aceCount > 0) {
            score -= 10;
            aceCount--;
        }

        return score;
    }

    private void playerHit() {
        playerHand.add(deck.drawCard());
        updateUI();

        if (calculateScore(playerHand) > 21) {
            statusMessageTextView.setText("Player Busts!");
            disableButtons();
            new Handler().postDelayed(() -> openResultFragment(new LossFragment(), false), 1500);
        }
    }

    private void playerStand() {
        while (calculateScore(dealerHand) < 17) {
            dealerHand.add(deck.drawCard());
        }
        updateUI();

        int dealerScore = calculateScore(dealerHand);
        int playerScore = calculateScore(playerHand);

        if (dealerScore > 21 || playerScore > dealerScore) {
            statusMessageTextView.setText("Player Wins!");
            disableButtons();
            new Handler().postDelayed(() -> openResultFragment(new winFragment(), true), 1500);
        } else if (dealerScore == playerScore) {
            statusMessageTextView.setText("Draw!");
            disableButtons();
            new Handler().postDelayed(() -> openResultFragment(new TieFragment(), null), 1500);
        } else {
            statusMessageTextView.setText("Dealer Wins!");
            disableButtons();
            new Handler().postDelayed(() -> openResultFragment(new LossFragment(), false), 1500);
        }
    }

    private void disableButtons() {
        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        playAgainButton.setVisibility(View.VISIBLE);
    }

    private void playAgain() {
        startGame();
    }

    private void openResultFragment(Fragment fragment, @Nullable Boolean playerWins) {
        // Update score in DB
        if (playerWins != null) {
            int newScore = playerScoreInDb;
            if (playerWins) {
                newScore += betAmount;
                Log.d("FirstFragment", "Player wins! Adding betAmount: " + betAmount);
            } else {
                newScore -= betAmount;
                Log.d("FirstFragment", "Player loses! Subtracting betAmount: " + betAmount);
            }
            dbHelper.updateScore(username, newScore);
            playerScoreInDb = newScore;
            Log.d("FirstFragment", "Updated score in DB: " + playerScoreInDb);
        }

        // Pass only betAmount via Bundle
        Bundle bundle = new Bundle();
        bundle.putInt("betAmount", betAmount);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}






