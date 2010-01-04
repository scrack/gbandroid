package org.ddth.android.monitor.observer;

import java.util.Date;

import org.ddth.android.monitor.core.AndroidReceiver;
import org.ddth.mobile.monitor.core.DC;
import org.ddth.mobile.monitor.core.WatcherAdapter;
import org.ddth.mobile.monitor.report.Call;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.telephony.TelephonyManager;

public class AndroidCallWatcher extends WatcherAdapter<Call> implements AndroidReceiver {

	private static final String[] INTENTS = {TelephonyManager.ACTION_PHONE_STATE_CHANGED};

	private String phoneNumber;
	
	@Override
	public String[] getIntents() {
		return INTENTS;
	}

	@Override
	public void start(DC dc) {
		super.start(dc);
		final Context context = (Context) dc.getPlatformContext();
		TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		phoneNumber = telephony.getLine1Number();
	}

	@Override
	protected Call getReport(Object observable) {
		return parseCall((Context)observable);
	}

	Call parseCall(Context context) {
		// Current Android SDK supports broadcast receiver for phone state
		// changed only, we have to extract call information ourselves.
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
		int type = Call.CALL_TYPE_UNKNOWN_DIRECTION;
		switch (cursor.getInt(typeColumn)) {
		case CallLog.Calls.INCOMING_TYPE:
			type = Call.CALL_TYPE_INCOMING;
			break;
			
		case CallLog.Calls.OUTGOING_TYPE:
			from = phoneNumber;
			to = number;
			type = Call.CALL_TYPE_OUTGOING;
			break;
			
		case CallLog.Calls.MISSED_TYPE:
			type = Call.CALL_TYPE_MISSEDCALL;
			break;
		}
		return new Call(from, to, duration, type, now);
	}
}