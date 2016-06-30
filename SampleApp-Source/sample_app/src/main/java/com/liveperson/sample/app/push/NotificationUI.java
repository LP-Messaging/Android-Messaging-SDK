package com.liveperson.sample.app.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.liveperson.infra.messaging_ui.uicomponents.PushMessageParser;
import com.liveperson.sample.app.FragmentContainerActivity;
import com.liveperson.sample.app.MainActivity;
import com.liveperson.sample.app.R;
import com.liveperson.sample.app.account.AccountStorage;

/**
 * Created by ofira on 6/26/16.
 */
public class NotificationUI {

    private static final String TAG = NotificationUI.class.getSimpleName();
    private static final int NOTIFICATION_ID = 143434567;
    public static final String PUSH_NOTIFICATION = "push_notification";


    public static void showNotification(Context ctx, PushMessageParser messageParser) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx).
                setContentIntent(getPendingIntent(ctx)).
                setContentTitle(messageParser.getAgentName()).
                setAutoCancel(true).
                setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS).
                setSmallIcon(R.mipmap.ic_launcher).
                setContentText(messageParser.getMessage());

        if (Build.VERSION.SDK_INT >= 21) {
            builder = builder.
                    setCategory(Notification.CATEGORY_MESSAGE).
                    setPriority(Notification.PRIORITY_HIGH);
        }


        getNotificationManager(ctx).notify(TAG, NOTIFICATION_ID, builder.build());
    }

    private static NotificationManager getNotificationManager(Context ctx) {
        return (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
    }


    private static PendingIntent getPendingIntent(Context ctx) {
        Intent showIntent = new Intent(ctx, MainActivity.class);
        showIntent.putExtra(PUSH_NOTIFICATION, true);
        return PendingIntent.getActivity(ctx, 0, showIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
