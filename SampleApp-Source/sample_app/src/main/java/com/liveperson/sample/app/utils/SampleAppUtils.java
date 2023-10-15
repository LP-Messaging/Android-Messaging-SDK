package com.liveperson.sample.app.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.liveperson.infra.BadArgumentException;
import com.liveperson.infra.CampaignInfo;
import com.liveperson.infra.auth.LPAuthenticationParams;
import com.liveperson.infra.auth.LPAuthenticationType;
import com.liveperson.infra.model.PKCEParams;
import com.liveperson.sample.app.FragmentContainerActivity;
import com.liveperson.sample.app.MessagingActivity;
import com.liveperson.sample.app.push.PushRegistrationIntentService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * ***** Sample app class - Not related to Messaging SDK ****
 * Utils class that we use in the sample app only.
 * All these methods are just to have the {@link MessagingActivity} and {@link FragmentContainerActivity}
 * simple as possible.
 */
public class SampleAppUtils {

    static final String AUTHORIZE_ENDPOINT = "your_authorize_endpoint"; // Replace with the actual endpoint
    static final String CLIENT_ID = "your_client_id"; // Replace with the actual client ID
    static final String REDIRECT_URI = "your_redirect_uri"; // Replace with the actual redirect URI

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

    public static void handlePusherRegistration(Context ctx) {
        Intent intent = new Intent(ctx, PushRegistrationIntentService.class);
        ctx.startService(intent);
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

//        This API is available from v5.14.0
//        lpAuthenticationParams.setIssuerDisplayName("issuer display name");

        //
        if (SampleAppStorage.getInstance(context).isPkceEnabled() &&
                SampleAppStorage.getInstance(context).getCodeVerifier() != null) {
            lpAuthenticationParams.setCodeVerifier(
                    SampleAppStorage.getInstance(context).getCodeVerifier()
            );
            lpAuthenticationParams.setHostAppRedirectUri(REDIRECT_URI);
        }
        if (!TextUtils.isEmpty(publicKey.trim())) {
            String[] keys = publicKey.split(",");
            for (String key : keys) {
                lpAuthenticationParams.addCertificatePinningKey(key);
            }
        }
        return lpAuthenticationParams;
    }

    public static String generateAuthorizeEndpoint(PKCEParams pkceParams) throws UnsupportedEncodingException {
        Map<String, String> authParams = new HashMap<>();
        authParams.put("client_id", CLIENT_ID);
        authParams.put("redirect_uri", REDIRECT_URI);
        authParams.put("response_type", "code");
        authParams.put("scope", "email openid profile");
        authParams.put("code_challenge", pkceParams.getCodeChallenge());
        authParams.put("code_challenge_method", pkceParams.getCodeChallengeMethod());

        StringBuilder paramBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : authParams.entrySet()) {
            if (paramBuilder.length() > 0) {
                paramBuilder.append("&");
            }
            paramBuilder.append(urlEncode(entry.getKey()) + "=" + urlEncode(entry.getValue()));
        }

        return AUTHORIZE_ENDPOINT + "?" + paramBuilder;
    }

    private static String urlEncode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, "UTF-8");
    }
}
