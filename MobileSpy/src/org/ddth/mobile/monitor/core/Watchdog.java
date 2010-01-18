package org.ddth.mobile.monitor.core;

/**
 * A class to maintain the registration of all observers.  
 * 
 * @author khoanguyen
 */
public interface Watchdog {

	/**
	 * Dispatch the observable data to the registered observers. 
	 * 
	 * @param dc
	 * @param observable
	 */
	void dispatch(DC dc, Object observable);
	
	/**
	 * Register an observer.
	 * 
	 * @param observer
	 */
	void register(Observer observer);
	
	/**
	 * Remove an observer from registration.
	 * 
	 * @param observer
	 */
	void unregister(Observer observer);

	/**
	 * Unregister all observers
	 */
	void clear();
	
	/**
	 * @return Number of managed observers.
	 */
	int size();
}
