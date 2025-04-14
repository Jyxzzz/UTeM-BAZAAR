package com.utembazaar.ecommapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.utembazaar.ecommapp.R;
import com.utembazaar.ecommapp.models.Item;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Item> itemList;
    private Context context;
    private OnAddToCartListener addToCartListener; // Third parameter to handle "Add to Cart" clicks

    // Constructor to accept the List, Context, and AddToCartListener
    public ItemAdapter(List<Item> itemList, Context context, OnAddToCartListener addToCartListener) {
        this.itemList = itemList;
        this.context = context;
        this.addToCartListener = addToCartListener;  // Initialize the listener
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Item item = itemList.get(position);

        // Bind item details to the views
        holder.itemNameTextView.setText(item.getName());
        holder.itemPriceTextView.setText("RM" + item.getPrice());
        holder.uploaderNameTextView.setText("" + item.getUploaderName()); // Set the uploader name
        holder.uploaderEmailTextView.setText("Contact: " + item.getUploaderEmail()); // Set the uploader email

        // Load the item image using Glide
        Glide.with(context)
                .load(item.getImageUrl())
                .into(holder.itemImageView);

        // Set click listener for the "Add to Cart" button
        holder.addToCartButton.setOnClickListener(v ->
                addToCartListener.onAddToCart(item));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // ViewHolder for item views
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView, itemPriceTextView, uploaderNameTextView, uploaderEmailTextView;
        Button addToCartButton;
        ImageView itemImageView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            itemPriceTextView = itemView.findViewById(R.id.itemPriceTextView);
            uploaderNameTextView = itemView.findViewById(R.id.uploaderNameTextView); // Uploader name
            uploaderEmailTextView = itemView.findViewById(R.id.uploaderEmailTextView); // Uploader email
            itemImageView = itemView.findViewById(R.id.itemImageView);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }
    }

    // Interface for handling the "Add to Cart" action
    public interface OnAddToCartListener {
        void onAddToCart(Item item);
    }
}