package org.ddth.mobile.monitor.core;

public abstract class WatcherAdapter<T extends Reporter> implements Watcher<T> {
	protected T reporter;

	@Override
	public void watch(DC dc, int state) {
		Watchdog watchdog = dc.getWatchdog();
		if (state == Watcher.START_MONITORING) {
			watchdog.register(this);
		}
		else {
			watchdog.unregister(this);
		}
	}
	
	@Override
	public void register(T reporter) {
		this.reporter = reporter;
	}

	@Override
	public boolean unregister(T reporter) {
		this.reporter = null;
		return false;
	}
}
