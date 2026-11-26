package com.nixh.os.launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    private SharedPreferences prefs;
    private int accent, bg, surface, text, muted;
    private LinearLayout root, content;
    private boolean musicPlaying = false;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("nixhos_theme", MODE_PRIVATE);
        accent = prefs.getInt("accent", Color.rgb(139, 169, 255));
        bg = prefs.getInt("background", Color.rgb(8, 11, 19));
        applyPalette();
        renderHome();
    }

    private void applyPalette() {
        surface = blend(bg, Color.WHITE, 0.075f);
        text = Color.rgb(244, 246, 252);
        muted = Color.rgb(151, 160, 180);
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        getWindow().getDecorView().setSystemUiVisibility(0);
    }
    private int blend(int a, int b, float f) {
        return Color.rgb((int)(Color.red(a)*(1-f)+Color.red(b)*f),
                (int)(Color.green(a)*(1-f)+Color.green(b)*f),
                (int)(Color.blue(a)*(1-f)+Color.blue(b)*f));
    }
    private GradientDrawable shape(int color, int radius, int stroke) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(dp(radius));
        if (stroke != 0) d.setStroke(dp(1), stroke);
        return d;
    }
    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + .5f); }
    private TextView label(String s, int size, int color, boolean bold) {
        TextView v = new TextView(this); v.setText(s); v.setTextSize(size); v.setTextColor(color);
        v.setGravity(Gravity.CENTER_VERTICAL);
        if (bold) v.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        return v;
    }
    private LinearLayout column() { LinearLayout l=new LinearLayout(this); l.setOrientation(1); return l; }
    private LinearLayout row() { LinearLayout l=new LinearLayout(this); l.setOrientation(0); l.setGravity(Gravity.CENTER_VERTICAL); return l; }
    private void gap(LinearLayout l, int h) { View v=new View(this); l.addView(v,new LinearLayout.LayoutParams(1,dp(h))); }
    private TextView chip(String s) {
        TextView v=label(s,12,muted,true); v.setPadding(dp(12),dp(8),dp(12),dp(8));
        v.setBackground(shape(surface,30,blend(surface,Color.WHITE,.10f))); return v;
    }
    private TextView action(String s) {
        TextView v=label(s,14,text,true); v.setGravity(Gravity.CENTER);
        v.setPadding(dp(14),dp(13),dp(14),dp(13)); v.setBackground(shape(surface,18,blend(surface,Color.WHITE,.08f)));
        return v;
    }
    private void renderHome() {
        root=column(); root.setBackgroundColor(bg);
        ScrollView scroll=new ScrollView(this); scroll.setFillViewport(true);
        content=column(); content.setPadding(dp(22),dp(22),dp(22),dp(18));
        scroll.addView(content); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout top=row();
        TextView brand=label("N I X H O S",13,accent,true);
        top.addView(brand,new LinearLayout.LayoutParams(0,dp(34),1));
        TextView profile=chip("●  YOUR SPACE"); top.addView(profile);
        content.addView(top);
        gap(content,30);

        Calendar now=Calendar.getInstance();
        TextView time=label(new SimpleDateFormat("h:mm",Locale.getDefault()).format(now.getTime()),64,text,true);
        time.setLetterSpacing(-.04f); content.addView(time);
        TextView date=label(new SimpleDateFormat("EEEE  ·  d MMMM",Locale.getDefault()).format(now.getTime()),15,muted,false);
        content.addView(date);
        gap(content,24);

        LinearLayout island=row(); island.setGravity(Gravity.CENTER);
        island.setPadding(dp(18),dp(13),dp(18),dp(13));
        island.setBackground(shape(Color.rgb(2,3,7),28,blend(surface,Color.WHITE,.16f)));
        TextView live=label("●",10,accent,true); island.addView(live);
        TextView islandText=label("   NixhOS is ready",13,text,true);
        island.addView(islandText,new LinearLayout.LayoutParams(0,dp(24),1));
        island.addView(label("•••",16,muted,true));
        content.addView(island,new LinearLayout.LayoutParams(-1,-2));
        island.setOnClickListener(v -> {
            if (android.provider.Settings.canDrawOverlays(this)) {
                startService(new Intent(this, DynamicIslandService.class));
                Toast.makeText(this, "Nixh Dynamic Island started", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    android.net.Uri.parse("package:" + getPackageName())));
            }
        });
        gap(content,28);

        LinearLayout heading=row();
        heading.addView(label("Today",23,text,true),new LinearLayout.LayoutParams(0,dp(34),1));
        heading.addView(chip("PERSONAL SPACE"));
        content.addView(heading);
        gap(content,12);

        LinearLayout weather=column(); weather.setPadding(dp(18),dp(17),dp(18),dp(17));
        weather.setBackground(shape(blend(surface,accent,.10f),24,blend(surface,accent,.35f)));
        LinearLayout wr=row();
        LinearLayout wl=column(); wl.addView(label("WEATHER  ·  YOUR LOCATION",10,muted,true));
        gap(wl,5); wl.addView(label("Add weather",20,text,true));
        wr.addView(wl,new LinearLayout.LayoutParams(0,-2,1));
        wr.addView(label("☼",38,accent,true)); weather.addView(wr);
        gap(weather,8); weather.addView(label("A calm space for your day",13,muted,false));
        content.addView(weather);
        gap(content,22);

        content.addView(label("At a glance",18,text,true)); gap(content,12);
        LinearLayout cards=row();
        LinearLayout music=column(); music.setPadding(dp(16),dp(15),dp(16),dp(15));
        music.setBackground(shape(surface,22,blend(surface,Color.WHITE,.08f)));
        music.addView(label("♫  NOW PLAYING",10,accent,true)); gap(music,12);
        music.addView(label("Your music",17,text,true)); gap(music,3);
        music.addView(label("Nothing playing yet",12,muted,false)); gap(music,12);
        TextView play=action("▶   Play");
        play.setOnClickListener(v->{musicPlaying=!musicPlaying;play.setText(musicPlaying?"Ⅱ   Pause":"▶   Play");});
        music.addView(play);
        LinearLayout focus=column(); focus.setPadding(dp(16),dp(15),dp(16),dp(15));
        focus.setBackground(shape(surface,22,blend(surface,Color.WHITE,.08f)));
        focus.addView(label("◷  FOCUS",10,accent,true)); gap(focus,12);
        focus.addView(label("Make room",17,text,true)); gap(focus,3);
        focus.addView(label("One thing at a time",12,muted,false)); gap(focus,12);
        TextView start=action("Start focus"); start.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Focus mode").setMessage("Focus tools are coming to NixhOS.").setPositiveButton("Got it",null).show()); focus.addView(start);
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(0,-2,1); cp.rightMargin=dp(8);
        cards.addView(music,cp); cards.addView(focus,new LinearLayout.LayoutParams(0,-2,1));
        content.addView(cards);
        gap(content,24);

        LinearLayout quickHead=row(); quickHead.addView(label("Quick launch",18,text,true),new LinearLayout.LayoutParams(0,dp(32),1));
        TextView all=chip("ALL APPS  →"); all.setOnClickListener(v->showAppDrawer()); quickHead.addView(all);
        content.addView(quickHead); gap(content,12);
        LinearLayout apps=row();
        String[][] items={{"☎","Phone","com.google.android.dialer"},{"✉","Messages","com.google.android.apps.messaging"},{"▣","Photos","com.google.android.apps.photos"},{"◉","Camera","com.motorola.camera3"}};
        for(String[] item:items){
            LinearLayout tile=column(); tile.setGravity(Gravity.CENTER); tile.setPadding(dp(5),dp(13),dp(5),dp(13));
            tile.setBackground(shape(surface,19,blend(surface,Color.WHITE,.07f)));
            TextView ico=label(item[0],23,accent,true); ico.setGravity(Gravity.CENTER); tile.addView(ico);
            gap(tile,7); TextView nm=label(item[1],11,text,true); nm.setGravity(Gravity.CENTER); tile.addView(nm);
            LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(0,dp(84),1); ap.rightMargin=dp(6); apps.addView(tile,ap);
            tile.setOnClickListener(v->launchPackage(item[2]));
        }
        content.addView(apps);
        gap(content,20);
        TextView personalize=label("✦     Personalize your NixhOS     →",15,Color.WHITE,true);
        personalize.setGravity(Gravity.CENTER); personalize.setPadding(dp(16),dp(17),dp(16),dp(17));
        personalize.setBackground(shape(accent,20,0)); personalize.setOnClickListener(v->showPersonalize());
        content.addView(personalize);
        gap(content,14);
        TextView note=label("A new kind of Android. Made yours.",11,muted,false); note.setGravity(Gravity.CENTER); content.addView(note);

        LinearLayout nav=row(); nav.setPadding(dp(16),dp(10),dp(16),dp(10)); nav.setBackgroundColor(blend(bg,Color.BLACK,.16f));
        String[] navs={"⌂  Home","▦  Apps","✦  Style"};
        for(int i=0;i<navs.length;i++){
            final int n=i; TextView t=label(navs[i],13,i==0?accent:muted,true); t.setGravity(Gravity.CENTER);
            nav.addView(t,new LinearLayout.LayoutParams(0,dp(42),1));
            t.setOnClickListener(v->{if(n==1)showAppDrawer();else if(n==2)showPersonalize();else renderHome();});
        }
        root.addView(nav); setContentView(root);
    }
    private void launchPackage(String pkg) {
        try { Intent i=getPackageManager().getLaunchIntentForPackage(pkg); if(i!=null)startActivity(i); else Toast.makeText(this,"App not available on this phone",Toast.LENGTH_SHORT).show(); }
        catch(Exception e){Toast.makeText(this,"Could not open app",Toast.LENGTH_SHORT).show();}
    }
    private void showAppDrawer() {
        ArrayList<String> names=new ArrayList<>(); ArrayList<Intent> intents=new ArrayList<>();
        Intent query=new Intent(Intent.ACTION_MAIN,null); query.addCategory(Intent.CATEGORY_LAUNCHER);
        for(android.content.pm.ResolveInfo ri:getPackageManager().queryIntentActivities(query,0)){
            String name=ri.loadLabel(getPackageManager()).toString();
            if(!ri.activityInfo.packageName.equals(getPackageName())) { names.add(name); intents.add(new Intent(Intent.ACTION_MAIN).setClassName(ri.activityInfo.packageName,ri.activityInfo.name).addCategory(Intent.CATEGORY_LAUNCHER)); }
        }
        Collections.sort(names,String.CASE_INSENSITIVE_ORDER);
        new AlertDialog.Builder(this).setTitle("All apps").setItems(names.toArray(new String[0]),(d,w)->{String selected=names.get(w); for(android.content.pm.ResolveInfo ri:getPackageManager().queryIntentActivities(query,0))if(ri.loadLabel(getPackageManager()).toString().equals(selected)){startActivity(new Intent(Intent.ACTION_MAIN).setClassName(ri.activityInfo.packageName,ri.activityInfo.name).addCategory(Intent.CATEGORY_LAUNCHER));break;}}).setNegativeButton("Close",null).show();
    }
    private void showPersonalize() {
        String[] choices={"Electric blue","Violet","Mint","Sunset","Rose","Ice","Custom accent colour","Custom background colour","Reset theme"};
        int[] colors={Color.rgb(139,169,255),Color.rgb(180,137,255),Color.rgb(100,220,185),Color.rgb(255,158,105),Color.rgb(255,135,178),Color.rgb(150,225,255)};
        new AlertDialog.Builder(this).setTitle("Make it yours").setItems(choices,(d,w)->{
            if(w<6){accent=colors[w];prefs.edit().putInt("accent",accent).apply();applyPalette();renderHome();}
            else if(w==6)customColor(true);else if(w==7)customColor(false);
            else {accent=Color.rgb(139,169,255);bg=Color.rgb(8,11,19);prefs.edit().clear().apply();applyPalette();renderHome();}
        }).setNegativeButton("Close",null).show();
    }
    private void customColor(boolean isAccent) {
        EditText input=new EditText(this); input.setSingleLine(true); input.setHint("#8BA9FF");
        new AlertDialog.Builder(this).setTitle(isAccent?"Accent colour (HEX)":"Background colour (HEX)").setView(input)
        .setPositiveButton("Apply",(d,w)->{try{int c=Color.parseColor(input.getText().toString().trim());if(isAccent){accent=c;prefs.edit().putInt("accent",c).apply();}else{bg=c;prefs.edit().putInt("background",c).apply();}applyPalette();renderHome();}catch(Exception e){Toast.makeText(this,"Use a valid HEX colour, e.g. #8BA9FF",Toast.LENGTH_LONG).show();}})
        .setNegativeButton("Cancel",null).show();
    }
}
