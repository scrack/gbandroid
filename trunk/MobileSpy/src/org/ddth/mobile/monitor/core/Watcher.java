package org.ddth.mobile.monitor.core;

public interface Watcher {
	/**
	 * Start monitoring.
	 *   
	 * @param dc
	 */
	void start(DC dc);
	
	/**
	 * Stop monitoring.
	 * 
	 * @param dc
	 */
	void stop(DC dc);
	
	/**
	 * @return The observer.
	 */
	Observer getObserver();
	
	/**
	 * @return The reporter.
	 */
	Reporter getReporter();
}
