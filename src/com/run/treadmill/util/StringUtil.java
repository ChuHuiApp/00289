package com.run.treadmill.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 判断字符串是否为空，字符串与其它类型之前的转换等字符串工具类
 * 
 * @author chende
 * 
 */
public class StringUtil {

	public static String substring(String str, int toCount, String more) {
		int reInt = 0;
		String reStr = "";
		if (str == null)
			return "";
		char[] tempChar = str.toCharArray();
		for (int kk = 0; (kk < tempChar.length && toCount > reInt); kk++) {
			String s1 = str.valueOf(tempChar[kk]);
			byte[] b = s1.getBytes();
			reInt += b.length;
			reStr += tempChar[kk];
		}
		if (toCount == reInt || (toCount == reInt - 1))
			reStr += more;
		return reStr;
	}

	/**
	 * 
	 * @param sourceString
	 *            eg:测试字符串
	 * @param maxLength
	 *            最大长度，一个中文占一个单位长度
	 * @param more
	 *            如果超过所截取的长度，字符串最后要加上的省略符。
	 * @return
	 */
	public static String limit(String sourceString, int maxLength, String more) {
		String resultString = "";
		if (sourceString == null || sourceString.equals("") || maxLength < 1) {
			return resultString;
		} else if (sourceString.length() <= maxLength) {
			return sourceString;
		} else if (sourceString.length() > 2 * maxLength) {
			// sourceString = sourceString.substring(0, 2 * maxLength);
		}

		if (sourceString.length() > maxLength) {
			char[] chr = sourceString.toCharArray();
			int strNum = 0;
			int strGBKNum = 0;
			boolean isHaveDot = false;

			for (int i = 0; i < sourceString.length(); i++) {
				if (chr[i] >= 0xa1) // 0xa1汉字最小位开始
				{
					strNum = strNum + 2;
					strGBKNum++;
				} else {
					strNum++;
				}

				if (strNum == 2 * maxLength || strNum == 2 * maxLength + 1) {
					if (i + 1 < sourceString.length()) {
						isHaveDot = true;
					}

					break;
				}
			}

			resultString = sourceString.substring(0, strNum - strGBKNum);
			if (isHaveDot) {
				resultString = resultString + more;
			}
		}
		return resultString;
	}

	/**
	 * 格式化时间
	 * 
	 * @param milliSecond
	 *            毫秒
	 * @return
	 */
	public static String timeToString(long milliSecond) {
		int ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;
		long day = hh * 24;
		long month = day * 30;
		long year = month * 12;

		long years = (milliSecond) / year;
		long months = (long) ((milliSecond - years * year) / month);
		long days = (milliSecond - years * year - months * month) / day;
		long hour = (long) ((milliSecond - years * year - months * month - days * day) / hh);
		long minute = (int) ((milliSecond - years * year - months * month - days * day - hour * hh) / mi);
		long second = (int) ((milliSecond - years * year - months * month - days * day - hour * hh - minute * mi) / ss);
		String result = "";
		if (years != 0) {
			result += years + "年前";
		} else if (months != 0) {
			result += months + "月前";
		} else if (days != 0) {
			result += days + "天前";
		} else if (hour != 0) {
			result += hour + "小时前";
		} else if (minute != 0) {
			result += minute + "分钟前";
		} else if (second != 0) {
			result += second + "秒前";
		}
		return result;
	}

	/**
	 * 格式化时间
	 * 
	 * @param milliSecond
	 *            毫秒
	 * @return
	 */
	public static String timeToString2(long milliSecond) {
		int ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;
		long day = hh * 24;
		long month = day * 30;
		long year = month * 12;

		long years = (milliSecond) / year;
		long months = (long) ((milliSecond - years * year) / month);
		long days = (milliSecond - years * year - months * month) / day;
		long hour = (long) ((milliSecond - years * year - months * month - days * day) / hh);
		long minute = (int) ((milliSecond - years * year - months * month - days * day - hour * hh) / mi);
		long second = (long) ((milliSecond - years * year - months * month - days * day - hour * hh - minute * mi) / ss);
		long ms = milliSecond - years * year - months * month - days * day - hour * hh - minute * mi - second * ss;
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		String result = "";
		if (years != 0) {
			result += years + "年";
		} else if (months != 0) {
			result += months + "月";
		} else if (days != 0) {
			result += days + "天";
		} else if (hour != 0) {
			result += hour + "小时";
		} else if (minute != 0) {
			result += minute + "分钟";
		} else if (second != 0) {
			// result += second + "秒";
			result += nf.format(second + (ms / 1000.0)) + "秒";
		}
		return result;
	}

