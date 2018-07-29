package com.example.zxd1997.dict;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.io.BufferedWriter;
import java.util.Objects;

public class DictProvider extends ContentProvider {
    DatabaseHelper db;
    public DictProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Word word=new Word();
        word.setWord( values.getAsString("word"));
        word.setExplanation(values.getAsString("explanation"));
        word.setLevel(values.getAsInteger("level"));
        db.insert(word);
        return new Uri.Builder().build();
    }

    @Override
    public boolean onCreate() {
        db=new DatabaseHelper(Objects.requireNonNull(getContext()));
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return db.queryTest();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
