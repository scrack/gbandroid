package org.ddth.mobile.monitor.core;

public interface Watcher<T extends Report> extends Observer {
	/**
	 * Add a reporter to this watcher. Client should be
	 * aware of number of reporters supported in order
	 * to register properly.
	 * 
	 * @param reporter
	 */
	void register(Reporter<T> reporter);
	
	/**
	 * Remove the given reporter from the reporter list.
	 *  
	 * @param reporter
	 * @return true if the given reporter is in the list
	 */
	boolean unregister(Reporter<T> reporter);
}
