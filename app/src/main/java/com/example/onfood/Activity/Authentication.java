package com.example.onfood.Activity;

import android.content.Intent;
import android.os.Bundle;
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
    private EditText emailEditText, passwordEditText,numberEdittext,nameEdittext;
    private Button loginButton, registerButton;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();  // Initialize Firestore

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        numberEdittext= findViewById(R.id.numberedittext);
        nameEdittext =findViewById(R.id.nameedittext);
        loginButton.setOnClickListener(v -> loginUser());
        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String name = nameEdittext.getText().toString(); // Get the name
        String phone = numberEdittext.getText().toString(); // Get the phone number from the correct EditText

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
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(Authentication.this, ItemListActivity.class));
                    } else {
                        Toast.makeText(Authentication.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
