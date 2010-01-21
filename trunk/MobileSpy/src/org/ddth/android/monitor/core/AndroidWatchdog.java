package org.ddth.android.monitor.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ddth.android.monitor.observer.AndroidWatcher;
import org.ddth.mobile.monitor.core.Event;
import org.ddth.mobile.monitor.core.Observer;
import org.ddth.mobile.monitor.core.Watchdog;

import android.content.Intent;

/**
 * This class implements a watchdog-like service and also keeps an object pool
 * which holds all {@link AndroidWatcher} instances in the system for easy to
 * share objects among Android services, activities, receivers...
 * 
 * @see Watchdog
 * @author khoanguyen
 */
public class AndroidWatchdog implements Watchdog {
	private Map<String, List<AndroidWatcher>> observers = new HashMap<String, List<AndroidWatcher>>();
	private Map<Object, AndroidWatcher> pool = new HashMap<Object, AndroidWatcher>();

	/**
	 * Look in the object pool for a watcher that has the given
	 * {@link #hashCode()}.
	 * 
	 * @param hashCode
	 * @return
	 */
	public AndroidWatcher getWatcher(Integer hashCode) {
		return pool.get(hashCode);
	}

	/**
	 * Start all watchers...
	 * 
	 * @param event
	 */
	public void start(Event event) {
		for (AndroidWatcher watcher : pool.values()) {
			watcher.start(event);
		}
	}
	
	@Override
	public void register(Observer observer) {
		AndroidWatcher watcher = (AndroidWatcher)observer;
		String[] intents = watcher.getIntents();
		for (String action : intents) {
			List<AndroidWatcher> list = observers.get(action);
			if (list == null) {
				list = new ArrayList<AndroidWatcher>();
				observers.put(action, list);
			}
			list.add(watcher);
		}
		pool.put(new Integer(watcher.hashCode()), watcher);
	}

	@Override
	public void unregister(Observer observer) {
		AndroidWatcher watcher = (AndroidWatcher)observer;
		String[] intents = watcher.getIntents();
		for (String action : intents) {
			List<AndroidWatcher> list = observers.get(action);
			if (list != null) {
				list.remove(observer);
			}
		}
	}

	@Override
	public void watch(Event event) {
		Intent intent = ((AndroidEvent)event).getIntent();
		String action = intent.getAction();
		List<AndroidWatcher> list = observers.get(action);
		if (list == null) {
			return;
		}
		for (AndroidWatcher watcher : list) {
			watcher.observed(event);
		}
	}

	@Override
	public void clear() {
		pool.clear();
		observers.clear();
		System.gc();
	}
}
