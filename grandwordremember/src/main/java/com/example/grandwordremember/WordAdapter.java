package com.example.grandwordremember;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Word> words;
    private boolean showAnswer = false;

    WordAdapter(List<Word> words) {
        this.words = words;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    public boolean isShowAnswer() {
        return showAnswer;
    }

    public void setShowAnswer(boolean showAnswer) {
        this.showAnswer = showAnswer;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.question, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        final Word word = words.get(position);
        List<String> answer = word.getAnswers();
        Log.d("size", "onBindViewHolder: " + answer.size());
        for (String s : answer) {
            Log.d("word", "onBindViewHolder: " + s);
        }
        if (isShowAnswer()) {
            switch (word.getRight()) {
                case 0:
                    viewHolder.a1.setBackgroundColor(Color.RED);
                    viewHolder.a2.setBackgroundColor(Color.WHITE);
                    viewHolder.a3.setBackgroundColor(Color.WHITE);
                    viewHolder.a4.setBackgroundColor(Color.WHITE);
                    break;
                case 1:
                    viewHolder.a1.setBackgroundColor(Color.WHITE);
                    viewHolder.a2.setBackgroundColor(Color.RED);
                    viewHolder.a3.setBackgroundColor(Color.WHITE);
                    viewHolder.a4.setBackgroundColor(Color.WHITE);
                    break;
                case 2:
                    viewHolder.a1.setBackgroundColor(Color.WHITE);
                    viewHolder.a2.setBackgroundColor(Color.WHITE);
                    viewHolder.a3.setBackgroundColor(Color.RED);
                    viewHolder.a4.setBackgroundColor(Color.WHITE);
                    break;
                case 3:
                    viewHolder.a1.setBackgroundColor(Color.WHITE);
                    viewHolder.a2.setBackgroundColor(Color.WHITE);
                    viewHolder.a3.setBackgroundColor(Color.WHITE);
                    viewHolder.a4.setBackgroundColor(Color.RED);
                    break;
            }
        }
        switch (word.getSelected()) {
            case 0:
                viewHolder.radioGroup.clearCheck();
                break;
            case 1:
                viewHolder.radioGroup.check(R.id.answer1);
                break;
            case 2:
                viewHolder.radioGroup.check(R.id.answer2);
                break;
            case 3:
                viewHolder.radioGroup.check(R.id.answer3);
                break;
            case 4:
                viewHolder.radioGroup.check(R.id.answer4);
                break;
        }
        viewHolder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.answer1:
                        word.setSelected(1);
                        break;
                    case R.id.answer2:
                        word.setSelected(2);
                        break;
                    case R.id.answer3:
                        word.setSelected(3);
                        break;
                    case R.id.answer4:
                        word.setSelected(4);
                        break;
                }
            }
        });
        viewHolder.word.setText(word.getWord());
        viewHolder.a1.setText(answer.get(0));
        viewHolder.a2.setText(answer.get(1));
        viewHolder.a3.setText(answer.get(2));
        viewHolder.a4.setText(answer.get(3));
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView word;
        RadioButton a1;
        RadioButton a2;
        RadioButton a3;
        RadioButton a4;
        RadioGroup radioGroup;

        ViewHolder(View itemView) {
            super(itemView);
            radioGroup = itemView.findViewById(R.id.group);
            word = itemView.findViewById(R.id.word);
            a1 = itemView.findViewById(R.id.answer1);
            a2 = itemView.findViewById(R.id.answer2);
            a3 = itemView.findViewById(R.id.answer3);
            a4 = itemView.findViewById(R.id.answer4);
        }
    }

}
