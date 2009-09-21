/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 5, 2008 1:36:31 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.impl.content;

import java.io.InputStream;

import org.w3c.dom.Document;

/**
 * This is what we have after parsing the low level content in stream. We should
 * represent it in an efficient form, and no doubt, it should be a DOM tree.
 * Later on, we could use XPath or all kind of things to extract its data very
 * easily. I refer using XPath to other methods because it's very easy to access
 * a node from, just a simple string from Firebug, an XPath string.<br>
 * <br>
 * 
 * @author khoa.nguyen
 * 
 */
public class DomTreeContent extends InputStreamContent {

	/**
	 * I don't know :((
	 */
	private Document document;
	
	/**
	 * Create a content object.
	 * 
	 * @param doc
	 * 		The document root of the DOM tree. 
	 */
	public DomTreeContent(InputStream inputStream, Document doc) {
		super(inputStream);
		document = doc;
	}
	
	/**
	 * Get the document node in the DOM tree.<br>
	 * 
	 * @return
	 * 		The document node of the DOM tree. 
	 */
	public Document getDocument() {
		return document;
	}
}
