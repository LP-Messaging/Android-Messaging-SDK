package com.liveperson.sample.app.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ofira on 03/01/2016.
 */
public class UserProfileStorage {

    private static final String TAG = UserProfileStorage.class.getSimpleName();
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String PHONE_NUMBER = "phone_number";
    private SharedPreferences mDefaultSharedPreferences;
    private static volatile UserProfileStorage Instance = null;

    private UserProfileStorage(Context context) {
        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public static UserProfileStorage getInstance(Context context) {
        if (Instance == null) {
            synchronized (UserProfileStorage.class) {
                if (Instance == null) {
                    Instance = new UserProfileStorage(context);
                }
            }
        }
        return Instance;
    }

    public void setFirstName(String firstName) {
        mDefaultSharedPreferences.edit().putString(FIRST_NAME, firstName).apply();
    }

    public void setLastName(String lastName) {
        mDefaultSharedPreferences.edit().putString(LAST_NAME, lastName).apply();
    }

    public void setPhoneNumber(String phoneNumber) {
        mDefaultSharedPreferences.edit().putString(PHONE_NUMBER, phoneNumber).apply();
    }

    public String getLastName() {
        return mDefaultSharedPreferences.getString(LAST_NAME, "");
    }

    public String getFirstName() {
        return mDefaultSharedPreferences.getString(FIRST_NAME, "");
    }

    public String getPhoneNumber() {
        return mDefaultSharedPreferences.getString(PHONE_NUMBER, "");
    }
}
