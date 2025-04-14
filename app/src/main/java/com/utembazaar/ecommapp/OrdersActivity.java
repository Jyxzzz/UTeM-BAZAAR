package com.utembazaar.ecommapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.utembazaar.ecommapp.adapters.OrdersAdapter;
import com.utembazaar.ecommapp.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private RecyclerView ordersRecyclerView;
    private OrdersAdapter ordersAdapter;
    private List<Order> orderList;
    private FirebaseAuth mAuth;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_orders);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        CardView bcCardIcon = findViewById(R.id.bc_card_icon);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        CardView btnHome = findViewById(R.id.home_button);
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        ordersRef = FirebaseDatabase.getInstance().getReference("orders").child(userId);

        // Initialize UI components
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        ordersAdapter = new OrdersAdapter(orderList, this);
        ordersRecyclerView.setAdapter(ordersAdapter);

        bcCardIcon.setOnClickListener(v -> startActivity(new Intent(OrdersActivity.this, UserProfileActivity.class)));

        btnAddItem.setOnClickListener(v -> startActivity(new Intent(OrdersActivity.this, AddItemActivity.class)));

        btnViewItems.setOnClickListener(v -> startActivity(new Intent(OrdersActivity.this, ItemDetailActivity.class)));
        btnOrderItems.setOnClickListener(v -> startActivity(new Intent(OrdersActivity.this, OrdersActivity.class)));
        btnOrderAllItems.setOnClickListener(v -> startActivity(new Intent(OrdersActivity.this, AllOrdersActivity.class)));
        btnHome.setOnClickListener(v -> startActivity(new Intent(OrdersActivity.this, HomeActivity.class)));
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(OrdersActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(OrdersActivity.this, LoginActivity.class));
            finish();
        });
        fetchOrdersFromFirebase();
    }


    private void fetchOrdersFromFirebase() {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orderList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }
                ordersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(OrdersActivity.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
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