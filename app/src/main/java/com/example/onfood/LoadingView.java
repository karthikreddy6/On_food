package com.example.onfood;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LoadingView extends RelativeLayout {

    private TextView tvFoodIcon, tvLoadingText;
    private ObjectAnimator bounceAnimation;
    private Handler handler;

    // Food emojis array
    private final String[] foodEmojis = {
            "😋", "🍔", "🍜", "🥗", "🍱", "🌮", "🍣", "🍪"
    };

    // Loading messages
    private final String[] loadingMessages = {
            "Finding best recipes...",
            "Preparing your feed...",
            "Almost ready!"
    };

    private int currentFoodIndex = 0;
    private int currentMessageIndex = 0;

    public LoadingView(Context context) {
        super(context);
        init(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // Inflate the custom layout for loading screen
        LayoutInflater.from(context).inflate(R.layout.activity_loading_view, this, true);

        // Find views in the layout
        tvFoodIcon = findViewById(R.id.tvFoodIcon);
        tvLoadingText = findViewById(R.id.tvLoadingText);

        // Initialize handler
        handler = new Handler(Looper.getMainLooper());

        // Setup and start animations
        setupBounceAnimation();
        startAnimations();
    }

    private void setupBounceAnimation() {
        // Create bounce animation for the emoji
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Clean up animations and handlers
        if (bounceAnimation != null) {
            bounceAnimation.cancel();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
