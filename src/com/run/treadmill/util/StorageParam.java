package com.run.treadmill.util;

import com.run.treadmill.serialutil.ParamCons;
import com.run.treadmill.systemInitParam.InitParam;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * sharedpreferences类，用于存放一些常用的变量值
 * 
 * @author chenyan
 * 
 */
public class StorageParam {
	// xml文件名
	private static final String SETTINGS_TREADMILL = "StorageParam_treadmill";
	

	// 跑动的模式
	private static final String RUN_MODE = "run_mode";
	// 开始时间
	private static final String RUN_BEGIN_TIME = "begin_time";
	// 暂停的时间
	private static final String RUN_STOP_TIME = "stop_time";
	// 设置速度
	private static final String SET_SPEED_VALUE = "set_speed_value";
	// 设置坡度
	private static final String SET_SLOPE_VALUE = "set_slope_value";
	// 设置跑步的距离
	private static final String SET_TOTAL_RUN_DISTANCE = "set_total_run_distance";
	// 跑步机是否正在运行
	private static final String IS_RUN_ING = "is_run_ing";
	// 设置系统语言
	private static final String SET_SYS_LANGUAGE = "set_sys_language";
	// 设置custom pass
	private static final String SET_CUSTOM_PASS = "set_custom_pass";
	// 设置srs pass
	private static final String SET_SRS_PASS = "set_srs_pass";
	// 设置已经跑步时间
	private static final String SET_TOTAL_TIME = "set_total_time";
	// 设置已经跑步距离
	private static final String SET_TOTAL_DIS = "set_total_dis";
	// 设置最大跑步时间
	private static final String SET_MAX_TIME = "set_max_time";
	// 设置最大跑步距离
	private static final String SET_MAX_DIS = "set_max_dis";
	// 设置logo类型
	private static final String SET_LOGO_TYPE = "set_logo_type";
	
	
	// factory one页面
	// 设置Unit 单位
	private static final String SET_UNIT = "set_unit";
	// 设置最大Level
	private static final String SET_MAX_LEVEL = "set_max_level";
	// 设置最小速度
	private static final String SET_MIN_LEVEL = "set_min_level";
	// 设置轮径尺寸
	private static final String SET_WHEEL_SIZE = "set_wheel_size";
	// 设置最大扬升
	private static final String SET_MAX_INCLINE = "set_max_incline";
	// 设置最大AD值
	private static final String SET_MAX_AD = "set_max_ad";
	// 设置最小AD值
	private static final String SET_MIN_AD = "set_min_ad";
	// factory two页面
	// 设置休眠模式
	private static final String SET_SPEED_MODE = "set_speed_mode";
	// 设置扬升反向
	private static final String SET_INCLINE_REVERT = "set_incline_revert";
	// 设置buzzer开关
	private static final String SET_BUZZER = "set_buzzer";
	// 设置pause mode开关
	private static final String SET_PAUSEMODE = "set_pausemode";
	// 设置keytone开关
	private static final String SET_KEYTONE = "set_keytone";
	// 设置备份跑步距离
	private static final String SET_BAK_TOTAL_DIS = "set_bak_total_dis";
	// 设置最大Lube
	private static final String SET_MAX_LUBE = "set_max_lube";

	// 设置Pwm
	private static final String SET_PWM = "set_pwm_";

	

	public static void clearAllUserData(Context mContext) {

		remove(mContext, RUN_MODE);
		remove(mContext, RUN_BEGIN_TIME);
		remove(mContext, RUN_STOP_TIME);
		remove(mContext, SET_SPEED_VALUE);
		remove(mContext, SET_SLOPE_VALUE);
		remove(mContext, IS_RUN_ING);

	}

	private static String getParam(Context context, String tagName, String defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(SETTINGS_TREADMILL, Context.MODE_PRIVATE);
		return sp.getString(tagName, defaultValue);
	}

