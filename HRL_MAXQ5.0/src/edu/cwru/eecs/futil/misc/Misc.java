package edu.cwru.eecs.futil.misc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Misc {
	public static String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(Calendar.getInstance().getTime());
	}
	
	public static String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		return dateFormat.format(Calendar.getInstance().getTime());
	}
}
