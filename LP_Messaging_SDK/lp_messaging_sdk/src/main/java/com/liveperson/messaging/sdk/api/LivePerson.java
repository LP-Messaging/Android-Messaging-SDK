package com.liveperson.messaging.sdk.api;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.liveperson.api.LivePersonCallback;
import com.liveperson.infra.ConversationViewParams;
import com.liveperson.infra.ICallback;
import com.liveperson.infra.InitLivePersonProperties;
import com.liveperson.infra.LPAuthenticationParams;
import com.liveperson.infra.LivePersonConfiguration;
import com.liveperson.infra.callbacks.InitLivePersonCallBack;
import com.liveperson.infra.callbacks.LogoutLivePersonCallBack;
import com.liveperson.infra.log.LPMobileLog;
import com.liveperson.infra.messaging_ui.MessagingUIFactory;
import com.liveperson.infra.messaging_ui.MessagingUiConfiguration;
import com.liveperson.infra.messaging_ui.MessagingUiInitData;
import com.liveperson.infra.messaging_ui.configuration.UIConfigurationKeys;
import com.liveperson.infra.messaging_ui.notification.NotificationController;
import com.liveperson.infra.messaging_ui.uicomponents.PushMessageParser;
import com.liveperson.infra.model.PushMessage;
import com.liveperson.infra.sdkstatemachine.shutdown.ShutDownCompletionListener;
import com.liveperson.messaging.MessagingFactory;
import com.liveperson.messaging.model.AgentData;
import com.liveperson.messaging.model.UserProfileBundle;
import com.liveperson.messaging.sdk.BuildConfig;
import com.liveperson.messaging.sdk.R;
import com.liveperson.messaging.sdk.api.callbacks.LogoutLivePersonCallback;
import com.liveperson.messaging.sdk.api.callbacks.ShutDownLivePersonCallback;
import com.liveperson.messaging.sdk.api.model.ConsumerProfile;

import java.util.Map;

/**
 * LivePerson Messaging SDK entry point.
 * <p/>
 * You must initialize this class before use. The simplest way is to just do
 * {#code LivePerson.initialize(Context, String)}.
 */
public class LivePerson {

    private static final String TAG = LivePerson.class.getSimpleName();
    public static final String ACTION_LP_UPDATE_NUM_UNREAD_MESSAGES_ACTION = NotificationController.ACTION_LP_UPDATE_NUM_UNREAD_MESSAGES_ACTION;
    public static final String ACTION_LP_UPDATE_NUM_UNREAD_MESSAGES_EXTRA = NotificationController.ACTION_LP_UPDATE_NUM_UNREAD_MESSAGES_EXTRA;

    private static String mBrandId;
    static LivePersonConfiguration mLivePersonConfiguration;

    private LivePerson() {
    }


    /**
     * Initialize the framework
     *
     * @param context Application or activity context
     * @deprecated - need to have app id in order to enable some features.
     * use {@link #initialize(Context, InitLivePersonProperties)} instead
     */
    @Deprecated
    public static void initialize(final Context context, final String brandId, final InitLivePersonCallBack initCallBack) {
        initialize(context, new InitLivePersonProperties(brandId, context.getApplicationInfo().packageName, initCallBack));
    }

    /**
     * Initialize the framework
     *
     * @param context Application or activity context
     */
    public static void initialize(Context context, final InitLivePersonProperties initProperties) {
        //check if initProperties contains all the mandatory params.
        if (!InitLivePersonProperties.isValid(initProperties)) {
            if (initProperties != null && initProperties.getInitCallBack() != null) {
                initProperties.getInitCallBack().onInitFailed(new Exception("InitLivePersonProperties not valid or missing parameters."));
            }
            LPMobileLog.w(TAG, "Invalid InitLivePersonProperties!");
            return;
        }
        context = context.getApplicationContext();
        //try to initialized
        if (!isValidState()) {
            mBrandId = initProperties.getBrandId();
            setLogDebugMode(context);
            UIConfigurationKeys.setDefaultConfiguration(context);
            mLivePersonConfiguration = new LivePersonConfiguration(null);
            MessagingUIFactory.getInstance().init(context, new MessagingUiInitData(initProperties, getSDKVersion()), new MessagingUiConfiguration(null));
        } else {
            initProperties.getInitCallBack().onInitSucceed();
            MessagingUIFactory.getInstance().setConfiguration(new MessagingUiConfiguration(null));
        }
    }

