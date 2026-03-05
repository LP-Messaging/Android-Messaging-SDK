package com.liveperson.sample.app.activities;

import static com.liveperson.sample.app.utils.InsetsUtilsKt.applyInsets;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentTransaction;

import com.liveperson.infra.CampaignInfo;
import com.liveperson.infra.ConversationViewParams;
import com.liveperson.infra.ICallback;
import com.liveperson.infra.InitLivePersonProperties;
import com.liveperson.infra.callbacks.InitLivePersonCallBack;
import com.liveperson.infra.messaging_ui.fragment.ConversationFragment;
import com.liveperson.infra.model.LPWelcomeMessage;
import com.liveperson.infra.model.MessageOption;
import com.liveperson.messaging.hybrid.commands.exceptions.HybridSDKException;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.messaging.sdk.api.model.ConsumerProfile;
import com.liveperson.sample.app.MainApplication;
import com.liveperson.sample.app.R;
import com.liveperson.sample.app.dialogs.DynamicWelcomeMessageDialog;
import com.liveperson.sample.app.notification.NotificationUI;
import com.liveperson.sample.app.utils.SampleAppStorage;
import com.liveperson.sample.app.utils.SampleAppUtils;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;

/**
 * ***** Sample app class - Not related to Messaging SDK ****
 *
 * Used as an example of how to use SDK "Fragment mode"
 */
public class FragmentContainerActivity extends BaseActivity {

    private static final String TAG = FragmentContainerActivity.class.getSimpleName();
    private static final String LIVEPERSON_FRAGMENT = "liveperson_fragment";
    public static final String KEY_READ_ONLY = "read_only";
    private ConversationFragment mConversationFragment;

    private static final String KEY_READ_ONLY_MODE = "mode.read_only";
    private static final String TAG_WELCOME_MESSAGE_DIALOG = "dialog.welcome.id";

    private Button mSendRandomMessageButton;
    private Button mOpenCameraButton;
    private Button mOpenGalleryButton;
    private Button mOpenFileChooserButton;
    private SwitchCompat mChangeReadOnlyModeSwitch;

