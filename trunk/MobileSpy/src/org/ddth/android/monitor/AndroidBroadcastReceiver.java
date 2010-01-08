package org.ddth.android.monitor;

import org.ddth.android.monitor.core.AndroidDC;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Client should implement this receiver within its own application
 * packages and register it to the AndroidManifest.xml file.
 * 
 * @author khoanguyen
 */
public abstract class AndroidBroadcastReceiver extends BroadcastReceiver {
	
	protected abstract Class<? extends AndroidRegisteringService> getRegisteringServiceClass();
	
	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		AndroidDC dc = new AndroidDC(this, context);
		if (Intent.ACTION_BOOT_COMPLETED.equals(action) ||
			Intent.ACTION_USER_PRESENT.equals(action))
		{
			Intent service = new Intent(context, getRegisteringServiceClass());
			context.startService(service);
		}
		dc.getWatchdog().observed(dc, intent);
	}
}
