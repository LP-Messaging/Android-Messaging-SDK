package com.liveperson.sample.app.utils;

import android.content.Context;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.liveperson.infra.BadArgumentException;
import com.liveperson.infra.CampaignInfo;
import com.liveperson.infra.auth.LPAuthenticationParams;
import com.liveperson.infra.auth.LPAuthenticationType;
import com.liveperson.sample.app.activities.FragmentContainerActivity;
import com.liveperson.sample.app.activities.MessagingActivity;
import com.liveperson.sample.app.push.PushRegistration;

/**
 * ***** Sample app class - Not related to Messaging SDK ****
 * Utils class that we use in the sample app only.
 * All these methods are just to have the {@link MessagingActivity} and {@link FragmentContainerActivity}
 * simple as possible.
 */
public class SampleAppUtils {

    /**
     * Enable a button and change the text
     *
     * @param btn         - the button to enable
     * @param enabledText - the text we want to show on the button
     */
    public static void enableButtonAndChangeText(Button btn, String enabledText) {
        btn.setText(enabledText);
        btn.setEnabled(true);
    }

    /**
     * Disable a button and change the text
     *
     * @param btn          - the button to enable
     * @param disabledText - the text we want to show on the button
     */
    public static void disableButtonAndChangeText(Button btn, String disabledText) {
        btn.setText(disabledText);
        btn.setEnabled(false);
    }

    /**
     * Call to the {@link com.liveperson.sample.app.push.PushRegistration} class which was taken from Google's
     * sample app for FCM integration
     */
    public static void handlePusherRegistration(Context ctx) {
        PushRegistration.INSTANCE.getToken(ctx);
    }

    /**
     * Get the CampaignInfo stored in the SampleAppStorage (if available). If not available return null
     */
    @Nullable
    public static CampaignInfo getCampaignInfo(Context context) {
        CampaignInfo campaignInfo = null;
        if (SampleAppStorage.getInstance(context).getCampaignId() != null || SampleAppStorage.getInstance(context).getEngagementId() != null ||
                SampleAppStorage.getInstance(context).getSessionId() != null || SampleAppStorage.getInstance(context).getVisitorId() != null) {

            try {
                campaignInfo = new CampaignInfo(SampleAppStorage.getInstance(context).getCampaignId(), SampleAppStorage.getInstance(context).getEngagementId(),
                        SampleAppStorage.getInstance(context).getInteractionContextId(),
                        SampleAppStorage.getInstance(context).getSessionId(), SampleAppStorage.getInstance(context).getVisitorId());
            } catch (BadArgumentException e) {
                return null;
            }
        }
        return campaignInfo;
    }

    /**
     * Create the {@link LPAuthenticationParams} object.
     */
    public static LPAuthenticationParams createLPAuthParams(Context context) {
        LPAuthenticationType authType = SampleAppStorage.getInstance(context).getAuthenticateType();
        String authCode = SampleAppStorage.getInstance(context).getAuthCode();
        String publicKey = SampleAppStorage.getInstance(context).getPublicKey();

        LPAuthenticationParams lpAuthenticationParams = new LPAuthenticationParams(authType);
        lpAuthenticationParams.setAuthKey(authCode);
        lpAuthenticationParams.setPerformStepUp(authType.equals(LPAuthenticationType.AUTH) &&
                SampleAppStorage.getInstance(context).getPerformStepUpAuthentication());
//		lpAuthenticationParams.setHostAppJWT("host app jwt");  // Set the jwt if needed.

        if (!publicKey.trim().isEmpty()) {
            String[] keyPair = publicKey.split(",");
            for (String key : keyPair) {
                String[] pinKeyPair = key.split(";");
                if (pinKeyPair.length == 2) {
                    lpAuthenticationParams.addCertificatePinningKey(pinKeyPair[0], pinKeyPair[1]);
                }
            }
        }
        return lpAuthenticationParams;
    }
}
