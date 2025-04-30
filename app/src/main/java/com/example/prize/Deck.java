package com.example.prize;
import com.example.prize.Card;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private final List<Card> cards;  /* רשימת הקלפים בחפיסה */

    // בנאי - יצירת חפיסה חדשה של 52 קלפים
    public Deck() {
        cards = new ArrayList<>();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};  /* סוגי הצורות */
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};  /* דרגות */
        int[] values = {2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 11};  /* ערכים מתאימים לדרגות */

        /* יצירת כל הקלפים לפי שילוב דרגות וצורות */
        for (String suit : suits) {
            for (int j = 0; j < ranks.length; j++) {
                cards.add(new Card(suit, ranks[j], values[j]));
            }
        }

        shuffle();  // ערבוב החפיסה
    }

    // ערבוב החפיסה
    public void shuffle() {
        Collections.shuffle(cards);
    }

    // שליפת קלף מהחפיסה
    public Card drawCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("The deck is empty");  /* טיפול במקרה של חפיסה ריקה */
        }
        return cards.remove(cards.size() - 1);  /* שליפת הקלף האחרון בחפיסה */
    }
}

