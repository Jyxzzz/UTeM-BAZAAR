package com.utembazaar.ecommapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.PayPalRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utembazaar.ecommapp.adapters.CheckoutAdapter;
import com.utembazaar.ecommapp.models.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity implements PaymentMethodNonceCreatedListener, BraintreeCancelListener, BraintreeErrorListener {

    private static final String TAG = "CheckoutActivity";
    private static final String BRAINTREE_CLIENT_TOKEN = "eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiJleUowZVhBaU9pSktWMVFpTENKaGJHY2lPaUpGVXpJMU5pSXNJbXRwWkNJNklqSXdNVGd3TkRJMk1UWXRjMkZ1WkdKdmVDSXNJbWx6Y3lJNkltaDBkSEJ6T2k4dllYQnBMbk5oYm1SaWIzZ3VZbkpoYVc1MGNtVmxaMkYwWlhkaGVTNWpiMjBpZlEuZXlKbGVIQWlPakUzTXpjMU5UYzNOalVzSW1wMGFTSTZJakEyWVRsalpXUmlMVGN3TkRBdE5EaGlaaTA1TXpRNUxXWmhNakpqTURFek1ESm1OU0lzSW5OMVlpSTZJbmMyY3pNM2RHdDZhM0Z3TW5KM2QzY2lMQ0pwYzNNaU9pSm9kSFJ3Y3pvdkwyRndhUzV6WVc1a1ltOTRMbUp5WVdsdWRISmxaV2RoZEdWM1lYa3VZMjl0SWl3aWJXVnlZMmhoYm5RaU9uc2ljSFZpYkdsalgybGtJam9pZHpaek16ZDBhM3ByY1hBeWNuZDNkeUlzSW5abGNtbG1lVjlqWVhKa1gySjVYMlJsWm1GMWJIUWlPbVpoYkhObGZTd2ljbWxuYUhSeklqcGJJbTFoYm1GblpWOTJZWFZzZENKZExDSnpZMjl3WlNJNld5SkNjbUZwYm5SeVpXVTZWbUYxYkhRaVhTd2liM0IwYVc5dWN5STZlMzE5LjE2SG1xajk1WjVLV2Q4LW1IVjNrMmNmTnp3SGU5WDRUQ2k4MVpwVHRGRkdBZ3RjQ1NtbDFhQVkydC10VXZGcEhrcm4yMEk3dk5DNVZfVGhwRFhudHZRIiwiY29uZmlnVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzL3c2czM3dGt6a3FwMnJ3d3cvY2xpZW50X2FwaS92MS9jb25maWd1cmF0aW9uIiwiZ3JhcGhRTCI6eyJ1cmwiOiJodHRwczovL3BheW1lbnRzLnNhbmRib3guYnJhaW50cmVlLWFwaS5jb20vZ3JhcGhxbCIsImRhdGUiOiIyMDE4LTA1LTA4IiwiZmVhdHVyZXMiOlsidG9rZW5pemVfY3JlZGl0X2NhcmRzIl19LCJjbGllbnRBcGlVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvdzZzMzd0a3prcXAycnd3dy9jbGllbnRfYXBpIiwiZW52aXJvbm1lbnQiOiJzYW5kYm94IiwibWVyY2hhbnRJZCI6Inc2czM3dGt6a3FwMnJ3d3ciLCJhc3NldHNVcmwiOiJodHRwczovL2Fzc2V0cy5icmFpbnRyZWVnYXRld2F5LmNvbSIsImF1dGhVcmwiOiJodHRwczovL2F1dGgudmVubW8uc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbSIsInZlbm1vIjoib2ZmIiwiY2hhbGxlbmdlcyI6W10sInRocmVlRFNlY3VyZUVuYWJsZWQiOnRydWUsImFuYWx5dGljcyI6eyJ1cmwiOiJodHRwczovL29yaWdpbi1hbmFseXRpY3Mtc2FuZC5zYW5kYm94LmJyYWludHJlZS1hcGkuY29tL3c2czM3dGt6a3FwMnJ3d3cifSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImJpbGxpbmdBZ3JlZW1lbnRzRW5hYmxlZCI6dHJ1ZSwiZW52aXJvbm1lbnROb05ldHdvcmsiOnRydWUsInVudmV0dGVkTWVyY2hhbnQiOmZhbHNlLCJhbGxvd0h0dHAiOnRydWUsImRpc3BsYXlOYW1lIjoiVXRlbUJhemFhciIsImNsaWVudElkIjpudWxsLCJiYXNlVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhc3NldHNVcmwiOiJodHRwczovL2NoZWNrb3V0LnBheXBhbC5jb20iLCJkaXJlY3RCYXNlVXJsIjpudWxsLCJlbnZpcm9ubWVudCI6Im9mZmxpbmUiLCJicmFpbnRyZWVDbGllbnRJZCI6Im1hc3RlcmNsaWVudDMiLCJtZXJjaGFudEFjY291bnRJZCI6InV0ZW1iYXphYXIiLCJjdXJyZW5jeUlzb0NvZGUiOiJVU0QifX0=";
    private RecyclerView checkoutRecyclerView;
    private CheckoutAdapter checkoutAdapter;
    private List<Item> checkoutItemsList;
    private FirebaseAuth mAuth;
    private DatabaseReference cartRef, orderRef;
    private TextView checkoutTotalPriceTextView;
    private Button confirmPurchaseButton, confirmAddressButton;
    private double totalPrice = 0.0;
    private String address;
    private BraintreeFragment mBraintreeFragment;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        cartRef = FirebaseDatabase.getInstance().getReference("cart").child(mAuth.getCurrentUser().getUid());
        orderRef = FirebaseDatabase.getInstance().getReference("orders").child(mAuth.getCurrentUser().getUid());

        // Initialize UI components
        checkoutRecyclerView = findViewById(R.id.checkoutRecyclerView);
        checkoutTotalPriceTextView = findViewById(R.id.checkoutTotalPriceTextView);
        confirmPurchaseButton = findViewById(R.id.confirmPurchaseButton);
        confirmAddressButton = findViewById(R.id.confirmAddressButton);

        checkoutRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        checkoutItemsList = new ArrayList<>();
        checkoutAdapter = new CheckoutAdapter(checkoutItemsList, this); // Using the new CheckoutAdapter
        checkoutRecyclerView.setAdapter(checkoutAdapter);

        fetchCartItems();

        // Initialize card views and set click listeners
        CardView bcCardIcon = findViewById(R.id.bc_card_icon);
        CardView btnLogout = findViewById(R.id.btnLogout);
        CardView btnHome = findViewById(R.id.home_button);
        CardView btnOrderAllItems = findViewById(R.id.bc_itemAll_icon);
        CardView btnOrderItems = findViewById(R.id.bc_item_icon);

        bcCardIcon.setOnClickListener(v -> startActivity(new Intent(CheckoutActivity.this, UserProfileActivity.class)));
        btnOrderAllItems.setOnClickListener(v -> startActivity(new Intent(CheckoutActivity.this, AllOrdersActivity.class)));
        btnHome.setOnClickListener(v -> startActivity(new Intent(CheckoutActivity.this, HomeActivity.class)));
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(CheckoutActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CheckoutActivity.this, LoginActivity.class));
            finish();
        });
        btnOrderItems.setOnClickListener(v -> startActivity(new Intent(CheckoutActivity.this, OrdersActivity.class)));

        confirmAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddressDialog();
            }
        });

        confirmPurchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmPurchase();
            }
        });

        // Initialize BraintreeFragment
        try {
            mBraintreeFragment = BraintreeFragment.newInstance(this, BRAINTREE_CLIENT_TOKEN);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to initialize Braintree", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Address");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                address = input.getText().toString();
                confirmPurchaseButton.setVisibility(View.VISIBLE);
                confirmAddressButton.setVisibility(View.GONE);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void fetchCartItems() {
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                checkoutItemsList.clear();
                totalPrice = 0.0; // Reset total price

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item item = snapshot.getValue(Item.class);
                    if (item != null) {
                        checkoutItemsList.add(item);
                        totalPrice += item.getPrice() * item.getQuantity();
                    }
                }

                checkoutTotalPriceTextView.setText("Total Price: RM" + String.format("%.2f", totalPrice));
                checkoutAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CheckoutActivity.this, "Failed to load cart items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmPurchase() {
        if (checkoutItemsList.isEmpty()) {
            Toast.makeText(CheckoutActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mBraintreeFragment == null) {
            Toast.makeText(CheckoutActivity.this, "Braintree is not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        PayPalRequest request = new PayPalRequest(String.valueOf(totalPrice))
                .currencyCode("MYR")
                .displayName("Purchase")
                .intent(PayPalRequest.INTENT_SALE);

        PayPal.requestOneTimePayment(mBraintreeFragment, request);
    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        // Send this nonce to server
        String nonce = paymentMethodNonce.getNonce();
        Log.d(TAG, "Payment nonce: " + nonce);

        // Create a unique order ID
        String orderId = orderRef.push().getKey();

        if (orderId != null) {
            Map<String, Object> orderDetails = new HashMap<>();
            orderDetails.put("items", checkoutItemsList);
            orderDetails.put("status", "pending");
            orderDetails.put("totalPrice", totalPrice);
            orderDetails.put("timestamp", System.currentTimeMillis());
            orderDetails.put("orderId", orderId);
            orderDetails.put("address", address);
            orderDetails.put("paymentNonce", nonce); // Add payment nonce to the order
            orderDetails.put("uploaderId", mAuth.getCurrentUser().getUid()); // Add uploaderId to the order

            // Save the order in Firebase
            orderRef.child(orderId).setValue(orderDetails)
                    .addOnSuccessListener(aVoid -> {
                        clearCart();
                        Toast.makeText(CheckoutActivity.this, "Purchase confirmed!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CheckoutActivity.this, OrdersActivity.class));
                    })
                    .addOnFailureListener(e -> Toast.makeText(CheckoutActivity.this, "Failed to confirm purchase", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onCancel(int requestCode) {
        Toast.makeText(this, "Payment canceled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Exception error) {
        Toast.makeText(this, "Payment error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void clearCart() {
        cartRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    checkoutItemsList.clear();
                    checkoutAdapter.notifyDataSetChanged();
                    checkoutTotalPriceTextView.setText("Total Price: RM0.00");
                })
                .addOnFailureListener(e -> Toast.makeText(CheckoutActivity.this, "Failed to clear cart", Toast.LENGTH_SHORT).show());
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}