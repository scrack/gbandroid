package org.ddth.android.monitor.observer;

import java.util.Date;

import org.ddth.android.monitor.core.AndroidEvent;
import org.ddth.mobile.monitor.core.Event;
import org.ddth.mobile.monitor.core.Reporter;
import org.ddth.mobile.monitor.report.Calendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.DateUtils;

/**
 * @author khoanguyen
 */
public final class AndroidCalendarWatcher extends AndroidWatcher {
	private static final String[] INTENTS = {};
	private static final Uri CONTENT_URI = Uri.parse("content://calendar/calendars");
	//private static final int BUFFER_SIZE = 5;
	//private static final List<Calendar> HISTORIES = new ArrayList<Calendar>(BUFFER_SIZE);
	
	private ContentObserver observer;
	
	public AndroidCalendarWatcher(Reporter reporter) {
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
		observer = null;
	}

	@Override
	public void service(AndroidEvent event) {
		Calendar calendar = readCalendar(event.getContext());
		if (calendar != null) {
			getReporter().report(event, calendar);
		}
	}
	
	/**
	 * Register an observer for data changed events.
	 *  
	 * @param event
	 */
	private void registerContentObserver(final AndroidEvent event) {
		if (observer != null) {
			return;
		}
		final Context context = event.getContext();
		observer = new ContentObserver(null) {
			public void onChange(boolean selfChange) {
				service(event);
			}
		};
		context.getContentResolver().registerContentObserver(
				CONTENT_URI, true, observer);
	}
	
	/**
	 * Read the latest modification from the content database.
	 * 
	 * @see #registerContentObserver(AndroidEvent, int)
	 * @param context
	 */
	public Calendar readCalendar(Context context) {
		ContentResolver contentResolver = context.getContentResolver();
		
		final Cursor cursor = contentResolver.query(Uri.parse("content://calendar/calendars"),
				(new String[] { "_id", "displayName", "selected" }), null, null, null);
		if (cursor.moveToNext()) {
			final String _id = cursor.getString(0);
			final String displayName = cursor.getString(1);
			System.out.println("Id: " + _id + " Display Name: " + displayName);
			readItem(contentResolver, _id);
		}
		cursor.close();
		return null;
	}
	
	private Calendar readItem(ContentResolver contentResolver, String id) {
		Uri.Builder builder = Uri.parse("content://calendar/instances/when").buildUpon();
		long now = new Date().getTime();
		ContentUris.appendId(builder, now - DateUtils.WEEK_IN_MILLIS);
		ContentUris.appendId(builder, now + DateUtils.WEEK_IN_MILLIS);
		Cursor cursor = contentResolver.query(builder.build(),
				new String[] { "title", "begin", "end", "allDay"}, "Calendars._id=" + id,
				null, "startDay ASC, startMinute ASC");
		Calendar calendar = null;
		if (cursor.moveToNext()) {
			//date \t time \t title \t startDateTime \t endDateTime \t description \t location \r
			final String title = cursor.getString(0);
			final Date begin = new Date(cursor.getLong(1));
			final Date end = new Date(cursor.getLong(2));
			System.out.println("Title: " + title + " Begin: " + begin + " End: " + end);
			calendar = new Calendar(new Date(), begin.toGMTString(), end.toGMTString(), title, "description", "location");
		}
		cursor.close();
		return calendar;
	}
}