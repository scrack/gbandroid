/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.core;

/**
 * Listen to the request events. Wow, it's fantastic, like the singing of a
 * nightingale =)) I am a coder bie^'t la`m tho* =))
 * 
 * @author khoa.nguyen
 * 
 */
public interface ConnectionListener {

	/**
	 * Notify about the status of a request is being made.
	 * 
	 * @param event
	 *            The connection event
	 */
	public void notifyEvent(ConnectionEvent event);
}