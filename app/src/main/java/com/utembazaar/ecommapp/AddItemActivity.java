package com.utembazaar.ecommapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddItemActivity extends AppCompatActivity {
    private EditText itemNameEditText, itemPriceEditText, itemDescriptionEditText;
    private Button addImageButton, saveItemButton;
    private Uri imageUri;
    private DatabaseReference dbRef;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_add_item);

        // Adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase references
        dbRef = FirebaseDatabase.getInstance().getReference("items");
        storage = FirebaseStorage.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Initialize UI components
        itemNameEditText = findViewById(R.id.itemNameEditText);
        itemPriceEditText = findViewById(R.id.itemPriceEditText);
        itemDescriptionEditText = findViewById(R.id.itemDescriptionEditText);
        addImageButton = findViewById(R.id.addImageButton);
        saveItemButton = findViewById(R.id.saveItemButton);

        // Initialize CardView elements (correctly cast)
        CardView btnHome = findViewById(R.id.home_button);
        CardView btnProfile = findViewById(R.id.bc_card_icon);
        CardView btnAllOrders = findViewById(R.id.bc_itemAll_icon);
        CardView btnOrders = findViewById(R.id.bc_item_icon);
        CardView btnAddItem = findViewById(R.id.btnAddItem);
        CardView btnViewItems = findViewById(R.id.btnViewItems);
        CardView btnLogout = findViewById(R.id.btnLogout);

        // Set click listeners for CardView elements
        btnHome.setOnClickListener(v -> startActivity(new Intent(AddItemActivity.this, HomeActivity.class)));
        btnProfile.setOnClickListener(v -> startActivity(new Intent(AddItemActivity.this, UserProfileActivity.class)));
        btnAllOrders.setOnClickListener(v -> startActivity(new Intent(AddItemActivity.this, AllOrdersActivity.class)));
        btnOrders.setOnClickListener(v -> startActivity(new Intent(AddItemActivity.this, OrdersActivity.class)));
        btnAddItem.setOnClickListener(v -> startActivity(new Intent(AddItemActivity.this, AddItemActivity.class)));
        btnViewItems.setOnClickListener(v -> startActivity(new Intent(AddItemActivity.this, ItemDetailActivity.class)));
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(AddItemActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddItemActivity.this, LoginActivity.class));
            finish();
        });

        // Set click listeners for buttons
        addImageButton.setOnClickListener(view -> openImageSelector());
        saveItemButton.setOnClickListener(view -> uploadItem());
    }

    // Open image selector
    private void openImageSelector() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    // Handle image selection result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            ImageView selectedImageView = findViewById(R.id.selectedImageView);
            selectedImageView.setVisibility(View.VISIBLE);
            selectedImageView.setImageURI(imageUri);
        }
    }

    // Upload item details and image to Firebase
    private void uploadItem() {
        String itemName = itemNameEditText.getText().toString().trim();
        String itemPrice = itemPriceEditText.getText().toString().trim();
        String itemDescription = itemDescriptionEditText.getText().toString().trim();

        // Validate inputs
        if (itemName.isEmpty() || itemPrice.isEmpty() || itemDescription.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload image to Firebase Storage
        StorageReference storageRef = storage.getReference().child("images/" + UUID.randomUUID().toString());
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        fetchUploaderDetails(itemName, itemPrice, itemDescription, imageUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Fetch uploader's name and email from Firebase
    private void fetchUploaderDetails(String name, String price, String description, String imageUrl) {
        String uploaderId = mAuth.getCurrentUser().getUid();
        usersRef.child(uploaderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String uploaderName = dataSnapshot.child("name").getValue(String.class);
                    String uploaderEmail = dataSnapshot.child("email").getValue(String.class); // Fetch uploader's email
                    saveItemToRealtimeDatabase(name, price, description, imageUrl, uploaderId, uploaderName, uploaderEmail);
                } else {
                    Toast.makeText(AddItemActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddItemActivity.this, "Failed to fetch uploader details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Save item details to Firebase Realtime Database
    private void saveItemToRealtimeDatabase(String name, String price, String description, String imageUrl, String uploaderId, String uploaderName, String uploaderEmail) {
        String itemId = dbRef.push().getKey();

        Map<String, Object> item = new HashMap<>();
        item.put("id", itemId);
        item.put("name", name);
        item.put("price", Double.parseDouble(price));
        item.put("description", description);
        item.put("imageUrl", imageUrl);
        item.put("uploaderId", uploaderId);
        item.put("uploaderName", uploaderName);
        item.put("uploaderEmail", uploaderEmail); // Add uploader's email

        if (itemId != null) {
            dbRef.child(itemId).setValue(item)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to add item: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}