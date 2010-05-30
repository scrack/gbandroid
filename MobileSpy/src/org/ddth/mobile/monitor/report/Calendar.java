package org.ddth.mobile.monitor.report;

import java.util.Date;

import org.ddth.mobile.monitor.core.Report;

public class Calendar implements Report {
	public Date date;
	public String start;
	public String end;
	public String title;
	public String description;
	public String location;

	public Calendar(Date date, String start, String end, String title, String description, String location) {
		this.date = date;
		this.start = start;
		this.end = end;
		this.title = title;
		this.description = description;
		this.location = location;
	}
}
