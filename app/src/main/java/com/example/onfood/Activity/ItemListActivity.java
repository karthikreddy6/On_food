package com.example.onfood.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ItemListActivity extends AppCompatActivity implements ItemAdapter.OnAddToCartListener {

    private RecyclerView recyclerViewItems;
    private FirebaseFirestore db;
    private List<Item> itemList = new ArrayList<>();
    private Spinner categorySpinner;
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
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));

        cartManager = CartManager.getInstance(this); // Initialize CartManager

        buttonCart.setOnClickListener(v -> startActivity(new Intent(ItemListActivity.this, OrderHistoryActivity.class)));
        buttonProfile.setOnClickListener(v -> startActivity(new Intent(ItemListActivity.this, ProfileActivity.class)));

        categorySpinner = findViewById(R.id.categorySpinner);
        setupCategorySpinner();
        loadItemsFromFirestore();
        buttonGoToCart.setOnClickListener(v -> gotoChart());
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Reload data or refresh UI
    }

    private void setupCategorySpinner() {
        String[] categories = {"All", "Tiffen", "drinks", "Category3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedCategory = categories[position];
                filterItemsByCategory(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
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
