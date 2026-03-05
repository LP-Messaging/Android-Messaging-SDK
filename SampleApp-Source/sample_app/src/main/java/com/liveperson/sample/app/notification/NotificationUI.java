package com.liveperson.sample.app.notification;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.liveperson.infra.model.PushMessage;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.sample.app.R;
import com.liveperson.sample.app.activities.MessagingActivity;

import java.util.List;
import java.util.UUID;

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

    public static final int SAMMARY_NOTIFICATION_ID = 12342144;


    public static void showPushNotification(Context ctx, PushMessage pushMessage) {
        NotificationCompat.Builder builder = createNotificationBuilder(ctx, CHANNEL_PUSH_NOTIFICATION_ID, "Push Notification", true);

        builder.setContentIntent(getPendingIntent(ctx, pushMessage.getPushMessageId()))
                .setContentTitle(pushMessage.getMessage())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setPriority(Notification.PRIORITY_HIGH)
                .setTimeoutAfter(pushMessage.getLookBackPeriod())
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(TextUtils.isEmpty(pushMessage.getTitle()) ? pushMessage.getMessage() : pushMessage.getTitle())
                        .addLine(pushMessage.getFrom())
                        .addLine(pushMessage.getBrandId())
                        .addLine(pushMessage.getConversationId())
                        .addLine(pushMessage.getBackendService())
                        .addLine(pushMessage.getCollapseKey())
                        .addLine("id: " + pushMessage.getPushMessageId())
                )
                .setGroup(pushMessage.getConversationId());

        int unreadMessages = pushMessage.getCurrentUnreadMessagesCounter();
        Notification summaryNotification = new NotificationCompat.Builder(ctx, CHANNEL_PUSH_NOTIFICATION_ID)
                .setContentTitle("You have " + unreadMessages + " unread messages")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setGroup(pushMessage.getConversationId())
                .setGroupSummary(true)
                .setNumber(unreadMessages)
                .setOnlyAlertOnce(true)
                .build();

        // If payload contains title and message, set title as contentTitle
        if (!TextUtils.isEmpty(pushMessage.getTitle())) {
            builder.setContentTitle(pushMessage.getTitle()).setContentText(pushMessage.getMessage());
        }

        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            int id = UUID.randomUUID().hashCode();
            NotificationManagerCompat manager = NotificationManagerCompat.from(ctx);
            manager.notify(id, builder.build());
            manager.notify(SAMMARY_NOTIFICATION_ID, summaryNotification);
        }
    }

    public static NotificationCompat.Builder createUploadNotificationBuilder(Context ctx) {
        return createServiceNotificationBuilder(ctx, "Uploading image", android.R.drawable.arrow_up_float);
    }

    public static NotificationCompat.Builder createDownloadNotificationBuilder(Context ctx) {
        return createServiceNotificationBuilder(ctx, "Downloading image", android.R.drawable.arrow_down_float);
    }

    public static void hideNotification(Context ctx){
        NotificationManagerCompat.from(ctx).cancel(PUSH_NOTIFICATION_ID);
    }

    private static NotificationCompat.Builder createServiceNotificationBuilder(Context ctx, String contentTitle, int smallIcon) {
        NotificationCompat.Builder notificationBuilder = createNotificationBuilder(ctx, CHANNEL_SERVICE_NOTIFICATION_ID, "Foreground Service", false);

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
    private static NotificationCompat.Builder createNotificationBuilder(Context ctx, String channelId, String channelName, boolean isHighImportance) {
        createNotificationChannel(ctx, channelId, channelName, isHighImportance);
        return new NotificationCompat.Builder(ctx, channelId);
    }

    /**
     * Creates a notification channel with the given parameters.
     */
    /**
     * Creates a notification channel with the given parameters.
     */
    private static void createNotificationChannel(
            Context context,
            String channelId,
            String channelName,
            boolean isHighImportance
    ) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        int importance = isHighImportance
                ? NotificationManagerCompat.IMPORTANCE_HIGH
                : NotificationManagerCompat.IMPORTANCE_DEFAULT;
        NotificationChannelCompat notificationChannel;
        notificationChannel = new NotificationChannelCompat.Builder(channelId, importance)
                .setName(channelName)
                .setVibrationEnabled(isHighImportance)
                .setShowBadge(true)
                .build();
        manager.createNotificationChannel(notificationChannel);
    }

    private static NotificationManager getNotificationManager(Context ctx) {
        return (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
    }


    private static PendingIntent getPendingIntent(Context ctx, String pushMessageId) {
        Intent showIntent = new Intent(ctx, MessagingActivity.class);
        showIntent.putExtra(NOTIFICATION_EXTRA, true);
        showIntent.putExtra(NOTIFICATION_MESSAGE_ID, pushMessageId);

        int intentFlags;
        intentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
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
