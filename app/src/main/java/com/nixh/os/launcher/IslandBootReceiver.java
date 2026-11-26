package com.nixh.os.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

public class IslandBootReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        if (intent == null || !Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;
        if (!Settings.canDrawOverlays(context)) return;
        Intent service = new Intent(context, DynamicIslandService.class);
        try {
            if (Build.VERSION.SDK_INT >= 26) context.startForegroundService(service);
            else context.startService(service);
        } catch (RuntimeException ignored) {
            // Android may defer background starts; the user can restart Island from NixhOS.
        }
    }
}