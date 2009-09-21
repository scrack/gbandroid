/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.core.content;

/**
 * Simple content interface.
 * 
 * @author khoa.nguyen
 * 
 * @param <T>
 */
public interface Content<T> {

	/**
	 * Save the content for other purposes.
	 * 
	 * @param content
	 *            The content to be saved.
	 */
	public void setContent(T content);

	/**
	 * Get the content :D (very silly comment =)))
	 * 
	 * @return The content inside this content :D
	 */
	public T getContent();

}