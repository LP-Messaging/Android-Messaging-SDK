package com.liveperson.sample.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.liveperson.infra.InitLivePersonCallBack;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.sample.app.account.AccountStorage;
import com.liveperson.sample.app.account.UserProfileStorage;
import com.liveperson.sample.app.push.RegistrationIntentService;

/**
 * Created by shiranr on 11/11/2015.
 */
public class CustomActivity extends AppCompatActivity {

    private static final String TAG = CustomActivity.class.getSimpleName();
    public static final String IS_AUTH = "IS_AUTH";
    private static final String MY_CUSTOM_FRAGMENT = "MyCustomFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        Log.i(TAG, "onCreate");
        LivePerson.initialize(this, AccountStorage.getInstance(this).getAccount(), new InitLivePersonCallBack() {

            @Override
            public void onInitSucceed() {
                Log.e(TAG, "onInitSucceed");
                initFragment();

                String firstName = UserProfileStorage.getInstance(CustomActivity.this).getFirstName();
                String lastName = UserProfileStorage.getInstance(CustomActivity.this).getLastName();
                String phoneNumber = UserProfileStorage.getInstance(CustomActivity.this).getPhoneNumber();

                LivePerson.setUserProfile(AccountStorage.SDK_SAMPLE_APP_ID, firstName, lastName, phoneNumber);
                handleGCMRegistration();
            }

            @Override
            public void onInitFailed(Exception e) {
                Log.e(TAG, "onInitFailed : " + e.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    private void initFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(MY_CUSTOM_FRAGMENT);
        if (fragment == null) {
            Bundle chatBundle = getIntent().getExtras();
            if (chatBundle != null) {
                boolean is_auth = getIntent().getBooleanExtra(IS_AUTH, false);
                if(is_auth){
                    String authCode = UserProfileStorage.getInstance(CustomActivity.this).getAuthCode();
                    fragment = LivePerson.getConversationFragment(authCode);
                }else{
                    fragment = LivePerson.getConversationFragment();
                }
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.custom_fragment_container, fragment, MY_CUSTOM_FRAGMENT).commit();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag(TAG1));
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        LivePerson.shutDown();
        super.onDestroy();
    }

    private void handleGCMRegistration() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
