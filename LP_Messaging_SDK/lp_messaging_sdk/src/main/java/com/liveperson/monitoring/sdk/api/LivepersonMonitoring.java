package com.liveperson.monitoring.sdk.api;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.liveperson.infra.log.LPMobileLog;
import com.liveperson.lp_monitoring_sdk.BuildConfig;
import com.liveperson.monitoring.MonitoringFactory;
import com.liveperson.monitoring.sdk.callbacks.MonitoringErrorType;
import com.liveperson.monitoring.sdk.callbacks.SdeCallback;
import com.liveperson.sdk.MonitoringParams;
import com.liveperson.sdk.callbacks.EngagementCallback;


/**
 * Liveperson Monitoring SDK entry point.
 *
 * You must initialize this class before use.
 */
public class LivepersonMonitoring {

	public static final String TAG = "LivepersonMonitoring";

	private LivepersonMonitoring() {
	}


	/**
	 * Send and SDE
	 * @param context
	 * @param consumerId
	 * @param monitoringParams
	 * @param callback
	 */
	public static void sendSde(Context context, @NonNull String consumerId, @NonNull MonitoringParams monitoringParams, SdeCallback callback) {
		if (!MonitoringFactory.INSTANCE.isInitialized()) {
			LPMobileLog.w(TAG, "sendSde: not initialized");
			if (callback != null) {
				callback.onError(MonitoringErrorType.NOT_INITIALIZED, null);
			}
			return;
		}

		if (context == null) {
			LPMobileLog.w(TAG, "Context is null. Aborting.");
			if (callback != null) {
				callback.onError(MonitoringErrorType.PARAMETER_MISSING, new Exception("Context parameter is missing"));
			}
			return;
		}

		if (TextUtils.isEmpty(consumerId)) {
			LPMobileLog.w(TAG, "ConsumerId is mandatory.");
			if (callback != null) {
				callback.onError(MonitoringErrorType.PARAMETER_MISSING, new Exception("ConsumerId is mandatory"));
			}
			return;
		}

		if (monitoringParams == null || monitoringParams.getEngagementAttributes() == null) {
			LPMobileLog.w(TAG, "sendSde: EngagementAttributes were not provided. Aborting.");
			callback.onError(MonitoringErrorType.PARAMETER_MISSING, new Exception("EngagementAttributes were not provided"));
			return;
		}

		MonitoringFactory.INSTANCE.sendSde(context, consumerId, monitoringParams, callback);
	}

	/**
	 * Get engagement
	 * @param context
	 * @param consumerId
	 * @param monitoringParams
	 * @param callback
	 */
	public static void getEngagement(Context context, @Nullable String consumerId, MonitoringParams monitoringParams, EngagementCallback callback){
		if (!MonitoringFactory.INSTANCE.isInitialized()) {
			LPMobileLog.w(TAG, "getEngagement: not initialized");
			if (callback != null) {
				callback.onError(MonitoringErrorType.NOT_INITIALIZED, null);
			}
			return;
		}

		if (context == null) {
			LPMobileLog.w(TAG, "Context is null. Aborting.");
			if (callback != null) {
				callback.onError(MonitoringErrorType.PARAMETER_MISSING, new Exception("Context parameter is missing"));
			}
			return;
		}


		MonitoringFactory.INSTANCE.getEngagement(context, consumerId, monitoringParams, callback);

	}

	/**
	 * Get the LivePerson Monitoring SDK version
	 *
	 * @return
	 */
	public static String getSDKVersion() {
		return BuildConfig.VERSION_NAME;
	}

}
