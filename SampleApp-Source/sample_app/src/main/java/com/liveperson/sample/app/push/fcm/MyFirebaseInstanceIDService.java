package com.liveperson.sample.app.push.fcm;

import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;

/**
 * Created by nirni on 11/17/16.
 */
public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

	/**
	 * Called if InstanceID token is updated. This may occur if the security of
	 * the previous token had been compromised. Note that this is called when the InstanceID token
	 * is initially generated so this is where you would retrieve the token.
	 */
	@Override
	public void onNewToken(String token) {
		super.onNewToken(token);
		Intent intent = new Intent(this, FirebaseRegistrationIntentService.class);
		startService(intent);
	}
}
