package com.utembazaar.ecommapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import com.utembazaar.ecommapp.models.Item;

public class ItemDetailActivity extends AppCompatActivity {
    private ImageView itemImageView;
    private TextView itemNameTextView, itemPriceTextView, itemDescriptionTextView;
    private Button buyButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        itemImageView = findViewById(R.id.itemImageView);
        itemNameTextView = findViewById(R.id.itemNameTextView);
        itemPriceTextView = findViewById(R.id.itemPriceTextView);
        itemDescriptionTextView = findViewById(R.id.itemDescriptionTextView);
        buyButton = findViewById(R.id.buyButton);
        // Get item data from intent
        Item item = (Item) getIntent().getSerializableExtra("item");

        if (item != null) {
            itemNameTextView.setText(item.getName());
            itemPriceTextView.setText(String.format("$%.2f", item.getPrice()));
            itemDescriptionTextView.setText(item.getDescription());

            // Load image using Glide or any other image loading library
            Glide.with(this).load(item.getImageUrl()).into(itemImageView);
        }

        buyButton.setOnClickListener(v -> {
            // Implement purchase logic (e.g., opening a payment gateway or handling transaction)
            // This could involve integrating with a payment API
            Toast.makeText(this, "Purchase feature not implemented", Toast.LENGTH_SHORT).show();
        });
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}