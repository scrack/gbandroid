package org.ddth.android.monitor;

import org.ddth.android.monitor.core.AndroidEvent;
import org.ddth.mobile.monitor.core.Watchdog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Client must implement this receiver and add the following lines in the
 * AndroidManifest.xml file in order to work properly.
 * 
 * <pre>
 * &lt;receiver android:name="org.ddth.android.monitor.AndroidBroadcastReceiver"&gt;
 *     &lt;intent-filter&gt;
 *         &lt;action android:name="android.intent.action.BOOT_COMPLETED" /&gt;
 *         &lt;action android:name="android.intent.action.USER_PRESENT" /&gt;
 *         &lt;action android:name="android.intent.action.NEW_OUTGOING_CALL" /&gt;
 *         &lt;action android:name="android.intent.action.PHONE_STATE" /&gt;
 *         &lt;action android:name="android.provider.Telephony.SMS_RECEIVED" /&gt;
 *     &lt;/intent-filter&gt;
 * &lt;/receiver&gt;
 * </pre>
 * 
 * @author khoanguyen
 */
public abstract class AndroidBroadcastReceiver extends BroadcastReceiver {

	/**
	 * Use volatile to reduce the probability of invalid thread access error
	 * here ;-)...
	 */
	private volatile static boolean initialized = false;
	
	protected abstract void initialize(Watchdog watchdog, AndroidEvent event);
	
	public void onReceive(Context context, Intent intent) {
		AndroidEvent event = new AndroidEvent(this, context, intent);
		Watchdog watchdog = AndroidWatchdogService.getWatchdog();
		// Check if we should start registration process.
 		if (!initialized) {
			initialized = true;
			initialize(watchdog, event);
			Intent service = new Intent(context, AndroidWatchdogService.class);
			context.startService(service);
		}
		watchdog.watch(event);
	}
}
