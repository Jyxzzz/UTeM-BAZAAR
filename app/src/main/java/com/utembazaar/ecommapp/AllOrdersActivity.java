package com.utembazaar.ecommapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utembazaar.ecommapp.adapters.AllOrdersAdapter;
import com.utembazaar.ecommapp.models.Item;
import com.utembazaar.ecommapp.models.Order;

import java.util.ArrayList;
import java.util.List;

public class AllOrdersActivity extends AppCompatActivity {

    private RecyclerView allOrdersRecyclerView;
    private AllOrdersAdapter allOrdersAdapter;
    private List<Order> orderList = new ArrayList<>();
    private DatabaseReference ordersRef;
    private DatabaseReference itemsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_orders);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        allOrdersRecyclerView = findViewById(R.id.allOrdersRecyclerView);
        allOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        allOrdersAdapter = new AllOrdersAdapter(this, orderList);
        allOrdersRecyclerView.setAdapter(allOrdersAdapter);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        CardView bcCardIcon = findViewById(R.id.bc_card_icon);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        CardView btnAddItem = findViewById(R.id.btnAddItem);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        CardView btnViewItems = findViewById(R.id.btnViewItems);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        CardView btnLogout = findViewById(R.id.btnLogout);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        CardView btnOrderItems = findViewById(R.id.bc_item_icon);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        CardView btnOrderAllItems = findViewById(R.id.bc_itemAll_icon);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        CardView btnHome = findViewById(R.id.home_button);

        // Get a reference to the Firebase database for orders and items
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        itemsRef = FirebaseDatabase.getInstance().getReference("items");

        // Get current user's ID (uploaderId)
        String currentUploaderId = mAuth.getCurrentUser().getUid();

        // Fetch orders uploaded by this user
        fetchOrdersForUploader(currentUploaderId);

        bcCardIcon.setOnClickListener(v -> startActivity(new Intent(AllOrdersActivity.this, UserProfileActivity.class)));

        btnAddItem.setOnClickListener(v -> startActivity(new Intent(AllOrdersActivity.this, AddItemActivity.class)));

        btnViewItems.setOnClickListener(v -> startActivity(new Intent(AllOrdersActivity.this, ItemDetailActivity.class)));
        btnOrderItems.setOnClickListener(v -> startActivity(new Intent(AllOrdersActivity.this, OrdersActivity.class)));
        btnOrderAllItems.setOnClickListener(v -> startActivity(new Intent(AllOrdersActivity.this, AllOrdersActivity.class)));
        btnHome.setOnClickListener(v -> startActivity(new Intent(AllOrdersActivity.this, HomeActivity.class)));
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(AllOrdersActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AllOrdersActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void fetchOrdersForUploader(String currentUploaderId) {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear(); // Clear the list to avoid duplicates

                for (DataSnapshot userOrdersSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot orderSnapshot : userOrdersSnapshot.getChildren()) {
                        Order order = orderSnapshot.getValue(Order.class);

                        // Log the fetched order for debugging
                        Log.d("AllOrdersActivity", "Order fetched: " + orderSnapshot.getKey() + " - " + order);

                        // Fetch the uploaderId from the items node
                        fetchUploaderIdFromItems(order, currentUploaderId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AllOrdersActivity.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUploaderIdFromItems(Order order, String currentUploaderId) {
        if (order != null && order.getItems() != null) {
            for (Item item : order.getItems()) {
                String itemId = item.getId();
                itemsRef.child(itemId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String uploaderId = dataSnapshot.child("uploaderId").getValue(String.class);

                        // Check if the logged-in user is the uploader
                        if (uploaderId != null && uploaderId.equals(currentUploaderId)) {
                            orderList.add(order); // Add only orders uploaded by this user
                            allOrdersAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the list
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(AllOrdersActivity.this, "Failed to load item details", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}