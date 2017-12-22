package com.run.treadmill.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
//import android.gpio.GpioManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.run.treadmill.R;
import com.run.treadmill.db.UserInfo;
import com.run.treadmill.db.UserInfoManager;
import com.run.treadmill.runningParamManager.RunParamTableManager;
import com.run.treadmill.serialutil.SerialUtils;
import com.run.treadmill.util.AppLog;
import com.run.treadmill.util.CTConstant;

public class InterfaceUtils {

	private static final String TAG = "com.run.treadmill.service.InterfaceUtils";
//	private SerialUtils serialUtils = SerialUtils.getInstance();

	
	/**
	 * 得到已跑的里程(km) 路程 = 速度 * 时间
	 * 
	 * @return
	 */
	public float getRunMileage(Context context, float speed, float time) {
		return speed * time;
	}
	

	/**
	 * 得到已跑时间(ms)
	 * 
	 * @returnr
	 */
	public float getRunTime(Context context) {
		long beginRunTime = StorageParam.getRunBeginTime(context, System.currentTimeMillis());
		long runTime = System.currentTimeMillis() - beginRunTime - StorageParam.getRunStopTime(context);
		/*Log.d(TAG," System.currentTimeMillis() :" +  System.currentTimeMillis()
				+ " beginRunTime:" +  beginRunTime + 
				"StorageParam.getRunStopTime(context):" + StorageParam.getRunStopTime(context));*/
		return (float) runTime;
	}

	/**
	 * 得到当前跑步的卡路里(kCAL) 热量 = K*体重*速度*时间 体重单位：Kg 速度单位：Km/h 时间：小时
	 * K系数：男人为1.25，女人1.15. 热量单位：Kcal 如果没有输入体重默认60Kg。
	 * 
	 * @return
	 */
	public float getRunCalories(UserInfo mUserInfo, float distance) {
		float userWeight = CTConstant.DEFAULT_WEIGHT;
		if (mUserInfo != null && StringUtil.isNotNullOrEmpty(mUserInfo.mUserWeight[UserInfoManager.getInstance().getRunMode()])) {
			userWeight = Float.parseFloat(mUserInfo.mUserWeight[UserInfoManager.getInstance().getRunMode()]);
		}
		float kValue = CTConstant.BOY_KEY;
		if (mUserInfo != null && mUserInfo.mSex == CTConstant.GENDER_GIRL) {
			kValue = CTConstant.GIRL_KEY;
		}
		float calories = kValue * userWeight * distance;
		return calories;
	}

	/**
	 * 
	 * @return
	 */
	public float getRunCaloriesPerSecond(UserInfo mUserInfo, float speed, float incline) {
		float userWeight = CTConstant.DEFAULT_WEIGHT;
		if (mUserInfo != null && StringUtil.isNotNullOrEmpty(mUserInfo.mUserWeight[UserInfoManager.getInstance().getRunMode()])) {
			userWeight = Float.parseFloat(mUserInfo.mUserWeight[UserInfoManager.getInstance().getRunMode()]);
		}
		userWeight = MyUtils.getKgToLb((int)userWeight);
		float calories = 0;
		speed = MyUtils.getKmToMile(speed);
		if( speed >= 3.7 ) {
			calories = (float) (( 1 + ( 1.532 * speed ) + ( 0.0685 * speed * incline ) ) 
							* userWeight / 2.2f / 3600.0);
		} else {
			calories = (float) (( 1 + ( 0.768 * speed ) + ( 0.1370 * speed * incline ) ) 
							* userWeight / 2.2f / 3600.0);
		}
		return calories;
	}

	/**
	 * 
	 * @return
	 */
	public float getRunCaloriesPerSec(UserInfo mUserInfo, float speed, int level) {
		/*KCAL/HOUR={[1+(12.256*SPEED)+(1.37*SPE
				ED*LEVEL)]+WEIGHT_LB IN LBS/2.2}*1.5*/
		float userWeight = CTConstant.DEFAULT_WEIGHT;
		if (mUserInfo != null && StringUtil.isNotNullOrEmpty(mUserInfo.mUserWeight[UserInfoManager.getInstance().getRunMode()])) {
			userWeight = Float.parseFloat(mUserInfo.mUserWeight[UserInfoManager.getInstance().getRunMode()]);
		}

		float calories = 0;
		speed = MyUtils.getKmToMile(speed);
		
		calories =    (float) ( (( 1 + 12.256 * speed + 1.37 * speed * level 
				+ userWeight ) * 1.5 ) / 3600.0 );
		
		/*Log.d("interfaceUtils"," getRunCaloriesPerSec speed " + speed + 
				" level " + level + " userWeight " + userWeight + " calories " + calories );*/
		return calories;
	}

