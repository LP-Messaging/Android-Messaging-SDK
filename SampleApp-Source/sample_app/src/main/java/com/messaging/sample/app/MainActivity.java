package com.messaging.sample.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.liveperson.messaging.sdk.ILivePersonCallback;
import com.liveperson.messaging.sdk.bootstrap.LivePerson;

import com.liveperson.messaging.sdk.managers.NotificationCenter;
import com.liveperson.messaging.sdk.model.ChatMessage;
import com.messaging.sample.app.push.RegistrationIntentService;


public class MainActivity extends Activity {

    public static final String BRAND_ID = "brand_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /**TODO: StrictMode - Dev tool - Need to REMOVE before moving to production**/
/*        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .detectCustomSlowCalls()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectActivityLeaks()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());*/
        /**TODO: StrictMode - Dev tool - Need to REMOVE before moving to production**/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initVersion();

        final EditText account = (EditText) findViewById(R.id.account_number);
        final EditText firstName = (EditText) findViewById(R.id.lp_first_name);
        final EditText lastName = (EditText) findViewById(R.id.lp_last_name);
        final EditText phoneNumber = (EditText) findViewById(R.id.lp_phone_number);

        String defaultAccount = getSavedBrandId();
        account.setText(defaultAccount);
        account.setSelection(defaultAccount.length());

        Button btn = (Button) findViewById(R.id.start_messaging);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String accountStr = account.getText().toString().trim();
                String fName = firstName.getText().toString().trim();
                String lName = lastName.getText().toString().trim();
                String phone = phoneNumber.getText().toString().trim();
                if (!TextUtils.isEmpty(accountStr)) {
                    if (!TextUtils.isEmpty(fName) && !TextUtils.isEmpty(lName) && !TextUtils.isEmpty(phone)) {
                        LivePerson.setUserProfile(fName, lName, phone);
                    } else {
                        LivePerson.setUserProfile("LivePerson", "Default", "050-1234567");
                    }

                    saveBrandId(accountStr);
                    LivePerson.initialize(MainActivity.this, accountStr);
                    LivePerson.showConversation(MainActivity.this);

                    //We start RegistrationIntentService here because we need to initialize Liveperson
                    Intent intent = new Intent(MainActivity.this, RegistrationIntentService.class);
                    startService(intent);
                }
            }
        });

        //Set the custom gui callback
        LivePerson.setCallback(new ILivePersonCallback() {
            @Override
            public void onCustomGuiTapped() {
                String phone = "";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }

            @Override
            public void onInAppMessageReceived(String agentName, String brandId, String message) {
                NotificationCenter.getInstance().showNotification(agentName, brandId, message);
            }

            @Override
            public void onInitProcessFailed(ILivePersonCallback.InitFailedReasons reason) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Init Failed!!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onServerConnectionError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onServerConnectionError", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onTokenRequestError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onTokenRequestError", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void logEvent(String log) {
                Log.d(MainActivity.class.getSimpleName(), log);
            }
        });


    }

    /**
     * Get LivePerson's SDK version
     */
    private void initVersion() {
        TextView version = (TextView) findViewById(R.id.version);
        version.setText(String.format("SDK Version %1$s", LivePerson.getSDKVersion()));
    }

    /**
     * Save the last used brand id for future sessions
     *
     * @param brandId
     */
    private void saveBrandId(String brandId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putString(BRAND_ID, brandId).apply();
    }

    /**
     * Get the last saved brand id
     *
     * @return
     */
    private String getSavedBrandId() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(BRAND_ID, "");
    }

    @Override
    public void finish() {
        LivePerson.shutDown();
        super.finish();
    }

}
