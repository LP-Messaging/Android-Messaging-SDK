package com.liveperson.messagingtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.liveperson.api.LivePersonCallback;
import com.liveperson.messaging.TaskType;
import com.liveperson.messaging.sdk.bootstrap.LivePerson;
import com.liveperson.messagingtest.account.AccountStorage;
import com.liveperson.messagingtest.account.UserProfileStorage;
import com.liveperson.messagingtest.pusher.RegistrationIntentService;

public class MessagingTestActivity extends AppCompatActivity {

    private static final String TAG = CustomActivity.class.getSimpleName();
    private EditText mAccountTextView;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mPhoneNumberView;
    private CheckBox mIdpCheckBox;

    private TextView mSdkVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        initAccount();
        initOpenConversationButton();
        initStartFragmentButton();
    }

    private void initPusher() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
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
        mSdkVersion = (TextView)findViewById(R.id.sdk_version);
        mSdkVersion.setText(sdkVersion);
    }

    private void prepare() {
        saveAccount();
        LivePerson.setCallback(new LivePersonCallback() {
            @Override
            public void onCustomGuiTapped() {

            }

            @Override
            public void onInAppMessageReceived(String agentName, String brandId, String message) {

            }

            @Override
            public void onInitProcessFailed(InitFailedReasons error) {

            }

            @Override
            public void onServerConnectionError() {

            }

            @Override
            public void onTokenRequestError() {

            }

            @Override
            public void logEvent(String log) {

            }

            @Override
            public void onSdkVersionNotCompatible() {

            }

            @Override
            public void onError(TaskType type, String message) {
                Toast.makeText(MessagingTestActivity.this, type.name() + " problem ", Toast.LENGTH_LONG);
            }
        });
        // you can't register pusher before initialization
        initPusher();
    }

    private void saveAccount() {
        // Get brand ID from UI
        String account = mAccountTextView.getText().toString().trim();
        String firstName = mFirstNameView.getText().toString().trim();
        String lastName = mLastNameView.getText().toString().trim();
        String phoneNumber = mPhoneNumberView.getText().toString().trim();

        LivePerson.initialize(this, account);
        AccountStorage.getInstance(this).setAccount(account);
        UserProfileStorage.getInstance(this).setFirstName(firstName);
        UserProfileStorage.getInstance(this).setLastName(lastName);
        UserProfileStorage.getInstance(this).setPhoneNumber(phoneNumber);
    }

    private void initOpenConversationButton() {
        findViewById(R.id.button_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepare();
                if(mIdpCheckBox.isChecked()){
                    LivePerson.showConversation(MessagingTestActivity.this, AccountStorage.getInstance(MessagingTestActivity.this).getAccount());
                }else{
                    LivePerson.showConversation(MessagingTestActivity.this);
                }
                LivePerson.setUserProfile(AccountStorage.SDK_SAMPLE_APP_ID, mFirstNameView.getText().toString(), mLastNameView.getText().toString(), mPhoneNumberView.getText().toString());
            }
        });
    }

    private void initStartFragmentButton() {
        findViewById(R.id.button_start_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepare();
                Intent in = new Intent(MessagingTestActivity.this, CustomActivity.class);
                in.putExtra(CustomActivity.ACCOUNT_ID, AccountStorage.getInstance(MessagingTestActivity.this).getAccount());
                startActivity(in);
                LivePerson.setUserProfile(AccountStorage.SDK_SAMPLE_APP_ID, mFirstNameView.getText().toString(), mLastNameView.getText().toString(), mPhoneNumberView.getText().toString());
            }
        });
    }
}
