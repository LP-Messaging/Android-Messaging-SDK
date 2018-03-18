package com.liveperson.sample.app.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Button;

import com.liveperson.infra.BadArgumentException;
import com.liveperson.infra.CampaignInfo;
import com.liveperson.sample.app.FragmentContainerActivity;
import com.liveperson.sample.app.MessagingActivity;
import com.liveperson.sample.app.push.fcm.FirebaseRegistrationIntentService;

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
     * Call to the {@link FirebaseRegistrationIntentService} class which was taken from Google's
     * sample app for GCM integration
     */
    public static void handleGCMRegistration(Context ctx) {
        Intent intent = new Intent(ctx, FirebaseRegistrationIntentService.class);
        ctx.startService(intent);
    }

	/**
	 * Get the CampaignInfo stored in the SampleAppStorage (if available). If not available return null
	 * @param context
	 * @return
	 */
	@Nullable
	public static CampaignInfo getCampaignInfo(Context context) {
		CampaignInfo campaignInfo = null;
		if(SampleAppStorage.getInstance(context).getCampaignId() != null || SampleAppStorage.getInstance(context).getEngagementId() != null ||
				SampleAppStorage.getInstance(context).getSessionId() != null || SampleAppStorage.getInstance(context).getVisitorId() != null){

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


}
