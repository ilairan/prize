package com.example.prize;

public class Card {

    private String suit;   /* סוג הצורה (לב, תלתן וכו') */
    private String rank;   /* דרגת הקלף (2, 3, ג'ק, קינג וכו') */
    private int value;     /* ערך מספרי של הקלף (למטרות חישוב ניקוד) */

    // בנאי (Constructor)
    public Card(String suit, String rank, int value) {
        this.suit = suit;
        this.rank = rank;
        this.value = value;
    }

    // פונקציה להחזרת הצורה
    public String getSuit() {
        return suit;
    }

    // פונקציה להחזרת הדרגה
    public String getRank() {
        return rank;
    }

    // פונקציה להחזרת הערך
    public int getValue() {
        return value;
    }

    // פונקציה להחזרת תיאור הקלף (למשל: "Ace of Spades")
    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}

