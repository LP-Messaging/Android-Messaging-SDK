//package com.liveperson.sample.app.push.huawei
//
//import android.util.Log
//import com.huawei.hms.push.HmsMessageService
//import com.huawei.hms.push.RemoteMessage
//import com.liveperson.messaging.sdk.api.LivePerson
//import com.liveperson.sample.app.notification.NotificationUI
//import com.liveperson.sample.app.utils.SampleAppStorage
//
//class LpHmsPushService : HmsMessageService() {
//
//	private companion object {
//		private const val TAG = "LpHmsPushService"
//	}
//
//	override fun onNewToken(pntoken: String?) {
//		super.onNewToken(pntoken)
//
//		Log.d(TAG, "onNewToken: $pntoken")
//	}
//
//	override fun onMessageReceived(remoteMessage: RemoteMessage?) {
//		super.onMessageReceived(remoteMessage)
//
//		remoteMessage?.let {
//			Log.d(TAG, "onMessageReceived: ${remoteMessage.data}")
//			Log.d(TAG, "Message data payload: " + remoteMessage.dataOfMap)
//
//			// Send the data into the SDK
//			val account = SampleAppStorage.getInstance(this).account
//			val message = LivePerson.handlePushMessage(this, remoteMessage.dataOfMap, account, false)
//
//			//Code snippet to add push UI notification
//			if (message != null) {
//				NotificationUI.showPushNotification(this, message);
//			}
//		}
//	}
//}
