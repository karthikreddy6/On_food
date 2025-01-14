package com.example.onfood;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onfood.Activity.MainActivity;

public class test extends AppCompatActivity {
    private TextView tvFoodIcon;
    private TextView tvLoadingText;
    private Handler handler;
    private ObjectAnimator bounceAnimation;

    // Food emojis array
    private final String[] foodEmojis = {
            "🍕", "🍔", "🍜", "🥗", "🍱", "🌮", "🍣", "🍪"
    };

    // Loading messages
    private final String[] loadingMessages = {
            "Finding best recipes...",
            "Preparing your feed...",
            "Almost ready!"
    };

    private int currentFoodIndex = 0;
    private int currentMessageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Initialize views
        tvFoodIcon = findViewById(R.id.tvFoodIcon);
        tvLoadingText = findViewById(R.id.tvLoadingText);
        handler = new Handler(Looper.getMainLooper());

        // Setup animations
        setupBounceAnimation();
        startAnimations();

        // Simulate loading time (3 seconds)
        handler.postDelayed(this::moveToMainActivity, 3000);
    }

    private void setupBounceAnimation() {
        // Create bounce animation
        bounceAnimation = ObjectAnimator.ofFloat(tvFoodIcon, "translationY", 0f, -30f, 0f);
        bounceAnimation.setDuration(1000);
        bounceAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        bounceAnimation.setRepeatCount(ObjectAnimator.INFINITE);
    }

    private void startAnimations() {
        // Start bounce animation
        bounceAnimation.start();

        // Update food emoji every 500ms
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentFoodIndex = (currentFoodIndex + 1) % foodEmojis.length;
                tvFoodIcon.setText(foodEmojis[currentFoodIndex]);
                handler.postDelayed(this, 500);
            }
        }, 500);

        // Update loading message every 800ms
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentMessageIndex = (currentMessageIndex + 1) % loadingMessages.length;
                tvLoadingText.setText(loadingMessages[currentMessageIndex]);
                handler.postDelayed(this, 800);
            }
        }, 800);
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(test.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up animations and handlers
        if (bounceAnimation != null) {
            bounceAnimation.cancel();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}