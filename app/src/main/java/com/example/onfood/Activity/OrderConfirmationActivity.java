package com.example.onfood.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.onfood.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OrderConfirmationActivity extends AppCompatActivity {
    private TextView orderIdTextView, totalPriceTextView, orderDetailsTextView, orderTimeTextView, statusTextView;
    private ImageView statusImageView;
    private DatabaseReference ordersRef;
    private ValueEventListener orderListener;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        // Initialize UI components
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        ImageButton buttonCart = findViewById(R.id.buttonCart);
        TextView navText = findViewById(R.id.navtext);
        navText.setText("ORDER STATUS");

        buttonCart.setOnClickListener(v -> {
            String source = getIntent().getStringExtra("source");

            Intent intent = new Intent(OrderConfirmationActivity.this, OrderHistoryActivity.class);
            if (!"cart".equals(source)) {
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            }
            startActivity(intent);
            finish();
        });

        buttonBack.setOnClickListener(v -> onBackPressed());

        buttonBack.setVisibility(View.VISIBLE);
        buttonCart.setVisibility(View.VISIBLE);

        orderIdTextView = findViewById(R.id.orderId);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        orderDetailsTextView = findViewById(R.id.textView3);
        orderTimeTextView = findViewById(R.id.orderDateTextView);
        statusImageView = findViewById(R.id.statusImage);
        statusTextView = findViewById(R.id.statustittle);

        // Get Order ID from Intent
        orderId = getIntent().getStringExtra("ORDER_ID");

        if (orderId != null && !orderId.isEmpty()) {
            fetchOrderDetails(orderId);
        } else {
            Toast.makeText(this, "Invalid Order ID!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchOrderDetails(String orderId) {
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(orderId);

        orderListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String amount = String.valueOf(dataSnapshot.child("amount").getValue(Double.class));
                    String time = dataSnapshot.child("orderTime").getValue(String.class);
                    String date = dataSnapshot.child("orderDate").getValue(String.class);
                    String orderStatus = dataSnapshot.child("status").getValue(String.class);

                    orderIdTextView.setText(orderId);
                    totalPriceTextView.setText("Total Amount: " + amount);
                    orderTimeTextView.setText(date + " (" + time + ")");
                    updateOrderStatus(orderStatus);

                    // Display Order Items
                    StringBuilder orderDetails = new StringBuilder();
                    for (DataSnapshot itemSnapshot : dataSnapshot.child("items").getChildren()) {
                        String name = itemSnapshot.child("name").getValue(String.class);
                        Integer quantity = itemSnapshot.child("quantity").getValue(Integer.class);

                        if (name != null && quantity != null) {
                            orderDetails.append(name).append(" x ").append(quantity).append("\n");
                        }
                    }
                    orderDetailsTextView.setText(orderDetails.toString());
                } else {
                    Toast.makeText(OrderConfirmationActivity.this, "Order not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(OrderConfirmationActivity.this, "Error fetching order details", Toast.LENGTH_SHORT).show();
            }
        };

        ordersRef.addValueEventListener(orderListener);
    }

    private void updateOrderStatus(String status) {
        if (status == null) status = "unknown";

        switch (status) {
            case "confirmed":
                statusImageView.setImageResource(R.drawable.tick);
                statusTextView.setText("Confirmed");
                break;
            case "cooking":
                statusImageView.setImageResource(R.drawable.ic_cookin);
                statusTextView.setText("Cooking");
                break;
            case "ready":
                statusImageView.setImageResource(R.drawable.ic_food_ready);
                statusTextView.setText("Ready");
                break;
            case "delivered":
                statusImageView.setImageResource(R.drawable.ic_packed);
                statusTextView.setText("Delivered");
                break;
            default:
                statusImageView.setImageResource(R.drawable.donut);
                statusTextView.setText("Status Unknown");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ordersRef != null && orderListener != null) {
            ordersRef.removeEventListener(orderListener);
        }
    }
}
