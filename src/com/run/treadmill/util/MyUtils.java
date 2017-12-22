package com.run.treadmill.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import com.run.treadmill.R;
import com.run.treadmill.db.UserInfoManager;

import android.media.AudioManager;
import android.media.SoundPool;

public class MyUtils {

	/**
	 * 以左移动画的形式，启动Activity
	 * 
	 * @param mActivity
	 * @param intent
	 * @param finishSelf
	 */
	public static void leftAnimStartActivity(Activity mActivity, Intent intent, boolean finishSelf) {
		if (finishSelf) {
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
		mActivity.startActivity(intent);
//		mActivity.overridePendingTransition(R.anim.right_to_current, R.anim.curent_to_left);
		if (finishSelf) {
			mActivity.finish();
		}
	}

	public static void leftAnimStartActivityForResult(Activity mActivity, Intent intent, boolean finishSelf) {
		if (finishSelf) {
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
		mActivity.startActivityForResult(intent, CTConstant.REQUEST_CODE_DELETE);
//		mActivity.overridePendingTransition(R.anim.right_to_current, R.anim.curent_to_left);
		if (finishSelf) {
			mActivity.finish();
		}
	}

	/**
	 * 以左移动画的形式，启动Activity,并且需要返回结果
	 * 
	 * @param mActivity
	 * @param intent
	 * @param finishSelf
	 */
	public static void leftAnimStartActivityForResult(Activity mActivity, Intent intent, boolean finishSelf, int activity) {
		if (finishSelf) {
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
		mActivity.startActivityForResult(intent, activity);
		mActivity.overridePendingTransition(R.anim.right_to_current, R.anim.curent_to_left);
		if (finishSelf) {
			mActivity.finish();
		}
	}

	/**
	 * 以右移动画的形式，启动Activity,并且需要返回结果
	 * 
	 * @param mActivity
	 * @param intent
	 * @param finishSelf
	 */
	public static void rightAnimStartActivityForResult(Activity mActivity, Intent intent, boolean finishSelf, int activity) {
		if (finishSelf) {
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
		mActivity.startActivityForResult(intent, activity);
		mActivity.overridePendingTransition(R.anim.left_to_current, R.anim.curent_to_left);
		if (finishSelf) {
			mActivity.finish();
		}
	}

	/**
	 * 以左移动画的形式，启动Activity
	 * 
	 * @param mActivity
	 * @param intent
	 * @param finishSelf
	 */
	public static void rightAnimStartActivity(Activity mActivity, Intent intent, boolean finishSelf) {
		if (finishSelf) {
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
		mActivity.startActivity(intent);
		mActivity.overridePendingTransition(R.anim.left_to_current, R.anim.curent_to_right);
		if (finishSelf) {
			mActivity.finish();
		}
	}

	/**
	 * 以右移动画的形式关闭activity
	 * 
	 * @param mActivity
	 */
	public static void animFinishActivity(Activity mActivity) {
		mActivity.finish();
		mActivity.overridePendingTransition(R.anim.left_to_current, R.anim.curent_to_right);
	}

	/**
	 * 以左移动画的形式关闭activity
	 * 
	 * @param mActivity
	 */
	public static void animLeftFinishActivity(Activity mActivity) {
		mActivity.finish();
		mActivity.overridePendingTransition(R.anim.right_to_current, R.anim.curent_to_left);
	}

	public static void animLeftFinishActivity(Activity mActivity, Intent intent) {
		Log.i("MyUtils","___________animLeftFinishActivity___________");
		mActivity.setResult(mActivity.RESULT_OK, intent);
		mActivity.finish();
		mActivity.overridePendingTransition(R.anim.right_to_current, R.anim.curent_to_left);
	}

	public static void startActivity(Context mContext, String packageName) {
		Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
		if (intent != null) {
			mContext.startActivity(intent);
		}
	}

	public static String getShowIntValue(float value) {
		//DecimalFormat df = new DecimalFormat("0.00");
		DecimalFormat df = new DecimalFormat("0");
		String defaultDistanceString = df.format((double) value);
		return defaultDistanceString;
	}

	public static String getShowOnePointValue(float value) {
		//DecimalFormat df = new DecimalFormat("0.00");
		DecimalFormat df = new DecimalFormat("0.0");
		//String defaultDistanceString = df.format((double) value);
		String defaultDistanceString = String.format(Locale.ENGLISH, "%.1f", (double) value);
//		Log.d("getShowOnePointValue", "===================" + defaultDistanceString);
		return defaultDistanceString;
	}

	/**
	 * 
	 * 
	 * @param 距离为km，时间为ms
	 */
	public static String calSpeedBasisDistanceTime(float distanceValue, float timeValue) {
		if( timeValue < 0.99999 || timeValue == 0 ) {
			return String.format(Locale.ENGLISH, "%.1f", 0.0f);
		}
		if( timeValue == 0 ) {
			timeValue = 1;
		}
		float speed = (float) Math.round(distanceValue / ( timeValue / 60.0 / 60.0 / 1000 ) * 10) / 10;
		Log.d("runDistance"," ==4===== " + distanceValue + " timeValue " + timeValue);
		//test
		if( speed > 24.0f ) {
			speed = 0.8f;
		}
		DecimalFormat df = new DecimalFormat("0.0");
//		String defaultSpeedString = df.format((double) speed);
		String defaultSpeedString = String.format(Locale.ENGLISH, "%.1f", speed);
		return defaultSpeedString;
	}

	public static String getDistanceValue(Context context, float alreadyRunDistance, TextView textUnit) {
		if (alreadyRunDistance >= 1) {
			textUnit.setText(R.string.distance_unit);
			return context.getResources().getString(R.string.distance_value, getShowOnePointValue(alreadyRunDistance));
		} else {
			textUnit.setText(R.string.distance_unit2);
			return context.getResources().getString(R.string.distance_value2, getShowOnePointValue(alreadyRunDistance * 1000));
		}
	}

	public static String getDistanceValue(Context context, float alreadyRunDistance) {
		//if (alreadyRunDistance >= 1) {
			return context.getResources().getString(R.string.distance_value, getShowOnePointValue(alreadyRunDistance));
		//} else {
		//	return context.getResources().getString(R.string.distance_value2, getShowValue(alreadyRunDistance * 1000));
		//}
	}

	public static String getDistanceMileValue(Context context, float alreadyRunDistance) {
		float tmp = getKmToMileOneP(alreadyRunDistance);
		return context.getResources().getString(R.string.distance_value, getShowOnePointValue(tmp));
	}

	public static String getMsToSecTimeValue(float value) {
		long time = Math.round(value / 1000.0);
		long minute = time / 60;
		long hour = minute / 60;
		long second = time % 60;
		minute %= 60;
		minute += hour * 60;
		if (hour > 0) {
//			return String.format("%02d:%02d:%02d", hour, minute, second);
			return String.format("%02d:%02d", minute, second);
		} else {
			return String.format("%02d:%02d", minute, second);
		}
	}

	public static String getMsToHourTimeValue(float value, TextView mTextView) {
		long time = Math.round(value / 1000.0);
		long minute = time / 60;
		long hour = minute / 60;
		long second = time % 60;
		minute %= 60;
		if (hour > 0) {
			mTextView.setText("min");
			return String.format("%02d:%02d", hour, minute);
			
		} else {
			mTextView.setText("sec");
			return String.format("%02d:%02d", minute, second);
		}
	}

	public static String getMsToTimeValue(float value, float targetValue) {
		long time = Math.round(value / 1000.0);
		long minute = time / 60;
		long hour = minute / 60;
		long second = time % 60;
		minute %= 60;
		if (hour > 0) {
			return String.format("%02d:%02d:%02d", hour, minute, second);
		} else {
			return String.format("%02d:%02d", minute, second);
		}
	}
	
	public static String getHourToTimeValue(float value) {
		long time = Math.round(value * 60 * 60);
		long minute = time / 60;
		long hour = minute / 60;
		long second = time % 60;
		minute %= 60;
		if (hour > 0) {
			return String.format("%02d:%02d:%02d", hour, minute, second);
		} else {
			return String.format("%02d:%02d", minute, second);
		}
	}

	public static String getSecToRemainTime(long value) {
		long time = Math.round(value);
		long minute = time / 60;
		long hour = minute / 60;
		long second = time % 60;
		minute %= 60;
		if (hour > 0) {
			return String.format("%d hr %02d min", hour, minute);
			
		} else {
			return String.format("%d min %02d sec", minute, second);
		}
	}

	public static String getSecToHour(long value) {
		long time = Math.round(value);
		long minute = time / 60;
		long hour = minute / 60;
		long second = time % 60;
		minute %= 60;
		return hour + "";
	}

	public static String getRunData(long stime) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(stime);
		return StringUtil.formatDate(cal.getTime(), "yyyy-MM-dd");
	}

	public static List<Date> dataToMonth(Date mdate) {
		List<Date> list = new ArrayList<Date>();
		Calendar cale = Calendar.getInstance();
		cale.add(Calendar.MONTH, 1);
		cale.set(Calendar.DAY_OF_MONTH, 0);
		int day = cale.get(Calendar.DAY_OF_MONTH);
		for (int i = 1; i <= day; i++) {
			Calendar dayCalendar = Calendar.getInstance();
			dayCalendar.set(Calendar.DAY_OF_MONTH, i);
			Date fdate = new Date();
			fdate.setTime(dayCalendar.getTimeInMillis());
			list.add(i - 1, fdate);
		}
		return list;
	}

	public static List<Date> dateToWeek(Date mdate) {
		int b = mdate.getDay();
		Date fdate;
		List<Date> list = new ArrayList<Date>();
		Long fTime = mdate.getTime() - b * 24 * 3600000;
		for (int a = 1; a <= 7; a++) {
			fdate = new Date();
			fdate.setTime(fTime + (a * 24 * 3600000));
			list.add(a - 1, fdate);
		}
		return list;
	}
	
	private static long lastClickTime;
		public synchronized static boolean isFastClick() {
			long time = System.currentTimeMillis();   
			if ( time - lastClickTime < 1200) {   
				return true;   
			}   
			lastClickTime = time;   
			return false;
	}

	public static int getKgToLb(int kg) {
		return (int) Math.round( ( kg * 2205) / 1000.0f );
	}

	public static int getLbToKg(int kg) {
		return (int) Math.round( ( kg * 4536) / 10000.0f );
	}

	public static float getKmToMile(float km) {
//		return (float) ( Math.round((km * 6214) / 10000f * 100) / 100.0 );
		return (float) ( Math.round((km / 1.6) * 100f) / 100.0 );
	}

	public static float getKmToMileOneP(float km) {
//		return (float) ( Math.round((km * 6214) / 10000f * 10) / 10.0 );
		return (float) ( Math.round((km / 1.6) * 10) / 10.0 );
	}

	/*public static float getKmToMileOneP(float km) {
		return (float) ( Math.round((km * 6214) / 10000f * 10) / 10.0 );
	}*/

	public static int getKmToMileIntType(float km) {
//		return (int) ( Math.round((km * 6214) / 10000f * 100) / 100.0 );
		return (int) ( Math.round((km / 1.6) * 100f) / 100.0 );
	}

	public static int getMileToKmIntType(float km) {
//		return (int) ( Math.round((km * 1609) / 1000f * 100) / 100.0 );
		return (int) ( Math.round((km * 1600) / 1000f * 100) / 100.0 );
	}

	public static float getMileToKmFloat(float km) {
//		return (float) ( Math.round((km * 1609) / 1000f * 10) / 10.0 );
		return (float) ( Math.round((km * 1600) / 1000f * 10) / 10.0 );
	}

	public static float formatFloatToOnePoint(float d) {
        return (float) Math.round( d * 10 ) / 10;
    }

	public static int formatFloatToInt(float d) {
        return (int) Math.round( d );
    }

	public static int getVideoPlayerSpeedValue(float value) {
		int res = (int) (formatFloatToOnePoint( (float) (( value - 0.8 ) / ( 24 -0.8 )  * 15.0) ) + 5);
        return res;
    }

	public static float getSpeedMileOneP(int rpm) {//0.00114*RPM*60
		return (float) ( Math.round( ( 0.00114 * rpm * 60 ) * 10 ) / 10.0 );
	}

	public static float getSpeedKmOneP(int rpm) {
		return (float) ( Math.round( (0.00114 * rpm * 60 * 1600) / 1000f * 10) / 10.0 );
	}
	
}
