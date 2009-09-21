/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.core.connection;


/**
 * A transportation service which supports client to make any requests to a
 * remote server by giving it a detailed request information.<br>
 * <br>
 * Any implementation of this class should provide non-blocking call mechanism
 * so the client doesn't have to wait for the request made by the underlying
 * layer.<br>
 * <br>
 * 
 * @author khoa.nguyen
 * 
 */
public interface ConnectionModel {

	/**
	 * Send a request to server and return. This is a non blocking call.
	 * 
	 * @param request
	 *            The request to be sent.
	 * @return A RequestFuture object for client to cancel or collect result.
	 */
	public RequestFuture sendRequest(Request request);

	/**
	 * Start running its engine.
	 */
	public void open();

	/**
	 * Check if the connection is running (opened).
	 * 
	 * @return true if the connection is already opened.
	 */
	public boolean running();

	/**
	 * Close the connection. Other invocation has no effect if the connection
	 * was closed. Open it again before making any requests.
	 */
	public void close();

}