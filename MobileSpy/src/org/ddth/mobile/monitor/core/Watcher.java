package org.ddth.mobile.monitor.core;

public interface Watcher extends Observer {
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
}
