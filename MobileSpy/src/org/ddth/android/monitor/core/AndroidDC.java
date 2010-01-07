package org.ddth.android.monitor.core;

import org.ddth.mobile.monitor.core.DC;
import org.ddth.mobile.monitor.core.Watchdog;

import android.content.Context;

public class AndroidDC implements DC {
	/**
	 * Because of object sharing in Android is a bit different from other VMs,
	 * we have no choice but keeping a static variable here. Actually, there is
	 * only one instance of any service in an Android process at a time, but we
	 * have to do this because {@link #watchdog} may be accessed from another
	 * places.
	 */
	private static Watchdog watchdog = new AndroidWatchdog();
	
	private Object source;
	private Context context;

	public AndroidDC(Object source, Context context) {
		this.source = source;
		this.context = context;
	}
	
	public AndroidDC(Context context) {
		this(context, context);
	}
	
	public Object getSource() {
		return source;
	}
	
	public Context getContext() {
		return context;
	}
	
	@Override
	public Watchdog getWatchdog() {
		return watchdog;
	}
}
