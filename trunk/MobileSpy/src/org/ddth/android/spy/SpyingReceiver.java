package org.ddth.android.spy;

import org.ddth.android.monitor.AndroidBroadcastReceiver;
import org.ddth.android.monitor.AndroidRegisteringService;

public class SpyingReceiver extends AndroidBroadcastReceiver {

	@Override
	protected Class<? extends AndroidRegisteringService> getRegisteringServiceClass() {
		return SpyService.class;
	}
}
