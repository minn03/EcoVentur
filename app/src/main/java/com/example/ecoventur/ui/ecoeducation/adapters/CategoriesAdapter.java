package com.example.ecoventur.ui.ecoeducation.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoventur.R;
import com.example.ecoventur.ui.ecoeducation.activities.QuizActivity;
import com.example.ecoventur.ui.ecoeducation.models.QuizCategories;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ArticlesViewHolder> {

    private final List<QuizCategories> itemList;
    private Context context;

    public CategoriesAdapter(Context mContext, List<QuizCategories> itemList) {
        this.itemList = itemList;
        this.context = mContext;
    }

    @Override
    public ArticlesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.categories, parent, false);

        // Return a new holder instance
        return new ArticlesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArticlesViewHolder holder, int position) {
        // Get the data model based on position
        QuizCategories currentItem = itemList.get(position);
        // Set item views
        holder.title.setText(currentItem.getName());

        holder.itemView.setOnClickListener(view->{
            //open article link with the url
            Intent intent = new Intent(context, QuizActivity.class);
            intent.putExtra("category",currentItem.getName());
            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // ViewHolder class
    public static class ArticlesViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;

        public ArticlesViewHolder(View itemView) {
            super(itemView);

            // Find views by ID
            title = itemView.findViewById(R.id.quiz);
        }
    }
}
