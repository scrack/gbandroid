package org.ddth.android.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Client should implement this receiver within its own application packages and
 * register it to the AndroidManifest.xml file.
 * 
 * @author khoanguyen
 */
public abstract class AndroidBroadcastReceiver extends BroadcastReceiver {
	/**
	 * Get the implementation class of the {@link AndroidWatchdogService} class.
	 * 
	 * @return
	 */
	protected abstract Class<AndroidWatchdogService> getWatchdogServiceClass();
	
	public void onReceive(Context context, Intent intent) {
		// Start the watchdog service to handle this event
		Intent service = new Intent(context, getWatchdogServiceClass());
		service.putExtras(intent.getExtras());
		context.startService(service);
	}
}
