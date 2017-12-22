package com.run.treadmill.runningParamManager;

import com.run.treadmill.db.UserInfoManager;
import com.run.treadmill.systemInitParam.InitParam;
import com.run.treadmill.util.CTConstant;
import com.run.treadmill.util.InterfaceUtils;
import com.run.treadmill.util.SafeKeyTimer;
import com.run.treadmill.util.StorageParam;
import com.run.treadmill.util.CTConstant.RunMode;

import android.content.Context;
import android.util.Log;


public class RunningParamFitness extends RunningParam {
	public String TAG = "RunningParamFitness";

	private int thirtySecCount = 0;
	private long runTime = 0;
	private InterfaceUtils mInterfaceUtils;
	private int lastStageHr = 0;
	private int curStageHr = 0;
	private int lastStageWatt = 0;
	private int curStageWatt = 0;

	private int minRpm = 48;
	private int maxRpm = 52;

	public int proMaleLevelTable[] = { 17,17,17,17,17, 17,17,17,17,17, 17,17,17,17,17, 
			17,17,17,17,17,  17,17,17,17,17,  17,17,17,17,17, };
	public int proFemaleLevelTable[] = { 16,16,16,16,16, 16,16,16,16,16, 16,16,16,16,16, 
			16,16,16,16,16, 16,16,16,16,16, 16,16,16,16,16, };

	private int lastFiveSecHrSum = 0;
	private boolean isReSumHr = false;
	private int reSumCount = 0;

	private int stageNum = 1;
	private long stageStartTime = 0;

	private int twoSecHr = 0;
	private int threeSecHr = 0;

	private boolean isShowLevel = false;


