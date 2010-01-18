package org.ddth.android.monitor;

import org.ddth.http.core.Logger;
import org.ddth.mobile.monitor.core.DC;
import org.ddth.mobile.monitor.core.Watchdog;
import org.ddth.mobile.monitor.core.Watcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Client should implement this receiver within its own application
 * packages and register it to the AndroidManifest.xml file.
 * 
 * @author khoanguyen
 */
public class AndroidBroadcastReceiver extends BroadcastReceiver {

	public void onReceive(final Context context, Intent intent) {
		DC dc = AndroidWatchdogService.createDC(this, context);
		Watchdog watchdog = dc.getWatchdog();
		// Check if we should start the observer registration service  
		if (watchdog.size() == 0) {
			try {
				String className = intent.getStringExtra("watchdog");
				Class clazz = Class.forName(className);
				Watcher watcher = (Watcher) clazz.newInstance();
				watcher.start(dc);
			}
			catch (Exception e) {
				Logger.getDefault().error("Error when creating watchdog", e);
			}
		}
		watchdog.dispatch(dc, intent);
	}
}
