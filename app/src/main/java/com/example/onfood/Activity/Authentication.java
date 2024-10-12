package com.example.onfood.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onfood.R;
import com.example.onfood.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Authentication extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText, numberEditText, nameEditText;
    private Button loginButton, registerButton, switchToRegisterButton, switchToLoginButton;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize Views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        numberEditText = findViewById(R.id.numberedittext);
        nameEditText = findViewById(R.id.nameedittext);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        switchToRegisterButton = findViewById(R.id.swichtoregister);
        switchToLoginButton = findViewById(R.id.swichtologinButton);

        // Set onClick listeners
        loginButton.setOnClickListener(v -> loginUser());
        registerButton.setOnClickListener(v -> registerUser());
        switchToRegisterButton.setOnClickListener(v -> switchToRegister());
        switchToLoginButton.setOnClickListener(v -> switchToLogin());

        // Initially show login form
        showLoginForm();
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
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

    private void switchToRegister() {
        showRegisterForm();
    }

    private void switchToLogin() {
        showLoginForm();
    }

    private void showLoginForm() {
        emailEditText.setVisibility(View.VISIBLE);
        passwordEditText.setVisibility(View.VISIBLE);
        numberEditText.setVisibility(View.GONE);
        nameEditText.setVisibility(View.GONE);
        loginButton.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.GONE);
        switchToRegisterButton.setVisibility(View.VISIBLE);
        switchToLoginButton.setVisibility(View.GONE);
    }
    private void showRegisterForm() {
        emailEditText.setVisibility(View.VISIBLE);
        passwordEditText.setVisibility(View.VISIBLE);
        numberEditText.setVisibility(View.VISIBLE);
        nameEditText.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.GONE);
        registerButton.setVisibility(View.VISIBLE);
        switchToRegisterButton.setVisibility(View.GONE);
        switchToLoginButton.setVisibility(View.VISIBLE); // Missing parenthesis
    }
}