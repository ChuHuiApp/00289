package com.run.treadmill.util;

import android.util.Log;

/**
 * SDKLog设定类
 */
public final class AppLog {
	/**
	 * 需要显示的log级别定义
	 */
	public final static int ERROR = 0X10;
	public final static int WARNING = 0X11;
	public final static int INFO = 0X12;
	public final static int DEBUG = 0X13;
	public final static int VERBOSE = 0X14;
	public final static int CLOSE_LOG = 0X15;
	/**
	 * log Tag
	 */
	private static String TAG = "Treadmill";
	/**
	 * log需要显示的级别设定
	 */
	private static int logLevel = DEBUG;

	/**
	 * 设置SDK的log级别
	 * 
	 * @param loglevel
	 *            分别对应：ERROR，WARNING，INFO，DEBUG，VERBOSE，CLOSE_LOG这几钟情况；默认是：ERROR
	 */
	public static void setLogLevel(int loglevel) {
		logLevel = loglevel;
	}

	public static void e(String logString) {
		if (logLevel >= ERROR) {
			Log.e(TAG, logString);
		}
	}

	public static void w(String logString) {
		if (logLevel >= WARNING) {
			Log.w(TAG, logString);
		}
	}

	public static void i(String logString) {
		if (logLevel >= INFO) {
			Log.i(TAG, logString);
		}
	}

	public static void d(String logString) {
		if (logLevel >= DEBUG) {
			Log.d(TAG, logString);
		}
	}

	public static void v(String logString) {
		if (logLevel >= VERBOSE) {
//			Log.v(TAG, logString);
			Log.v(TAG, Thread.currentThread().getStackTrace()[2].getMethodName() + 
					Thread.currentThread().getStackTrace()[2].getLineNumber() + logString);
		}
	}
	// Log.d(TAG,new Exception().getStackTrace()[0].getMethodName()); //函数名 
	// Log.d(TAG, Thread.currentThread().getStackTrace()[2].getFileName()); //文件名 
}