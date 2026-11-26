package com.nixh.os.launcher;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.view.animation.DecelerateInterpolator;

public class DynamicIslandService extends Service {
 private WindowManager wm; private TextView pill; private boolean expanded=false; private int accent;
 @Override public void onCreate(){
  super.onCreate(); accent=getSharedPreferences("nixhos_theme",MODE_PRIVATE).getInt("accent",Color.rgb(139,169,255));
  wm=(WindowManager)getSystemService(WINDOW_SERVICE); pill=new TextView(this);
  pill.setText("✦   NIXH"); pill.setTextColor(Color.WHITE); pill.setTextSize(13); pill.setGravity(Gravity.CENTER);
  pill.setPadding(dp(24),dp(11),dp(24),dp(11)); pill.setBackground(bg()); pill.setElevation(dp(12));
  pill.setOnClickListener(v->toggle());
  pill.setOnTouchListener(new View.OnTouchListener(){float x,y;int sx,sy;boolean moved;
   public boolean onTouch(View v,MotionEvent e){WindowManager.LayoutParams p=(WindowManager.LayoutParams)v.getLayoutParams();
    if(e.getAction()==MotionEvent.ACTION_DOWN){x=e.getRawX();y=e.getRawY();sx=p.x;sy=p.y;moved=false;return true;}
    if(e.getAction()==MotionEvent.ACTION_MOVE){float dx=e.getRawX()-x,dy=e.getRawY()-y;if(Math.abs(dx)>5||Math.abs(dy)>5)moved=true;p.x=sx+(int)dx;p.y=sy+(int)dy;wm.updateViewLayout(v,p);return true;}
    if(e.getAction()==MotionEvent.ACTION_UP){if(!moved)v.performClick();return true;}return true;
   }});
  int type=Build.VERSION.SDK_INT>=26?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:WindowManager.LayoutParams.TYPE_PHONE;
  WindowManager.LayoutParams p=new WindowManager.LayoutParams(dp(148),dp(42),type,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,PixelFormat.TRANSLUCENT);
  p.gravity=Gravity.TOP|Gravity.CENTER_HORIZONTAL;p.y=dp(8);wm.addView(pill,p);
 }
 private GradientDrawable bg(){GradientDrawable d=new GradientDrawable();d.setColor(Color.rgb(8,10,16));d.setCornerRadius(dp(28));d.setStroke(dp(1),accent);return d;}
 private int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
 private void toggle(){expanded=!expanded;pill.setText(expanded?"NIXH ISLAND  •  READY\n♫ Music     ◉ Alerts     ◷ Calls":"✦   NIXH");pill.setTextSize(expanded?12:13);pill.setPadding(dp(22),dp(expanded?17:11),dp(22),dp(expanded?17:11));WindowManager.LayoutParams p=(WindowManager.LayoutParams)pill.getLayoutParams();p.width=dp(expanded?250:148);p.height=dp(expanded?76:42);wm.updateViewLayout(pill,p);pill.animate().scaleX(1.04f).scaleY(1.04f).setDuration(120).setInterpolator(new DecelerateInterpolator()).withEndAction(()->pill.animate().scaleX(1f).scaleY(1f).setDuration(130).start()).start();}
 @Override public void onDestroy(){if(pill!=null)try{wm.removeView(pill);}catch(Exception ignored){}super.onDestroy();}
 @Override public IBinder onBind(Intent i){return null;}
}