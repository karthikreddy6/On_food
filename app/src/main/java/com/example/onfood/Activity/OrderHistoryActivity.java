package com.example.onfood.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import com.example.onfood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {
    private ListView listViewOrders;
    private DatabaseReference ordersRef;
    private FirebaseAuth mAuth;
    private List<String> orderIds; // To store order IDs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // Include navigation bar
        View navigationBar = getLayoutInflater().inflate(R.layout.navigation_bar, null);
        ((ViewGroup) findViewById(R.id.navigationContainer)).addView(navigationBar);

        ImageButton buttonBack = findViewById(R.id.buttonBack);
        TextView navText = findViewById(R.id.navtext);
        buttonBack.setOnClickListener(v -> onBackPressed());
        buttonBack.setVisibility(View.VISIBLE);
        navText.setText("HISTORY");
        listViewOrders = findViewById(R.id.listViewOrders);
        mAuth = FirebaseAuth.getInstance();
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        orderIds = new ArrayList<>();

        fetchOrderHistory();
    }

    private void fetchOrderHistory() {
        String userId = mAuth.getCurrentUser().getUid(); // Get the current user ID

        ordersRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> orderList = new ArrayList<>();
                orderIds.clear(); // Clear previous order IDs
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    String orderId = orderSnapshot.getKey();
                    double amount = orderSnapshot.child("amount").getValue(Double.class);
                    String date = orderSnapshot.child("orderDate").getValue(String.class);
                    String time = orderSnapshot.child("orderTime").getValue(String.class);

                    // Build order details string
                    String orderDetails = "Order ID: " + orderId + "\nTotal Amount: $" + amount +
                            "\nDate: " + date + "\nTime: " + time;
                    orderList.add(orderDetails);
                    orderIds.add(orderId); // Store the order ID
                }

                if (orderList.isEmpty()) {
                    Toast.makeText(OrderHistoryActivity.this, "No previous orders found.", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(OrderHistoryActivity.this,
                            android.R.layout.simple_list_item_1, orderList);
                    listViewOrders.setAdapter(adapter);

                    // Set item click listener to navigate to OrderConfirmationActivity
                    listViewOrders.setOnItemClickListener((parent, view, position, id) -> {
                        String selectedOrderId = orderIds.get(position); // Get the order ID
                        Intent intent = new Intent(OrderHistoryActivity.this, OrderConfirmationActivity.class);
                        intent.putExtra("ORDER_ID", selectedOrderId); // Pass the order ID
                        startActivity(intent);
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(OrderHistoryActivity.this, "Error fetching order history.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}