	private static void setParam(Context context, String tagName, String value) {
		SharedPreferences sp = context.getSharedPreferences(SETTINGS_TREADMILL, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(tagName, value);
		editor.commit();
	}

	private static float getParam(Context context, String tagName, float defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(SETTINGS_TREADMILL, Context.MODE_PRIVATE);
		return sp.getFloat(tagName, defaultValue);
	}

	private static void setParam(Context context, String tagName, float value) {
		SharedPreferences sp = context.getSharedPreferences(SETTINGS_TREADMILL, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putFloat(tagName, value);
		editor.commit();
	}

	private static long getParam(Context context, String tagName, long defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(SETTINGS_TREADMILL, Context.MODE_PRIVATE);
		return sp.getLong(tagName, defaultValue);
	}

	private static void setParam(Context context, String tagName, long value) {
		SharedPreferences sp = context.getSharedPreferences(SETTINGS_TREADMILL, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putLong(tagName, value);
		editor.commit();
	}

	private static int getParam(Context context, String tagName, int defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(SETTINGS_TREADMILL, Context.MODE_PRIVATE);
		return sp.getInt(tagName, defaultValue);
	}

	private static void setParam(Context context, String tagName, int value) {
		SharedPreferences sp = context.getSharedPreferences(SETTINGS_TREADMILL, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(tagName, value);
		editor.commit();
	}

	private static boolean getParam(Context context, String tagName, boolean defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(SETTINGS_TREADMILL, Context.MODE_PRIVATE);
		return sp.getBoolean(tagName, defaultValue);
	}

	private static void setParam(Context context, String tagName, boolean value) {
		SharedPreferences sp = context.getSharedPreferences(SETTINGS_TREADMILL, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean(tagName, value);
		editor.commit();
	}
	

	private static void remove(Context context, String tagName) {
		SharedPreferences sp = context.getSharedPreferences(SETTINGS_TREADMILL, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.remove(tagName);
		editor.commit();
	}
	//获取跑步模式
	public static int getRunMode(Context context) {
		return getParam(context, RUN_MODE, CTConstant.DEFAULT);
	}
	//设置跑步模式
	public static void setRunMode(Context mContext, int run_mode) {
		setParam(mContext, RUN_MODE, run_mode);
	}
	
	public static long getRunBeginTime(Context context, long defaultValue) {
		return getParam(context, RUN_BEGIN_TIME, defaultValue);
	}

	public static void setRunBeginTime(Context mContext, long time) {
		setParam(mContext, RUN_BEGIN_TIME, time);
	}
	
	public static long getRunStopTime(Context context) {
		return getParam(context, RUN_STOP_TIME, 0l);
	}

	public static void setRunStopTime(Context mContext, long time) {
		setParam(mContext, RUN_STOP_TIME, time);
	}
	
	public static float getRunSpeedValue(Context context, float defaultValue) {
		return getParam(context, SET_SPEED_VALUE, defaultValue);
	}

	public static void setRunSpeedValue(Context mContext, float speed) {
		setParam(mContext, SET_SPEED_VALUE, speed);
	}
	
	public static float getRunSlopeValue(Context context, float defaultValue) {
		return getParam(context, SET_SLOPE_VALUE, defaultValue);
	}

	public static void setRunSlopeValue(Context mContext, float slope) {
		setParam(mContext, SET_SLOPE_VALUE, slope);
	}
	
	public static float getTotalRunDistanceValue(Context context) {
		return getParam(context, SET_TOTAL_RUN_DISTANCE, CTConstant.DEFAULT_FLOAT_VALUE);
	}

	public static void setTotalRunDistanceValue(Context mContext, float distance) {
		setParam(mContext, SET_TOTAL_RUN_DISTANCE, distance);
	}
	
	public static boolean getisRunIng(Context context) {
		return getParam(context, IS_RUN_ING, false);
	}

	public static void setIsRunIng(Context mContext, boolean isPlaying) {
		setParam(mContext, IS_RUN_ING, isPlaying);
	}

	public static int getSystemLanuage(Context context) {
		return getParam(context, SET_SYS_LANGUAGE, CTConstant.DEFAULT);
	}

	public static void setSystemLanuage(Context mContext, int sysLan) {
		setParam(mContext, SET_SYS_LANGUAGE, sysLan);
	}

	//存储客户密码
	public static String getCustomPass(Context context) {
		return getParam(context, SET_CUSTOM_PASS, CTConstant.customPass);
	}

	public static void setCustomPass(Context mContext, String customPass) {
		setParam(mContext, SET_CUSTOM_PASS, customPass);
	}

	public static String getSrsPass(Context context) {
		return getParam(context, SET_SRS_PASS, CTConstant.srsPass);
	}

	// 设置已经跑步时间 s
	public static void setRunTotalTime(Context mContext, long time) {
		setParam(mContext, SET_TOTAL_TIME, time);
	}
	public static long getRunTotalTime(Context context) {
		/*return 10000*60*60l;*/
		return getParam(context, SET_TOTAL_TIME, 0l);
	}

	// 设置已经跑步距离 km
	public static void setRunTotalDis(Context mContext, float dis) {
		setParam(mContext, SET_TOTAL_DIS, StorageParam.getRunTotalDis(mContext) + dis);
		setBakRunTotalDis(mContext, StorageParam.getBakRunTotalDis(mContext) + dis);
	}
	public static void resetRunTotalDis(Context mContext, float dis) {
		setParam(mContext, SET_TOTAL_DIS, dis);
	}
	public static float getRunTotalDis(Context context) {
		return getParam(context, SET_TOTAL_DIS, 0f);
	}

	// 设置最大跑步时间 s
	public static void setMaxTime(Context mContext, long time) {
		setParam(mContext, SET_MAX_TIME, time);
	}
	public static long getMaxTime(Context context) {
		return getParam(context, SET_MAX_TIME, CTConstant.runMaxTime);
	}

	// 设置最大跑步距离 km
	public static void setMaxDis(Context mContext, long dis) {
		setParam(mContext, SET_MAX_DIS, dis);
	}
	public static long getMaxDis(Context context) {
		return getParam(context, SET_MAX_DIS, CTConstant.runMaxDis);
	}

	//是否为内部logo
	public static boolean getisInnerLogo(Context context) {
		return getParam(context, SET_LOGO_TYPE, true);
	}

	public static void setIsInnerLogo(Context mContext, boolean inner) {
		setParam(mContext, SET_LOGO_TYPE, inner);
	}

	// factory one页面
	// 设置Unit 单位
	public static boolean getIsMetric(Context context) {
		return getParam(context, SET_UNIT, true);
	}
	public static void setUnit(Context mContext, boolean isMetric) {
		setParam(mContext, SET_UNIT, isMetric);
	}
	// 设置最大Level
	public static void setMaxLevel(Context mContext, int level) {
		setParam(mContext, SET_MAX_LEVEL, level);
	}
	public static int getMaxLevel(Context context) {
		return getParam(context, SET_MAX_LEVEL, InitParam.MaxLevel);
	}

	// 设置最小Level
	public static void setMinLevel(Context mContext, int level) {
		setParam(mContext, SET_MIN_LEVEL, level);
	}
	public static int getMinLevel(Context context) {
		return getParam(context, SET_MIN_LEVEL, InitParam.MinLevel);
	}
	// 设置轮径尺寸
	public static float getWheelSize(Context context) {
		return getParam(context, SET_WHEEL_SIZE, InitParam.WheelSize);
	}
	public static void setWheelSize(Context mContext, float wheelSize) {
		setParam(mContext, SET_WHEEL_SIZE, wheelSize);
	}
	// 设置最大扬升
	public static int getMaxInlcine(Context context) {
		return getParam(context, SET_MAX_INCLINE, InitParam.MaxLevel);
	}
	public static void setMaxInlcine(Context mContext, int incline) {
		setParam(mContext, SET_MAX_INCLINE, incline);
	}
	// 设置最大AD值
	public static int getMaxAd(Context context) {
		return getParam(context, SET_MAX_AD, InitParam.MaxAD);
	}
	public static void setMaxAd(Context mContext, int maxAd) {
		setParam(mContext, SET_MAX_AD, maxAd);
	}
	// 设置最小AD值
	public static int getMinAd(Context context) {
		return getParam(context, SET_MIN_AD, InitParam.MinAD);
	}
	public static void setMinAd(Context mContext, int minAd) {
		setParam(mContext, SET_MIN_AD, minAd);
	}
	// factory two页面
	// 设置休眠模式
	public static boolean getSleepMode(Context context) {
		return getParam(context, SET_SPEED_MODE, InitParam.SleepMode);
	}
	public static void setSleepMode(Context mContext, boolean speedMode) {
		setParam(mContext, SET_SPEED_MODE, speedMode);
	}
	// 设置扬升反向
	public static boolean getInclineRevert(Context context) {
		return getParam(context, SET_INCLINE_REVERT, InitParam.InclineRevert);
	}
	public static void setInclineRevert(Context mContext, boolean inclineRevert) {
		setParam(mContext, SET_INCLINE_REVERT, inclineRevert);
	}
	// 设置buzzer开关
	public static boolean getBuzzer(Context context) {
		return getParam(context, SET_BUZZER, InitParam.BuzzerON);
	}
	public static void setBuzzer(Context mContext, boolean buzzer) {
		setParam(mContext, SET_BUZZER, buzzer);
	}

	// 设置pause mode开关
	public static boolean getPauseMode(Context context) {
		return getParam(context, SET_PAUSEMODE, InitParam.PauseMode);
	}
	public static void setPauseMode(Context mContext, boolean buzzer) {
		setParam(mContext, SET_PAUSEMODE, buzzer);
	}

	// 设置keyTone开关
	public static boolean getkeyTone(Context context) {
		return getParam(context, SET_KEYTONE, InitParam.KeyToneON);
	}
	public static void setkeyTone(Context mContext, boolean buzzer) {
		setParam(mContext, SET_KEYTONE, buzzer);
	}

	// 设置备份已经跑步距离
	public static void setBakRunTotalDis(Context mContext, float dis) {
		setParam(mContext, SET_BAK_TOTAL_DIS, dis);
	}
	public static float getBakRunTotalDis(Context context) {
		return getParam(context, SET_BAK_TOTAL_DIS, 0f);
	}
	// 设置Lube
	public static int getMaxLubeDis(Context context) {
		return getParam(context, SET_MAX_LUBE, InitParam.MaxLubeDis);
	}
	public static void setMaxLubeDis(Context mContext, int maxLube) {
		setParam(mContext, SET_MAX_LUBE, maxLube);
	}

	// 设置pwm
	public static int getPwm(Context context, int counter) {
		return getParam(context, SET_PWM + counter, ParamCons.counter[counter] & 0x0000ffff);
	}
	public static void setPwm(Context mContext, int counter, int pwm) {
		setParam(mContext, SET_PWM + counter, pwm);
	}
	public static short[] getPwmArray(Context mContext) {
		short[] counter = new short[ParamCons.totalLevel];
		for ( int i = 0; i < counter.length; i++ ) {
			counter[i] = (short) (getPwm(mContext, i) & 0xffff);
		}
		return counter;
	}

}
