package com.liveperson.sample.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.liveperson.api.LivePersonCallback;
import com.liveperson.infra.ICallback;
import com.liveperson.infra.InitLivePersonCallBack;
import com.liveperson.messaging.TaskType;
import com.liveperson.messaging.model.AgentData;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.sample.app.account.AccountStorage;
import com.liveperson.sample.app.account.UserProfileStorage;
import com.liveperson.sample.app.push.RegistrationIntentService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shiranr on 11/11/2015.
 */
public class FragmentContainerActivity extends AppCompatActivity {

    private static final String TAG = FragmentContainerActivity.class.getSimpleName();
    private static final String LIVEPERSON_FRAGMENT = "liveperson_fragment";

    public static final String CHECK_ACTIVE_CONVERSATION = "checkActiveConversation";
    public static final String CHECK_CONVERSATION_IS_MARKED_AS_URGENT = "checkConversationIsMarkedAsUrgent";
    public static final String CHECK_AGENT_ID = "checkAgentID";
    public static final String MARK_CONVERSATION_AS_URGENT = "markConversationAsUrgent";
    public static final String MARK_CONVERSATION_AS_NORMAL = "markConversationAsNormal";
    public static final String RESOLVE_CONVERSATION = "resolveConversation";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);
        Log.i(TAG, "onCreate");

        Toolbar toolbar = (Toolbar) findViewById(R.id.dev_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        LivePerson.initialize(this, AccountStorage.getInstance(this).getAccount(), new InitLivePersonCallBack() {

            @Override
            public void onInitSucceed() {
                Log.i(TAG, "onInitSucceed");
                initFragment();
                setCallBack();

                String firstName = UserProfileStorage.getInstance(FragmentContainerActivity.this).getFirstName();
                String lastName = UserProfileStorage.getInstance(FragmentContainerActivity.this).getLastName();
                String phoneNumber = UserProfileStorage.getInstance(FragmentContainerActivity.this).getPhoneNumber();

                LivePerson.setUserProfile(AccountStorage.SDK_SAMPLE_APP_ID, firstName, lastName, phoneNumber);
                handleGCMRegistration(AccountStorage.SDK_SAMPLE_APP_ID);
            }

            @Override
            public void onInitFailed(Exception e) {
                Log.e(TAG, "onInitFailed : " + e.getMessage());
            }
        });
        initSpinner();
    }

    private void initFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(LIVEPERSON_FRAGMENT);
        if (fragment == null) {
            String authCode = UserProfileStorage.getInstance(FragmentContainerActivity.this).getAuthCode();
            if (!TextUtils.isEmpty(authCode)) {
                fragment = LivePerson.getConversationFragment(authCode);
            } else {
                fragment = LivePerson.getConversationFragment();
            }
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.custom_fragment_container, fragment, LIVEPERSON_FRAGMENT).commit();
        }
    }

    private void initSpinner() {
        final List<String> list = new ArrayList<String>();
        list.add("Choose Action...");
        list.add(CHECK_CONVERSATION_IS_MARKED_AS_URGENT);
        list.add(CHECK_AGENT_ID);
        list.add(MARK_CONVERSATION_AS_URGENT);
        list.add(MARK_CONVERSATION_AS_NORMAL);
        list.add(RESOLVE_CONVERSATION);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner_dev);
        if (spinner == null) {
            return;
        }
        spinner.setAdapter(dataAdapter);

        ImageView spinnerButton = (ImageView) findViewById(R.id.spinner_button);
        if (spinnerButton != null) {
            spinnerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String selectedItem = (String) spinner.getSelectedItem();
                    Log.d(TAG, selectedItem + " button pressed");
                    switch (selectedItem) {
                        case CHECK_ACTIVE_CONVERSATION:
                            LivePerson.checkActiveConversation(new ICallback<Boolean, Exception>() {
                                @Override
                                public void onSuccess(Boolean value) {
                                    Toast.makeText(FragmentContainerActivity.this, value + "", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onError(Exception exception) {
                                    Toast.makeText(FragmentContainerActivity.this, "Error! " + exception.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                            break;
                        case CHECK_CONVERSATION_IS_MARKED_AS_URGENT:
                            LivePerson.checkConversationIsMarkedAsUrgent(new ICallback<Boolean, Exception>() {
                                @Override
                                public void onSuccess(Boolean value) {
                                    Toast.makeText(FragmentContainerActivity.this, value + "", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onError(Exception exception) {
                                    Toast.makeText(FragmentContainerActivity.this, "Error! " + exception.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                            break;
                        case CHECK_AGENT_ID:
                            LivePerson.checkAgentID(new ICallback<AgentData, Exception>() {
                                @Override
                                public void onSuccess(AgentData value) {
                                    Toast.makeText(FragmentContainerActivity.this, value != null ? value.toString() : " No data!", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onError(Exception exception) {
                                    Toast.makeText(FragmentContainerActivity.this, "Error! " + exception.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                            break;
                        case MARK_CONVERSATION_AS_URGENT:
                            LivePerson.markConversationAsUrgent();
                            break;
                        case MARK_CONVERSATION_AS_NORMAL:
                            LivePerson.markConversationAsNormal();
                            break;
                        case RESOLVE_CONVERSATION:
                            LivePerson.resolveConversation();
                            break;
                    }


                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        LivePerson.shutDown();
        super.onDestroy();
    }

    private void handleGCMRegistration(String appID) {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        intent.putExtra(RegistrationIntentService.EXTRA_APP_ID, appID);
        startService(intent);
    }

    private void setCallBack() {
        LivePerson.setCallback(new LivePersonCallback() {
            @Override
            public void onConnectionChanged(boolean isConnected) {
                Toast.makeText(FragmentContainerActivity.this, "onConnectionChanged : " + isConnected, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(TaskType type, String message) {
                Toast.makeText(FragmentContainerActivity.this, type.name() + " problem ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTokenExpired() {
                Log.i(TAG, "onTokenExpired");
                Toast.makeText(FragmentContainerActivity.this, "onTokenExpired", Toast.LENGTH_LONG).show();
                String authKey = UserProfileStorage.getInstance(FragmentContainerActivity.this).getAuthCode();

                Log.i(TAG, "Calling reconnect with key " + authKey);
                //Reconnect with new authentication key
                LivePerson.reconnect(authKey);
            }

            @Override
            public void onConversationStarted() {
                Toast.makeText(FragmentContainerActivity.this, "onConversationStarted", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConversationResolved() {
                Toast.makeText(FragmentContainerActivity.this, "onConversationResolved", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAgentDetailsChanged(AgentData agentData) {
                Toast.makeText(FragmentContainerActivity.this, "Agent Details Changed " + agentData, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCsatDismissed() {
                Toast.makeText(FragmentContainerActivity.this, "on Csat Dismissed", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConversationMarkedAsUrgent() {
                Toast.makeText(FragmentContainerActivity.this, "Conversation Marked As Urgent", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConversationMarkedAsNormal() {
                Toast.makeText(FragmentContainerActivity.this, "Conversation Marked As Normal", Toast.LENGTH_LONG).show();
            }

			@Override
			public void onOfflineHoursChanges(boolean isOfflineHoursOn) {
				Toast.makeText(FragmentContainerActivity.this, "Offline hours: " + (isOfflineHoursOn ? "On" : "Off"), Toast.LENGTH_LONG).show();
			}

		});
    }
}