	/**
	 * 格式化日期
	 * 
	 * @param date
	 * @return
	 */
	public static String timeToString3(Date date) {
		if (date == null)
			return "无";
		long milliSecond = System.currentTimeMillis() - date.getTime();
		int ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;
		long day = hh * 24;
		long month = day * 30;
		long year = month * 12;

		long years = (milliSecond) / year;
		long months = (long) ((milliSecond - years * year) / month);
		long days = (milliSecond - years * year - months * month) / day;
		long hour = (long) ((milliSecond - years * year - months * month - days * day) / hh);
		long minute = (int) ((milliSecond - years * year - months * month - days * day - hour * hh) / mi);
		long second = (int) ((milliSecond - years * year - months * month - days * day - hour * hh - minute * mi) / ss);
		String result = "";
		if (years != 0) {
			result += years + "年前";
		} else if (months != 0) {
			result += months + "月前";
		} else if (days != 0) {
			result += days + "天前";
		} else if (hour != 0) {
			result += hour + "小时前";
		} else if (minute != 0) {
			result += minute + "分钟前";
		} else if (second != 0) {
			result += second + "秒前";
		}
		return result;
	}

	/**
	 * 判断字符串是否为NULL或空字符串
	 * 
	 * @param obj
	 *            被判断的字符串
	 * @return 为空返回true，否则返回 false
	 */
	public static boolean isNullOrEmpty(String obj) {
		if (obj == null)
			return true;
		if (obj != null && "null".equals(obj))
			return true;
		if ("".equals(obj))
			return true;
		return false;
	}

	public static boolean isNotNullOrEmpty(String obj) {
		return !isNullOrEmpty(obj);
	}

	public static boolean isNullOrEmpty(List<?> list) {
		if (list == null || list.size() == 0) {
			return true;
		}
		return false;
	}

	public static boolean isNotNullOrEmpty(List<?> list) {
		return !isNullOrEmpty(list);
	}

