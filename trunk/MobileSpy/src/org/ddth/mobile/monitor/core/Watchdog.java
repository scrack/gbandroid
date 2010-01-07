package org.ddth.mobile.monitor.core;

/**
 * @author khoanguyen
 */
public interface Watchdog extends Observer {

	/**
	 * @param observer
	 */
	void register(Observer observer);
	
	/**
	 * @param observer
	 */
	void unregister(Observer observer);
}
