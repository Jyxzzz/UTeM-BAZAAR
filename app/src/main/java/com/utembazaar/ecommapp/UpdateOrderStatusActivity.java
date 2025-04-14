package com.utembazaar.ecommapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.utembazaar.ecommapp.models.Order;

import java.util.ArrayList;
import java.util.List;

public class UpdateOrderStatusActivity extends AppCompatActivity {
    private TextView orderIdTextView;
    private TextView currentStatusTextView;
    private Spinner statusSpinner;
    private Button updateStatusButton;

    private String orderId;
    private String currentStatus;
    private FirebaseAuth mAuth;
    private DatabaseReference ordersRef;
    private List<Order> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_order_status);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
        // Get order details from Intent
        orderId = getIntent().getStringExtra("ORDER_ID");
        currentStatus = getIntent().getStringExtra("ORDER_STATUS");


        // Initialize Firebase Database reference for orders
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        // Initialize UI components
        orderIdTextView = findViewById(R.id.orderIdTextView);
        currentStatusTextView = findViewById(R.id.currentStatusTextView);
        statusSpinner = findViewById(R.id.statusSpinner);
        updateStatusButton = findViewById(R.id.updateStatusButton);

        // Set order details in TextViews
        orderIdTextView.setText("Order ID: " + orderId);
        currentStatusTextView.setText("Current Status: " + currentStatus);

        searchAndUpdateOrderStatusOnCreate(orderId);

        // Populate the status spinner with options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.order_status_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        // Set the spinner's default value to the current status
        if (currentStatus != null) {
            int spinnerPosition = adapter.getPosition(currentStatus);
            statusSpinner.setSelection(spinnerPosition);
        }

        // Update status button click listener
        updateStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newStatus = statusSpinner.getSelectedItem().toString();
                searchAndUpdateOrderStatus(orderId, newStatus);
            }
        });

        bcCardIcon.setOnClickListener(v -> startActivity(new Intent(UpdateOrderStatusActivity.this, UserProfileActivity.class)));

        btnAddItem.setOnClickListener(v -> startActivity(new Intent(UpdateOrderStatusActivity.this, AddItemActivity.class)));

        btnViewItems.setOnClickListener(v -> startActivity(new Intent(UpdateOrderStatusActivity.this, ItemDetailActivity.class)));
        btnOrderItems.setOnClickListener(v -> startActivity(new Intent(UpdateOrderStatusActivity.this, OrdersActivity.class)));
        btnOrderAllItems.setOnClickListener(v -> startActivity(new Intent(UpdateOrderStatusActivity.this, AllOrdersActivity.class)));

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(UpdateOrderStatusActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UpdateOrderStatusActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void searchAndUpdateOrderStatusOnCreate(String orderId) {
        // Querying nested orderId inside orderDetails
        ordersRef.orderByChild(orderId+"/orderId").equalTo(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot orderDetailSnapshot : orderSnapshot.getChildren()) {
                            Order order = orderDetailSnapshot.getValue(Order.class);

                        }
                    }



                } else {
                    Toast.makeText(UpdateOrderStatusActivity.this, "Order not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateOrderStatusActivity.this, "Error fetching orders", Toast.LENGTH_SHORT).show();
            }
        });
    }

   private void searchAndUpdateOrderStatus(String orderId, String newStatus) {
        // Querying nested orderId inside orderDetails
        ordersRef.orderByChild(orderId+"/orderId").equalTo(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                if (dataSnapshot.exists()) {


                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        Order order = dataSnapshot.getValue(Order.class);
                        orderList.add(order);
                        updateOrderStatus(orderSnapshot.getKey(), newStatus, orderId);
                    }

                } else {
                    Toast.makeText(UpdateOrderStatusActivity.this, "Order not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateOrderStatusActivity.this, "Error fetching orders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateOrderStatus(String orderId, String newStatus, String orderID) {
        // Update the order status in Firebase
        ordersRef.child(orderId).child(orderID).child("status").setValue(newStatus)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(UpdateOrderStatusActivity.this, "Order status updated!", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity after updating
                    } else {
                        Toast.makeText(UpdateOrderStatusActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}