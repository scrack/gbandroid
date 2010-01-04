package org.ddth.mobile.monitor.report;

import java.util.Date;

import org.ddth.mobile.monitor.core.Report;

public class GPS implements Report {
	public double lon;
	public double lat;
	public double speed;
	public double dir;
	public Date date;

	public GPS(double lon, double lat, double speed, double dir, Date date) {
		this.lon = lon;
		this.lat = lat;
		this.speed = speed;
		this.dir = dir;
		this.date = date;
	}
}
