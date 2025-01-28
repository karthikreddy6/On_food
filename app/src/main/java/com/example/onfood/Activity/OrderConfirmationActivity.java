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
    private TextView orderIdTextView;
    private TextView totalPriceTextView;
    private TextView orderDetailsTextView;
    private TextView orderTimeTextView;
    private ImageView statusImageView;
    private TextView statusTextView;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        ImageButton buttonBack = findViewById(R.id.buttonBack);
        ImageButton buttonCart = findViewById(R.id.buttonCart);
        TextView navText = findViewById(R.id.navtext);
        navText.setText("ORDER STATUS");

        buttonCart.setOnClickListener(v -> {
            String source = getIntent().getStringExtra("source");

            if ("cart".equals(source)) {
                startActivity(new Intent(OrderConfirmationActivity.this, OrderHistoryActivity.class));
            } else {
                Intent intent = new Intent(OrderConfirmationActivity.this, OrderHistoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }

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
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        String orderId = getIntent().getStringExtra("ORDER_ID");

        fetchOrderDetails(orderId);
    }

    private void fetchOrderDetails(String orderId) {
        ordersRef.child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String amount = dataSnapshot.child("amount").getValue(Double.class).toString();
                    String time = dataSnapshot.child("orderTime").getValue(String.class).toString();
                    String date = dataSnapshot.child("orderDate").getValue(String.class).toString();
                    String orderStatus = dataSnapshot.child("status").getValue(String.class);

                    orderIdTextView.setText(orderId);
                    totalPriceTextView.setText("Total Amount: " + amount);
                    orderTimeTextView.setText(date + " (" + time + ")");
                    updateOrderStatus(orderStatus);

                    StringBuilder orderDetails = new StringBuilder();
                    for (DataSnapshot itemSnapshot : dataSnapshot.child("items").getChildren()) {
                        String name = itemSnapshot.child("name").getValue(String.class);
                        int quantity = itemSnapshot.child("quantity").getValue(Integer.class);
                        orderDetails.append(name).append(" x ").append(quantity).append("\n");
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
        });
    }

    private void updateOrderStatus(String status) {
        switch (status) {
            case "confirmed":
                statusImageView.setImageResource(R.drawable.tick); // Replace with confirmed logo
                statusTextView.setText("Confirmed");
                break;
            case "cooking":
                statusImageView.setImageResource(R.drawable.ic_cookin); // Replace with cooking logo
                statusTextView.setText("Cooking");
                break;
            case "ready":
                statusImageView.setImageResource(R.drawable.ic_food_ready); // Replace with ready logo
                statusTextView.setText("Ready");
                break;
            case "delivered":
                statusImageView.setImageResource(R.drawable.ic_packed); // Replace with delivered logo
                statusTextView.setText("Delivered");
                break;
            default:
                statusImageView.setImageResource(R.drawable.donut); // Default status logo
                statusTextView.setText("Status Unknown");
                break;
        }
    }
}