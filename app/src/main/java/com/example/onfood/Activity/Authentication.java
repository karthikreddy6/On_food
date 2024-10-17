package com.example.onfood.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onfood.R;
import com.example.onfood.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Authentication extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private EditText emailEditText, passwordEditText, numberEditText, nameEditText;
    private EditText emailRegisterEditText, passwordRegisterEditText;
    private Button loginButton, registerButton, switchToRegisterButton, switchToLoginButton;
    private ViewFlipper viewFlipper;
    private RelativeLayout Singuptoogle,Logintoogle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize Views
        viewFlipper = findViewById(R.id.viewFlipper);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailRegisterEditText = findViewById(R.id.emailRegisterEditText);
        passwordRegisterEditText = findViewById(R.id.passwordRegisterEditText);
        numberEditText = findViewById(R.id.numberEditText);
        nameEditText = findViewById(R.id.nameEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        switchToRegisterButton = findViewById(R.id.switchToRegisterButton);
        switchToLoginButton = findViewById(R.id.switchToLoginButton);
        Singuptoogle = findViewById(R.id.Singuptoogle);
        Logintoogle = findViewById(R.id.logintoogle);

        // Set onClick listeners
        loginButton.setOnClickListener(v -> loginUser());
        registerButton.setOnClickListener(v -> registerUser());

        switchToRegisterButton.setOnClickListener(v -> switchToRegister());
        switchToLoginButton.setOnClickListener(v -> switchToLogin());

        // Gesture detection for swipe
        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getX() - e2.getX() > 50) { // Swipe left
                    return switchToRegister();
                } else if (e2.getX() - e1.getX() > 50) { // Swipe right
                    return switchToLogin();
                }
                return false;
            }
        });

        viewFlipper.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    private boolean switchToRegister() {
        if (viewFlipper.getDisplayedChild() == 0) { // Login is currently displayed
            viewFlipper.setInAnimation(this, R.anim.slide_in_right);
            viewFlipper.setOutAnimation(this, R.anim.slide_out_left);
            viewFlipper.showNext(); // Switch to Register layout
            Singuptoogle.setBackgroundResource(R.drawable.gradient_background_on);
            Logintoogle.setBackgroundResource(R.drawable.round_curve);
            return true;
        }
        return false;
    }

    private boolean switchToLogin() {
        if (viewFlipper.getDisplayedChild() == 1) { // Register is currently displayed
            viewFlipper.setInAnimation(this, R.anim.slide_in_left);
            viewFlipper.setOutAnimation(this, R.anim.slide_out_right);
            viewFlipper.showPrevious(); // Switch to Login layout
            Logintoogle.setBackgroundResource(R.drawable.gradient_background_on);
            Singuptoogle.setBackgroundResource(R.drawable.round_curve);
            return true;
        }
        return false;
    }

    private void registerUser() {
        String email = emailRegisterEditText.getText().toString().trim();
        String password = passwordRegisterEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String phone = numberEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(Authentication.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String userId = firebaseUser.getUid();

                        // Create user object
                        User user = new User(userId, email, name, phone);

                        // Save user info in Firestore under "Users" collection
                        firestore.collection("Users").document(userId)
                                .set(user)
                                .addOnCompleteListener(userTask -> {
                                    if (userTask.isSuccessful()) {
                                        startActivity(new Intent(Authentication.this, ItemListActivity.class));
                                        finish(); // Finish the activity
                                    } else {
                                        Toast.makeText(Authentication.this, "Failed to save user info", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(Authentication.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(Authentication.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(Authentication.this, ItemListActivity.class));
                        finish(); // Finish the activity
                    } else {
                        Toast.makeText(Authentication.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}