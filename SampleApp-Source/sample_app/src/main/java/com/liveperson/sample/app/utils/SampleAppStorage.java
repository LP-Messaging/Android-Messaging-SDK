package com.liveperson.sample.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.liveperson.infra.auth.LPAuthenticationType;

/**
 * ***** Sample app class - Not related to Messaging SDK ****
 *
 * Sample app "Shared Preferences" object - helper class to save
 * fields like: first name, last name, account and auth code
 */
public class SampleAppStorage {

    private static final String TAG = SampleAppStorage.class.getSimpleName();

    public static final String SDK_SAMPLE_APP_ID = "com.liveperson.sdksample";
    public static final String SDK_SAMPLE_FCM_APP_ID = "com.liveperson.sdksampleFcm";

    private static final String AUTHENTICATE_ITEM_POSITION = "authenticate_item_position";
    private static final String AUTHENTICATE_TYPE_ORDINAL = "AUTHENTICATE_TYPE_ORDINAL";

    private static final String PERFORM_STEP_UP_AUTHENTICATION = "PERFORM_STEP_UP_AUTHENTICATION";

    // Messaging
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String PHONE_NUMBER = "phone_number";
    private static final String AUTH_CODE = "auth_code";
    private static final String SDK_MODE = "sdk_mode";
    private static final String BRAND_ID = "brand_id";
    private static final String PUBLIC_KEY = "public_key";

    // Monitoring
	private static final String APP_INSTALL_ID = "app_install_id";
	private static final String CONSUMER_ID = "consume_id";
	private static final String PAGE_ID = "page_id";

	private Long mCampaignId;
	private Long mEngagementId;
	private String mSessionId;
	private String mVisitorId;
	private String mInteractionContextId;

	private SharedPreferences mDefaultSharedPreferences;
    private static volatile SampleAppStorage Instance = null;
    public enum SDKMode {ACTIVITY, FRAGMENT}


    private SampleAppStorage(Context context) {
        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public static SampleAppStorage getInstance(Context context) {
        if (Instance == null) {
            synchronized (SampleAppStorage.class) {
                if (Instance == null) {
                    Instance = new SampleAppStorage(context);
                }
            }
        }
        return Instance;
    }

	public void setAuthenticateItemPosition(int position) {
		mDefaultSharedPreferences.edit().putInt(AUTHENTICATE_ITEM_POSITION, position).apply();
	}

	public int getAuthenticateItemPosition() {
		return mDefaultSharedPreferences.getInt(AUTHENTICATE_ITEM_POSITION, 0);
	}

    public void setAuthenticateTypeOrdinal(int position) {
        mDefaultSharedPreferences.edit().putInt(AUTHENTICATE_TYPE_ORDINAL, position).apply();
    }

    public LPAuthenticationType getAuthenticateType() {
        return LPAuthenticationType.values()[mDefaultSharedPreferences.getInt(AUTHENTICATE_TYPE_ORDINAL, 0)];
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

    public void setAuthCode(String authCode) {
        mDefaultSharedPreferences.edit().putString(AUTH_CODE, authCode).apply();
    }

    public void setPublicKey(String publicKey) {
        mDefaultSharedPreferences.edit().putString(PUBLIC_KEY, publicKey).apply();
    }

    public void setSDKMode(SDKMode state) {
        mDefaultSharedPreferences.edit().putInt(SDK_MODE, state.ordinal()).apply();
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

    public String getAuthCode() {
        return mDefaultSharedPreferences.getString(AUTH_CODE, "");
    }

    public String getPublicKey(){
        return mDefaultSharedPreferences.getString(PUBLIC_KEY, "");
    }

    public SDKMode getSDKMode() {
        int sdkModeInt = mDefaultSharedPreferences.getInt(SDK_MODE, SDKMode.ACTIVITY.ordinal());
        return sdkModeInt == SDKMode.ACTIVITY.ordinal() ? SDKMode.ACTIVITY : SDKMode.FRAGMENT;
    }

    public void setAccount(String account) {
        mDefaultSharedPreferences.edit().putString(BRAND_ID, account).apply();
    }

    public String getAccount() {
        return mDefaultSharedPreferences.getString(BRAND_ID, "");
    }

    // Monitoring
	public void setAppInstallId(String appInstallId) {
		mDefaultSharedPreferences.edit().putString(APP_INSTALL_ID, appInstallId).apply();
	}

	public String getAppInstallId() {
		return mDefaultSharedPreferences.getString(APP_INSTALL_ID, "");
	}

	public void setConsumerId(String consumerId) {
		mDefaultSharedPreferences.edit().putString(CONSUMER_ID, consumerId).apply();
	}

	public String getConsumerId() {
		return mDefaultSharedPreferences.getString(CONSUMER_ID, "");
	}

	public void setPageId(String pageId) {
		mDefaultSharedPreferences.edit().putString(PAGE_ID, pageId).apply();
	}

	public String getPageId() {
		return mDefaultSharedPreferences.getString(PAGE_ID, "");
	}

	public void setCampaignId(Long campaignId) {
		mCampaignId = campaignId;
	}

	public Long getCampaignId() {
		return mCampaignId;
	}

	public void setEngagementId(Long engagementId) {
		mEngagementId = engagementId;
	}

	public Long getEngagementId() {
		return mEngagementId;
	}

	public String getSessionId() {
		return mSessionId;
	}

	public void setSessionId(String sessionId) {
		mSessionId = sessionId;
	}

	public String getVisitorId() {
		return mVisitorId;
	}

	public void setVisitorId(String visitorId) {
		mVisitorId = visitorId;
	}

	public String getInteractionContextId() {
		return mInteractionContextId;
	}

	public void setInteractionContextId(String interactionContextId) {
		mInteractionContextId = interactionContextId;
	}

    public void setPerformStepUpAuthentication(boolean enable) {
        mDefaultSharedPreferences.edit().putBoolean(PERFORM_STEP_UP_AUTHENTICATION, enable).apply();
    }

    public boolean getPerformStepUpAuthentication() {
        return mDefaultSharedPreferences.getBoolean(PERFORM_STEP_UP_AUTHENTICATION, false);
    }
}
