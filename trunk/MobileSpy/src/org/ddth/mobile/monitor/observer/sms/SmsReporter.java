package org.ddth.mobile.monitor.observer.sms;

import java.util.Date;

import org.ddth.mobile.monitor.core.Reporter;

public abstract class SmsReporter implements Reporter {
	public static class SMS {
		public String from;
		public String to;
		public Date date;
		public String message;

		public SMS(String from, String to, String message, Date date) {
			this.from = from;
			this.to = to;
			this.message = message;
			this.date = date;
		}
	}
	
	public abstract void report(SMS sms);
}
