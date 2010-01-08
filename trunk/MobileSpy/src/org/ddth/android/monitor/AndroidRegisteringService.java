package org.ddth.android.monitor;

import org.ddth.android.monitor.core.AndroidDC;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Client must register this service to the Application node in the
 * AndroidManifest.xml file.
 * <p>
 * &lt;service android:name="org.ddth.android.monitor.MyRegisteringService"
 * /&gt;
 * </p>
 * 
 * @author khoanguyen
 */
public abstract class AndroidRegisteringService extends Service {

	private boolean isRegistered;
	
	@Override
	public void onCreate() {
		super.onCreate();
		setForeground(true);
		isRegistered = false;
	}
	
	/**
	 * Put your initialization code here.
	 * 
	 * @param dc
	 */
	public abstract void initialize(AndroidDC dc);

	@Override
	public void onStart(final Intent intent, int startId) {
		super.onStart(intent, startId);
		if (!isRegistered) {
			initialize(new AndroidDC(this));
			isRegistered = true;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
