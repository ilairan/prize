package com.example.prize;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Userdata.db";  /* שם קובץ המסד */
    private static final int DATABASE_VERSION = 3;              /* גרסה */
    private static final String TABLE_NAME = "Userdetails";     /* שם הטבלה */
    private static final String COL_NAME = "name";              /* שם המשתמש */
    private static final String COL_EMAIL = "email";            /* אימייל */
    private static final String COL_PHONE_NUM = "phone_num";    /* מספר טלפון */
    private static final String COL_PASSWORD = "password";      /* סיסמה */

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /* יצירת טבלת משתמשים עם עמודות נוספות */
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_NAME + " TEXT PRIMARY KEY, " +
                COL_EMAIL + " TEXT, " +
                COL_PHONE_NUM + " TEXT, " +
                COL_PASSWORD + " TEXT, " +
                "score INTEGER DEFAULT 100, " +
                "music_volume INTEGER DEFAULT 100, " +
                "sound_effects_volume INTEGER DEFAULT 100, " +
                "notifications_enabled INTEGER DEFAULT 1, " +
                "games_played INTEGER DEFAULT 0, " +
                "games_won INTEGER DEFAULT 0, " +
                "games_lost INTEGER DEFAULT 0, " +
                "games_tied INTEGER DEFAULT 0, " +
                "current_win_streak INTEGER DEFAULT 0, " +
                "longest_win_streak INTEGER DEFAULT 0, " +
                "total_cards_drawn INTEGER DEFAULT 0, " +
                "largest_bet INTEGER DEFAULT 0)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /* שדרוג טבלה - הוספת עמודות בגרסאות חדשות */
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN music_volume INTEGER DEFAULT 100");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN sound_effects_volume INTEGER DEFAULT 100");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN notifications_enabled INTEGER DEFAULT 1");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN games_played INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN games_won INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN games_lost INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN games_tied INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN current_win_streak INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN longest_win_streak INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN total_cards_drawn INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN largest_bet INTEGER DEFAULT 0");
        }
    }

    /* === פונקציות ניהול משתמשים === */

    // הוספת משתמש חדש
    public boolean insertUserdata(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, user.getName());
        values.put(COL_EMAIL, user.getEmail());
        values.put(COL_PHONE_NUM, user.getPhone_num());
        values.put(COL_PASSWORD, user.getPassword());
        values.put("score", 100);  // ניקוד התחלתי

        return db.insert(TABLE_NAME, null, values) != -1;
    }

    // עדכון פרטי משתמש
    public boolean updateUserdata(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EMAIL, user.getEmail());
        values.put(COL_PHONE_NUM, user.getPhone_num());
        values.put(COL_PASSWORD, user.getPassword());

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_NAME + "=?", new String[]{user.getName()});
        boolean exists = cursor.getCount() > 0;
        if (exists) {
            db.update(TABLE_NAME, values, COL_NAME + "=?", new String[]{user.getName()});
        }
        cursor.close();
        return exists;
    }

    // מחיקת משתמש
    public boolean deleteData(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_NAME + "=?", new String[]{user.getName()});
        boolean exists = cursor.getCount() > 0;
        if (exists) {
            db.delete(TABLE_NAME, COL_NAME + "=?", new String[]{user.getName()});
        }
        cursor.close();
        return exists;
    }

    // שליפת כל המשתמשים


    // שליפת משתמש לפי אימייל
    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_EMAIL + "=?", new String[]{email});
        User user = null;
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE_NUM));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD));
            user = new User(name, email, phone, password);
        }
        cursor.close();
        return user;
    }

    // שליפת ניקוד המשתמש
    public int getScore(String email) {
        return getStatByUsername(email, "score");
    }

    // עדכון ניקוד המשתמש
    public void updateScore(String email, int newScore) {
        updateStatByUsername(email, "score", newScore);
    }

    /* === פונקציות סטטיסטיקה כלליות (גישה לפי עמודה) === */

    private int getStatByUsername(String username, String column) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + column + " FROM " + TABLE_NAME + " WHERE email=?", new String[]{username});
        int stat = 0;
        if (cursor.moveToFirst()) {
            stat = cursor.getInt(cursor.getColumnIndexOrThrow(column));
        }
        cursor.close();
        return stat;
    }

    private void updateStatByUsername(String username, String column, int value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(column, value);
        db.update(TABLE_NAME, values, "email=?", new String[]{username});
    }

    /* === פונקציות סטטיסטיקה ייעודיות === */
    public int getGamesPlayed(String username) { return getStatByUsername(username, "games_played"); }
    public int getGamesWon(String username) { return getStatByUsername(username, "games_won"); }
    public int getGamesLost(String username) { return getStatByUsername(username, "games_lost"); }
    public int getGamesTied(String username) { return getStatByUsername(username, "games_tied"); }
    public int getCurrentWinStreak(String username) { return getStatByUsername(username, "current_win_streak"); }
    public int getLongestWinStreak(String username) { return getStatByUsername(username, "longest_win_streak"); }
    public int getTotalCardsDrawn(String username) { return getStatByUsername(username, "total_cards_drawn"); }
    public int getLargestBet(String username) { return getStatByUsername(username, "largest_bet"); }

    public void updateGamesPlayed(String username, int value) { updateStatByUsername(username, "games_played", value); }
    public void updateGamesWon(String username, int value) { updateStatByUsername(username, "games_won", value); }
    public void updateGamesLost(String username, int value) { updateStatByUsername(username, "games_lost", value); }
    public void updateGamesTied(String username, int value) { updateStatByUsername(username, "games_tied", value); }
    public void updateCurrentWinStreak(String username, int value) { updateStatByUsername(username, "current_win_streak", value); }
    public void updateLongestWinStreak(String username, int value) { updateStatByUsername(username, "longest_win_streak", value); }
    public void updateTotalCardsDrawn(String username, int value) { updateStatByUsername(username, "total_cards_drawn", value); }
    public void updateLargestBet(String username, int value) { updateStatByUsername(username, "largest_bet", value); }

    /* === פונקציות הגדרות (ווליום, נוטיפיקציות) === */
    public int getMusicVolume(String email) { return getStatByUsername(email, "music_volume"); }
    public void setMusicVolume(String email, int volume) { updateStatByUsername(email, "music_volume", volume); }

    public int getSoundEffectsVolume(String email) { return getStatByUsername(email, "sound_effects_volume"); }
    public void setSoundEffectsVolume(String email, int volume) { updateStatByUsername(email, "sound_effects_volume", volume); }

    public boolean getNotificationsEnabled(String email) {
        return getStatByUsername(email, "notifications_enabled") == 1;
    }
    public void setNotificationsEnabled(String email, boolean enabled) {
        updateStatByUsername(email, "notifications_enabled", enabled ? 1 : 0);
    }
}





