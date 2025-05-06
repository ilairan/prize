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

    // משתנים למשחק ולממשק
    private Deck deck;
    private List<Card> dealerHand, playerHand;
    private TextView dealerScoreTextView, playerScoreTextView, statusMessageTextView;
    private Button hitButton, standButton;
    private ImageView deckView;

    // RecyclerViews ותצוגות קלפים
    private RecyclerView dealerRecyclerView, playerRecyclerView;
    private CardAdapter dealerAdapter, playerAdapter;

    private SoundEffectsManager soundEffectsManager;

    private String username;
    private int betAmount;
    private int playerScoreInDb;

    private DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        soundEffectsManager = new SoundEffectsManager(requireContext());  // אתחול אפקטים
        dbHelper = new DBHelper(requireContext());  // אתחול DBHelper

        // שליפת שם המשתמש מ-SharedPreferences
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        username = sharedPref.getString("username", "defaultUser");

        // שליפת סכום ההימור מה-Bundle
        if (getArguments() != null) {
            betAmount = getArguments().getInt("betAmount", 10);
            Log.d("FirstFragment", "Received BetAmount from Bundle: " + betAmount);
        }

        // שליפת ניקוד המשתמש מהמסד
        playerScoreInDb = dbHelper.getScore(username);
        Log.d("FirstFragment", "Username: " + username + ", Score: " + playerScoreInDb);

        // קישור רכיבי UI
        dealerScoreTextView = view.findViewById(R.id.dealerScore);
        playerScoreTextView = view.findViewById(R.id.playerScore);
        statusMessageTextView = view.findViewById(R.id.statusMessage);
        hitButton = view.findViewById(R.id.hitButton);
        standButton = view.findViewById(R.id.standButton);
        deckView = view.findViewById(R.id.deckView);
        dealerRecyclerView = view.findViewById(R.id.dealerRecyclerView);
        playerRecyclerView = view.findViewById(R.id.playerRecyclerView);

        // אתחול חפיסה וידיים
        deck = new Deck();
        dealerHand = new ArrayList<>();
        playerHand = new ArrayList<>();

        // הגדרת RecyclerViews
        dealerAdapter = new CardAdapter(dealerHand);
        playerAdapter = new CardAdapter(playerHand);
        dealerRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        dealerRecyclerView.setAdapter(dealerAdapter);
        playerRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        playerRecyclerView.setAdapter(playerAdapter);

        // התחלת המשחק
        startGame();

        // לחצנים
        hitButton.setOnClickListener(v -> playerHit());
        standButton.setOnClickListener(v -> playerStand());

        return view;
    }

    private void startGame() {
        // עדכון קלפים שנמשכו
        int cardsDrawn = dbHelper.getTotalCardsDrawn(username);
        dbHelper.updateTotalCardsDrawn(username, cardsDrawn + 2);

        dealerHand.clear();
        playerHand.clear();
        deck.shuffle();

        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        statusMessageTextView.setText("Dealing cards...");

        Handler handler = new Handler();

        // חלוקת קלפים עם השהיה לכל שלב
        handler.postDelayed(() -> {
            drawCard(playerHand, playerAdapter, playerRecyclerView);
            updateScores();
        }, 500);

        handler.postDelayed(() -> {
            drawCard(dealerHand, dealerAdapter, dealerRecyclerView);
            updateScores();
        }, 1000);

        handler.postDelayed(() -> {
            drawCard(playerHand, playerAdapter, playerRecyclerView);
            updateScores();
            hitButton.setEnabled(true);
            standButton.setEnabled(true);
            statusMessageTextView.setText("Your turn!");
        }, 1500);
    }

    private void playerHit() {
        int cardsDrawn = dbHelper.getTotalCardsDrawn(username);
        dbHelper.updateTotalCardsDrawn(username, cardsDrawn + 1);

        drawCard(playerHand, playerAdapter, playerRecyclerView);
        updateScores();

        if (calculateScore(playerHand) > 21) {
            statusMessageTextView.setText("Player Busts!");
            soundEffectsManager.playLose();
            disableButtons();
            new Handler().postDelayed(() -> openResultFragment(new LossFragment(), false), 1500);
        }
    }

    private void playerStand() {
        disableButtons();
        statusMessageTextView.setText("Dealer's turn...");
        dealerTurn();
    }

    private void dealerTurn() {
        int dealerScore = calculateScore(dealerHand);

        if (dealerScore < 17) {
            drawCard(dealerHand, dealerAdapter, dealerRecyclerView);
            updateScores();
            new Handler().postDelayed(this::dealerTurn, 1000);
        } else {
            evaluateWinner();
        }
    }

    private void evaluateWinner() {
        int dealerScore = calculateScore(dealerHand);
        int playerScore = calculateScore(playerHand);

        if (dealerScore > 21 || playerScore > dealerScore) {
            statusMessageTextView.setText("Player Wins!");
            soundEffectsManager.playWin();
            new Handler().postDelayed(() -> openResultFragment(new winFragment(), true), 1500);
        } else if (dealerScore == playerScore) {
            statusMessageTextView.setText("Draw!");
            new Handler().postDelayed(() -> openResultFragment(new TieFragment(), null), 1500);
        } else {
            statusMessageTextView.setText("Dealer Wins!");
            soundEffectsManager.playLose();
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

        // התאמת ערך האסים אם עובר 21
        while (score > 21 && aceCount > 0) {
            score -= 10;
            aceCount--;
        }

        return score;
    }

    private void drawCard(List<Card> hand, CardAdapter adapter, RecyclerView recyclerView) {
        Card card = deck.drawCard();
        hand.add(card);
        adapter.notifyItemInserted(hand.size() - 1);
        animateLastCard(recyclerView, hand.size() - 1);
        soundEffectsManager.playCardDraw();
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
            if (holder != null) animateCardDraw(holder.itemView);
        });
    }

    private void animateCardDraw(View cardView) {
        if (deckView == null || cardView == null) return;

        int[] deckPos = new int[2];
        int[] cardPos = new int[2];

        deckView.getLocationOnScreen(deckPos);
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
        dealerScoreTextView.setText("Dealer: " + calculateScore(dealerHand));
        playerScoreTextView.setText("Player: " + calculateScore(playerHand));
    }
}