	/**
	 * 得到当前的心率(RPM): 用户运动时当前心率，次/分钟。范围：50~200。 心率数据获得从心跳侦测模块获得。
	 * 
	 * @return
	 */
	public int getRunHeartRate() {
//		int heart = serialUtils.getHeart();
		return 80;
	}

	/**
	 *获取mets 
	 * @return
	 */
	public int getMets(float speed, float incline) {
		float mets = 0;
//		(float) ( Math.round((km * 6214) / 10000f * 100) / 100.0 );
		float speedInch = MyUtils.getKmToMile(speed);
		if( speedInch <= 4.5 ) {
			mets = (float) Math.round((3.5f + 0.1f*speedInch + 1.8f*speedInch*incline) * 10 / 3.5) / 10;
		} else {
			mets = (float) Math.round((3.5f + 0.2f*speedInch + 0.9f*speedInch*incline) * 10 / 3.5) / 10;
		}
		return (int) mets;
	}

	/**
	 * 得到锻炼指数
	 */
	// public float getExerciseIndex() {
		// float exerciseIndex = (float) Math.random() * 100;
		// float returnExerciseIndex = (float) (Math.round(exerciseIndex * 100)) / 100;
		// return returnExerciseIndex;
	// }

	/**
	 * 得到活力指数
	 */
	//X = (S/10 + T/60) * 50;X 是活力指数，S是路程(千米)、T是跑步时间(分钟)
	public float getVigorIndex(float distance, float runTime) {
		//float vigorIndex = (float) Math.random() * 100;
		//float returnVigorIndex = (float) (Math.round(vigorIndex * 100)) / 100;
		long minute_time = Math.round(runTime * 60);
		//AppLog.i("_______"+minute_time+"-distance--"+distance);
		float returnVigorIndex = (float) (Math.round((distance / 10 + minute_time / 60.0)* 50 * 10)) / 10;
		//AppLog.i("________________"+returnVigorIndex+"____distance / 10___"+(distance / 10)+"____minute_time / 60.0___" +(minute_time / 60.0));
		return returnVigorIndex;
	}

	/**
	 * 得到健身指导
	 */
	public String getRunGuide() {
		return "";
	}

	/**
	 * 得到速度数组
	 */
	public List<Float> getRunSpeedList(Context context) {
		List<Float> returnList = new ArrayList<Float>();
		String[] speedString = context.getResources().getStringArray(R.array.set_speed_value);
		for (int i = 0; i < speedString.length; i++) {
			String value = speedString[i];
			returnList.add(Float.parseFloat(value));
		}
		return returnList;
	}

	/**
	 * 设置速度
	 */
	public void setRunSpeed(float speed) {
		AppLog.e("设置速度---" + speed);
		//为了匹配20.0km/h用200表示
		int s = (int)speed*10;
//		serialUtils.send(SerialCmd.CMD_SET_SPEED, s);
	}

	/**
	 * 得到坡度数组
	 */
	public List<Float> getRunSlopeList(Context context) {
		List<Float> returnList = new ArrayList<Float>();
		String[] speedString = context.getResources().getStringArray(R.array.set_slope_value);
		for (int i = 0; i < speedString.length; i++) {
			String value = speedString[i];
			returnList.add(Float.parseFloat(value));
		}
		return returnList;
	}

	/**
	 * 设置坡度
	 */
	public void setRunSlope(float slope) {
		AppLog.e("设置坡度---" + slope);
		AppLog.i("setRunSpeed()--slope: " + slope );
//		serialUtils.send(SerialCmd.CMD_RISE_DROP_ELECTRICAL_LOCATION, (int)slope);
	}

	/**
	 * 得到距离数组
	 */
	public List<Float> getRunDistanceList(Context context) {
		List<Float> returnList = new ArrayList<Float>();
		String[] speedString = context.getResources().getStringArray(R.array.set_distance_value);
		for (int i = 0; i < speedString.length; i++) {
			String value = speedString[i];
			returnList.add(Float.parseFloat(value));
		}
		return returnList;
	}

	/**
	 * 设置距离
	 */
	public void setRunDistance(float distance) {
		AppLog.e("设置距离---" + distance);
	}

