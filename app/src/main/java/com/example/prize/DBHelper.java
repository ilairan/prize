package com.example.prize;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Userdata.db";
    private static final int DATABASE_VERSION = 3;  // Updated version
    private static final String TABLE_NAME = "Userdetails";
    private static final String COL_NAME = "name";
    private static final String COL_EMAIL = "email";
    private static final String COL_PHONE_NUM = "phone_num";
    private static final String COL_PASSWORD = "password";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
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
                // Add this to the create table statement

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
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

    // Insert, update, delete, and score methods remain unchanged...

    // === SETTINGS METHODS ===

    // Get music volume (0-100)
    public int getMusicVolume(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT music_volume FROM " + TABLE_NAME + " WHERE email = ?", new String[]{email});
        int volume = 100;
        if (cursor.moveToFirst()) {
            volume = cursor.getInt(cursor.getColumnIndexOrThrow("music_volume"));
        }
        cursor.close();
        return volume;
    }
    private void updateStat(String email, String column, int value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(column, value);
        db.update(TABLE_NAME, values, COL_EMAIL + "=?", new String[]{email});
    }





    public void setMusicVolume(String email, int volume) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("music_volume", volume);
        db.update(TABLE_NAME, values, COL_EMAIL + "=?", new String[]{email});
    }

    // Get sound effects volume (0-100)
    public int getSoundEffectsVolume(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT sound_effects_volume FROM " + TABLE_NAME + " WHERE email = ?", new String[]{email});
        int volume = 100;
        if (cursor.moveToFirst()) {
            volume = cursor.getInt(cursor.getColumnIndexOrThrow("sound_effects_volume"));
        }
        cursor.close();
        return volume;
    }

    public void setSoundEffectsVolume(String email, int volume) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sound_effects_volume", volume);
        db.update(TABLE_NAME, values, COL_EMAIL + "=?", new String[]{email});
    }

    // Get notifications toggle (true/false)
    public boolean getNotificationsEnabled(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT notifications_enabled FROM " + TABLE_NAME + " WHERE email = ?", new String[]{email});
        boolean enabled = true;
        if (cursor.moveToFirst()) {
            enabled = cursor.getInt(cursor.getColumnIndexOrThrow("notifications_enabled")) == 1;
        }
        cursor.close();
        return enabled;
    }

    public void setNotificationsEnabled(String email, boolean enabled) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("notifications_enabled", enabled ? 1 : 0);
        db.update(TABLE_NAME, values, COL_EMAIL + "=?", new String[]{email});
    }

public boolean insertUserdata(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, user.getName());
        contentValues.put(COL_PHONE_NUM, user.getPhone_num());
        contentValues.put(COL_PASSWORD, user.getPassword());
        contentValues.put(COL_EMAIL, user.getEmail());
        contentValues.put("score", 100); // Initialize score

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public boolean updateUserdata(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_PHONE_NUM, user.getPhone_num());
        contentValues.put(COL_PASSWORD, user.getPassword());
        contentValues.put(COL_EMAIL, user.getEmail());

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_NAME + "=?", new String[]{user.getName()});
        if (cursor.getCount() > 0) {
            long result = db.update(TABLE_NAME, contentValues, COL_NAME + "=?", new String[]{user.getName()});
            cursor.close();
            return result != -1;
        } else {
            cursor.close();
            return false;
        }
    }

    public boolean deleteData(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_NAME + "=?", new String[]{user.getName()});
        if (cursor.getCount() > 0) {
            long result = db.delete(TABLE_NAME, COL_NAME + "=?", new String[]{user.getName()});
            cursor.close();
            return result != -1;
        } else {
            cursor.close();
            return false;
        }
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone_num"));
                String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));

                User user = new User(name, email, phone, password);
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userList;
    }

    // New method: get user's score
    public int getScore(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT score FROM " + TABLE_NAME + " WHERE email = ?", new String[]{email});
        int score = -1;
        if (cursor.moveToFirst()) {
            score = cursor.getInt(cursor.getColumnIndexOrThrow("score"));
        }
        cursor.close();
        return score;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE email = ?", new String[]{email});
        User user = null;
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone_num"));
            String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            user = new User(name, email, phone, password);
        }
        cursor.close();
        return user;
    }


    public void updateScore(String email, int newScore) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("score", newScore);
        db.update(TABLE_NAME, contentValues, COL_EMAIL + "=?", new String[]{email});
    }

    // General get stat
    private int getStatByUsername(String username, String column) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + column + " FROM " + TABLE_NAME + " WHERE email = ?", new String[]{username});
        int stat = 0;
        if (cursor.moveToFirst()) {
            stat = cursor.getInt(cursor.getColumnIndexOrThrow(column));
        }
        cursor.close();
        return stat;
    }

    // General update stat
    private void updateStatByUsername(String username, String column, int value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, value);
        db.update(TABLE_NAME, contentValues, "email=?", new String[]{username});
    }

    // Public getters
    public int getGamesPlayed(String username) { return getStatByUsername(username, "games_played"); }
    public int getGamesWon(String username) { return getStatByUsername(username, "games_won"); }
    public int getGamesLost(String username) { return getStatByUsername(username, "games_lost"); }
    public int getGamesTied(String username) { return getStatByUsername(username, "games_tied"); }
    public int getCurrentWinStreak(String username) { return getStatByUsername(username, "current_win_streak"); }
    public int getLongestWinStreak(String username) { return getStatByUsername(username, "longest_win_streak"); }
    public int getTotalCardsDrawn(String username) { return getStatByUsername(username, "total_cards_drawn"); }
    public int getLargestBet(String username) { return getStatByUsername(username, "largest_bet"); }

    // Public setters
    public void updateGamesPlayed(String username, int value) { updateStatByUsername(username, "games_played", value); }
    public void updateGamesWon(String username, int value) { updateStatByUsername(username, "games_won", value); }
    public void updateGamesLost(String username, int value) { updateStatByUsername(username, "games_lost", value); }
    public void updateGamesTied(String username, int value) { updateStatByUsername(username, "games_tied", value); }
    public void updateCurrentWinStreak(String username, int value) { updateStatByUsername(username, "current_win_streak", value); }
    public void updateLongestWinStreak(String username, int value) { updateStatByUsername(username, "longest_win_streak", value); }
    public void updateTotalCardsDrawn(String username, int value) { updateStatByUsername(username, "total_cards_drawn", value); }
    public void updateLargestBet(String username, int value) { updateStatByUsername(username, "largest_bet", value); }



}





