package com.example.zxd1997.dict;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private SQLiteDatabase db;

    DatabaseHelper(Context context) {
        File file = context.getDatabasePath("dict.db");
        if (!file.exists()) file.mkdirs();
        db = SQLiteDatabase.openOrCreateDatabase(file.getPath() + "/dict.db", null);
        createTable();
    }

    private void createTable() {
        db.execSQL("CREATE TABLE IF NOT EXISTS dict(_id integer primary key autoincrement, word varchar(64) unique, explanation text, level int default 0, modified_time timestamp)");
    }
    public void insertAll(List<Word> words){
        db.beginTransaction();
        for (Word word:words){
            insert(word);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }
    public void insert(Word word) {
        ContentValues values = new ContentValues();
        values.put("word", word.getWord());
        values.put("explanation", word.getExplanation());
        values.put("level", word.getLevel());
        values.put("modified_time", System.currentTimeMillis());
        db.insertWithOnConflict("dict", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void delete(String word) {
        String whereClause = "word=?";
        String[] whereArgs = {word};
        db.delete("dict", whereClause, whereArgs);
    }

    public List<Word> query(int mode, String like) {
        Cursor cursor;
        switch (mode) {
            case 1:
                cursor = queryAll();
                break;
            case 2:
                cursor = queryFirst(like);
                break;
            default:
                cursor = queryLike(like);
                break;
        }
        List<Word> words = new ArrayList<>();
        while (cursor.moveToNext()) {
            Word word = new Word();
            word.setWord(cursor.getString(1));
            word.setExplanation(cursor.getString(2));
            word.setLevel(cursor.getInt(3));
            words.add(word);
        }
        cursor.close();
        return words;
    }

    public Cursor queryTest(){
        return db.query("dict", null, null, null, null, null, "RANDOM()", "20");
    }
    private Cursor queryAll() {
        return db.query("dict", null, null, null, null, null, "word  COLLATE NOCASE", null);
    }

    private Cursor queryFirst(String first) {
        return db.query("dict", null, "word like '" + first + "%'", new String[]{}, null, null, "word  COLLATE NOCASE", null);
    }

    private Cursor queryLike(String like) {
        return db.query("dict", null, "word like " + "'%" + like + "%'", null, null, null, "word  COLLATE NOCASE", null);

    }
}
