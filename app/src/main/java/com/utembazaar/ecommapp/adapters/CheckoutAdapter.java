package com.utembazaar.ecommapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utembazaar.ecommapp.R;
import com.utembazaar.ecommapp.models.Item;

import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder> {

    private List<Item> itemList;
    private Context context;

    public CheckoutAdapter(List<Item> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.checkout_item_list, parent, false);
        return new CheckoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.itemNameTextView.setText(item.getName());
        holder.itemPriceTextView.setText("RM" + item.getPrice());
        holder.itemQuantityTextView.setText("Quantity: " + item.getQuantity());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class CheckoutViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView, itemPriceTextView, itemQuantityTextView;

        public CheckoutViewHolder(View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            itemPriceTextView = itemView.findViewById(R.id.itemPriceTextView);
            itemQuantityTextView = itemView.findViewById(R.id.itemQuantityTextView);
        }
    }
}
