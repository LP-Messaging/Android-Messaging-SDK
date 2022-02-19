package com.liveperson.sample.app.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.liveperson.infra.model.PushMessage;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.sample.app.MessagingActivity;
import com.liveperson.sample.app.R;
import com.liveperson.sample.app.push.PushUtils;

import java.util.List;

/**
 * ***** Sample app class - Not related to Messaging SDK *****
 *
 * Used as an example of how to create push notification in terms of UI.
 * As best practise each host app needs to handle the push notifications UI implementation.
 *
 */
public class NotificationUI {
    public static final int PUSH_NOTIFICATION_ID = 143434567;
    public static final String NOTIFICATION_EXTRA = "notification_extra";

    private static final String CHANNEL_SERVICE_NOTIFICATION_ID = "channel_service_notification";
    private static final String CHANNEL_PUSH_NOTIFICATION_ID = "channel_push_notification";
    public static final String NOTIFICATION_MESSAGE_ID = "notification_message_id";


	public static void showPushNotification(Context ctx, PushMessage pushMessage) {
        Notification.Builder builder = createNotificationBuilder(ctx, CHANNEL_PUSH_NOTIFICATION_ID, "Push Notification", true);

        builder.setContentIntent(getPendingIntent(ctx, pushMessage.getPushMessageId())).
            setContentTitle(pushMessage.getMessage()).
            setAutoCancel(true).
            setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS).
            setSmallIcon(R.mipmap.ic_launcher).
            setNumber(pushMessage.getCurrentUnreadMessagesCounter()).
            setStyle(new Notification.InboxStyle()

                    .addLine(TextUtils.isEmpty(pushMessage.getTitle()) ? "" : pushMessage.getMessage())
                    .addLine(pushMessage.getFrom())
                    .addLine(pushMessage.getBrandId())
                    .addLine(pushMessage.getConversationId())
                    .addLine(pushMessage.getBackendService())
                    .addLine(pushMessage.getCollapseKey())
                    .addLine("Unread messages : " + LivePerson.getNumUnreadMessages(pushMessage.getBrandId()))

            );

        // If payload contains title and message, set title as contentTitle
        if (!TextUtils.isEmpty(pushMessage.getTitle())) {
            builder.setContentTitle(pushMessage.getTitle()).
                    setContentText(pushMessage.getMessage());
        }

		if (Build.VERSION.SDK_INT >= 21) {
            builder = builder.
                    setCategory(Notification.CATEGORY_MESSAGE).
                    setPriority(Notification.PRIORITY_HIGH);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setTimeoutAfter(pushMessage.getLookBackPeriod());
        }

        getNotificationManager(ctx).notify(PUSH_NOTIFICATION_ID, builder.build());
    }

    public static Notification.Builder createUploadNotificationBuilder(Context ctx) {
        return createServiceNotificationBuilder(ctx, "Uploading image", android.R.drawable.arrow_up_float);
    }

    public static Notification.Builder createDownloadNotificationBuilder(Context ctx) {
        return createServiceNotificationBuilder(ctx, "Downloading image", android.R.drawable.arrow_down_float);
    }

    public static void hideNotification(Context ctx){
        getNotificationManager(ctx).cancel(PUSH_NOTIFICATION_ID);

    }

    private static Notification.Builder createServiceNotificationBuilder(Context ctx, String contentTitle, int smallIcon) {
        Notification.Builder notificationBuilder = createNotificationBuilder(ctx, CHANNEL_SERVICE_NOTIFICATION_ID, "Foreground Service", false);

        notificationBuilder
                .setContentIntent(getPendingIntent(ctx, null))
                .setContentTitle(contentTitle)
                .setSmallIcon(smallIcon)
                .setProgress(0, 0, true);

        return notificationBuilder;
    }

    /**
     * Create notification builder according to platform level.
     */
    private static Notification.Builder createNotificationBuilder(Context ctx, String channelId, String channelName, boolean isHighImportance) {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(ctx);
        } else {
            //Create a channel for the notification.
            createNotificationChannel(ctx, channelId, channelName, isHighImportance);
            builder = new Notification.Builder(ctx, channelId);
        }

        return builder;
    }

    /**
     * Creates a notification channel with the given parameters.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void createNotificationChannel(Context context, String channelId, String channelName, boolean isHighImportance) {
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        if (isHighImportance) {
            notificationChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
        }
        getNotificationManager(context).createNotificationChannel(notificationChannel);
    }

    private static NotificationManager getNotificationManager(Context ctx) {
        return (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
    }


    private static PendingIntent getPendingIntent(Context ctx, String pushMessageId) {
        Intent showIntent = new Intent(ctx, MessagingActivity.class);
        showIntent.putExtra(NOTIFICATION_EXTRA, true);
        showIntent.putExtra(NOTIFICATION_MESSAGE_ID, pushMessageId);

        int intentFlags;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            // Workaround for KitKat notification action Pending Intent fails after application re-install
            intentFlags = PendingIntent.FLAG_ONE_SHOT;
        } else {
            intentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            intentFlags |= PendingIntent.FLAG_IMMUTABLE;
        }

        return PendingIntent.getActivity(ctx, 0, showIntent, intentFlags);
    }

    /************************ Example of app Icon Badge - For Samsung *******************************/
    public static void setBadge(Context context, int count) {
        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        context.sendBroadcast(intent);
    }

    /**
     * Example of app icon badge for Huawei.
     *
     * @param context     The context
     * @param badgeNumber The badge number
     */
    public static void setBadgeForHuawei(Context context, int badgeNumber) {
        // Uncomment this once Huawei releases new version with API 31 support
//        if (PushUtils.INSTANCE.isHuaweiServicesAvailable(context)) {
//            Bundle extra = new Bundle();
//            extra.putString("package", "com.liveperson.messaging.test");
//            extra.putString("class", "com.liveperson.messaging.test.ui.activities.IntroActivity");
//            extra.putInt("badgenumber", badgeNumber);
//            context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, extra);
//        }
    }

    public static String getLauncherClassName(Context context) {

        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                return resolveInfo.activityInfo.name;
            }
        }
        return null;
    }

    /**
     * Listen to changes in unread messages counter and updating app icon badge
     */
    public static class BadgeBroadcastReceiver extends BroadcastReceiver{

        public BadgeBroadcastReceiver(){}

        @Override
        public void onReceive(Context context, Intent intent) {
            int unreadCounter = intent.getIntExtra(LivePerson.ACTION_LP_UPDATE_NUM_UNREAD_MESSAGES_EXTRA, 0);
            NotificationUI.setBadge(context, unreadCounter);
            NotificationUI.setBadgeForHuawei(context, unreadCounter);
        }
    }
}
