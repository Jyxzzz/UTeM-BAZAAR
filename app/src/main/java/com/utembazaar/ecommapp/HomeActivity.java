package com.utembazaar.ecommapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utembazaar.ecommapp.adapters.ChatAdapter;
import com.utembazaar.ecommapp.adapters.ItemAdapter;
import com.utembazaar.ecommapp.models.Item;
import com.utembazaar.ecommapp.models.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView itemsRecyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    private List<Item> filteredItemList;
    private FirebaseAuth mAuth;
    private DatabaseReference cartRef;
    private SearchView searchView;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private EditText userInput;
    private ConstraintLayout chatDialog;
    private Spinner sortSpinner;
    private String currentSortOrder = "Alphabetical";


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cartRef = FirebaseDatabase.getInstance().getReference("cart").child(mAuth.getCurrentUser().getUid());

        CardView bcCardIcon = findViewById(R.id.bc_card_icon);
        CardView btnAddItem = findViewById(R.id.btnAddItem);
        CardView btnViewItems = findViewById(R.id.btnViewItems);
        CardView btnHome = findViewById(R.id.home_button);
        CardView btnLogout = findViewById(R.id.btnLogout);
        CardView btnOrderItems = findViewById(R.id.bc_item_icon);
        CardView btnOrderAllItems = findViewById(R.id.bc_itemAll_icon);

        FloatingActionButton floatingActionButton = findViewById(R.id.floating_action_button);
        FloatingActionButton chatFab = findViewById(R.id.chatFab);

        itemsRecyclerView = findViewById(R.id.itemsRecyclerView);
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        filteredItemList = new ArrayList<>();

        itemAdapter = new ItemAdapter(filteredItemList, this, item -> {
            showQuantityDialog(item);
        });
        itemsRecyclerView.setAdapter(itemAdapter);

        fetchItemsFromDatabase();

        floatingActionButton.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, CartActivity.class)));

        bcCardIcon.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, UserProfileActivity.class)));
        btnAddItem.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, AddItemActivity.class)));
        btnViewItems.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ItemDetailActivity.class)));
        btnOrderItems.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, OrdersActivity.class)));
        btnOrderAllItems.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, AllOrdersActivity.class)));
        btnHome.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, HomeActivity.class)));
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(HomeActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterItems(newText);
                return true;
            }
        });

        sortSpinner = findViewById(R.id.sortSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSortOrder = parent.getItemAtPosition(position).toString();
                filterItems(searchView.getQuery().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });



    // Chat UI Setup
        chatDialog = findViewById(R.id.chatDialog);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        userInput = findViewById(R.id.userInput);
        Button sendButton = findViewById(R.id.sendButton);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter();
        chatRecyclerView.setAdapter(chatAdapter);
        sendButton.setOnClickListener(this::onSendMessage);

        chatFab.setOnClickListener(v -> toggleChatDialog());
    }

    private void fetchItemsFromDatabase() {
        DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference("items");

        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item item = snapshot.getValue(Item.class);
                    if (item != null) {
                        item.setUploaderId(snapshot.child("uploaderId").getValue(String.class));
                        fetchUploaderName(item);
                        itemList.add(item);
                    }
                }
                filterItems(searchView.getQuery().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, "Failed to fetch items.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUploaderName(Item item) {
        if (item.getUploaderId() == null) {
            Log.e("HomeActivity", "uploaderId is null for item: " + item.getName());
            return;
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(item.getUploaderId());
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String uploaderName = dataSnapshot.child("name").getValue(String.class);
                    item.setUploaderName(uploaderName);
                    itemAdapter.notifyDataSetChanged();
                } else {
                    Log.e("HomeActivity", "User data does not exist for uploaderId: " + item.getUploaderId());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, "Failed to fetch uploader name.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showQuantityDialog(Item item) {
        int quantity = 1;
        addToCart(item, quantity);
    }

    private void addToCart(Item item, int quantity) {
        DatabaseReference cartItemRef = cartRef.child(item.getId());

        cartItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(HomeActivity.this, "Already Item added to cart", Toast.LENGTH_SHORT).show();
                } else {
                    item.setQuantity(quantity);
                    cartItemRef.setValue(item).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            cartItemRef.child("quantity").setValue(quantity);
                            Toast.makeText(HomeActivity.this, "Item added to cart", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HomeActivity.this, "Failed to add item to cart", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterItems(String query) {
        filteredItemList.clear();
        if (query.isEmpty()) {
            filteredItemList.addAll(itemList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Item item : itemList) {
                if (item.getName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredItemList.add(item);
                }
            }
        }

        sortItems(currentSortOrder);
        itemAdapter.notifyDataSetChanged();
    }

    private void sortItems(String sortOrder) {
        switch (sortOrder) {
            case "Alphabetical":
                Collections.sort(filteredItemList, new Comparator<Item>() {
                    @Override
                    public int compare(Item item1, Item item2) {
                        return item1.getName().compareToIgnoreCase(item2.getName());
                    }
                });
                break;
            case "Price":
                Collections.sort(filteredItemList, new Comparator<Item>() {
                    @Override
                    public int compare(Item item1, Item item2) {
                        return Double.compare(item1.getPrice(), item2.getPrice());
                    }
                });
                break;
            // Add more cases for other sorting options if needed
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (chatDialog.getVisibility() == View.VISIBLE) {
            toggleChatDialog();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit and log out?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mAuth.signOut();
                            Toast.makeText(HomeActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                            finish();
                        }
                    })
                    .create().show();
        }
    }

    public void onSendMessage(View view) {
        String userMessage = userInput.getText().toString();
        if (!userMessage.isEmpty()) {
            chatAdapter.addMessage(new Message("user", userMessage));

            String botResponse = getBotResponse(userMessage);
            chatAdapter.addMessage(new Message("bot", botResponse));

            userInput.setText("");
            chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }

    private String getBotResponse(String userMessage) {
        switch (userMessage.toLowerCase()) {
            case "help":
                return "How can I assist you?";
            case "settings":
                return "You can adjust settings here.";
            default:
                return "I didn't understand that.";
        }
    }

    private void toggleChatDialog() {
        if (chatDialog.getVisibility() == View.VISIBLE) {
            chatDialog.setVisibility(View.GONE);
            Log.d("ChatDialog", "Chat dialog hidden");
        } else {
            chatDialog.setVisibility(View.VISIBLE);
            Log.d("ChatDialog", "Chat dialog shown");
        }
    }
}