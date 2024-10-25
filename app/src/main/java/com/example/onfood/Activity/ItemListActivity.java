package com.example.onfood.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.onfood.HorizontalItemAdapter; // Import the new HorizontalItemAdapter
import com.example.onfood.R;
import com.example.onfood.CategoryAdapter; // Ensure to import the new CategoryAdapter
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ItemListActivity extends AppCompatActivity implements ItemAdapter.OnAddToCartListener {

    private RecyclerView recyclerViewItems, recyclerViewCategories, recyclerViewHorizontalItems;
    private FirebaseFirestore db;
    private List<Item> itemList = new ArrayList<>();
    private List<Item> horizontalItemList = new ArrayList<>(); // New list for horizontal items
    private ProgressBar progressBar;
    private CartManager cartManager;
    private BadgeDrawable badgeDrawable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        View navigationBar = getLayoutInflater().inflate(R.layout.navigation_bar, null);
        ((ViewGroup) findViewById(R.id.navigationContainer)).addView(navigationBar);

        ImageButton buttonCart = findViewById(R.id.buttonCart);
        ImageButton buttonProfile = findViewById(R.id.buttonProfile);
        TextView navText = findViewById(R.id.navtext);
        navText.setText("MENU");

        buttonCart.setVisibility(View.VISIBLE);
        buttonProfile.setVisibility(View.VISIBLE);

        recyclerViewItems = findViewById(R.id.recyclerViewItems);
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories); // Horizontal RecyclerView for categories
        recyclerViewHorizontalItems = findViewById(R.id.recyclerViewHorizontalItems); // New horizontal RecyclerView for items

        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)); // Horizontal Layout for categories
        recyclerViewHorizontalItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)); // New horizontal layout for items

        cartManager = CartManager.getInstance(this); // Initialize CartManager
        setupBottomNavigation();

        buttonCart.setOnClickListener(v -> startActivity(new Intent(ItemListActivity.this, OrderHistoryActivity.class)));
        buttonProfile.setOnClickListener(v -> startActivity(new Intent(ItemListActivity.this, ProfileActivity.class)));

        setupCategoryRecyclerView();
        loadItemsFromFirestore(); // Load items from Firestore
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                return true;
            } else if (itemId == R.id.navigation_cart) {
                gotoChart(); // Navigate to the cart
                return true;
            }
            return false;
        });

        // Create a badge for the cart icon
        badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.navigation_cart);
        badgeDrawable.setVisible(false); // Make it invisible initially
        badgeDrawable.setNumber(0); // Set the initial count to zero
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge(); // Update badge whenever the activity is resumed
    }

    private void setupCategoryRecyclerView() {
        final List<String> categories = new ArrayList<>();
        CategoryAdapter categoryAdapter = new CategoryAdapter(categories, selectedCategory -> {
            filterItemsByCategory(selectedCategory);
        });

        recyclerViewCategories.setAdapter(categoryAdapter);

        // Fetch categories from Firestore
        db = FirebaseFirestore.getInstance();
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

        // Fetch vertical items
        db.collection("MenuItem").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                itemList.clear(); // Clear the vertical item list
                for (DocumentSnapshot doc : task.getResult()) {
                    Item item = doc.toObject(Item.class);
                    if (item != null) {
                        item.setId(doc.getId());
                        itemList.add(item); // Add to vertical items
                    }
                }
                // Pass the CartManager to the adapter for vertical items
                ItemAdapter adapter = new ItemAdapter(itemList, cartManager, this);
                recyclerViewItems.setAdapter(adapter);
            } else {
                Toast.makeText(ItemListActivity.this, "Failed to load vertical items", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch horizontal items from another path
        db.collection("SplMenuItem").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                horizontalItemList.clear(); // Clear the horizontal item list
                for (DocumentSnapshot doc : task.getResult()) {
                    Item item = doc.toObject(Item.class);
                    if (item != null) {
                        item.setId(doc.getId());
                        horizontalItemList.add(item); // Add to horizontal items
                    }
                }
                // Set up the horizontal item view adapter
                HorizontalItemAdapter horizontalAdapter = new HorizontalItemAdapter(horizontalItemList);
                recyclerViewHorizontalItems.setAdapter(horizontalAdapter);
            } else {
                Toast.makeText(ItemListActivity.this, "Failed to load horizontal items", Toast.LENGTH_SHORT).show();
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
        updateCartBadge(); // Update badge count when an item is added to the cart
    }

    private void updateCartBadge() {
        int itemCount = cartManager.getCartItemsWithQuantities().size();
        if (itemCount > 0) {
            badgeDrawable.setVisible(true);
            badgeDrawable.setNumber(itemCount);
        } else {
            badgeDrawable.setVisible(false);
        }
    }
}