package com.example.grandwordremember;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class TestActivity extends AppCompatActivity {
    List<Word> words = new ArrayList<>();
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tset);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Cursor cursor = getContentResolver().query(Uri.parse("content://com.example.dict"), null, null, null, null);
        assert cursor != null;
        while (cursor.moveToNext()) {
            Log.d("word", "onCreate: " + cursor.getString(1) + cursor.getString(2));
            Word word = new Word();
            word.setWord(cursor.getString(1));
            word.setExplanation(cursor.getString(2));
            word.setLevel(cursor.getInt(3));
            word.getAnswers().add(word.getExplanation().replaceAll("\\[.*?\\]", ""));
            words.add(word);
        }
        cursor.close();
        Random random = new Random();
        for (int i = 0; i < words.size(); i++) {
            for (int j = 0; j < 3; j++) {
                int t;
                while ((t = random.nextInt(19)) == i) {
                }
                words.get(i).getAnswers().add(words.get(t).getExplanation().replaceAll("\\[.*?\\]", ""));
            }
            Collections.sort(words.get(i).getAnswers());
        }
        for (Word word : words) {
            Log.d("word", "onCreate: " + word.getWord() + " " + word.getAnswers());
        }
        recyclerView = findViewById(R.id.questions);
        recyclerView.setLayoutManager(new LinearLayoutManager(TestActivity.this));
        recyclerView.setAdapter(new WordAdapter(words));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.submit) {
            int i = 0, j = 0;
            for (Word word : words) {
                if (word.getSelected() == 0) {
                    Toast.makeText(TestActivity.this, "还未全部填选", Toast.LENGTH_LONG).show();
                    recyclerView.smoothScrollToPosition(i);
                    return true;
                }
                if (word.getAnswers().get(word.getSelected() - 1).equals(word.getExplanation().replaceAll("\\[.*?\\]", "")))
                    j++;
                i++;
            }
//            Toast.makeText(TestActivity.this,"正确 "+j+" 题",Toast.LENGTH_LONG).show();
            AlertDialog dialog = new AlertDialog.Builder(TestActivity.this)
                    .setTitle("完成测试").setMessage("共 20 个单词，答对 " + j + " 个")
                    .setPositiveButton("完成", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
            return true;
        }

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}