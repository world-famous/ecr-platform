package com.tianwen.springcloud.ecrapi.util;

import java.util.*;
import java.text.*;

/**
 *
 * @author rebel
 */
public class DateTimeUtil {
	/** Creates a new instance of DateUtil */
	public DateTimeUtil() {
	}

	/**
	 * @param days
	 * @return 
	 */
	public static String getDate(int days) {
		String str = null;

		try {
			Calendar cal = new GregorianCalendar();
			cal.add(Calendar.DATE, days);

			Date date = cal.getTime();

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy'-'MM'-'dd");
			str = simpleDateFormat.format(date);

		} catch(Exception e) { 
		}

		return str;
	}

	/**
	 * @param days
	 * @return 
	 */
	public static String getDateTime(int days) {
		String str = null;

		try {
			Calendar cal = new GregorianCalendar();
			cal.add(Calendar.DATE, days);

			Date date = cal.getTime();

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy'/'MM'/'dd' 'HH':'mm':'ss");
			str = simpleDateFormat.format(date);

		} catch(Exception e) { 
		}

		return str;
	}

	public static String getDate()
	{
		String str = null;

		try {
			TimeZone timeZone = new SimpleTimeZone( 9 * 60 * 60 * 1000, "PRC" );
			TimeZone.setDefault(timeZone);

			Date date = new Date();

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy'/'MM'/'dd");
			str = simpleDateFormat.format(date);
		} catch(Exception e) { 
		}

		return str;
	}

	public static java.util.Date stringToDate(String str, String format)
	{
		java.util.Date date = null;

		try
		{
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
			date = simpleDateFormat.parse(str);
		} catch(Exception e) { 
		}

		return date;
	}

	public static String dateFormat(String format)
	{
		String str = null;

		try {
			TimeZone timeZone = new SimpleTimeZone( 9 * 60 * 60 * 1000, "PRC" );
			TimeZone.setDefault(timeZone);
			Date date = new Date();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
			str = simpleDateFormat.format(date);
		} catch(Exception e) { 
		}

		return str;
	}

	public static String getDateTime(){
		String str = null;

		try {
			TimeZone timeZone = new SimpleTimeZone( 9 * 60 * 60 * 1000, "PRC" );
			TimeZone.setDefault(timeZone);

			Date date = new Date();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy'/'MM'/'dd' 'HH':'mm':'ss");
			str = simpleDateFormat.format(date);
		} catch(Exception e) {
		}
		return str;
	}

	public static String getCurrentDate(){
		String str = null;

		try {
			TimeZone timeZone = new SimpleTimeZone( 9 * 60 * 60 * 1000, "PRC" );
			TimeZone.setDefault(timeZone);

			Date date = new Date();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy'/'MM'/'dd");
			str = simpleDateFormat.format(date);
		} catch(Exception e) {
		}
		return str;
	}

	public static String getDateTimeMS(){
		String str = null;

		try {
			TimeZone timeZone = new SimpleTimeZone( 9 * 60 * 60 * 1000, "PRC" );
			TimeZone.setDefault(timeZone);

			Date date = new Date();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy'-'MM'-'dd' 'HH':'mm':'ss':'SSS");
			str = simpleDateFormat.format(date);
		} catch(Exception e) {
		}
		return str;
	}

	public static String getUniqueTime(){
		String str = null;

		try {
			TimeZone timeZone = new SimpleTimeZone( 9 * 60 * 60 * 1000, "PRC" );
			TimeZone.setDefault(timeZone);
			Date date = new Date();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			str = simpleDateFormat.format(date);
		} catch(Exception e){
		}

		return str;
	}

	public static int getDifferenceOfDate(String beginDate, String endDate) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

			Date _beginDate = formatter.parse(beginDate);
			Date _endDate = formatter.parse(endDate);

			long diff = _endDate.getTime() - _beginDate.getTime();
			long diffDays = diff / (24 * 60 * 60 * 1000);

			return (int)diffDays;

		} catch(Exception e) {
			e.printStackTrace();
		}

		return -99999;
	}

}
