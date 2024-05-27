package com.example.ecoventur.ui.greenspace.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecoventur.R;
import com.example.ecoventur.ui.greenspace.Review;

import java.util.ArrayList;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>{
    private ArrayList<Review> reviews;
    public ReviewsAdapter(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }
    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_greenspace_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review currentReview = reviews.get(position);
        holder.bind(currentReview);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView TVReviewerName, TVReviewRating, TVReviewDesc, TVReviewTimestamp;
        ImageView IVReviewImage;
        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            TVReviewerName = itemView.findViewById(R.id.TVReviewerName);
            TVReviewRating = itemView.findViewById(R.id.TVReviewRating);
            TVReviewDesc = itemView.findViewById(R.id.TVReviewDesc);
            TVReviewTimestamp = itemView.findViewById(R.id.TVReviewTimestamp);
            IVReviewImage = itemView.findViewById(R.id.IVReviewImage);
        }
        public void bind(Review review) {
            TVReviewerName.setText(review.getReviewerName());
            TVReviewRating.setText("Rated: " + review.getRating() + "/5");
            if (review.getDescription() == null || review.getDescription().equals("")) {
                TVReviewDesc.setVisibility(View.GONE);
            }
            else {
                TVReviewDesc.setText(review.getDescription());
            }
            if (review.getImageLink() == null) {
                IVReviewImage.setVisibility(View.GONE);
            }
            else {
                Glide.with(itemView.getContext())
                        .load(review.getImageLink())
                        .into(IVReviewImage);
            }
            if (review.getTimestamp() == null) {
                TVReviewTimestamp.setText("Unknown post date");
            }
            else {
                TVReviewTimestamp.setText("Posted on " + review.getTimestamp() + ".");
            }
        }
    }
}
