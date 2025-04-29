package com.example.onfood.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.onfood.LoadingView;
import com.example.onfood.R;
import com.example.onfood.VersionChecker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private String name;
    private TextView textViewName;
    private FirebaseAuth mAuth;
    private LoadingView loadingView;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Request notification permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!isNotificationPermissionGranted()) {
                requestNotificationPermission();
            }
        }

        // Check for app updates
        VersionChecker versionChecker = new VersionChecker(this);
        versionChecker.checkForUpdates();

        // Set up UI elements
        button = findViewById(R.id.button);
        textViewName = findViewById(R.id.title);
        UserDataFrom();

        // Button click listener
        button.setOnClickListener(view -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                checkRestaurantStatus(); // Check Firestore before allowing access
            } else {
                Intent intent = new Intent(MainActivity.this, Authentication.class);
                startActivity(intent);
            }
        });
    }

    private boolean isNotificationPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestNotificationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                Toast.makeText(this, "Notification permission required to send notifications.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkRestaurantStatus() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("RestaurantStatus").document("status")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("WorkingStatus")) {
                        boolean isWorking = documentSnapshot.getBoolean("WorkingStatus");

                        if (isWorking) {
                            Toast.makeText(MainActivity.this, "Restaurant is open!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, ItemListActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Restaurant is closed!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Restaurant status unavailable!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this, "Error fetching status: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void UserDataFrom() {
        // Get SharedPreferences reference
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        name = sharedPreferences.getString("name", "On Food");
        textViewName.setText(name);
    }
}
