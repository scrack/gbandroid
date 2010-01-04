package org.ddth.mobile.monitor.core;

public interface Observer {
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
	 * Get notified by the monitor to which this observer
	 * registered.
	 *  
	 * @param observable The observable object 
	 */
	void observed(Object observable);

}
