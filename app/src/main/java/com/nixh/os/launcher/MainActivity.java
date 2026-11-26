package com.nixh.os.launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private final int bg = Color.rgb(8, 10, 16);
    private final int panel = Color.rgb(24, 29, 41);
    private int accent;
    private SharedPreferences prefs;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = getSharedPreferences("nixhos_theme", MODE_PRIVATE);
        accent = prefs.getInt("accent", Color.rgb(80, 150, 255));
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        render();
    }
    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(radius); return d;
    }
    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD); return t;
    }
    private void addSpace(LinearLayout box, int height) { box.addView(new View(this), new LinearLayout.LayoutParams(1, height)); }
    private TextView card(String value, int size) {
        TextView t = text(value, size, Color.WHITE, false); t.setPadding(20, 18, 20, 18); t.setBackground(shape(panel, 28)); return t;
    }
    private void render() {
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(24, 24, 24, 20); root.setBackgroundColor(bg);
        root.addView(text("NIXHOS   •   YOUR SPACE", 13, accent, true)); addSpace(root, 42);
        String time = new SimpleDateFormat("h:mm", Locale.getDefault()).format(new Date());
        root.addView(text(time, 58, Color.WHITE, true));
        root.addView(text(new SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(new Date()), 15, Color.LTGRAY, false)); addSpace(root, 28);
        TextView island = card("●     NixhOS is ready                         •••", 15); island.setGravity(Gravity.CENTER); island.setBackground(shape(Color.rgb(35, 42, 58), 40));
        root.addView(island, new LinearLayout.LayoutParams(-1, 58)); addSpace(root, 30);
        root.addView(text("Your space", 25, Color.WHITE, true)); addSpace(root, 14);
        root.addView(card("WEATHER     Add your weather widget", 15), new LinearLayout.LayoutParams(-1, -2)); addSpace(root, 20);
        root.addView(text("Quick access", 18, Color.WHITE, true)); addSpace(root, 10);
        String[] apps = {"☎  Phone", "✉  Messages", "▧  Gallery", "⚙  Settings"};
        for (String app : apps) {
            TextView item = card(app, 17); LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2); p.bottomMargin = 8; root.addView(item, p);
            item.setOnClickListener(v -> { if (app.contains("Settings")) showPersonalization(); else openApp(app); });
        }
        Button customize = new Button(this); customize.setText("PERSONALIZE  →"); customize.setTextColor(Color.WHITE);
        customize.setBackgroundTintList(android.content.res.ColorStateList.valueOf(accent)); root.addView(customize, new LinearLayout.LayoutParams(-1, 54));
        customize.setOnClickListener(v -> showPersonalization());
        TextView footer = text("HOME        •        SEARCH        •        PERSONALIZE", 12, accent, true); footer.setGravity(Gravity.CENTER);
        root.addView(footer, new LinearLayout.LayoutParams(-1, 0, 1)); setContentView(root);
    }
    private void showPersonalization() {
        String[] names = {"Electric Blue", "Violet", "Mint", "Sunset Orange", "Rose Pink", "White"};
        int[] colors = {Color.rgb(80,150,255), Color.rgb(166,112,255), Color.rgb(70,210,170), Color.rgb(255,145,75), Color.rgb(255,105,160), Color.WHITE};
        new AlertDialog.Builder(this).setTitle("NixhOS • Personalize").setItems(names, (dialog, which) -> { accent = colors[which]; prefs.edit().putInt("accent", accent).apply(); render(); }).setNegativeButton("Close", null).show();
    }
    private void openApp(String label) {
        String pkg = label.contains("Phone") ? "com.google.android.dialer" : label.contains("Messages") ? "com.google.android.apps.messaging" : "com.google.android.apps.photos";
        try { android.content.Intent launch = getPackageManager().getLaunchIntentForPackage(pkg); if (launch != null) startActivity(launch); else new AlertDialog.Builder(this).setMessage("App not found on this phone.").setPositiveButton("OK", null).show(); } catch (Exception ignored) { }
    }
}
