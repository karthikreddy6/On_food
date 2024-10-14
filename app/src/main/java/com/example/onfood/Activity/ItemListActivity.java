package com.example.onfood.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onfood.CartManager;
import com.example.onfood.Item;
import com.example.onfood.ItemAdapter;
import com.example.onfood.R;
import com.example.onfood.CategoryAdapter; // Make sure to import the new CategoryAdapter
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ItemListActivity extends AppCompatActivity implements ItemAdapter.OnAddToCartListener {

    private RecyclerView recyclerViewItems, recyclerViewCategories;
    private FirebaseFirestore db;
    private List<Item> itemList = new ArrayList<>();
    private ProgressBar progressBar;
    private CartManager cartManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        View navigationBar = getLayoutInflater().inflate(R.layout.navigation_bar, null);
        ((ViewGroup) findViewById(R.id.navigationContainer)).addView(navigationBar);

        ImageButton buttonCart = findViewById(R.id.buttonCart);
        ImageButton buttonProfile = findViewById(R.id.buttonProfile);
        TextView navText = findViewById(R.id.navtext);
        navText.setText("MENU ");

        buttonCart.setVisibility(View.VISIBLE);
        buttonProfile.setVisibility(View.VISIBLE);
        Button buttonGoToCart = findViewById(R.id.buttonGoToCart);
        recyclerViewItems = findViewById(R.id.recyclerViewItems);
        progressBar = findViewById(R.id.progressBar);
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories); // Initialize RecyclerView for categories

        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)); // Horizontal Layout for categories

        cartManager = CartManager.getInstance(this); // Initialize CartManager

        buttonCart.setOnClickListener(v -> startActivity(new Intent(ItemListActivity.this, OrderHistoryActivity.class)));
        buttonProfile.setOnClickListener(v -> startActivity(new Intent(ItemListActivity.this, ProfileActivity.class)));

        setupCategoryRecyclerView(); // Method to setup categories
        loadItemsFromFirestore(); // Load items from Firestore
        buttonGoToCart.setOnClickListener(v -> gotoChart());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data or refresh UI
    }

    private void setupCategoryRecyclerView() {
        final List<String> categories = new ArrayList<>();
        CategoryAdapter categoryAdapter = new CategoryAdapter(categories, selectedCategory -> {
            filterItemsByCategory(selectedCategory);
        });

        recyclerViewCategories.setAdapter(categoryAdapter);

        // Fetch categories from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Categories").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot doc : task.getResult()) {
                    String categoryName = doc.getString("name");
                    if (categoryName != null) {
                        categories.add(categoryName);
                    }
                }
                categoryAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(ItemListActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadItemsFromFirestore() {
        db = FirebaseFirestore.getInstance();
        db.collection("MenuItem").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                itemList.clear();
                for (DocumentSnapshot doc : task.getResult()) {
                    Item item = doc.toObject(Item.class);
                    if (item != null) {
                        item.setId(doc.getId());
                        itemList.add(item);
                    }
                }
                // Pass the CartManager to the adapter
                ItemAdapter adapter = new ItemAdapter(itemList, cartManager, this);
                recyclerViewItems.setAdapter(adapter);
            } else {
                Toast.makeText(ItemListActivity.this, "Failed to load items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterItemsByCategory(String category) {
        List<Item> filteredList = new ArrayList<>();
        for (Item item : itemList) {
            if (category.equals("All") || item.getCategory().equals(category)) {
                filteredList.add(item);
            }
        }
        recyclerViewItems.setAdapter(new ItemAdapter(filteredList, cartManager, this));
    }

    public void gotoChart() {
        if (cartManager.getCartItemsWithQuantities().isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(ItemListActivity.this, CartActivity.class));
        }
    }

    @Override
    public void onAddToCart(Item item) {
        // Handle the action after an item is added to the cart
    }
}