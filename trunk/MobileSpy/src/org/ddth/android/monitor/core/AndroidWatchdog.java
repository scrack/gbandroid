package org.ddth.android.monitor.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ddth.mobile.monitor.core.Observer;
import org.ddth.mobile.monitor.core.Watchdog;

import android.content.Context;
import android.content.Intent;

/**
 * @author khoanguyen
 */
public class AndroidWatchdog implements Watchdog {
	private Map<String, List<Observer>> observers = new HashMap<String, List<Observer>>();

	public void observe(Context context, Intent intent) {
		String action = intent.getAction();
		List<Observer> list = observers.get(action);
		for (Observer watcher : list) {
			watcher.observed(intent);
		}
	}

	@Override
	public void register(Observer receiver) {
		String[] intents = ((AndroidReceiver)receiver).getIntents();
		for (String action : intents) {
			List<Observer> list = observers.get(action);
			if (list == null) {
				list = new ArrayList<Observer>();
				observers.put(action, list);
			}
			list.add(receiver);
		}
	}

	@Override
	public void unregister(Observer receiver) {
		String[] intents = ((AndroidReceiver)receiver).getIntents();
		for (String action : intents) {
			List<Observer> list = observers.get(action);
			if (list != null) {
				list.remove(receiver);
			}
		}
	}
}
