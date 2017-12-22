package com.run.treadmill.runningParamManager;


import com.run.treadmill.systemInitParam.InitParam;

import android.content.Context;


public abstract class RunningParam implements ParamInterface {
	public String TAG = "RunningParam";

	public Context mContext;
	public int oneMinCount = 0;
	public int thirSecCount = 0;
	public int elevenSecCount = 0;
	public boolean isWarmUpPage = false;
	public int warmUpLevel = 1;
	public int warmUpRunTime = 0;
	public int targetHr;

	public boolean isPreparePage = false;
	public int countDown = InitParam.CountDown;
	
	public boolean isRefreshPage = false;

	public boolean isPausePage = false;

	public boolean isCoolDownPage = false;
	public int coolDownLevel = 0;
	public int coolDownTimer = 0;

	public int mLevelItemValueArray[] = new int[InitParam.TOTAL_RUN_INTERVAL_NUM];
	public float alreadyRunDistance = 0f;
	public float alreadyRunCalories = 0f;
	public float mShowRunDistance = 0f;
	public float mShowRunCalories = 0f;
	public int alreadyRunTime = 0;
	public int mShowRunTime = 0;

	public int alreadyRunLevel = 0;

	public boolean isSetTime = false;
	public boolean isSetDistance = false;
	public boolean isSetCalories = false;

	//程式跑步模式的分段索引
	public int oldRunStageNum = -1;
	public int runStageNum = -1;
	public int runMaxTime = -1;
	public boolean incycleRun = false;

	public boolean isfitnessVo2StopSkip = false;
	
	public boolean isRunEnd = false;
	public boolean isRelease = false;
	

	public int commonSecCount = 0;

	public int HRC_RUN_STATUS = 0;

	public boolean isJumpInterface = false;

	//30秒计时器
	@Override
	public boolean isAccThirSec() {
		if( thirSecCount == 30 ) {
			
		} else {
			thirSecCount ++ ;
		}

		if( thirSecCount == 30 ) {
			return true;
		}
		return false;
	}

	//fitness专用
	//10秒计时器
	@Override
	public boolean isAccElevenSec() {
		elevenSecCount ++ ;
		if( elevenSecCount == 11 ) {
			return true;
		}
		return false;
	}
}
