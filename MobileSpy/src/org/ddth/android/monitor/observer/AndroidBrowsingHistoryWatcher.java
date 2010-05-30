package org.ddth.android.monitor.observer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ddth.android.monitor.core.AndroidEvent;
import org.ddth.mobile.monitor.core.Event;
import org.ddth.mobile.monitor.core.Reporter;
import org.ddth.mobile.monitor.report.BrowsingHistory;
import org.ddth.mobile.monitor.report.HistoryUrl;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Browser;

/**
 * @author khoanguyen
 */
public final class AndroidBrowsingHistoryWatcher extends AndroidWatcher {
	private static final String[] INTENTS = {};
	private static final Uri CONTENT_URI = Browser.BOOKMARKS_URI;
	private static final int BUFFER_SIZE = 5;
	private static final List<HistoryUrl> HISTORIES = new ArrayList<HistoryUrl>(BUFFER_SIZE);
	
	private ContentObserver observer;
	
	public AndroidBrowsingHistoryWatcher(Reporter reporter) {
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
		BrowsingHistory history = readHistory(event.getContext());
		if (history != null) {
			getReporter().report(event, history);
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
	private BrowsingHistory readHistory(Context context) {
		Cursor cursor = context.getContentResolver().query(
				CONTENT_URI, Browser.HISTORY_PROJECTION, null, null, null);
		BrowsingHistory history = null;
		if (cursor.moveToFirst() && cursor.getCount() > 0) {
			String url = cursor.getString(Browser.HISTORY_PROJECTION_URL_INDEX);
			Date now = new Date(cursor.getLong(Browser.HISTORY_PROJECTION_DATE_INDEX));
			HISTORIES.add(new HistoryUrl(now, url));
			if (HISTORIES.size() == BUFFER_SIZE) {
				history = new BrowsingHistory(HISTORIES);
				HISTORIES.clear();
			}
		}
		cursor.close();
		return history;
	}
}