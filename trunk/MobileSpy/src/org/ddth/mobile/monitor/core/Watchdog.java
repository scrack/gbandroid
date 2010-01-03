package org.ddth.mobile.monitor.core;

/**
 * @author khoanguyen
 */
public interface Watchdog {

	/**
	 * @param observer
	 */
	void register(Observer observer);
	
	/**
	 * @param observer
	 */
	void unregister(Observer observer);
}
