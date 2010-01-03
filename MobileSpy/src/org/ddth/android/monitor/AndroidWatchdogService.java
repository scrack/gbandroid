package org.ddth.android.monitor;

import java.util.Timer;
import java.util.TimerTask;

import org.ddth.android.monitor.core.AndroidWatchdog;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Client should extend this service within its own application packages and
 * register it to the AndroidManifest.xml file.
 * 
 * @author khoanguyen
 */
public abstract class AndroidWatchdogService extends Service {
	/**
	 * Because of object sharing in Android is a bit different from other VMs,
	 * we have no choice but keeping a static variable here. Actually, there is
	 * only one instance of any service in an Android process at a time, but we
	 * have to do this because {@link #watchdog} may be accessed from another
	 * places.
	 */
	private static AndroidWatchdog watchdog;
	
	/**
	 * This timer is for deferred starting of the observation
	 */
	private Timer timer;

	/**
	 * Get the watchdog object for registering/unregistering new observers
	 * 
	 * @return
	 */
	public static AndroidWatchdog getWatchdog() {
		return watchdog;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		timer = new Timer();
		setForeground(true);
	}
	
	@Override
	public void onStart(final Intent intent, int startId) {
		super.onStart(intent, startId);

		// To ensure the data is fully updated & stable, we defer the
		// processing a little bit...
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				watchdog.observe(AndroidWatchdogService.this, intent);
			}
		}, 500);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
