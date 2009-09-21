/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.core;


import org.ddth.http.core.connection.Request;
import org.ddth.http.core.connection.Response;

/**
 * Just a simple object that stores information about connection event. When
 * something happened, an event is fired in order to send notification to all of
 * its listeners about the changes.<br>
 * <br>
 * You can get the original {@link Request} or {@link Response} objects from
 * this event and do your own processing with them.<br>
 * <br>
 * However, in some case, the {@link Response} object might be null because the
 * request hasn't been executed. This is okay because I think that you hardly
 * access it. In fact, it was here for internally used, and again, you won't
 * need it.<br>
 * 
 * @author khoa.nguyen
 */
public class ConnectionEvent {

	/**
	 * The request is being made.
	 */
	public static final int REQUEST_INITIATED = 0;
	/**
	 * The response has been retrieved.
	 */
	public static final int RESPONSE_RECEIVED = 1;
	/**
	 * The request is closed.
	 */
	public static final int REQUEST_FINISHED = 2;
	
	private int eventType;
	private Request request;
	private Response response;

	/**
	 * Construct the event with its data.
	 * 
	 * @param request
	 *            The request that generates this event.
	 * @param response
	 *            The response that is being processing.
	 */
	public ConnectionEvent(int eventType, Request request, Response response) {
		this.eventType = eventType;
		this.request = request;
		this.response = response;
	}

	/**
	 * The given request has just been made, no response received.<br>
	 * It's easy! Add one more line of comment for increasing the commenting
	 * percentage per LOC =)).
	 * 
	 * @param request
	 *            The request that generates this event.
	 */
	public ConnectionEvent(Request request) {
		this(REQUEST_INITIATED, request, null);
	}

	/**
	 * Get event type.
	 * 
	 * @see
	 * {@link #REQUEST_INITIATED}<br>
	 * {@link #RESPONSE_RECEIVED}<br>
	 * {@link #REQUEST_DONE}<br>
	 * @return
	 */
	public int getEventType() {
		return eventType;
	}
	
	/**
	 * Simple get. No set :D
	 * 
	 * @return The request that generates this event.
	 */
	public Request getRequest() {
		return request;
	}

	/**
	 * Simple get. No set :D
	 * 
	 * @return The response that was generated. It might be null.
	 */
	public Response getResponse() {
		return response;
	}
}
