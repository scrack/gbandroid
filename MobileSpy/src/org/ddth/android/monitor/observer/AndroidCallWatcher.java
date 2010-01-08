package org.ddth.android.monitor.observer;

import java.util.Date;

import org.ddth.android.monitor.core.AndroidDC;
import org.ddth.mobile.monitor.core.DC;
import org.ddth.mobile.monitor.core.Reporter;
import org.ddth.mobile.monitor.report.Call;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.provider.CallLog;
import android.telephony.TelephonyManager;

/**
 * @author khoanguyen
 */
public class AndroidCallWatcher extends AndroidWatcher {

	private static final String[] INTENTS = {TelephonyManager.ACTION_PHONE_STATE_CHANGED};

	private String phoneNumber;
	
	/**
	 * This will create a handler in registering thread.
	 */
	private Handler handler = new Handler();
	
	public AndroidCallWatcher(Reporter reporter) {
		setReporter(reporter);
	}
	
	@Override
	public String[] getIntents() {
		return INTENTS;
	}

	@Override
	public void start(DC dc) {
		super.start(dc);
		Context context = ((AndroidDC)dc).getContext();
		TelephonyManager telephony = (TelephonyManager) context.getSystemService(
				Context.TELEPHONY_SERVICE);
		phoneNumber = telephony.getLine1Number();
	}
	
	@Override
	public void service(final AndroidDC dc, Intent intent) {
		String phoneState = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
		// An idle state means the call has just been ended. We should proceed
		// to extract calling information from call log.
		if (TelephonyManager.EXTRA_STATE_IDLE.equals(phoneState)) {
			final Context context = dc.getContext();
			// To ensure the data is fully updated & stable to be correctly
			// read, we defer the processing a little bit, 500ms...
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					Call call = getCall(context);
					if (call != null) {
						getReporter().report(dc, call);
					}
				}
			}, 500);
		}
	}

	/**
	 * Get the most recently called data from the call log.
	 * 
	 * @param context
	 * @return
	 */
	private Call getCall(Context context) {
		Cursor cursor = context.getContentResolver().query(
				CallLog.Calls.CONTENT_URI,
				null, null, null,
				CallLog.Calls.DEFAULT_SORT_ORDER
		); 
		
		int numberColumn = cursor.getColumnIndex(CallLog.Calls.NUMBER);
		int typeColumn = cursor.getColumnIndex(CallLog.Calls.TYPE);
		int dateColumn = cursor.getColumnIndex(CallLog.Calls.DATE);
		int durationColumn = cursor.getColumnIndex(CallLog.Calls.DURATION);

		Call call = null;
		if (cursor.moveToFirst()) {
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
			call = new Call(from, to, duration, type, now);
		}
		cursor.close();
		return call;
	}
}