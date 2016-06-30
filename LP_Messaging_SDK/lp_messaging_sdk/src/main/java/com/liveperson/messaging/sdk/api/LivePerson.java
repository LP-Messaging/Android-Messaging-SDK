package com.liveperson.messaging.sdk.api;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.liveperson.api.LivePersonCallback;
import com.liveperson.infra.ICallback;
import com.liveperson.infra.Infra;
import com.liveperson.infra.InitLivePersonCallBack;
import com.liveperson.infra.log.LPMobileLog;
import com.liveperson.infra.messaging_ui.MessagingUi;
import com.liveperson.infra.messaging_ui.notification.NotificationController;
import com.liveperson.messaging.Messaging;
import com.liveperson.messaging.model.AgentData;
import com.liveperson.messaging.model.UserProfileBundle;
import com.liveperson.messaging.sdk.BuildConfig;

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
    public static void initialize(Context context, String brandId, InitLivePersonCallBack initCallBack) {
        if (initialized) {
            initCallBack.onInitSucceed();
            return;
        }
        initialized = true;
        mBrandId = brandId;
        setLogDebugMode(context);
        Infra.instance.init(context, new SdkEntryPointProcess(), initCallBack);
    }

    public static class SdkEntryPointProcess extends Infra.EntryPoint {

        @Override
        protected void init() {
            Messaging.getInstance().init();
            MessagingUi.getInstance().init(getContext());
        }

        @Override
        protected String getHostVersion() {
            return getSDKVersion();
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
        return MessagingUi.getInstance().showConversation(activity, mBrandId, authenticationKey);
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
        MessagingUi.getInstance().hideConversation(activity);
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
            Log.e(TAG, "getConversationFragment- not initialized! mBrandId = "+ mBrandId);
            return null;
        }
        return MessagingUi.getInstance().getConversationFragment(mBrandId, authKey);
    }

    /**
     * Reconnect with new authentication key
     * @param authKey the authentication key to connect with
     */
    public static void reconnect(String authKey) {

        if (!isValidState()) {
            return;
        }

        Messaging.getInstance().reconnect(mBrandId, authKey);
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
        Messaging.getInstance().registerPusher(brandId, appId, gcmToken);
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
        Messaging.getInstance().unregisterPusher(brandId, appId, null, false);
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
    public static void handlePush(Context context, Bundle data, String brandId, boolean showNotification) {

        if (TextUtils.isEmpty(brandId)) {
            LPMobileLog.e(TAG, "No Brand! ignoring push message?");
            return;
        }

        String message = data.getString("message");

        NotificationController.instance.addMessageAndDisplayNotification(context, brandId, message, showNotification);
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
        Messaging.getInstance().setCallback(listener);
    }

    /**
     * Remove the callback
     */
    public static void removeCallBack() {
        if (!isValidState()) {
            return;
        }
        Messaging.getInstance().removeCallback();
    }

    /**
     * Set user profile for this session
     */
    public static void setUserProfile(String appId, String firstName, String lastName, String phone) {
        if (!isValidState()) {
            return;
        }
        UserProfileBundle userProfileBundle = new UserProfileBundle(firstName, lastName, phone);
        Messaging.getInstance().sendUserProfile(mBrandId, appId, userProfileBundle);
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
            Messaging.getInstance().checkActiveConversation(mBrandId, callback);
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
            return;
        }else {
            Messaging.getInstance().checkConversationIsMarkedAsUrgent(mBrandId, callback);
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
            Messaging.getInstance().checkAgentID(mBrandId, callback);
        }
    }

    public static void markConversationAsUrgent() {
        if (!isValidState()) {
            return;
        }
        Messaging.getInstance().markConversationAsUrgent(mBrandId);
    }

    public static void markConversationAsNormal() {
        if (!isValidState()) {
            return;
        }
        Messaging.getInstance().markConversationAsNormal(mBrandId);
    }


    public static void resolveConversation() {
        if (!isValidState()) {
            return;
        }
        Messaging.getInstance().resolveConversation(mBrandId);
    }

    private static boolean isValidState() {
        return initialized && !TextUtils.isEmpty(mBrandId);
    }

	/**
	 * Clear all messages and conversations for the current brand.
	 * This method will clear only if there is no open conversation active.
	 *
	 * @return <code>true</code> if messages cleared, <code>false</code> if messages were not cleared (due to open conversation, or no current brand)
	 */
	public static boolean clearHistory() {
		if (!isValidState()) {
			return false;
		}
		return Messaging.getInstance().clearHistory(mBrandId);
	}

	/**
     * Close LivePerson Messaging SDK
     * Uninitialized SDK without cleaning data.
     * This does not handle the screen. To close the Activity call @hideConversation BEFORE shutdown
     */
    public static void shutDown() {
        if (!initialized) {
            return;
        }
        Messaging.getInstance().shutDown();
        MessagingUi.getInstance().shutDown();
        Infra.instance.shutDown();
        mBrandId = null;
        initialized = false;
        LPMobileLog.d(TAG, "Finished ShutDown");
    }

    /**
     * Clear LivePerson Messaging SDK data and unregistering push.
     * Will fail
     * This does not handle the screen. To close the Activity call @hideConversation BEFORE logout
     */
    public static void logOut(final Context context, final String brandId, final String appId, final LogoutLivePersonCallback logoutCallback){
        //Handler to call the host app with the callback on the same thread.
        final Handler logoutHandler = new Handler();

        //need to initialized so we can run unregister push.
        initialize(context, brandId, new InitLivePersonCallBack() {

            @Override
            public void onInitSucceed() {
                runUnregisterPushAndClear();
            }

            @Override
            public void onInitFailed(Exception e) {
                notifyLogoutFailed();
            }

            private void runUnregisterPushAndClear() {
                //if we are not connected = unregister will fail.
                Messaging.getInstance().unregisterPusher(brandId, appId, new ICallback<Void, Exception>() {
                    @Override
                    public void onSuccess(Void value) {
                        shutDown();
                        clear();
                        notifyLogoutSucceed();
                    }

                    @Override
                    public void onError(Exception exception) {
                        LPMobileLog.w(TAG, "LivePerson Logout: Error: " + exception.getMessage());
                        notifyLogoutFailed();
                    }
                }, true);
            }

            private void notifyLogoutFailed() {
                if (logoutCallback != null){
                    logoutHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            logoutCallback.onLogoutFailed();
                        }
                    });
                }
            }

            private void notifyLogoutSucceed() {
                if (logoutCallback != null) {
                    logoutHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            logoutCallback.onLogoutSucceed();
                        }
                    });
                }
            }
        });

    }

    private static void clear() {
        NotificationController.instance.clear();
        Messaging.getInstance().clear();
        MessagingUi.getInstance().clear();
        Infra.instance.clear();
    }

}