	public RunningParamFitness(Context context) {
		mContext = context;
		if( UserInfoManager.getInstance().getRunMode() == 
				RunMode.IDX_HOME_FITNESS_MODE.ordinal() ) { 
			mLevelItemValueArray = new int[InitParam.TOTAL_RUN_INTERVAL_NUM];

			targetHr = (int) (( 220 - UserInfoManager.getInstance().getAge(UserInfoManager.getInstance().getRunMode()) ) * 85 / 100.0);
		}
		mInterfaceUtils = new InterfaceUtils();
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
	public boolean getIsThirtySec() {
		thirtySecCount ++ ;
		if( thirtySecCount == 30 ) {
			thirtySecCount = 0;
			return true;
		}
		return false;
	}

	//hrc no hr 计时器
	private int hrcNoHrSecCount = 0;

	public int getHrcHRTimerSec() {
		hrcNoHrSecCount ++ ;
		return hrcNoHrSecCount;
	}
	public void resetHrcHRTimerSec() {
		hrcNoHrSecCount = 0;
	}

	//rpm no range 计时器
	private int rpmSecCount = 0;

	public int getRpmTimerSec() {
		rpmSecCount ++ ;
		return rpmSecCount;
	}
	public void resetRpmTimerSec() {
		rpmSecCount = 0;
	}

	@Override
	public boolean isRunEnd() {
		if( isRunEnd ) {
			return isRunEnd;
		}

		/*if ( targetHr >= UserInfoManager.getInstance().ecgValue ) {
			isRunEnd = false;
		}*/

		return isRunEnd;

	}

	@Override
	public void getRunStageNum() {

		if( isCoolDownPage || isRunEnd ) {
			return ;
		}
		if ( !isWarmUpPage && UserInfoManager.getInstance().rpm == 0 ) {
			if ( isAccThirSec() ) {
				HRC_RUN_STATUS = CTConstant.NO_RPM_STATUS;
				/*thirSecCount = 0;*/
				/*//增加1000MS
				alreadyRunTime += 1000;
				mShowRunTime += 1000;*/
				if ( isAccElevenSec() ) {
					HRC_RUN_STATUS = CTConstant.HRC_AUTO_END_STATUS;
					thirSecCount = 0;
					elevenSecCount = 0;
				}
			}
			Log.d(TAG, " == thirSecCount + " + thirSecCount);
		} else {
			if ( HRC_RUN_STATUS == CTConstant.NO_RPM_STATUS ) {
				HRC_RUN_STATUS = 0;
			}
			thirSecCount = 0;
			elevenSecCount = 0;
		}
		/*if ( HRC_RUN_STATUS == CTConstant.NO_RPM_STATUS ) {
			return ;
		}*/

		/*getIsAccOneMin();*/

		if( isWarmUpPage ) {
			warmUpRunTime += 1000;
			return ;
		}

		if( getIsAccOneMin() ) {
			runStageNum ++ ;
			if( runStageNum >= InitParam.TOTAL_RUN_INTERVAL_NUM ) {
				runStageNum = 0;
			}
		}

		//增加1000MS
		alreadyRunTime += 1000;
		mShowRunTime += 1000;

		if ( !isWarmUpPage && UserInfoManager.getInstance().ecgValue == 0 ) {
			int secCount = getHrcHRTimerSec();
			if( secCount % 17 == 0  ) {//31//16
				HRC_RUN_STATUS = CTConstant.HRC_AUTO_END_STATUS;
				//return ;
			} else if( secCount % 1 == 0  ) {
				HRC_RUN_STATUS = CTConstant.HRC_NO_HR_STATUS;
			}
			/*return ;*/
		} else {
			resetHrcHRTimerSec();
			if ( HRC_RUN_STATUS == CTConstant.FITNESS_OVER_TAG_HR ||
					HRC_RUN_STATUS == CTConstant.HRC_AUTO_END_STATUS ) {
				return ;
			} else if ( HRC_RUN_STATUS == CTConstant.HRC_NO_HR_STATUS ) {
				HRC_RUN_STATUS = 0;
			}
		}
		
		warmUpRunTime = 0;

		runTime = (long) (mShowRunTime - warmUpRunTime);
		setVo2MaxFirstValue(0);

		if ( UserInfoManager.getInstance().rpm > 0 && 
				UserInfoManager.getInstance().rpm < minRpm ) {
			if ( getRpmTimerSec() == 30 ) {
				HRC_RUN_STATUS = CTConstant.LOW_RPM;
			} else if ( getRpmTimerSec() == 32 ) {
				HRC_RUN_STATUS = CTConstant.HRC_AUTO_END_STATUS;
			}
		} else if ( UserInfoManager.getInstance().rpm > maxRpm ) {
			if ( getRpmTimerSec() == 30 ) {
				HRC_RUN_STATUS = CTConstant.OUT_RANGE_RPM;
			} else if ( getRpmTimerSec() == 32 ) {
				HRC_RUN_STATUS = CTConstant.HRC_AUTO_END_STATUS;
			}
		} else {
			if ( HRC_RUN_STATUS == CTConstant.FITNESS_OVER_TAG_HR ||
					HRC_RUN_STATUS == CTConstant.HRC_AUTO_END_STATUS ) {
				return ;
			} else if ( HRC_RUN_STATUS == CTConstant.OUT_RANGE_RPM ||
					HRC_RUN_STATUS == CTConstant.LOW_RPM ) {
				HRC_RUN_STATUS = 0;
			}
			resetRpmTimerSec();
		}

		//第一个阶段
		if ( stageNum == 1 ) {
			if ( runTime == (3 * 60 * 1000) || 
					runTime == (3 * 60 * 1000 - 1) || 
					runTime == (3 * 60 * 1000 - 2) || 
					runTime == (3 * 60 * 1000 - 3) || 
					runTime == (3 * 60 * 1000 - 4) ) {
				lastFiveSecHrSum += UserInfoManager.getInstance().ecgValue;
				if ( UserInfoManager.getInstance().ecgValue == 0 ) {
					isReSumHr = true;
					lastFiveSecHrSum = 0;
				}
			}
			if ( runTime == (3 * 60 * 1000) && 
					UserInfoManager.getInstance().ecgValue != 0 ) {
				stageNum = 2;
				stageStartTime = runTime;
				lastStageHr = curStageHr;
				lastStageWatt = curStageWatt;
			}

			if ( isReSumHr == true ) {
				reSumCount++;
				lastFiveSecHrSum += UserInfoManager.getInstance().ecgValue;
				if ( reSumCount == 6 ) {
					isReSumHr = false;
					reSumCount = 0;
					stageNum = 2;
					lastStageHr = curStageHr;
					lastStageWatt = curStageWatt;
					stageStartTime = runTime - 1000;
				} else if ( UserInfoManager.getInstance().ecgValue == 0 ) {
					isReSumHr = true;
					lastFiveSecHrSum = 0;
					reSumCount = 0;
				}
			}
		}

		/*Log.d(TAG, "=====   1211 isReSumHr " + isReSumHr + 
				 " reSumCount " + reSumCount);*/
		if ( stageNum == 2 ) {
			/*runStageNum = 1;*/
			for( int i = runStageNum; i < InitParam.TOTAL_RUN_INTERVAL_NUM; i++ ) {
				if ( mLevelItemValueArray[i] != 
						getLevelOfStage(stageNum, (int)(lastFiveSecHrSum / 5.0)) ) {
					isShowLevel = true;
				}
				mLevelItemValueArray[i] =
						getLevelOfStage(stageNum, (int)(lastFiveSecHrSum / 5.0));
			}
			if ( ( runTime - stageStartTime ) == 2 * 60 * 1000 ) {
				twoSecHr = UserInfoManager.getInstance().ecgValue;
			}
			if ( ( runTime - stageStartTime ) == 3 * 60 * 1000 ) {
				threeSecHr = UserInfoManager.getInstance().ecgValue;
				if ( Math.abs(threeSecHr - twoSecHr) > 6 ) {
					stageNum = 3;
					stageStartTime = runTime;
					lastStageHr = curStageHr;
					lastStageWatt = curStageWatt;
				}
			}
			if ( ( runTime - stageStartTime ) == 4 * 60 * 1000 ) {
				stageNum = 3;
				stageStartTime = runTime;
				lastStageHr = curStageHr;
				lastStageWatt = curStageWatt;
			}

			if ( ( runTime - stageStartTime ) == (3 * 60 * 1000) || 
					( runTime - stageStartTime ) == (3 * 60 * 1000 - 1) || 
					( runTime - stageStartTime ) == (3 * 60 * 1000 - 2) || 
					( runTime - stageStartTime ) == (3 * 60 * 1000 - 3) || 
					( runTime - stageStartTime ) == (3 * 60 * 1000 - 4) ) {
				lastFiveSecHrSum += UserInfoManager.getInstance().ecgValue;
			}

			if ( lastStageHr >= 110 && curStageHr >= 110 && 
					lastStageHr <= targetHr && curStageHr >= targetHr ) {
				HRC_RUN_STATUS = CTConstant.FITNESS_OVER_TAG_HR;
				alreadyRunTime -= 1000;
				mShowRunTime -= 1000;
			}

		}

		if ( stageNum == 3 ) {
			/*runStageNum = 2;*/
			for( int i = runStageNum; i < InitParam.TOTAL_RUN_INTERVAL_NUM; i++ ) {
				if ( mLevelItemValueArray[i] != 
						getLevelOfStage(stageNum, (int)(lastFiveSecHrSum / 5.0)) ) {
					isShowLevel = true;
				}
				mLevelItemValueArray[i] =
						getLevelOfStage(stageNum, (int)(lastFiveSecHrSum / 5.0));
			}
			if ( ( runTime - stageStartTime ) == 2 * 60 * 1000 ) {
				twoSecHr = UserInfoManager.getInstance().ecgValue;
			}
			if ( ( runTime - stageStartTime ) == 3 * 60 * 1000 ) {
				threeSecHr = UserInfoManager.getInstance().ecgValue;
				if ( Math.abs(threeSecHr - twoSecHr) > 6 ) {
					stageNum = 4;
					stageStartTime = runTime;
					lastStageHr = curStageHr;
					lastStageWatt = curStageWatt;
				}
			}
			if ( ( runTime - stageStartTime ) == 4 * 60 * 1000 ) {
				stageNum = 4;
				stageStartTime = runTime;
				lastStageHr = curStageHr;
				lastStageWatt = curStageWatt;
			}

			if ( ( runTime - stageStartTime ) == (3 * 60 * 1000) || 
					( runTime - stageStartTime ) == (3 * 60 * 1000 - 1) || 
					( runTime - stageStartTime ) == (3 * 60 * 1000 - 2) || 
					( runTime - stageStartTime ) == (3 * 60 * 1000 - 3) || 
					( runTime - stageStartTime ) == (3 * 60 * 1000 - 4) ) {
				lastFiveSecHrSum += UserInfoManager.getInstance().ecgValue;
			}

			if ( lastStageHr >= 110 && curStageHr >= 110 && 
					lastStageHr <= targetHr && curStageHr >= targetHr ) {
				HRC_RUN_STATUS = CTConstant.FITNESS_OVER_TAG_HR;
				alreadyRunTime -= 1000;
				mShowRunTime -= 1000;
			}

		}

		if ( stageNum == 4 ) {
			/*runStageNum = 3;*/
			for( int i = runStageNum; i < InitParam.TOTAL_RUN_INTERVAL_NUM; i++ ) {
				if ( mLevelItemValueArray[i] != 
						getLevelOfStage(stageNum, (int)(lastFiveSecHrSum / 5.0)) ) {
					isShowLevel = true;
				}
				mLevelItemValueArray[i] =
						getLevelOfStage(stageNum, (int)(lastFiveSecHrSum / 5.0));
			}
			if ( ( runTime - stageStartTime ) == 2 * 60 * 1000 ) {
				twoSecHr = UserInfoManager.getInstance().ecgValue;
			}
			if ( ( runTime - stageStartTime ) == 3 * 60 * 1000 ) {
				threeSecHr = UserInfoManager.getInstance().ecgValue;
				if ( Math.abs(threeSecHr - twoSecHr) > 6 ) {
					isRunEnd = true;
				}
			}
			if ( ( runTime - stageStartTime ) == 4 * 60 * 1000 ) {
				isRunEnd = true;
			}

			if ( lastStageHr >= 110 && curStageHr >= 110 && 
					lastStageHr <= targetHr && curStageHr >= targetHr ) {
				HRC_RUN_STATUS = CTConstant.FITNESS_OVER_TAG_HR;
				alreadyRunTime -= 1000;
				mShowRunTime -= 1000;
			}

		}

		//进入下一个阶段前判断心跳是否超出目标心跳
		if( targetHr <= UserInfoManager.getInstance().ecgValue ) {
			HRC_RUN_STATUS = CTConstant.FITNESS_OVER_TAG_HR;
			alreadyRunTime -= 1000;
			mShowRunTime -= 1000;
		} else {

		}

		setVo2MaxValue(runStageNum);

		curStageHr = UserInfoManager.getInstance().ecgValue;
		curStageWatt = mInterfaceUtils.getWattValue(UserInfoManager.getInstance().rpm, 
				UserInfoManager.getInstance().curLevel);

		/*Log.d(TAG,"RefreshParam runStageNum: " + runStageNum + 
				" mShowRunTime: " + mShowRunTime + " runTime: " + runTime+ " targetHr" + 
				targetHr + " ecg " + UserInfoManager.getInstance().ecgValue);*/
		Log.d(TAG,"lastStageWatt: " + lastStageWatt + 
				" curStageWatt: " + curStageWatt + " curStageHr: " + curStageHr+ " lastStageHr" + 
				lastStageHr);

	}

	public int getLevelOfStage(int stage, int sumHr) {
		int level = 0;
		if ( UserInfoManager.getInstance().getSex() == CTConstant.GENDER_BOY ) {
			switch (stage) {
				case 2:
					if ( sumHr < 90 ) {
						level = 32;
					} else if ( sumHr >= 90 && sumHr <= 105 ) {
						level = 30;
					} else if ( sumHr > 105 ) {
						level = 27;
					}
					break;
				case 3:
					if ( sumHr < 120 ) {
						level = 36;
					} else if ( sumHr >= 120 && sumHr <= 135 ) {
						level = 26;
					} else if ( sumHr > 135 ) {
						level = 30;
					}
					break;
				case 4:
					if ( sumHr < 120 ) {
						level = 36;
					} else if ( sumHr >= 120 && sumHr <= 135 ) {
						level = 21;
					} else if ( sumHr > 135 ) {
						level = 30;
					}
					break;
				default:
					break;
			}
		} else {
			switch (stage) {
				case 2:
					if ( sumHr < 80 ) {
						level = 19;
					} else if ( sumHr >= 80 && sumHr <= 90 ) {
						level = 22;
					} else if ( sumHr >= 91 && sumHr <= 100 ) {
						level = 11;
					} else if ( sumHr > 100 ) {
						level = 5;
					}
					break;
				case 3:
					if ( sumHr < 80 ) {
						level = 23;
					} else if ( sumHr >= 80 && sumHr <= 90 ) {
						level = 21;
					} else if ( sumHr >= 91 && sumHr <= 100 ) {
						level = 17;
					} else if ( sumHr > 100 ) {
						level = 11;
					}
					break;
				case 4:
					if ( sumHr < 80 ) {
						level = 25;
					} else if ( sumHr >= 80 && sumHr <= 90 ) {
						level = 23;
					} else if ( sumHr >= 91 && sumHr <= 100 ) {
						level = 19;
					} else if ( sumHr > 100 ) {
						level = 17;
					}
					break;
				default:
					break;
			}
		}
		return level;
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

	public void setVo2MaxFirstValue ( int num ) {
		/*UserInfoManager.getInstance().fitnessVo2Max = 
				RunParamTableManager.getInstance(mContext).fintessVO2Max[num];*/
		UserInfoManager.getInstance().fitnessVo2Max = 0;
	}

	public void setVo2MaxValue ( int num ) {
		/*Log.d(TAG,"setVo2MaxValue num "  + num);
		if( isfitnessVo2StopSkip ) {	 
			num += 30;
		}
		num += 1;
		if ( num < 0 ) {
			num = 0;
		} else if ( num >= 41 ) {
			num = 40;
		}
		UserInfoManager.getInstance().fitnessVo2Max = 
				RunParamTableManager.getInstance(mContext).fintessVO2Max[num];*/
		int lastWorkRatt = lastStageWatt * 6;
		int curWorkRatt = curStageWatt * 6;
		int weight = Integer.parseInt( UserInfoManager.getInstance().getmWeigth( 
				UserInfoManager.getInstance().getRunMode() ) );

		float V02SM1 = 1.8f * lastWorkRatt / weight + 7;
		float V02SM2 = 1.8f * curWorkRatt / weight + 7;

		float B = 0f;
		if ( curStageHr <= lastStageHr ) {
			B = ( V02SM2 - V02SM1 );
		} else {
			B = ( V02SM2 - V02SM1 ) / ( curStageHr - lastStageHr );
		}

		int HRMAX = 220 - UserInfoManager.getInstance().getAge( 
				UserInfoManager.getInstance().getRunMode() ) ;
		UserInfoManager.getInstance().fitnessVo2Max = V02SM2 + B * ( HRMAX - curStageHr );
		if ( UserInfoManager.getInstance().fitnessVo2Max > 100) {
			UserInfoManager.getInstance().fitnessVo2Max = 100;
		}
		UserInfoManager.getInstance().fitnessVo2Max = 
				(float) Math.round( UserInfoManager.getInstance().fitnessVo2Max * 10 ) / 10;
	}

}
