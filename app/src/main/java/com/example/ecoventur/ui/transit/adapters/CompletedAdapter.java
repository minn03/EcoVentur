package com.example.ecoventur.ui.transit.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecoventur.R;
import com.example.ecoventur.ui.transit.model.Completed;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CompletedAdapter extends RecyclerView.Adapter<CompletedAdapter.ViewHolder> {
    private final Context context;
    private List<Completed> allCompletedList;

    public CompletedAdapter(Context context, List<Completed> allCompletedList) {
        this.context = context;
        this.allCompletedList = allCompletedList;
    }

    public void setAllChallengesList(List<Completed> allChallengesList) {
        this.allCompletedList = allChallengesList;
    }

    @NonNull
    @Override
    public CompletedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.completed_item, parent, false);
        return new CompletedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedAdapter.ViewHolder holder, int position) {
        Completed completed = allCompletedList.get(position);

        // Bind data to the ViewHolder
        holder.titleTextView.setText(completed.getTitle());

        // Format and set start and end dates
        String formattedDates = formatDate(completed.getStartDate()) + " - " + formatDate(completed.getEndDate());
        holder.datesTextView.setText(formattedDates);

        // Load the image from the URL into the ImageView using Glide
        Glide.with(context)
                .load(completed.getImageUrl())
                .into(holder.imageView);

        // Bind tags to the ViewHolder
        holder.tagsLayout.removeAllViews(); // Clear existing tags
        for (int i = 0; i < completed.getTags().size(); i++) {
            String tag = completed.getTags().get(i);

            if (isNumeric(tag)) {
                //Do nothing
            } else {
                // If the tag is not a number, add it as a regular TextView
                TextView tagTextView = new TextView(context);
                tagTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                tagTextView.setText(tag);
                tagTextView.setTextSize(10);
                tagTextView.setTextColor(Color.BLACK);
                tagTextView.setBackgroundResource(R.drawable.tag_background);
                int padding = 8;
                tagTextView.setPadding(padding, padding, padding, padding);

                // Add margin to the right of each tag
                int marginRight = 10;
                ((LinearLayout.LayoutParams) tagTextView.getLayoutParams()).setMargins(0, 0, marginRight, 0);

                holder.tagsLayout.addView(tagTextView);
            }
        }
    }

    @Override
    public int getItemCount() {
        return allCompletedList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView titleTextView;
        TextView datesTextView;
        LinearLayout tagsLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.challenge_image);
            titleTextView = itemView.findViewById(R.id.challenge_title);
            datesTextView = itemView.findViewById(R.id.challenge_dates);
            tagsLayout = itemView.findViewById(R.id.tags);
        }
    }

    // Helper method to format date
    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    // Function to check if a string is numeric
    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
