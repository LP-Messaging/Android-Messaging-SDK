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
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.liveperson.infra.model.PushMessage;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.sample.app.MessagingActivity;
import com.liveperson.sample.app.R;

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


	public static void showPushNotification(Context ctx, PushMessage pushMessage) {
        Notification.Builder builder = createNotificationBuilder(ctx, CHANNEL_PUSH_NOTIFICATION_ID, "Push Notification", true);

		builder.setContentIntent(getPendingIntent(ctx)).
			setContentTitle(pushMessage.getMessage()).
			setAutoCancel(true).
			setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS).
			setSmallIcon(R.mipmap.ic_launcher).
			setNumber(pushMessage.getCurrentUnreadMessgesCounter()).
			setStyle(new Notification.InboxStyle()

					.addLine(pushMessage.getFrom())
					.addLine(pushMessage.getBrandId())
					.addLine(pushMessage.getConversationId())
					.addLine(pushMessage.getBackendService())
					.addLine(pushMessage.getCollapseKey())
					.addLine("Unread messages : " + LivePerson.getNumUnreadMessages(pushMessage.getBrandId()))

			);

		if (Build.VERSION.SDK_INT >= 21) {
            builder = builder.
                    setCategory(Notification.CATEGORY_MESSAGE).
                    setPriority(Notification.PRIORITY_HIGH);
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
                .setContentIntent(getPendingIntent(ctx))
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


    private static PendingIntent getPendingIntent(Context ctx) {
        Intent showIntent = new Intent(ctx, MessagingActivity.class);
        showIntent.putExtra(NOTIFICATION_EXTRA, true);

		return PendingIntent.getActivity(ctx, 0, showIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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
        }
    }
}
