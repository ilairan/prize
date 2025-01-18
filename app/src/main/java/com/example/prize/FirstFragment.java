package com.example.prize;

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
import androidx.fragment.app.FragmentManager;
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
    private ImageView dealerCardImageView1Mid;
    private ImageView dealerCardImageView2Mid;
    private ImageView dealerCardImageView3Mid;
    private TextView dealerCard1TopLeft, dealerCard1BottomRight;
    private TextView dealerCard2TopLeft, dealerCard2BottomRight;
    private TextView dealerCard3TopLeft, dealerCard3BottomRight;
    private ImageView playerCardImageView1Mid;
    private ImageView playerCardImageView2Mid;
    private ImageView playerCardImageView3Mid;
    private TextView playerCard1TopLeft, playerCard1BottomRight;
    private TextView playerCard2TopLeft, playerCard2BottomRight;
    private TextView playerCard3TopLeft, playerCard3BottomRight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        dealerScoreTextView = view.findViewById(R.id.dealerScore);
        playerScoreTextView = view.findViewById(R.id.playerScore);
        statusMessageTextView = view.findViewById(R.id.statusMessage);
        hitButton = view.findViewById(R.id.hitButton);
        standButton = view.findViewById(R.id.standButton);
        playAgainButton = view.findViewById(R.id.playAgainButton);
        backButton = view.findViewById(R.id.backButton);
        dealerCardImageView1Mid = view.findViewById(R.id.dealerCard1Mid);
        dealerCardImageView2Mid = view.findViewById(R.id.dealerCard2Mid);
        dealerCardImageView3Mid = view.findViewById(R.id.dealerCard3Mid);
        dealerCard1TopLeft = view.findViewById(R.id.dealerCard1TopLeft);
        dealerCard1BottomRight = view.findViewById(R.id.dealerCard1BottomRight);
        dealerCard2TopLeft = view.findViewById(R.id.dealerCard2TopLeft);
        dealerCard2BottomRight = view.findViewById(R.id.dealerCard2BottomRight);
        dealerCard3TopLeft = view.findViewById(R.id.dealerCard3TopLeft);
        dealerCard3BottomRight = view.findViewById(R.id.dealerCard3BottomRight);
        playerCardImageView1Mid = view.findViewById(R.id.playerCard1Mid);
        playerCardImageView2Mid = view.findViewById(R.id.playerCard2Mid);
        playerCardImageView3Mid = view.findViewById(R.id.playerCard3Mid);
        playerCard1TopLeft = view.findViewById(R.id.playerCard1TopLeft);
        playerCard1BottomRight = view.findViewById(R.id.playerCard1BottomRight);
        playerCard2TopLeft = view.findViewById(R.id.playerCard2TopLeft);
        playerCard2BottomRight = view.findViewById(R.id.playerCard2BottomRight);
        playerCard3TopLeft = view.findViewById(R.id.playerCard3TopLeft);
        playerCard3BottomRight = view.findViewById(R.id.playerCard3BottomRight);

        deck = new Deck();
        dealerHand = new ArrayList<>();
        playerHand = new ArrayList<>();

        startGame();

        hitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerHit();
            }
        });

        standButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerStand();
            }
        });

        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAgain();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();

                if (getActivity() instanceof Main_menu) {
                    Main_menu mainMenu = (Main_menu) getActivity();
                    mainMenu.frameLayout.setVisibility(View.GONE);
                    mainMenu.settingsbutton.setVisibility(View.VISIBLE);
                    mainMenu.startbutton.setVisibility(View.VISIBLE);
                    mainMenu.button3.setVisibility(View.VISIBLE);
                    mainMenu.textView.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }

    private void startGame() {
        dealerHand.clear();
        playerHand.clear();
        deck.shuffle();

        playerHand.add(deck.drawCard());
        playerHand.add(deck.drawCard());
        dealerHand.add(deck.drawCard());

        updateUI();
    }

    private void updateUI() {
        int dealerScore = calculateScore(dealerHand);
        int playerScore = calculateScore(playerHand);

        dealerScoreTextView.setText("Dealer: " + dealerScore);
        playerScoreTextView.setText("Player: " + playerScore);

        updateCardViews();

        playAgainButton.setVisibility(View.GONE); // Hide Play Again button initially
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
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openResultFragment(new LossFragment());                }
            }, 1500);
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
            // Delay the fragment transition by 3 seconds (3000 milliseconds)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openResultFragment(new winFragment());
                }
            }, 1500);
        } else if (dealerScore == playerScore) {
            statusMessageTextView.setText("Draw!");
            disableButtons();
            // Delay the fragment transition by 3 seconds (3000 milliseconds)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openResultFragment(new TieFragment());
                }
            }, 1500);
        } else {
            statusMessageTextView.setText("Dealer Wins!");
            disableButtons();
            // Delay the fragment transition by 3 seconds (3000 milliseconds)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openResultFragment(new LossFragment());
                }
            }, 1500);
        }
    }


    private void disableButtons() {
        hitButton.setEnabled(false);
        standButton.setEnabled(false);
    }

    private void playAgain() {
        startGame();
        statusMessageTextView.setText("Status");
        hitButton.setEnabled(true);
        standButton.setEnabled(true);
        playAgainButton.setVisibility(View.GONE); // Hide Play Again button
    }

    private void updateCardViews() {
        if (!dealerHand.isEmpty()) {
            updateCardView(dealerHand.get(0), dealerCardImageView1Mid, dealerCard1TopLeft, dealerCard1BottomRight);
            if (dealerHand.size() > 1) {
                updateCardView(dealerHand.get(1), dealerCardImageView2Mid, dealerCard2TopLeft, dealerCard2BottomRight);
            }
            if (dealerHand.size() > 2) {
                updateCardView(dealerHand.get(2), dealerCardImageView3Mid, dealerCard3TopLeft, dealerCard3BottomRight);
            }
        }
        if (!playerHand.isEmpty()) {
            updateCardView(playerHand.get(0), playerCardImageView1Mid, playerCard1TopLeft, playerCard1BottomRight);
            if (playerHand.size() > 1) {
                updateCardView(playerHand.get(1), playerCardImageView2Mid, playerCard2TopLeft, playerCard2BottomRight);
            }
            if (playerHand.size() > 2) {
                updateCardView(playerHand.get(2), playerCardImageView3Mid, playerCard3TopLeft, playerCard3BottomRight);
            }
        }
    }

    private void updateCardView(Card card, ImageView cardMidImageView, TextView cardTopLeft, TextView cardBottomRight) {
        int cardResId = getCardResId(card);
        cardMidImageView.setImageResource(cardResId);
        cardTopLeft.setText(card.getRank());
        cardBottomRight.setText(card.getRank());
    }

    private int getCardResId(Card card) {
        String suit = card.getSuit().toLowerCase();
        String cardImageName = "ic_" + suit + "_" + card.getRank().toLowerCase(); // Assuming card images are named like ic_spades_ace, ic_hearts_2, etc.
        return getResources().getIdentifier(cardImageName, "drawable", getActivity().getPackageName());
    }

    private void openResultFragment(Fragment fragment) {
        int dealerScore = calculateScore(dealerHand);
        int playerScore = calculateScore(playerHand);

        // Add debug logs to check the scores
        Log.d("FirstFragment", "Dealer Score: " + dealerScore);
        Log.d("FirstFragment", "Player Score: " + playerScore);

        // Create a Bundle to pass the scores
        Bundle bundle = new Bundle();
        bundle.putInt("dealerScore", dealerScore);
        bundle.putInt("playerScore", playerScore);

        // Set the arguments for the fragment
        fragment.setArguments(bundle);

        // Validate the bundle content
        Log.d("FirstFragment", "Bundle Dealer Score: " + bundle.getInt("dealerScore"));
        Log.d("FirstFragment", "Bundle Player Score: " + bundle.getInt("playerScore"));

        // Perform the fragment transaction
        FragmentManager fragmentManager = getParentFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}




