package com.run.treadmill.runningModeActivity;


import java.util.Locale;

import com.run.treadmill.BaseActivity;
import com.run.treadmill.R;
import com.run.treadmill.SummaryActivity;
import com.run.treadmill.db.UserInfoManager;
import com.run.treadmill.floatWindow.FloatWindowManager;
import com.run.treadmill.manager.ErrorProcManager;
import com.run.treadmill.manager.SelfAudioManager;
import com.run.treadmill.runningParamManager.RunParamTableManager;
import com.run.treadmill.runningParamManager.RunningParamHRC;
import com.run.treadmill.selfdefView.BlurringView;
import com.run.treadmill.selfdefView.LongClickImage;
import com.run.treadmill.selfdefView.ParamView;
import com.run.treadmill.selfdefView.VerticalSeekBar;
import com.run.treadmill.serialutil.Command;
import com.run.treadmill.serialutil.SerialUtils;
import com.run.treadmill.serialutil.Command.FANSTATUS;
import com.run.treadmill.serialutil.Command.SYSRUNSTATUS;
import com.run.treadmill.systemInitParam.InitParam;
import com.run.treadmill.util.BaseTimer;
import com.run.treadmill.util.BaseTimer.HrBeatTimerCallBack;
import com.run.treadmill.util.BaseTimer.ParamRefreshTimerCallBack;
import com.run.treadmill.util.BaseTimer.TimerCallBack;
import com.run.treadmill.util.CTConstant;
import com.run.treadmill.util.CTConstant.MediaENItem;
import com.run.treadmill.util.ImageUtil;
import com.run.treadmill.util.InterfaceUtils;
import com.run.treadmill.util.MyUtils;
import com.run.treadmill.util.StorageParam;
import com.run.treadmill.util.Support;
import com.run.treadmill.util.CTConstant.MediaItem;
import com.run.treadmill.util.CTConstant.RunMode;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class RunningHRCActivity extends BaseActivity implements OnClickListener, 
	TimerCallBack, ParamRefreshTimerCallBack, HrBeatTimerCallBack {

	private String TAG = "RunningHRCActivity";

	private int captionHint[] = { 
			R.string.string_hill_caption, R.string.string_interval_caption, R.string.string_goal_caption,
			R.string.string_quick_start_caption, R.string.string_virtualreality_caption, R.string.string_HRC_caption,
			R.string.string_fitnesstest_caption, R.string.string_user_program_cap, };

	private RelativeLayout mLayout_running;
	private BlurringView mBlurringView;
	private RelativeLayout blurredView;

	private RelativeLayout top_layout;
	private ImageView img_sportmode_countdown;
	private RelativeLayout volume_layout;
	private ImageView btn_media_volume;
	private ImageView sb_media_volume_bk;
	private VerticalSeekBar mVerticalSeekBar;

	private TextView text_level;
	private TextView text_time;
	private TextView text_distance;
	private TextView text_calories;
	private ImageView img_pulse;
	private TextView text_pulse;
	private TextView text_watt;
	private TextView text_speed;

	private LongClickImage btn_sportmode_down;
	private LongClickImage btn_sportmode_up;

	private ImageView btn_sportmode_media_in;

	private ImageView btn_sportmode_media_out;
	private ImageView btn_media_mp3;
	private ImageView btn_media_mp4;
	private ImageView btn_media_i71;
	private ImageView btn_media_bai;
	private ImageView btn_media_av;
	private ImageView btn_media_weibo;
	private ImageView btn_media_6;
	private ImageView btn_media_7;
	private ImageView btn_media_8;
	private ImageView btn_media_9;

	private ImageView btn_sportmode_stop;

	private ImageView btn_fan;

	private RelativeLayout pause_page;
	private ImageView btn_pause_quit;
	private ImageView btn_pause_continue;

	private RelativeLayout cooldown_page;
	private ImageView btn_sportmode_cooldown;

	private RelativeLayout error_status_layout;
	private ImageView error_status_icon;

	private Context mContext;

	private InterfaceUtils mInterfaceUtils;

	private RunningParamHRC mRunningParam = null;
	private BaseTimer mCoolDownTimer = new BaseTimer();
	private BaseTimer mPausePageTimer = mCoolDownTimer;
	private BaseTimer mPreparePageTimer = mCoolDownTimer;
	private BaseTimer mRunParamRefreshTimer = new BaseTimer();
	private BaseTimer mHrBeatTimer = new BaseTimer();

	private boolean needResume = false;
	
	private boolean isMetric = true;
	private String language;

	private boolean isEnterPause = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;
		isMetric = StorageParam.getIsMetric(mContext);
		Locale locale = getResources().getConfiguration().locale;
		language = locale.getLanguage();
		if ( !language.endsWith("zh") ) {
			setContentView(R.layout.activity_running_en);
		} else {
			setContentView(R.layout.activity_running);
		}
		initSurfaceView();
		setListenerEvent();

		mInterfaceUtils = new InterfaceUtils();
		initParamDataAndRunTimer();

		startTimerForPrepare(false);

		btn_sportmode_down.setEnabled(false);
		btn_sportmode_up.setEnabled(false);
		btn_sportmode_down.setTag(-1);
		btn_sportmode_up.setTag(-1);
	}

    @Override
    public void onResume() {
    	Log.d(TAG,"onResume function!");
    	super.onResume();
    	if ( isEnterPause && 
    			UserInfoManager.getInstance().mIsMediaMode && needResume ) {
    		isEnterPause = false;
    		FloatWindowManager.getInstance(mContext).quitBackHomeOrRunningSurface();
    	}
    	if( !UserInfoManager.getInstance().mIsMediaMode && needResume ) {
    		if( UserInfoManager.getInstance().mIsreleaseQuitFlag ) {
    			UserInfoManager.getInstance().mIsreleaseQuitFlag = false;
    			releaseQuitProc();
    			return ;
    		}
    		reSettingParamShow();
    		mLayout_running.setVisibility(View.VISIBLE);
    		if( UserInfoManager.getInstance().sysRunStatus == SYSRUNSTATUS.STA_ERROR_PAGE.ordinal() ) {
    			showErrorMsgPage();
    			if( mRunningParam.HRC_RUN_STATUS == CTConstant.HRC_NO_HR_STATUS ) {
    				startTimerToRefreshParam();
    			}
    		} else if( UserInfoManager.getInstance().sysRunStatus == SYSRUNSTATUS.STA_MOT.ordinal() ) {
    			startTimerToRefreshParam();
    			setRunProLevel(mRunningParam.runStageNum);
    		} else if( UserInfoManager.getInstance().sysRunStatus == SYSRUNSTATUS.STA_PAUSE.ordinal() ) {
    			setRunProLevel(mRunningParam.runStageNum);
    			enterPausePage();
    		} else if( UserInfoManager.getInstance().sysRunStatus == SYSRUNSTATUS.STA_COOL_DOWN.ordinal() ) {
    			setRunProLevel(mRunningParam.runStageNum);
    			enterCoolDownPage();
    			startTimerToRefreshParam();
    		}
    	}
    }

    @Override
    public void onPause() {
    	super.onPause();
    	isEnterPause = true;
    }
    
    public void reSettingParamShow() {
    	float speed = MyUtils.getSpeedKmOneP(UserInfoManager.getInstance().rpm);
    	int level = UserInfoManager.getInstance().curLevel;

		//计算距离值
		/*float curRunDistance = mInterfaceUtils.getRunMileage(mContext, 
				MyUtils.getSpeedKmOneP(UserInfoManager.getInstance().rpm), (float) (1/60.0/60.0));mRunningParam.alreadyRunDistance += curRunDistance;
		mRunningParam.mShowRunDistance += curRunDistance;*/
		float showRunDistance = mRunningParam.mShowRunDistance;
		if( mRunningParam.isSetDistance ) {
			showRunDistance = UserInfoManager.getInstance().getTargetDistance() - showRunDistance;
		}

		//计算卡路里
		/*float curRunCalories = mInterfaceUtils.getRunCaloriesPerSec(
				UserInfoManager.getInstance().getUserInfo(), 
				speed, level);
		mRunningParam.alreadyRunCalories += curRunCalories;
		mRunningParam.mShowRunCalories += curRunCalories;*/
		float showRunCalories = mRunningParam.mShowRunCalories;
		if( mRunningParam.isSetCalories ) {
			showRunCalories = UserInfoManager.getInstance().getTargetCalorie() - showRunCalories;
		}
		String runCaloriesStr = MyUtils.getShowIntValue(showRunCalories);
		if( !mRunningParam.isCoolDownPage  ) {
			if( UserInfoManager.getInstance().getTargetTime(UserInfoManager.getInstance().getRunMode()) != 0f ) {
				float tempTime = UserInfoManager.getInstance().getTargetTime(UserInfoManager.getInstance().getRunMode()) * 60 * 1000 -
						mRunningParam.mShowRunTime;
				text_time.setText( MyUtils.getMsToSecTimeValue(tempTime) + "" );
			} else {
				text_time.setText( MyUtils.getMsToSecTimeValue(mRunningParam.mShowRunTime) + "" );
			}
		}

		if( !isMetric )
			text_speed.setText( MyUtils.getSpeedMileOneP(UserInfoManager.getInstance().rpm) + " mph" );
		else
			text_speed.setText( speed + " kph" );
		text_level.setText( level + "" );
		if ( UserInfoManager.getInstance().rpm <= 150 ){
			img_sportmode_rpm.setImageResource(CTConstant.rpmRes[Math.round(UserInfoManager.getInstance().rpm / 5.0f)]);
			text_sportmode_rpm_1.setImageResource(CTConstant.rpmValueRes[UserInfoManager.getInstance().rpm / 100]);
			text_sportmode_rpm_2.setImageResource(CTConstant.rpmValueRes[UserInfoManager.getInstance().rpm % 100 / 10]);
			text_sportmode_rpm_3.setImageResource(CTConstant.rpmValueRes[UserInfoManager.getInstance().rpm % 10]);
		}

		text_calories.setText( runCaloriesStr + " kcal");
		text_pulse.setText(UserInfoManager.getInstance().ecgValue + "");
		text_watt.setText( mInterfaceUtils.getWattValue(UserInfoManager.getInstance().rpm, level) +"");
		if( !isMetric )
			text_distance.setText(MyUtils.getDistanceMileValue(mContext, showRunDistance) + "mile");
		else
			text_distance.setText(MyUtils.getDistanceValue(mContext, showRunDistance) + " km");

		btn_fan.setImageResource(CTConstant.fanRes[UserInfoManager.getInstance().fanStatus-1]);
    }
	public void initParamDataAndRunTimer() {
		mRunningParam = new RunningParamHRC(mContext);
		System.arraycopy( RunParamTableManager.getInstance(this).
				proLevelTable[UserInfoManager.getInstance().getRunMode()], 0, mRunningParam.mLevelItemValueArray,
				0, InitParam.TOTAL_RUN_INTERVAL_NUM );

//		Log.d(TAG, "mSpeedItemValueArray === "+mRunningParamManager.mSpeedItemValueArray[0]);

		min_Chart_View_Histogram.setLevel(0);
		min_Chart_View_Histogram.setItemValueArray(mRunningParam.mLevelItemValueArray);
		min_Chart_View_Histogram.invalidate();
		
		if ( UserInfoManager.getInstance().rpm <= 150 ){
			img_sportmode_rpm.setImageResource(CTConstant.rpmRes[Math.round(UserInfoManager.getInstance().rpm / 5.0f)]);
			text_sportmode_rpm_1.setImageResource(CTConstant.rpmValueRes[UserInfoManager.getInstance().rpm / 100]);
			text_sportmode_rpm_2.setImageResource(CTConstant.rpmValueRes[UserInfoManager.getInstance().rpm % 100 / 10]);
			text_sportmode_rpm_3.setImageResource(CTConstant.rpmValueRes[UserInfoManager.getInstance().rpm % 10]);
		}

		UserInfoManager.getInstance().clearList();

		
		mRunningParam.isSetTime = true;
		mRunningParam.runMaxTime = UserInfoManager.getInstance().getTargetTime(UserInfoManager.getInstance().getRunMode());
		text_time.setText( MyUtils.getMsToSecTimeValue(mRunningParam.runMaxTime * 60 * 1000) + "" );
		if( UserInfoManager.getInstance().getTargetTime(UserInfoManager.getInstance().getRunMode()) == 0f ) {
			mRunningParam.runMaxTime = InitParam.MAX_RUN_TIME;
			mRunningParam.incycleRun = true;
			text_time.setText( MyUtils.getMsToSecTimeValue(
					UserInfoManager.getInstance().getTargetTime(UserInfoManager.getInstance().getRunMode()) * 60 * 1000) + "" );
		}

		//初始化距离和速度显示
		if( !isMetric ) {
			text_distance.setText( 0.0 + " mile");
			text_speed.setText( 0.0 + " mph" );
		} else {
			text_distance.setText( 0.0 + " km");
			text_speed.setText( 0.0 + " kph" );
		}
		text_level.setText( mRunningParam.mLevelItemValueArray[0] + "" );
		mRunningParam.runStageNum = 0;

		UserInfoManager.getInstance().curLevel = mRunningParam.mLevelItemValueArray[0];
	}

	private void startTimerForPrepare(boolean isContinueRun) {

		topLayotIsFrontInBlurringView(false);

		img_sportmode_countdown.setVisibility(View.VISIBLE);

		mPreparePageTimer.closeTimer();
		mRunningParam.isPreparePage = true;
		mRunningParam.countDown = InitParam.CountDown;
		mPreparePageTimer.setIsContinue(isContinueRun);
		mPreparePageTimer.startTimer(0, PREPARE_PAGE_TIMER_DELAY, this);
		disableOnclickEvent();
	}
	
	private void stopTimerForPrepare() {
		mPreparePageTimer.closeTimer();
		mRunningParam.isPreparePage = false;
		enbleOnclickEvent();
	}

	private void startTimerToRefreshParam() {
		mRunParamRefreshTimer.closeTimer();
		mRunningParam.isRefreshPage = true;
		mRunParamRefreshTimer.startParamRefreshTimer(0, RUN_VALUE_GET_TIME, this);
		startTimerToHrBeat();
	}

	private void stopTimerToRefreshParam() {
		mRunParamRefreshTimer.closeTimer();
		mRunningParam.isRefreshPage = false;
	}

	private void startTimerToHrBeat() {
		mHrBeatTimer.closeTimer();
		mHrBeatTimer.startParamHrBeat(0, RUN_HEART_BEART, this);
	}

	private void stopTimerToHrBeat() {
		mHrBeatTimer.closeTimer();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			
			case R.id.min_Chart_View_Histogram:
				Support.buzzerRingOnce();
				if( isMinChart == true ) {
					startMinToMaxAnimation();
					
				} else if ( isMinChart == false ) {
					startMaxToMinAnimation();
				}
				if( btn_sportmode_media_out.getDrawable().getConstantState().equals(
						getResources().getDrawable(
								R.drawable.btn_sportmode_media_3).getConstantState()) ) {
					btn_sportmode_media_out_layout.setVisibility(View.GONE);
					btn_sportmode_media_in.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.btn_sportmode_down:

				Log.d(TAG,"btn_sportmode_down");
				if ( (Integer)btn_sportmode_down.getTag() != 1 ) {
					Support.buzzerRingOnce();
				} else {
					btn_sportmode_down.setTag(-1);
				}

				min_Chart_View.clearAnimation();
				setLevelValue(-1, 0);
				break;
			case R.id.btn_sportmode_up:

				if ( (Integer)btn_sportmode_up.getTag() != 1 ) {
					Support.buzzerRingOnce();
				} else {
					btn_sportmode_up.setTag(-1);
				}

				min_Chart_View.clearAnimation();
				setLevelValue(1, 0);
				break;
			case R.id.btn_fan:
				Support.buzzerRingOnce();
				setFanStatus();
				break;
			case R.id.btn_sportmode_media_in:
				Support.buzzerRingOnce();
				if( btn_sportmode_media_in.getDrawable().getConstantState().equals(
						getResources().getDrawable(
								R.drawable.btn_sportmode_media_1).getConstantState()) ) {
					btn_sportmode_media_out_layout.setVisibility(View.VISIBLE);
					btn_sportmode_media_in.setVisibility(View.GONE);
				}
				min_Chart_View.clearAnimation();
				if ( isMinChart == false && min_Chart_View.getVisibility() == View.VISIBLE ) {
					directShowMinChartView();
				}
				break;
			case R.id.btn_sportmode_media_out:
				Support.buzzerRingOnce();
				if( btn_sportmode_media_out.getDrawable().getConstantState().equals(
						getResources().getDrawable(
								R.drawable.btn_sportmode_media_3).getConstantState()) ) {
					btn_sportmode_media_out_layout.setVisibility(View.GONE);
					btn_sportmode_media_in.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.btn_media_mp3:
				if ( !language.endsWith("zh") ) {
					UserInfoManager.getInstance().mediaMode = MediaENItem.ITEM_MEDIA_INSTAGRAM.ordinal();
					enterThirdPartyUsage(UserInfoManager.getInstance().mediaMode);
				} else {
					UserInfoManager.getInstance().mediaMode = MediaItem.ITEM_MEDIA_MP3.ordinal();
					enterThirdPartyUsage(UserInfoManager.getInstance().mediaMode);
				}

				break;
			case R.id.btn_media_mp4:
				if ( !language.endsWith("zh") ) {
					UserInfoManager.getInstance().mediaMode = MediaENItem.ITEM_MEDIA_MP3.ordinal();
					enterThirdPartyUsage(UserInfoManager.getInstance().mediaMode);
				} else {
					UserInfoManager.getInstance().mediaMode = MediaItem.ITEM_MEDIA_MP4.ordinal();
					enterThirdPartyUsage(UserInfoManager.getInstance().mediaMode);
				}
				break;
			case R.id.btn_media_i71:
				if ( !language.endsWith("zh") ) {
					UserInfoManager.getInstance().mediaMode = MediaENItem.ITEM_MEDIA_FACEBOOK.ordinal();
					enterThirdPartyUsage(UserInfoManager.getInstance().mediaMode);
				} else {
					UserInfoManager.getInstance().mediaMode = MediaItem.ITEM_MEDIA_AIQIYI.ordinal();
					enterThirdPartyUsage(UserInfoManager.getInstance().mediaMode);
				}
				break;
			case R.id.btn_media_bai:
				if ( !language.endsWith("zh") ) {
					UserInfoManager.getInstance().mediaMode = MediaENItem.ITEM_MEDIA_YOUTUBE.ordinal();
					enterThirdPartyUsage(UserInfoManager.getInstance().mediaMode);
				} else {
					UserInfoManager.getInstance().mediaMode = MediaItem.ITEM_MEDIA_BAIDU.ordinal();
					enterThirdPartyUsage(UserInfoManager.getInstance().mediaMode);
				}
				break;
			case R.id.btn_media_av:
				if ( !language.endsWith("zh") ) {
					UserInfoManager.getInstance().mediaMode = MediaENItem.ITEM_MEDIA_FLIKR.ordinal();
					enterThirdPartyUsage(UserInfoManager.getInstance().mediaMode);
				} else {
					UserInfoManager.getInstance().mediaMode = MediaItem.ITEM_MEDIA_AVIN.ordinal();
					enterThirdPartyUsage(UserInfoManager.getInstance().mediaMode);
				}
				break;
			case R.id.btn_media_weibo:
				if ( !language.endsWith("zh") ) {
					UserInfoManager.getInstance().mediaMode = MediaENItem.ITEM_MEDIA_CHROME.ordinal();
					enterThirdPartyUsage(UserInfoManager.getInstance().mediaMode);
				} else {
					UserInfoManager.getInstance().mediaMode = MediaItem.ITEM_MEDIA_WEIBO.ordinal();
					enterThirdPartyUsage(UserInfoManager.getInstance().mediaMode);
				}
				break;
			case R.id.btn_media_6:
				if ( !language.endsWith("zh") ) {
					UserInfoManager.getInstance().mediaMode = MediaENItem.ITEM_MEDIA_MP4.ordinal();
					enterThirdPartyUsage(UserInfoManager.getInstance().mediaMode);
				} else {
					UserInfoManager.getInstance().mediaMode = MediaItem.ITEM_MEDIA_SCREEN_MIRROR.ordinal();
					enterThirdPartyUsage(UserInfoManager.getInstance().mediaMode);
				}
				break;
			case R.id.btn_media_7:
				if ( !language.endsWith("zh") ) {
					UserInfoManager.getInstance().mediaMode = MediaENItem.ITEM_MEDIA_AVIN.ordinal();
					enterThirdPartyUsage(UserInfoManager.getInstance().mediaMode);
				}
				break;
			case R.id.btn_media_8:
				if ( !language.endsWith("zh") ) {
					UserInfoManager.getInstance().mediaMode = MediaENItem.ITEM_MEDIA_TWITTER.ordinal();
					enterThirdPartyUsage(UserInfoManager.getInstance().mediaMode);
				}
				break;
			case R.id.btn_media_9:
				if ( !language.endsWith("zh") ) {
					UserInfoManager.getInstance().mediaMode = MediaENItem.ITEM_MEDIA_SCREEN_MIRROR.ordinal();
					enterThirdPartyUsage(UserInfoManager.getInstance().mediaMode);
				}
				break;
			case R.id.volume_layout:
				Support.buzzerRingOnce();
				if ( mVerticalSeekBar.getVisibility() == View.VISIBLE ) {
					mVerticalSeekBar.setVisibility(View.GONE);
					sb_media_volume_bk.setVisibility(View.GONE);
				} else if ( mVerticalSeekBar.getVisibility() == View.GONE ) {
					sb_media_volume_bk.setVisibility(View.VISIBLE);
					mVerticalSeekBar.setVisibility(View.VISIBLE);
					mVerticalSeekBar.setProgress( SelfAudioManager.getInstance(null).
							getCurrentPro(mVerticalSeekBar.getMax()) );
				}
				break;
			case R.id.btn_sportmode_stop:
//				Log.d(TAG, "=====2=====mRunningParam.startPauseTime =" + mRunningParam.startPauseTime);
				Support.buzzerRingOnce();
				enterPausePage();
				break;
			
			case R.id.btn_pause_quit:

				if ( isNeedBuzzer == false ) {
					isNeedBuzzer = true;
				} else {
					Support.buzzerRingOnce();
				}
				mRunningParam.isPausePage = false;
				mPausePageTimer.closeTimer();
				stopRunAndSkipToSumActivity(mRunningParam.alreadyRunTime, 
						mRunningParam.alreadyRunDistance, mRunningParam.alreadyRunCalories, false);
				break;
			case R.id.btn_pause_continue:
				if ( isNeedBuzzer == false ) {
					isNeedBuzzer = true;
				} else {
					Support.buzzerRingOnce();
				}
				enbleOnclickEvent();
				mRunningParam.isPausePage = false;
				mPausePageTimer.closeTimer();

				mBlurringView.setVisibility(View.GONE);
				pause_page.setVisibility(View.GONE);
				btn_sportmode_stop.setVisibility(View.VISIBLE);

				startTimerForPrepare(true);
				break;
			case R.id.btn_sportmode_cooldown:
				if ( isNeedBuzzer == false ) {
					isNeedBuzzer = true;
				} else {
					Support.buzzerRingOnce();
				}
				mCoolDownTimer.closeTimer();
				stopTimerToRefreshParam();
				stopRunAndSkipToSumActivity(mRunningParam.alreadyRunTime, 
						mRunningParam.alreadyRunDistance, mRunningParam.alreadyRunCalories, true);
				break;
			default:
				break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "=====2======");
		StorageParam.setRunStopTime(this, 0l);
		mRunParamRefreshTimer.closeTimer();
		mCoolDownTimer.closeTimer();
		stopTimerToHrBeat();
		// mMsgHandler.removeCallbacksAndMessages(null);
//		mMsgHandler.remeCallbacksAndMessages(null);
	}

	private Handler mMsgHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MEG_COUNT_DOWN:
					int count = msg.arg1;
					Log.d(TAG,"===================11  " + count);
					img_sportmode_countdown.setImageResource(
							ImageUtil.getNumberBitmap(mContext, InitParam.CountDown - count));
					if ( count >= 1 ) {
						mSerialUtils.setBuzzerContinu(50);
					} else if ( count == 0 ) {
						mSerialUtils.setBuzzerContinu(100);
					}
					if (count == -1) {
						img_sportmode_countdown.setVisibility(View.GONE);

						mBlurringView.setVisibility(View.GONE);

						initRunParamAndStartRun((Boolean) msg.obj, false);

						stopTimerForPrepare();
						startTimerToRefreshParam();
						/*startTimerToHrBeat();*/
					}
					break;
				case MSG_REFRESH_PARAM_VALUE:

					if( mRunningParam.isCoolDownPage ) {
						coolDownPageRefreshParam();
					} else {
						runParamProc();
					}

					break;
				case R.id.btn_sportmode_cooldown:
					enbleOnclickEvent();
					btn_sportmode_cooldown.performClick();
					break;
				case R.id.btn_pause_quit:
					enbleOnclickEvent();
					btn_pause_quit.performClick();
					break;
				case MSG_PROC_ENBLE_CONTINUE:
					/*enbleOnclickEvent();*/
					btn_pause_continue.setEnabled(true);
					break;
				case MSG_REFRESH_HEART:
					changePluse();
					break;
				case MSG_PROC_ERROR_CMD:
					int[] values = (int[]) msg.obj;
					if( values[0] == 1) {
						disableOnclickEvent();
						enterErrorPage();
						error_status_layout.setVisibility(View.VISIBLE);
						error_status_icon.setImageResource(R.drawable.img_pop_emergencystop);
					} else {
						/*enbleOnclickEvent();
						error_status_layout.setVisibility(View.GONE);
						btn_sportmode_stop.performClick();*/
						disableOnclickEvent();
						enterErrorPageOther(values[0]);
					}
					break;
				case R.id.btn_pause_continue:
					btn_pause_continue.performClick();
					break;
			}
		}
	};

	public void initRunParamAndStartRun(boolean isContinue, boolean isWarmUpStop) {

		if( !isContinue ) {
			UserInfoManager.getInstance().addLevelList(
					mRunningParam.mLevelItemValueArray[mRunningParam.runStageNum], 0);
			UserInfoManager.getInstance().addHrList(UserInfoManager.getInstance().ecgValue, 0);

		}
		setRunProLevel(mRunningParam.runStageNum);

	}
	
	public void runParamProc() {
		/*Log.d(TAG,"runParamProc");*/
		int level = mRunningParam.mLevelItemValueArray[mRunningParam.runStageNum];
		mRunningParam.alreadyRunLevel += level;

		mRunningParam.getRunStageNum();
		/*showErrorMsgPage();*/

		if( mRunningParam.isShowLevelIcon() ) {
			setRunProLevel(mRunningParam.runStageNum);
		}

		/*int level = mRunningParam.mLevelItemValueArray[mRunningParam.runStageNum];*/

		//计算距离值
		float curRunDistance = mInterfaceUtils.getRunMileage(mContext, 
				MyUtils.getSpeedKmOneP(UserInfoManager.getInstance().rpm), (float) (1/60.0/60.0));
		mRunningParam.alreadyRunDistance += curRunDistance;
		mRunningParam.mShowRunDistance += curRunDistance;
//		Log.d("runDistance"," ==0===== " + mRunningParam.alreadyRunDistance + " timeValue " + mRunningParam.alreadyRunTime);
		float showRunDistance = mRunningParam.mShowRunDistance;
		if( mRunningParam.isSetDistance ) {
			showRunDistance = UserInfoManager.getInstance().getTargetDistance() - showRunDistance;
		}

		//计算卡路里
		float curRunCalories = mInterfaceUtils.getRunCaloriesPerSec(
				UserInfoManager.getInstance().getUserInfo(), MyUtils.getSpeedKmOneP(UserInfoManager.getInstance().rpm), level);
		mRunningParam.alreadyRunCalories += curRunCalories;
		mRunningParam.mShowRunCalories += curRunCalories;
		float showRunCalories = mRunningParam.mShowRunCalories;
		if( mRunningParam.isSetCalories ) {
			showRunCalories = UserInfoManager.getInstance().getTargetCalorie() - showRunCalories;
		}
		String runCaloriesStr = MyUtils.getShowIntValue(showRunCalories);

		StorageParam.setRunTotalTime(mContext, 
				StorageParam.getRunTotalTime(mContext)+1l);
		StorageParam.setRunTotalDis(mContext, curRunDistance);
		/*mRunningParam.alreadyRunLevel += level;*/
		showErrorMsgPage();

		if ( mRunningParam.isRunEnd() ) {
			enterCoolDownPage();
			mRunningParam.coolDownLevel = 
					mRunningParam.mLevelItemValueArray[mRunningParam.runStageNum];
		} else {

			if( !mRunningParam.isCoolDownPage  ) {
				if( UserInfoManager.getInstance().getTargetTime(UserInfoManager.getInstance().getRunMode()) != 0f ) {
					float tempTime = UserInfoManager.getInstance().getTargetTime(UserInfoManager.getInstance().getRunMode()) * 60 * 1000 -
							mRunningParam.mShowRunTime;
					text_time.setText( MyUtils.getMsToSecTimeValue(tempTime) + "" );
				} else {
					text_time.setText( MyUtils.getMsToSecTimeValue(mRunningParam.mShowRunTime) + "" );
				}
			}
			
			/*Log.d(TAG,"mRunningParam.mShowRunTime : " + mRunningParam.mShowRunTime + 
					"mRunningParam.alreadyRunTime : " + mRunningParam.alreadyRunTime);*/

			if( !isMetric )
				text_speed.setText( MyUtils.getSpeedMileOneP(UserInfoManager.getInstance().rpm) + " mph" );
			else
				text_speed.setText( MyUtils.getSpeedKmOneP(UserInfoManager.getInstance().rpm) + " kph" );
			text_level.setText( level + "" );
			if ( UserInfoManager.getInstance().rpm <= 150 ){
				img_sportmode_rpm.setImageResource(CTConstant.rpmRes[Math.round(UserInfoManager.getInstance().rpm / 5.0f)]);
				text_sportmode_rpm_1.setImageResource(CTConstant.rpmValueRes[UserInfoManager.getInstance().rpm / 100]);
				text_sportmode_rpm_2.setImageResource(CTConstant.rpmValueRes[UserInfoManager.getInstance().rpm % 100 / 10]);
				text_sportmode_rpm_3.setImageResource(CTConstant.rpmValueRes[UserInfoManager.getInstance().rpm % 10]);
			}

			text_calories.setText( runCaloriesStr + " kcal");
			text_pulse.setText(UserInfoManager.getInstance().ecgValue + "");
			text_watt.setText( mInterfaceUtils.getWattValue(UserInfoManager.getInstance().rpm, level) +"");
			if( !isMetric )
				text_distance.setText(MyUtils.getDistanceMileValue(mContext, showRunDistance) + "mile");
			else
				text_distance.setText(MyUtils.getDistanceValue(mContext, showRunDistance) + " km");
			if( mRunningParam.isAccOneMin() ) {
				Log.d(TAG,"isAccOneMin set speed incline hr list");
				UserInfoManager.getInstance().addLevelList(level, mRunningParam.alreadyRunTime);
				/*UserInfoManager.getInstance().addHrList(UserInfoManager.getInstance().ecgValue, 
						mRunningParam.alreadyRunTime);*/
			}
		}
	}

	public void stopRunAndSkipToSumActivity( float returnRunTime, float returnRunDistance ,
			float runCalories, boolean isCoolDownEnter ) {
		Intent intent = new Intent(mContext, SummaryActivity.class);
		StorageParam.setIsRunIng(mContext, false);
		intent.putExtra(CTConstant.IS_FINSH, true);
		UserInfoManager.getInstance().setTotalTime(returnRunTime);
		UserInfoManager.getInstance().setTotalDistance(returnRunDistance);
		UserInfoManager.getInstance().setTotalCalories(runCalories);
		UserInfoManager.getInstance().setTotalLevel(mRunningParam.alreadyRunLevel);
		
		Log.d(TAG,"end running record list");
		if ( !isCoolDownEnter ) {
			UserInfoManager.getInstance().addLevelList(mRunningParam.mLevelItemValueArray[mRunningParam.runStageNum], 
					mRunningParam.alreadyRunTime);//UserInfoManager.getInstance().curLevel
			UserInfoManager.getInstance().addHrList(UserInfoManager.getInstance().ecgValue, 
					mRunningParam.alreadyRunTime);
		}

		MyUtils.leftAnimStartActivityForResult(this, intent, false);
		mLayout_running.setVisibility(View.GONE);
	}

	public void showErrorMsgPage() {
		if( mRunningParam.HRC_RUN_STATUS == CTConstant.HRC_NO_HR_STATUS ) {
			disableOnclickEvent();
			topLayotIsFrontInBlurringView(true);
			error_status_layout.setVisibility(View.VISIBLE);
			error_status_icon.setImageResource(R.drawable.img_pop_nohr);
		} else if( mRunningParam.HRC_RUN_STATUS == CTConstant.HRC_OVERPULSE_STATUS ) {
			disableOnclickEvent();
			topLayotIsFrontInBlurringView(true);
			error_status_layout.setVisibility(View.VISIBLE);
			error_status_icon.setImageResource(R.drawable.img_pop_over_pulse);
		} else if( mRunningParam.HRC_RUN_STATUS == CTConstant.HRC_AUTO_END_STATUS ) {
			disableOnclickEvent();
			stopTimerToRefreshParam();
			error_status_layout.setVisibility(View.GONE);
			stopRunAndSkipToSumActivity(mRunningParam.alreadyRunTime, 
					mRunningParam.alreadyRunDistance, mRunningParam.alreadyRunCalories, false);
		} else {
			if( error_status_layout.getVisibility() == View.VISIBLE ) {
				error_status_layout.setVisibility(View.GONE);
				enbleOnclickEvent();
			}
			if( mBlurringView.getVisibility() == View.VISIBLE )
				mBlurringView.setVisibility(View.GONE);
		}
		if ( mRunningParam.HRC_RUN_STATUS == CTConstant.NO_RPM_STATUS ) {
			disableOnclickEvent();
			topLayotIsFrontInBlurringView(true);
			stopTimerToRefreshParam();
			enterPausePage();
			mRunningParam.HRC_RUN_STATUS = 0;
		}
	}

	private void enterCoolDownPage()
	{

		UserInfoManager.getInstance().addLevelList(UserInfoManager.getInstance().curLevel, 
				mRunningParam.alreadyRunTime);
		UserInfoManager.getInstance().addHrList(UserInfoManager.getInstance().ecgValue, 
				mRunningParam.alreadyRunTime);

		disableOnclickEvent();

		mRunningParam.isCoolDownPage = true;
		btn_sportmode_stop.setVisibility(View.GONE);
		topLayotIsFrontInBlurringView(true);

		top_layout.setVisibility(View.VISIBLE);
		cooldown_page.setVisibility(View.VISIBLE);

		mCoolDownTimer.startTimer(COOL_DOWN_TIMER_DELAY, COOL_DOWN_TIMER_DELAY, this);

	}

	@Override
	public void callback() {
		if( mRunningParam.isCoolDownPage ) {
			mRunningParam.coolDownLevel -= 1;
			if( mRunningParam.coolDownLevel < 1 ) {
				mRunningParam.coolDownLevel = 1;
			}
			UserInfoManager.getInstance().curLevel = mRunningParam.coolDownLevel;
			mSerialUtils.setRunLevel(UserInfoManager.getInstance().curLevel);
			
		} else if ( mRunningParam.isPausePage ) {
			mRunningParam.isPausePage = false;
			mPausePageTimer.closeTimer();
			mMsgHandler.sendEmptyMessage(R.id.btn_pause_quit);

		} else if ( mRunningParam.isPreparePage ) {
			mRunningParam.countDown--;
			Message message = new Message();
			message.what = MEG_COUNT_DOWN;
			message.arg1 = mRunningParam.countDown;
			message.obj = mPreparePageTimer.getIsContinue();
			mMsgHandler.sendMessage(message);
		}
	}

	@Override
	public void paramRefreshProc() {
		if ( mRunningParam.isRefreshPage ) {
			mMsgHandler.sendEmptyMessage(MSG_REFRESH_PARAM_VALUE);
		}
	}

	@Override
	public void HrBeatProc() {
		if( !mRunningParam.isPausePage ) {
			mMsgHandler.sendEmptyMessage(MSG_REFRESH_HEART);
		}
	}

	private void enterPausePage()
	{
		stopTimerToHrBeat();
		btn_sportmode_media_out_layout.setVisibility(View.GONE);
		btn_sportmode_media_in.setVisibility(View.VISIBLE);
		/*btn_pause_continue.setEnabled(false);*/
		disableOnclickEvent();
		stopTimerToRefreshParam();
		btn_sportmode_stop.setVisibility(View.GONE);
		topLayotIsFrontInBlurringView(true);

		pause_page.setVisibility(View.VISIBLE);
		mRunningParam.isPausePage = true;

		if ( StorageParam.getPauseMode(mContext)  ) {
			mPausePageTimer.startTimer( PAUSE_PAGE_TIMER_DELAY, this);
		}

		UserInfoManager.getInstance().curLevel = 1;
		mSerialUtils.setRunLevel(UserInfoManager.getInstance().curLevel);
	}

	private void enterErrorPage()
	{
		disableOnclickEvent();
		stopTimerToRefreshParam();
		topLayotIsFrontInBlurringView(true);

		mRunningParam.isPausePage = true;

	}
	private void enterErrorPageOther( int errValue)	{
		if( mRunningParam.isPreparePage ) {
			return ;
		}
		stopTimerToRefreshParam();
		topLayotIsFrontInBlurringView(true);

		error_status_layout.setVisibility(View.VISIBLE);
		error_status_icon.setImageResource(ErrorProcManager.getInstance().getErrRes(errValue));

		ErrorProcManager.getInstance().err_status = errValue;
		ErrorProcManager.getInstance().isHasERR = true;
	}

	public void initSurfaceView() {
		mLayout_running = (RelativeLayout) findViewById(R.id.mLayout_running);
		blurredView = ( RelativeLayout ) findViewById(R.id.blurred_view_group);
		mBlurringView = (BlurringView) findViewById(R.id.blurring_view);
		
		top_layout = (RelativeLayout) findViewById(R.id.top_layout);

		btn_media_volume = (ImageView) findViewById(R.id.btn_media_volume);
		volume_layout = (RelativeLayout) findViewById(R.id.volume_layout);
		sb_media_volume_bk = (ImageView) findViewById(R.id.sb_media_volume_bk);

		text_level = (TextView) findViewById(R.id.text_level);
		text_time = (TextView) findViewById(R.id.text_time);
		text_distance = (TextView) findViewById(R.id.text_distance);
		text_calories = (TextView) findViewById(R.id.text_calories);
		text_pulse = (TextView) findViewById(R.id.text_pulse);
		img_pulse = (ImageView) findViewById(R.id.img_pulse);
		text_watt = (TextView) findViewById(R.id.text_watt);
		text_speed = (TextView) findViewById(R.id.text_speed);
		
		mVerticalSeekBar = (VerticalSeekBar) findViewById(R.id.sb_media_volume);
		img_sportmode_countdown = (ImageView) findViewById(R.id.img_sportmode_countdown);

		layout_rpm_view = (RelativeLayout) findViewById(R.id.layout_rpm_view);
		
		layout_sportmode_rpm = (RelativeLayout) findViewById(R.id.layout_sportmode_rpm);
		img_sportmode_rpm = (ImageView) findViewById(R.id.img_sportmode_rpm);
		text_sportmode_rpm_1 = (ImageView) findViewById(R.id.text_sportmode_rpm_1);
		text_sportmode_rpm_2 = (ImageView) findViewById(R.id.text_sportmode_rpm_2);
		text_sportmode_rpm_3 = (ImageView) findViewById(R.id.text_sportmode_rpm_3);

		min_Chart_View = (RelativeLayout) findViewById(R.id.min_Chart_View);
		min_Chart_View_Unit = (ImageView) findViewById(R.id.min_Chart_View_Unit);

		min_Chart_View_bg = (ImageView) findViewById(R.id.min_Chart_View_bg);
		min_Chart_View_Histogram = (ParamView) findViewById(R.id.min_Chart_View_Histogram);
		min_Chart_View_Cap = (TextView) findViewById(R.id.min_Chart_View_Cap);
		min_Chart_View_Cap.setText(captionHint[UserInfoManager.getInstance().getRunMode()]);

		btn_sportmode_down = (LongClickImage) findViewById(R.id.btn_sportmode_down);
		btn_sportmode_up = (LongClickImage) findViewById(R.id.btn_sportmode_up);
		
		btn_sportmode_stop = (ImageView) findViewById(R.id.btn_sportmode_stop);
		btn_fan = (ImageView) findViewById(R.id.btn_fan);
		btn_fan.setImageResource(CTConstant.fanRes[UserInfoManager.getInstance().fanStatus-1]);

		btn_sportmode_media_in = (ImageView) findViewById(R.id.btn_sportmode_media_in);
		btn_sportmode_media_out_layout = (RelativeLayout) findViewById(R.id.btn_sportmode_media_out_layout);
		btn_sportmode_media_out = (ImageView) findViewById(R.id.btn_sportmode_media_out);
		btn_media_mp3 = (ImageView) findViewById(R.id.btn_media_mp3);
		btn_media_mp4 = (ImageView) findViewById(R.id.btn_media_mp4);
		btn_media_i71 = (ImageView) findViewById(R.id.btn_media_i71);
		btn_media_bai = (ImageView) findViewById(R.id.btn_media_bai);
		btn_media_av = (ImageView) findViewById(R.id.btn_media_av);
		btn_media_weibo = (ImageView) findViewById(R.id.btn_media_weibo);
		btn_media_6 = (ImageView) findViewById(R.id.btn_media_6);
		btn_media_7 = (ImageView) findViewById(R.id.btn_media_7);
		btn_media_8 = (ImageView) findViewById(R.id.btn_media_8);
		btn_media_9 = (ImageView) findViewById(R.id.btn_media_9);

		pause_page = (RelativeLayout) findViewById(R.id.pause_page);
		btn_pause_quit = (ImageView) findViewById(R.id.btn_pause_quit);
		btn_pause_continue = (ImageView) findViewById(R.id.btn_pause_continue);

		cooldown_page = (RelativeLayout) findViewById(R.id.cooldown_page);
		btn_sportmode_cooldown = (ImageView) findViewById(R.id.btn_sportmode_cooldown);

		error_status_layout = (RelativeLayout) findViewById(R.id.error_status_layout);
		error_status_icon = (ImageView) findViewById(R.id.error_status_icon);
	}

	public void setListenerEvent() {
		volume_layout.setOnClickListener(this);
		mVerticalSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				SelfAudioManager.getInstance(null).setAudioVolume(progress, seekBar.getMax());
			}
		});

		min_Chart_View_Histogram.setOnClickListener(this);

		btn_sportmode_down.setOnClickListener(this);
		btn_sportmode_up.setOnClickListener(this);
		
		btn_sportmode_stop.setOnClickListener(this);

		btn_fan.setOnClickListener(this);
		btn_sportmode_media_in.setOnClickListener(this);
		btn_sportmode_media_out.setOnClickListener(this);
		btn_media_mp3.setOnClickListener(this);
		btn_media_mp4.setOnClickListener(this);
		btn_media_i71.setOnClickListener(this);
		btn_media_bai.setOnClickListener(this);
		btn_media_av.setOnClickListener(this);
		btn_media_weibo.setOnClickListener(this);
		btn_media_6.setOnClickListener(this);
		btn_media_7.setOnClickListener(this);
		btn_media_8.setOnClickListener(this);
		btn_media_9.setOnClickListener(this);

		btn_pause_quit.setOnClickListener(this);
		btn_pause_continue.setOnClickListener(this);

		btn_sportmode_cooldown.setOnClickListener(this);
	}
	
	public void setLevelValue(int isUp, int mLevel) {
		if( mRunningParam.runStageNum == -1 )
			return;
		int level = mRunningParam.mLevelItemValueArray[mRunningParam.runStageNum];
		if( isUp == 1 ) {
			if( (int)level < StorageParam.getMaxLevel(mContext) ) {
				level ++;
			}
		} else if( isUp == -1 ) {
			if( (int)level > InitParam.MinLevel ) {
				level --;
			}
		} else if( isUp == 0) {
			level = mLevel;
		}
		Log.d(TAG,"setLevelValue : " + level );

		mRunningParam.mLevelItemValueArray[mRunningParam.runStageNum] = level;
		UserInfoManager.getInstance().addLevelList(level, mRunningParam.alreadyRunTime);
		
		if( StorageParam.getRunMode(mContext) == 
				RunMode.IDX_HOME_VIRTUAL_MODE.ordinal() || StorageParam.getRunMode(mContext) == 
						RunMode.IDX_HOME_QUICKSTART_MODE.ordinal() ) {
				for( int i = mRunningParam.runStageNum; i < InitParam.TOTAL_RUN_INTERVAL_NUM; i++ ) {
					mRunningParam.mLevelItemValueArray[i] =
							mRunningParam.mLevelItemValueArray[mRunningParam.runStageNum];
				}
		}
		
		setRunProLevel(mRunningParam.runStageNum);
	}
	
	public synchronized void setRunProLevel(int mShowItemNum) {
		Log.d(TAG,"setRunProgSpeedSlope mShowItemNum : =" + mShowItemNum);
		if( mShowItemNum < 0 )
			mShowItemNum = 0 ;

		min_Chart_View_Histogram.setLevel(0);
		min_Chart_View_Histogram.setItemValueArray(mRunningParam.mLevelItemValueArray);

		UserInfoManager.getInstance().curLevel = mRunningParam.mLevelItemValueArray[mShowItemNum];

		mSerialUtils.setRunLevel(UserInfoManager.getInstance().curLevel);
		UserInfoManager.getInstance().addLevelList(
				UserInfoManager.getInstance().curLevel, mRunningParam.alreadyRunTime);

		text_level.setText( UserInfoManager.getInstance().curLevel + "" );

		if ( UserInfoManager.getInstance().getRunMode() != 
				RunMode.IDX_HOME_VIRTUAL_MODE.ordinal() ) {
			min_Chart_View_Histogram.setShowItemNum( mShowItemNum + 1 );
			min_Chart_View_Histogram.postInvalidate();
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Log.i(TAG, "onActivityResult resultCode: " + resultCode);
			switch (requestCode) {
				case CTConstant.REQUEST_CODE_DELETE:
					Log.i(TAG, "onActivityResult requestCode: " + requestCode);
					boolean isFinsh = data.getBooleanExtra(CTConstant.IS_FINSH, false);
					boolean isSuspended = data.getBooleanExtra(CTConstant.IS_SUSPENDED, false);

					Intent intent = new Intent();
					intent.putExtra(CTConstant.IS_FINSH, isFinsh);
					intent.putExtra(CTConstant.IS_SUSPENDED, isSuspended);
					MyUtils.animLeftFinishActivity(this, intent);
					break;
			}
		}
		if(resultCode == CTConstant.RESULT_BACK)
		{
			switch (requestCode) {
			case CTConstant.REQUEST_CODE_DELETE:
				break;
			}
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

    @Override  
	public void onBackPressed() {
    	Log.d(TAG, "onBackPressed()");
//    	super.onBackPressed();
		mInterfaceUtils.stopRunning();
		//跑步停止
		StorageParam.setIsRunIng(mContext, false);
		stopTimerToRefreshParam();
	}

	public void coolDownPageRefreshParam() {

		mRunningParam.coolDownTimer++;
		if( mRunningParam.coolDownTimer > 60 * 3 ) {
			mRunningParam.isCoolDownPage = false;
			mMsgHandler.sendEmptyMessage(R.id.btn_sportmode_cooldown);
		} else {
			text_time.setText(  MyUtils.getMsToSecTimeValue( (3 * 60 - mRunningParam.coolDownTimer) * 1000) + "" );
		}
		Log.d(TAG," ======   " +  mRunningParam.coolDownTimer);

		if( !isMetric )
			text_speed.setText( MyUtils.getSpeedMileOneP(UserInfoManager.getInstance().rpm) + " mph" );
		else
			text_speed.setText( MyUtils.getSpeedKmOneP(UserInfoManager.getInstance().rpm) + " kph" );
			text_pulse.setText(UserInfoManager.getInstance().ecgValue + "");
		text_level.setText( UserInfoManager.getInstance().curLevel + "" );
	}

	@Override
	public void errorEventProc(int errorValue) {
		/*if( errorValue == 1 ) {
			sendMesgProc(MSG_PROC_ERROR_CMD, errorValue);
			
		} else {
			sendMesgProc(MSG_PROC_ERROR_CMD, errorValue);
		}*/
		if( errorValue == 1 ) {
			releaseQuitProc();
		}
		if( errorValue != 1 && errorValue != 0 ) {
			if ( ErrorProcManager.getInstance().E5E6E7IsNoProc(errorValue) ) {
				sendMesgProc(CTConstant.MSG_PROC_ERROR_CMD, errorValue);
			}
		}
	}

	@Override
	public void onCmdEvent(Message msg) {
		if ( ErrorProcManager.getInstance().isHasERR ) {
			if ( ErrorProcManager.getInstance().isErrUnProc() ) {
				return ;
			}
			if (msg.arg1 != Command.KEY_CMD_STOP_CANCEL) {
				return ;
			} else {
				error_status_layout.setVisibility(View.GONE);
				ErrorProcManager.getInstance().err_status = -1;
				ErrorProcManager.getInstance().isHasERR = false;
				ErrorProcManager.getInstance().isFirstE5E6E7 = true;
				enterPausePage();
				return;
			}
		}
		if ( mRunningParam.isCoolDownPage ) {
			switch (msg.arg1) {
				case Command.KEY_CMD_STOP_CANCEL:
					Support.keyToneOnce();
					isNeedBuzzer = false;
					mMsgHandler.sendEmptyMessage(R.id.btn_sportmode_cooldown);
					break;
			}
		}
		if( mRunningParam.isPreparePage == true || 
				mRunningParam.isCoolDownPage ) {
			return ;
		}
		switch (msg.arg1) {
			case Command.KEY_CMD_QUICK_START:
				if( btn_pause_continue.isEnabled() && 
						btn_sportmode_stop.getVisibility() != View.VISIBLE ) {
					Support.keyToneOnce();
					isNeedBuzzer = false;
					mMsgHandler.sendEmptyMessage(R.id.btn_pause_continue);
				}
				break;
			case Command.KEY_CMD_STOP_CANCEL:
				if( mRunningParam.HRC_RUN_STATUS == CTConstant.HRC_NO_HR_STATUS || 
						mRunningParam.HRC_RUN_STATUS == CTConstant.HRC_OVERPULSE_STATUS ) {
					break;
				}
				if( mRunningParam.isPausePage == true ) {
					Support.keyToneOnce();
					isNeedBuzzer = false;
					mMsgHandler.sendEmptyMessage(R.id.btn_pause_quit);
				} else {
					Support.keyToneOnce();
					if ( error_status_layout.getVisibility() == View.VISIBLE ) {
						error_status_layout.setVisibility(View.GONE);
					}
					if ( mBlurringView.getVisibility() == View.VISIBLE ) {
						mBlurringView.setVisibility(View.GONE);
					}
					mRunningParam.HRC_RUN_STATUS = 0;
					enterPausePage();
				}
				break;
		}
		if( mRunningParam.isPausePage == true ) {
			return ;
		}
		switch (msg.arg1) {
			/*case Command.KEY_CMD_LEVEL_DEC_F:
			case Command.KEY_CMD_LEVEL_DEC_S:
				Support.buzzerRingOnce();
				showInclineProfile();
				setLevelValue(-1, 0);
				break;
			case Command.KEY_CMD_LEVEL_PLUS_F:
			case Command.KEY_CMD_LEVEL_PLUS_S:
				Support.buzzerRingOnce();
				showInclineProfile();
				setLevelValue(1, 0);
				break;
			case Command.KEY_CMD_LEVEL_DEC_F_LONG_1:
			case Command.KEY_CMD_LEVEL_DEC_F_LONG_2:
			case Command.KEY_CMD_LEVEL_DEC_S_LONG_1:
			case Command.KEY_CMD_LEVEL_DEC_S_LONG_2:
				showInclineProfile();
				setLevelValue(-1, 0);
				break;
			case Command.KEY_CMD_LEVEL_PLUS_F_LONG_1:
			case Command.KEY_CMD_LEVEL_PLUS_F_LONG_2:
			case Command.KEY_CMD_LEVEL_PLUS_S_LONG_1:
			case Command.KEY_CMD_LEVEL_PLUS_S_LONG_2:
				showInclineProfile();
				setLevelValue(1, 0);
				break;*/
			default:
				Log.d(TAG, "key value " + msg.arg1);
				break;
		}
		return ;
	}

	public void showSpeedProfile() {
		min_Chart_View.clearAnimation();

		min_Chart_View_bg.setImageResource(R.drawable.img_sportmode_profile_speed_1);
	}

	public void showInclineProfile() {
		min_Chart_View.clearAnimation();
		
	}

	private void releaseQuitProc() {
		stopTimerToRefreshParam();

		mRunningParam.isRelease = true;

		StorageParam.setIsRunIng(mContext, false);
		UserInfoManager.getInstance().setTotalTime(mRunningParam.alreadyRunTime);
		UserInfoManager.getInstance().setTotalDistance(mRunningParam.alreadyRunDistance);
		UserInfoManager.getInstance().setTotalCalories(mRunningParam.alreadyRunCalories);
		UserInfoManager.getInstance().setTotalLevel(mRunningParam.alreadyRunLevel);

		Log.d(TAG,"end running record list");
		UserInfoManager.getInstance().addLevelList(UserInfoManager.getInstance().curLevel, 
				mRunningParam.alreadyRunTime);
		UserInfoManager.getInstance().addHrList(UserInfoManager.getInstance().ecgValue, 
				mRunningParam.alreadyRunTime);

		Intent intent = new Intent();
		intent.putExtra(CTConstant.IS_FINSH, false);
		MyUtils.animLeftFinishActivity(RunningHRCActivity.this, intent);
	}

	public void sendMesgProc( int msg, int value) {
		if( mMsgHandler != null ) {
			Message message = new Message();
			message.what = msg;
			int mesg[] = {value};
			message.obj = mesg;
			mMsgHandler.sendMessage(message);
		}		
	}

	public void enbleOnclickEvent() {
		volume_layout.setEnabled(true);
		btn_sportmode_media_in.setEnabled(true);
		btn_sportmode_stop.setEnabled(true);
		min_Chart_View_Histogram.setEnabled(true);
		/*btn_sportmode_down.setEnabled(true);
		btn_sportmode_up.setEnabled(true);*/
		btn_fan.setEnabled(true);
	}

	public void disableOnclickEvent() {
		btn_sportmode_media_out_layout.setVisibility(View.GONE);
		btn_sportmode_media_in.setVisibility(View.VISIBLE);
		volume_layout.setEnabled(false);
		mVerticalSeekBar.setVisibility(View.GONE);
		sb_media_volume_bk.setVisibility(View.GONE);
		btn_sportmode_media_in.setEnabled(false);
		btn_sportmode_stop.setEnabled(false);
		min_Chart_View_Histogram.setEnabled(false);
		btn_sportmode_down.setEnabled(false);
		btn_sportmode_up.setEnabled(false);
		btn_fan.setEnabled(false);
	}

	public void topLayotIsFrontInBlurringView( boolean isVisiable ) {
		blurredView.removeView(top_layout);
		mLayout_running.removeView(top_layout);
		if( isVisiable ) {

			mLayout_running.addView(top_layout);
			mBlurringView.setVisibility(View.VISIBLE);
			mBlurringView.setBlurredView(blurredView);
	        mBlurringView.invalidate();
	        top_layout.setVisibility(View.VISIBLE);

		} else {

			blurredView.addView(top_layout);
			top_layout.setVisibility(View.VISIBLE);
			mBlurringView.setVisibility(View.VISIBLE);
			mBlurringView.setBlurredView(blurredView);
	        mBlurringView.invalidate();

		}	
		
	}

    @SuppressWarnings("deprecation")
	public void setFanStatus() {
    	if( btn_fan.getDrawable().getConstantState().equals(
    			mContext.getResources().getDrawable(
						R.drawable.btn_fan_1_1).getConstantState()) ) {
    		btn_fan.setImageResource(R.drawable.btn_fan_2_1);
    		SerialUtils.getInstance().setFanOnOff(100);
    		UserInfoManager.getInstance().fanStatus = FANSTATUS.TURN_ON_HIGHEST_SPEED.ordinal();
    	} else if( btn_fan.getDrawable().getConstantState().equals(
    			mContext.getResources().getDrawable(
						R.drawable.btn_fan_2_1).getConstantState()) ) {
    		btn_fan.setImageResource(R.drawable.btn_fan_3_1);
    		SerialUtils.getInstance().setFanOnOff(150);
    		UserInfoManager.getInstance().fanStatus = FANSTATUS.TURN_ON_MIDDLE_SPEED.ordinal();
    	} else if( btn_fan.getDrawable().getConstantState().equals(
    			mContext.getResources().getDrawable(
						R.drawable.btn_fan_3_1).getConstantState()) ) {
    		btn_fan.setImageResource(R.drawable.btn_fan_4_1);
    		SerialUtils.getInstance().setFanOnOff(200);
    		UserInfoManager.getInstance().fanStatus = FANSTATUS.TURN_ON_LOWEST_SPEED.ordinal();
    	} else if( btn_fan.getDrawable().getConstantState().equals(
    			mContext.getResources().getDrawable(
						R.drawable.btn_fan_4_1).getConstantState()) ) {
    		btn_fan.setImageResource(R.drawable.btn_fan_1_1);
    		SerialUtils.getInstance().setFanOnOff(0);
    		UserInfoManager.getInstance().fanStatus = FANSTATUS.TURN_OFF.ordinal();
    	}
    }

    public void enterThirdPartyUsage(int item) {
    	UserInfoManager.getInstance().sysRunStatus = SYSRUNSTATUS.STA_MOT.ordinal();
    	Support.buzzerRingOnce();
		mLayout_running.setVisibility(View.GONE);
		needResume = true;
		stopTimerForPrepare();
		stopTimerToRefreshParam();
		UserInfoManager.getInstance().mIsMediaMode = true;
		if ( !language.endsWith("zh") ) {
			if ( item == MediaENItem.ITEM_MEDIA_FACEBOOK.ordinal() ) {
				PackageManager pm = getPackageManager();
				Intent intent = pm.getLaunchIntentForPackage("com.facebook.katana");
				startActivity(intent);
				/*ShellCmdUtils.getInstance().execCommand("am start -n com.facebook.katana/.LoginActivity");*/
			} else {
				Support.doStartApplicationWithPackageName(mContext,
						CTConstant.mediaENPkgName[item]);
			}
		} else {
			if( item == MediaItem.ITEM_MEDIA_MP3.ordinal() ) {
				Support.doStartApplicationWithPackageName(this, "com.android.music", 
						"com.android.music.MusicBrowserActivity");
			} else {
				Support.doStartApplicationWithPackageName(mContext,
						CTConstant.mediaPkgName[item]);
			}
		}
		btn_sportmode_media_out_layout.setVisibility(View.GONE);
		btn_sportmode_media_in.setVisibility(View.VISIBLE);

		FloatWindowManager.getInstance(mContext).
			runningActivityStartMedia(mRunningParam);
    }

    public void changePluse() {
    	if( UserInfoManager.getInstance().ecgValue > 0 ) {
			if( img_pulse.getDrawable().getConstantState().equals(
					getResources().getDrawable(
							R.drawable.img_pulse_1).getConstantState()) ) {
				img_pulse.setImageResource(R.drawable.img_pulse_2);
			} else {
				img_pulse.setImageResource(R.drawable.img_pulse_1);
			}
		} else {
			img_pulse.setImageResource(R.drawable.img_pulse_1);
		}
    	UserInfoManager.getInstance().addHrList(UserInfoManager.getInstance().ecgValue, 
				mRunningParam.alreadyRunTime);
    }

}
