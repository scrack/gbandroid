package org.ddth.mobile.monitor.report;

import java.util.Date;

import org.ddth.mobile.monitor.core.Report;

public class Email implements Report {
	public String from;
	public String to;
	public String subject;
	public String body;
	public Date date;

	public Email(String from, String to, String subject, String body, Date date) {
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.body = body;
		this.date = date;
	}
}
