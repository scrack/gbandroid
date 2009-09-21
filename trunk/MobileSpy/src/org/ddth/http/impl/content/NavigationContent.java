/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.impl.content;


/**
 * This type of content is used to store all the navigation links in a webpage.
 * Very useful for a crawler, grabber... Google bot might be using this idea of
 * implementation as well =)).<br>
 * <br>
 * 
 * @author khoa.nguyen
 * 
 */
public class NavigationContent extends DomTreeContent {

	/**
	 * List of all urls in the content.
	 */
	private String[] urls;

	/**
	 * Construct a navigation object with the given URLs
	 * 
	 * @param urls
	 *            The urls that can be used to travel next...
	 */
	public NavigationContent(DomTreeContent content, String[] urls) {
		super(content.getContent(), content.getDocument());
		this.urls = urls;
	}

	/**
	 * Just return what it's holding...
	 * 
	 * @return The list of urls.
	 */
	public String[] getNextURLs() {
		return urls;
	}
}