    private static void setLogDebugMode(Context context) {
        boolean isDebuggable = (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
        LPMobileLog.setDebugMode(isDebuggable);
    }

    /**
     * Show the conversation screen
     *
     * @param activity
     * @return
     */
    @Deprecated
    public static boolean showConversation(Activity activity) {
        return showConversation(activity, new LPAuthenticationParams(), new ConversationViewParams(false));
    }

    /**
     * Show the conversation screen
     *
     * @param activity
     * @param authenticationKey
     * @return
     */
    @Deprecated
    public static boolean showConversation(Activity activity, String authenticationKey) {
        return showConversation(activity, new LPAuthenticationParams().setAuthKey(authenticationKey), new ConversationViewParams(false));
    }

    /**
     * Show the conversation screen
     */
    public static boolean showConversation(Activity activity, LPAuthenticationParams lpAuthenticationParams, ConversationViewParams params‎) {
        return isValidState() && MessagingUIFactory.getInstance().showConversation(activity, mBrandId, lpAuthenticationParams, params‎);
    }

    /**
     * Hide the conversation screen
     *
     * @param activity
     */
    public static void hideConversation(Activity activity) {
        if (!isValidState()) {
            return;
        }
        MessagingUIFactory.getInstance().hideConversation(activity);
    }

    /**
     * Get the conversation fragment only
     */
    @Deprecated
    public static Fragment getConversationFragment() {
        return getConversationFragment(new LPAuthenticationParams(), new ConversationViewParams(false));
    }


    /**
     * Get the conversation fragment only
     *
     * @param authKey
     * @return
     */
    @Deprecated
    public static Fragment getConversationFragment(String authKey) {
        return getConversationFragment(new LPAuthenticationParams().setAuthKey(authKey), new ConversationViewParams(false));
    }

    /**
     * Get the conversation fragment only
     *
     * @return
     */
    public static Fragment getConversationFragment(LPAuthenticationParams lpAuthenticationParams, ConversationViewParams params‎) {
        if (!isValidState()) {
            LPMobileLog.e(TAG, "getConversationFragment- not initialized! mBrandId = " + mBrandId);
            return null;
        }
        return MessagingUIFactory.getInstance().getConversationFragment(mBrandId, lpAuthenticationParams, params‎);
    }

    /**
     * Reconnect with new authentication key
     *
     * @param authKey the authentication key to connect with
     */
    @Deprecated
    public static void reconnect(String authKey) {

        if (!isValidState()) {
            return;
        }

        MessagingFactory.getInstance().getController().reconnect(mBrandId, new LPAuthenticationParams().setAuthKey(authKey));
    }

    /**
     * Reconnect with new authentication key / JWT
     *
     * @param lpAuthenticationParams the authentication params to connect with
     */
    public static void reconnect(LPAuthenticationParams lpAuthenticationParams) {

        if (!isValidState()) {
            return;
        }

        MessagingFactory.getInstance().getController().reconnect(mBrandId, lpAuthenticationParams);
    }

    /**
     * Register LivePerson pusher service
     *
     * @param brandId
     * @param gcmToken
     */
    public static void registerLPPusher(String brandId, String appId, String gcmToken) {
        if (!isValidState()) {
            return;
        }
        MessagingFactory.getInstance().getController().registerPusher(brandId, appId, gcmToken);
    }

    /**
     * Unregister LivePerson pusher service for the given brandId
     *
     * @param brandId
     * @param appId
     */
    public static void unregisterLPPusher(String brandId, String appId) {
        if (!isValidState()) {
            return;
        }
        MessagingFactory.getInstance().getController().unregisterPusher(brandId, appId, null, false);
    }

    /**
     * Get the LivePerson Messaging SDK version
     *
     * @return
     */
    public static String getSDKVersion() {
        return BuildConfig.VERSION_NAME;
    }


    /**
     * Handle the push notification
     *
     * @param data
     */
    @Deprecated
    public static void handlePush(Context context, Bundle data, String brandId, boolean showNotification) {

        if (TextUtils.isEmpty(brandId)) {
            LPMobileLog.e(TAG, "No Brand! ignoring push message?");
            return;
        }

        PushMessage message = PushMessageParser.parseBundle(brandId, data);

        if (message != null) {
            NotificationController.instance.addMessageAndDisplayNotification(context, brandId, message, showNotification, R.drawable.liveperson_icon);
        }
    }


    /***
     * Since the AMS is sending the message with the name of the agent we need to remove it and leave the message only
     * @param context
     * @param remoteMessage
     * @param brandId
     * @param showNotification
     * @return push message object - can be nullable - if its not our push message or we had error parsing it.
     */
    @Nullable
    public static PushMessage handlePushMessage(Context context, Map<String, String> remoteMessage, String brandId, boolean showNotification) {

        if (TextUtils.isEmpty(brandId)) {
            LPMobileLog.e(TAG, "No Brand! ignoring push message?");
            return null;
        }

        //Parse the bundle in case it's related to LivePerson messages
        PushMessage message = PushMessageParser.parse(brandId, remoteMessage);

        if (message != null) {
            NotificationController.instance.addMessageAndDisplayNotification(context, brandId, message, showNotification, R.drawable.liveperson_icon);
        }

        return message;
    }

    /**
     * @param brandId
     * @return
     * @deprecated
     */
    public static int getNumUnreadMessages(String brandId) {
        if (TextUtils.isEmpty(brandId)) {
            LPMobileLog.e(TAG, "No Brand! returning -1");
            return -1;
        }
        return NotificationController.instance.getNumUnreadMessages(brandId);
    }


    /**
     * Getting the number of unread messages
     * Note: SDK needs to be initialized in order to call this API
     *
     * @param callback
     * @return
     */
    public static void getNumUnreadMessages(String appId, final ICallback<Integer, Exception> callback) {
        if (!isValidState()) {
            callback.onError(new Exception("SDK not initialized"));
        } else {
            MessagingFactory.getInstance().getController().getUnreadMessagesCount(mBrandId, appId, callback);
        }
    }


    /**
     * Set a callback to be called when needed
     *
     * @param listener
     */
    public static void setCallback(final LivePersonCallback listener) {
        if (!isValidState()) {
            return;
        }
        MessagingFactory.getInstance().getController().setCallback(listener);
    }

    /**
     * Remove the callback
     */
    public static void removeCallBack() {
        if (!isValidState()) {
            return;
        }
        MessagingFactory.getInstance().getController().removeCallback();
    }

    /**
     * Set user profile for this session
     *
     * @deprecated The setUserProfile method with appId parameter is deprecated. Please use {@link #setUserProfile(ConsumerProfile)}}
     */
    @Deprecated
    public static void setUserProfile(String appId, String firstName, String lastName, String phone) {
        if (!isValidState()) {
            return;
        }
        ConsumerProfile profile = new ConsumerProfile.Builder()
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPhoneNumber(phone)
                .build();

        setUserProfile(profile);
    }


    /**
     * Set the consumer's profile for this session
     *
     * @param profile {@link ConsumerProfile}
     */
    public static void setUserProfile(ConsumerProfile profile) {
        if (!isValidState()) {
            return;
        }
        UserProfileBundle userProfile = new UserProfileBundle.Builder()
                .setFirstName(profile.getFirstName())
                .setLastName(profile.getLastName())
                .setPhoneNumber(profile.getPhoneNumber())
                .setNickname(profile.getNickname())
                .setAvatarUrl(profile.getAvatarUrl())
                .build();

        MessagingFactory.getInstance().getController().sendUserProfile(mBrandId, userProfile);
    }

    /**
     * Checks whether there is an active (unresolved) conversation and returns a boolean accordingly
     *
     * @return
     */
    public static void checkActiveConversation(final ICallback<Boolean, Exception> callback) {
        if (!isValidState()) {
            callback.onError(new Exception("SDK not initialized"));
        } else {
            MessagingFactory.getInstance().getController().checkActiveConversation(mBrandId, callback);
        }
    }

    /**
     * Checks whether there is an active (unresolved) conversation and returns a if it marked as urgent
     *
     * @return
     */
    public static void checkConversationIsMarkedAsUrgent(final ICallback<Boolean, Exception> callback) {
        if (!isValidState()) {
            callback.onError(new Exception("SDK not initialized"));
        } else {
            MessagingFactory.getInstance().getController().checkConversationIsMarkedAsUrgent(mBrandId, callback);
        }
    }

    /**
     * return the agent data(first name, last name, email, avatarURL) in case the user tapped on Agent's Image
     *
     * @param callback
     */
    public static void checkAgentID(final ICallback<AgentData, Exception> callback) {
        if (!isValidState()) {
            callback.onError(new Exception("SDK not initialized"));
        } else {
            MessagingFactory.getInstance().getController().checkAgentID(mBrandId, callback);
        }
    }


    public static void markConversationAsUrgent() {
        if (!isValidState()) {
            return;
        }
        MessagingFactory.getInstance().getController().markConversationAsUrgent(mBrandId, mBrandId);
    }

    public static void markConversationAsNormal() {
        if (!isValidState()) {
            return;
        }
        MessagingFactory.getInstance().getController().markConversationAsNormal(mBrandId, mBrandId);
    }


    public static void resolveConversation() {
        if (!isValidState()) {
            return;
        }
        MessagingFactory.getInstance().getController().resolveConversation(mBrandId, mBrandId);
    }

    private static boolean isValidState() {

        boolean initialized = MessagingUIFactory.getInstance().isInitialized();
        boolean isEmpty = TextUtils.isEmpty(mBrandId);
        if (initialized && isEmpty) {
            mBrandId = MessagingUIFactory.getInstance().getMessagingUi().getInitData().getBrandId();
        }
        LPMobileLog.d(TAG, "init = " + initialized + " mBrandId = " + mBrandId);
        return initialized && !TextUtils.isEmpty(mBrandId);
    }

    /**
     * Clear all messages and conversations for the current brand.
     * This method will clear only if there is no open conversation active.
     *
     * @return <code>true</code> if messages cleared, <code>false</code> if messages were not cleared (due to open conversation, or no current brand)
     */
    public static boolean clearHistory() {
        return isValidState() && MessagingFactory.getInstance().getController().clearHistory(mBrandId);
    }

    /**
     * Close LivePerson Messaging SDK
     * Uninitialized SDK without cleaning data.
     * This does not handle the screen. To close the Activity call @hideConversation BEFORE shutdown
     */
    public static void shutDown(final ShutDownLivePersonCallback shutdownCallback) {

        if (!isValidState()) {
            return;
        }

        MessagingUIFactory.getInstance().shutDown(new ShutDownCompletionListener() {

            @Override
            public void shutDownCompleted() {
                if (shutdownCallback != null) {
                    shutdownCallback.onShutdownSucceed();
                }

                reset();
            }

            @Override
            public void shutDownFailed() {

                if (shutdownCallback != null) {
                    shutdownCallback.onShutdownFailed();
                }
            }
        });
    }

    /**
     * Close LivePerson Messaging SDK
     * Uninitialized SDK without cleaning data.
     * This does not handle the screen.
     * To close the Activity call @hideConversation BEFORE shutdown
     * To close the fragment - remove it from its activity's container BEFORE shutdown.
     *
     * @deprecated This does not provide any indication whether the shutdown was succeeded.
     * Please use shutDown(ShutDownLivePersonCallback)
     */
    @Deprecated
    public static void shutDown() {

        shutDown(null);
    }

    private static void reset() {
        mBrandId = null;
    }


    /**
     * Clear LivePerson Messaging SDK data and unregistering push.
     * Will fail
     * This does not handle the screen. To close the Activity call @hideConversation BEFORE logout
     */
    public static void logOut(final Context context, final String brandId, final String appId, final LogoutLivePersonCallback logoutCallback) {

        mBrandId = brandId;
        //Handler to call the host app with the callback on the same thread.
        final Handler logoutHandler = new Handler();
        InitLivePersonProperties initProperties = new InitLivePersonProperties(brandId, appId, null);
        MessagingUiInitData ui = new MessagingUiInitData(initProperties, getSDKVersion());
        MessagingUIFactory.getInstance().logout(context, ui, new LogoutLivePersonCallBack() {
            @Override
            public void onLogoutSucceed() {
                logoutHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        logoutCallback.onLogoutSucceed();
                    }
                });
            }

            @Override
            public void onLogoutFailed(final Exception e) {
                logoutHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        logoutCallback.onLogoutFailed();
                    }
                });
            }
        });
    }

    /**
     * Set a PendingIntent to be used on the image foreground service notification.
     * Note: the foreground service will be used only if the <i>upload_photo_using_service</i> configuration is set to true
     *
     * @param pendingIntent
     */
    public static void setImageServicePendingIntent(PendingIntent pendingIntent) {
        if (pendingIntent != null) {
            MessagingFactory.getInstance().getController().setImageServicePendingIntent(pendingIntent);
        }
    }

    /**
     * Set a notification builder that represents the ongoing notification for the image upload foreground service. It is assumed that a pending intent
     * is added to the given notification builder.
     * Note: the foreground service will be used only if the <i>upload_photo_using_service</i> configuration is set to true
     *
     * @param builder - the Notification.Builder for the ongoing notification
     */
    public static void setImageServiceUploadNotificationBuilder(Notification.Builder builder) {
        if (builder != null) {
            MessagingFactory.getInstance().getController().setImageForegroundServiceUploadNotificationBuilder(builder);
        }
    }

    /**
     * Set a notification builder that represents the ongoing notification for the image download foreground service. It is assumed that a pending intent
     * is added to the given notification builder.
     * Note: the foreground service will be used only if the <i>upload_photo_using_service</i> configuration is set to true
     *
     * @param builder - the Notification.Builder for the ongoing notification
     */
    public static void setImageServiceDownloadNotificationBuilder(Notification.Builder builder) {
        if (builder != null) {
            MessagingFactory.getInstance().getController().setImageForegroundServiceDownloadNotificationBuilder(builder);
        }
    }

}
