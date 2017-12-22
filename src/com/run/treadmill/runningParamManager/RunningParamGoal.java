package com.run.treadmill.runningParamManager;

import com.run.treadmill.db.UserInfoManager;
import com.run.treadmill.systemInitParam.InitParam;
import com.run.treadmill.util.CTConstant;
import com.run.treadmill.util.SafeKeyTimer;
import com.run.treadmill.util.StorageParam;

import android.content.Context;
import android.util.Log;


public class RunningParamGoal extends RunningParam {
	public String TAG = "RunningParamGoal";

			
	public RunningParamGoal(Context context) {
		mContext = context;
	}

	//重置1分钟定时器
	@Override
	public boolean isAccOneMin() {
		if( oneMinCount == 60 ) {
			oneMinCount = 0;
			return true;
		}
		return false;
	}

	//1分钟计时器
	public boolean getIsAccOneMin() {
		oneMinCount ++ ;
		if( oneMinCount == 60 ) {
			return true;
		}
		return false;
	}

	//common 计时器
	public int getCommonTimerSec() {
		commonSecCount ++ ;
		return commonSecCount;
	}
	public void resetCommonTimerSec() {
		commonSecCount = 0;
	}

	@Override
	public boolean isRunEnd() {
		if( isRunEnd ) {
			return isRunEnd;
		}
		if( isSetTime ) {
//			Log.d(TAG,"returnRunTime " + returnRunTime + "SET_TIME_VALUE = " + SET_TIME_VALUE * 60 * 1000);
			isRunEnd = ( mShowRunTime >= runMaxTime * 60 * 1000 );
		} else if ( isSetDistance ) {
			isRunEnd = ( alreadyRunDistance >= UserInfoManager.getInstance().getTargetDistance() );
			Log.d(TAG,"isRunEnd " + isRunEnd);
		} else if ( isSetCalories ) {
			isRunEnd = ( alreadyRunCalories >= UserInfoManager.getInstance().getTargetCalorie() );
		}
		return isRunEnd;
	}

	@Override
	public void getRunStageNum() {
		if( isCoolDownPage || isRunEnd ) {
			return ;
		}
		if ( UserInfoManager.getInstance().rpm == 0 ) {
			if ( isAccThirSec() ) {
				HRC_RUN_STATUS = CTConstant.NO_RPM_STATUS;
				thirSecCount = 0;
				//增加1000MS
				alreadyRunTime += 1000;
				mShowRunTime += 1000;
			}
		} else {
			thirSecCount = 0;
		}
		if ( HRC_RUN_STATUS == CTConstant.NO_RPM_STATUS ) {
			return ;
		}

		//增加1000MS
		alreadyRunTime += 1000;
		mShowRunTime += 1000;
		
		if( UserInfoManager.getInstance().getTargetTime(UserInfoManager.getInstance().getRunMode()) != 0f ) {
			runStageNum = (int) ( mShowRunTime / 
					( runMaxTime * 60 * 1000 / InitParam.TOTAL_RUN_INTERVAL_NUM ) );
			if( runStageNum >= InitParam.TOTAL_RUN_INTERVAL_NUM ) {
				runStageNum = InitParam.TOTAL_RUN_INTERVAL_NUM - 1;
			}
			getIsAccOneMin();
		} else {
			if ( mShowRunTime >= (InitParam.MAX_RUN_TIME + 1) * 60 * 1000 ) {
				mShowRunTime = 0;
			}

			if( getIsAccOneMin() ) {
				runStageNum ++ ;
				if( runStageNum >= InitParam.TOTAL_RUN_INTERVAL_NUM ) {
					runStageNum = 0;
				}
			}
		}
	}

	@Override
	public boolean isShowLevelIcon() {
		if( runStageNum >= 0 && 
				runStageNum != oldRunStageNum 
				&& runStageNum != InitParam.TOTAL_RUN_INTERVAL_NUM ) {
			oldRunStageNum = runStageNum;
			return true;
		}
		return false;
	}	

	@Override
	public float getShowRunTime() {
		return mShowRunTime;
	}
}
