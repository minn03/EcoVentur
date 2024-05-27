package com.example.ecoventur.ui.ecoeducation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoventur.R;
import com.example.ecoventur.ui.ecoeducation.models.Badges;

import java.util.List;

public class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.ArticlesViewHolder> {

    private final List<Badges> itemList;
    private Context context;

    public BadgeAdapter(Context mContext, List<Badges> itemList) {
        this.itemList = itemList;
        this.context = mContext;
    }

    @Override
    public ArticlesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.badge, parent, false);

        // Return a new holder instance
        return new ArticlesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArticlesViewHolder holder, int position) {
        // Get the data model based on position
        Badges currentItem = itemList.get(position);
        // Set item views
        holder.desc.setText(currentItem.getDescription());
        holder.name.setText(currentItem.getName());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // ViewHolder class
    public static class ArticlesViewHolder extends RecyclerView.ViewHolder {
        public final TextView name, desc;

        public ArticlesViewHolder(View itemView) {
            super(itemView);

            // Find views by ID
            name = itemView.findViewById(R.id.name);
            desc = itemView.findViewById(R.id.desc);
        }
    }
}
