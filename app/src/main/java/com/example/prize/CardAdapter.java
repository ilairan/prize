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

    private final List<Card> cardList;  /* רשימת הקלפים (יד השחקן או הדילר) */

    // בנאי - מקבל רשימת קלפים
    public CardAdapter(List<Card> cardList) {
        this.cardList = cardList;
    }

    // יצירת ViewHolder חדש
    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /* טעינת תצוגת קלף מה-XML */
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new CardViewHolder(view);
    }

    // הצגת נתונים בכל קלף (Binding)
    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card currentCard = cardList.get(position);  /* קבלת הקלף לפי המיקום */

        String rank = currentCard.getRank();  /* דרגת הקלף (למשל: A, 10, K) */
        String suitSymbol = getSuitSymbol(currentCard.getSuit());  /* סמל הצורה (♠, ♥ וכו') */

        // עדכון טקסטים בתצוגת הקלף
        holder.topLeft.setText(rank);
        holder.bottomRight.setText(rank);
        holder.center.setText(suitSymbol);
    }

    @Override
    public int getItemCount() {
        return cardList.size();  /* מספר הקלפים להציג */
    }

    // ViewHolder - מחזיק את רכיבי התצוגה של כל קלף
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        final TextView topLeft, bottomRight, center;
        final ImageView background;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            topLeft = itemView.findViewById(R.id.card_top_left);
            bottomRight = itemView.findViewById(R.id.card_bottom_right);
            center = itemView.findViewById(R.id.card_center);
            background = itemView.findViewById(R.id.card_background);
        }
    }

    // פונקציה להחזרת סמל הצורה לפי שם הצורה
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
                return "?";  /* ברירת מחדל */
        }
    }
}
