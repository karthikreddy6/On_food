package com.example.onfood.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onfood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileActivity extends AppCompatActivity {
    private TextView textViewName;
    private TextView textViewPhone;
    private TextView textView_totalAmount,textView_orderCount;
    private String name,phone,userId ;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textView_totalAmount = findViewById(R.id.Total_amount_TextView);
        textView_orderCount = findViewById(R.id.ordersCountTextView);
        textViewName = findViewById(R.id.textViewName);
        textViewPhone = findViewById(R.id.textViewPhone);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // In OrderConfirmationActivity.java
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        ImageButton buttonCart = findViewById(R.id.buttonCart);
        ImageButton buttonLogOut =findViewById(R.id.Logout);
        TextView navText =findViewById(R.id.navtext);

        navText.setText("Profile");
        buttonLogOut.setOnClickListener(v-> logoutUser());
        buttonBack.setOnClickListener(v -> onBackPressed());

// Hide profile button in this activity
        buttonBack.setVisibility(View.VISIBLE);
        buttonLogOut.setVisibility(View.VISIBLE);
        UserDataFrom();
    }
    public void calculateUserOrderSummary(String userId ) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int orderCount = 0;
                double totalAmount = 0.0;

                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    // Check if the order belongs to the given userId
                    String orderUserId = orderSnapshot.child("userId").getValue(String.class);
                    if (userId.equals(orderUserId)) {
                        // Retrieve the order amount
                        Double amount = orderSnapshot.child("amount").getValue(Double.class);
                        if (amount != null) {
                            totalAmount += amount;
                        }
                        orderCount++;
                    }
                }

                // Update TextViews with the calculated values
                textView_totalAmount.setText(" ₹" + totalAmount);
                textView_orderCount.setText("" + orderCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserOrderSummary", "Failed to retrieve data: " + error.getMessage());
            }
        });
    }



    public void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent =new Intent(ProfileActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        deleteUserDataLocally();
        startActivity(intent);
    }
    private void deleteUserDataLocally() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
    private void UserDataFrom() {
        // Get SharedPreferences reference
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
      name = sharedPreferences.getString("name", "Karthik_test");
        userId = sharedPreferences.getString("userId", "Karthik_test");

        phone = sharedPreferences.getString("phone", "546");
         textViewName.setText(name);
         textViewPhone.setText(phone);
         calculateUserOrderSummary(userId);



    }

}