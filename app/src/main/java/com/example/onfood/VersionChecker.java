package com.example.onfood;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class VersionChecker {
    private FirebaseFirestore db;

    public VersionChecker() {
        db = FirebaseFirestore.getInstance();
    }

    // Method to check the current app version against Firestore
    public void checkForUpdates(Context context) {
        String currentVersion = getCurrentAppVersion(context);

        db.collection("appInfo").document("versionInfo")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String latestVersion = documentSnapshot.getString("latestVersion");
                        String updateUrl = documentSnapshot.getString("updateUrl");

                        if (latestVersion != null && currentVersion != null) {
                            if (isVersionLower(currentVersion, latestVersion)) {
                                // Prompt user to update
                                promptUserToUpdate(updateUrl);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error fetching version info: " + e.getMessage());
                });
    }

    // Method to get the current app version
    private String getCurrentAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName; // Return the version name
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to compare versions
    private boolean isVersionLower(String currentVersion, String latestVersion) {
        return currentVersion.compareTo(latestVersion) < 0; // Compare version strings
    }

    // Method to prompt the user to update (you can customize this)
    private void promptUserToUpdate(String updateUrl) {
        // Implement a dialog or toast to prompt the user to download the latest version
        System.out.println("A new version is available! Please update from: " + updateUrl);
        // You might want to open a browser or an intent to download the new version
    }
}