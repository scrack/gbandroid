package org.ddth.android.monitor;

import org.ddth.android.monitor.core.AndroidDC;
import org.ddth.android.monitor.core.AndroidWatchdog;
import org.ddth.android.monitor.observer.AndroidWatcher;
import org.ddth.mobile.monitor.core.DC;
import org.ddth.mobile.monitor.core.Observer;
import org.ddth.mobile.monitor.core.Watchdog;

import android.app.Service;
import android.content.Context;
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
public final class AndroidWatchdogService extends Service {
	public static final String EXTRA_KEY_OBJECT_HASH_CODE = "object.hashcode";

	/**
	 * A {@link Watchdog} may be accessed from multiple places for retrieving
	 * {@link Observer} objects. Because object sharing in Android is very very
	 * complex, we have no choice but keeping a <b>static</b> variable here.
	 * Actually, there is only one instance of any service in an Android process
	 * at a time, and thus we could put this {@link #watchdog} object there but
	 * it isn't wise to do so.
	 */
	private static Watchdog watchdog = new AndroidWatchdog();

	public static DC createDC(Object source, Context context) {
		return new AndroidDC(source, context, watchdog);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		setForeground(true);
	}
	
	@Override
	public void onStart(final Intent intent, int startId) {
		super.onStart(intent, startId);
		// The service looks up the appropriate watcher by using
		// information in the given intent and then delegate the
		// processing to this watcher.
		AndroidDC dc = new AndroidDC(this, watchdog);
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
