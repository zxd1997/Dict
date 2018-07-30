package com.example.grandwordremember;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        Cursor cursor=getContentResolver().query(Uri.parse("content://com.example.dict"),null,null,null,null);
//        assert cursor != null;
//        while (cursor.moveToNext()){
//            Log.d("word", "onCreate: "+cursor.getString(1)+cursor.getString(2));
//        }
        Button button = findViewById(R.id.test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final View v = getLayoutInflater().inflate(R.layout.dialog_add, null);
                final TextInputEditText word = v.findViewById(R.id.wordInput);
                final TextInputEditText mean = v.findViewById(R.id.meanInput);
                final TextInputEditText level = v.findViewById(R.id.levelInput);
                new AlertDialog.Builder(MainActivity.this).setTitle("添加单词")
                        .setView(v).setPositiveButton("添加", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ContentValues values = new ContentValues();
                                values.put("word", word.getText().toString());
                                values.put("explanation", mean.getText().toString());
                                values.put("level", Integer.valueOf(level.getText().toString().equals("") ? "0" : level.getText().toString()));
                                getContentResolver().insert(Uri.parse("content://com.example.dict"), values);
                                dialog.dismiss();
                                Snackbar.make(view, "添加成功", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
