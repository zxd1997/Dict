package com.example.zxd1997.dict;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuWrapperFactory;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ProgressDialog pd;
    WordAdapter adapter;
    RecyclerView recyclerView;
    DatabaseHelper helper;
    List<Word> words;
    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(final Message msg) {
            if (msg.what==1){
                pd.setProgress(Integer.valueOf(msg.obj.toString()));
                return true;
            }
            final JsonParser parser = new JsonParser();
            final JsonArray jsonArray = parser.parse(msg.obj.toString()).getAsJsonArray();
            pd.dismiss();
            pd=new ProgressDialog(MainActivity.this);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setTitle("下载单词");
            pd.setMessage("下载中");
            pd.setCanceledOnTouchOutside(false);
            pd.setMax(jsonArray.size());
            pd.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    words.clear();
                    int i=1;
                    for (JsonElement e : jsonArray) {
                        Word word = new Gson().fromJson(e, Word.class);
                        words.add(word);
//                        helper.insert(word);
                        Message message=Message.obtain();
                        message.obj=i;
                        message.what=1;
                        handler.sendMessage(message);
                        i++;
                        Log.d("word", "run: "+word.getWord());
                    }
//                    words=helper.query(1,"");
                    helper.insertAll(words);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            pd.dismiss();
                        }
                    });
                }
            }).start();
            return true;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView=findViewById(R.id.list);
        helper=new DatabaseHelper(MainActivity.this);
//        helper.createDatabase();
        words=helper.query(1,"");
        adapter=new WordAdapter(words);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this,DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
        if (id == R.id.download) {
            pd = new ProgressDialog(MainActivity.this);
            pd.setTitle("下载单词");
            pd.setMessage("下载中");
            pd.setCanceledOnTouchOutside(false);
            pd.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    InputStream is = null;
                    try {
                        is = getAssets().open("dict123456.txt");
                        int length = is.available();
                        byte[] buffer = new byte[length];
                        is.read(buffer);
                        String result = new String(buffer, "utf8");
                        Message message=Message.obtain();
                        message.obj=result;
                        handler.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            return true;
        }
        if (id==R.id.show){
            Log.d("show", "onOptionsItemSelected: "+item.isChecked());
            item.setChecked(!item.isChecked());
            adapter.setShow(item.isChecked());
            adapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
