package com.liveperson.sample.app.push.fcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.sample.app.utils.SampleAppStorage;

/**
 * Created by nirni on 11/20/16.
 */
public class FirebaseRegistrationIntentService extends IntentService {

	public static final String TAG = FirebaseRegistrationIntentService.class.getSimpleName();

	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 *
	 */
	public FirebaseRegistrationIntentService() {
		super(TAG);
	}


	@Override
	protected void onHandleIntent(Intent intent) {

		Log.d(TAG, "onHandleIntent: registering the token to pusher");

		FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
			@Override
			public void onComplete(@NonNull Task<InstanceIdResult> task) {
				if (!task.isSuccessful()) {
					Log.w(TAG, "getInstanceId failed", task.getException());
					return;
				}

				// Get new Instance ID token
				String token = task.getResult().getToken();
				// Register to Liveperson Pusher
				String account = SampleAppStorage.getInstance(getBaseContext()).getAccount();
				String appID = SampleAppStorage.SDK_SAMPLE_FCM_APP_ID;
				LivePerson.registerLPPusher(account, appID, token);
			}
		});

		// Notify UI that registration has completed, so the progress indicator can be hidden.
//		Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
//		LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

	}
}
