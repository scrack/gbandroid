package org.ddth.android.monitor.observer;

import java.util.Date;

import org.ddth.android.monitor.core.AndroidEvent;
import org.ddth.http.core.Logger;
import org.ddth.mobile.monitor.core.Event;
import org.ddth.mobile.monitor.core.Reporter;
import org.ddth.mobile.monitor.report.SMS;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;

/**
 * @author khoanguyen
 *
 */
public final class AndroidSmsWatcher extends AndroidWatcher {
	
	public static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	public static final String ACTION_NEW_OUTGOING_SMS = "android.provider.Telephony.NEW_OUTGOING_SMS";

	static final String CONTENT_SMS = "content://sms";
	static final int MESSAGE_TYPE_OUTBOX = 4;
	static final int MESSAGE_TYPE_SENT = 2;

	private static final String[] INTENTS = {ACTION_SMS_RECEIVED, ACTION_NEW_OUTGOING_SMS};
	
	private ContentObserver observer;
	
	public AndroidSmsWatcher(Reporter reporter) {
		setReporter(reporter);
	}
	
	@Override
	public String[] getIntents() {
		return INTENTS;
	}

	@Override
	public void start(Event dc) {
		super.start(dc);
		// Because current Android SDK doesn't support broadcast receiver for
		// outgoing SMS events, we should monitor the sms inbox by registering
		// a content observer to the ContentResolver.
		registerContentObserver(((AndroidEvent) dc));
	}

	@Override
	public void stop(Event dc) {
		super.stop(dc);
		Context context = ((AndroidEvent) dc).getContext();
		context.getContentResolver().unregisterContentObserver(observer);
	}

	@Override
	public void service(AndroidEvent event) {
		SMS sms = readFromIncomingSMS(event.getIntent());
		if (sms != null) {
			getReporter().report(event, sms);
		}
	}
	
	/**
	 * Register an observer for listening outgoing sms events.
	 *  
	 * @param dc
	 */
	private void registerContentObserver(final AndroidEvent dc) {
		final Context context = dc.getContext();
		observer = new ContentObserver(null) {
			public void onChange(boolean selfChange) {
				SMS sms = readFromOutgoingSMS(context);
				// Get super class notified
				if (sms != null) {
					getReporter().report(dc, sms);
				}
			}
		};
		context.getContentResolver().registerContentObserver(
			Uri.parse(CONTENT_SMS), true, observer);
	}

	/**
	 * This is invoked directly from the SMS observer to wrap the outgoing SMS.
	 * A more elegant method would be firing an intent to the BroadcastReceiver
	 * and let the receiver handles the intent naturally.
	 * 
	 * @see #registerContentObserver(AndroidEvent, int)
	 * @param context
	 */
	private SMS readFromOutgoingSMS(Context context) {
		Cursor cursor = context.getContentResolver().query(
				Uri.parse(CONTENT_SMS), null, null, null, null);
		SMS sms = null;
		if (cursor.moveToNext()) {
			String protocol = cursor.getString(cursor.getColumnIndex("protocol"));
			int type = cursor.getInt(cursor.getColumnIndex("type"));
			// Only processing outgoing sms event & only when it
			// is sent successfully (available in SENT box).
			if (protocol != null || type != MESSAGE_TYPE_SENT) {
				return sms;
			}
			int dateColumn = cursor.getColumnIndex("date");
			int bodyColumn = cursor.getColumnIndex("body");
			int addressColumn = cursor.getColumnIndex("address");

			String from = "0";
			String to = cursor.getString(addressColumn);
			Date now = new Date(cursor.getLong(dateColumn));
			String message = cursor.getString(bodyColumn);
			sms = new SMS(from, to, message, now);
		}
		cursor.close();
		return sms;
	}
	
	/**
	 * This method reconstruct the first incoming SMS by parsing the intent.
	 * 
	 * @param intent
	 * @return an SMS message
	 */
	private SMS readFromIncomingSMS(Intent intent) {
		SmsMessage msg[] = getMessagesFromIntent(intent);
		SMS sms = null;
		for (int i = 0; i < msg.length; i++) {
			String message = msg[i].getDisplayMessageBody();
			if (message != null && message.length() > 0) {
				String from = msg[i].getOriginatingAddress();
				String to = "0";
				Date now = new Date();
				sms = new SMS(from, to, message, now);
				break;
			}
		}
		return sms;
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
			Logger.getDefault().error("Fail to create an incoming SMS from pdus", e);
		}
		return msgs;
	}
}