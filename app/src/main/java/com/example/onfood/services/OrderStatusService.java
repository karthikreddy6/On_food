package com.example.onfood.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.onfood.Activity.MainActivity;
import com.example.onfood.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OrderStatusService extends Service {
    private static final String TAG = "OrderStatusService";
    private DatabaseReference ordersRef;
    private String latestOrderId;  // To keep track of the latest order

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate() called");

        // Set up Firebase Database reference to listen for order status updates
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        // Retrieve the latest order ID from SharedPreferences
        latestOrderId = getSharedPreferences("UserData", MODE_PRIVATE).getString("latestOrderId", null);
        Log.d(TAG, "Latest Order ID retrieved from SharedPreferences: " + latestOrderId);

        // Start listening for order updates
        if (latestOrderId != null) {
            Log.d(TAG, "Starting to listen for updates for order ID: " + latestOrderId);
            listenForOrderUpdates(latestOrderId);
        } else {
            Log.e(TAG, "No latest order ID found.");
            stopSelf(); // Stop service if no order ID is available
        }
    }

    private void listenForOrderUpdates(String orderId) {
        Log.d(TAG, "Listening for updates on order ID: " + orderId);

        ordersRef.child(orderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "DataSnapshot received: " + dataSnapshot.toString());

                String orderStatus = dataSnapshot.child("status").getValue(String.class);
                Log.d(TAG, "Order status from Firebase: " + orderStatus);

                if (orderStatus != null) {
                    switch (orderStatus) {
                        case "cooking":
                            Log.d(TAG, "Order is cooking.");
                            sendNotification(orderId, "Your order is cooking!");
                            break;
                        case "ready":
                            Log.d(TAG, "Order is ready.");
                            sendNotification(orderId, "Your order is ready to pick up!");
                            break;
                        case "delivered":
                            Log.d(TAG, "Order has been delivered.");
                            sendNotification(orderId, "Your order has been delivered!");
                            stopSelf(); // Stop the service when the order is delivered
                            break;
                        default:
                            Log.d(TAG, "Unknown order status: " + orderStatus);
                            break;
                    }
                } else {
                    Log.e(TAG, "Order status is null.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to read order status: " + databaseError.getMessage());
            }
        });
    }

    private void sendNotification(String orderId, String message) {
        Log.d(TAG, "Sending notification for order ID: " + orderId + ", message: " + message);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String channelId = "order_status_channel";

        // Create notification channel (for Android O and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "Creating notification channel.");
            NotificationChannel channel = new NotificationChannel(channelId, "Order Status Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Create an intent that will be fired when the notification is clicked
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("ORDER_ID", orderId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_cart) // Replace with a valid drawable icon
                .setContentTitle("Order Update")
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Show the notification
        notificationManager.notify(orderId.hashCode(), builder.build());
        Log.d(TAG, "Notification sent for order ID: " + orderId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("ORDER_ID")) {
            latestOrderId = intent.getStringExtra("ORDER_ID");
            Log.d(TAG, "Received ORDER_ID from intent: " + latestOrderId);

            // Optionally save it to SharedPreferences for persistence
            getSharedPreferences("UserData", MODE_PRIVATE)
                    .edit().putString("latestOrderId", latestOrderId).apply();
            showForegroundNotification();

            listenForOrderUpdates(latestOrderId);
        } else {
            Log.e(TAG, "No ORDER_ID passed in intent, stopping service.");
            stopSelf(); // No order ID passed
        }

        return START_STICKY;
    }

    private void showForegroundNotification() {
        Log.d(TAG, "Showing foreground notification.");

        String channelId = "order_status_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "Creating notification channel for foreground service.");
            NotificationChannel channel = new NotificationChannel(channelId, "Order Status", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Tracking Order")
                .setContentText("We're monitoring your order status...")
                .setSmallIcon(R.drawable.ic_cart) // Your app's icon
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        // This is mandatory to keep the service running
        startForeground(1, notification);
        Log.d(TAG, "Foreground notification shown.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service onDestroy() called");
        // Unregister listeners if necessary (e.g., Firebase listeners)
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() called, returning null as we are not binding this service.");
        return null; // We are not binding this service
    }
}
