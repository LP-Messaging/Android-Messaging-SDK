package com.liveperson.sample.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.liveperson.infra.ICallback;
import com.liveperson.infra.InitLivePersonCallBack;
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
public class CustomActivity extends AppCompatActivity {

    private static final String TAG = CustomActivity.class.getSimpleName();

	public static final String CHECK_ACTIVE_CONVERSATION = "checkActiveConversation";
	public static final String CHECK_CONVERSATION_IS_MARKED_AS_URGENT = "checkConversationIsMarkedAsUrgent";
	public static final String CHECK_AGENT_ID = "checkAgentID";
	public static final String MARK_CONVERSATION_AS_URGENT = "markConversationAsUrgent";
	public static final String MARK_CONVERSATION_AS_NORMAL = "markConversationAsNormal";
	public static final String RESOLVE_CONVERSATION = "resolveConversation";

	public static final String IS_AUTH = "IS_AUTH";
    private static final String MY_CUSTOM_FRAGMENT = "MyCustomFragment";
    private static final boolean TEST_PUSH_APP_ID_CHANGES = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

		Toolbar toolbar = (Toolbar) findViewById(R.id.dev_toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

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
                handleGCMRegistration(AccountStorage.SDK_SAMPLE_APP_ID);
            }

            @Override
            public void onInitFailed(Exception e) {
                Log.e(TAG, "onInitFailed : " + e.getMessage());
            }
        });
        //For test use only!
        Button changeAppID = ((Button)findViewById(R.id.change_app_id));
        if (changeAppID != null){
            if (TEST_PUSH_APP_ID_CHANGES){
                changeAppID.setOnClickListener(new View.OnClickListener() {
                    boolean alt = false;
                    @Override
                    public void onClick(View v) {
                        alt = !alt;
                        if (alt) {
                            handleGCMRegistration(AccountStorage.SDK_SAMPLE_APP_ID_ALT);
                        }else{
                            handleGCMRegistration(AccountStorage.SDK_SAMPLE_APP_ID);
                        }
                    }
                });
            }else{
                changeAppID.setVisibility(View.GONE);
            }
        }

		initSpinner();
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
					switch (selectedItem){
						case
								CHECK_ACTIVE_CONVERSATION:
							LivePerson.checkActiveConversation(new ICallback<Boolean, Exception>() {
								@Override
								public void onSuccess(Boolean value) {
									Toast.makeText(CustomActivity.this, value + "", Toast.LENGTH_LONG).show();
								}

								@Override
								public void onError(Exception exception) {
									Toast.makeText(CustomActivity.this, "Error! " + exception.getMessage(), Toast.LENGTH_LONG).show();
								}
							});
							break;
						case
								CHECK_CONVERSATION_IS_MARKED_AS_URGENT:
							LivePerson.checkConversationIsMarkedAsUrgent(new ICallback<Boolean, Exception>() {
								@Override
								public void onSuccess(Boolean value) {
									Toast.makeText(CustomActivity.this, value + "", Toast.LENGTH_LONG).show();
								}

								@Override
								public void onError(Exception exception) {
									Toast.makeText(CustomActivity.this, "Error! " + exception.getMessage(), Toast.LENGTH_LONG).show();
								}
							});
							break;
						case
								CHECK_AGENT_ID:
							LivePerson.checkAgentID(new ICallback<AgentData, Exception>() {
								@Override
								public void onSuccess(AgentData value) {
									Toast.makeText(CustomActivity.this, value!= null ? value.toString() : " No data!" , Toast.LENGTH_LONG).show();
								}

								@Override
								public void onError(Exception exception) {
									Toast.makeText(CustomActivity.this, "Error! " + exception.getMessage() , Toast.LENGTH_LONG).show();
								}
							});
							break;
						case
								MARK_CONVERSATION_AS_URGENT:
							LivePerson.markConversationAsUrgent();
							break;
						case
								MARK_CONVERSATION_AS_NORMAL:
							LivePerson.markConversationAsNormal();
							break;
						case
								RESOLVE_CONVERSATION:
							LivePerson.resolveConversation();
							break;
					}


				}
			});
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

    private void handleGCMRegistration(String appID) {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        intent.putExtra(RegistrationIntentService.EXTRA_APP_ID, appID);
        startService(intent);
    }
}