	/**
	 * 得到时间数组
	 */
	public List<Float> getRunTimeList(Context context) {
		List<Float> returnList = new ArrayList<Float>();
		String[] speedString = context.getResources().getStringArray(R.array.set_time_value);
		for (int i = 0; i < speedString.length; i++) {
			String value = speedString[i];
			returnList.add(Float.parseFloat(value));
		}
		return returnList;
	}

	/**
	 * 设置时间
	 */
	public void setRunTime(float time) {
		AppLog.e("设置时间---" + time);
	}

	/**
	 * 得到卡路里数组
	 */
	public List<Float> getRunCaloriesList(Context context) {
		List<Float> returnList = new ArrayList<Float>();
		String[] speedString = context.getResources().getStringArray(R.array.set_calories_value);
		for (int i = 0; i < speedString.length; i++) {
			String value = speedString[i];
			returnList.add(Float.parseFloat(value));
		}
		return returnList;
	}

	/**
	 * 设置卡路里
	 */
	public void setRunCalories(float calories) {
		AppLog.e("设置卡路里---" + calories);
	}

	/**
	 * 开始跑步
	 */
	public void startRunning() {
		AppLog.e("开始跑步");
//		gpioUtils.startReadHeart();
		//serialUtils.send(SerialCmd.CMD_RISE_DROP_ELECTRICAL_LOCATION, 0);
		//测试用
//		serialUtils.send(SerialCmd.CMD_START, 0);
	}

	/**
	 * 停止跑步
	 */
	public void stopRunning() {
		AppLog.e("停止跑步");
//		gpioUtils.stopReadHeart();
//		serialUtils.send(SerialCmd.CMD_STOP, 0);
	}

	/**
	 * 得到娱乐列表
	 */
	// public List<Entertainment> getEntertainmentList(Context context) {
		// List<Entertainment> returnList = new ArrayList<Entertainment>();
		// String[] entertainment_title = context.getResources().getStringArray(R.array.entertainment_title);
		// String[] entertainment_url = context.getResources().getStringArray(R.array.entertainment_url);
		// for (int i = 0; i < entertainment_title.length; i++) {
			// Entertainment entertainment = new Entertainment();
			// entertainment.title = entertainment_title[i];
			// entertainment.logoHeadId = getLogoHeadInt(i + 1, context, "logo_selector_", R.drawable.logo_selector_1);
			// entertainment.url = entertainment_url[i];
			// returnList.add(entertainment);
		// }
		// return returnList;
	// }

	private int getLogoHeadInt(int intRes, Context context, String prefix, int defaultId) {
		Resources resources = context.getResources();
		int indentify = resources.getIdentifier(prefix + intRes, "drawable", context.getPackageName());
		if (indentify == 0) {
			return defaultId;
		} else {
			return indentify;
		}
	}

	private Bitmap getLogoHead(int intRes, Context context, String prefix, int defaultId) {
		Resources resources = context.getResources();
		int indentify = resources.getIdentifier(prefix + intRes, "drawable", context.getPackageName());
		if (indentify == 0) {
			return BitmapFactory.decodeResource(resources, defaultId);
		}
		Bitmap bmp = BitmapFactory.decodeResource(resources, indentify);
		if (bmp != null) {
			return bmp;
		} else {
			return BitmapFactory.decodeResource(resources, defaultId);
		}
	}

	public int getRpmValueFirstRes(int value) {
		int tmp = 0;
		return CTConstant.rpmValueRes[value];
	}

	public int getNumRes(int value) {
		return CTConstant.rpmValueRes[value];
	}

	public int getWattValue (int rpm , int level) {
		int rpmIndex = 0;
		if ( rpm < 20 ) {
			rpm = 20;
			rpmIndex = 0;
		}
		if ( rpm > 120 ) {
			rpm = 120;
			rpmIndex = 11;
		}
		
		rpmIndex = Math.round( (rpm -20 ) / 10.0f );
		if ( rpmIndex > 11 || rpmIndex < 0) {
			rpmIndex = 0;
		}
		
		return RunParamTableManager.getInstance(null).wattTable[level-1][rpmIndex];
	}

	/**
	 * 得到健身apk列表
	 */
	// public List<Fitness> getFitnessList(Context context) {
		// List<Fitness> returnList = new ArrayList<Fitness>();
		// String[] entertainment_title = context.getResources().getStringArray(R.array.fitness_content);
		// for (int i = 0; i < entertainment_title.length; i++) {
			// Fitness fitness = new Fitness();
			// fitness.content = entertainment_title[i];
			// fitness.picture = getLogoHead(i + 1, context, "fitness_", R.drawable.fitness_1);
			// returnList.add(fitness);
		// }
		// return returnList;
	// }

}
