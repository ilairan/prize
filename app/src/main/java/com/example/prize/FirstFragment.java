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
import android.widget.ImageView;
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
    private ImageView deckView;

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
        deckView = view.findViewById(R.id.deckView);
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

        return view;
    }

    private void startGame() {
        dealerHand.clear();
        playerHand.clear();
        deck.shuffle();

        hitButton.setEnabled(false);  // Disable buttons during dealing
        standButton.setEnabled(false);
        statusMessageTextView.setText("Dealing cards...");

        Handler handler = new Handler();

        // Player first card
        handler.postDelayed(() -> {
            playerHand.add(deck.drawCard());
            playerAdapter.notifyItemInserted(playerHand.size() - 1);
            animateLastCard(playerRecyclerView, playerHand.size() - 1);
            updateScores();
        }, 500);

        // Dealer card
        handler.postDelayed(() -> {
            dealerHand.add(deck.drawCard());
            dealerAdapter.notifyItemInserted(dealerHand.size() - 1);
            animateLastCard(dealerRecyclerView, dealerHand.size() - 1);
            updateScores();
        }, 1000);

        // Player second card
        handler.postDelayed(() -> {
            playerHand.add(deck.drawCard());
            playerAdapter.notifyItemInserted(playerHand.size() - 1);
            animateLastCard(playerRecyclerView, playerHand.size() - 1);
            updateScores();

            // Enable buttons after all cards are dealt
            hitButton.setEnabled(true);
            standButton.setEnabled(true);
            statusMessageTextView.setText("Your turn!");
        }, 1500);
    }



    private void playerHit() {
        playerHand.add(deck.drawCard());
        playerAdapter.notifyItemInserted(playerHand.size() - 1);
        animateLastCard(playerRecyclerView, playerHand.size() - 1);

        // Update scores after hit
        updateScores();

        if (calculateScore(playerHand) > 21) {
            statusMessageTextView.setText("Player Busts!");
            disableButtons();
            new Handler().postDelayed(() -> openResultFragment(new LossFragment(), false), 1500);
        }
    }


    private void playerStand() {
        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        statusMessageTextView.setText("Dealer's turn...");
        dealerTurn();
    }

    private void dealerTurn() {
        int dealerScore = calculateScore(dealerHand);

        if (dealerScore < 17) {
            // Dealer draws a card
            dealerHand.add(deck.drawCard());
            dealerAdapter.notifyItemInserted(dealerHand.size() - 1);
            animateLastCard(dealerRecyclerView, dealerHand.size() - 1);
            updateScores();

            // Delay next card
            new Handler().postDelayed(this::dealerTurn, 1000);  // 1-second delay for next card
        } else {
            // Dealer is done drawing
            evaluateWinner();
        }
    }

    private void evaluateWinner() {
        int dealerScore = calculateScore(dealerHand);
        int playerScore = calculateScore(playerHand);

        if (dealerScore > 21 || playerScore > dealerScore) {
            statusMessageTextView.setText("Player Wins!");
            new Handler().postDelayed(() -> openResultFragment(new winFragment(), true), 1500);
        } else if (dealerScore == playerScore) {
            statusMessageTextView.setText("Draw!");
            new Handler().postDelayed(() -> openResultFragment(new TieFragment(), null), 1500);
        } else {
            statusMessageTextView.setText("Dealer Wins!");
            new Handler().postDelayed(() -> openResultFragment(new LossFragment(), false), 1500);
        }
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

    private void disableButtons() {
        hitButton.setEnabled(false);
        standButton.setEnabled(false);
    }

    private void openResultFragment(Fragment fragment, @Nullable Boolean playerWins) {
        if (playerWins != null) {
            int newScore = playerScoreInDb + (playerWins ? betAmount : -betAmount);
            dbHelper.updateScore(username, Math.max(newScore, 0));
        }

        Bundle bundle = new Bundle();
        bundle.putInt("betAmount", betAmount);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void animateLastCard(RecyclerView recyclerView, int position) {
        recyclerView.post(() -> {
            RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(position);
            if (holder != null && holder.itemView != null) {
                animateCardDraw(holder.itemView);
            }
        });
    }

    private void animateCardDraw(View cardView) {
        if (deckView == null || cardView == null) return;

        int[] deckPos = new int[2];
        deckView.getLocationOnScreen(deckPos);

        int[] cardPos = new int[2];
        cardView.getLocationOnScreen(cardPos);

        float deltaX = deckPos[0] - cardPos[0];
        float deltaY = deckPos[1] - cardPos[1];

        cardView.setTranslationX(deltaX);
        cardView.setTranslationY(deltaY);
        cardView.setScaleX(0.3f);
        cardView.setScaleY(0.3f);
        cardView.setRotation(-30f);
        cardView.setAlpha(0f);

        cardView.animate()
                .translationX(0)
                .translationY(0)
                .scaleX(1f)
                .scaleY(1f)
                .rotation(0f)
                .alpha(1f)
                .setDuration(600)
                .start();
    }


    private void updateScores() {
        int dealerScore = calculateScore(dealerHand);
        int playerScore = calculateScore(playerHand);
        dealerScoreTextView.setText("Dealer: " + dealerScore);
        playerScoreTextView.setText("Player: " + playerScore);
    }

}


