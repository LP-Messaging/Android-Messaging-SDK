package com.liveperson.sample.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.sample.app.account.AccountStorage;


/**
 * Created by shiranr on 11/11/2015.
 */
public class CustomActivity extends AppCompatActivity {

    private static final String TAG = CustomActivity.class.getSimpleName();
    public static final String IS_AUTH = "is_auth";
    private static final String MY_CUSTOM_FRAGMENT = "MyCustomFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        initFragment();
    }

    private void initFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(MY_CUSTOM_FRAGMENT);
        if (fragment == null) {
            Bundle chatBundle = getIntent().getExtras();
            if (chatBundle != null) {
                boolean is_auth = getIntent().getBooleanExtra(IS_AUTH, false);
                if(is_auth){
                    fragment = LivePerson.getConversationFragment(AccountStorage.getInstance(CustomActivity.this).getAccount());
                }else{
                    fragment = LivePerson.getConversationFragment();
                }
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.custom_fragment_container, fragment, MY_CUSTOM_FRAGMENT).commit();
            }
        }
    }

}
