package com.example.ecoventur.ui.ecoeducation.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecoventur.R;
import com.example.ecoventur.ui.ecoeducation.models.Tutorials;

import java.util.List;

public class MoreTutorialsAdapter extends RecyclerView.Adapter<MoreTutorialsAdapter.ArticlesViewHolder> {

    private final List<Tutorials> itemList;
    private Context context;

    public MoreTutorialsAdapter(Context mContext, List<Tutorials> itemList) {
        this.itemList = itemList;
        this.context = mContext;
    }

    @Override
    public ArticlesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View view = inflater.inflate(R.layout.more_adapter, parent, false);

        // Return a new holder instance
        return new ArticlesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArticlesViewHolder holder, int position) {
        // Get the data model based on position
        Tutorials currentItem = itemList.get(position);
        // Set item views
        Glide.with(context).load(currentItem.getImageUrl()).into(holder.banner);
        holder.title.setText(currentItem.getTitle());

        holder.itemView.setOnClickListener(view->{
            //open article link with the url
            String url = currentItem.getTutoUrl();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
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
        public final ImageView banner;

        public ArticlesViewHolder(View itemView) {
            super(itemView);

            // Find views by ID
            title = itemView.findViewById(R.id.title);
            banner = itemView.findViewById(R.id.bannerImage);
        }
    }
}
