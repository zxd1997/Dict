package com.example.zxd1997.dict;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Word> words;
    private boolean show=false;

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public WordAdapter(List<Word> words) {
        this.words = words;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder= (ViewHolder) holder;
        viewHolder.word.setText(words.get(position).getWord());
        viewHolder.mean.setVisibility(View.GONE);
        viewHolder.mean.setText(words.get(position).getExplanation());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.mean.getVisibility()==View.GONE){
                    viewHolder.mean.setVisibility(View.VISIBLE);
                }else viewHolder.mean.setVisibility(View.GONE);
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
