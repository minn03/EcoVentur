package com.example.ecoventur.ui.ecorewards.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoventur.R;
import com.example.ecoventur.ui.ecorewards.models.Transaction;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Transaction> transactionList;
    private boolean isEarning;

    public TransactionAdapter(Context context, ArrayList<Transaction> transactionList, boolean isEarning) {
        this.context = context;
        this.transactionList = transactionList;
        this.isEarning = isEarning;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.textVoucherTitle.setText(transaction.getTransactionTitle());
        int ecoCoins = transaction.getEcoCoins();

        if (isEarning) {
            holder.textEcoCoins.setText("+ " + ecoCoins + " ec");
        } else {
            holder.textEcoCoins.setText("- " + ecoCoins + " ec");
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public void setTransactionList(ArrayList<Transaction> transactions) {
        this.transactionList = transactions;
        notifyDataSetChanged();
    }

    public void clearTransactionList() {
        this.transactionList.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textVoucherTitle;
        TextView textEcoCoins;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textVoucherTitle = itemView.findViewById(R.id.textVoucherTitle);
            textEcoCoins = itemView.findViewById(R.id.textEcoCoins);
        }
    }
}
