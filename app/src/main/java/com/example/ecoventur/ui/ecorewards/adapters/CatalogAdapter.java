package com.example.ecoventur.ui.ecorewards.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecoventur.R;
import com.example.ecoventur.ui.ecorewards.models.Catalog;

import java.util.ArrayList;

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Catalog> catalogList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public CatalogAdapter(Context context, ArrayList<Catalog> catalogList) {
        this.context = context;
        this.catalogList = catalogList;
    }

    public void updateList(ArrayList<Catalog> newList) {
        catalogList.clear();
        catalogList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Catalog catalog = catalogList.get(position);
        holder.voucherTitle.setText(catalog.getVoucherTitle());
        holder.ecoCoins.setText(String.valueOf(catalog.getEcoCoins()));
        Glide.with(context).load(catalog.getImgURL1()).into(holder.img1);
        Glide.with(context).load(catalog.getImgURL2()).into(holder.img2);

        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return catalogList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView voucherTitle;
        TextView ecoCoins;
        ImageView img1;
        ImageView img2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            voucherTitle = itemView.findViewById(R.id.vouchertitle);
            ecoCoins = itemView.findViewById(R.id.vouchervalue);
            img1 = itemView.findViewById(R.id.villagegrocer);
            img2 = itemView.findViewById(R.id.coinbag);
        }
    }
}
