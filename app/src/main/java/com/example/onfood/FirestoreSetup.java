package com.example.onfood;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.HashMap;
import java.util.Map;

public class FirestoreSetup {
    private FirebaseFirestore db;

    public FirestoreSetup() {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
    }

    // Method to store version info
    public void storeVersionInfo(String latestVersion, String updateUrl) {
        Map<String, Object> versionInfo = new HashMap<>();
        versionInfo.put("latestVersion", latestVersion);
        versionInfo.put("updateUrl", updateUrl);

        // Store the version info in Firestore
        db.collection("appInfo").document("versionInfo")
                .set(versionInfo)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Version info successfully written!");
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error writing version info: " + e.getMessage());
                });
    }

    // Method to fetch version info
    public void fetchVersionInfo(OnVersionInfoFetchedListener listener) {
        db.collection("appInfo").document("versionInfo")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String latestVersion = documentSnapshot.getString("latestVersion");
                        String updateUrl = documentSnapshot.getString("updateUrl");
                        listener.onSuccess(latestVersion, updateUrl);
                    } else {
                        listener.onFailure("Document does not exist");
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onFailure("Error fetching version info: " + e.getMessage());
                });
    }

    // Listener interface for version info fetch
    public interface OnVersionInfoFetchedListener {
        void onSuccess(String latestVersion, String updateUrl);
        void onFailure(String errorMessage);
    }
}