package org.ddth.android.monitor.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ddth.android.monitor.observer.AndroidWatcher;
import org.ddth.mobile.monitor.core.DC;
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
	 * Look up the given {@link #hashCode()} for a watcher in the object pool.
	 * 
	 * @param hashCode
	 * @return
	 */
	public AndroidWatcher getWatcher(Integer hashCode) {
		return pool.get(hashCode);
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
	public void dispatch(DC dc, Object observable) {
		Intent intent = (Intent) observable;
		String action = intent.getAction();
		List<AndroidWatcher> list = observers.get(action);
		if (list == null) {
			return;
		}
		for (AndroidWatcher watcher : list) {
			watcher.observed(dc, observable);
		}
	}

	@Override
	public void clear() {
		pool.clear();
		observers.clear();
		System.gc();
	}

	@Override
	public int size() {
		return pool.size();
	}
}
