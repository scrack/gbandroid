package org.ddth.mobile.monitor.report;

import java.io.File;

import org.ddth.mobile.monitor.core.Report;

public class Media implements Report {
	public File file;
	public String type;
	
	public Media(File file, String type) {
		this.file = file;
		this.type = type;
	}
}
