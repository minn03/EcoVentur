package com.example.ecoventur.ui.ecorewards.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecoventur.R;
import com.example.ecoventur.ui.ecorewards.models.Voucher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Voucher> voucherList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public VoucherAdapter(Context context, ArrayList<Voucher> voucherList) {
        this.context = context;
        this.voucherList = voucherList;
    }

    public void updateList(List<Voucher> vouchers) {
        // Update adapter list and notify data set changed
        this.voucherList.clear();
        this.voucherList.addAll(vouchers);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.voucher, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Voucher voucher = voucherList.get(position);
        holder.voucherName.setText(voucher.getVoucherTitle());
        holder.itemView.setOnClickListener(view -> {
            // Create a bundle to pass data to QRDisplayFragment
            Bundle bundle = new Bundle();
            bundle.putString("voucherTitle", voucher.getVoucherTitle());
            bundle.putString("voucherImage", voucher.getImageUrl());
            // Add other necessary details...

            // Navigate to QRDisplayFragment
            Navigation.findNavController(view).navigate(R.id.action_e00101_to_QRDisplay, bundle);
        });

        // Convert Timestamp to a formatted String for displaying expiry date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(voucher.getExpiryDate().toDate());
        holder.expiryDate.setText(formattedDate);

        // Load image into ImageView using Picasso (replace placeholder_image with your placeholder resource)
        Glide.with(context).load(voucher.getImageUrl())
                .placeholder(R.drawable.villagegrocer)
                .into(holder.voucherImage);
    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView voucherName;
        TextView expiryDate;
        ImageView voucherImage;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            voucherName = itemView.findViewById(R.id.voucherName);
            expiryDate = itemView.findViewById(R.id.expirydate);
            voucherImage = itemView.findViewById(R.id.voucherimg);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
}
