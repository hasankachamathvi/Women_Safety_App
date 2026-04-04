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

    public String buildSosMessage(double latitude, double longitude) {
        return String.format(Locale.getDefault(),
                "SOS! I need help. My location: https://maps.google.com/?q=%f,%f",
                latitude,
                longitude);
    }

    public void openSmsComposer(List<String> phoneNumbers, String message) {
        if (phoneNumbers == null || phoneNumbers.isEmpty()) {
            return;
        }
        String joined = android.text.TextUtils.join(";", phoneNumbers);
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + joined));
        intent.putExtra("sms_body", message);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void openEmergencyDialer() {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:119"));
        dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dialIntent);
    }
}
