package com.liveperson.sample.app.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Ilya Gazman on 11/25/2015.
 */
public class AccountStorage {

    private static final String TAG = AccountStorage.class.getSimpleName();
    public static final String BRAND_ID = "brand_id";
    public static final String SDK_SAMPLE_APP_ID = "com.liveperson.sdksample";
    public static final String SDK_SAMPLE_APP_ID_ALT = "com.liveperson.sdksample.Altered";
    private SharedPreferences mDefaultSharedPreferences;
    private static volatile AccountStorage Instance = null;

    private AccountStorage(Context context) {
        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public static AccountStorage getInstance(Context context) {
        if (Instance == null) {
            synchronized (AccountStorage.class) {
                if (Instance == null) {
                    Instance = new AccountStorage(context);
                }
            }
        }
        return Instance;
    }

    public void setAccount(String account) {
        mDefaultSharedPreferences.edit().putString(BRAND_ID, account).apply();
    }

    public String getAccount() {
        return mDefaultSharedPreferences.getString(BRAND_ID, "");
    }
}