	/**
	 * 将字符串转化为日期
	 * 
	 * @param dateStr
	 *            源字符串
	 * @param style
	 *            格式化串
	 * @return 返回Date
	 * @throws ParseException
	 */
	public static Date parseDate(String dateStr, String style) {
		if (isNullOrEmpty(dateStr)) {
			return null;
		}
		SimpleDateFormat sf = new SimpleDateFormat(style);
		Date date;
		try {
			date = sf.parse(dateStr);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将日期格式化为字符串
	 * 
	 * @param date
	 *            日期
	 * @param style
	 *            格式化形式
	 * @return
	 */
	public static String formatDate(Date date, String style) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sf = new SimpleDateFormat(style);
		String str = sf.format(date);
		return str;
	}

	public static Long toLong(String data) {
		Long _data = null;
		if (data != null && !"".equals(data) && !"null".equals(data)) {
			_data = Long.parseLong(data);
		}
		return _data;
	}

	public static Integer toInteger(String data) {
		Integer _data = null;
		if (data != null && !"".equals(data) && !"null".equals(data)) {
			_data = Integer.parseInt(data);
		}
		return _data;
	}

	public static String toString(String _data) {
		String data = _data;
		if (data != null) {
			data = data.trim();
		}
		if ("".equals(data) || "null".equals(data)) {
			return null;
		}
		return data;
	}

	public static Double toDouble(String data) {
		Double _data = null;
		if (data != null && !"".equals(data) && !"null".equals(data)) {
			try {
				_data = Double.parseDouble(data);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return _data;
	}

	public static int getCurrentYear() {
		Calendar now = Calendar.getInstance();
		return now.get(Calendar.YEAR);
	}

	public static String getFileSizeString(Long _size) {
		String size = "";
		long size_long = Math.abs(_size == null ? 0 : _size.longValue());
		NumberFormat formatter = NumberFormat.getInstance();
		formatter.setMaximumFractionDigits(2);
		if (size_long > 700000000L) {
			String[] args = { formatter.format(1.0 * size_long / (1024L * 1024L * 1024L)) };
			for (int _a = 0; _a < args.length; _a++) {
				size += args[_a];
			}
			size += " GB";
		} else if (size_long > 700000L) {
			String[] args = { formatter.format(1.0 * size_long / (1024L * 1024L)) };
			for (int _a = 0; _a < args.length; _a++) {
				size += args[_a];
			}
			size += " MB";
		} else if (size_long > 700L) {
			String[] args = { formatter.format(1.0 * size_long / 1024L) };
			for (int _a = 0; _a < args.length; _a++) {
				size += args[_a];
			}
			size += " KB";
		} else {
			String[] args = { formatter.format(size_long) };
			for (int _a = 0; _a < args.length; _a++) {
				size += args[_a];
			}
			size += " Bytes";
		}
		if (_size < 0) {
			size = "-" + size;
		}
		return size;
	}

	public static String convertToCNNumber(Integer num) {
		if (num == null)
			return null;

		String[] cn_names = new String[] { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九" };

		int v = num.intValue();
		StringBuilder str = new StringBuilder();

		String tmp = "" + v;
		for (int i = 0; i < tmp.length(); i++) {
			char c = tmp.charAt(i);
			int tt = Integer.parseInt("" + c);
			str.append(cn_names[tt]);

		}

		if (str.length() > 0) {
			return str.toString();
		} else {
			return null;
		}
	}

	public static boolean isHttpUrl(String str) {
		if (StringUtil.isNullOrEmpty(str)) {
			return false;
		}
		String patternstr = "^http://.*";
		Pattern pattern = Pattern.compile(patternstr, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);
		if (matcher != null && matcher.find()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean startsWith(String src, String start) {
		String patternstr = "^" + start;
		Pattern pattern = Pattern.compile(patternstr, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(src);
		if (matcher != null && matcher.find()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean endsWith(String src, String end) {
		String patternstr = end + "$";
		Pattern pattern = Pattern.compile(patternstr, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(src);
		if (matcher != null && matcher.find()) {
			return true;
		} else {
			return false;
		}
	}

	public static String fillUrl(String str, String contextPath) {
		if (isNullOrEmpty(str)) {
			return str;
		}
		if (!isHttpUrl(str)) {
			return contextPath + str;
		}
		return str;
	}

	/**
	 * 
	 * @param lat
	 *            纬度
	 * @param lon
	 *            经度
	 * @param raidus
	 *            半径，单位米
	 * @return {最小纬度,最小经度,最大纬度,最大经度}
	 */
	public static double[] getAround(double lat, double lon, int raidus) {

		Double latitude = lat;
		Double longitude = lon;

		Double degree = (24901 * 1609) / 360.0;
		double raidusMile = raidus;

		Double dpmLat = 1 / degree;
		Double radiusLat = dpmLat * raidusMile;
		Double minLat = latitude - radiusLat;
		Double maxLat = latitude + radiusLat;

		Double mpdLng = degree * Math.cos(latitude * (Math.PI / 180));
		Double dpmLng = 1 / mpdLng;
		Double radiusLng = dpmLng * raidusMile;
		Double minLng = longitude - radiusLng;
		Double maxLng = longitude + radiusLng;
		return new double[] { minLat, minLng, maxLat, maxLng };
	}

	public static double[] getAround2(double lat, double lon, int raidus) {

		Double latitude = lat;
		Double longitude = lon;

		Double degree = 69.17032342863616d;
		double raidusMile = raidus;

		Double dpmLat = 1 / degree;
		Double radiusLat = dpmLat * raidusMile;
		Double minLat = latitude - radiusLat;
		Double maxLat = latitude + radiusLat;

		Double mpdLng = degree * Math.cos(latitude * (Math.PI / 180));
		Double dpmLng = 1 / mpdLng;
		Double radiusLng = dpmLng * raidusMile;
		Double minLng = longitude - radiusLng;
		Double maxLng = longitude + radiusLng;
		return new double[] { minLat, minLng, maxLat, maxLng };
	}

	public static byte[] compress(String str) {
		if (StringUtil.isNullOrEmpty(str)) {
			return null;
		}
		byte[] compressed = compress(str.getBytes());
		return compressed;
	}

	public static byte[] compress(byte[] src) {
		if (src == null || src.length == 0) {
			return null;
		}
		byte[] compressed = null;
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			GZIPOutputStream zOut = new GZIPOutputStream(b);
			zOut.write(src);
			zOut.flush();
			zOut.close();
			b.close();
			compressed = b.toByteArray();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return compressed;
	}

	public static final String decompress(byte[] compressed) {
		if (compressed == null || compressed.length == 0)
			return null;
		GZIPInputStream zIn = null;
		ByteArrayOutputStream outStream = null;
		try {
			zIn = new GZIPInputStream(new ByteArrayInputStream(compressed));
			outStream = new ByteArrayOutputStream();
			byte[] buff = new byte[4096];
			int length = 0;
			while ((length = zIn.read(buff)) >= 0) {
				outStream.write(buff, 0, length);
			}
			String decompressed = new String(outStream.toByteArray());
			zIn.close();
			outStream.close();
			return decompressed;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static final byte[] decompressToByte(byte[] compressed) {
		if (compressed == null || compressed.length == 0)
			return null;
		GZIPInputStream zIn = null;
		ByteArrayOutputStream outStream = null;
		try {
			zIn = new GZIPInputStream(new ByteArrayInputStream(compressed));
			outStream = new ByteArrayOutputStream();
			byte[] buff = new byte[4096];
			int length = 0;
			while ((length = zIn.read(buff)) >= 0) {
				outStream.write(buff, 0, length);
			}
			byte[] result = outStream.toByteArray();
			zIn.close();
			outStream.close();
			return result;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isNumber(String str) {
		if (isNullOrEmpty(str)) {
			return false;
		}
		Pattern pattern = Pattern.compile("[1-9][0-9]*");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	public static String getNameByPath(String path) {
		return path.substring(path.lastIndexOf("/") + 1, path.length());
	}

	public static String getParentByPath(String path) {
		return path.substring(0, path.lastIndexOf("/") + 1);
	}

	public static String getFormParamStringWithPrefix(String name, String value) {
		StringBuilder param = new StringBuilder();
		if (StringUtil.isNotNullOrEmpty(value)) {
			param.append("&").append(name).append("=").append(value);
			return param.toString();
		}
		return null;
	}

	public static String getFormParamStringWithoutPrefix(String name, String value) {
		StringBuilder param = new StringBuilder();
		if (StringUtil.isNotNullOrEmpty(value)) {
			param.append(name).append("=").append(value);
			return param.toString();
		}
		return null;
	}

	public static String getFormParamStringWithPrefix(String name, int value) {
		StringBuilder param = new StringBuilder();
		param.append("&").append(name).append("=").append(value);
		return param.toString();
	}

	public static String getFormParamStringWithoutPrefix(String name, int value) {
		StringBuilder param = new StringBuilder();
		param.append(name).append("=").append(value);
		return param.toString();
	}

	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			dest = str.trim();
		}
		return dest;
	}

	/**
	 * 检查输入的数据中是否有特殊字符
	 * 
	 * @param qString
	 *            要检查的数据
	 * @param regx
	 *            特殊字符正则表达式
	 * @return boolean 如果包含正则表达式 <code> regx </code> 中定义的特殊字符，返回true； 否则返回false
	 */
	public static boolean hasCrossScriptRisk(String qString) {
		String regx = "!|！|@|◎|#|＃|(\\$)|￥|%|％|(\\^)|……|(\\&)|※|(\\*)|×|(\\()|（|(\\))|）|_|——|(\\+)|＋|(\\|)|§";
		if (qString != null) {
			qString = qString.trim();
			Pattern p = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(qString);
			return m.find();
		}
		return false;
	}

	/**
	 * 制定5G设置信道方法
	 */
	public static int set5GChannel(int channel) {
		int n = 0;
		if ((channel == 149) || (channel == 153) || (channel == 157) || (channel == 161)) {
			return channel;
		} else {
			return 149;
		}
	}

	private static final Pattern IPV4_PATTERN = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
	private static final Pattern IPV6_STD_PATTERN = Pattern.compile("^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");
	private static final Pattern IPV6_HEX_COMPRESSED_PATTERN = Pattern.compile("^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$");

	/**
	 * 判断字符串是否是IP格式的
	 * 
	 * @param ip
	 * @return
	 */
	public static boolean isIPFormat(String ip) {
		try{
		boolean isIPV4 = IPV4_PATTERN.matcher(ip).matches();
		boolean isIPV6 = IPV6_STD_PATTERN.matcher(ip).matches();
		boolean isIPV6HEX = IPV6_HEX_COMPRESSED_PATTERN.matcher(ip).matches();
		if (isIPV4 || isIPV6 || isIPV6HEX) {
			return true;
		} else {
			return false;
		}
		}catch(Exception e){
			return false;
		}

	}
}
