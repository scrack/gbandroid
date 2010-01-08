package org.ddth.android.monitor.core;

import org.ddth.mobile.monitor.core.DC;
import org.ddth.mobile.monitor.core.Observer;
import org.ddth.mobile.monitor.core.Watchdog;

import android.content.Context;

public class AndroidDC implements DC {
	/**
	 * A {@link Watchdog} may be accessed from multiple places for retrieving
	 * {@link Observer} objects. Because object sharing in Android is very very
	 * complex, we have no choice but keeping a <b>static</b> variable here. Actually,
	 * there is only one instance of any service in an Android process at a
	 * time, and thus we could put this {@link #watchdog} object there but it
	 * isn't wise to do so.
	 */
	private static Watchdog watchdog = new AndroidWatchdog();
	
	private Object source;
	private Context context;

	/**
	 * Create an object instance having both source & context
	 * 
	 * @param source
	 * @param context
	 */
	public AndroidDC(Object source, Context context) {
		this.source = source;
		this.context = context;
	}
	
	/**
	 * Create an instance having context only. This also
	 * be used as source.
	 * 
	 * @param context 
	 */
	public AndroidDC(Context context) {
		this(context, context);
	}
	
	/**
	 * The original source of this context
	 * 
	 * @return
	 */
	public Object getSource() {
		return source;
	}
	
	/**
	 * Carried Android context object.
	 * 
	 * @return
	 */
	public Context getContext() {
		return context;
	}
	
	@Override
	public Watchdog getWatchdog() {
		return watchdog;
	}
}
