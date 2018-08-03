package com.social.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.Interval;
import org.joda.time.Period;

public class DateTimeUtils {
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	private DateTimeUtils(){}
	
	public static String getDisplayTime(Date p_date, Date date){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		Interval interval = new Interval(p_date.getTime(), date.getTime());
	    Period period = interval.toPeriod();
	      
	    
	    String str_p_data_date = simpleDateFormat.format(p_date).split(" ")[0];
	    String str_p_data_time = simpleDateFormat.format(p_date).split(" ")[1];
		String str_date_date = simpleDateFormat.format(date).split(" ")[0];
		String str_date_time = simpleDateFormat.format(date).split(" ")[1];
		
		int result;
		if(!str_p_data_date.split("-")[0].equals(str_date_date.split("-")[0])){
			result= Integer.valueOf(str_date_date.split("-")[0]) - Integer.valueOf(str_p_data_date.split("-")[0]);
			return result +"年前";
		}else{
			if(!str_p_data_date.split("-")[1].equals(str_date_date.split("-")[1])){
				result= Integer.valueOf(str_date_date.split("-")[1]) - Integer.valueOf(str_p_data_date.split("-")[1]);
				return result +"个月前";
			}else{
				if(!str_p_data_date.split("-")[2].equals(str_date_date.split("-")[2])){
					result= Integer.valueOf(str_date_date.split("-")[2]) - Integer.valueOf(str_p_data_date.split("-")[2]);
					return result +"天前";
				}else{
					if(!str_p_data_time.split(":")[0].equals(str_date_time.split(":")[0])){
						result= Integer.valueOf(str_date_time.split(":")[0]) - Integer.valueOf(str_p_data_time.split(":")[0]);
						return result +"小时前";
					}else{
						if(!str_p_data_time.split(":")[1].equals(str_date_time.split(":")[1])){
							result= Integer.valueOf(str_date_time.split(":")[1]) - Integer.valueOf(str_p_data_time.split(":")[1]);
							return result +"分钟前";
						}else{
							if(!str_p_data_time.split(":")[2].equals(str_date_time.split(":")[2])){
								result= Integer.valueOf(str_date_time.split(":")[2]) - Integer.valueOf(str_p_data_time.split(":")[2]);
								return result +"秒前";
							}
							else return "";
						}
					}
				}
			}
		}
	      
//	    System.out.printf(
//	              "%d years, %d months, %d days, %d hours, %d minutes, %d seconds%n", 
//	              period.getYears(), period.getMonths(), period.getDays(),
//	              period.getHours(), period.getMinutes(), period.getSeconds());
	}
	
	
	public static String getTime(String old_time, String new_time){
		String login_time = getLoginTimeWithMinutes(old_time, new_time);
		//System.out.println( "login_time = " + login_time);
		int int_login_time = Integer.valueOf(login_time);
		//System.out.println( "int_login_time = " + int_login_time);
		if(int_login_time > 60){
			login_time = getLoginTimeWithHours(old_time, new_time);
			int_login_time = Integer.valueOf(login_time);
			if(int_login_time > 24){
				login_time = getLoginTimeWithDays(old_time, new_time);
				int_login_time = Integer.valueOf(login_time);
				if(int_login_time > 30){
					login_time = getLoginTimeWithMonth(old_time, new_time);
					int_login_time = Integer.valueOf(login_time); 
					return login_time + "个月前";
				}
				return login_time + "天前"; 
			}else{
				return login_time + "小时前";
			}
		}else{
			return login_time + "分钟前";
		}
		
	}
	
	public static String getTime2(String old_time, String new_time){
		String login_time = getLoginTimeWithMinutes(old_time, new_time);
		//System.out.println( "login_time = " + login_time);
		int int_login_time = Integer.valueOf(login_time);
		//System.out.println( "int_login_time = " + int_login_time);
		if(int_login_time > 60){
			//login_time = getLoginTimeWithHours(old_time, new_time);
			int_login_time = int_login_time / 60;
			if(int_login_time > 24){
				int_login_time = int_login_time /24;
				if(int_login_time > 30){
					int_login_time = int_login_time / 30;
					return int_login_time + "个月前";
				}
				return int_login_time + "天前"; 
			}else{
				return int_login_time + "小时前";
			}
		}else{
			return int_login_time + "分钟前";
		}
		
	}
	
	public static String getLoginTimeWithMinutes(String old_time, String new_time){
		String login_time = "";
		long from;
		long to;
		try {
			from = simpleDateFormat.parse(old_time).getTime();
			//System.out.println("获取分钟数：form = " + String.valueOf(from));
			to = simpleDateFormat.parse(new_time).getTime();  
			//System.out.println("获取分钟数：form = " + String.valueOf(to));
			int minutes = (int) ((to - from)/(1000 * 60)); 
			login_time = minutes + "";
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		return login_time;
	}
	
	public static String getLoginTimeWithHours(String old_time, String new_time){
		String login_time = "";
		long from;
		long to;
		try {
			from = simpleDateFormat.parse(old_time).getTime();
			to = simpleDateFormat.parse(new_time).getTime();  
			int hours = (int) ((to - from)/(1000 * 60 * 60)); 
			login_time = hours + "";
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		return login_time;
	}
	
	public static String getLoginTimeWithDays(String old_time, String new_time){
		String login_time = "";
		long from;
		long to;
		try {
			from = simpleDateFormat.parse(old_time).getTime();
			to = simpleDateFormat.parse(new_time).getTime();  
			int days = (int) ((to - from)/(1000 * 60 * 60 * 24)); 
			login_time = days + "";
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return login_time;
	}
	
	public static String getLoginTimeWithMonth(String old_time, String new_time){
		String login_time = "";
		long from;
		long to;
		try {
			from = simpleDateFormat.parse(old_time).getTime();
			to = simpleDateFormat.parse(new_time).getTime();  
			System.out.println("from = " + from);
			System.out.println("to = " + to);
			long month = (long) ((to - from)/(1000 * 60 * 60 * 24 * 30)); 
			System.out.println("to -  from = " + to + "- " + from + "= " + month);
			login_time = month + "";
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return login_time;
	}
	

}
