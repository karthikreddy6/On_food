package com.example.onfood.Activity;

import static com.example.onfood.R.id.viewFlipper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.onfood.LoadingView;
import com.example.onfood.R;
import com.example.onfood.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Authentication extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private TextView forgotPasswordTextView;
    private EditText emailEditText, passwordEditText, numberEditText, nameEditText;
    private EditText emailRegisterEditText, passwordRegisterEditText;
    private Button loginButton, registerButton, switchToRegisterButton, switchToLoginButton;
    private ViewFlipper viewFlipper;
    private RelativeLayout Singuptoogle,Logintoogle;
    private ProgressBar progressBar,progressBar1;


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
        forgotPasswordTextView = findViewById(R.id.forgotPassword);
        // Set onClick listeners
        loginButton.setOnClickListener(v -> loginUser());
        registerButton.setOnClickListener(v -> registerUser());
        forgotPasswordTextView.setOnClickListener(v -> sendPasswordResetEmail(String.valueOf(emailEditText.getText()), this));

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
        setloadindscreen();
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
                                        saveUserDataLocally(userId,name,email,phone);
                                        finish(); // Finish the activity
                                    } else {
                                        Toast.makeText(Authentication.this, "Failed to save user info", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthUserCollisionException e) {
                            // Email is already registered
                            showSnackbar("Email is already registered.");
                        } catch (FirebaseAuthWeakPasswordException e) {
                            // Password is too weak
                            showSnackbar("Password is too weak. It should be at least 6 characters.");
                        } catch (FirebaseAuthEmailException e) {
                            // Invalid email format
                            showSnackbar("Invalid email format.");
                        } catch (FirebaseAuthException e) {
                            // General signup failure
                        showSnackbar("Signup failed. Please try again.");
                        } catch (Exception e) {
                            // Other unexpected errors
                        showSnackbar("Signup failed. Please try again.");
                        }
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
        setloadindscreen();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String userId = firebaseUser.getUid();  // Get the userId

                        // Fetch user data from Firestore after login
                        fetchUserDataFromFirestore(userId);
                        finish(); // Finish the activity
                    } else {
                        // Log the full exception to understand which error occurred
                        Exception exception = task.getException();
                        Toast.makeText(Authentication.this, "Login failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        try {
                            throw exception;
                        } catch (FirebaseAuthInvalidUserException e) {
                            // No user found with the email
                            showSnackbar("No user found with this email.");
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            // Incorrect password
                            showSnackbar("Incorrect password. Please try again.");
                        } catch (FirebaseAuthException e) {
                            // General Firebase authentication error
                            showSnackbar("Authentication failed. Please try again.");
                        } catch (Exception e) {
                            // Other unexpected errors
                            showSnackbar("Login failed. Please check your credentials.");
                        }
                    }
                });
    }
    private void showSnackbar(String message) {
        rmLoadingView();
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    private void rmLoadingView(){
        progressBar.setVisibility(View.GONE);
        progressBar1.setVisibility(View.GONE);
        loginButton.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.VISIBLE);
    }

    private  void setloadindscreen(){
        progressBar=findViewById(R.id.progersbar2);
        progressBar.setVisibility(View.VISIBLE);
        progressBar1=findViewById(R.id.progersbar3);
        progressBar1.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.GONE);
        registerButton.setVisibility(View.GONE);


    }
    private void saveUserDataLocally(String userId, String name,String email, String phone) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userId);
        editor.putString("email", email);
        editor.putString("name", name);
        editor.putString("phone", phone);
        editor.apply(); // Commit the changes
       Toast.makeText(this, name+ " login successfully", Toast.LENGTH_SHORT).show();

    }
    private void fetchUserDataFromFirestore(String userId) {
        firestore.collection("Users")
                .document(userId)  // Use the userId to directly fetch the user document
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve user data from the Firestore document
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        String phone = documentSnapshot.getString("phone");

                        // Save the user data locally
                        saveUserDataLocally(userId, name, email, phone);

                        // Proceed to the next activity after saving user data locally
                        startActivity(new Intent(Authentication.this, ItemListActivity.class));
                        finish();  // Finish the current activity
                    } else {
                        showSnackbar("User data not found.");
                    }
                })
                .addOnFailureListener(e -> {
                    showSnackbar("Failed to fetch user data. Please try again.");
                });
    }
    public void sendPasswordResetEmail(String email, Context context) {
        setloadindscreen();
        if (email.isEmpty()) {
            Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Reset email sent. Check your inbox.", Toast.LENGTH_LONG).show();
                        rmLoadingView();
                    } else {
                        Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        rmLoadingView();
                    }
                });

    }


}