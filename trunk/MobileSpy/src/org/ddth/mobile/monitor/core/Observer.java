package org.ddth.mobile.monitor.core;

public interface Observer {
	/**
	 * Get notified by the monitor to which this observer
	 * registered.
	 * 
	 * @param dc A device context
	 * @param observable The observable object 
	 */
	void observed(DC dc, Object observable);

}
