package com.example.onfood.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EdgeEffect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onfood.CartManager;
import com.example.onfood.Item;
import com.example.onfood.ItemAdapter;
import com.example.onfood.HorizontalItemAdapter;
import com.example.onfood.LoadingView;
import com.example.onfood.R;
import com.example.onfood.CategoryAdapter;
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
    private List<Item> horizontalItemList = new ArrayList<>();
    private CartManager cartManager;
    private BadgeDrawable badgeDrawable;
    private TextView CategoriesTitel;
    private NestedScrollView nestedScrollView;
    private LoadingView loadingView;
    private   LinearLayout rootLayout;

    private View bottemnav;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        setloadindscreen();
        initializeViews();
        setupRecyclerViews();
        setupScrollBehavior();
        setupBottomNavigation();
        setupCategoryRecyclerView();
        loadItemsFromFirestore();
    }


    private void initializeViews() {
        nestedScrollView = findViewById(R.id.nestedScrollView);
        CategoriesTitel = findViewById(R.id.CategoriesTitel);
        ImageButton buttonCart = findViewById(R.id.buttonCart);
        ImageButton buttonProfile = findViewById(R.id.buttonProfile);
        TextView navText = findViewById(R.id.navtext);
        navText.setText("MENU");

        buttonCart.setVisibility(View.VISIBLE);
        buttonProfile.setVisibility(View.VISIBLE);

        buttonCart.setOnClickListener(v -> startActivity(new Intent(ItemListActivity.this, OrderHistoryActivity.class)));
        buttonProfile.setOnClickListener(v -> startActivity(new Intent(ItemListActivity.this, ProfileActivity.class)));
    }

    private void setupRecyclerViews() {
        recyclerViewItems = findViewById(R.id.recyclerViewItems);
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        recyclerViewHorizontalItems = findViewById(R.id.recyclerViewHorizontalItems);

        // Setup horizontal items RecyclerView
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewHorizontalItems.setLayoutManager(horizontalLayoutManager);
        recyclerViewHorizontalItems.setHasFixedSize(true);

        // Setup categories RecyclerView
        LinearLayoutManager categoriesLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategories.setLayoutManager(categoriesLayoutManager);
        recyclerViewCategories.setHasFixedSize(true);

        // Setup main items RecyclerView
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerViewItems.setLayoutManager(verticalLayoutManager);
        recyclerViewItems.setHasFixedSize(true);

        cartManager = CartManager.getInstance(this);
    }

    private void setupScrollBehavior() {
        // Add smooth scroll behavior
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY,
                                       int oldScrollX, int oldScrollY) {
                // Add fade effect for the navigation bar when scrolling
                float ratio = Math.min(1, scrollY / 200f);
                findViewById(R.id.navigationBar).setAlpha(1 - ratio * 0.4f);
            }
        });

        // Add smooth scrolling for horizontal RecyclerViews
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewHorizontalItems);

        // Add fade edge effect for RecyclerViews
        recyclerViewHorizontalItems.setEdgeEffectFactory(new RecyclerView.EdgeEffectFactory() {
            @NonNull
            @Override
            protected EdgeEffect createEdgeEffect(@NonNull RecyclerView view, int direction) {
                EdgeEffect edgeEffect = new EdgeEffect(view.getContext());
                edgeEffect.setColor(ContextCompat.getColor(view.getContext(), R.color.primary_color));
                return edgeEffect;
            }
        });

        recyclerViewCategories.setEdgeEffectFactory(new RecyclerView.EdgeEffectFactory() {
            @NonNull
            @Override
            protected EdgeEffect createEdgeEffect(@NonNull RecyclerView view, int direction) {
                EdgeEffect edgeEffect = new EdgeEffect(view.getContext());
                edgeEffect.setColor(ContextCompat.getColor(view.getContext(), R.color.primary_color));
                return edgeEffect;
            }
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                return false;
            } else if (itemId == R.id.navigation_cart) {
                gotoChart();
                return false;
            }
            return false;
        });

        badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.navigation_cart);
        badgeDrawable.setVisible(false);
        badgeDrawable.setNumber(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    private void setupCategoryRecyclerView() {
        final List<String> categories = new ArrayList<>();
        CategoryAdapter categoryAdapter = new CategoryAdapter(categories, selectedCategory -> {
            filterItemsByCategory(selectedCategory);
        });

        recyclerViewCategories.setAdapter(categoryAdapter);

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
                itemList.clear();
                for (DocumentSnapshot doc : task.getResult()) {
                    Item item = doc.toObject(Item.class);
                    if (item != null) {
                        item.setId(doc.getId());
                        itemList.add(item);
                    }
                }
                ItemAdapter adapter = new ItemAdapter(itemList, cartManager, this);
                recyclerViewItems.setAdapter(adapter);
                rmLoadingView();

            } else {
                Toast.makeText(ItemListActivity.this, "Failed to load vertical items", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch horizontal items
        db.collection("SplMenuItem").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                horizontalItemList.clear();
                for (DocumentSnapshot doc : task.getResult()) {
                    Item item = doc.toObject(Item.class);
                    if (item != null) {
                        item.setId(doc.getId());
                        horizontalItemList.add(item);
                    }
                }
                HorizontalItemAdapter horizontalAdapter = new HorizontalItemAdapter(horizontalItemList, cartManager, this);
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
        updateCartBadge();
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
    private void rmLoadingView(){
        loadingView.setVisibility(View.GONE); // Hide the loading view
        rootLayout.setVisibility(View.VISIBLE); // Show the main content
        bottemnav.setVisibility(View.VISIBLE);
        rootLayout.requestLayout(); // Refresh layout
        rootLayout.invalidate(); // Redraw layout
    }

    private  void setloadindscreen(){
        bottemnav=findViewById(R.id.bottomNavigation);

        rootLayout = findViewById(R.id.item_list);
        loadingView = new LoadingView(this);
        loadingView.setVisibility(View.VISIBLE);
        bottemnav.setVisibility(View.GONE);
        rootLayout.addView(loadingView);

    }
}