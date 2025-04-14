package com.utembazaar.ecommapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.utembazaar.ecommapp.models.Order;

public class OrderDetailActivity extends AppCompatActivity {
    private TextView orderIdTextView, totalPriceTextView, statusTextView;
    private RecyclerView orderItemsRecyclerView;
    private Button updateStatusButton;
    private Order order;
    private DatabaseReference ordersRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        orderIdTextView = findViewById(R.id.orderIdTextView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        statusTextView = findViewById(R.id.statusTextView);
        orderItemsRecyclerView = findViewById(R.id.orderItemsRecyclerView);
        updateStatusButton = findViewById(R.id.updateStatusButton);

        // Set up RecyclerView for order items
        orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the order ID from the intent
        String orderId = getIntent().getStringExtra("ORDER_ID");
        Log.e("3333333333333333", orderId);

        // Get a reference to the Firebase database for orders
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        // Fetch order details
        fetchOrderDetails(orderId);

        // Set up the update status button
        updateStatusButton.setOnClickListener(v -> {
            // Example logic for updating the order status
            String newStatus = "Completed"; // Change this as needed
            updateOrderStatus(orderId, newStatus);
        });
    }

    private void fetchOrderDetails(String orderId) {
        ordersRef.orderByChild(orderId+"/orderId").equalTo(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("OrderDetailActivity", "DataSnapshot: " + dataSnapshot.toString());
                if (dataSnapshot.exists()) {
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        order = orderSnapshot.getValue(Order.class);
                        if (order != null) {

                            displayOrderDetails(order);
                        }
                    }
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Order not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OrderDetailActivity.this, "Failed to load order details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayOrderDetails(Order order) {
        Log.e("77777777777777777", String.valueOf(order.getOrderId()));
        orderIdTextView.setText("Order ID: " + order.getOrderId());
        totalPriceTextView.setText("Total Price: RM" + order.getTotalPrice());
        statusTextView.setText("Status: " + order.getStatus());


    }

    private void updateOrderStatus(String orderId, String newStatus) {
        ordersRef.orderByChild("orderId").equalTo(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    orderSnapshot.child("status").getRef().setValue(newStatus)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(OrderDetailActivity.this, "Order status updated!", Toast.LENGTH_SHORT).show();
                                    fetchOrderDetails(orderId); // Refresh the order details
                                } else {
                                    Toast.makeText(OrderDetailActivity.this, "Failed to update order status", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OrderDetailActivity.this, "Failed to update order status", Toast.LENGTH_SHORT).show();
            }
        });
    }
}