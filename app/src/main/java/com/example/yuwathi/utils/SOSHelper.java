package com.example.yuwathi.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import java.util.List;
import java.util.Locale;

/**
 * Helper for composing and launching SOS-related intents/messages.
 */
public class SOSHelper {

    private final Context context;  // App context needed to access system services

    // Constructor - takes the app context
    public SOSHelper(Context context) {

        this.context = context;
    }

    /**
     * Builds SOS message with Google Maps location link
     */
    public String buildSosMessage(double latitude, double longitude) {
        return String.format(Locale.getDefault(),
                "SOS! I need help. My location: https://maps.google.com/?q=%f,%f",
                latitude,
                longitude);
    }

    /**
     * Opens SMS app with multiple recipients and pre-filled message
     */
    public void openSmsComposer(List<String> phoneNumbers, String message) {

        // If no contacts available, do nothing
        if (phoneNumbers == null || phoneNumbers.isEmpty()) {
            return;
        }

        // Join multiple phone numbers using semicolon (;)
        String joined = android.text.TextUtils.join(";", phoneNumbers);

        // Create intent to send SMS
        Intent intent = new Intent(Intent.ACTION_SENDTO);

        // Set SMS URI with recipients
        intent.setData(Uri.parse("smsto:" + joined));

        // Set SMS body message
        intent.putExtra("sms_body", message);

        // Needed when calling from non-activity context
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Open SMS application
        context.startActivity(intent);
    }

    /**
     * Opens phone dialer with emergency number (119)
     */
    public void openEmergencyDialer() {

        // Create dial intent with emergency number
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:119"));

        // Needed when calling from non-activity context
        dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Open phone dialer
        context.startActivity(dialIntent);
    }
}