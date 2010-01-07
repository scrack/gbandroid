package org.ddth.android.spy;

import org.ddth.android.monitor.AndroidBroadcastReceiver;
import org.ddth.android.monitor.core.AndroidDC;

public class SpyingReceiver extends AndroidBroadcastReceiver {
	
	protected void initialize(AndroidDC dc) {
		super.initialize(dc);
		Spy.getInstance().initialize(dc);
	}
}
