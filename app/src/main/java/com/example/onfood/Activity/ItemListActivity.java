package com.example.onfood.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onfood.Activity.OrderHistoryActivity;
import com.example.onfood.Activity.ProfileActivity;
import com.example.onfood.Item;
import com.example.onfood.ItemAdapter;
import com.example.onfood.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ItemListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewItems;
    private FirebaseFirestore db;
    private List<Item> itemList = new ArrayList<>();
    private Spinner categorySpinner;
    private ProgressBar progressBar;
    public ImageButton buttonAddToCart,buttnprofil, buttonback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);


        View navigationBar = getLayoutInflater().inflate(R.layout.navigation_bar, null);
        ((ViewGroup) findViewById(R.id.navigationContainer)).addView(navigationBar);

        ImageButton buttonCart = findViewById(R.id.buttonCart);
        ImageButton buttonprofil = findViewById(R.id.buttonProfile);
        ImageButton buttonback =findViewById(R.id.buttonBack);
        TextView navText =findViewById(R.id.navtext);
        navText.setText("MENU ");

        recyclerViewItems = findViewById(R.id.recyclerViewItems);
        progressBar = findViewById(R.id.progressBar);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));


        buttonCart.setOnClickListener(v -> startActivity(new Intent(ItemListActivity.this, OrderHistoryActivity.class)));
        buttonprofil.setOnClickListener(v -> startActivity(new Intent(ItemListActivity.this, ProfileActivity.class)));

        categorySpinner = findViewById(R.id.categorySpinner);
        recyclerViewItems = findViewById(R.id.recyclerViewItems);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        setupCategorySpinner();
        loadItemsFromFirestore();
    }

    private void setupCategorySpinner() {
        // Assuming you have predefined categories
        String[] categories = {"All", "Category1", "Category2", "Category3"};
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
        db.collection("MenuItem").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                itemList.clear(); // Clear the previous list
                for (DocumentSnapshot doc : task.getResult()) {
                    Item item = doc.toObject(Item.class);
                    if (item != null) {
                        item.setId(doc.getId());
                        itemList.add(item);
                    }
                }
                // Initially display all items
                recyclerViewItems.setAdapter(new ItemAdapter(itemList));
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
        recyclerViewItems.setAdapter(new ItemAdapter(filteredList));
    }
}