package com.liveperson.sample.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.liveperson.infra.InternetConnectionService;
import com.liveperson.infra.LocalBroadcastReceiver;
import com.liveperson.infra.callbacks.InitLivePersonCallBack;
import com.liveperson.messaging.TaskType;
import com.liveperson.messaging.model.AgentData;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.sample.app.account.AccountStorage;
import com.liveperson.sample.app.account.UserProfileStorage;
import com.liveperson.sample.app.push.RegistrationIntentService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText mAccountTextView;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mPhoneNumberView;
    private EditText mAuthCodeView;
    private TextView mSdkVersion;
    private LocalBroadcastReceiver mLocalBroadcastReceiver;
    private Button mOpenConversationButton;
    private Button mOpenFragmentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initOpenConversationButton();
        initStartFragmentButton();
    }

    /**
     * Init Views
     */
    private void initViews() {
        // Set the default account in the view
        mAccountTextView = (EditText) findViewById(R.id.brand_id);
        mAccountTextView.setText(AccountStorage.getInstance(this).getAccount());

        mFirstNameView = (EditText) findViewById(R.id.first_name);
        mFirstNameView.setText(UserProfileStorage.getInstance(this).getFirstName());

        mLastNameView = (EditText) findViewById(R.id.last_name);
        mLastNameView.setText(UserProfileStorage.getInstance(this).getLastName());

        mPhoneNumberView = (EditText) findViewById(R.id.phone_number);
        mPhoneNumberView.setText(UserProfileStorage.getInstance(this).getPhoneNumber());

        mAuthCodeView = (EditText) findViewById(R.id.auth_code);
        mAuthCodeView.setText(UserProfileStorage.getInstance(this).getAuthCode());

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
                LivePerson.reconnect(UserProfileStorage.getInstance(MainActivity.this).getAuthCode());
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
                Toast.makeText(MainActivity.this, "on Csat Dismissed", Toast.LENGTH_LONG).show();
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
                Toast.makeText(MainActivity.this, "on Offline Hours Changes - " + isOfflineHoursOn , Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Validate that the account field is not empty
     * @return
     */
    private boolean validateAccount() {
        String account = mAccountTextView.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            Toast.makeText(this, "No account!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
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
        AccountStorage.getInstance(this).setAccount(account);
        UserProfileStorage.getInstance(this).setFirstName(firstName);
        UserProfileStorage.getInstance(this).setLastName(lastName);
        UserProfileStorage.getInstance(this).setPhoneNumber(phoneNumber);
        UserProfileStorage.getInstance(this).setAuthCode(authCode);
    }

    /**
     * Set the listener on the open conversation button
     */
    private void initOpenConversationButton() {
        mOpenConversationButton = (Button) findViewById(R.id.button_start);
        mOpenConversationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateAccount()) {
                    return;
                }
                mOpenConversationButton.setText(R.string.initializing);
                mOpenConversationButton.setEnabled(false);

                saveAccountAndUserSettings();
                LivePerson.initialize(MainActivity.this,new InitLivePersonProperties(AccountStorage.getInstance(MainActivity.this).getAccount(), AccountStorage.SDK_SAMPLE_APP_ID , new InitLivePersonCallBack() {
                    @Override
                    public void onInitSucceed() {
                        Log.i(TAG, "onInitSucceed");
                        setCallBack();
                        // you can't register pusher before initialization
                        handleGCMRegistration();
                        registerConnection();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                openActivity();
                                restoreButton();
                            }
                        });
                    }

                    @Override
                    public void onInitFailed(Exception e) {
                        restoreButton();
                        Toast.makeText(MainActivity.this, "Init Failed", Toast.LENGTH_SHORT).show();
                    }
                }));
            }

            private void restoreButton() {
                mOpenConversationButton.setText(R.string.open_conversation);
                mOpenConversationButton.setEnabled(true);
            }

            private void openActivity() {
                String authCode = UserProfileStorage.getInstance(MainActivity.this).getAuthCode();
                if (TextUtils.isEmpty(authCode)) {
                    LivePerson.showConversation(MainActivity.this);
                }else{
                    LivePerson.showConversation(MainActivity.this, authCode);
                }
                LivePerson.setUserProfile(AccountStorage.SDK_SAMPLE_APP_ID, mFirstNameView.getText().toString(), mLastNameView.getText().toString(), mPhoneNumberView.getText().toString());
            }
        });
    }

    /**
     * Set the listener on the open fragment button
     */
    private void initStartFragmentButton() {
        mOpenFragmentButton = (Button) findViewById(R.id.button_start_fragment);
        mOpenFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateAccount()) {
                    return;
                }
                saveAccountAndUserSettings();
                setCallBack();
                handleGCMRegistration();
                openFragmentContainer();
            }

            private void openFragmentContainer() {
                Intent in = new Intent(MainActivity.this, FragmentContainerActivity.class);
                startActivity(in);
            }
        });
    }


    private void registerConnection() {
        mLocalBroadcastReceiver = new LocalBroadcastReceiver.Builder()
                .addAction(InternetConnectionService.BROADCAST_INTERNET_CONNECTION_CONNECTED)
                .build(new LocalBroadcastReceiver.IOnReceive() {
                    @Override
                    public void onBroadcastReceived(Context context, Intent intent) {
                        handleGCMRegistration();
                    }
                });
    }


    private void handleGCMRegistration() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isTokenSent = sharedPreferences.getBoolean(RegistrationIntentService.SENT_TOKEN_TO_SERVER, false);
        if (!isTokenSent) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }
}
