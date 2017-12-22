package com.run.treadmill;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.run.treadmill.ModeSettingActivity.FitnessTestModeSettingActivity;
import com.run.treadmill.ModeSettingActivity.GoalModeSettingActivity;
import com.run.treadmill.ModeSettingActivity.HRCModeSettingActivity;
import com.run.treadmill.ModeSettingActivity.HillModeSettingActivity;
import com.run.treadmill.ModeSettingActivity.IntervalModeSettingActivity;
import com.run.treadmill.ModeSettingActivity.UserProgramModeSettingActivity;
import com.run.treadmill.ModeSettingActivity.VirtualRealityModeSettingActivity;
import com.run.treadmill.adapter.MyPagerAdapter;
import com.run.treadmill.db.UserInfoManager;
import com.run.treadmill.factorySettingActivity.FactoryActivity;
import com.run.treadmill.floatWindow.FloatWindowManager;
import com.run.treadmill.manager.ErrorProcManager;
import com.run.treadmill.runningModeActivity.RunningQuickActivity;
import com.run.treadmill.runningParamManager.RunParamTableManager;
import com.run.treadmill.selfdefView.BlurringView;
import com.run.treadmill.selfdefView.HomePopupDialog;
import com.run.treadmill.selfdefView.LongPressView;
import com.run.treadmill.selfdefView.NoScrollViewPager;
import com.run.treadmill.serialutil.Command;
import com.run.treadmill.serialutil.SerialUtils;
import com.run.treadmill.serialutil.Command.FANSTATUS;
import com.run.treadmill.serialutil.Command.SYSRUNSTATUS;
import com.run.treadmill.settingActivity.SettingActivity;
import com.run.treadmill.systemInitParam.InitParam;
import com.run.treadmill.util.BaseTimer.TimerCallBack;
import com.run.treadmill.util.CTConstant.MediaENItem;
import com.run.treadmill.util.CTConstant.MediaItem;
import com.run.treadmill.util.CTConstant.RunMode;
import com.run.treadmill.util.BaseTimer;
import com.run.treadmill.util.CTConstant;
import com.run.treadmill.util.MyUtils;
import com.run.treadmill.util.PermissionUtil;
import com.run.treadmill.util.StorageParam;
import com.run.treadmill.util.Support;

