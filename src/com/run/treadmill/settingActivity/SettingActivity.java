package com.run.treadmill.settingActivity;


import com.run.treadmill.BaseActivity;
import com.run.treadmill.R;

import com.run.treadmill.db.UserInfoManager;
import com.run.treadmill.floatWindow.FloatWindowManager;
import com.run.treadmill.manager.SelfAudioManager;
import com.run.treadmill.selfCaculaterPopuWindow.CaculaterPopuWindowOfPass;
import com.run.treadmill.selfCaculaterPopuWindow.CaculaterPopuWindowOfPass.PassWinCallBack;
import com.run.treadmill.selfCaculaterPopuWindow.CaculaterPopuWindowOfSetting;
import com.run.treadmill.serialutil.Command;
import com.run.treadmill.serialutil.SerialUtils;
import com.run.treadmill.util.CTConstant;
import com.run.treadmill.util.MyUtils;
import com.run.treadmill.util.StorageParam;
import com.run.treadmill.util.Support;

import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


public class SettingActivity extends BaseActivity implements OnClickListener, OnTouchListener,
	OnSeekBarChangeListener, PassWinCallBack {
	
	private String TAG = "SettingActivity";
	private Context mContext;

	private ImageView btn_home;
	private ImageView btn_ergo;

	private TextView textview_first;
	private RelativeLayout layout_system;
	private SeekBar mBrightnessSeekBar;
	private SeekBar mSoundSeekBar;
	private RelativeLayout layout_lock;

	private TextView textview_second;
	private RelativeLayout layout_bluetooth;
	private RelativeLayout layout_password;


	private TextView textview_wireless;
	private RelativeLayout layout_wireless;

	private RelativeLayout layout_time_dis;
	private TextView textview_lock;
	private RelativeLayout layout_time;
	private RelativeLayout layout_distance;
	private EditText editText_time;
	private EditText editText_distance;
	private ImageView btn_time_reset;
	private ImageView btn_distance_reset;
	private TextView editText_remaintime;
	private TextView editText_remaindis;

	private EditText editText_custompass;
	private ImageView btn_pass_reset;
	
	
	private CaculaterPopuWindowOfSetting caluWindow;
	private CaculaterPopuWindowOfPass passWordWindow;
	private AudioManager mAudioManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		/*SystemBrightManager.setBrightnessMode(this, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);*/
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		/*mAudioManager.adjustStreamVolume (AudioManager.STREAM_MUSIC, 
				AudioManager.ADJUST_RAISE | AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);*/

		setDispContentView(null);
		mContext = this;
		caluWindow = new CaculaterPopuWindowOfSetting(this);
		passWordWindow = new CaculaterPopuWindowOfPass(this);

	}

	@Override
	public void onClick(View v) {
		Support.buzzerRingOnce();
		switch (v.getId()) {
			case R.id.btn_home:

				
				if( textview_first.getText().toString().contains(
						getResources().getString(R.string.string_set_lock)) ) {
					btn_home.setImageResource(R.drawable.btn_home);

					textview_first.setText(getResources().getString(R.string.string_set_system));
					textview_second.setText(getResources().getString(R.string.string_set_bluetooth));
					textview_first.setTextColor(this.getResources().getColor(R.color.blue_light));
					textview_second.setTextColor(this.getResources().getColor(color.darker_gray));

					layout_system.setVisibility(View.VISIBLE);
					layout_bluetooth.setVisibility(View.GONE);
					layout_wireless.setVisibility(View.GONE);
					layout_lock.setVisibility(View.GONE);

					textview_lock.setVisibility(View.VISIBLE);
					textview_wireless.setVisibility(View.VISIBLE);

					passWordWindow.StopPopupWindow();
					caluWindow.StopPopupWindow();
					textview_second.setEnabled(true);

					
					layout_distance.setVisibility(View.VISIBLE);
					layout_time.setVisibility(View.VISIBLE);
					layout_time_dis.setVisibility(View.GONE);
					layout_password.setVisibility(View.GONE);
				} else {
					finish();
				}
				break;
			case R.id.textview_first:
				Log.d(TAG, " textview_first " + textview_first.getText().toString() + "=string_set_system==" + 
						getResources().getString(R.string.string_set_system));
				if ( textview_first.getText().toString().contains(
						getResources().getString(R.string.string_set_system)) ) {
					layout_system.setVisibility(View.VISIBLE);
					layout_bluetooth.setVisibility(View.GONE);
					layout_wireless.setVisibility(View.GONE);
					textview_first.setTextColor(this.getResources().getColor(R.color.blue_light));
					textview_second.setTextColor(this.getResources().getColor(color.darker_gray));
					textview_wireless.setTextColor(this.getResources().getColor(color.darker_gray));

				} else {
					if ( textview_second.isEnabled() ) {
						passWordWindow.StopPopupWindow();
					}
					layout_lock.setVisibility(View.VISIBLE);
					layout_password.setVisibility(View.GONE);
					textview_first.setTextColor(this.getResources().getColor(R.color.blue_light));
					textview_second.setTextColor(this.getResources().getColor(color.darker_gray));
				}
				break;
			case R.id.textview_second:
				Log.d(TAG, " textview_second " + textview_second.getText().toString() + "=string_set_bluetooth==" + 
						getResources().getString(R.string.string_set_bluetooth));
				if ( textview_second.getText().toString().contains(
						getResources().getString(R.string.string_set_bluetooth)) ) {
					layout_system.setVisibility(View.GONE);
					layout_bluetooth.setVisibility(View.VISIBLE);
					layout_wireless.setVisibility(View.GONE);
					textview_first.setTextColor(this.getResources().getColor(color.darker_gray));
					textview_second.setTextColor(this.getResources().getColor(R.color.blue_light));
					textview_wireless.setTextColor(this.getResources().getColor(color.darker_gray));

					FloatWindowManager.getInstance(this).startOtherBackFloatWindow();
					Support.doStartApplicationWithPackageName(this, "com.android.settings",
							"com.android.settings.bluetooth.BluetoothSettings");
				} else {
					layout_distance.setVisibility(View.VISIBLE);
					layout_time.setVisibility(View.VISIBLE);
					caluWindow.StopPopupWindow();
					layout_lock.setVisibility(View.GONE);
					layout_password.setVisibility(View.VISIBLE);
					textview_first.setTextColor(this.getResources().getColor(color.darker_gray));
					textview_second.setTextColor(this.getResources().getColor(R.color.blue_light));
					editText_custompass.setCursorVisible(false);
					editText_custompass.setTextColor(this.getResources().getColor(R.color.blue_light));
				}
				break;
			case R.id.textview_wireless:
				layout_system.setVisibility(View.GONE);
				layout_bluetooth.setVisibility(View.GONE);
				layout_wireless.setVisibility(View.VISIBLE);
				textview_first.setTextColor(this.getResources().getColor(color.darker_gray));
				textview_second.setTextColor(this.getResources().getColor(color.darker_gray));
				textview_wireless.setTextColor(this.getResources().getColor(R.color.blue_light));

				FloatWindowManager.getInstance(this).startOtherBackFloatWindow();
				Support.doStartApplicationWithPackageName(this, "com.android.settings", 
						"com.android.settings.wifi.WifiSettings");
				break;
			case R.id.textview_lock:
				textview_first.setText(getResources().getString(R.string.string_set_lock));
				textview_second.setText(getResources().getString(R.string.string_set_password));
				textview_first.setTextColor(this.getResources().getColor(R.color.blue_light));
				textview_second.setTextColor(this.getResources().getColor(color.darker_gray));
				textview_wireless.setTextColor(this.getResources().getColor(color.darker_gray));

				layout_system.setVisibility(View.GONE);
				layout_bluetooth.setVisibility(View.GONE);
				layout_wireless.setVisibility(View.GONE);
				layout_lock.setVisibility(View.VISIBLE);

				textview_lock.setVisibility(View.GONE);
				textview_wireless.setVisibility(View.GONE);

				passWordWindow.setRelatedAndRes(null,
						R.drawable.tv_keybord_password, layout_time_dis, this);
				passWordWindow.StartPopupWindow(findViewById(R.id.main), 508, 159);
				textview_second.setEnabled(false);
				btn_home.setImageResource(R.drawable.btn_back_revert);
				break;
			case R.id.btn_time_reset:
				/*long timeValue = Long.parseLong(editText_time.getText().toString());
				StorageParam.setMaxTime(this, timeValue * 60 * 60);
				long remainTime = StorageParam.getMaxTime(this) - 
						StorageParam.getRunTotalTime(this);
				editText_remaintime.setText(MyUtils.getSecToRemainTime(remainTime));*/
				layout_distance.setVisibility(View.GONE);
				editText_time.setCursorVisible(false);
				editText_time.setTextColor(this.getResources().getColor(R.color.blue_light));
				editText_distance.setCursorVisible(false);
				caluWindow.setRelatedAndRes(editText_time,
						R.drawable.tv_keybord_time, layout_distance, editText_remaintime);
				caluWindow.StartPopupWindow(findViewById(R.id.main), 741, 169);
				break;
			case R.id.btn_distance_reset:
				/*long timeDis = Long.parseLong(editText_distance.getText().toString());
				StorageParam.setMaxDis(this, timeDis);
				float mRunTotalDis = StorageParam.getRunTotalDis(this);
				long remainDis = StorageParam.getMaxDis(this) - 
						(long)mRunTotalDis;
				editText_remaindis.setText(remainDis + " km");*/
				layout_time.setVisibility(View.GONE);
				editText_distance.setCursorVisible(false);
				editText_distance.setTextColor(this.getResources().getColor(R.color.blue_light));
				caluWindow.setRelatedAndRes(editText_distance,
						R.drawable.tv_keybord_distance, layout_time, editText_remaindis);
				caluWindow.StartPopupWindow(findViewById(R.id.main), 171, 169);
				break;
			case R.id.btn_pass_reset:
				/*StorageParam.setCustomPass(this, editText_custompass.getText().toString());*/
				editText_custompass.setCursorVisible(false);
				editText_custompass.setTextColor(this.getResources().getColor(R.color.blue_light));
				passWordWindow.setRelatedAndRes(editText_custompass,
						R.drawable.tv_keybord_password, null, this);
				passWordWindow.StartPopupWindow(findViewById(R.id.main), 784, 159);
				break;
			default:
				break;
		}

	}

	@Override
	public void setPassCallback() {
		textview_second.setEnabled(true);
	}

	/**
	 * @param view
	 * @exception设置contextView后再重新获取各个组件
	 */
	private void setDispContentView(View view) {
		initSubView(view);
		setSubClickListener(view);
		enterLockWin();
	}

	/**
	 * @exception初始化子Ui
	 */
	private void initSubView(View view) {
		btn_home = (ImageView) findViewById(R.id.btn_home);
		btn_ergo = (ImageView) findViewById(R.id.btn_ergo);

		textview_first = (TextView) findViewById(R.id.textview_first);
		mBrightnessSeekBar = (SeekBar) findViewById(R.id.system_brightness);
		/*mBrightnessSeekBar.setProgress( SystemBrightManager.getBrightness(this) * 
				mBrightnessSeekBar.getMax() / 255 );*/
		mBrightnessSeekBar.setProgress( SystemBrightManager.getBrightness(this) * 
				mBrightnessSeekBar.getMax() / 179 );

		mSoundSeekBar = (SeekBar) findViewById(R.id.system_sounds);
		Log.d(TAG, "================" + SelfAudioManager.getInstance(null).
				getCurrentPro(mSoundSeekBar.getMax()));
		mSoundSeekBar.setProgress( SelfAudioManager.getInstance(null).
				getCurrentPro(mSoundSeekBar.getMax()) );
		layout_system = (RelativeLayout) findViewById(R.id.layout_system);
		layout_lock = (RelativeLayout) findViewById(R.id.layout_lock);

		textview_second = (TextView) findViewById(R.id.textview_second);
		layout_bluetooth = (RelativeLayout) findViewById(R.id.layout_bluetooth);
		layout_password = (RelativeLayout) findViewById(R.id.layout_password);

		textview_wireless = (TextView) findViewById(R.id.textview_wireless);
		layout_wireless = (RelativeLayout) findViewById(R.id.layout_wireless);

		layout_time_dis = (RelativeLayout) findViewById(R.id.layout_time_dis);
		textview_lock = (TextView) findViewById(R.id.textview_lock);
		layout_time = (RelativeLayout) findViewById(R.id.layout_time);
		layout_distance = (RelativeLayout) findViewById(R.id.layout_distance);

		editText_time = (EditText) findViewById(R.id.editText_time);
		editText_time.setText(MyUtils.getSecToHour(StorageParam.getMaxTime(this)));
		editText_remaintime = (TextView) findViewById(R.id.editText_remaintime);

		long remainTime = StorageParam.getMaxTime(this) - 
				StorageParam.getRunTotalTime(this);
		remainTime = remainTime <0 ? 0 : remainTime;
		editText_remaintime.setText(MyUtils.getSecToRemainTime(remainTime));

		editText_distance = (EditText) findViewById(R.id.editText_distance);
		editText_distance.setText(StorageParam.getMaxDis(this)+"");
		editText_remaindis = (TextView) findViewById(R.id.editText_remaindis);

		float mRunTotalDis = StorageParam.getRunTotalDis(this);
		long remainDis = StorageParam.getMaxDis(this) - 
				(long)mRunTotalDis;
		remainDis = remainDis <0 ? 0 : remainDis;
		editText_remaindis.setText(remainDis + " km");
		btn_time_reset = (ImageView) findViewById(R.id.btn_time_reset);
		btn_distance_reset = (ImageView) findViewById(R.id.btn_distance_reset);

		editText_custompass = (EditText) findViewById(R.id.editText_custompass);
		editText_custompass.setText(StorageParam.getCustomPass(this));
		btn_pass_reset = (ImageView) findViewById(R.id.btn_pass_reset);

		return;
	}

	private void setSubClickListener(View view) {
		btn_home.setOnClickListener(this);

		textview_first.setOnClickListener(this);
		mBrightnessSeekBar.setOnSeekBarChangeListener(this);
		mSoundSeekBar.setOnSeekBarChangeListener(this);

		textview_second.setOnClickListener(this);
		textview_wireless.setOnClickListener(this);
		textview_lock.setOnClickListener(this);
		
		editText_time.setOnTouchListener(this);
		editText_distance.setOnTouchListener(this);
		btn_time_reset.setOnClickListener(this);
		btn_distance_reset.setOnClickListener(this);

		editText_custompass.setOnTouchListener(this);
		btn_pass_reset.setOnClickListener(this);
		return;
	}

	public void enterLockWin() {
		if ( !getIntent().getBooleanExtra(CTConstant.ISLOCK, false) ) {
			return ;
		}
		textview_first.setText(getResources().getString(R.string.string_set_lock));
		textview_second.setText(getResources().getString(R.string.string_set_password));
		textview_first.setTextColor(this.getResources().getColor(R.color.blue_light));
		textview_second.setTextColor(this.getResources().getColor(color.darker_gray));

		layout_system.setVisibility(View.GONE);
		layout_bluetooth.setVisibility(View.GONE);
		layout_wireless.setVisibility(View.GONE);
		layout_lock.setVisibility(View.VISIBLE);

		textview_lock.setVisibility(View.GONE);
		textview_wireless.setVisibility(View.GONE);

		textview_second.setEnabled(false);
		layout_time_dis.setVisibility(View.VISIBLE);
		textview_second.setEnabled(true);
		btn_home.setImageResource(R.drawable.btn_back_revert);
	}

	@Override
    public boolean dispatchKeyEvent(KeyEvent event) {
		if( event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN ) {
			/*mAudioManager.adjustStreamVolume (AudioManager.STREAM_MUSIC, 
    				AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND );*/
			return true;
		} else if ( event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP ) {
			/*mAudioManager.adjustStreamVolume (AudioManager.STREAM_MUSIC, 
    				AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND );*/
			return true;
		}
        return super.dispatchKeyEvent(event);
    }

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {}
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if ( seekBar == mBrightnessSeekBar ) {
			Log.d(TAG,"mBrightnessSeekBar=" + progress + " value =" + SystemBrightManager.getBrightness(this));
			int max = seekBar.getMax();
			int toset = (progress * 179 / max);
			SystemBrightManager.setBrightness(this, toset);
		} else if ( seekBar == mSoundSeekBar ) {
			Log.d(TAG,"mSoundSeekBar=" + progress + " value " + 
					mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
			SelfAudioManager.getInstance(null).setAudioVolume(progress, seekBar.getMax());
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
			case R.id.editText_time:
//				layout_distance.setVisibility(View.GONE);
				/*editText_time.requestFocus();
				editText_time.setSelection(editText_time.getText().length());*/
				editText_time.setCursorVisible(false);
				editText_time.setTextColor(this.getResources().getColor(R.color.blue_light));
				editText_distance.setCursorVisible(false);
//				caluWindow.setRelatedAndRes(editText_time,
//						R.drawable.tv_keybord_time, layout_distance, editText_remaintime);
//				caluWindow.StartPopupWindow(findViewById(R.id.main), 784, 159);
				break;
			case R.id.editText_distance:
//				layout_time.setVisibility(View.GONE);
				/*editText_distance.requestFocus();
				editText_distance.setSelection(editText_distance.getText().length());*/
				editText_distance.setCursorVisible(false);
				editText_distance.setTextColor(this.getResources().getColor(R.color.blue_light));
//				caluWindow.setRelatedAndRes(editText_distance,
//						R.drawable.tv_keybord_distance, layout_time, editText_remaindis);
//				caluWindow.StartPopupWindow(findViewById(R.id.main), 214, 159);
				break;
			case R.id.editText_custompass:
				editText_custompass.setCursorVisible(false);
				editText_custompass.setTextColor(this.getResources().getColor(R.color.blue_light));
//				passWordWindow.setRelatedAndRes(editText_custompass,
//						R.drawable.tv_keybord_password, null, this);
//				passWordWindow.StartPopupWindow(findViewById(R.id.main), 784, 159);
				break;
			
			default:
				break;
		}
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG,"========================");
		/*InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
		// 获取软键盘的显示状态
		boolean isOpen=imm.isActive();
		// 如果软键盘已经显示，则隐藏，反之则显示 
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);*/
		/*InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
		    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
		}*/
		((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);  

		Support.setLogoIcon(this, btn_ergo);
		if( UserInfoManager.getInstance().mIsreleaseQuitFlag ) {
			UserInfoManager.getInstance().mIsreleaseQuitFlag = false;
			finish();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG,"onDestroy");
		caluWindow.StopPopupWindow();
		passWordWindow.DesPopupWinAndInerDialog();
	}

	@Override
	public void errorEventProc(int errorValue) {
		if( errorValue == 1 ) {
			finish();
		}
	}

	@Override
	public void onCmdEvent(Message msg) {
		switch (msg.arg1) {
			case Command.KEY_CMD_STOP_CANCEL:
				if ( passWordWindow.isShowPassPopWin() ) {
					Support.keyToneOnce();
					passWordWindow.closePassPopWin();
				}
				break;
		}

	}

}
