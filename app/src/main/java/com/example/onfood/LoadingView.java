package com.example.onfood;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.ImageView;

public class LoadingView extends RelativeLayout {

    private ImageView foodItem1, foodItem2, foodItem3;
    private float rotationAngle = 360f; // Complete rotation in degrees

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

        // Find food items in the layout
        foodItem1 = findViewById(R.id.food_item1);


        // Start the rotating animation
        startRotationAnimation();
    }

    private void startRotationAnimation() {
        // Create ObjectAnimator for each food item to rotate
        ObjectAnimator rotateFoodItem1 = ObjectAnimator.ofFloat(foodItem1, "rotation", 0f, rotationAngle);
        ObjectAnimator rotateFoodItem2 = ObjectAnimator.ofFloat(foodItem2, "rotation", 0f, rotationAngle);
        ObjectAnimator rotateFoodItem3 = ObjectAnimator.ofFloat(foodItem3, "rotation", 0f, rotationAngle);

        // Set the animation to repeat infinitely
        rotateFoodItem1.setRepeatCount(ObjectAnimator.INFINITE);
        rotateFoodItem2.setRepeatCount(ObjectAnimator.INFINITE);
        rotateFoodItem3.setRepeatCount(ObjectAnimator.INFINITE);

        // Set the duration of the animation
        rotateFoodItem1.setDuration(2000); // 2 seconds for each rotation
        rotateFoodItem2.setDuration(2000);
        rotateFoodItem3.setDuration(2000);

        // Set the interpolator to make the animation smooth
        rotateFoodItem1.setInterpolator(new android.view.animation.LinearInterpolator());
        rotateFoodItem2.setInterpolator(new android.view.animation.LinearInterpolator());
        rotateFoodItem3.setInterpolator(new android.view.animation.LinearInterpolator());

        // Start the rotation animations
        rotateFoodItem1.start();
        rotateFoodItem2.start();
        rotateFoodItem3.start();
    }

    // Zoom-in effect for the loading view
    public void applyZoomInEffect() {
        // Create ObjectAnimator for scaling the loading view
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 0f, 1f);

        // Set the duration for zoom-in effect (500ms)
        scaleX.setDuration(500);
        scaleY.setDuration(500);

        // Use an AccelerateInterpolator for smooth zoom-in effect
        scaleX.setInterpolator(new android.view.animation.AccelerateInterpolator());
        scaleY.setInterpolator(new android.view.animation.AccelerateInterpolator());

        // Start the zoom-in animations
        scaleX.start();
        scaleY.start();

        // Optionally, show the view after animation completes (if initially hidden)
        scaleX.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                // The loading view will have finished zooming in
                setVisibility(VISIBLE); // Ensure the view is visible after animation
            }
        });
    }
}
