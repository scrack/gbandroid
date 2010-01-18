package org.ddth.android.monitor.core;

import org.ddth.mobile.monitor.core.DC;
import org.ddth.mobile.monitor.core.Watchdog;

import android.content.Context;

public class AndroidDC implements DC {
	private Watchdog watchdog;
	private Object source;
	private Context context;

	/**
	 * Create an instance having both source & context
	 * 
	 * @param source
	 * @param context
	 */
	public AndroidDC(Object source, Context context, Watchdog watchdog) {
		this.source = source;
		this.context = context;
		this.watchdog = watchdog;
	}

	/**
	 * Create an instance having context only. This also
	 * be used as source.
	 * 
	 * @param context 
	 */
	public AndroidDC(Context context, Watchdog watchdog) {
		this(context, context, watchdog);
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