public class HomeActivity extends BaseActivity implements OnPageChangeListener, OnClickListener,
	TimerCallBack {

	private String TAG = "HomeActivity" ;

	private RelativeLayout m_activity_main;
	private RelativeLayout blurredView;
	private BlurringView mBlurringView;

	private RelativeLayout error_status_layout;
	private ImageView error_status_icon;
	private EditText textview_lock;
	private NoScrollViewPager viewPager;
	private ImageView pager_prompt_dot;
	private ImageView btn_home_language;
	private ImageView btn_home_setting;
	private LongPressView btn_ergo;
	private LayoutInflater mLayoutInflater;
	private MyPagerAdapter mPagerAdapter;
	private ArrayList<View> mViewList = new ArrayList<View>();
	
	private ImageView runModeView[] = new ImageView[RunMode.IDX_HOME_USERPROGRAM_MODE.ordinal()+1];

	private ImageView btn_home_bai;
	private ImageView btn_home_weibo;
	private ImageView btn_home_i71;
	private ImageView btn_home_avin;
	private ImageView btn_home_mp3;
	private ImageView btn_home_mp4;
	private ImageView btn_home_three_2;
	private ImageView btn_home_three_3;
	private ImageView btn_home_three_4;
	private ImageView btn_home_three_5;
	private ImageView btn_two_quickstart;
	private ImageView btn_three_quickstart;
	
	private TextView textview_cmd;
	
	
	private Context mContext;
	private FloatWindowManager mFloatWindowManager;

	private final int SLEEP_TIME_VALUE = 5 * 1000 * 60;
	private BaseTimer mSleepTimer = new BaseTimer();
	private boolean clickFlag = false;

	private HomePopupDialog mHomePopupDialog;
	private int old_error = -1;
	private int new_error = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate");
		super.onCreate(savedInstanceState);
		Log.d(TAG, " " + Build.VERSION.SDK_INT + " " + Build.VERSION_CODES.M);
		/*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			finish();
		}*/
//		Support.isGrantExternalRW(this);
		setContentView(R.layout.activity_main);
		AndroidBug5497Workaround.assistActivity(findViewById(android.R.id.content));

		mContext = this;
		mLayoutInflater = getLayoutInflater();
		//创建用户对象
		UserInfoManager.getInstance();
		RunParamTableManager.getInstance(mContext);
		initSubView();

		mSerialUtils = SerialUtils.getInstance();
		/*mSerialUtils.init(mContext);*/
		StorageParam.setRunStopTime(this, 0l);
		Support.hideBottomUIMenu(this);
		PermissionUtil.hasReadExternalStoragePermission(this);
		PermissionUtil.hasAlertWindowPermission(this);
		mFloatWindowManager = FloatWindowManager.getInstance(mContext);
		mHomePopupDialog = new HomePopupDialog(mContext, false);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		//
		/*StorageParam.setPwm(mContext, 1, 2);
		short[] aa = StorageParam.getPwmArray(mContext);
		for( int i = 0; i < 36; i++ ) {
			Log.d(TAG,"PPP=========i " + i + " a= " + aa[i] + "   " + StorageParam.getPwm(mContext, i));
		}*/
		//

	}
	
	private void initSubView() {

		m_activity_main = ( RelativeLayout ) findViewById(R.id.m_activity_main);

		blurredView = ( RelativeLayout ) findViewById(R.id.blurred_view_group);
		mBlurringView = (BlurringView) findViewById(R.id.blurring_view);

		textview_cmd = (TextView) findViewById(R.id.textview_cmd);

		btn_home_language = (ImageView) findViewById(R.id.btn_home_language);
		error_status_layout = (RelativeLayout) findViewById(R.id.error_status_layout);
		error_status_icon = (ImageView) findViewById(R.id.error_status_icon);
		textview_lock = (EditText) findViewById(R.id.textview_lock);

		Locale locale = getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		String country = locale.getCountry();
		Log.d(TAG,"language " + language + " country " + country);
		if(language.endsWith("en")) {
			btn_home_language.setImageResource(R.drawable.btn_home_language_us);
		} else if(language.endsWith("zh") && country.endsWith("CN")) {
			btn_home_language.setImageResource(R.drawable.btn_home_language_cn);
		} else if( language.endsWith("de") )  {
			btn_home_language.setImageResource(R.drawable.btn_home_language_de);
		} else if(language.endsWith("tr") && country.endsWith("TR"))  {
			btn_home_language.setImageResource(R.drawable.btn_home_language_tr);
		} else if(language.endsWith("ir") && country.endsWith("IR"))  {
			btn_home_language.setImageResource(R.drawable.btn_home_language_ir);
		} else if(language.endsWith("es") && country.endsWith("ES"))  {
			btn_home_language.setImageResource(R.drawable.btn_home_language_esp);
		} else if(language.endsWith("pt") && country.endsWith("PT"))  {
			btn_home_language.setImageResource(R.drawable.btn_home_language_pt);
		} else if(language.endsWith("ru") && country.endsWith("RU"))  {
			btn_home_language.setImageResource(R.drawable.btn_home_language_rus);
		}
		btn_home_setting = (ImageView) findViewById(R.id.btn_home_setting);
		btn_ergo = (LongPressView) findViewById(R.id.btn_ergo);


		View viewOne;
		viewOne = mLayoutInflater.inflate(R.layout.home_view_pager_one, null);
		View viewTwo;
		viewTwo = mLayoutInflater.inflate(R.layout.home_view_pager_two, null);
		btn_home_mp3 = (ImageView) viewTwo.findViewById(R.id.btn_home_mp3);
		btn_home_bai = (ImageView) viewTwo.findViewById(R.id.btn_home_bai);
		btn_home_weibo = (ImageView) viewTwo.findViewById(R.id.btn_home_weibo);
		btn_home_i71 = (ImageView) viewTwo.findViewById(R.id.btn_home_i71);
		btn_home_avin = (ImageView) viewTwo.findViewById(R.id.btn_home_avin);
		btn_two_quickstart = (ImageView) viewTwo.findViewById(R.id.btn_two_quickstart);
		if ( !language.endsWith("zh") ) {
			btn_home_bai.setImageResource(R.drawable.btn_home_youtube);
			btn_home_weibo.setImageResource(R.drawable.btn_home_chrome);
			btn_home_i71.setImageResource(R.drawable.btn_home_facebook);
			btn_home_avin.setImageResource(R.drawable.btn_home_flikr);
			btn_home_mp3.setImageResource(R.drawable.btn_home_instagram);
		}

		View viewThree;
		viewThree = mLayoutInflater.inflate(R.layout.home_view_pager_three, null);
		btn_home_mp4 = (ImageView) viewThree.findViewById(R.id.btn_home_mp4);
		btn_home_three_2 = (ImageView) viewThree.findViewById(R.id.btn_home_three_2);

		btn_home_three_3 = (ImageView) viewThree.findViewById(R.id.btn_home_three_3);
		btn_home_three_4 = (ImageView) viewThree.findViewById(R.id.btn_home_three_4);
		btn_home_three_5 = (ImageView) viewThree.findViewById(R.id.btn_home_three_5);
		btn_three_quickstart = (ImageView) viewThree.findViewById(R.id.btn_three_quickstart);
		if ( !language.endsWith("zh") ) {
			btn_home_mp4.setImageResource(R.drawable.btn_home_mp3);
			btn_home_three_2.setImageResource(R.drawable.btn_home_mp4);
			btn_home_three_2.setVisibility(View.VISIBLE);
			btn_home_three_3.setImageResource(R.drawable.btn_home_avin);
			btn_home_three_3.setVisibility(View.VISIBLE);
			btn_home_three_4.setImageResource(R.drawable.btn_home_twitter);
			btn_home_three_4.setVisibility(View.VISIBLE);
			btn_home_three_5.setVisibility(View.VISIBLE);
		}
		
		viewPager = (NoScrollViewPager) findViewById(R.id.view_pager);
		pager_prompt_dot = (ImageView) findViewById(R.id.pager_prompt_dot);
		mViewList.add(viewOne); 
		mViewList.add(viewTwo);
		mViewList.add(viewThree);
		
		mPagerAdapter = new MyPagerAdapter(mViewList);  
		viewPager.setAdapter(mPagerAdapter); 
		viewPager.setOnPageChangeListener(this);
		
		runModeView[RunMode.IDX_HOME_HILL_MODE.ordinal()] = (ImageView) viewOne.findViewById(R.id.btn_home_hill);
		runModeView[RunMode.IDX_HOME_INTERVAL_MODE.ordinal()] = (ImageView) viewOne.findViewById(R.id.btn_home_interval);
		runModeView[RunMode.IDX_HOME_GOAL_MODE.ordinal()] = (ImageView) viewOne.findViewById(R.id.btn_home_goal);
		runModeView[RunMode.IDX_HOME_FITNESS_MODE.ordinal()] = (ImageView) viewOne.findViewById(R.id.btn_home_fitness);
		runModeView[RunMode.IDX_HOME_HRC_MODE.ordinal()] = (ImageView) viewOne.findViewById(R.id.btn_home_hrc);
		runModeView[RunMode.IDX_HOME_VIRTUAL_MODE.ordinal()] = (ImageView) viewOne.findViewById(R.id.btn_home_virtual);
		runModeView[RunMode.IDX_HOME_USERPROGRAM_MODE.ordinal()] = (ImageView) viewOne.findViewById(R.id.btn_home_userprogram);
		runModeView[RunMode.IDX_HOME_QUICKSTART_MODE.ordinal()] = (ImageView) viewOne.findViewById(R.id.btn_home_quickstart);
		

		btn_home_language.setOnClickListener(this);
		btn_home_setting.setOnClickListener(this);
		btn_two_quickstart.setOnClickListener(this);
		btn_three_quickstart.setOnClickListener(this);

//		btn_ergo.setOnClickListener(this);
		btn_ergo.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
        	public boolean onLongClick(View v) {
				Log.d(TAG,"===============================");
				if( mBlurringView.getVisibility() == View.VISIBLE ) {
					
					return false;
				}
				MyUtils.leftAnimStartActivityForResult(HomeActivity.this, 
						new Intent(mContext, FactoryActivity.class), false);
				return false;
        	}
    	});

		
		for(int i = 0; i <= RunMode.IDX_HOME_USERPROGRAM_MODE.ordinal(); i++) {
			runModeView[i].setOnClickListener(this);
		}

		//主界面第二页
		btn_home_mp3.setOnClickListener(this);
		btn_home_bai.setOnClickListener(this);
		btn_home_weibo.setOnClickListener(this);
		btn_home_i71.setOnClickListener(this);
		btn_home_avin.setOnClickListener(this);
		//主界面第三页
		btn_home_mp4.setOnClickListener(this);
		btn_home_three_2.setOnClickListener(this);
		btn_home_three_3.setOnClickListener(this);
		btn_home_three_4.setOnClickListener(this);
		btn_home_three_5.setOnClickListener(this);
		
		if( StorageParam.getRunMode(this) == CTConstant.DEFAULT ) {
			StorageParam.setRunMode(this, 0);
		}
		/*textview_lock.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		textview_lock.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				showSoftInputFromWindow(HomeActivity.this, textview_lock);
				return false;
			}
		});*/
		textview_lock.setOnKeyListener( new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				Log.d(TAG,"keyCode " + keyCode);//EditorInfo.IME_ACTION_DONE
				if ( keyCode == 66 ){
					
					String outValue;
					outValue = textview_lock.getText().toString();
					if ( textview_lock.getText().length() > 0 )
						textview_lock.setSelection(textview_lock.getText().length());
					if( outValue.contains(StorageParam.getCustomPass(mContext) )
							|| outValue.contains(StorageParam.getSrsPass(mContext)) ) {
						mBlurringView.setVisibility(View.GONE);
						error_status_layout.setVisibility(View.GONE);
						error_status_icon.setImageResource(R.drawable.img_pop_emergencystop);
						textview_lock.setVisibility(View.GONE);
						enbleOnclickEvent();
						Intent intent = new Intent(mContext, SettingActivity.class);
						intent.putExtra(CTConstant.ISLOCK, true);
						MyUtils.leftAnimStartActivityForResult(HomeActivity.this, intent, false);
					}
				}
				return false;
			}
	    });
		/*runModeView[StorageParam.getRunMode(this)].requestFocus();*/
	}
	public void showSoftInputFromWindow(Activity activity, EditText editText) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
		imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT); //SHOW_FORCED  
        /*editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);*/
    }

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
//		m_activity_main.setVisibility(View.VISIBLE);
		if( StorageParam.getRunMode(this) == -1 ) {
			StorageParam.setRunMode(this, 0);
		}
		/*for(int i = 0; i < RunMode.IDX_HOME_USERPROGRAM_MODE.ordinal()+1; i++) {
			runModeView[i].setSelected(false);
		}*/

		/*runModeView[StorageParam.getRunMode(this)].setSelected(true);	*/
		
		Support.setLogoIcon(this, btn_ergo);
		//离开运动界面关闭风扇
		mSerialUtils.setFanOnOff(0);
		UserInfoManager.getInstance().fanStatus = FANSTATUS.TURN_OFF.ordinal();
		if( StorageParam.getSleepMode(mContext) ) {
			startTimerToSleep();
		}
		clickFlag = false;
		if ( StorageParam.getMaxLubeDis(mContext) <= 
				(int)StorageParam.getBakRunTotalDis(mContext) ) {
			mHomePopupDialog.showToPopUpDialog(R.drawable.img_pop_lube_message_1);
		} else if ( StorageParam.getMaxDis(mContext) <= 
				(int)StorageParam.getRunTotalDis(mContext) || 
				StorageParam.getMaxTime(mContext) <= StorageParam.getRunTotalTime(mContext) ) {
			error_status_icon.setImageResource(R.drawable.img_pop_console_locked);
			sendMesgProc(CTConstant.MSG_POP_LOCK_WIN, -1);
		}
		Log.d(TAG,  "getMaxDis " + StorageParam.getMaxDis(mContext) + " " + StorageParam.getRunTotalDis(mContext));

	}

	@Override
    public void resetInfo(int res) {
		StorageParam.setMaxLubeDis(mContext, InitParam.MaxSecLubeDis);
		StorageParam.setBakRunTotalDis(mContext, 0);
		if ( StorageParam.getMaxDis(mContext) <= 
				(int)StorageParam.getRunTotalDis(mContext) || 
				StorageParam.getMaxTime(mContext) <= StorageParam.getRunTotalTime(mContext) ) {
			error_status_icon.setImageResource(R.drawable.img_pop_console_locked);
			sendMesgProc(CTConstant.MSG_POP_LOCK_WIN, -1);
		}

    }

	private void startTimerToSleep() {
		mSleepTimer.closeTimer();
		mSleepTimer.startTimer(SLEEP_TIME_VALUE, this);
	}

	private void stopTimerToSleep() {
		mSleepTimer.closeTimer();
	}

	@Override
	public void callback() {
		if ( UserInfoManager.getInstance().rpm == 0 ) {
			UserInfoManager.getInstance().curLevel = 1;
			mSerialUtils.setRunLevel(UserInfoManager.getInstance().curLevel);
			mSerialUtils.setFanOnOff(0);
			mSerialUtils.setSleep(1);
			Log.d(TAG,"enter Sleep");
		}
		startTimerToSleep();
		Log.d(TAG,"=====Sleep callback=======");
	}
	
	@Override
	public synchronized void onClick(View view) {
		if ( clickFlag ) {
			return ;
		}
		Locale locale = getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		if( view.getId() != R.id.btn_home_quickstart || 
				view.getId() != R.id.btn_two_quickstart || 
				view.getId() != R.id.btn_three_quickstart ) {
			Support.buzzerRingOnce();
		}

		switch (view.getId()) {
			case R.id.btn_home_language:
				Intent intent = new Intent(mContext, LanguageActivity.class);
//				MyUtils.leftAnimStartActivity(this, intent, true);
				/*HomeActivity.this.finish();*/
				HomeActivity.this.startActivity(intent);
				clickFlag = true;
				break;
			case R.id.btn_home_setting:
				MyUtils.leftAnimStartActivityForResult(this, new Intent(mContext, SettingActivity.class), false);
				clickFlag = true;
				break;
			case R.id.btn_home_hill:
				MyUtils.leftAnimStartActivityForResult(this, new Intent(mContext, HillModeSettingActivity.class), false);
				StorageParam.setRunMode(this, RunMode.IDX_HOME_HILL_MODE.ordinal());
				UserInfoManager.getInstance().setRunMode(RunMode.IDX_HOME_HILL_MODE.ordinal());
				clickFlag = true;
				break;
			case R.id.btn_home_interval:
				MyUtils.leftAnimStartActivityForResult(this, new Intent(mContext, IntervalModeSettingActivity.class), false);
				StorageParam.setRunMode(this, RunMode.IDX_HOME_INTERVAL_MODE.ordinal());
				UserInfoManager.getInstance().setRunMode(RunMode.IDX_HOME_INTERVAL_MODE.ordinal());
				clickFlag = true;
				break;
			case R.id.btn_home_goal:
				MyUtils.leftAnimStartActivityForResult(this, new Intent(mContext, GoalModeSettingActivity.class), false);
				StorageParam.setRunMode(this, RunMode.IDX_HOME_GOAL_MODE.ordinal());
				UserInfoManager.getInstance().setRunMode(RunMode.IDX_HOME_GOAL_MODE.ordinal());
				clickFlag = true;
				break;
			case R.id.btn_home_fitness:
				MyUtils.leftAnimStartActivityForResult(this, new Intent(mContext, FitnessTestModeSettingActivity.class), false);
				StorageParam.setRunMode(this, RunMode.IDX_HOME_FITNESS_MODE.ordinal());
				UserInfoManager.getInstance().setRunMode(RunMode.IDX_HOME_FITNESS_MODE.ordinal());
				clickFlag = true;
				break;
			case R.id.btn_home_hrc:
				MyUtils.leftAnimStartActivityForResult(this, new Intent(mContext, HRCModeSettingActivity.class), false);
				
				StorageParam.setRunMode(this, RunMode.IDX_HOME_HRC_MODE.ordinal());
				UserInfoManager.getInstance().setRunMode(RunMode.IDX_HOME_HRC_MODE.ordinal());
				clickFlag = true;
				break;
			case R.id.btn_home_virtual:
				MyUtils.leftAnimStartActivityForResult(this, new Intent(mContext, VirtualRealityModeSettingActivity.class), false);
				StorageParam.setRunMode(this, RunMode.IDX_HOME_VIRTUAL_MODE.ordinal());
				UserInfoManager.getInstance().setRunMode(RunMode.IDX_HOME_VIRTUAL_MODE.ordinal());
				clickFlag = true;
				break;
			case R.id.btn_home_userprogram:
				MyUtils.leftAnimStartActivityForResult(this, new Intent(mContext, UserProgramModeSettingActivity.class), false);
				StorageParam.setRunMode(this, RunMode.IDX_HOME_USERPROGRAM_MODE.ordinal());
				UserInfoManager.getInstance().setRunMode(RunMode.IDX_HOME_USERPROGRAM_MODE.ordinal());
				clickFlag = true;
				break;
			case R.id.btn_home_quickstart:
				UserInfoManager.getInstance().setRunMode(RunMode.IDX_HOME_QUICKSTART_MODE.ordinal());
				UserInfoManager.getInstance().setTargetTime(0, UserInfoManager.getInstance().getRunMode());
				MyUtils.leftAnimStartActivityForResult(this, new Intent(mContext, RunningQuickActivity.class), false);
				StorageParam.setRunMode(this, RunMode.IDX_HOME_QUICKSTART_MODE.ordinal());
				clickFlag = true;
				break;
			case R.id.btn_home_mp3:
				if ( !language.endsWith("zh") ) {
					UserInfoManager.getInstance().mediaMode = MediaENItem.ITEM_MEDIA_INSTAGRAM.ordinal();
					enterThirdPartyUsage();
				} else {
					UserInfoManager.getInstance().mediaMode = MediaItem.ITEM_MEDIA_MP3.ordinal();
					enterThirdPartyUsage();
				}
				/*Support.doStartApplicationWithPackageName(this, "com.android.music", 
						"com.android.music.PlaylistBrowserActivity");*/
				clickFlag = true;
				break;
			case R.id.btn_home_bai:
				if ( !language.endsWith("zh") ) {
					UserInfoManager.getInstance().mediaMode = MediaENItem.ITEM_MEDIA_YOUTUBE.ordinal();
					enterThirdPartyUsage();
				} else {
					UserInfoManager.getInstance().mediaMode = MediaItem.ITEM_MEDIA_BAIDU.ordinal();
					enterThirdPartyUsage();
				}
				
				clickFlag = true;
				break;
			case R.id.btn_home_weibo:
				if ( !language.endsWith("zh") ) {
					UserInfoManager.getInstance().mediaMode = MediaENItem.ITEM_MEDIA_CHROME.ordinal();
					enterThirdPartyUsage();
				} else {
					UserInfoManager.getInstance().mediaMode = MediaItem.ITEM_MEDIA_WEIBO.ordinal();
					enterThirdPartyUsage();
				}
				
				clickFlag = true;
				break;
			case R.id.btn_home_i71:
				if ( !language.endsWith("zh") ) {
					UserInfoManager.getInstance().mediaMode = MediaENItem.ITEM_MEDIA_FACEBOOK.ordinal();
					enterThirdPartyUsage();
				} else {
					UserInfoManager.getInstance().mediaMode = MediaItem.ITEM_MEDIA_AIQIYI.ordinal();
					enterThirdPartyUsage();
				}
				
				clickFlag = true;
				break;
			case R.id.btn_home_avin:
				if ( !language.endsWith("zh") ) {
					UserInfoManager.getInstance().mediaMode = MediaENItem.ITEM_MEDIA_FLIKR.ordinal();
					enterThirdPartyUsage();
				} else {
					UserInfoManager.getInstance().mediaMode = MediaItem.ITEM_MEDIA_AVIN.ordinal();
					enterThirdPartyUsage();
				}
				
				clickFlag = true;
				break;
			case R.id.btn_home_mp4:
				if ( !language.endsWith("zh") ) {
					UserInfoManager.getInstance().mediaMode = MediaENItem.ITEM_MEDIA_MP3.ordinal();
					enterThirdPartyUsage();
				} else {
					UserInfoManager.getInstance().mediaMode = MediaItem.ITEM_MEDIA_MP4.ordinal();
					enterThirdPartyUsage();
				}
				clickFlag = true;
				break;
			case R.id.btn_home_three_2:
				if ( !language.endsWith("zh") ) {
					UserInfoManager.getInstance().mediaMode = MediaENItem.ITEM_MEDIA_MP4.ordinal();
					enterThirdPartyUsage();
				} else {
					UserInfoManager.getInstance().mediaMode = MediaItem.ITEM_MEDIA_SCREEN_MIRROR.ordinal();
					enterThirdPartyUsage();
				}
				clickFlag = true;
				break;
			case R.id.btn_home_three_3:
				if ( !language.endsWith("zh") ) {
					UserInfoManager.getInstance().mediaMode = MediaENItem.ITEM_MEDIA_AVIN.ordinal();
					enterThirdPartyUsage();
				}
				clickFlag = true;
				break;
			case R.id.btn_home_three_4:
				if ( !language.endsWith("zh") ) {
					UserInfoManager.getInstance().mediaMode = MediaENItem.ITEM_MEDIA_TWITTER.ordinal();
					enterThirdPartyUsage();
				}
				clickFlag = true;
				break;
			case R.id.btn_home_three_5:
				if ( !language.endsWith("zh") ) {
					UserInfoManager.getInstance().mediaMode = MediaENItem.ITEM_MEDIA_SCREEN_MIRROR.ordinal();
					enterThirdPartyUsage();
				}
				clickFlag = true;
				break;
			case R.id.btn_two_quickstart:
				UserInfoManager.getInstance().setRunMode(RunMode.IDX_HOME_QUICKSTART_MODE.ordinal());
				UserInfoManager.getInstance().setTargetTime(0, UserInfoManager.getInstance().getRunMode());
				MyUtils.leftAnimStartActivityForResult(this, new Intent(mContext, RunningQuickActivity.class), false);
				StorageParam.setRunMode(this, RunMode.IDX_HOME_QUICKSTART_MODE.ordinal());
				clickFlag = true;
				break;
			case R.id.btn_three_quickstart:
				UserInfoManager.getInstance().setRunMode(RunMode.IDX_HOME_QUICKSTART_MODE.ordinal());
				UserInfoManager.getInstance().setTargetTime(0, UserInfoManager.getInstance().getRunMode());
				MyUtils.leftAnimStartActivityForResult(this, new Intent(mContext, RunningQuickActivity.class), false);
				StorageParam.setRunMode(this, RunMode.IDX_HOME_QUICKSTART_MODE.ordinal());
				clickFlag = true;
				break;
			default:
				break;
		}
	}


	public boolean onKeyDown(int keyCode,KeyEvent event){
		switch(keyCode){
//			case KeyEvent.KEYCODE_HOME:return true;
			//屏蔽掉就可以回到系统
			case KeyEvent.KEYCODE_BACK:
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private final static String ACTION_BACK = "ococci.press.back";
	public Handler mMsgHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case CTConstant.MSG_PROC_ERROR_CMD:
					int[] values = (int[]) msg.obj;
					/*Log.d(TAG, "CTConstant.MSG_PROC_ERROR_CMD " + values[0]);*/
					if( ( error_status_icon.getDrawable().getConstantState().equals(
							getResources().getDrawable(
									R.drawable.img_pop_console_locked).getConstantState()) 
									&& values[0] != 0 ) ||
									(mHomePopupDialog.isShowPopUpDialog() && values[0] != 0) ) {
						textview_lock.setVisibility(View.GONE);
						error_status_layout.setVisibility(View.GONE);
						mBlurringView.setVisibility(View.GONE);
						error_status_icon.setImageResource(R.drawable.img_pop_emergencystop);
						mHomePopupDialog.hidePopUpDialog();
					}
					if( values[0] == 1) {
						old_error = 1;
						if( mBlurringView.getVisibility() == View.GONE ) {
							viewPager.setCurrentItem(0);
							mBlurringView.setVisibility(View.VISIBLE);
							mBlurringView.setBlurredView(blurredView);
					        mBlurringView.invalidate();
							error_status_layout.setVisibility(View.VISIBLE);
							disableOnclickEvent();
						}
					} else if ( values[0] == 0) {
						new_error = 0;
						Log.d(TAG, "values[0] 0");
						if ( textview_lock.getVisibility() == View.VISIBLE ) {
							break ;
						}
						Log.d(TAG, "values[0] 1");
						if ( mHomePopupDialog.isShowPopUpDialog() ) {
							break;
						}
						Log.d(TAG, "values[0] 2");
						if( mBlurringView.getVisibility() == View.VISIBLE ) {
							mBlurringView.setVisibility(View.GONE);
							error_status_layout.setVisibility(View.GONE);
							enbleOnclickEvent();
							
						}
						if ( isValid() ) {
							//重新弹出其他提示框
							if ( StorageParam.getMaxLubeDis(mContext) <= 
									(int)StorageParam.getBakRunTotalDis(mContext) ) {
								mBlurringView.setVisibility(View.GONE);
								error_status_layout.setVisibility(View.GONE);
								mHomePopupDialog.showToPopUpDialog(R.drawable.img_pop_lube_message_1);
								break;
							}
							if ( StorageParam.getMaxDis(mContext) <= 
									(int)StorageParam.getRunTotalDis(mContext) || 
									StorageParam.getMaxTime(mContext) <= StorageParam.getRunTotalTime(mContext) ) {
								error_status_layout.setVisibility(View.GONE);
								mBlurringView.setVisibility(View.GONE);
								error_status_icon.setImageResource(R.drawable.img_pop_console_locked);
								sendMesgProc(CTConstant.MSG_POP_LOCK_WIN, -1);
								break;
							}
						}

					} else {
						disableOnclickEvent();
						enterErrorPageOther(values[0]);
					}
					break;
				case CTConstant.MSG_SEND_BACK_EVENT:
					Log.d(TAG,"MSG_SEND_BACK_EVENT");
					sendBroadcast(new Intent(ACTION_BACK));  
					break;
				case CTConstant.MSG_PROC_SHOW_CMD:
					Log.d(TAG,"MSG_PROC_SHOW_CMD");
					int[] values1 = (int[]) msg.obj;
					textview_cmd.setText("key: " + values1[0]);
					break;
				case CTConstant.MSG_POP_LOCK_WIN:
					if( mBlurringView.getVisibility() == View.GONE ) {
						mBlurringView.setVisibility(View.VISIBLE);
						mBlurringView.setBlurredView(blurredView);
				        mBlurringView.invalidate();
				        error_status_icon.setImageResource(R.drawable.img_pop_console_locked);
						error_status_layout.setVisibility(View.VISIBLE);
						textview_lock.setText("");
						textview_lock.setVisibility(View.VISIBLE);
						disableOnclickEvent();

						textview_lock.setInputType(EditorInfo.TYPE_CLASS_PHONE);
						showSoftInputFromWindow(HomeActivity.this, textview_lock);
						textview_lock.requestFocus();
						textview_lock.setCursorVisible(true);
						textview_lock.setOnTouchListener(new View.OnTouchListener() {
							@Override
							public boolean onTouch(View arg0, MotionEvent arg1) {
								if ( arg1.getAction() == MotionEvent.ACTION_UP ) {
									textview_lock.setInputType(EditorInfo.TYPE_CLASS_PHONE);
									showSoftInputFromWindow(HomeActivity.this, textview_lock);
									textview_lock.requestFocus();
									textview_lock.setCursorVisible(true);
									/*getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE 
						        			| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);*/
								}
								return true;
							}
						});
					}
					break;
				case MSG_PROC_ENBLE_CONTINUE:
					//加油 lock不能跑步
					if ( StorageParam.getMaxLubeDis(mContext) <= 
							(int)StorageParam.getBakRunTotalDis(mContext) ) {
						break;
					}
					if ( StorageParam.getMaxDis(mContext) <= 
							(int)StorageParam.getRunTotalDis(mContext) || 
							StorageParam.getMaxTime(mContext) <= StorageParam.getRunTotalTime(mContext) ) {
						break;
					}
					int[] values2 = (int[]) msg.obj;
					if( values2[0] == 0 ) {
						runModeView[RunMode.IDX_HOME_QUICKSTART_MODE.ordinal()].setEnabled(true);
						btn_two_quickstart.setEnabled(true);
						btn_three_quickstart.setEnabled(true);
						if ( UserInfoManager.getInstance().sysRunStatus != 
								SYSRUNSTATUS.STA_RESET.ordinal() ) {
							UserInfoManager.getInstance().sysRunStatus = SYSRUNSTATUS.STA_NOR.ordinal();
						}
					} else {
						runModeView[RunMode.IDX_HOME_QUICKSTART_MODE.ordinal()].setEnabled(true);
						btn_two_quickstart.setEnabled(true);
						btn_three_quickstart.setEnabled(true);
					}
					break;
			}
		}
	};

	private void enterErrorPageOther( int errValue)	{

		UserInfoManager.getInstance().sysRunStatus = SYSRUNSTATUS.STA_PAUSE.ordinal();

		error_status_layout.setVisibility(View.VISIBLE);
		error_status_icon.setImageResource(ErrorProcManager.getInstance().getErrRes(errValue));

		ErrorProcManager.getInstance().err_status = errValue;
		ErrorProcManager.getInstance().isHasERR = true;
	}

	@Override
	public void errorEventProc(int errorValue) {
		if( errorValue == 1 ) {
			sendMesgProc(CTConstant.MSG_PROC_ERROR_CMD, errorValue);
		} else {
			if ( !ErrorProcManager.getInstance().E5E6E7IsNoProc(errorValue) ) {
				sendMesgProc(CTConstant.MSG_PROC_ERROR_CMD, errorValue);
			}
		}
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Log.i(TAG, "onActivityResult resultCode: " + resultCode);
			switch (requestCode) {
				case CTConstant.REQUEST_CODE_DELETE:
//					Log.i(TAG, "onActivityResult requestCode: " + requestCode);
					Log.i(TAG, "aaa ==================== 0  " + requestCode);
					boolean isFinsh = data.getBooleanExtra(CTConstant.IS_FINSH, false);

					if( !isFinsh ) {
						Log.i(TAG, "aaa ==================== 1 " + requestCode);
						sendMesgProc(CTConstant.MSG_PROC_ERROR_CMD, 1);
					}

					break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void enbleOnclickEvent() {
		viewPager.setNoScroll(false);
		viewPager.setEnabled(true);
		btn_home_language.setEnabled(true);
		btn_home_setting.setEnabled(true);
		btn_ergo.setEnabled(true);
		for(int i = 0; i <= RunMode.IDX_HOME_USERPROGRAM_MODE.ordinal(); i++) {
			/*if( i != RunMode.IDX_HOME_QUICKSTART_MODE.ordinal() )*/
				runModeView[i].setEnabled(true);
		}
		btn_home_bai.setEnabled(true);
		btn_home_weibo.setEnabled(true);
		btn_home_i71.setEnabled(true);
		btn_home_avin.setEnabled(true);
		btn_home_mp3.setEnabled(true);
		btn_home_mp4.setEnabled(true);
		btn_two_quickstart.setEnabled(true);
		btn_three_quickstart.setEnabled(true);
	}

	public void disableOnclickEvent() {
		viewPager.setNoScroll(true);
		viewPager.setEnabled(false);
		btn_home_language.setEnabled(false);
		btn_home_setting.setEnabled(false);
		btn_ergo.setEnabled(false);
		for(int i = 0; i <= RunMode.IDX_HOME_USERPROGRAM_MODE.ordinal(); i++) {
			runModeView[i].setEnabled(false);
		}
		btn_home_bai.setEnabled(false);
		btn_home_weibo.setEnabled(false);
		btn_home_i71.setEnabled(false);
		btn_home_avin.setEnabled(false);
		btn_home_mp3.setEnabled(false);
		btn_home_mp4.setEnabled(false);
		btn_two_quickstart.setEnabled(false);
		btn_three_quickstart.setEnabled(false);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mFloatWindowManager.stopBackFloatWindow();
		/*StorageStateManager.getInstance().unregisterReceiver(this);*/
		mHomePopupDialog.destoryToPopUpDialog();
		Log.d(TAG,"onDestroy");
	}

	@Override
	public void onPause() {
		super.onPause();
//		m_activity_main.setVisibility(View.GONE);
		stopTimerToSleep();
		Log.d(TAG,"onPause");
	}
	
	@Override  
    public void onPageSelected(int position) {
		if ( error_status_layout.getVisibility() == View.VISIBLE ) {
			return ;
		}
        if ( position == 0 ) {
        	pager_prompt_dot.setImageResource(R.drawable.img_virtual_reality_page_1);
        	
        } else if ( position == 1 ) {
        	pager_prompt_dot.setImageResource(R.drawable.img_virtual_reality_page_2);
        	
        } else if ( position == 2 ) {
        	pager_prompt_dot.setImageResource(R.drawable.img_virtual_reality_page_3);
        	
        } 
    }

    @Override  
    public void onPageScrolled(int arg0, float arg1, int arg2) {  
        // TODO Auto-generated method stub    
    }

    @Override  
    public void onPageScrollStateChanged(int arg0) {  
        // TODO Auto-generated method stub 
    }

    public void enterThirdPartyUsage() {
    	UserInfoManager.getInstance().setRunMode(RunMode.IDX_HOME_QUICKSTART_MODE.ordinal());
		UserInfoManager.getInstance().setTargetTime(0, UserInfoManager.getInstance().getRunMode());
		MyUtils.leftAnimStartActivityForResult(this, new Intent(mContext, RunningQuickActivity.class), false);
		StorageParam.setRunMode(this, RunMode.IDX_HOME_QUICKSTART_MODE.ordinal());
		UserInfoManager.getInstance().mIsMediaMode = true;
		
    }

	@Override
	public void onCmdEvent(Message msg) {
		/*sendMesgProc(CTConstant.MSG_PROC_SHOW_CMD, msg.arg1);*/
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
				enbleOnclickEvent();
				return;
			}
		}
		switch (msg.arg1) {
			case Command.KEY_CMD_QUICK_START:
				if( mBlurringView.getVisibility() == View.GONE && 
						runModeView[RunMode.IDX_HOME_QUICKSTART_MODE.ordinal()].isEnabled() ) {
					/*Support.keyToneOnce();*/
					runModeView[RunMode.IDX_HOME_QUICKSTART_MODE.ordinal()].performClick();
					Support.keyToneOnce();
				}
				break;
			/*case Command.ACTUAL_SPEED:*/
			case Command.KEY_CMD_LEVEL_DEC_F:
			case Command.KEY_CMD_LEVEL_DEC_S:
			case Command.KEY_CMD_LEVEL_PLUS_F:
			case Command.KEY_CMD_LEVEL_PLUS_S:
			case Command.KEY_CMD_LEVEL_DEC_F_LONG_1:
			case Command.KEY_CMD_LEVEL_DEC_F_LONG_2:
			case Command.KEY_CMD_LEVEL_DEC_S_LONG_1:
			case Command.KEY_CMD_LEVEL_DEC_S_LONG_2:
			case Command.KEY_CMD_LEVEL_PLUS_F_LONG_1:
			case Command.KEY_CMD_LEVEL_PLUS_F_LONG_2:
			case Command.KEY_CMD_LEVEL_PLUS_S_LONG_1:
			case Command.KEY_CMD_LEVEL_PLUS_S_LONG_2:
				Log.d(TAG, "KEY_CMD_LEVEL_PLUS_S_LONG_2");
				break;
		}
		return ;
	}

	public boolean isValid() {
		if ( old_error != new_error && old_error == 1 && 
				new_error == 0 ) {
			old_error = -1;
			new_error = -1;
			return true;
		} else {
			old_error = -1;
			new_error = -1;
			return false;
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		//Log.d(TAG, "dispatchTouchEvent");
		stopTimerToSleep();
		if( StorageParam.getSleepMode(mContext) ) {
			startTimerToSleep();
		}
		return super.dispatchTouchEvent(event);
	}

}
