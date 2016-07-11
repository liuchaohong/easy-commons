package com.github.easy.commons.util.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;

public class DateUtil {

	public static String dateToStr(Date date, String pattern){
		String dateStr = DateFormatUtils.format(date, pattern);
		return dateStr;
	}
	
	public static Date strToDate(String dateStr, String pattern){
		Date date = null;
		try {
			date = new SimpleDateFormat(pattern).parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static String strToStr(String dateStr, String fromPattern, String toPattern){
		String toStr = "";
		try {
			Date date = new SimpleDateFormat(fromPattern).parse(dateStr);
			toStr = DateFormatUtils.format(date, toPattern);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return toStr;
	}
	
}
