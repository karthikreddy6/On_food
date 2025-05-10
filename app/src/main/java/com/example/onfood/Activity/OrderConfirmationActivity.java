package com.example.onfood.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

    private TextView orderStatusTv1, orderStatusTv2, orderStatusTv3, orderStatusTv4;
    private ImageView orderStatusImg1, orderStatusImg2, orderStatusImg3, orderStatusImg4;
    private LinearLayout orderStatusLayout1, orderStatusLayout2, orderStatusLayout3, orderStatusLayout4;
    private int gray400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        gray400 = ContextCompat.getColor(this, R.color.black);

        // Initialize UI components
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        ImageButton buttonCart = findViewById(R.id.buttonCart);
        ImageButton buttonhelp = findViewById(R.id.help);
        TextView navText = findViewById(R.id.navtext);
        navText.setText("ORDER STATUS");

        buttonCart.setOnClickListener(v -> {
            String source = getIntent().getStringExtra("");

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
        buttonhelp.setVisibility(View.VISIBLE);
        // Status UI components
        orderStatusImg1 = findViewById(R.id.confirmedStatusImageView1);
        orderStatusImg2 = findViewById(R.id.confirmedStatusImageView2);
        orderStatusImg3 = findViewById(R.id.confirmedStatusImageView3);
        orderStatusImg4 = findViewById(R.id.confirmedStatusImageView4);

        orderStatusLayout1 = findViewById(R.id.confirmedStatus1);
        orderStatusLayout2 = findViewById(R.id.confirmedStatus2);
        orderStatusLayout3 = findViewById(R.id.confirmedStatus3);
        orderStatusLayout4 = findViewById(R.id.confirmedStatus4);

//        orderStatusTv1 = findViewById(R.id.confirmedStatustextView1);
//        orderStatusTv2 = findViewById(R.id.confirmedStatustextView2);
//        orderStatusTv3 = findViewById(R.id.confirmedStatustextView3);
//        orderStatusTv4 = findViewById(R.id.confirmedStatustextView4);

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
                    totalPriceTextView.setText("Total Amount: " + (amount != null ? amount : "0"));
                    orderTimeTextView.setText((date != null ? date : "Unknown") + " (" + (time != null ? time : "Unknown") + ")");
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

        statusTextView.setText(status.substring(0, 1).toUpperCase() + status.substring(1));

        int[] layouts = {R.id.confirmedStatus1, R.id.confirmedStatus2, R.id.confirmedStatus3, R.id.confirmedStatus4};
        int[] images = {R.id.confirmedStatusImageView1, R.id.confirmedStatusImageView2, R.id.confirmedStatusImageView3, R.id.confirmedStatusImageView4};
//        int[] textViews = {R.id.confirmedStatustextView1, R.id.confirmedStatustextView2, R.id.confirmedStatustextView3, R.id.confirmedStatustextView4};
        int[] backgroundDrawables = {
                R.drawable.order_status_1, R.drawable.order_status_2,
                R.drawable.order_status_3, R.drawable.order_status_4
        };
        int[] statusIcons = {R.drawable.tick, R.drawable.ic_cookin, R.drawable.ic_food_ready, R.drawable.ic_packed};

        String[] statuses = {"confirmed", "cooking", "ready", "delivered"};
        int statusIndex = -1;

        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equals(status)) {
                statusIndex = i;
                break;
            }
        }

        if (statusIndex != -1) {
            statusImageView.setImageResource(statusIcons[statusIndex]);
            for (int i = 0; i <= statusIndex; i++) {
                findViewById(layouts[i]).setBackground(ContextCompat.getDrawable(this, backgroundDrawables[i]));
                ((ImageView) findViewById(images[i])).setColorFilter(gray400, PorterDuff.Mode.SRC_ATOP);
//                ((TextView) findViewById(textViews[i])).setTextColor(Color.WHITE);
            }
        } else {
            statusImageView.setImageResource(R.drawable.donut);
            statusTextView.setText("Status Unknown");
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
