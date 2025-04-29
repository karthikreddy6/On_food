package com.example.onfood;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.onfood.Activity.MainActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;

public class VersionChecker {

    private FirestoreSetup firestoreSetup;
    private Context context;
    private static final String APK_NAME = "app_name.apk";
    String destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + APK_NAME;
    File apkFile = new File(destinationPath);

    public VersionChecker(Context context) {
        this.context = context;
        firestoreSetup = new FirestoreSetup();
    }

    // Method to check the current app version against Firestore
    public void checkForUpdates() {
        String currentVersion = getCurrentAppVersion();

        firestoreSetup.fetchVersionInfo(new FirestoreSetup.OnVersionInfoFetchedListener() {
            @Override
            public void onSuccess(String latestVersion, String updateUrl) {
                if (latestVersion != null && currentVersion != null) {
                    if (isVersionLower(currentVersion, latestVersion)) {
                        // Show dialog to confirm download
                        showUpdateDialog(updateUrl);
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
    private String getCurrentAppVersion() {
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

    // Show dialog to confirm download
    private void showUpdateDialog(String updateUrl) {
        new AlertDialog.Builder(context)
                .setTitle("Update Available")
                .setMessage("A new version is available. Do you want to update?")
                .setPositiveButton("Update Now ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Start the download if the user agrees
                        startDownload(updateUrl);
                    }
                })
                .setNegativeButton("later", null)
                .show();
    }

    // Start the download of the APK
    private void startDownload(String updateUrl) {
        String destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + APK_NAME;

        if (apkFile.exists()) {
            boolean deleted = apkFile.delete();
            if (deleted) {
                // Notify the user that the old APK was deleted
                Toast.makeText(context, "Previous APK deleted. Downloading new version.", Toast.LENGTH_SHORT).show();
            } else {
                // If failed to delete, you can show an error message or handle the issue
                Toast.makeText(context, "Failed to delete previous APK. Proceeding with the download.", Toast.LENGTH_SHORT).show();
            }
        }
        // Start downloading APK using DownloadManager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(updateUrl))
                .setTitle("APK Download")
                .setDescription("Downloading APK file")
                .setDestinationUri(Uri.parse("file://" + destinationPath))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            long downloadId = downloadManager.enqueue(request);

            // Track download progress
            trackDownloadProgress(downloadManager, downloadId);
        }
    }

    // Track the progress of the download (removed progress bar handling)
    private void trackDownloadProgress(DownloadManager downloadManager, long downloadId) {
        new Thread(() -> {
            boolean downloading = true;
            while (downloading) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                android.database.Cursor cursor = downloadManager.query(query);

                if (cursor != null && cursor.moveToFirst()) {
                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
                        installAPK(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), APK_NAME));
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        downloading = false;
                        ((MainActivity) context).runOnUiThread(() -> Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show());
                    }
                }
                if (cursor != null) {
                    cursor.close();
                }
            }
        }).start();
    }

    // Install the downloaded APK
    private void installAPK(File file) {
        if (file.exists()) {
            Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "APK not found", Toast.LENGTH_SHORT).show();
        }
    }
}
