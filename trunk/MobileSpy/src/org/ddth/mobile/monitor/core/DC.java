package org.ddth.mobile.monitor.core;

/**
 * Mobile Device Context
 * 
 * @author khoanguyen
 *
 */
public interface DC {
	
	/**
	 * Get the watchdog object for registering/unregistering new observers
	 * 
	 * @return
	 */
	Watchdog getWatchdog();
}
