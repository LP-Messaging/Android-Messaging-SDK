package com.liveperson.messaging.sdk.api;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.liveperson.api.LivePersonCallback;
import com.liveperson.infra.ICallback;
import com.liveperson.infra.InfraController;
import com.liveperson.infra.database.DataBaseCommand;
import com.liveperson.infra.log.LPMobileLog;
import com.liveperson.infra.messaging_ui.MessagingUiController;
import com.liveperson.infra.model.Notifications;
import com.liveperson.messaging.MessagingController;
import com.liveperson.messaging.model.Conversation;
import com.liveperson.messaging.model.MessagingUserProfile;
import com.liveperson.messaging.model.UserProfileBundle;
import com.liveperson.messaging.sdk.BuildConfig;
import com.liveperson.messaging.model.AgentData;

/**
 * LivePerson Messaging SDK entry point.
 * <p/>
 * You must initialize this class before use. The simplest way is to just do
 * {#code LivePerson.initialize(Context, String)}.
 */
public class LivePerson {

    private static final String TAG = LivePerson.class.getSimpleName();
    private static boolean initialized;
    private static String mBrandId;


    private LivePerson() {
    }


    /**
     * Initialize the framework
     *
     * @param context Application or activity context
     */
    public static void initialize(Context context, String brandId) {
        if (initialized) {
            return;
        }
        initialized = true;
        mBrandId = brandId;
        InfraController.instance.init(context, SdkEntryPointProcess.class);
    }

    public static class SdkEntryPointProcess extends InfraController.EntryPoint {

        @Override
        protected void init() {
            MessagingController.getInstance().init();
            MessagingUiController.getInstance().init(getContext());
        }
    }


    /**
     * Show the conversation screen
     *
     * @param activity
     * @return
     */
    public static boolean showConversation(Activity activity) {
        return showConversation(activity, null);
    }

    /**
     * Show the conversation screen
     *
     * @param activity
     * @param authenticationKey
     * @return
     */
    public static boolean showConversation(Activity activity, String authenticationKey) {
        if (!isValidState()) {
            return false;
        }
        return MessagingUiController.getInstance().showConversation(activity, mBrandId, authenticationKey);
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
        MessagingUiController.getInstance().hideConversation(activity);
    }

    /**
     * Get the conversation fragment only
     */
    public static Fragment getConversationFragment() {
        return getConversationFragment(null);
    }

    /**
     * Get the conversation fragment only
     *
     * @param authKey
     * @return
     */
    public static Fragment getConversationFragment(String authKey) {
        if (!isValidState()) {
            return null;
        }
        return MessagingUiController.getInstance().getConversationFragment(mBrandId, authKey);
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
        MessagingController.getInstance().registerPusher(brandId, appId, gcmToken);
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
        MessagingController.getInstance().unregisterPusher(brandId, appId);
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
    public static void handlePush(Context ctx, Bundle data, String brandId) {

        if (TextUtils.isEmpty(brandId)) {
            LPMobileLog.e(TAG, "No Brand! ignoring push message?");
            return;
        }

        InfraController.instance.init(ctx, SdkEntryPointProcess.class);
        String message = data.getString("message");

        //TODO: Need to init only the push receiver and context

        Notifications.instance.addMessage(brandId, message);
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
        MessagingController.getInstance().setCallback(listener);
    }

    /**
     * Remove the callback
     */
    public static void removeCallBack() {
        if (!isValidState()) {
            return;
        }
        MessagingController.getInstance().removeCallback();
    }

    /**
     * Set user profile for this session
     */
    public static void setUserProfile(String appId, String firstName, String lastName, String phone) {
        if (!isValidState()) {
            return;
        }
        UserProfileBundle userProfileBundle = new UserProfileBundle(firstName, lastName, phone);
        MessagingController.getInstance().sendUserProfile(mBrandId, appId, userProfileBundle);
    }

    /**
     * Checks whether there is an active (unresolved) conversation and returns a boolean accordingly
     *
     * @return
     */
    public static void checkActiveConversation(final ICallback<Boolean, Exception> callback) {
        if (!isValidState()) {
            callback.onError(new Exception("SDK not initialized"));
            return;
        }else {
            MessagingController.getInstance().checkActiveConversation(mBrandId, callback);
        }
    }

    /**
     * return the agent data(first name, last name, email, avatarURL) in case we have an active conversation
     * or null otherwise
     *
     * @param callback
     */
    public static void checkAgentID(final ICallback<AgentData, Exception> callback){
        if (!isValidState()) {
            callback.onError(new Exception("SDK not initialized"));
            return;
        }else {
            MessagingController.getInstance().checkAgentID(mBrandId, callback);
        }
    }

    public static void markConversationAsUrgent() {
        if (!isValidState()) {
            return;
        }
        MessagingController.getInstance().markConversationAsUrgent(mBrandId);
    }

    public static void markConversationAsNormal() {
        if (!isValidState()) {
            return;
        }
        MessagingController.getInstance().markConversationAsNormal(mBrandId);
    }


    public static void resolveConversation() {
        if (!isValidState()) {
            return;
        }
        MessagingController.getInstance().resolveConversation(mBrandId);
    }

    private static boolean isValidState() {
        return initialized && !TextUtils.isEmpty(mBrandId);
    }

    /**
     * Close LivePerson Messaging SDK
     */
    public static void shutDown() {
        //TODO: Need to fully implement
        initialized = false;
        MessagingController.getInstance().shutDown();
        MessagingUiController.getInstance().shutDown();
        InfraController.instance.shutDown();
    }
}
