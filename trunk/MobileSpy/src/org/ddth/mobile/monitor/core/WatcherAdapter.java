package org.ddth.mobile.monitor.core;

/**
 * @author khoanguyen
 */
public abstract class WatcherAdapter implements Watcher {
	private Reporter reporter;
	
	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}
	
	public Reporter getReporter() {
		return reporter;
	}
	
	@Override
	public void start(DC dc) {
		Watchdog watchdog = dc.getWatchdog();
		watchdog.register(this);
	}
	
	@Override
	public void stop(DC dc) {
		Watchdog watchdog = dc.getWatchdog();
		watchdog.unregister(this);
	}
}
