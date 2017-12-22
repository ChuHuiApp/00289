package com.run.treadmill.db;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.run.treadmill.serialutil.Command.FANSTATUS;
import com.run.treadmill.serialutil.Command.SYSRUNSTATUS;
import com.run.treadmill.util.CTConstant.RunMode;



public class UserInfoManager {

	public static final String TAG = "UserInfoManager";
	private static UserInfoManager mInstance = null;
	private UserInfo mUserInfo = null;
	private ArrayList<Double> speedList = new ArrayList<Double>();
	private ArrayList<Double> speedTimeList = new ArrayList<Double>();
	private ArrayList<Double> levelList = new ArrayList<Double>();
	private ArrayList<Double> levelTimeList = new ArrayList<Double>();
	private ArrayList<Double> hrList = new ArrayList<Double>();
	private ArrayList<Double> hrTimeList = new ArrayList<Double>();
	
	public int sysRunStatus = SYSRUNSTATUS.STA_NOR.ordinal();
	public int vrSceneVideoNo = 0;

	public int buzzerType = 0;
	public int buzzerCount = 0;

	public int curLevel = 0;

	public float actualSpeed = 0f;
	public float actualIncline = 0f;

	public int ecgValue = 0;
	public int ecgDevice = 0;

	public int rpm = 0;

	public float fitnessVo2Max = 0f; 

	public int fanStatus = FANSTATUS.TURN_OFF.ordinal();

	public int mediaMode = -1;
	public boolean mIsMediaMode = false;
	public boolean mIsreleaseQuitFlag = false;

	public static UserInfoManager getInstance() {
		synchronized ( UserInfoManager.class ) {
			if ( mInstance == null ) {
				mInstance = new UserInfoManager();
			}
		}
		return mInstance;
	}
	
    public UserInfoManager()
    {
    	mUserInfo = new UserInfo();
    }

    public UserInfo getUserInfo()
    {
    	return mUserInfo;
    }
	
	public String getmUserName() {
		return mUserInfo.mUserName;
	}
	public void setmUserName(String mUserName) {
		mUserInfo.mUserName = mUserName;
	}
	
	public String getmHeight() {
		return mUserInfo.mUserHeight;
	}
	public void setmHeight(String mHeight) {
		mUserInfo.mUserHeight = mHeight;
	}
	
	public String getmWeigth(int runMode) {
		return mUserInfo.mUserWeight[runMode];
	}
	public void setmWeigth(String mWeigth, int runMode) {
		mUserInfo.mUserWeight[runMode] = mWeigth;
	}
	
	public int getAge(int runMode) {
		return mUserInfo.mAge[runMode];
	}
	public void setAge(int age, int runMode) {
		mUserInfo.mAge[runMode] = age;
	}

	public void setTargetTime(int totalTime, int runMode) {
		mUserInfo.mTargetTime[runMode] = totalTime;
	}
	public int getTargetTime(int runMode) {
		return mUserInfo.mTargetTime[runMode];
	}
	public void setTargetHrcValue(float targetHrcValue) {
		mUserInfo.mTargetHrcValue = targetHrcValue;
	}
	public float getTargetHrcValue() {
		return mUserInfo.mTargetHrcValue;
	}
	public void setTargetDistance(float TargetDistance) {
		mUserInfo.mTargetDistance = TargetDistance;
	}
	public float getTargetDistance() {
		return mUserInfo.mTargetDistance;
	}
	public void setTargetCalorie(float TargetCalorie) {
		mUserInfo.mTargetCalorie = TargetCalorie;
	}
	public float getTargetCalorie() {
		return mUserInfo.mTargetCalorie;
	}
	
	public long getmTotal() {
		return mUserInfo.mTotal;
	}
	public void setmTotal(long mTotal) {
		mUserInfo.mTotal = mTotal;
	}
	
	public int getSex() {
		return mUserInfo.mSex;
	}
	public void setSex(int sex) {
		mUserInfo.mSex = sex;
	}
	
	public int getRunMode() {
		return mUserInfo.mRunMode;
	}
	public void setRunMode(int runMode) {
		mUserInfo.mRunMode = runMode;
	}

	public float getTargetRunType() {
		return mUserInfo.mTargetType;
	}
	public void setTargetRunType(int targetType) {
		mUserInfo.mTargetType = targetType;
	}

	public float getTotalTime() {
		return mUserInfo.mTotalTime;
	}
	public void setTotalTime(float totalTime) {
		mUserInfo.mTotalTime = totalTime;
	}

	public float getTotalCalories() {
		return mUserInfo.mTotalCalories;
	}
	public void setTotalCalories(float totalCalories) {
		mUserInfo.mTotalCalories = totalCalories;
	}
	
	public float getTotalDistance() {
		return mUserInfo.mTotalDistance;
	}
	public void setTotalDistance(float totalDistance) {
		mUserInfo.mTotalDistance = totalDistance;
	}
	
	/*public ArrayList<Double> getSpeedList() {
		return speedList;
	}
	public ArrayList<Double> getSpeedTimeList() {
		return speedTimeList;
	}*/
	/*public void addSpeedList(float speed, float time) {
		speedList.add((double) speed);
		speedTimeList.add((double) time);
	}*/

	public ArrayList<Double> getLevelList() {
		return levelList;
	}
	public ArrayList<Double> getLevelTimeList() {
		return levelTimeList;
	}
	public void addLevelList(float level, float time) {
		levelList.add((double) level);
		levelTimeList.add((double) time);
	}

	public ArrayList<Double> getHrList() {
		return hrList;
	}
	public ArrayList<Double> getHrTimeList() {
		return hrTimeList;
	}
	public void addHrList(float hr, float time) {
		hrList.add((double) hr);
		hrTimeList.add((double) time);
	}

	public int getTotalLevel() {
		return mUserInfo.mTotalLevel;
	}
	public void setTotalLevel(int level) {
		mUserInfo.mTotalLevel = level;
	}
	
	public void clearList() {
		speedList.clear();
		levelList.clear();
		hrList.clear();
		speedTimeList.clear();
		levelTimeList.clear();
		hrTimeList.clear();
	}

}
