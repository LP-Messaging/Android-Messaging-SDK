package com.liveperson.sample.app;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.liveperson.infra.ConversationViewParams;
import com.liveperson.infra.ICallback;
import com.liveperson.infra.Infra;
import com.liveperson.infra.InitLivePersonProperties;
import com.liveperson.infra.LPAuthenticationParams;
import com.liveperson.infra.callbacks.InitLivePersonCallBack;
import com.liveperson.messaging.sdk.api.callbacks.LogoutLivePersonCallback;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.messaging.sdk.api.model.ConsumerProfile;
import com.liveperson.sample.app.Utils.SampleAppStorage;
import com.liveperson.sample.app.Utils.SampleAppUtils;
import com.liveperson.sample.app.push.NotificationUI;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * ***** Sample app class - Not related to Messaging SDK ****
 * <p>
 * The main activity of the sample app
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText mAccountTextView;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mPhoneNumberView;
    private EditText mAuthCodeView;
    private Button mOpenConversationButton;
    private TextInputLayout mAccountIdLayout;
    private TextView mTime;
    private TextView mDate;
    private CheckBox mCallbackToastCheckBox;
    private CheckBox mReadOnlyModeCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSampleAppViews();
        initOpenConversationButton();
        initStartFragmentButton();

        handlePush(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handlePush(intent);
    }


    /**
     * Init Views
     */
    private void initSampleAppViews() {
        // Set the default account in the view
        mAccountTextView = (EditText) findViewById(R.id.account_id);
        mAccountIdLayout = (TextInputLayout) findViewById(R.id.account_id_layout);
        mAccountTextView.setText(SampleAppStorage.getInstance(this).getAccount());

        mFirstNameView = (EditText) findViewById(R.id.first_name);
        mFirstNameView.setText(SampleAppStorage.getInstance(this).getFirstName());

        mLastNameView = (EditText) findViewById(R.id.last_name);
        mLastNameView.setText(SampleAppStorage.getInstance(this).getLastName());

        mPhoneNumberView = (EditText) findViewById(R.id.phone_number);
        mPhoneNumberView.setText(SampleAppStorage.getInstance(this).getPhoneNumber());

        mAuthCodeView = (EditText) findViewById(R.id.auth_code);
        mAuthCodeView.setText(SampleAppStorage.getInstance(this).getAuthCode());

        String sdkVersion = String.format("SDK version %1$s ", LivePerson.getSDKVersion());
        ((TextView) findViewById(R.id.sdk_version)).setText(sdkVersion);

        mTime = (TextView) findViewById(R.id.time_sample_textView);
        mDate = (TextView) findViewById(R.id.date_sample_textView);

        mCallbackToastCheckBox = (CheckBox) findViewById(R.id.check_box_toasts);
        mReadOnlyModeCheckBox = (CheckBox) findViewById(R.id.check_box_read_only);

        updateTime();
        initLocaleSpinner();

        setLogout();
        setBadgeButton();
    }


    private void setLogout() {
        (findViewById(R.id.logout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                LivePerson.logOut(getApplicationContext(), SampleAppStorage.getInstance(MainActivity.this).getAccount(), SampleAppStorage.SDK_SAMPLE_FCM_APP_ID,
                        new LogoutLivePersonCallback() {
                            @Override
                            public void onLogoutSucceed() {
                                findViewById(R.id.logout).setEnabled(true);
                                mAccountTextView.setText("");
                                mAuthCodeView.setText("");
                                saveAccountAndUserSettings();
                            }

                            @Override
                            public void onLogoutFailed() {
                                findViewById(R.id.logout).setEnabled(true);
                                mAccountTextView.setText("Failed!");
                            }
                        });
            }
        });
    }

    private void setBadgeButton(){
        (findViewById(R.id.badge)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LivePerson.getNumUnreadMessages(SampleAppStorage.SDK_SAMPLE_FCM_APP_ID, new ICallback<Integer, Exception>() {
                    @Override
                    public void onSuccess(Integer value) {
                        Toast.makeText(MainActivity.this, "New badge value: " + value, Toast.LENGTH_LONG).show();
                        updateToolBar(value);
                    }

                    @Override
                    public void onError(Exception exception) {
                        Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }


    private void updateTime() {
        Locale locale = getLocale();
        DateFormat formatTime = DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
        DateFormat formatDate = DateFormat.getDateInstance(DateFormat.LONG, locale);
        Date date = new Date(System.currentTimeMillis());
        mDate.setText(formatDate.format(date));
        mTime.setText(formatTime.format(date));
    }


    private void initLocaleSpinner() {
        final EditText language = (EditText) findViewById(R.id.language_editText);
        final EditText country = (EditText) findViewById(R.id.country_editText);

        final Spinner localeSpinner = (Spinner) findViewById(R.id.spinner_locale);
        ArrayAdapter<String> localeSpinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.supported_locales));
        localeSpinner.setAdapter(localeSpinnerArrayAdapter);

        Button updateLocale = (Button) findViewById(R.id.update_language_button);
        updateLocale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedLocale = (String) localeSpinner.getSelectedItem();
                String[] lang_reg = selectedLocale.split("-");

                if (lang_reg.length >= 2) {
                    Log.i(TAG, "createLocale: " + lang_reg[0] + "-" + lang_reg[1]);
                    createLocale(lang_reg[0], lang_reg[1]);

                } else if (lang_reg.length == 1) {

                    String lang = lang_reg[0];

                    if (TextUtils.isEmpty(lang)) {
                        Log.i(TAG, "createLocale: taking custom locale from edit text.. ");
                        createLocale(language.getText().toString(), country.getText().toString());
                    } else {
                        Log.i(TAG, "createLocale: " + lang + "-null");
                        createLocale(lang, null);
                    }
                }

                updateTime();
            }
        });


        final Button clear = (Button) findViewById(R.id.clear_locale_button);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                language.setText(null);
                country.setText(null);
                localeSpinner.setSelection(0);
            }
        });
    }


    /**
     * Save the user input such as: account, first name, last name, phone number and auth code
     */
    private void saveAccountAndUserSettings() {
        String account = mAccountTextView.getText().toString().trim();
        String firstName = mFirstNameView.getText().toString().trim();
        String lastName = mLastNameView.getText().toString().trim();
        String phoneNumber = mPhoneNumberView.getText().toString().trim();
        String authCode = mAuthCodeView.getText().toString().trim();
        SampleAppStorage.getInstance(this).setAccount(account);
        SampleAppStorage.getInstance(this).setFirstName(firstName);
        SampleAppStorage.getInstance(this).setLastName(lastName);
        SampleAppStorage.getInstance(this).setPhoneNumber(phoneNumber);
        SampleAppStorage.getInstance(this).setAuthCode(authCode);
    }

    /**
     * Set the listener on the "open_conversation" button (Activity mode)
     */
    private void initOpenConversationButton() {
        mOpenConversationButton = (Button) findViewById(R.id.button_start_activity);
        mOpenConversationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sample app setting - used to initialize the SDK with "Activity mode" when entering from push notification
                SampleAppStorage.getInstance(MainActivity.this).setSDKMode(SampleAppStorage.SDKMode.ACTIVITY);
                if (!isValidAccount()) {
                    return;
                }
                SampleAppUtils.disableButtonAndChangeText(mOpenConversationButton, getString(R.string.initializing));
                saveAccountAndUserSettings();
                removeNotification();
                initActivityConversation();
            }
        });
    }

    /**
     * Set the listener on the "Open Fragment" button (Fragment mode)
     */
    private void initStartFragmentButton() {
        Button openFragmentButton = (Button) findViewById(R.id.button_start_fragment);
        openFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sample app setting - used to initialize the SDK with "Fragment mode" when entering from push notification
                SampleAppStorage.getInstance(MainActivity.this).setSDKMode(SampleAppStorage.SDKMode.FRAGMENT);
                if (!isValidAccount()) {
                    return;
                }
                saveAccountAndUserSettings();
                removeNotification();
                MainApplication.getInstance().setShowToastOnCallback(mCallbackToastCheckBox.isChecked());
                openFragmentContainer();
            }
        });
    }

    private void removeNotification() {
        NotificationUI.hideNotification(this);
    }

    /**
     * Validate that the text field is not empty
     *
     * @return
     */
    private boolean isValidAccount() {
        if (TextUtils.isEmpty(mAccountTextView.getText())) {
            mAccountIdLayout.setError("Enter valid Account");
            return false;
        }
        mAccountIdLayout.setError("");
        return true;
    }

    /**
     * Initialize the Messaging SDK and start the SDK in "Activity Mode"
     */
    private void initActivityConversation() {

        LivePerson.initialize(MainActivity.this, new InitLivePersonProperties(SampleAppStorage.getInstance(MainActivity.this).getAccount(), SampleAppStorage.SDK_SAMPLE_FCM_APP_ID, new InitLivePersonCallBack() {
            @Override
            public void onInitSucceed() {
                Log.i(TAG, "SDK initialize completed with Activity mode");
                //we are not setting a call back here - we'll listen to callbacks with broadcast receiver
                // in main application class.
                //setCallBack();
                MainApplication.getInstance().setShowToastOnCallback(mCallbackToastCheckBox.isChecked());
                // you can't register pusher before initialization
                SampleAppUtils.handleGCMRegistration(MainActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openActivity();
                        SampleAppUtils.enableButtonAndChangeText(mOpenConversationButton, getString(R.string.open_activity));
                    }
                });
            }

            @Override
            public void onInitFailed(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SampleAppUtils.enableButtonAndChangeText(mOpenConversationButton, getString(R.string.open_activity));
                        Toast.makeText(MainActivity.this, "Init Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }));
    }

    /**
     * Start {@link FragmentContainerActivity} that handles the SDK the Messaging SDK and start the SDK in "Fragment Mode"
     */
    private void openFragmentContainer() {
        Intent in = new Intent(MainActivity.this, FragmentContainerActivity.class);
        in.putExtra(Infra.KEY_READ_ONLY, isReadOnly());
        startActivity(in);
    }

    /**
     * Calling to "showConversation" API
     */
    private void openActivity() {
        String authCode = SampleAppStorage.getInstance(MainActivity.this).getAuthCode();
        LivePerson.showConversation(MainActivity.this, new LPAuthenticationParams().setAuthKey(authCode), new ConversationViewParams(isReadOnly()));
        ConsumerProfile consumerProfile = new ConsumerProfile.Builder()
                .setFirstName(mFirstNameView.getText().toString())
                .setLastName(mLastNameView.getText().toString())
                .setPhoneNumber(mPhoneNumberView.getText().toString())
                .build();
        LivePerson.setUserProfile(consumerProfile);
    }

    private boolean isReadOnly() {
        return mReadOnlyModeCheckBox.isChecked();
    }

    /**
     * If we initiated from a push message we show the screen that was in use the previous session (fragment/activity)
     * Activity mode is the default
     *
     * @param intent
     */
    private void handlePush(Intent intent) {
        boolean isFromPush = intent.getBooleanExtra(NotificationUI.PUSH_NOTIFICATION, false);

        //Check if we came from Push Notification
        if (isFromPush) {
            clearPushNotifications();
            switch (SampleAppStorage.getInstance(this).getSDKMode()) {
                //Initialize the SDK with "Activity mode"
                case ACTIVITY:
                    initActivityConversation();
                    break;
                //Initialize the SDK with "Fragment mode"
                case FRAGMENT:
                    openFragmentContainer();
                    break;
            }
        }
    }

    /**
     * Hide any shown notification
     */
    private void clearPushNotifications() {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NotificationUI.NOTIFICATION_ID);
    }

    /**
     * @param language
     * @param country
     */
    protected void createLocale(String language, @Nullable String country) {
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        Locale customLocale;

        if (TextUtils.isEmpty(language)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                language = resources.getConfiguration().getLocales().get(0).getCountry();
            } else {
                language = resources.getConfiguration().locale.getCountry();
            }
        }

        if (TextUtils.isEmpty(country)) {
            customLocale = new Locale(language);
        } else {
            customLocale = new Locale(language, country);
        }
        Locale.setDefault(customLocale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(customLocale);
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        } else {
            configuration.locale = customLocale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }

        Locale locale = getLocale();
        Log.d(TAG, "country = " + locale.getCountry() + ", language = " + locale.getLanguage());

    }

    /**
     * @return
     */
    private Locale getLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return getResources().getConfiguration().getLocales().get(0);
        } else {
            return getResources().getConfiguration().locale;
        }
    }

    IntentFilter unreadMessagesCounterFilter = new IntentFilter(LivePerson.ACTION_LP_UPDATE_NUM_UNREAD_MESSAGES_ACTION);
    BroadcastReceiver unreadMessagesCounter = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int newValue = intent.getIntExtra(LivePerson.ACTION_LP_UPDATE_NUM_UNREAD_MESSAGES_EXTRA, 0);
            Log.d(TAG, "Got new value for unread messages counter: " + newValue);
            updateToolBar(newValue);
        }
    };

    private void updateToolBar(final int newValue) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (newValue > 0) {
                    setTitle(getResources().getString(R.string.app_name) + " (" + newValue + ") ");
                } else {
                    setTitle(R.string.app_name);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateToolBar(LivePerson.getNumUnreadMessages(SampleAppStorage.getInstance(MainActivity.this).getAccount()));
        registerReceiver(unreadMessagesCounter, unreadMessagesCounterFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(unreadMessagesCounter);
        super.onPause();
    }
}
