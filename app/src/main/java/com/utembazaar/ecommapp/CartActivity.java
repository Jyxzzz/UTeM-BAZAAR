package com.utembazaar.ecommapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.utembazaar.ecommapp.adapters.CartAdapter;
import com.utembazaar.ecommapp.models.Item;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<Item> cartItemList;
    private DatabaseReference cartRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_cart); // Ensure this layout exists
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        CardView bcCardIcon = findViewById(R.id.bc_card_icon);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        CardView btnLogout = findViewById(R.id.btnLogout);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        CardView btnOrderItems = findViewById(R.id.bc_item_icon);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        CardView btnOrderAllItems = findViewById(R.id.bc_itemAll_icon);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        CardView btnHome = findViewById(R.id.home_button);
        Button checkoutButton = findViewById(R.id.checkoutButton);
        cartRecyclerView = findViewById(R.id.cartRecyclerView);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItemList, this, new CartAdapter.OnQuantityChangeListener() {
            @Override
            public void onQuantityChanged(Item item, int newQuantity) {
                updateItemQuantityInCart(item, newQuantity);
            }

            @Override
            public void onItemRemoved(Item item) {
                removeItemFromCart(item);
            }
        });
        cartRecyclerView.setAdapter(cartAdapter);

        // Fetch the cart items from Firebase
        fetchCartItems();
        checkoutButton.setOnClickListener(view -> startActivity(new Intent(CartActivity.this, CheckoutActivity.class)));
        bcCardIcon.setOnClickListener(v -> startActivity(new Intent(CartActivity.this, UserProfileActivity.class)));
        btnOrderItems.setOnClickListener(v -> startActivity(new Intent(CartActivity.this, OrdersActivity.class)));
        btnOrderAllItems.setOnClickListener(v -> startActivity(new Intent(CartActivity.this, AllOrdersActivity.class)));
        btnHome.setOnClickListener(v -> startActivity(new Intent(CartActivity.this, HomeActivity.class)));
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(CartActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CartActivity.this, LoginActivity.class));
            finish();
        });
    }

    // Fetch cart items from Firebase
    private void fetchCartItems() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference("cart").child(userId);

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cartItemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item item = snapshot.getValue(Item.class);
                    if (item != null) {
                        cartItemList.add(item);
                    }
                }
                cartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CartActivity.this, "Failed to load cart items.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Update item quantity in the cart
    private void updateItemQuantityInCart(Item item, int newQuantity) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference cartItemRef = cartRef.child(item.getId());

        cartItemRef.child("quantity").setValue(newQuantity).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(CartActivity.this, "Quantity updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CartActivity.this, "Failed to update quantity", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Remove item from the cart
    private void removeItemFromCart(Item item) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference cartItemRef = cartRef.child(item.getId());

        cartItemRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(CartActivity.this, "Item removed from cart", Toast.LENGTH_SHORT).show();
                cartItemList.remove(item); // Remove the item from the list
                cartAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(CartActivity.this, "Failed to remove item", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
