package com.run.treadmill.runningParamManager;

import com.run.treadmill.db.UserInfoManager;
import com.run.treadmill.systemInitParam.InitParam;
import com.run.treadmill.util.CTConstant;
import com.run.treadmill.util.StorageParam;

import android.content.Context;
import android.util.Log;


public class RunningParamHRC extends RunningParam {
	public String TAG = "RunningParamHRC";

	private int tenSecCount = 0;
	private int commonSecCount = 0;
	boolean isMinLevel = false;

	private int perLevelValue = 0;
	private boolean isJustOpen = true;

	private boolean isShowLevel = false;

	public RunningParamHRC(Context context) {
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

	//30s计时器
	public boolean getIsTenSec() {
		tenSecCount ++ ;
		if( tenSecCount == 10 ) {
			tenSecCount = 0;
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

	private int hrcNoHrSecCount = 0;
	//hrc no hr 计时器
	public int getHrcHRTimerSec() {
		hrcNoHrSecCount ++ ;
		return hrcNoHrSecCount;
	}
	public void resetHrcHRTimerSec() {
		hrcNoHrSecCount = 0;
	}

	@Override
	public boolean isRunEnd() {
		if( isRunEnd ) {
			return isRunEnd;
		}

		isRunEnd = ( mShowRunTime >= runMaxTime * 60 * 1000 );
		return isRunEnd;

	}

	@Override
	public void getRunStageNum() {
		if( isCoolDownPage || isRunEnd ) {
			return ;
		}
		//NO RPM 和 NO HR同时休出现时，以NO HR优先
		if ( UserInfoManager.getInstance().rpm == 0 &&
				UserInfoManager.getInstance().ecgValue != 0 ) {
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
		//NO RPM 和 NO HR同时休出现时，以NO HR优先
		if ( HRC_RUN_STATUS == CTConstant.NO_RPM_STATUS && 
				UserInfoManager.getInstance().ecgValue != 0 ) {
			return ;
		}

		//新增处理刚开始运动时，如果无心跳，先显示提示，再处理退出界面
		if ( UserInfoManager.getInstance().ecgValue == 0 && isJustOpen ) {
			if( hrcNoHrSecCount == 16 ) {//18
				HRC_RUN_STATUS = CTConstant.HRC_AUTO_END_STATUS;
			}
			/*if ( HRC_RUN_STATUS == CTConstant.HRC_NO_HR_STATUS || 
					HRC_RUN_STATUS == CTConstant.HRC_AUTO_END_STATUS ) {
				getHrcHRTimerSec();
				return ;
			}*/
		}

		//增加1000MS
		alreadyRunTime += 1000;
		mShowRunTime += 1000;

		//刚开启HRC时,无心跳，进入HRC_NO_HR_STATUS界面 
		if ( UserInfoManager.getInstance().ecgValue == 0 && isJustOpen ) {
			int secCount = getHrcHRTimerSec();
			if( secCount % 15 == 0  ) {//30
				HRC_RUN_STATUS = CTConstant.HRC_AUTO_END_STATUS;
			} else if( secCount % 1 == 0  ) {//} else if( secCount % 16 == 0  ) {//15
				HRC_RUN_STATUS = CTConstant.HRC_NO_HR_STATUS;
			} 
			return ;
		} else {
			isJustOpen = false;
			resetHrcHRTimerSec();
			/*//test start
			UserInfoManager.getInstance().ecgValue = 0;
			//test end*/
			if ( HRC_RUN_STATUS == CTConstant.HRC_NO_HR_STATUS ) {

			} else {
				HRC_RUN_STATUS = 0;
			}
		}
		/*procHRCModeData();*/

		if ( UserInfoManager.getInstance().getTargetTime(
				UserInfoManager.getInstance().getRunMode()) != 0f ) {
			runStageNum = (int) ( mShowRunTime / 
					( runMaxTime * 60 * 1000 / InitParam.TOTAL_RUN_INTERVAL_NUM ) );
			if( runStageNum >= InitParam.TOTAL_RUN_INTERVAL_NUM ) {
				runStageNum = InitParam.TOTAL_RUN_INTERVAL_NUM - 1;
			}
			getIsAccOneMin();
		} else if( mShowRunTime >= (InitParam.MAX_RUN_TIME + 1) * 60 * 1000 
				|| mShowRunCalories > InitParam.MaxGoalCal 
				|| mShowRunDistance >= (InitParam.MaxGoalDis + 0.9f) ) {
			if ( mShowRunTime >= (InitParam.MAX_RUN_TIME + 1) * 60 * 1000 ) {
				mShowRunTime = 0;
			}
			if ( mShowRunCalories > InitParam.MaxGoalCal ) {
				mShowRunCalories = 0;
			}
			if ( mShowRunDistance >= (InitParam.MaxGoalDis + 0.9f) ) {
				mShowRunDistance = 0;
			}
			if( getIsAccOneMin() ) {
				runStageNum ++ ;
				if( runStageNum >= InitParam.TOTAL_RUN_INTERVAL_NUM ) {
					runStageNum = 0;
				}
			}
		}
		procHRCModeData();

		/*getIsAccOneMin();*/

	}

	@Override
	public boolean isShowLevelIcon() {
		if( runStageNum >= 0 && 
				runStageNum != oldRunStageNum 
				&& runStageNum != InitParam.TOTAL_RUN_INTERVAL_NUM || isShowLevel ) {
			oldRunStageNum = runStageNum;
			isShowLevel = false;
			return true;
		}
		return false;
	}	

	@Override
	public float getShowRunTime() {
		return mShowRunTime;
	}

	public void procHRCModeData() {
		if( getIsTenSec() && (0 != UserInfoManager.getInstance().ecgValue) ) {
			Log.d(TAG, "SpeedItem " + mLevelItemValueArray[runStageNum]);

			perLevelValue = mLevelItemValueArray[runStageNum];
			/*runStageNum ++;
			if( runStageNum > 29 ) {
				runStageNum = 0;
			}*/

			if( ( UserInfoManager.getInstance().getTargetHrcValue() - 25 )
					> UserInfoManager.getInstance().ecgValue ) {
				mLevelItemValueArray[runStageNum] = perLevelValue;

				mLevelItemValueArray[runStageNum] += 3;

				if( mLevelItemValueArray[runStageNum] >= StorageParam.getMaxLevel(mContext) ) {
					mLevelItemValueArray[runStageNum] = StorageParam.getMaxLevel(mContext);
				}
				isShowLevel = true;
			} else if( ( UserInfoManager.getInstance().getTargetHrcValue() - 15 )
					> UserInfoManager.getInstance().ecgValue ) {

				mLevelItemValueArray[runStageNum] = perLevelValue;

				mLevelItemValueArray[runStageNum] += 2;

				if( mLevelItemValueArray[runStageNum] >= StorageParam.getMaxLevel(mContext) ) {
					mLevelItemValueArray[runStageNum] = StorageParam.getMaxLevel(mContext);
				}
				isShowLevel = true;
			} else if( ( UserInfoManager.getInstance().getTargetHrcValue() - 5 )
					> UserInfoManager.getInstance().ecgValue ) {

				mLevelItemValueArray[runStageNum] = perLevelValue;

				mLevelItemValueArray[runStageNum] += 1;

				if( mLevelItemValueArray[runStageNum] >= StorageParam.getMaxLevel(mContext) ) {
					mLevelItemValueArray[runStageNum] = StorageParam.getMaxLevel(mContext);
				}
				isShowLevel = true;
			} else if( ( UserInfoManager.getInstance().getTargetHrcValue() + 25 )
					< UserInfoManager.getInstance().ecgValue ) {

				mLevelItemValueArray[runStageNum] = perLevelValue;

				mLevelItemValueArray[runStageNum] -= 2;

				if( mLevelItemValueArray[runStageNum] <= StorageParam.getMinLevel(mContext) ) {
					mLevelItemValueArray[runStageNum] = StorageParam.getMinLevel(mContext);
					Log.d(TAG,"=====  0");
				}
				isShowLevel = true;
			} else if( ( UserInfoManager.getInstance().getTargetHrcValue() + 15 )
					< UserInfoManager.getInstance().ecgValue ||
					( UserInfoManager.getInstance().getTargetHrcValue() + 5 )
					< UserInfoManager.getInstance().ecgValue ) {

				mLevelItemValueArray[runStageNum] = perLevelValue;

				mLevelItemValueArray[runStageNum] -= 1;

				if( mLevelItemValueArray[runStageNum] <= StorageParam.getMinLevel(mContext) ) {
					mLevelItemValueArray[runStageNum] = StorageParam.getMinLevel(mContext);
				}
				isShowLevel = true;
			} else {

				mLevelItemValueArray[runStageNum] = perLevelValue;
			}

			for( int i = runStageNum; i < InitParam.TOTAL_RUN_INTERVAL_NUM; i++ ) {
				mLevelItemValueArray[i] =
						mLevelItemValueArray[runStageNum];
			}
			
			Log.d(TAG,"TargetHrc " + UserInfoManager.getInstance().getTargetHrcValue() + "ecgValue "
					+ UserInfoManager.getInstance().ecgValue + "SpeedItem " + mLevelItemValueArray[runStageNum]);
		}

		/*if( mLevelItemValueArray[runStageNum] <= StorageParam.getMinLevel(mContext) ) {
			mLevelItemValueArray[runStageNum] = StorageParam.getMinLevel(mContext);
			isMinLevel = true;
			Log.d(TAG,"=====  1");
		}*/
		if( ( 0 == UserInfoManager.getInstance().ecgValue ) && isMinLevel ) {
			Log.d(TAG,"=====  2");
			HRC_RUN_STATUS = CTConstant.HRC_AUTO_END_STATUS;
		} else if( 0 == UserInfoManager.getInstance().ecgValue ) {
			HRC_RUN_STATUS = CTConstant.HRC_NO_HR_STATUS;
			int commonSecCount = getCommonTimerSec();
			if ( ( commonSecCount - 16 )  % 10 == 0  && ( commonSecCount - 16 ) / 10 != 0 
					&& commonSecCount > 16 ) {
				perLevelValue = mLevelItemValueArray[runStageNum];
				/*runStageNum ++;
				if( runStageNum > 29 ) {
					runStageNum = 0;
				}*/
				mLevelItemValueArray[runStageNum] = perLevelValue;
				mLevelItemValueArray[runStageNum] -= 1;
				if( mLevelItemValueArray[runStageNum] <= StorageParam.getMinLevel(mContext) ) {
					mLevelItemValueArray[runStageNum] = StorageParam.getMinLevel(mContext);
					isMinLevel = true;
				}
				for( int i = runStageNum; i < InitParam.TOTAL_RUN_INTERVAL_NUM; i++ ) {
					mLevelItemValueArray[i] =
							mLevelItemValueArray[runStageNum];
				}
				isShowLevel = true;

			}
//			Log.d(TAG,"=====  3 == " + mLevelItemValueArray[runStageNum]);
		} else {
//			Log.d(TAG,"=====  4");
			resetCommonTimerSec();
			HRC_RUN_STATUS = 0;
			isMinLevel = false;
		}

	}
}
