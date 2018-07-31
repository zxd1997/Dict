package com.example.zxd1997.dict;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ProgressDialog pd;
    WordAdapter adapter;
    RecyclerView recyclerView;
    Observer observer = new Observer(new Handler());
    DatabaseHelper helper;
    List<Word> words;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(final Message msg) {
            if (msg.what == 1) {
                pd.setProgress(Integer.valueOf(msg.obj.toString()));
                return true;
            }
            final JsonParser parser = new JsonParser();
            final JsonArray jsonArray = parser.parse(msg.obj.toString()).getAsJsonArray();
            pd.dismiss();
            pd = new ProgressDialog(MainActivity.this);
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
                    int i = 1;
                    for (JsonElement e : jsonArray) {
                        Word word = new Gson().fromJson(e, Word.class);
                        words.add(word);
//                        helper.insert(word);
                        Message message = Message.obtain();
                        message.obj = i;
                        message.what = 1;
                        handler.sendMessage(message);
                        i++;
                        Log.d("word", "run: " + word.getWord());
                    }
//                    words=helper.query(1,"");
                    helper.insertAll(words);
                    words.clear();
                    words.addAll(helper.query(1, ""));
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
        getContentResolver().registerContentObserver(Uri.parse("content://com.example.dict"), true, observer);
        recyclerView = findViewById(R.id.list);
        helper = new DatabaseHelper(MainActivity.this);
//        helper.createDatabase();
        words = helper.query(1, "");
        adapter = new WordAdapter(words, MainActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        String t[] = new String[]{"全部", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    words.clear();
                    words.addAll(helper.query(2, String.valueOf((char) ('a' + position - 1))));
                    adapter.notifyDataSetChanged();
                    Log.d("first", "onItemSelected: " + String.valueOf((char) ('a' + position - 1)));
                } else {
                    words.clear();
                    words.addAll(helper.query(1, ""));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, t));
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
                        final Word word1 = new Word();
                        word1.setWord(word.getText().toString());
                        word1.setExplanation(mean.getText().toString());
                        word1.setLevel(Integer.valueOf(level.getText().toString().equals("") ? "0" : level.getText().toString()));
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                helper.insert(word1);
                                words.clear();
                                words.addAll(helper.query(1, ""));
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }).start();
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
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(observer);
    }

    class Observer extends ContentObserver {
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        private Handler handler;

        public Observer(Handler handler) {
            super(handler);
            this.handler = handler;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    words.clear();
                    words.addAll(helper.query(1, ""));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setMaxWidth(1080);
        searchView.onActionViewExpanded();
        searchView.setIconifiedByDefault(false);
//        SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        assert searchManager != null;
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Log.d("Search", "onCreate: " + query);
                searchView.clearFocus();
                Log.d("search", "onQueryTextSubmit: " + query);
                words.clear();
                words.addAll(helper.query(3, query));
                adapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });
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
                        Message message = Message.obtain();
                        message.obj = result;
                        handler.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            return true;
        }
        if (id == R.id.show) {
            Log.d("show", "onOptionsItemSelected: " + item.isChecked());
            item.setChecked(!item.isChecked());
            adapter.setShow(item.isChecked());
            adapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
