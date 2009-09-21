/****************************************************
 * $Project: DinoAge                     $
 * $Date:: Jan 1, 2008 7:45:55 PM                  $
 * $Revision: $	
 * $Author:: khoanguyen                           $
 * $Comment::                                      $
 **************************************************/
package org.ddth.http.impl.content;

import java.io.InputStream;

import org.ddth.http.core.content.ContentAdapter;

/**
 * 
 * @author khoa.nguyen
 */
public class InputStreamContent extends ContentAdapter<InputStream> {

	public InputStreamContent(InputStream content) {
		setContent(content);
	}
}
