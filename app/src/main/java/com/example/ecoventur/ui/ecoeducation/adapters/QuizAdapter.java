package com.example.ecoventur.ui.ecoeducation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoventur.R;
import com.example.ecoventur.ui.ecoeducation.models.Quiz;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ArticlesViewHolder> {

    private final List<Quiz> itemList;
    private Context context;

    public QuizAdapter(Context mContext, List<Quiz> itemList) {
        this.itemList = itemList;
        this.context = mContext;
    }

    @Override
    public ArticlesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.layout_quiz, parent, false);

        return new ArticlesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArticlesViewHolder holder, int position) {
        // Get the data model based on position
        Quiz currentItem = itemList.get(position);
        // Set item views
        holder.a.setText(currentItem.getA());
        holder.b.setText(currentItem.getB());
        holder.c.setText(currentItem.getC());
        holder.d.setText(currentItem.getD());

    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // ViewHolder class
    public static class ArticlesViewHolder extends RecyclerView.ViewHolder {
        public final MaterialButton a , b, c, d;
        public ArticlesViewHolder(View itemView) {
            super(itemView);

            // Find views by ID
            a = itemView.findViewById(R.id.a);
            b = itemView.findViewById(R.id.b);
            c = itemView.findViewById(R.id.c);
            d = itemView.findViewById(R.id.d);
        }
    }
}
