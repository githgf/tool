package cn.hans.common.utils;

import com.alibaba.fastjson.JSON;
import cn.hans.common.constant.CommonConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 日期工具类, 继承org.apache.commons.lang.time.DateUtils类
 * 
 * @author hans
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

	private static String[] parsePatterns = { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
			"yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM", "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss",
			"yyyy.MM.dd HH:mm", "yyyy.MM" };

	/**日期格式 年月日*/
	public static final String YMD_PATTERN = "yyyy-MM-dd";
	/**日期格式 年月日 时分*/
	public static final String YMDHM_PATTERN = "yyyy-MM-dd HH:mm";
	/**日期格式 年月日 时分秒*/
	public static final String YMDHMS_PATTERN = "yyyy-MM-dd HH:mm:ss";
	/**风信日期格式 年/月/日/时:分:秒*/
	public static final String WINDBASE_YMDHMS = "yyyy/MM/dd/HH:mm:ss";

	public static final String YMDHMS_T = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd）
	 */
	public static String getDate() {
		return getDate("yyyy-MM-dd");
	}

	/**
	 * 获取指定日期的零点
	 * @param day		日期
	 * @return			零点的时刻
	 */
	public static Date getStartTime(Date day) {
		Calendar todayStart = Calendar.getInstance();
		todayStart.setTime(day == null ? new Date() : day);
		todayStart.set(Calendar.HOUR, 0);
		todayStart.set(Calendar.MINUTE, 0);
		todayStart.set(Calendar.SECOND, 0);
		todayStart.set(Calendar.MILLISECOND, 0);
		return todayStart.getTime();
	}

	/**
	 * 获取指定日期的最后一个时间
	 * @return			最后时刻	23:59:59
	 */
	public static Date getEndTime(Date day) {
		Calendar todayEnd = Calendar.getInstance();
		todayEnd.setTime(day == null ? new Date() : day);
		todayEnd.set(Calendar.HOUR, 23);
		todayEnd.set(Calendar.MINUTE, 59);
		todayEnd.set(Calendar.SECOND, 59);
		todayEnd.set(Calendar.MILLISECOND, 999);
		return todayEnd.getTime();
	}

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String getDate(String pattern) {
		return DateFormatUtils.format(new Date(), pattern);
	}

	/**
	 * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String formatDate(Date date, Object... pattern) {
		String formatDate = null;
		if (pattern != null && pattern.length > 0) {
			formatDate = DateFormatUtils.format(date, pattern[0].toString());
		} else {
			formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
		}
		return formatDate;
	}

	/**
	 * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String formatDateTime(Date date) {
		if (date == null)
			return "";
		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 	返回传入时间的前后一天，比如说传入 2018-09-02 05：20：00 返回===>  2018-09-02 00：00：00 			2018-09-03 00：00：00
	 */
	public static Map<String,Date> getBetweenDate(Date date){

		if (date == null){
			return null;
		}

		Map<String,Date> dateMap = new LinkedHashMap<>();

		Date startDate = parseDay(formatDate(date, YMD_PATTERN));
		dateMap.put("start",startDate);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.add(Calendar.DAY_OF_MONTH,1);

		dateMap.put("end",startDate);

		return dateMap;
	}

	/**
	 * 得到当前时间字符串 格式（HH:mm:ss）
	 */
	public static String getTime() {
		return formatDate(new Date(), "HH:mm:ss");
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String getDateTime() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前年份字符串 格式（yyyy）
	 */
	public static String getYear() {
		return formatDate(new Date(), "yyyy");
	}

	/**
	 * 得到当前月份字符串 格式（MM）
	 */
	public static String getMonth() {
		return formatDate(new Date(), "MM");
	}

	/**
	 * 得到当天字符串 格式（dd）
	 */
	public static String getDay() {
		return formatDate(new Date(), "dd");
	}

	/**
	 * 得到当前星期字符串 格式（E）星期几
	 */
	public static String getWeek() {
		return formatDate(new Date(), "E");
	}

	/**
	 * 得到当前小时
	 * @return
	 */
	public static String getHour() {
		return formatDate(new Date(),"HH");
	}

	/**
	 * 日期型字符串转化为日期 格式 { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
	 * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy.MM.dd",
	 * "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
	 */
	public static Date parseDate(Object str) {
		if (str == null) {
			return null;
		}
		try {
			return parseDate(str.toString(), parsePatterns);
		} catch (ParseException e) {
			return null;
		}
	}

	public static Date getToday(){
		return getStartTime(null);
	}


	public static Date getToday5AM(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 5);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date getTodayTime(int startTime){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, startTime);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}


	/**
	 * 获取过去的天数
	 * 
	 * @param date
	 * @return
	 */
	public static long pastDays(Date date) {
		long t = new Date().getTime() - date.getTime();
		return t / (24 * 60 * 60 * 1000);
	}

	/**
	 * 获取过去的小时
	 * 
	 * @param date
	 * @return
	 */
	public static long pastHour(Date date) {
		long t = new Date().getTime() - date.getTime();
		return t / (60 * 60 * 1000);
	}

	/**
	 * 获取过去的分钟
	 * 
	 * @param date
	 * @return 算出负数的时候坑爹的很
	 */
	@Deprecated
	public static long pastMinutes(Date date) {
		long t = new Date().getTime() - date.getTime();
		return t / (60 * 1000);
	}

	/**
	 * 分种差
	 * 
	 * @param before
	 * @param after
	 * @return
	 */
	public static int diffMinute(Date before, Date after) {
		return (int) (after.getTime() - before.getTime()) / 1000 / 60;
	}
	
	/**
	 * 分种差
	 * 
	 * @param before
	 * @return
	 */
	public static int diffMinute(Date before) {
		return (int) (System.currentTimeMillis() - before.getTime()) / 1000 / 60;
	}

	/**
	 * 转换为时间（天,时:分:秒.毫秒）
	 * 
	 * @param timeMillis
	 * @return
	 */
	public static String formatDateTime(long timeMillis) {
		long day = timeMillis / (24 * 60 * 60 * 1000);
		long hour = (timeMillis / (60 * 60 * 1000) - day * 24);
		long min = ((timeMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long s = (timeMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		long sss = (timeMillis - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);
		return (day > 0 ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
	}

	/**
	 * 获取两个日期之间的天数
	 * 
	 * @param before
	 * @param after
	 * @return
	 */
	public static double getDistanceOfTwoDate(Date before, Date after) {
		long beforeTime = before.getTime();
		long afterTime = after.getTime();
		return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
	}

	public static Date parseDate(String str) {
		if (str == null || str.equalsIgnoreCase(""))
			return null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date rt = null;
		try {
			rt = simpleDateFormat.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return rt;
	}

	public static String formatDateTime(Date time, String pattern,TimeZone timeZone) {
		if (time == null)
			return null;
		SimpleDateFormat simpleDateFormat = null;
		if (StringUtils.isBlank(pattern)){
			simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}else {
			simpleDateFormat = new SimpleDateFormat(pattern);
		}
		if (timeZone != null){
			simpleDateFormat.setTimeZone(timeZone);
		}
		String rt = null;
		rt = simpleDateFormat.format(time);
		return rt;
	}

	public static Date parseDay(String str) {
		if (str == null || str.equalsIgnoreCase(""))
			return null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date rt = null;
		try {
			rt = simpleDateFormat.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return rt;
	}

	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {

		String s = formatDateTime(new Date(1536510763000L), null, JSON.defaultTimeZone);
		System.out.println(s);

	}

	public static Date GMT2CSTDate(Date GMTDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(GMTDate);
		calendar.add(Calendar.HOUR, 8);
		return calendar.getTime();
	}

	public static Long GMT2CSTLong(Date GMTDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(GMTDate);
		calendar.add(Calendar.HOUR, 8);
		return calendar.getTime().getTime();
	}

	/**
	 * utc时间转换为Date时间
	 * 
	 * @param utcTime
	 * @return
	 */
	public static Date convertToDate(String utcTime) {
		try {

			String timeZone;
			if (!utcTime.endsWith("Z")) {
				// 末尾不包含Z,则不需要减去8小时
				utcTime += "Z";
				timeZone = "+0800";
			} else {
				timeZone = "+0000";
			}
			Date date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")).parse(utcTime.replaceAll("Z$", timeZone));
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Date时间转utc时间
	 *
	 * @param date
	 * @return
	 */
	public static String convertToUtcTime(Date date) {
		if (date == null) {
			return "";
		}
		try {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取到以小时为最小计数单位的Date类型
	 *
	 * @return
	 */
	public static Date getDateInHour() {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		return today.getTime();
	}
	
	public static Date getDateInMinute() {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		return today.getTime();
	}


	/**
	 * yyyy-MM-dd格式的开始时间转化为yyyy-MM-dd HH:mm:ss格式
	 * @param startDate
	 * @return
	 */
	public static Date  convertTimeFormat(Date startDate,int day){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);

		//在传入的日期上加上传入的天数
		//startDate自动加上00：00：00变成startTime(+0)
		//endDate自动加上00：00：00并加1天变成endTime(+1)
		calendar.add(Calendar.DATE,day);
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		Date date=calendar.getTime();
		String needTime=df.format(date);
		try{
			return  df.parse(needTime);
		}catch (ParseException e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * yyyy-MM-dd格式的开始时间转化为yyyy-MM-dd格式
	 * @param startDate
	 * @return
	 */
	public static String  convertTimeFormatString(Date startDate,int day){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);

		//在传入的日期上加上传入的天数
		//startDate自动加上00：00：00变成startTime(+0)
		//endDate自动加上00：00：00并加1天变成endTime(+1)
		calendar.add(Calendar.DATE,day);
		Date date=calendar.getTime();
		String needTime=df.format(date);
		return needTime;
	}

	/**
	 * 判断一串字符是否为期望格式的日期
	 *
	 * @param sourceStr    字符串
	 * @param expectFormat 期望格式
	 * @return true or false
	 */
	public static boolean isFormat(String sourceStr, SimpleDateFormat expectFormat) {
		boolean isFormat = true;
		try {
			expectFormat.parse(sourceStr);
		} catch (Exception e) {
			isFormat = false;
		}
		return isFormat;
	}

	/**
	 * 	进行简单的加减
	 * @param origin	原日期
	 * @param value		加减的值
	 * @param field		年、月、日
	 * @return
	 */
	public static Date dateAdd(Date origin,int value,int field){
		if (origin == null)return origin;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(origin);
		calendar.add(field,value);
		return calendar.getTime();

	}

	/**
	 * 	返回传入的时间的当日零点
	 * 	比如 传入 2018-10-11 11：00：00
	 * 		返回	 2018-10-11 00：00：00
	 * @param origin
	 * @return
	 */
	public static Date zeroTimeToday(Date origin){
		if (origin == null)return origin;
		return parseDay(formatDate(origin));
	}


}
