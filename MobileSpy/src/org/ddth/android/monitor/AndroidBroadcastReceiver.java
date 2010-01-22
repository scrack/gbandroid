package org.ddth.android.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AndroidBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
/*		ComponentName receiver = new ComponentName(context,
				AndroidBroadcastReceiver.class);
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(receiver,
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP);
		Logger.getDefault().debug("Never called again??");*/
	}
}
