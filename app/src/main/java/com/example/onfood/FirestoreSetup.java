package com.example.onfood;

import com.google.firebase.firestore.FirebaseFirestore;
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
}