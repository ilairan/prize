package com.example.prize;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private final List<Card> cardList;

    public CardAdapter(List<Card> cardList) {
        this.cardList = cardList;  // List of Card objects (dealer or player hand)
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the card_item.xml layout for each card
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card currentCard = cardList.get(position);

        String rank = currentCard.getRank();   // e.g., "A", "10", "K"
        String suitSymbol = getSuitSymbol(currentCard.getSuit()); // Get the suit symbol (♠, ♥, etc.)

        // Set texts for top-left, bottom-right, and center of the card
        holder.topLeft.setText(rank);
        holder.bottomRight.setText(rank);
        holder.center.setText(suitSymbol);
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    // ViewHolder class holds the views for each card
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView topLeft, bottomRight, center;
        ImageView background;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            topLeft = itemView.findViewById(R.id.card_top_left);
            bottomRight = itemView.findViewById(R.id.card_bottom_right);
            center = itemView.findViewById(R.id.card_center);
            background = itemView.findViewById(R.id.card_background);

        }
    }

    // Helper to get the correct suit symbol from the suit name
    private String getSuitSymbol(String suitName) {
        switch (suitName) {
            case "Spades":
                return "♠";
            case "Hearts":
                return "♥";
            case "Diamonds":
                return "♦";
            case "Clubs":
                return "♣";
            default:
                return "?";
        }
    }

}
