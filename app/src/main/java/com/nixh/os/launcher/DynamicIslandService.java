package com.nixh.os.launcher;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DynamicIslandService extends Service {
    private static final String CHANNEL_ID = "nixh_island_status";
    private static final int NOTIFICATION_ID = 4101;
    private WindowManager wm;
    private LinearLayout island;
    private TextView title, subtitle, battery;
    private boolean expanded = false;
    private int accent;
    private int batteryLevel = -1;
    private boolean charging = false;
    private final BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            if (intent == null || title == null) return;
            String eventTitle = intent.getStringExtra(IslandNotificationListener.EXTRA_TITLE);
            String eventText = intent.getStringExtra(IslandNotificationListener.EXTRA_TEXT);
            String kind = intent.getStringExtra(IslandNotificationListener.EXTRA_KIND);
            title.setText("music".equals(kind) ? "♫  NOW PLAYING" : "●  NOTIFICATION");
            subtitle.setText(eventTitle == null ? "New notification" : eventTitle);
            battery.setText(eventText == null ? "" : eventText);
            subtitle.setVisibility(View.VISIBLE);
            battery.setVisibility(eventText == null || eventText.isEmpty() ? View.GONE : View.VISIBLE);
            if (!expanded) {
                expanded = true;
                island.setPadding(dp(16), dp(13), dp(16), dp(13));
                WindowManager.LayoutParams lp = (WindowManager.LayoutParams) island.getLayoutParams();
                lp.width = dp(248); lp.height = dp(104);
                wm.updateViewLayout(island, lp);
                island.setBackground(background(true));
            }
        }
    };
    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
            if (level >= 0 && scale > 0) batteryLevel = Math.round(level * 100f / scale);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            charging = status == BatteryManager.BATTERY_STATUS_CHARGING
                    || status == BatteryManager.BATTERY_STATUS_FULL;
            updateContent();
        }
    };

    @Override public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildNotification());
        accent = getSharedPreferences("nixhos_theme", MODE_PRIVATE)
                .getInt("accent", Color.rgb(139, 169, 255));
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (wm == null || !android.provider.Settings.canDrawOverlays(this)) {
            stopSelf();
            return;
        }
        buildIsland();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent sticky = registerReceiver(batteryReceiver, filter);
        if (sticky != null) batteryReceiver.onReceive(this, sticky);
        IntentFilter eventFilter = new IntentFilter(IslandNotificationListener.ACTION_ISLAND_EVENT);
        if (Build.VERSION.SDK_INT >= 33) registerReceiver(eventReceiver, eventFilter, Context.RECEIVER_NOT_EXPORTED);
        else registerReceiver(eventReceiver, eventFilter);
        int type = Build.VERSION.SDK_INT >= 26
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;
        WindowManager.LayoutParams p = new WindowManager.LayoutParams(
                dp(132), dp(36), type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        p.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        // The pill hugs the centred punch-hole/camera area at the top edge.
        p.y = dp(3);
        try { wm.addView(island, p); }
        catch (RuntimeException ex) { stopSelf(); }
    }

    private void buildIsland() {
        island = new LinearLayout(this);
        island.setOrientation(LinearLayout.VERTICAL);
        island.setGravity(Gravity.CENTER);
        island.setPadding(dp(14), dp(7), dp(14), dp(7));
        island.setBackground(background(false));
        island.setElevation(dp(16));

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView dot = text("●", 9, accent, true);
        top.addView(dot);
        title = text("NIXH ISLAND", 11, Color.WHITE, true);
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(-2, -2);
        tp.leftMargin = dp(7);
        top.addView(title, tp);
        island.addView(top);

        subtitle = text("Ready for your day", 10, Color.rgb(170, 178, 196), false);
        battery = text("", 10, Color.WHITE, true);
        island.addView(subtitle);
        island.addView(battery);
        subtitle.setVisibility(View.GONE);
        battery.setVisibility(View.GONE);

        island.setOnClickListener(v -> toggle());
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        return t;
    }

    private GradientDrawable background(boolean open) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(Color.rgb(3, 4, 8));
        d.setCornerRadius(dp(open ? 30 : 24));
        d.setStroke(dp(1), Color.argb(95, 150, 165, 205));
        return d;
    }

    private void updateContent() {
        if (battery == null) return;
        if (batteryLevel >= 0) {
            battery.setText((charging ? "⚡ Charging  ·  " : "Battery  ·  ") + batteryLevel + "%");
        }
        if (expanded) {
            subtitle.setText(charging ? "Your phone is charging" : "NixhOS is active");
        }
    }

    private void toggle() {
        expanded = !expanded;
        subtitle.setVisibility(expanded ? View.VISIBLE : View.GONE);
        battery.setVisibility(expanded ? View.VISIBLE : View.GONE);
        title.setText(expanded ? "NIXH  •  YOUR SPACE" : "NIXH ISLAND");
        if (expanded) updateContent();
        island.setPadding(dp(16), dp(expanded ? 13 : 7), dp(16), dp(expanded ? 13 : 7));
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) island.getLayoutParams();
        p.width = dp(expanded ? 248 : 132);
        p.height = dp(expanded ? 104 : 36);
        wm.updateViewLayout(island, p);
        island.setBackground(background(expanded));
        island.animate().scaleX(1.035f).scaleY(1.035f).setDuration(130)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> island.animate().scaleX(1f).scaleY(1f).setDuration(150).start()).start();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Nixh Island", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Keeps the Nixh Island overlay available while you use your phone");
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        Intent open = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, open,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= 23 ? PendingIntent.FLAG_IMMUTABLE : 0));
        Notification.Builder b = Build.VERSION.SDK_INT >= 26
                ? new Notification.Builder(this, CHANNEL_ID) : new Notification.Builder(this);
        return b.setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Nixh Island is active")
                .setContentText("Tap the island for live battery status")
                .setContentIntent(pi).setOngoing(true).build();
    }

    private int dp(float n) {
        return (int)(n * getResources().getDisplayMetrics().density + .5f);
    }

    @Override public void onDestroy() {
        try { unregisterReceiver(batteryReceiver); } catch (Exception ignored) {}
        try { unregisterReceiver(eventReceiver); } catch (Exception ignored) {}
        if (island != null && wm != null) try { wm.removeView(island); } catch (Exception ignored) {}
        super.onDestroy();
    }
    @Override public IBinder onBind(Intent intent) { return null; }
}
