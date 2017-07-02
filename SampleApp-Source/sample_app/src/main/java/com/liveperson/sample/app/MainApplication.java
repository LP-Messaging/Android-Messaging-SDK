package com.liveperson.sample.app;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.liveperson.api.LivePersonCallbackImpl;
import com.liveperson.api.LivePersonIntents;
import com.liveperson.api.ams.cm.types.CloseReason;
import com.liveperson.api.sdk.LPConversationData;
import com.liveperson.messaging.TaskType;
import com.liveperson.messaging.model.AgentData;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.sample.app.Utils.SampleAppStorage;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by shiranr on 4/9/17.
 */
public class MainApplication extends Application {


    private static final String TAG = MainApplication.class.getSimpleName();
    public static MainApplication Instance;
    private LivePersonCallbackImpl livePersonCallback;
    private BroadcastReceiver mLivePersonReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        Instance = this;
        registerToLivePersonEvents();
    }

    public static MainApplication getInstance() {
        return Instance;
    }

    public void registerToLivePersonEvents(){
        createLivePersonReceiver();
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mLivePersonReceiver, LivePersonIntents.getIntentFilterForAllEvents());
    }

    private void createLivePersonReceiver() {
        mLivePersonReceiver = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d(TAG, "Got LP intent event with action " + intent.getAction());
                switch (intent.getAction()){
                    case LivePersonIntents.ILivePersonIntentAction.LP_ON_AGENT_AVATAR_TAPPED_INTENT_ACTION:
                        onAgentAvatarTapped(LivePersonIntents.getAgentData(intent));
                        break;

                    case LivePersonIntents.ILivePersonIntentAction.LP_ON_AGENT_DETAILS_CHANGED_INTENT_ACTION:
                        AgentData agentData = LivePersonIntents.getAgentData(intent);
                        onAgentDetailsChanged(agentData);
                        break;

                    case LivePersonIntents.ILivePersonIntentAction.LP_ON_AGENT_TYPING_INTENT_ACTION:
                        boolean isTyping = LivePersonIntents.getAgentTypingValue(intent);
                        onAgentTyping(isTyping);
                        break;

                    case LivePersonIntents.ILivePersonIntentAction.LP_ON_CONNECTION_CHANGED_INTENT_ACTION:
                        boolean isConnected = LivePersonIntents.getConnectedValue(intent);
                        onConnectionChanged(isConnected);
                        break;

                    case LivePersonIntents.ILivePersonIntentAction.LP_ON_CONVERSATION_MARKED_AS_NORMAL_INTENT_ACTION:
                        onConversationMarkedAsNormal();
                        break;

                    case LivePersonIntents.ILivePersonIntentAction.LP_ON_CONVERSATION_MARKED_AS_URGENT_INTENT_ACTION:
                        onConversationMarkedAsUrgent();
                        break;

                    case LivePersonIntents.ILivePersonIntentAction.LP_ON_CONVERSATION_RESOLVED_INTENT_ACTION:
                        LPConversationData lpConversationData = LivePersonIntents.getLPConversationData(intent);
                        onConversationResolved(lpConversationData);
                        break;

                    case LivePersonIntents.ILivePersonIntentAction.LP_ON_CONVERSATION_STARTED_INTENT_ACTION:
                        LPConversationData lpConversationData1 = LivePersonIntents.getLPConversationData(intent);
                        onConversationStarted(lpConversationData1);
                        break;

                    case LivePersonIntents.ILivePersonIntentAction.LP_ON_CSAT_DISMISSED_INTENT_ACTION:
                        onCsatDismissed();
                        break;

                    case LivePersonIntents.ILivePersonIntentAction.LP_ON_CSAT_SUBMITTED_INTENT_ACTION:
                        String conversationId = LivePersonIntents.getConversationID(intent);
                        onCsatSubmitted(conversationId);
                        break;

                    case LivePersonIntents.ILivePersonIntentAction.LP_ON_ERROR_INTENT_ACTION:
                        TaskType type = LivePersonIntents.getOnErrorTaskType(intent);
                        String message = LivePersonIntents.getOnErrorMessage(intent);
                        onError(type, message);
                        break;

                    case LivePersonIntents.ILivePersonIntentAction.LP_ON_OFFLINE_HOURS_CHANGES_INTENT_ACTION:
                        onOfflineHoursChanges(LivePersonIntents.getOfflineHoursOn(intent));
                        break;

                    case LivePersonIntents.ILivePersonIntentAction.LP_ON_TOKEN_EXPIRED_INTENT_ACTION:
                        onTokenExpired();
                        break;
                }

            }
        };
    }

    public void registerToLivePersonCallbacks(){
        createLivePersonCallback();
        LivePerson.setCallback(livePersonCallback);
    }

    private void createLivePersonCallback() {
        if (livePersonCallback != null){
            return;
        }
        livePersonCallback = new LivePersonCallbackImpl() {
            @Override
            public void onError(TaskType type, String message) {
                MainApplication.this.onError(type, message);
            }

            @Override
            public void onTokenExpired() {
                MainApplication.this.onTokenExpired();
            }

            @Override
            public void onConversationStarted(LPConversationData convData) {
                MainApplication.this.onConversationStarted(convData);
            }

            @Override
            public void onConversationResolved(LPConversationData convData) {
                MainApplication.this.onConversationResolved(convData);
            }

            @Override
            public void onConversationResolved(CloseReason reason) {
                /*Toast.makeText(getApplicationContext(), "onConversationResolved", Toast.LENGTH_LONG).show();*/
            }

            @Override
            public void onConnectionChanged(boolean isConnected) {
                MainApplication.this.onConnectionChanged(isConnected);
            }

            @Override
            public void onAgentTyping(boolean isTyping) {
                MainApplication.this.onAgentTyping(isTyping);
            }

            @Override
            public void onAgentDetailsChanged(AgentData agentData) {
                MainApplication.this.onAgentDetailsChanged(agentData);
            }

            @Override
            public void onCsatDismissed() {
                MainApplication.this.onCsatDismissed();
            }

            @Override
            public void onCsatSubmitted(String conversationId) {
                MainApplication.this.onCsatSubmitted(conversationId);
            }

            @Override
            public void onConversationMarkedAsUrgent() {
                MainApplication.this.onConversationMarkedAsUrgent();
            }

            @Override
            public void onConversationMarkedAsNormal() {
                MainApplication.this.onConversationMarkedAsNormal();
            }

            @Override
            public void onOfflineHoursChanges(boolean isOfflineHoursOn) {
                MainApplication.this.onOfflineHoursChanges(isOfflineHoursOn);
            }

            @Override
            public void onAgentAvatarTapped(AgentData agentData) {
                MainApplication.this.onAgentAvatarTapped(agentData);

            }
        };
    }

    private void onAgentAvatarTapped(AgentData agentData) {
        Toast.makeText(getApplicationContext(), "on Agent Avatar Tapped - " + agentData.mFirstName + " " + agentData.mLastName, Toast.LENGTH_SHORT).show();
    }

    private void onOfflineHoursChanges(boolean isOfflineHoursOn) {
        Toast.makeText(getApplicationContext(), "on Offline Hours Changes - " + isOfflineHoursOn, Toast.LENGTH_LONG).show();
    }

    private void onConversationMarkedAsNormal() {
        Toast.makeText(getApplicationContext(), "Conversation Marked As Normal", Toast.LENGTH_LONG).show();
    }

    private void onConversationMarkedAsUrgent() {
        Toast.makeText(getApplicationContext(), "Conversation Marked As Urgent", Toast.LENGTH_LONG).show();
    }

    private void onCsatSubmitted(String conversationId) {
        Toast.makeText(getApplicationContext(), "on CSAT Submitted. ConversationID = " + conversationId, Toast.LENGTH_LONG).show();
    }

    private void onCsatDismissed() {
        Toast.makeText(getApplicationContext(), "on CSAT Dismissed", Toast.LENGTH_LONG).show();
    }

    private void onAgentDetailsChanged(AgentData agentData) {
        Toast.makeText(getApplicationContext(), "Agent Details Changed " + agentData, Toast.LENGTH_LONG).show();
    }

    private void onAgentTyping(boolean isTyping) {
        Toast.makeText(getApplicationContext(), "isTyping " + isTyping, Toast.LENGTH_LONG).show();
    }

    private void onConnectionChanged(boolean isConnected) {
        Toast.makeText(getApplicationContext(), "onConnectionChanged " + isConnected, Toast.LENGTH_LONG).show();
    }

    private void onConversationResolved(LPConversationData convData) {
        Toast.makeText(getApplicationContext(), "Conversation resolved " + convData.getId()
                + " reason " + convData.getCloseReason(), Toast.LENGTH_LONG).show();
    }

    private void onConversationStarted(LPConversationData convData) {
        Toast.makeText(getApplicationContext(), "Conversation started " + convData.getId()
                + " reason " + convData.getCloseReason(), Toast.LENGTH_LONG).show();
    }

    private void onTokenExpired() {
        Toast.makeText(getApplicationContext(), "onTokenExpired ", Toast.LENGTH_LONG).show();

        // Change authentication key here:
        LivePerson.reconnect(SampleAppStorage.getInstance(getApplicationContext()).getAuthCode());
    }

    private void onError(TaskType type, String message) {
        Toast.makeText(getApplicationContext(), " problem " + type.name(), Toast.LENGTH_LONG).show();
    }
}
