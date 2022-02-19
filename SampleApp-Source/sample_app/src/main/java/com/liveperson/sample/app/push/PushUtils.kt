package com.liveperson.sample.app.push

import android.content.Context
import com.google.android.gms.common.GoogleApiAvailability
//import com.huawei.hms.api.HuaweiApiAvailability
import com.google.android.gms.common.ConnectionResult as GoogleConnectionResult
//import com.huawei.hms.api.ConnectionResult as HuaweiConnectionResult

object PushUtils {

	fun isGooglePlayServicesAvailable(context: Context): Boolean {
		val googleApiAvailability = GoogleApiAvailability.getInstance()
		val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
		return resultCode == GoogleConnectionResult.SUCCESS;
	}

	// Uncomment this once Huawei releases new version with API 31 support
//	fun isHuaweiServicesAvailable(context: Context): Boolean {
//		val huaweiApiAvailability = HuaweiApiAvailability.getInstance()
//		val resultCode = huaweiApiAvailability.isHuaweiMobileServicesAvailable(context)
//		return resultCode == HuaweiConnectionResult.SUCCESS;
//	}
}
