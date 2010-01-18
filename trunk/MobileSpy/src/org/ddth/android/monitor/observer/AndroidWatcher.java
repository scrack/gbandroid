package org.ddth.android.monitor.observer;

import org.ddth.android.monitor.AndroidWatchdogService;
import org.ddth.android.monitor.core.AndroidDC;
import org.ddth.mobile.monitor.core.DC;
import org.ddth.mobile.monitor.core.Observer;
import org.ddth.mobile.monitor.core.WatcherAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * It's quite intricate to serialize objects across services, activities &
 * receivers... by using built-in parceling machanism with {@link Parcelable} &
 * {@link Parcel}. Therefore, this implementation uses an object pool to host
 * all watchers and then whenever the target handlers want to pick up a correct
 * watcher, it uses {@link #hashCode()} value which is bundled in the
 * {@link Intent} extras object and look it up in the object pool.<br>
 * <br>
 * Normally, subclass should extend {@link #service(AndroidDC, Intent)} callback
 * in order to handle incoming events.
 * 
 * @author khoanguyen
 */
public abstract class AndroidWatcher extends WatcherAdapter implements Observer {
	/**
	 * Put interested intent's names here.
	 * 
	 * @return The interested intents which will be used to register, look up &
	 *         invoke call-backs.
	 */
	public abstract String[] getIntents();

	/**
	 * No specific implementation. Subclass may want to implement this method to
	 * put actual event processing code here. Beware of the context of the
	 * calling.
	 * 
	 * @param dc
	 * @param intent
	 */
	public void service(AndroidDC dc, Intent intent) {
		// This method has empty body.
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Default implementation will spawn a service to handle the observation.
	 * Subclass may want to override this method in order to modify this default
	 * behavior.
	 * </p>
	 */
	@Override
	public void observed(DC dc, Object observable) {
		Context context = ((AndroidDC)dc).getContext();
		Intent intent = (Intent) observable;
		startService(context, intent);
	}

	@Override
	public Observer getObserver() {
		return this;
	}
	
	/**
	 * Start the watchdog service to handle this event.
	 * 
	 * @param context
	 * @param intent
	 */
	protected void startService(Context context, Intent intent) {
		Intent service = new Intent(context, AndroidWatchdogService.class);
		Bundle extras = intent.getExtras();
		if (extras != null) {
			service.putExtras(extras);
		}
		service.putExtra(AndroidWatchdogService.EXTRA_KEY_OBJECT_HASH_CODE, hashCode());
		context.startService(service);
	}
}