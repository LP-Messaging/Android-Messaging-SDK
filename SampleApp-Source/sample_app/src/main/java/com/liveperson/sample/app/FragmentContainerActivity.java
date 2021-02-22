package com.liveperson.sample.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.liveperson.infra.CampaignInfo;
import com.liveperson.infra.ConversationViewParams;
import com.liveperson.infra.InitLivePersonProperties;
import com.liveperson.infra.callbacks.InitLivePersonCallBack;
import com.liveperson.infra.messaging_ui.fragment.ConversationFragment;
import com.liveperson.infra.model.LPWelcomeMessage;
import com.liveperson.infra.model.MessageOption;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.messaging.sdk.api.model.ConsumerProfile;
import com.liveperson.sample.app.notification.NotificationUI;
import com.liveperson.sample.app.utils.SampleAppStorage;
import com.liveperson.sample.app.utils.SampleAppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ***** Sample app class - Not related to Messaging SDK ****
 *
 * Used as an example of how to use SDK "Fragment mode"
 */
public class FragmentContainerActivity extends AppCompatActivity {

    private static final String TAG = FragmentContainerActivity.class.getSimpleName();
    private static final String LIVEPERSON_FRAGMENT = "liveperson_fragment";
    public static final String KEY_READ_ONLY = "read_only";
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

                if (getIntent().getBooleanExtra(NotificationUI.NOTIFICATION_EXTRA, false)) {
                    String messageId = getIntent().getStringExtra(NotificationUI.NOTIFICATION_MESSAGE_ID);
                    LivePerson.setPushNotificationTapped(messageId);
                }

                runOnUiThread(() -> initFragment());
                setCallBack();
                SampleAppUtils.handlePusherRegistration(FragmentContainerActivity.this);
                String firstName = SampleAppStorage.getInstance(FragmentContainerActivity.this).getFirstName();
                String lastName = SampleAppStorage.getInstance(FragmentContainerActivity.this).getLastName();
                String phoneNumber = SampleAppStorage.getInstance(FragmentContainerActivity.this).getPhoneNumber();

				ConsumerProfile consumerProfile = new ConsumerProfile.Builder()
						.setFirstName(firstName)
						.setLastName(lastName)
						.setPhoneNumber(phoneNumber)
						.build();
                LivePerson.setUserProfile(consumerProfile);

                //Constructing the notification builder for the upload/download foreground service and passing it to the SDK.
                Notification.Builder uploadBuilder = NotificationUI.createUploadNotificationBuilder(getApplicationContext());
                Notification.Builder downloadBuilder = NotificationUI.createDownloadNotificationBuilder(getApplicationContext());
                LivePerson.setImageServiceUploadNotificationBuilder(uploadBuilder);
                LivePerson.setImageServiceDownloadNotificationBuilder(downloadBuilder);
            }

            @Override
            public void onInitFailed(Exception e) {
                Log.e(TAG, "onInitFailed : " + e.getMessage());
            }
        }));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.getBooleanExtra(NotificationUI.NOTIFICATION_EXTRA, false)) {
            String messageId = getIntent().getStringExtra(NotificationUI.NOTIFICATION_MESSAGE_ID);
            LivePerson.setPushNotificationTapped(messageId);
        }
    }

    private void initFragment() {
        mConversationFragment = (ConversationFragment) getSupportFragmentManager().findFragmentByTag(LIVEPERSON_FRAGMENT);
        Log.d(TAG, "initFragment. mConversationFragment = "+ mConversationFragment);
        if (mConversationFragment == null) {
			CampaignInfo campaignInfo = SampleAppUtils.getCampaignInfo(this);
            ConversationViewParams params = new ConversationViewParams().setCampaignInfo(campaignInfo).setReadOnlyMode(isReadOnly());
//            setWelcomeMessage(params);  //This method sets the welcome message with quick replies. Uncomment this line to enable this feature.
            mConversationFragment = (ConversationFragment) LivePerson.getConversationFragment(SampleAppUtils.createLPAuthParams(this), params);

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

    @SuppressWarnings("unused")
    private void setWelcomeMessage(ConversationViewParams params) {
        LPWelcomeMessage lpWelcomeMessage = new LPWelcomeMessage("Welcome Message");
        List<MessageOption> optionItems = new ArrayList<>();
        optionItems.add(new MessageOption("bill", "bill"));
        optionItems.add(new MessageOption("sales", "sales"));
        optionItems.add(new MessageOption("support", "support"));
        try {
            lpWelcomeMessage.setMessageOptions(optionItems);
        } catch (Exception e) {
            e.printStackTrace();
        }
        lpWelcomeMessage.setNumberOfItemsPerRow(8);
        lpWelcomeMessage.setMessageFrequency(LPWelcomeMessage.MessageFrequency.EVERY_CONVERSATION);
        params.setLpWelcomeMessage(lpWelcomeMessage);
    }

    private boolean isReadOnly() {
        return getIntent().getBooleanExtra(KEY_READ_ONLY, false);
    }

    private boolean isValidState() {
        return !isFinishing() && !isDestroyed();
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
     */
    public void onLeftPanelUpdate(View v){
        String size = ((EditText)findViewById(R.id.left_panel_size)).getText().toString();
        if (TextUtils.isEmpty(size)){
            return;
        }
        int width = Integer.parseInt(size);
        View layout_panel = findViewById(R.id.left_panel_layout);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout_panel.getLayoutParams();
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, r.getDisplayMetrics());
        params.width = (int) px;
        layout_panel.setLayoutParams(params);
    }

    /**
     * Relevant only for tablet. called from XML
     */
    public void onFooterPanelUpdate(View v){
        String size = ((EditText)findViewById(R.id.footer_panel_size)).getText().toString();
        if (TextUtils.isEmpty(size)){
            return;
        }
        int height = Integer.parseInt(size);
        View layout_panel = findViewById(R.id.footer_panel_layout);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout_panel.getLayoutParams();
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, r.getDisplayMetrics());
        params.height = (int) px;
        layout_panel.setLayoutParams(params);
    }

}
