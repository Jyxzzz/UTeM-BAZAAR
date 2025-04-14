package com.utembazaar.ecommapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.utembazaar.ecommapp.R;
import com.utembazaar.ecommapp.models.Item;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Item> cartItemList;
    private Context context;
    private OnQuantityChangeListener quantityChangeListener;

    // Constructor
    public CartAdapter(List<Item> cartItemList, Context context, OnQuantityChangeListener quantityChangeListener) {
        this.cartItemList = cartItemList;
        this.context = context;
        this.quantityChangeListener = quantityChangeListener;
    }
    // Overloaded constructor without the OnQuantityChangeListener (for CheckoutActivity)
    public CartAdapter(List<Item> itemList, Context context) {
        this.cartItemList = itemList;
        this.context = context;
        this.quantityChangeListener = null; // No quantity changes needed for checkout
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item_list, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Item item = cartItemList.get(position);
        holder.itemNameTextView.setText(item.getName());
        holder.itemPriceTextView.setText("RM" + item.getPrice());
        holder.itemQuantityTextView.setText(String.valueOf(item.getQuantity()));
        Glide.with(context).load(item.getImageUrl()).into(holder.itemImageView);

        // Increase quantity
        holder.increaseButton.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            item.setQuantity(newQuantity);
            holder.itemQuantityTextView.setText(String.valueOf(newQuantity));
            quantityChangeListener.onQuantityChanged(item, newQuantity);
        });

        // Decrease quantity
        holder.decreaseButton.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                int newQuantity = item.getQuantity() - 1;
                item.setQuantity(newQuantity);
                holder.itemQuantityTextView.setText(String.valueOf(newQuantity));
                quantityChangeListener.onQuantityChanged(item, newQuantity);
            }
        });

        // Remove item
        holder.removeButton.setOnClickListener(v -> {
            quantityChangeListener.onItemRemoved(item);
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    // ViewHolder for cart items
    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView, itemPriceTextView, itemQuantityTextView;
        Button increaseButton, decreaseButton, removeButton;
        ImageView itemImageView;

        public CartViewHolder(View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            itemPriceTextView = itemView.findViewById(R.id.itemPriceTextView);
            itemQuantityTextView = itemView.findViewById(R.id.itemQuantityTextView);
            itemImageView = itemView.findViewById(R.id.itemImageView);
            increaseButton = itemView.findViewById(R.id.increaseButton);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }

    // Interface for quantity change listener
    public interface OnQuantityChangeListener {
        void onQuantityChanged(Item item, int newQuantity);
        void onItemRemoved(Item item);
    }
}
