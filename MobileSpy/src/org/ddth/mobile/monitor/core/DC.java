package org.ddth.mobile.monitor.core;

/**
 * Mobile Device Context
 * 
 * @author khoanguyen
 *
 */
public interface DC {

	Watchdog getWatchdog();

	Object getPlatformContext();
}
