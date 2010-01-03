package org.ddth.mobile.monitor.core;

public interface Tracker<T extends Executor> extends Observer {

	/**
	 * Start monitoring
	 *   
	 * @param context
	 */
	void start(Object context);
	
	/**
	 * Stop monitoring
	 *   
	 * @param context
	 */
	void stop(Object context);
	
	/**
	 * Add a executor to this watcher. Client should be
	 * aware of number of executors supported in order
	 * to register properly.
	 * 
	 * @param executor
	 */
	void register(T executor);
	
	/**
	 * Remove the given executor from the executor list.
	 *  
	 * @param executor
	 * @return true if the given executor is in the list
	 */
	boolean unregister(T executor);
}
