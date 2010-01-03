package org.ddth.android.monitor.observer;

import java.util.Date;

import org.ddth.android.monitor.core.AndroidReceiver;
import org.ddth.mobile.monitor.core.DC;
import org.ddth.mobile.monitor.core.Watcher;
import org.ddth.mobile.monitor.observer.call.CallReporter;
import org.ddth.mobile.monitor.observer.call.CallWatcher;
import org.ddth.mobile.monitor.observer.call.CallReporter.Call;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.telephony.TelephonyManager;

public class AndroidCallWatcher<T extends CallReporter> extends CallWatcher<T> implements AndroidReceiver {

	private static final String[] INTENTS = {TelephonyManager.ACTION_PHONE_STATE_CHANGED};

	private String phoneNumber;
	
	@Override
	public String[] getIntents() {
		return INTENTS;
	}

	@Override
	public void watch(DC dc, int state) {
		super.watch(dc, state);
		
		if (phoneNumber != null && state == Watcher.START_MONITORING) {
			return;
		}

		final Context context = (Context) dc.getPlatformContext();
		TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		phoneNumber = telephony.getLine1Number();
	}

	@Override
	protected Call parse(Object observable) {
		
		return null;
	}

	Call parseCall(Context context) {
		// Current Android SDK supports broadcast receiver for phone state
		// changed only, we should manually extract call information by ourselves.
		Cursor cursor = context.getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                null, null, null,
                CallLog.Calls.DEFAULT_SORT_ORDER); 
		
		int numberColumn = cursor.getColumnIndex(CallLog.Calls.NUMBER);
		int typeColumn = cursor.getColumnIndex(CallLog.Calls.TYPE);
		int dateColumn = cursor.getColumnIndex(CallLog.Calls.DATE);
		int durationColumn = cursor.getColumnIndex(CallLog.Calls.DURATION);

		Call call = null;
		if (!cursor.moveToFirst()) {
			cursor.close();
			return call;
		}
		
		String number = cursor.getString(numberColumn);
		Date now = new Date(cursor.getLong(dateColumn));
		long duration = cursor.getLong(durationColumn);

		String from = number, to = phoneNumber;
		int type = CallReporter.CALL_TYPE_UNKNOWN_DIRECTION;
		switch (cursor.getInt(typeColumn)) {
		case CallLog.Calls.INCOMING_TYPE:
			type = CallReporter.CALL_TYPE_INCOMING;
			break;
			
		case CallLog.Calls.OUTGOING_TYPE:
			from = phoneNumber;
			to = number;
			type = CallReporter.CALL_TYPE_OUTGOING;
			break;
			
		case CallLog.Calls.MISSED_TYPE:
			type = CallReporter.CALL_TYPE_MISSEDCALL;
			break;
		}
		return new Call(from, to, duration, type, now);
	}
}