package com.example.onfood.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onfood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileActivity extends AppCompatActivity {
    private TextView textViewName;
    private TextView textViewPhone;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textViewName = findViewById(R.id.textViewName);
        textViewPhone = findViewById(R.id.textViewPhone);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        View navigationBar = getLayoutInflater().inflate(R.layout.navigation_bar, null);
        ((ViewGroup) findViewById(R.id.navigationContainer)).addView(navigationBar);

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
        fetchUserProfile();
    }

    private void fetchUserProfile() {
        String userId = mAuth.getCurrentUser().getUid(); // Get the current user ID

        firestore.collection("Users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = document.getString("name");
                            String phone = document.getString("phone");

                            textViewName.setText("Name: " + name);
                            textViewPhone.setText("Phone: " + phone);
                        } else {
                            Toast.makeText(ProfileActivity.this, "No profile found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, "Error fetching profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent =new Intent(ProfileActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}