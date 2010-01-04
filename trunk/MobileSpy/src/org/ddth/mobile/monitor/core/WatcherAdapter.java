package org.ddth.mobile.monitor.core;


public abstract class WatcherAdapter<T extends Report> implements Watcher<T> {
	protected Reporter<T> reporter;

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
	
	/**
	 * @param observable
	 * @return
	 */
	protected abstract T getReport(Object observable);
	
	@Override
	public void observed(Object observable) {
		T report = getReport(observable);
		if (report != null) {
			reporter.report(report);
		}
	}

	@Override
	public void register(Reporter<T> reporter) {
		this.reporter = reporter;
	}

	@Override
	public boolean unregister(Reporter<T> reporter) {
		return false;
	}
}
