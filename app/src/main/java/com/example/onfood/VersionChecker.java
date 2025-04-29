package com.example.onfood;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

public class VersionChecker {
    private FirestoreSetup firestoreSetup;

    public VersionChecker() {
        firestoreSetup = new FirestoreSetup();
    }

    // Method to check the current app version against Firestore
    public void checkForUpdates(Context context) {
        String currentVersion = getCurrentAppVersion(context);

        firestoreSetup.fetchVersionInfo(new FirestoreSetup.OnVersionInfoFetchedListener() {
            @Override
            public void onSuccess(String latestVersion, String updateUrl) {
                if (latestVersion != null && currentVersion != null) {
                    if (isVersionLower(currentVersion, latestVersion)) {
                        // Prompt user to update
                        promptUserToUpdate(context, updateUrl);
                    }
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle the error appropriately
                System.err.println("Error fetching version info: " + errorMessage);
            }
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

    // Method to prompt the user to update
    private void promptUserToUpdate(Context context, String updateUrl) {
        new AlertDialog.Builder(context)
                .setTitle("Update Available")
                .setMessage("A new version is available! Please update to the latest version.")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Open the update URL in a browser or handle it accordingly
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl)));
                        System.out.println("Redirecting to update URL: " + updateUrl);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}