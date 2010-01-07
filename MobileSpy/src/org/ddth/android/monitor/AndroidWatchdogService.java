package org.ddth.android.monitor;

import org.ddth.android.monitor.core.AndroidDC;
import org.ddth.android.monitor.core.AndroidWatchdog;
import org.ddth.android.monitor.observer.AndroidWatcher;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * This Android service is spawned when an {@link AndroidWatcher} want to
 * execute its processing in a service. Therefore, client must register this
 * service to the Application node in the AndroidManifest.xml file.
 * <p>
 * &lt;service android:name="org.ddth.android.monitor.AndroidWatchdogService"
 * /&gt;
 * </p>
 * 
 * @author khoanguyen
 */
public class AndroidWatchdogService extends Service {

	public static final String EXTRA_KEY_OBJECT_HASH_CODE = "object.hashcode";

	@Override
	public void onCreate() {
		super.onCreate();
		setForeground(true);
	}
	
	@Override
	public void onStart(final Intent intent, int startId) {
		super.onStart(intent, startId);
		// The service looks up the appropriate watcher by using
		// its embedded identify in the given intent and delegate
		// the processing to this watcher.
		AndroidDC dc = new AndroidDC(this);
		AndroidWatchdog watchdog = (AndroidWatchdog) dc.getWatchdog();
		Object key = intent.getExtras().get(EXTRA_KEY_OBJECT_HASH_CODE);
		AndroidWatcher watcher = watchdog.getWatcher((Integer) key);
		watcher.service(dc, intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
