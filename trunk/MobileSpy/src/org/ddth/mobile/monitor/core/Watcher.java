package org.ddth.mobile.monitor.core;

public interface Watcher<T extends Reporter> extends Observer {
	public static final int START_MONITORING = 0;
	public static final int STOP_MONITORING = 1;
	public static final int USER_DEFINED_MONITORING = 2;
	
	/**
	 * Start/Stop monitoring the given context.
	 *   
	 * @param dc
	 */
	void watch(DC dc, int state);

	/**
	 * Add a reporter to this watcher. Client should be
	 * aware of number of reporters supported in order
	 * to register properly.
	 * 
	 * @param reporter
	 */
	void register(T reporter);
	
	/**
	 * Remove the given reporter from the reporter list.
	 *  
	 * @param reporter
	 * @return true if the given reporter is in the list
	 */
	boolean unregister(T reporter);
}
