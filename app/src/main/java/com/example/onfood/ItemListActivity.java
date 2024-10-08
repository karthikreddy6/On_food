package com.example.onfood;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class ItemListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewItems;
    private FirebaseFirestore db;
    private List<Item> itemList = new ArrayList<>();
    private CartManager cartManager;
    private Button buttonAddToCart;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        buttonAddToCart = findViewById(R.id.buttonGoToCart);
        recyclerViewItems = findViewById(R.id.recyclerViewItems);
        progressBar = findViewById(R.id.progressBar);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        cartManager = CartManager.getInstance(this);

        loadItemsFromFirestore();
        buttonAddToCart.setOnClickListener(v -> gotoChart());
    }

    private void loadItemsFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);  // Show progress bar while fetching data
        db.collection("MenuItem").get().addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);  // Hide progress bar after task is done
            if (task.isSuccessful()) {
                itemList.clear();  // Clear any old data
                for (DocumentSnapshot doc : task.getResult()) {
                    Item item = doc.toObject(Item.class);
                    if (item != null) {
                        item.setId(doc.getId());
                        itemList.add(item);
                        Log.d("Firestore", "Item added: " + item.getName() + " with ID: " + item.getId());
                    }
                }

                // Set adapter after items are fetched
                ItemAdapter adapter = new ItemAdapter(itemList, cartManager, item -> {
//                    cartManager.addToCart(item);
                    Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show();
                });
                recyclerViewItems.setAdapter(adapter);
            } else {
                Log.e("Firestore", "Error getting items", task.getException());
                Toast.makeText(this, "Failed to load items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void gotoChart() {
        if (cartManager.getCartItemsWithQuantities().isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(ItemListActivity.this, CartActivity.class));
        }
    }
}
