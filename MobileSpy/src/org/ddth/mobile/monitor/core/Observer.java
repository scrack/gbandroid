package org.ddth.mobile.monitor.core;

public interface Observer {
	
	/**
	 * Get notified by the monitor to which this observer
	 * registered.
	 *  
	 * @param observable The observable object 
	 */
	void observed(Object observable);

}
