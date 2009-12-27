package org.ddth.http.impl.connection;

import java.io.File;
import java.util.Map;

import org.ddth.http.core.connection.Request;

public class HttpMultipartRequest extends Request {

	private File file;
	private String field;
	
	public HttpMultipartRequest(String url, String field, Map<String, String> params, File file) {
		super(url, params);
		this.file = file;
		this.field = field;
	}
	
	public File getFile() {
		return file;
	}

	public String getFieldName() {
		return field;
	}
}