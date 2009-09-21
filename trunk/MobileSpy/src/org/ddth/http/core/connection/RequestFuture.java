/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 27, 2008 2:14:06 AM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.core.connection;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A simple wrapper of a {@link Future} object from Sun. Hey, you've got the
 * Adapter design patterns in this sh!t again =))
 * 
 * @author khoa.nguyen
 * 
 */
public class RequestFuture implements Future<Response> {

	/**
	 * Delegated {@link Future} object.
	 */
	private Future<Response> future;

	/**
	 * Create a wrapper of the given future object.
	 * 
	 * @param future
	 *            The future object to be wrapped.
	 */
	public RequestFuture(Future<Response> future) {
		this.future = future;
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		return future.cancel(mayInterruptIfRunning);
	}

	public Response get() throws InterruptedException, ExecutionException {
		return future.get();
	}

	public Response get(long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		return future.get(timeout, unit);
	}

	public boolean isCancelled() {
		return future.isCancelled();
	}

	public boolean isDone() {
		return future.isDone();
	}
}
