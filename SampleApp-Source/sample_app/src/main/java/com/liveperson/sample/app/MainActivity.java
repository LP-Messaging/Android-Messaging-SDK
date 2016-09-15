package com.liveperson.sample.app;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.liveperson.api.LivePersonCallback;
import com.liveperson.infra.InitLivePersonProperties;
import com.liveperson.infra.callbacks.InitLivePersonCallBack;
import com.liveperson.messaging.TaskType;
import com.liveperson.messaging.model.AgentData;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.sample.app.Utils.SampleAppUtils;
import com.liveperson.sample.app.Utils.SampleAppStorage;
import com.liveperson.sample.app.push.NotificationUI;


/**
 * ***** Sample app class - Not related to Messaging SDK ****
 *
 * The main activity of the sample app
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText mAccountTextView;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mPhoneNumberView;
    private EditText mAuthCodeView;
    private TextView mSdkVersion;
    private Button mOpenConversationButton;
    private Button mOpenFragmentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSampleAppViews();
        initOpenConversationButton();
        initStartFragmentButton();

        handlePush(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handlePush(intent);
    }


    /**
     * Init Views
     */
    private void initSampleAppViews() {
        // Set the default account in the view
        mAccountTextView = (EditText) findViewById(R.id.brand_id);
        mAccountTextView.setText(SampleAppStorage.getInstance(this).getAccount());

        mFirstNameView = (EditText) findViewById(R.id.first_name);
        mFirstNameView.setText(SampleAppStorage.getInstance(this).getFirstName());

        mLastNameView = (EditText) findViewById(R.id.last_name);
        mLastNameView.setText(SampleAppStorage.getInstance(this).getLastName());

        mPhoneNumberView = (EditText) findViewById(R.id.phone_number);
        mPhoneNumberView.setText(SampleAppStorage.getInstance(this).getPhoneNumber());

        mAuthCodeView = (EditText) findViewById(R.id.auth_code);
        mAuthCodeView.setText(SampleAppStorage.getInstance(this).getAuthCode());

        String sdkVersion = String.format("SDK version %1$s ", LivePerson.getSDKVersion());
        mSdkVersion = (TextView) findViewById(R.id.sdk_version);
        mSdkVersion.setText(sdkVersion);
    }

    private void setCallBack() {
        LivePerson.setCallback(new LivePersonCallback() {
            @Override
            public void onError(TaskType type, String message) {
                Toast.makeText(MainActivity.this, " problem " + type.name(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTokenExpired() {
                Toast.makeText(MainActivity.this, "onTokenExpired ", Toast.LENGTH_LONG).show();

                // Change authentication key here
                LivePerson.reconnect(SampleAppStorage.getInstance(MainActivity.this).getAuthCode());
            }

            @Override
            public void onConversationStarted() {
                Toast.makeText(MainActivity.this, "onConversationStarted", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConversationResolved() {
                Toast.makeText(MainActivity.this, "onConversationResolved", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectionChanged(boolean isConnected) {
                Toast.makeText(MainActivity.this, "onConnectionChanged " + isConnected, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAgentTyping(boolean isTyping) {
                Toast.makeText(MainActivity.this, "isTyping " + isTyping, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAgentDetailsChanged(AgentData agentData) {
                Toast.makeText(MainActivity.this, "Agent Details Changed " + agentData, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCsatDismissed() {
                Toast.makeText(MainActivity.this, "on CSAT Dismissed", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCsatSubmitted(String conversationId) {
                Toast.makeText(MainActivity.this, "on CSAT Submitted. ConversationID = " + conversationId, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConversationMarkedAsUrgent() {
                Toast.makeText(MainActivity.this, "Conversation Marked As Urgent", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConversationMarkedAsNormal() {
                Toast.makeText(MainActivity.this, "Conversation Marked As Normal", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onOfflineHoursChanges(boolean isOfflineHoursOn) {
                Toast.makeText(MainActivity.this, "on Offline Hours Changes - " + isOfflineHoursOn, Toast.LENGTH_LONG).show();
            }
        });
    }



    /**
     * Save the user input such as: account, first name, last name, phone number and auth code
     */
    private void saveAccountAndUserSettings() {
        String account = mAccountTextView.getText().toString().trim();
        String firstName = mFirstNameView.getText().toString().trim();
        String lastName = mLastNameView.getText().toString().trim();
        String phoneNumber = mPhoneNumberView.getText().toString().trim();
        String authCode = mAuthCodeView.getText().toString().trim();
        SampleAppStorage.getInstance(this).setAccount(account);
        SampleAppStorage.getInstance(this).setFirstName(firstName);
        SampleAppStorage.getInstance(this).setLastName(lastName);
        SampleAppStorage.getInstance(this).setPhoneNumber(phoneNumber);
        SampleAppStorage.getInstance(this).setAuthCode(authCode);
    }

    /**
     * Set the listener on the "open_conversation" button (Activity mode)
     */
    private void initOpenConversationButton() {
        mOpenConversationButton = (Button) findViewById(R.id.button_start_activity);
        mOpenConversationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sample app setting - used to initialize the SDK with "Activity mode" when entering from push notification
                SampleAppStorage.getInstance(MainActivity.this).setSDKMode(SampleAppStorage.SDKMode.ACTIVITY);
                if (SampleAppUtils.isAccountEmpty(mAccountTextView, MainActivity.this)) {
                    return;
                }
                SampleAppUtils.disableButtonAndChangeText(mOpenConversationButton, getString(R.string.initializing));
                saveAccountAndUserSettings();
                initActivityConversation();
            }
        });
    }

    /**
     * Set the listener on the "Open Fragment" button (Fragment mode)
     */
    private void initStartFragmentButton() {
        mOpenFragmentButton = (Button) findViewById(R.id.button_start_fragment);
        mOpenFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sample app setting - used to initialize the SDK with "Fragment mode" when entering from push notification
                SampleAppStorage.getInstance(MainActivity.this).setSDKMode(SampleAppStorage.SDKMode.FRAGMENT);
                if (SampleAppUtils.isAccountEmpty(mAccountTextView, MainActivity.this)) {
                    return;
                }
                saveAccountAndUserSettings();
                openFragmentContainer();
            }
        });
    }

    /**
     * Initialize the Messaging SDK and start the SDK in "Activity Mode"
     */
    private void initActivityConversation() {

        LivePerson.initialize(MainActivity.this, new InitLivePersonProperties(SampleAppStorage.getInstance(MainActivity.this).getAccount(), SampleAppStorage.SDK_SAMPLE_APP_ID, new InitLivePersonCallBack() {
            @Override
            public void onInitSucceed() {
                Log.i(TAG, "SDK initialize completed with Activity mode");
                setCallBack();
                // you can't register pusher before initialization
                SampleAppUtils.handleGCMRegistration(MainActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openActivity();
                        SampleAppUtils.enableButtonAndChangeText(mOpenConversationButton, getString(R.string.open_conversation));
                    }
                });
            }

            @Override
            public void onInitFailed(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SampleAppUtils.enableButtonAndChangeText(mOpenConversationButton, getString(R.string.open_conversation));
                        Toast.makeText(MainActivity.this, "Init Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }));
    }

    /**
     * Start {@link FragmentContainerActivity} that handles the SDK the Messaging SDK and start the SDK in "Fragment Mode"
     */
    private void openFragmentContainer() {
        Intent in = new Intent(MainActivity.this, FragmentContainerActivity.class);
        startActivity(in);
    }

    /**
     * Calling to "showConversation" API
     */
    private void openActivity() {
        String authCode = SampleAppStorage.getInstance(MainActivity.this).getAuthCode();
        if (TextUtils.isEmpty(authCode)) {
            LivePerson.showConversation(MainActivity.this);
        } else {
            LivePerson.showConversation(MainActivity.this, authCode);
        }
        LivePerson.setUserProfile(SampleAppStorage.SDK_SAMPLE_APP_ID, mFirstNameView.getText().toString(), mLastNameView.getText().toString(), mPhoneNumberView.getText().toString());
    }

    /**
     * If we initiated from a push message we show the screen that was in use the previous session (fragment/activity)
     * Activity mode is the default
     * @param intent
     */
    private void handlePush(Intent intent) {
        boolean isFromPush = intent.getBooleanExtra(NotificationUI.PUSH_NOTIFICATION, false);

        //Check if we came from Push Notification
        if (isFromPush) {
            clearPushNotifications();
            switch (SampleAppStorage.getInstance(this).getSDKMode()){
                //Initialize the SDK with "Activity mode"
                case ACTIVITY:
                    initActivityConversation();
                    break;
                //Initialize the SDK with "Fragment mode"
                case FRAGMENT:
                    openFragmentContainer();
                    break;
            }
        }
    }

    /**
     * Hide any shown notification
     */
    private void clearPushNotifications() {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NotificationUI.NOTIFICATION_ID);
    }
}
