package com.run.treadmill.db;

import java.io.Serializable;

import com.run.treadmill.util.CTConstant;
import com.run.treadmill.util.CTConstant.RunMode;

public class UserInfo implements Serializable {

	//名字
	public String mUserName;
	//性别  0为男 1为女
	public int mSex = CTConstant.GENDER_BOY;
	//身高
	public String mUserHeight;
	//体重
	public String[] mUserWeight = {"70", "70", "70", "70", "70",
			"70", "70", "70",};
	//目标时间
	public int[] mTargetTime = {20, 20, 20, 20, 20,
			20, 20, 20,};
	//目标距离
	public float mTargetDistance = 0f;
	//目标卡路里
	public float mTargetCalorie = 0f;
	//hrc target value
	public float mTargetHrcValue = 0f;
	//目标跑步类型
	public int mTargetType = 0;
	//年龄
	public int[] mAge = {20, 20, 20, 20, 20,
			20, 20, 20,};

	//累计跑步
	public long mTotal;

	//选择跑步模式
	public int mRunMode = 0;
	//跑步总时间,单位ms
	public float mTotalTime = 20f;
	//跑步总距离,单位km
	public float mTotalCalories = 0;
	//跑步总卡路里,单位kcal
	public float mTotalDistance = 0;

	//跑步总level
	public int mTotalLevel = 0;
	
	public String getmUserName() {
		return mUserName;
	}
	public void setmUserName(String mUserName) {
		this.mUserName = mUserName;
	}
	
	public String getmHeight() {
		return mUserHeight;
	}
	public void setmHeight(String mHeight) {
		this.mUserHeight = mHeight;
	}
	
	/*public String getmWeigth() {
		return mUserWeight;
	}
	public void setmWeigth(String mWeigth) {
		this.mUserWeight = mWeigth;
	}*/
	
	/*public int getAge() {
		return mAge;
	}
	public void setAge(int age) {
		this.mAge = age;
	}*/
	
	public long getmTotal() {
		return mTotal;
	}
	public void setmTotal(long mTotal) {
		this.mTotal = mTotal;
	}
	
	public int getSex() {
		return mSex;
	}
	public void setSex(int sex) {
		this.mSex = sex;
	}

	public int getRunMode() {
		return mRunMode;
	}
	public void setRunMode(int runMode) {
		this.mRunMode = runMode;
	}

	public float getTotalTime() {
		return mTotalTime;
	}
	public void setTotalTime(float totalTime) {
		this.mTotalTime = totalTime;
	}

	public void setTargetDistance(float TargetDistance) {
		mTargetDistance = TargetDistance;
	}
	public float getTTargetDistance() {
		return mTargetDistance;
	}
	public void setTargetCalorie(float TargetCalorie) {
		mTargetCalorie = TargetCalorie;
	}
	public float getTargetCalorie() {
		return mTargetCalorie;
	}

}
