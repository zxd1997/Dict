package com.example.zxd1997.dict;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Word> words;
    private boolean show=false;
    private Context context;
    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public WordAdapter(List<Word> words, Context context) {
        this.words = words;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder= (ViewHolder) holder;
        final Word word = words.get(position);
        viewHolder.word.setText(word.getWord());
        viewHolder.mean.setVisibility(View.GONE);
        viewHolder.mean.setText(word.getExplanation());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.mean.getVisibility()==View.GONE){
                    viewHolder.mean.setVisibility(View.VISIBLE);
                }else viewHolder.mean.setVisibility(View.GONE);
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(context, viewHolder.itemView);
                popupMenu.getMenuInflater().inflate(R.menu.long_click, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                new AlertDialog.Builder(context)
                                        .setTitle("删除").setMessage("是否删除？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new DatabaseHelper(context).delete(word.getWord());
//                                                Toast.makeText(context,"删除成功",Toast.LENGTH_LONG).show();
                                        Snackbar.make(v, "删除成功", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        words.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, words.size());
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).show();
                                break;
                            case R.id.edit:
                                final View v1 = LayoutInflater.from(context).inflate(R.layout.dialog_add, null);
                                final TextInputEditText wordtext = v1.findViewById(R.id.wordInput);
                                final TextInputEditText mean = v1.findViewById(R.id.meanInput);
                                final TextInputEditText level = v1.findViewById(R.id.levelInput);
                                wordtext.setText(word.getWord());
                                mean.setText(word.getExplanation());
                                level.setText(String.valueOf(word.getLevel()));
                                new android.support.v7.app.AlertDialog.Builder(context).setTitle("修改单词")
                                        .setView(v1).setPositiveButton("修改", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        word.setWord(wordtext.getText().toString());
                                        word.setExplanation(mean.getText().toString());
                                        word.setLevel(Integer.valueOf(level.getText().toString().equals("") ? "0" : level.getText().toString()));
                                        new DatabaseHelper(context).insert(word);
                                        notifyItemChanged(position);
//                                        Toast.makeText(context,"修改成功",Toast.LENGTH_LONG).show();
                                        Snackbar.make(v, "修改成功", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).show();
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
                return true;
            }
        });
        if (isShow()){
            viewHolder.mean.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView word;
        TextView mean;
        ViewHolder(View itemView) {
            super(itemView);
            word= itemView.findViewById(R.id.word);
            mean= itemView.findViewById(R.id.mean);
        }
    }

}
