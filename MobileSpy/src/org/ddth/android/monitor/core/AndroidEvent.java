package org.ddth.android.monitor.core;

import org.ddth.mobile.monitor.core.Event;

import android.content.Context;
import android.content.Intent;

public class AndroidEvent implements Event {
	private Object source;
	private Context context;
	private Intent intent;

	/**
	 * Create an instance.
	 * 
	 * @param source
	 * @param intent
	 */
	public AndroidEvent(Object source, Context context, Intent intent) {
		this.source = source;
		this.context = context;
		this.intent = intent;
	}

	/**
	 * @return The original source of this event.
	 */
	public Object getSource() {
		return source;
	}

	/**
	 * @return The android context of this event.
	 */
	public Context getContext() {
		return context;
	}
	
	/**
	 * @return The intent of this event.
	 */
	public Intent getIntent() {
		return intent;
	}
}
