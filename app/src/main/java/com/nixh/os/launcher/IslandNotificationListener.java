package com.nixh.os.launcher;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;

public class IslandNotificationListener extends NotificationListenerService {
    public static final String ACTION_ISLAND_EVENT = "com.nixh.os.launcher.ISLAND_EVENT";
    public static final String EXTRA_TITLE = "event_title";
    public static final String EXTRA_TEXT = "event_text";
    public static final String EXTRA_KIND = "event_kind";

    @Override public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn == null || getPackageName().equals(sbn.getPackageName())) return;
        Notification n = sbn.getNotification();
        if (n == null || n.extras == null) return;
        Bundle e = n.extras;
        CharSequence title = e.getCharSequence(Notification.EXTRA_TITLE);
        CharSequence body = e.getCharSequence(Notification.EXTRA_TEXT);
        if ((title == null || title.length() == 0) && (body == null || body.length() == 0)) return;
        String kind = Notification.CATEGORY_TRANSPORT.equals(n.category) ? "music" : "notification";
        Intent event = new Intent(ACTION_ISLAND_EVENT);
        event.setPackage(getPackageName());
        event.putExtra(EXTRA_TITLE, title == null ? "Notification" : title.toString());
        event.putExtra(EXTRA_TEXT, body == null ? "" : body.toString());
        event.putExtra(EXTRA_KIND, kind);
        sendBroadcast(event);
    }
}