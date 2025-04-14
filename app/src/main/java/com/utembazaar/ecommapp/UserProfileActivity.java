package com.utembazaar.ecommapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;


public class UserProfileActivity extends AppCompatActivity {
    private TextView userNameTextView, userEmailTextView;
    private Button editProfileButton, logoutButton;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        CardView bcCardIcon = findViewById(R.id.bc_card_icon);
        CardView btnAddItem = findViewById(R.id.btnAddItem);
        CardView btnViewItems = findViewById(R.id.btnViewItems);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        CardView btnHome = findViewById(R.id.home_button);
        CardView btnLogout = findViewById(R.id.btnLogout);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        CardView btnOrderItems = findViewById(R.id.bc_item_icon);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        CardView btnOrderAllItems = findViewById(R.id.bc_itemAll_icon);
        mAuth = FirebaseAuth.getInstance();
//
        userNameTextView = findViewById(R.id.userNameTextView);
        userEmailTextView = findViewById(R.id.userEmailTextView);
        editProfileButton = findViewById(R.id.editProfileButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Set user details
        userEmailTextView.setText(mAuth.getCurrentUser().getEmail());
        // Assume we have a method getUserName() to fetch user's name
        userNameTextView.setText(mAuth.getCurrentUser().getUid());

        editProfileButton.setOnClickListener(v -> {
            // Navigate to edit profile activity
            Toast.makeText(this, "Edit Profile feature not implemented", Toast.LENGTH_SHORT).show();
        });

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
            finish();
        });

        bcCardIcon.setOnClickListener(v -> startActivity(new Intent(UserProfileActivity.this, UserProfileActivity.class)));

        btnAddItem.setOnClickListener(v -> startActivity(new Intent(UserProfileActivity.this, AddItemActivity.class)));

        btnViewItems.setOnClickListener(v -> startActivity(new Intent(UserProfileActivity.this, ItemDetailActivity.class)));
        btnOrderItems.setOnClickListener(v -> startActivity(new Intent(UserProfileActivity.this, OrdersActivity.class)));
        btnHome.setOnClickListener(v -> startActivity(new Intent(UserProfileActivity.this, HomeActivity.class)));
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(UserProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
            finish();
        });
        btnOrderAllItems.setOnClickListener(v -> startActivity(new Intent(UserProfileActivity.this, AllOrdersActivity.class)));
    }

    private String getUserName() {
        // Placeholder method to get user name from Firestore or other source
        return "User Name";
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

}