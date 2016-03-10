package com.liveperson.sample.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.liveperson.api.LivePersonCallback;
import com.liveperson.infra.InitLivePersonCallBack;
import com.liveperson.infra.InternetConnectionService;
import com.liveperson.infra.LocalBroadcastReceiver;
import com.liveperson.messaging.TaskType;
import com.liveperson.messaging.model.AgentData;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.sample.app.account.AccountStorage;
import com.liveperson.sample.app.account.UserProfileStorage;
import com.liveperson.sample.app.pusher.RegistrationIntentService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText mAccountTextView;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mPhoneNumberView;
    private CheckBox mIdpCheckBox;
    private TextView mSdkVersion;
    private LocalBroadcastReceiver mLocalBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initAccount();
        initOpenConversationButton();
        initStartFragmentButton();
    }

    private void initAccount() {
        // Set the default account in the view
        mAccountTextView = (EditText) findViewById(R.id.brand_id);
        mAccountTextView.setText(AccountStorage.getInstance(this).getAccount());

        mFirstNameView = (EditText) findViewById(R.id.first_name);
        mFirstNameView.setText(UserProfileStorage.getInstance(this).getFirstName());

        mLastNameView = (EditText) findViewById(R.id.last_name);
        mLastNameView.setText(UserProfileStorage.getInstance(this).getLastName());

        mPhoneNumberView = (EditText) findViewById(R.id.phone_number);
        mPhoneNumberView.setText(UserProfileStorage.getInstance(this).getPhoneNumber());

        mIdpCheckBox = (CheckBox) findViewById(R.id.idp_checkbox);

        String sdkVersion = String.format("SDK version %1$s ", LivePerson.getSDKVersion());
        mSdkVersion = (TextView) findViewById(R.id.sdk_version);
        mSdkVersion.setText(sdkVersion);
    }

    private void prepare(InitLivePersonCallBack callBack) {
        saveAccount(callBack);
        LivePerson.setCallback(new LivePersonCallback() {
            @Override
            public void onCustomGuiTapped() {

            }

            @Override
            public void onError(TaskType type, String message) {
                Toast.makeText(MainActivity.this, type.name() + " problem ", Toast.LENGTH_LONG).show();

			}

            @Override
            public void onTokenExpired(String brandId) {
                Toast.makeText(MainActivity.this, "onTokenExpired brand " + brandId, Toast.LENGTH_LONG).show();

				// Change authentication key here
				LivePerson.reconnect(AccountStorage.getInstance(MainActivity.this).getAccount());
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
                Toast.makeText(MainActivity.this, "Agent Details Changed "+ agentData, Toast.LENGTH_LONG).show();
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
        });
        // you can't register pusher before initialization
        handleGCMRegistration();
        registerConnection();
    }

    private void saveAccount(InitLivePersonCallBack callBack) {
        // Get brand ID from UI
        String account = mAccountTextView.getText().toString().trim();
        String firstName = mFirstNameView.getText().toString().trim();
        String lastName = mLastNameView.getText().toString().trim();
        String phoneNumber = mPhoneNumberView.getText().toString().trim();

        AccountStorage.getInstance(this).setAccount(account);
        UserProfileStorage.getInstance(this).setFirstName(firstName);
        UserProfileStorage.getInstance(this).setLastName(lastName);
        UserProfileStorage.getInstance(this).setPhoneNumber(phoneNumber);

        LivePerson.initialize(this, account, callBack);

    }

    private void initOpenConversationButton() {
        findViewById(R.id.button_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepare(new InitLivePersonCallBack() {
                    @Override
                    public void onInitSucceed() {
                        openActivity();
                    }

                    @Override
                    public void onInitFailed(Exception e) {
                        Toast.makeText(MainActivity.this,"Init Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            private void openActivity() {
                if (mIdpCheckBox.isChecked()) {
					// Change authentication key here
                    LivePerson.showConversation(MainActivity.this, AccountStorage.getInstance(MainActivity.this).getAccount());
                } else {
                    LivePerson.showConversation(MainActivity.this);
                }
                LivePerson.setUserProfile(AccountStorage.SDK_SAMPLE_APP_ID, mFirstNameView.getText().toString(), mLastNameView.getText().toString(), mPhoneNumberView.getText().toString());
            }
        });
    }

    private void initStartFragmentButton() {
        findViewById(R.id.button_start_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepare(new InitLivePersonCallBack() {
                    @Override
                    public void onInitSucceed() {
                        openFragment();
                    }

                    @Override
                    public void onInitFailed(Exception e) {
                        Toast.makeText(MainActivity.this, "Init Failed", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            private void openFragment() {
                Intent in = new Intent(MainActivity.this, CustomActivity.class);
                if(mIdpCheckBox.isChecked()){
                    in.putExtra(CustomActivity.IS_AUTH, true);
                }else{
                    in.putExtra(CustomActivity.IS_AUTH, false);
                }
                startActivity(in);
                LivePerson.setUserProfile(AccountStorage.SDK_SAMPLE_APP_ID, mFirstNameView.getText().toString(), mLastNameView.getText().toString(), mPhoneNumberView.getText().toString());
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


    private void handleGCMRegistration(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isTokenSent = sharedPreferences.getBoolean(RegistrationIntentService.SENT_TOKEN_TO_SERVER, false);
        if(!isTokenSent){
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }
}
