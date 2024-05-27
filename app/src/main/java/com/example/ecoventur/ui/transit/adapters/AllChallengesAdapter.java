package com.example.ecoventur.ui.transit.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecoventur.R;
import com.example.ecoventur.ui.transit.model.AllChallenges;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AllChallengesAdapter extends RecyclerView.Adapter<AllChallengesAdapter.ViewHolder> {

    private final Context context;
    private static List<AllChallenges> allChallengesList;
    private static OnChallengeItemClickListener onChallengeItemClickListener;


    public AllChallengesAdapter(Context context, List<AllChallenges> allChallengesList) {
        this.context = context;
        this.allChallengesList = allChallengesList;
    }

    public void setAllChallengesList(List<AllChallenges> allChallengesList) {
        this.allChallengesList = allChallengesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_challenges_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AllChallenges allChallenges = allChallengesList.get(position);

        // Bind data to the ViewHolder
        holder.titleTextView.setText(allChallenges.getTitle());

        // Format and set start and end dates
        String formattedDates = formatDate(allChallenges.getStartDate()) + " - " + formatDate(allChallenges.getEndDate());
        holder.datesTextView.setText(formattedDates);

        // Load the image from the URL into the ImageView using Glide
        Glide.with(context)
                .load(allChallenges.getImageUrl())
                .into(holder.imageView);

        // Bind tags to the ViewHolder
        holder.tagsLayout.removeAllViews(); // Clear existing tags
        for (int i = 0; i < allChallenges.getTags().size(); i++) {
            String tag = allChallenges.getTags().get(i);

            if (isNumeric(tag)) {
                // If the tag is a number, add "50" and Ecocoins image in a horizontal layout
                LinearLayout horizontalLayout = new LinearLayout(context);
                horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                horizontalLayout.setBackgroundResource(R.drawable.tag_background);

                // Add "50" TextView
                TextView numberTextView = new TextView(context);
                numberTextView.setText(tag);
                numberTextView.setTextSize(10);

                // Set text color to black
                numberTextView.setTextColor(Color.BLACK);

                int padding = 8;
                numberTextView.setPadding(padding, padding, padding, padding);
                numberTextView.setGravity(Gravity.CENTER_VERTICAL);

                // Add "50" TextView to the horizontal layout
                horizontalLayout.addView(numberTextView);

                // Add Ecocoins image
                ImageView ecocoinsImageView = new ImageView(context);
                ecocoinsImageView.setImageResource(R.drawable.ecocoin);
                int imageSize = 30; // Adjust the size as needed
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        imageSize,
                        imageSize
                );
                layoutParams.gravity = Gravity.CENTER_VERTICAL; // Center the image vertically
                ecocoinsImageView.setLayoutParams(layoutParams);

                // Add Ecocoins image to the horizontal layout
                horizontalLayout.addView(ecocoinsImageView);

                // Add margin to the right of each tag
                int marginRight = 8;
                ((LinearLayout.LayoutParams) ecocoinsImageView.getLayoutParams()).setMargins(0, 0, marginRight, 0);

                // Add the horizontal layout to the tags layout
                holder.tagsLayout.addView(horizontalLayout);
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
        return allChallengesList.size();
    }

    public void setOnChallengeItemClickListener(OnChallengeItemClickListener listener) {
        this.onChallengeItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView titleTextView;
        TextView datesTextView;
        LinearLayout tagsLayout;
        ImageView ecocoinsImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.challenge_image);
            titleTextView = itemView.findViewById(R.id.challenge_title);
            datesTextView = itemView.findViewById(R.id.challenge_dates);
            tagsLayout = itemView.findViewById(R.id.tags);
            ecocoinsImageView = itemView.findViewById(R.id.ecocoins);

            // Set click listener for the item view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onChallengeItemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onChallengeItemClickListener.onChallengeItemClick(position);
                }

                // Assuming you have a NavController instance in your Fragment/Activity
                NavController navController = Navigation.findNavController(view);

                // Use Bundle instead of BundleKt in Java
                Bundle bundle = new Bundle();
                bundle.putParcelable("challenge", (Parcelable) allChallengesList.get(position));

                navController.navigate(R.id.action_allChallengesFragment_to_challengeDetailFragment, bundle);
            }
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
