/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 1, 2008 7:45:55 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.core.content;

/**
 * An Adapter of Content :D<br>
 * <br>
 * If you don't know about "Adapter", let me explain it in a short example:<br>
 * <ol>
 * <li>You are a girl and you have to sit during the time taking a piss.</li>
 * <li>I am a boy and I can stand straight while taking a piss.</li>
 * </ol>
 * => If you have water-spout in your hands, you can stand upright to have a
 * piss =))<br>
 * And from that point, you would have known: Yes, water-spout is an Adapter
 * =)).<br>
 * <br>
 * Just joking, forget it! Google Apdater design patterns =)) <br>
 * <br>
 * 
 * @author khoa.nguyen
 * 
 * @param <T>
 *            A generic type for data to be used.
 */
public class ContentAdapter<T> implements Content<T> {

	/**
	 * The quintessence of all data we are trying to process.
	 */
	private T content;

	public void setContent(T content) {
		this.content = content;
	}

	public T getContent() {
		return content;
	}
}
