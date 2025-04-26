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
    private static final int DATABASE_VERSION = 1; // Bumped version to trigger onUpgrade
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
                "score INTEGER DEFAULT 100)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
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


    public void updateScore(String email, int newScore) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("score", newScore);
        db.update(TABLE_NAME, contentValues, COL_EMAIL + "=?", new String[]{email});
    }
}





