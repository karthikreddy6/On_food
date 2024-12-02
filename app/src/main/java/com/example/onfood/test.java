//package com.example.onfood.Activity;
//
//import android.Manifest;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.constraintlayout.widget.ConstraintLayout;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.example.onfood.LoadingView;
//import com.example.onfood.R;
//import com.example.onfood.VersionChecker;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//public class MainActivitykjn extends AppCompatActivity {
//    private Button button, logout;
//    private FirebaseAuth mAuth;
//    private LoadingView loadingView;
//
//    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Initialize root layout and loading view
//        ConstraintLayout rootLayout = findViewById(R.id.main);
//        loadingView = new LoadingView(this);
//        loadingView.setVisibility(View.GONE); // Initially hidden
//        rootLayout.addView(loadingView);
//
//        button = findViewById(R.id.button);
//        logout = findViewById(R.id.button2);
//
//        // Delay the display of the loading view
//        rootLayout.postDelayed(() -> {
//            loadingView.setVisibility(View.VISIBLE); // Show the loading view
//            loadingView.applyZoomInEffect(); // Add custom animation (optional)
//            rootLayout.requestLayout(); // Refresh layout
//            rootLayout.invalidate(); // Redraw layout
//        }, 3000); // Delay for 3 seconds
//
//        // Hide loading view after additional delay
//        rootLayout.postDelayed(() -> {
//            loadingView.setVisibility(View.GONE); // Hide the loading view
//            rootLayout.setVisibility(View.VISIBLE); // Ensure the main layout is visible
//            rootLayout.requestLayout(); // Refresh layout
//            rootLayout.invalidate(); // Redraw layout
//        }, 6000); // Total delay of 6 seconds
//
//        // Request notification permission if needed
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (!isNotificationPermissionGranted()) {
//                requestNotificationPermission();
//            }
//        }
//
//        // Initialize Firebase Auth
//        mAuth = FirebaseAuth.getInstance();
//
//        // Check for updates on app start
//        VersionChecker versionChecker = new VersionChecker();
//        versionChecker.checkForUpdates(this);
//
//        // Button click handlers
//        button.setOnClickListener(view -> {
//            FirebaseUser currentUser = mAuth.getCurrentUser();
//            if (currentUser != null) {
//                Toast.makeText(MainActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(MainActivity.this, ItemListActivity.class);
//                startActivity(intent);
//            } else {
//                Intent intent = new Intent(MainActivity.this, Authentication.class);
//                startActivity(intent);
//            }
//        });
//
//        logout.setOnClickListener(v -> logoutUser());
//    }
//
//    private boolean isNotificationPermissionGranted() {
//        return ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.POST_NOTIFICATIONS
//        ) == PackageManager.PERMISSION_GRANTED;
//    }
//
//    private void requestNotificationPermission() {
//        ActivityCompat.requestPermissions(
//                this,
//                new String[]{Manifest.permission.POST_NOTIFICATIONS},
//                NOTIFICATION_PERMISSION_REQUEST_CODE
//        );
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted, proceed with sending notifications
//            } else {
//                Toast.makeText(this, "Notification permission required to send notifications.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    public void logoutUser() {
//        FirebaseAuth.getInstance().signOut();
//        Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
//    }
//}
