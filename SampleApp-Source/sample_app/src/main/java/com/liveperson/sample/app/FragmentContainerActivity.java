package com.liveperson.sample.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.liveperson.infra.ConversationViewParams;
import com.liveperson.infra.Infra;
import com.liveperson.infra.InitLivePersonProperties;
import com.liveperson.infra.LPAuthenticationParams;
import com.liveperson.infra.callbacks.InitLivePersonCallBack;
import com.liveperson.infra.messaging_ui.fragment.ConversationFragment;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.messaging.sdk.api.model.ConsumerProfile;
import com.liveperson.sample.app.Utils.SampleAppStorage;
import com.liveperson.sample.app.Utils.SampleAppUtils;

/**
 * ***** Sample app class - Not related to Messaging SDK ****
 *
 * Used as an example of how to use SDK "Fragment mode"
 */
public class FragmentContainerActivity extends AppCompatActivity {

    private static final String TAG = FragmentContainerActivity.class.getSimpleName();
    private static final String LIVEPERSON_FRAGMENT = "liveperson_fragment";
    private ConversationFragment mConversationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        Log.i(TAG, "onCreate savedInstanceState = " + savedInstanceState );

        LivePerson.initialize(getApplicationContext(), new InitLivePersonProperties(SampleAppStorage.getInstance(this).getAccount(), SampleAppStorage.SDK_SAMPLE_FCM_APP_ID, new InitLivePersonCallBack() {

            @Override
            public void onInitSucceed() {
                Log.i(TAG, "onInitSucceed");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initFragment();
                    }
                });
                setCallBack();
                SampleAppUtils.handleGCMRegistration(FragmentContainerActivity.this);
                String firstName = SampleAppStorage.getInstance(FragmentContainerActivity.this).getFirstName();
                String lastName = SampleAppStorage.getInstance(FragmentContainerActivity.this).getLastName();
                String phoneNumber = SampleAppStorage.getInstance(FragmentContainerActivity.this).getPhoneNumber();

				ConsumerProfile consumerProfile = new ConsumerProfile.Builder()
						.setFirstName(firstName)
						.setLastName(lastName)
						.setPhoneNumber(phoneNumber)
						.build();
                LivePerson.setUserProfile(consumerProfile);
            }

            @Override
            public void onInitFailed(Exception e) {
                Log.e(TAG, "onInitFailed : " + e.getMessage());
            }
        }));
    }

    private void initFragment() {
        mConversationFragment = (ConversationFragment) getSupportFragmentManager().findFragmentByTag(LIVEPERSON_FRAGMENT);
        Log.d(TAG, "initFragment. mConversationFragment = "+ mConversationFragment);
        if (mConversationFragment == null) {
            String authCode = SampleAppStorage.getInstance(FragmentContainerActivity.this).getAuthCode();

            Log.d(TAG, "initFragment. authCode = "+ authCode);
            mConversationFragment = (ConversationFragment) LivePerson.getConversationFragment(new LPAuthenticationParams().setAuthKey(authCode), new ConversationViewParams(isReadOnly()));

            if (isValidState()) {

				// Pending intent for image foreground service
				Intent notificationIntent = new Intent(this, FragmentContainerActivity.class);
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
				LivePerson.setImageServicePendingIntent(pendingIntent);

				// Notification builder for image upload foreground service
				Notification.Builder uploadBuilder = 	new Notification.Builder(this.getApplicationContext());
				Notification.Builder downloadBuilder = 	new Notification.Builder(this.getApplicationContext());
				uploadBuilder.setContentTitle("Uploading image")
						.setSmallIcon(android.R.drawable.arrow_up_float)
						.setContentIntent(pendingIntent)
						.setProgress(0, 0, true);

				downloadBuilder.setContentTitle("Downloading image")
						.setSmallIcon(android.R.drawable.arrow_down_float)
						.setContentIntent(pendingIntent)
						.setProgress(0, 0, true);

				LivePerson.setImageServiceUploadNotificationBuilder(uploadBuilder);
				LivePerson.setImageServiceDownloadNotificationBuilder(downloadBuilder);

				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.custom_fragment_container, mConversationFragment, LIVEPERSON_FRAGMENT).commitAllowingStateLoss();
            }
        }else{
             attachFragment();
        }
    }

    private boolean isReadOnly() {
        return getIntent().getBooleanExtra(Infra.KEY_READ_ONLY, false);
    }

    private boolean isValidState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return !isFinishing() && !isDestroyed();
        }else{
            return !isFinishing();
        }
    }

    private void attachFragment() {
        if (mConversationFragment.isDetached()) {
            Log.d(TAG, "initFragment. attaching fragment");
            if (isValidState()){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.attach(mConversationFragment).commitAllowingStateLoss();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mConversationFragment != null){
            attachFragment();
        }
    }

    private void setCallBack() {
        //register via callback, also available to listen via BroadCastReceiver in Main Application
        MainApplication.getInstance().registerToLivePersonCallbacks();
    }


    @Override
    public void onBackPressed() {
        if (mConversationFragment == null || !mConversationFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }


    /**
     * Relevant only for tablet. called from XML
     * @param v
     */
    public void onLeftPanelUpdate(View v){
        String size = ((EditText)findViewById(R.id.left_panel_size)).getText().toString();
        if (TextUtils.isEmpty(size)){
            return;
        }
        int width = Integer.valueOf(size);
        View layout_panel = findViewById(R.id.left_panel_layout);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout_panel.getLayoutParams();
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, r.getDisplayMetrics());
        params.width = (int) px;
        layout_panel.setLayoutParams(params);
    }

    /**
     * Relevant only for tablet. called from XML
     * @param v
     */
    public void onFooterPanelUpdate(View v){
        String size = ((EditText)findViewById(R.id.footer_panel_size)).getText().toString();
        if (TextUtils.isEmpty(size)){
            return;
        }
        int height = Integer.valueOf(size);
        View layout_panel = findViewById(R.id.footer_panel_layout);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout_panel.getLayoutParams();
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, r.getDisplayMetrics());
        params.height = (int) px;
        layout_panel.setLayoutParams(params);
    }

}