    private boolean mIsReadonlyMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        applyInsets(this);
        if (savedInstanceState == null) {
            mIsReadonlyMode = isReadOnly();
        } else {
            mIsReadonlyMode = savedInstanceState.getBoolean(KEY_READ_ONLY_MODE, false);
        }
        Log.i(TAG, "onCreate savedInstanceState = " + savedInstanceState);
        setupHybridSDKActionButtons();
        LivePerson.initialize(getApplicationContext(), new InitLivePersonProperties(SampleAppStorage.getInstance(this).getAccount(), SampleAppStorage.SDK_SAMPLE_FCM_APP_ID, new InitLivePersonCallBack() {

            @Override
            public void onInitSucceed() {
                Log.i(TAG, "onInitSucceed");

                if (getIntent().getBooleanExtra(NotificationUI.NOTIFICATION_EXTRA, false)) {
                    String messageId = getIntent().getStringExtra(NotificationUI.NOTIFICATION_MESSAGE_ID);
                    LivePerson.setPushNotificationTapped(messageId);
                }
                MainApplication.getInstance().unregisterToLivePersonEvents();
                MainApplication.getInstance().registerToLivePersonCallbacks();
                runOnUiThread(() -> initFragment());

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
                NotificationCompat.Builder uploadBuilder = NotificationUI.createUploadNotificationBuilder(getApplicationContext());
                NotificationCompat.Builder downloadBuilder = NotificationUI.createDownloadNotificationBuilder(getApplicationContext());
                LivePerson.setImageServiceUploadNotificationBuilder(uploadBuilder);
                LivePerson.setImageServiceDownloadNotificationBuilder(downloadBuilder);
            }

            @Override
            public void onInitFailed(Exception e) {
                Log.e(TAG, "onInitFailed : " + e.getMessage());
            }
        }));
    }

    private void setupHybridSDKActionButtons() {
        mOpenCameraButton = findViewById(R.id.button_open_camera);
        if (mOpenCameraButton != null) {
            mOpenCameraButton.setOnClickListener(v -> {
                startCameraFileSharingFlow();
            });
        }
        mOpenGalleryButton = findViewById(R.id.button_open_gallery);
        if (mOpenGalleryButton != null) {
            mOpenGalleryButton.setOnClickListener(v -> {
                startPhotoPickerFileSharingFlow();
            });
        }
        mOpenFileChooserButton = findViewById(R.id.button_open_file_chooser);
        if (mOpenFileChooserButton != null) {
            mOpenFileChooserButton.setOnClickListener(v -> {
                startFileChooserSharingFlow();
            });
        }
        mSendRandomMessageButton = findViewById(R.id.button_send_text);
        mSendRandomMessageButton.setOnClickListener(v -> {
            sendRandomMessage();
        });
        mChangeReadOnlyModeSwitch = findViewById(R.id.view_only_mode_switch);
        if (mChangeReadOnlyModeSwitch != null) {
            mChangeReadOnlyModeSwitch.setChecked(mIsReadonlyMode);
            mChangeReadOnlyModeSwitch.jumpDrawablesToCurrentState();
            mChangeReadOnlyModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                changeReadOnlyModeValue(isChecked);
            });
        }
    }

    private void sendRandomMessage() {
        String message = String.format("Random message %d", System.currentTimeMillis());
        LivePerson.sendTextMessage(message, new HybridSDKCallback(this));
    }

    private void startCameraFileSharingFlow() {
        LivePerson.fileSharingOpenCamera(new HybridSDKCallback(this));
    }

    private void startPhotoPickerFileSharingFlow() {
        LivePerson.fileSharingOpenGallery(new HybridSDKCallback(this));
    }

    private void startFileChooserSharingFlow() {
        LivePerson.fileSharingOpenFile(new HybridSDKCallback(this));
    }

    private void changeReadOnlyModeValue(boolean isReadonlyMode) {
        mIsReadonlyMode = isReadonlyMode;
        LivePerson.changeReadOnlyMode(isReadonlyMode, new HybridSDKCallback(this));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putBoolean(KEY_READ_ONLY_MODE, mIsReadonlyMode);
        }
    }

    @Override
    protected void onDestroy() {
        MainApplication.getInstance().unregisterToLivePersonCallbacks();
        if (mChangeReadOnlyModeSwitch != null) {
            mChangeReadOnlyModeSwitch.setOnCheckedChangeListener(null);
        }
        if (mSendRandomMessageButton != null) {
            mSendRandomMessageButton.setOnClickListener(null);
        }
        if (mOpenCameraButton != null) {
            mOpenCameraButton.setOnClickListener(null);
        }
        if (mOpenGalleryButton != null) {
            mOpenGalleryButton.setOnClickListener(null);
        }
        if (mOpenFileChooserButton != null) {
            mOpenFileChooserButton.setOnClickListener(null);
        }
        super.onDestroy();
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
                int intentFlags = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    intentFlags = PendingIntent.FLAG_IMMUTABLE;
                }
				PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, intentFlags);
				LivePerson.setImageServicePendingIntent(pendingIntent);

				// Notification builder for image upload foreground service
                NotificationCompat.Builder uploadBuilder = new NotificationCompat.Builder(this.getApplicationContext());
                NotificationCompat.Builder downloadBuilder = new NotificationCompat.Builder(this.getApplicationContext());
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

    private void showWelcomeMessageSettings() {
        String account = SampleAppStorage.getInstance(this).getAccount();
        DynamicWelcomeMessageDialog dialog = DynamicWelcomeMessageDialog.newInstance(account);
        dialog.show(getSupportFragmentManager(), TAG_WELCOME_MESSAGE_DIALOG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mConversationFragment != null){
            attachFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fragment_container_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_add_welcome_message) {
            showWelcomeMessageSettings();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
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

    private static class HybridSDKCallback implements ICallback<Unit, HybridSDKException> {

        private final Context context;

        private HybridSDKCallback(Context context) {
            this.context = context;
        }

        @Override
        public void onSuccess(Unit value) {
            Log.d(TAG, "Successfully finished hybrid call");
        }

        @Override
        public void onError(HybridSDKException exception) {
            String message = exception.getMessage() == null ? exception.toString() : exception.getMessage();
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
