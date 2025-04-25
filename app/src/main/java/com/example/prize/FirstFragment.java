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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private TextView backButton;

    private RecyclerView dealerRecyclerView;
    private RecyclerView playerRecyclerView;
    private CardAdapter dealerAdapter;
    private CardAdapter playerAdapter;

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
        backButton = view.findViewById(R.id.backButton);

        dealerRecyclerView = view.findViewById(R.id.dealerRecyclerView);
        playerRecyclerView = view.findViewById(R.id.playerRecyclerView);

        // Initialize deck and hands
        deck = new Deck();
        dealerHand = new ArrayList<>();
        playerHand = new ArrayList<>();

        // Get betAmount from Bundle
        if (getArguments() != null) {
            betAmount = getArguments().getInt("betAmount", 10);
            Log.d("FirstFragment", "Received BetAmount from Bundle: " + betAmount);
        }

        // Get username from SharedPreferences
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        username = sharedPref.getString("username", "defaultUser");

        // Initialize DBHelper and get score
        dbHelper = new DBHelper(requireContext());
        playerScoreInDb = dbHelper.getScore(username);

        Log.d("FirstFragment", "Username: " + username + ", Score: " + playerScoreInDb);

        // Setup RecyclerViews
        dealerAdapter = new CardAdapter(dealerHand);
        playerAdapter = new CardAdapter(playerHand);

        dealerRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        dealerRecyclerView.setAdapter(dealerAdapter);

        playerRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        playerRecyclerView.setAdapter(playerAdapter);

        // Start game
        startGame();

        // Button listeners
        hitButton.setOnClickListener(v -> playerHit());
        standButton.setOnClickListener(v -> playerStand());
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
        hitButton.setEnabled(true);
        standButton.setEnabled(true);

        updateUI();
    }

    private void updateUI() {
        int dealerScore = calculateScore(dealerHand);
        int playerScore = calculateScore(playerHand);

        dealerScoreTextView.setText("Dealer: " + dealerScore);
        playerScoreTextView.setText("Player: " + playerScore);

        dealerAdapter.notifyDataSetChanged();
        playerAdapter.notifyDataSetChanged();
    }

    private int calculateScore(List<Card> hand) {
        int score = 0;
        int aceCount = 0;
        for (Card card : hand) {
            score += card.getValue();
            if (card.getRank().equals("Ace")) aceCount++;
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
    }

    private void openResultFragment(Fragment fragment, @Nullable Boolean playerWins) {
        if (playerWins != null) {
            int newScore = playerScoreInDb;
            if (playerWins) {
                newScore += betAmount;
            } else {
                newScore -= betAmount;
            }
// Prevent score from going below 0
            newScore = Math.max(newScore, 0);
            dbHelper.updateScore(username, newScore);

        }

        Bundle bundle = new Bundle();
        bundle.putInt("betAmount", betAmount);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}




