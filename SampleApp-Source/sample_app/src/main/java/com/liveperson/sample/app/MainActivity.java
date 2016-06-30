package com.liveperson.sample.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.liveperson.api.LivePersonCallback;
import com.liveperson.infra.InitLivePersonCallBack;
import com.liveperson.messaging.TaskType;
import com.liveperson.messaging.model.AgentData;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.sample.app.account.AccountStorage;
import com.liveperson.sample.app.account.UserProfileStorage;
import com.liveperson.sample.app.push.NotificationUI;
import com.liveperson.sample.app.push.RegistrationIntentService;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText mAccountTextView;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mPhoneNumberView;
    private EditText mAuthCodeView;
    private TextView mSdkVersion;
    private Switch mSwitchMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        initViews();
        initOpenConversationButton();
        initStartFragmentButton();

        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.i(TAG, "onNewIntent");
        //Check if this activity was called from a push message
        if (intent != null && intent.getBooleanExtra(NotificationUI.PUSH_NOTIFICATION, false)) {
            boolean isFragmentMode = AccountStorage.getInstance(this).getIsFragmentMode();
            if (isFragmentMode) {
                openConversationFragment();
            } else {
                openConversationActivity();
            }
        }
    }

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

        mSwitchMode = (Switch) findViewById(R.id.switchMode);
        mSwitchMode.setChecked(AccountStorage.getInstance(this).getIsFragmentMode());

        String sdkVersion = String.format("SDK version %1$s ", LivePerson.getSDKVersion());
        mSdkVersion = (TextView) findViewById(R.id.sdk_version);
        mSdkVersion.setText(sdkVersion);
    }

    private void setCallBack() {
        LivePerson.setCallback(new LivePersonCallback() {
            @Override
            public void onConnectionChanged(boolean isConnected) {
                Toast.makeText(MainActivity.this, "onConnectionChanged : " + isConnected, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(TaskType type, String message) {
                Toast.makeText(MainActivity.this, type.name() + " problem ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTokenExpired() {
                Log.i(TAG, "onTokenExpired");
                Toast.makeText(MainActivity.this, "onTokenExpired", Toast.LENGTH_LONG).show();

                String authKey = UserProfileStorage.getInstance(MainActivity.this).getAuthCode();

                Log.i(TAG, "Calling reconnect with key " + authKey);
                //Reconnect with new authentication key
                LivePerson.reconnect(authKey);
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
                Toast.makeText(MainActivity.this, "isOfflineHoursOn " + isOfflineHoursOn, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateAccount() {
        String account = mAccountTextView.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            Toast.makeText(this, "No account!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void saveAccountAndUserSettings() {
        // Get brand ID from UI
        String account = mAccountTextView.getText().toString().trim();
        String firstName = mFirstNameView.getText().toString().trim();
        String lastName = mLastNameView.getText().toString().trim();
        String phoneNumber = mPhoneNumberView.getText().toString().trim();
        String authCode = mAuthCodeView.getText().toString().trim();

        AccountStorage.getInstance(this).setAccount(account);
        AccountStorage.getInstance(this).setIsFragmentMode(mSwitchMode.isChecked());
        UserProfileStorage.getInstance(this).setFirstName(firstName);
        UserProfileStorage.getInstance(this).setLastName(lastName);
        UserProfileStorage.getInstance(this).setPhoneNumber(phoneNumber);
        UserProfileStorage.getInstance(this).setAuthCode(authCode);
    }

    private void initOpenConversationButton() {
        Button openActivity = (Button) findViewById(R.id.button_start);
        if (openActivity != null) {

            openActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!validateAccount()) {
                        return;
                    }
                    saveAccountAndUserSettings();
                    openConversationActivity();
                }
            });
        }
    }

    private void initStartFragmentButton() {
        Button openFragment = (Button) findViewById(R.id.button_start_fragment);
        if (openFragment != null) {
            openFragment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!validateAccount()) {
                        return;
                    }

                    saveAccountAndUserSettings();
                    Log.i(TAG, "initStartFragmentButton");
                    openConversationFragment();
                }
            });
        }
    }


    private void openConversationFragment() {
        Intent in = new Intent(MainActivity.this, FragmentContainerActivity.class);
        startActivity(in);
    }

    private void openConversationActivity() {
        LivePerson.initialize(MainActivity.this, AccountStorage.getInstance(MainActivity.this).getAccount(), new InitLivePersonCallBack() {
            @Override
            public void onInitSucceed() {
                Log.i(TAG, "onInitSucceed");
                setCallBack();
                handleGCMRegistration();
                openActivity();
            }

            @Override
            public void onInitFailed(Exception e) {
                Toast.makeText(MainActivity.this, "Init Failed", Toast.LENGTH_SHORT).show();
            }

            private void openActivity() {
                String auth = UserProfileStorage.getInstance(MainActivity.this).getAuthCode();
                if (!TextUtils.isEmpty(auth)) {
                    LivePerson.showConversation(MainActivity.this, auth);
                } else {
                    LivePerson.showConversation(MainActivity.this);
                }
                LivePerson.setUserProfile(AccountStorage.SDK_SAMPLE_APP_ID, mFirstNameView.getText().toString(), mLastNameView.getText().toString(), mPhoneNumberView.getText().toString());
            }
        });
    }

    private void handleGCMRegistration() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
