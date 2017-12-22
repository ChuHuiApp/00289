package com.run.treadmill.factorySettingActivity;


import java.io.File;

import com.run.treadmill.BaseActivity;
import com.run.treadmill.R;
import android.R.color;

import com.run.treadmill.manager.StorageStateManager;
import com.run.treadmill.manager.UpgradeManager;
import com.run.treadmill.manager.StorageStateManager.MountCallBack;
import com.run.treadmill.manager.VersionManager;
import com.run.treadmill.selfCaculaterPopuWindow.CaculaterPopuWindowOfFactory;
import com.run.treadmill.selfdefView.PopupDialog;
import com.run.treadmill.serialutil.Command;
import com.run.treadmill.serialutil.ParamCons;
import com.run.treadmill.serialutil.SerialUtils;
import com.run.treadmill.systemInitParam.FactoryTwoParam;
import com.run.treadmill.util.MyUtils;
import com.run.treadmill.util.StorageParam;
import com.run.treadmill.util.Support;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class FactoryActivity extends BaseActivity implements OnClickListener, OnTouchListener,
		MountCallBack {
	
	private String TAG = "FactoryActivity";
	private Context mContext;

	private View factory_one_Layout = null;
    private View factory_two_Layout = null;
    private boolean isShowFac2 = false;
    
	private ImageView btn_home;
	private ImageView btn_ergo;
	private ImageView btn_factory1;
	private ImageView btn_factory2;

	private ImageView btn_factory_metric;
	private ImageView btn_factory_imperial;

	private ImageView btn_factory_bike;
	private ImageView btn_factory_recumbent;
	private ImageView btn_factory_elliptical;


	private RelativeLayout layout_setting;
	private RelativeLayout layout_mode;
	private TextView textview_setting;
	private ImageView btn_onoff_displaymode;
	private ImageView btn_onoff_pausemode;
	private ImageView btn_onoff_keytone;
	private ImageView btn_onoff_buzzer;
	private ImageView btn_onoff_childlock;
	private ImageView btn_onoff_loginctl;
	private ImageView btn_start_keytest;
	private EditText editText_level;
	private EditText editText_pwm;
	private TextView textview_totaltime;
	private TextView textview_totaldis;

	private RelativeLayout layout_information;
	private TextView textview_information;
	private ImageView btn_info_reset;
	private TextView textview_softversion, textview_sdkversion, textview_fireversion;

	private RelativeLayout layout_update;
	private TextView textview_update;
	private ImageView btn_factory_update;

	private RelativeLayout layout_logo;
	private TextView textview_logo;
	private ImageView btn_factory_logoicon;
	private ImageView btn_factory_updatelogo;
	private ImageView btn_factory_updateicon;

	private CaculaterPopuWindowOfFactory caluWindow;
	private PopupDialog mPopupDialog;

	private String logoName = "logo.png";
	private String apkName = "AC00289.apk";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_factory);
		LayoutInflater layoutInflater = LayoutInflater.from(this);

		mContext = this;
		caluWindow = new CaculaterPopuWindowOfFactory(this);


		btn_home = (ImageView) findViewById(R.id.btn_home);
		btn_ergo = (ImageView) findViewById(R.id.btn_ergo);
		btn_factory1 = (ImageView) findViewById(R.id.btn_factory1);
		btn_factory2 = (ImageView) findViewById(R.id.btn_factory2);

		factory_one_Layout = layoutInflater.inflate(R.layout.activity_factory_one, null);
		factory_two_Layout = layoutInflater.inflate(R.layout.activity_factory_two, null);

		btn_home.setOnClickListener(this);
		btn_factory1.setOnClickListener(this);
		btn_factory2.setOnClickListener(this);
		
		Support.hideBottomUIMenu(this);
		mPopupDialog = new PopupDialog(mContext, false);
		StorageStateManager.getInstance().setMountCallBackFun(this);
	}


	@Override
	public void onClick(View v) {
		Support.buzzerRingOnce();
		if ( caluWindow.isShowing() ) {
			caluWindow.StopPopupWindow();
		}
		switch (v.getId()) {
			case R.id.btn_home:
				finish();
				break;
			case R.id.btn_factory1:
				setDispContentView(factory_one_Layout);
				break;
			case R.id.btn_factory2:
				isShowFac2 = true;
				setDispContentView(factory_two_Layout);
				break;
			case R.id.btn_factory_metric:
				btn_factory_metric.setSelected(true);
				btn_factory_imperial.setSelected(false);
				if ( FactoryTwoParam.getInstance().IsMetric ) {
					return ;
				}
				FactoryTwoParam.getInstance().IsMetric = true;
				StorageParam.setUnit(mContext, FactoryTwoParam.getInstance().IsMetric);
				break;
			case R.id.btn_factory_imperial:
				btn_factory_metric.setSelected(false);
				btn_factory_imperial.setSelected(true);
				/*StorageParam.setUnit(mContext, false);*/
				if ( !FactoryTwoParam.getInstance().IsMetric ) {
					return ;
				}
				FactoryTwoParam.getInstance().IsMetric = false;
				StorageParam.setUnit(mContext, FactoryTwoParam.getInstance().IsMetric);
				break;
			case R.id.btn_factory_bike:
				btn_factory_bike.setSelected(true);
				btn_factory_recumbent.setSelected(false);
				btn_factory_elliptical.setSelected(false);
				break;
			case R.id.btn_factory_recumbent:
				btn_factory_bike.setSelected(false);
				btn_factory_recumbent.setSelected(true);
				btn_factory_elliptical.setSelected(false);
				break;
			case R.id.btn_factory_elliptical:
				btn_factory_bike.setSelected(false);
				btn_factory_recumbent.setSelected(false);
				btn_factory_elliptical.setSelected(true);
				break;
			case R.id.textview_setting:
				layout_setting.setVisibility(View.VISIBLE);
				layout_information.setVisibility(View.GONE);
				layout_update.setVisibility(View.GONE);
				layout_logo.setVisibility(View.GONE);
				textview_setting.setTextColor(this.getResources().getColor(R.color.blue_light));
				textview_information.setTextColor(this.getResources().getColor(color.darker_gray));
				textview_update.setTextColor(this.getResources().getColor(color.darker_gray));
				textview_logo.setTextColor(this.getResources().getColor(color.darker_gray));
				
				break;
			case R.id.textview_information:
				layout_setting.setVisibility(View.GONE);
				layout_information.setVisibility(View.VISIBLE);
				layout_update.setVisibility(View.GONE);
				layout_logo.setVisibility(View.GONE);
				textview_setting.setTextColor(this.getResources().getColor(color.darker_gray));
				textview_information.setTextColor(this.getResources().getColor(R.color.blue_light));
				textview_update.setTextColor(this.getResources().getColor(color.darker_gray));
				textview_logo.setTextColor(this.getResources().getColor(color.darker_gray));

				break;
			case R.id.textview_update:
				layout_setting.setVisibility(View.GONE);
				layout_information.setVisibility(View.GONE);
				layout_update.setVisibility(View.VISIBLE);
				layout_logo.setVisibility(View.GONE);
				textview_setting.setTextColor(this.getResources().getColor(color.darker_gray));
				textview_information.setTextColor(this.getResources().getColor(color.darker_gray));
				textview_update.setTextColor(this.getResources().getColor(R.color.blue_light));
				textview_logo.setTextColor(this.getResources().getColor(color.darker_gray));
				if( StorageStateManager.getInstance().getUdiskState() ) {
					btn_factory_update.setEnabled(true);
				} else {
					btn_factory_update.setEnabled(false);
				}

				break;
			case R.id.textview_logo:
				layout_setting.setVisibility(View.GONE);
				layout_information.setVisibility(View.GONE);
				layout_update.setVisibility(View.GONE);
				layout_logo.setVisibility(View.VISIBLE);
				textview_setting.setTextColor(this.getResources().getColor(color.darker_gray));
				textview_information.setTextColor(this.getResources().getColor(color.darker_gray));
				textview_update.setTextColor(this.getResources().getColor(color.darker_gray));
				textview_logo.setTextColor(this.getResources().getColor(R.color.blue_light));
				if( StorageStateManager.getInstance().getUdiskState() ) {
					if ( !Support.isCheckExist(StorageStateManager.getInstance().getUdiskPath() 
							+ logoName) ) {
						break;
					}
					btn_factory_updatelogo.setEnabled(true);
					Bitmap bmpDefaultPic;
					bmpDefaultPic = BitmapFactory.decodeFile(
							StorageStateManager.getInstance().getUdiskPath() + logoName,null);
					btn_factory_updateicon.setImageBitmap(bmpDefaultPic);
					/*if( bmpDefaultPic != null && !bmpDefaultPic.isRecycled() ) {   
						bmpDefaultPic.recycle();   
						bmpDefaultPic = null;   
					}*/
					bmpDefaultPic = null; 
					btn_factory_updateicon.setVisibility(View.VISIBLE);
				} else {
					btn_factory_updatelogo.setEnabled(false);
					/*btn_factory_updateicon.setVisibility(View.GONE);*/
				}

				break;
			case R.id.btn_onoff_displaymode:
				if( (Integer) btn_onoff_displaymode.getTag() == R.drawable.btn_off ) {
					btn_onoff_displaymode.setImageResource(R.drawable.btn_on);
					btn_onoff_displaymode.setTag(R.drawable.btn_on);
					StorageParam.setSleepMode(mContext, false);
				} else {
					btn_onoff_displaymode.setImageResource(R.drawable.btn_off);
					btn_onoff_displaymode.setTag(R.drawable.btn_off);
					StorageParam.setSleepMode(mContext, true);
				}
				break;
			case R.id.btn_onoff_pausemode:
				if( (Integer)btn_onoff_pausemode.getTag() == R.drawable.btn_off ) {
					btn_onoff_pausemode.setImageResource(R.drawable.btn_on);
					btn_onoff_pausemode.setTag(R.drawable.btn_on);
					StorageParam.setPauseMode(mContext, true);
				} else {
					btn_onoff_pausemode.setImageResource(R.drawable.btn_off);
					btn_onoff_pausemode.setTag(R.drawable.btn_off);
					StorageParam.setPauseMode(mContext, false);
				}
				break;
			case R.id.btn_onoff_keytone:
				if( (Integer)btn_onoff_keytone.getTag() == R.drawable.btn_off ) {
					btn_onoff_keytone.setImageResource(R.drawable.btn_on);
					btn_onoff_keytone.setTag(R.drawable.btn_on);
					StorageParam.setkeyTone(mContext, true);
				} else {
					btn_onoff_keytone.setImageResource(R.drawable.btn_off);
					btn_onoff_keytone.setTag(R.drawable.btn_off);
					StorageParam.setkeyTone(mContext, false);
				}
				break;
			case R.id.btn_onoff_buzzer:
				if( (Integer)btn_onoff_buzzer.getTag() == R.drawable.btn_off ) {
					btn_onoff_buzzer.setImageResource(R.drawable.btn_on);
					btn_onoff_buzzer.setTag(R.drawable.btn_on);
					StorageParam.setBuzzer(mContext, true);
				} else {
					btn_onoff_buzzer.setImageResource(R.drawable.btn_off);
					btn_onoff_buzzer.setTag(R.drawable.btn_off);
					StorageParam.setBuzzer(mContext, false);
				}
				break;
			case R.id.btn_onoff_childlock:
				if( (Integer)btn_onoff_childlock.getTag() == R.drawable.btn_off ) {
					btn_onoff_childlock.setImageResource(R.drawable.btn_on);
					btn_onoff_childlock.setTag(R.drawable.btn_on);
				} else {
					btn_onoff_childlock.setImageResource(R.drawable.btn_off);
					btn_onoff_childlock.setTag(R.drawable.btn_off);
				}
				break;
			case R.id.btn_onoff_loginctl:
				if( (Integer)btn_onoff_loginctl.getTag() == R.drawable.btn_off ) {
					btn_onoff_loginctl.setImageResource(R.drawable.btn_on);
					btn_onoff_loginctl.setTag(R.drawable.btn_on);
				} else {
					btn_onoff_loginctl.setImageResource(R.drawable.btn_off);
					btn_onoff_loginctl.setTag(R.drawable.btn_off);
				}
				break;
			case R.id.btn_start_keytest:
				mPopupDialog.showToPopUpDialog(R.drawable.img_pop_key_test);
				break;
			case R.id.btn_info_reset:
				mPopupDialog.showToPopUpDialog(R.drawable.img_pop_factory_reset);
				break;
			case R.id.btn_factory_update:
				Log.d(TAG,"=== "+ StorageStateManager.getInstance().getUdiskPath() + apkName);
				if ( !Support.isCheckExist(StorageStateManager.getInstance().getUdiskPath() 
						+ apkName) ) {
					break;
				}
				UpgradeManager.getInstance().installApk(FactoryActivity.this, 
						new File(StorageStateManager.getInstance().getUdiskPath() + apkName));
				break;
			case R.id.btn_factory_updatelogo:
				if ( !Support.isCheckExist(StorageStateManager.getInstance().getUdiskPath() 
						+ logoName) ) {
					break;
				}
				Support.CopySdcardFile(this, StorageStateManager.getInstance().getUdiskPath() + logoName,
						mContext.getFilesDir() + "/" + logoName);
				StorageParam.setIsInnerLogo(this, false);
				Support.setLogoIcon(mContext, btn_ergo);
				break;
			default:
				break;
		}

	}

	@Override
	public void resetInfo( int res ) {
		super.resetInfo(res);
		Log.d(TAG,"resetInfo");
		if ( res == R.drawable.img_pop_factory_reset ) {
			StorageParam.resetRunTotalDis(this, 0f);
			textview_totaldis = (TextView) factory_two_Layout.findViewById(R.id.textview_totaldis);
			textview_totaldis.setText( (long)StorageParam.getRunTotalDis(this) + " km");
			StorageParam.setRunTotalTime(this, 0l);
			textview_totaltime = (TextView) factory_two_Layout.findViewById(R.id.textview_totaltime);
			textview_totaltime.setText(MyUtils.getSecToRemainTime(StorageParam.getRunTotalTime(this)));
		}
	}

	/**
	 * @param view
	 * @exception设置contextView后再重新获取各个组件
	 */
	private void setDispContentView(View view) {
		setContentView(view);
		initSubView(view);
		setSubClickListener(view);
	}

	/**
	 * @exception初始化子Ui
	 */
	private void initSubView(View view) {
		btn_home = (ImageView) view.findViewById(R.id.btn_home);
		btn_ergo = (ImageView) view.findViewById(R.id.btn_ergo);
		btn_home.setOnClickListener(this);
		Support.setLogoIcon(this, btn_ergo);

		if ( view == factory_one_Layout ) {
			btn_factory_metric = (ImageView) factory_one_Layout.findViewById(R.id.btn_factory_metric);
			btn_factory_imperial = (ImageView) factory_one_Layout.findViewById(R.id.btn_factory_imperial);
			
			btn_factory_bike = (ImageView) factory_one_Layout.findViewById(R.id.btn_factory_bike);
			btn_factory_recumbent = (ImageView) factory_one_Layout.findViewById(R.id.btn_factory_recumbent);
			btn_factory_elliptical = (ImageView) factory_one_Layout.findViewById(R.id.btn_factory_elliptical);
		} else {
			layout_setting = (RelativeLayout) factory_two_Layout.findViewById(R.id.layout_setting);
			layout_mode = (RelativeLayout) factory_two_Layout.findViewById(R.id.layout_mode);
			textview_setting = (TextView) factory_two_Layout.findViewById(R.id.textview_setting);
			btn_onoff_displaymode = (ImageView) factory_two_Layout.findViewById(R.id.btn_onoff_displaymode);
			if ( StorageParam.getSleepMode(mContext) ) {
				btn_onoff_displaymode.setImageResource(R.drawable.btn_off);
				btn_onoff_displaymode.setTag(R.drawable.btn_off);
			} else {
				btn_onoff_displaymode.setImageResource(R.drawable.btn_on);
				btn_onoff_displaymode.setTag(R.drawable.btn_on);
			}
			/*btn_onoff_displaymode.setEnabled(false);*/
			
			btn_onoff_pausemode = (ImageView) factory_two_Layout.findViewById(R.id.btn_onoff_pausemode);
			if ( StorageParam.getPauseMode(mContext) ) {
				btn_onoff_pausemode.setImageResource(R.drawable.btn_on);
				btn_onoff_pausemode.setTag(R.drawable.btn_on);
			} else {
				btn_onoff_pausemode.setImageResource(R.drawable.btn_off);
				btn_onoff_pausemode.setTag(R.drawable.btn_off);
			}
			
			btn_onoff_keytone = (ImageView) factory_two_Layout.findViewById(R.id.btn_onoff_keytone);
			if ( StorageParam.getkeyTone(mContext) ) {
				btn_onoff_keytone.setImageResource(R.drawable.btn_on);
				btn_onoff_keytone.setTag(R.drawable.btn_on);
			} else {
				btn_onoff_keytone.setImageResource(R.drawable.btn_off);
				btn_onoff_keytone.setTag(R.drawable.btn_off);
			}

			btn_onoff_buzzer = (ImageView) factory_two_Layout.findViewById(R.id.btn_onoff_buzzer);
			if( StorageParam.getBuzzer(mContext) ) {
				btn_onoff_buzzer.setTag(R.drawable.btn_on);
				btn_onoff_buzzer.setImageResource(R.drawable.btn_on);
			} else {
				btn_onoff_buzzer.setTag(R.drawable.btn_off);
				btn_onoff_buzzer.setImageResource(R.drawable.btn_off);
			}

			btn_onoff_childlock = (ImageView) factory_two_Layout.findViewById(R.id.btn_onoff_childlock);
			btn_onoff_childlock.setImageResource(R.drawable.btn_on_2);
			btn_onoff_childlock.setTag(R.drawable.btn_on);
			btn_onoff_childlock.setEnabled(false);
			
			btn_onoff_loginctl = (ImageView) factory_two_Layout.findViewById(R.id.btn_onoff_loginctl);
			btn_onoff_loginctl.setTag(R.drawable.btn_on);
			
			btn_start_keytest = (ImageView) factory_two_Layout.findViewById(R.id.btn_start_keytest);
			
			layout_information = (RelativeLayout) factory_two_Layout.findViewById(R.id.layout_information);
			textview_information = (TextView) factory_two_Layout.findViewById(R.id.textview_information);
			btn_info_reset = (ImageView) factory_two_Layout.findViewById(R.id.btn_info_reset);

			textview_sdkversion = (TextView) factory_two_Layout.findViewById(R.id.textview_sdkversion);
			textview_sdkversion.setText(VersionManager.getSdkVersion());
			textview_fireversion = (TextView) factory_two_Layout.findViewById(R.id.textview_fireversion);
			textview_fireversion.setText(VersionManager.getfirewareVersion());
			textview_softversion = (TextView) factory_two_Layout.findViewById(R.id.textview_softversion);
			textview_softversion.setText(VersionManager.getAppVersionName(this));

			textview_totaltime = (TextView) factory_two_Layout.findViewById(R.id.textview_totaltime);
			textview_totaltime.setText(MyUtils.getSecToRemainTime(StorageParam.getRunTotalTime(this)));
			textview_totaldis = (TextView) factory_two_Layout.findViewById(R.id.textview_totaldis);
			textview_totaldis.setText( (long)StorageParam.getRunTotalDis(this) + " km");

			editText_level = (EditText) factory_two_Layout.findViewById(R.id.editText_level);
			editText_pwm = (EditText) factory_two_Layout.findViewById(R.id.editText_pwm);
			editText_pwm.setText( 
					StorageParam.getPwm( mContext, 
							Integer.parseInt(editText_level.getText().toString()) - 1 ) + "" );

			layout_update = (RelativeLayout) factory_two_Layout.findViewById(R.id.layout_update);
			textview_update = (TextView) factory_two_Layout.findViewById(R.id.textview_update);
			btn_factory_update = (ImageView) factory_two_Layout.findViewById(R.id.btn_factory_update);

			layout_logo = (RelativeLayout) factory_two_Layout.findViewById(R.id.layout_logo);
			textview_logo = (TextView) factory_two_Layout.findViewById(R.id.textview_logo);
			btn_factory_logoicon = (ImageView) factory_two_Layout.findViewById(R.id.btn_factory_logoicon);
			Support.setLogoIcon(this, btn_factory_logoicon);
			btn_factory_updatelogo = (ImageView) factory_two_Layout.findViewById(R.id.btn_factory_updatelogo);
			if( !StorageStateManager.getInstance().getUdiskState() ) {
				btn_factory_updatelogo.setEnabled(false);
			}
			btn_factory_updateicon = (ImageView) factory_two_Layout.findViewById(R.id.btn_factory_updateicon);
		}
		return;
	}

	private void setSubClickListener(View view) {
		if ( view == factory_one_Layout ) {
			if( StorageParam.getIsMetric(mContext) ) {
				btn_factory_metric.setSelected(true);
				btn_factory_imperial.setSelected(false);
				FactoryTwoParam.getInstance().IsMetric = true;
			} else {
				btn_factory_metric.setSelected(false);
				btn_factory_imperial.setSelected(true);
				FactoryTwoParam.getInstance().IsMetric = false;
			}
			
			btn_factory_metric.setOnClickListener(this);
			btn_factory_imperial.setOnClickListener(this);

			btn_factory_bike.setSelected(true);
			btn_factory_recumbent.setSelected(false);
			btn_factory_elliptical.setSelected(false);
			btn_factory_bike.setOnClickListener(this);
			btn_factory_recumbent.setOnClickListener(this);
			btn_factory_elliptical.setOnClickListener(this);
		} else {
			textview_setting.setOnClickListener(this);
			btn_onoff_displaymode.setOnClickListener(this);
			btn_onoff_pausemode.setOnClickListener(this);
			btn_onoff_keytone.setOnClickListener(this);
			btn_onoff_buzzer.setOnClickListener(this);
			btn_onoff_childlock.setOnClickListener(this);
			btn_onoff_loginctl.setOnClickListener(this);
			btn_start_keytest.setOnClickListener(this);
			editText_level.setOnTouchListener(this);
			editText_pwm.setOnTouchListener(this);

			textview_information.setOnClickListener(this);
			btn_info_reset.setOnClickListener(this);

			textview_update.setOnClickListener(this);
			btn_factory_update.setOnClickListener(this);

			textview_logo.setOnClickListener(this);
			btn_factory_updatelogo.setOnClickListener(this);
		}
		return;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
			case R.id.editText_level:
				editText_level.setCursorVisible(false);
				layout_mode.setVisibility(View.GONE);
				editText_level.requestFocus();
				editText_level.setSelection(editText_level.getText().length());
				caluWindow.setRelatedAndRes(editText_level,
						R.drawable.tv_keybord_level, layout_mode, editText_pwm, -1);
				caluWindow.StartPopupWindow(findViewById(R.id.main), 171, 169);
				break;
			case R.id.editText_pwm:
				editText_pwm.setCursorVisible(false);
				layout_mode.setVisibility(View.GONE);
				editText_pwm.requestFocus();
				editText_pwm.setSelection(editText_pwm.getText().length());
				caluWindow.setRelatedAndRes(editText_pwm,
						R.drawable.tv_keybord_pwm, layout_mode, editText_level);
				caluWindow.StartPopupWindow(findViewById(R.id.main), 171, 169);
				break;
			default:
				break;
		}
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		Support.setLogoIcon(this, btn_ergo);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG,"onDestroy");
		mPopupDialog.destoryToPopUpDialog();
		StorageStateManager.getInstance().setMountCallBackFun(null);

	}


	@Override
	public void errorEventProc(int errorValue) {
		// TODO Auto-generated method stub
		if( errorValue == 1 ) {
			finish();
		}
	}

	@Override
	public void onCmdEvent(Message msg) {
		if ( !isShowFac2 ) {
			return ;
		}
		switch (msg.arg1) {
			case Command.KEY_CMD_QUICK_START:
				if ( mPopupDialog.isShowPopUpDialog() ) {
					Support.keyToneOnce();
				}
				mPopupDialog.showToKeyIcon(msg.arg1);
				break;
			case Command.KEY_CMD_STOP_CANCEL:
				if ( mPopupDialog.isShowPopUpDialog() ) {
					Support.keyToneOnce();
				}
				mPopupDialog.showToKeyIcon(msg.arg1);
				break;
			case Command.KEY_CMD_LEVEL_PLUS_F:
			case Command.KEY_CMD_LEVEL_PLUS_S:
			case Command.KEY_CMD_LEVEL_PLUS_F_LONG_1:
			case Command.KEY_CMD_LEVEL_PLUS_F_LONG_2:
			case Command.KEY_CMD_LEVEL_PLUS_S_LONG_1:
			case Command.KEY_CMD_LEVEL_PLUS_S_LONG_2:
				mPopupDialog.showToKeyIcon(msg.arg1);
				if ( mPopupDialog.isShowPopUpDialog() ) {
					Support.keyToneOnce();
					break;
				}
				if ( !editText_level.hasFocus() && !editText_pwm.hasFocus() ) {
					break;
				}
				if ( msg.arg1 == Command.KEY_CMD_LEVEL_PLUS_F || 
						msg.arg1 == Command.KEY_CMD_LEVEL_PLUS_S ) {
					Support.keyToneOnce();
				}
				if ( msg.arg1 == Command.KEY_CMD_LEVEL_DEC_F_LONG_1 || 
						msg.arg1 == Command.KEY_CMD_LEVEL_DEC_F_LONG_2 || 
						msg.arg1 == Command.KEY_CMD_LEVEL_DEC_S_LONG_1 || 
						msg.arg1 == Command.KEY_CMD_LEVEL_DEC_S_LONG_2 ) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				int level_o = Integer.parseInt(editText_level.getText().toString());
				if ( editText_level.hasFocus() && 
						level_o < ParamCons.totalLevel ) {
					level_o++;
					editText_level.setText( level_o + "" );
					if ( caluWindow.isShowing() ) {
						caluWindow.setRelatedAndRes(editText_level,
								R.drawable.tv_keybord_level, layout_mode, editText_pwm, -1);
						caluWindow.StartPopupWindow(findViewById(R.id.main), 171, 169);
					}
					editText_pwm.setText( StorageParam.getPwm( mContext, level_o - 1 ) + "");
					SerialUtils.getInstance().setRunLevel(level_o - 1);
					SerialUtils.getInstance().setLPWM(level_o - 1, 
							Integer.parseInt(editText_pwm.getText().toString()));
				}
				if ( editText_pwm.hasFocus() && 
						Integer.parseInt(editText_pwm.getText().toString()) <= ParamCons.maxPwm ) {
					editText_pwm.setText( (Integer.parseInt(editText_pwm.getText().toString())+1) + "" );
					if ( caluWindow.isShowing() ) {
						caluWindow.setRelatedAndRes(editText_pwm,
								R.drawable.tv_keybord_pwm, layout_mode, editText_level);
						caluWindow.StartPopupWindow(findViewById(R.id.main), 171, 169);
					} else {
						StorageParam.setPwm(mContext, level_o - 1, 
										Integer.parseInt(editText_pwm.getText().toString()) );
					}
					SerialUtils.getInstance().setLPWM(level_o - 1, 
							Integer.parseInt(editText_pwm.getText().toString()));
				}
				break;
			case Command.KEY_CMD_LEVEL_DEC_F:
			case Command.KEY_CMD_LEVEL_DEC_S:
			case Command.KEY_CMD_LEVEL_DEC_F_LONG_1:
			case Command.KEY_CMD_LEVEL_DEC_F_LONG_2:
			case Command.KEY_CMD_LEVEL_DEC_S_LONG_1:
			case Command.KEY_CMD_LEVEL_DEC_S_LONG_2:
				mPopupDialog.showToKeyIcon(msg.arg1);
				if ( mPopupDialog.isShowPopUpDialog() ) {
					Support.keyToneOnce();
					break;
				}
				if ( !editText_level.hasFocus() && !editText_pwm.hasFocus() ) {
					break;
				}
				if ( msg.arg1 == Command.KEY_CMD_LEVEL_DEC_F || 
						msg.arg1 == Command.KEY_CMD_LEVEL_DEC_S ) {
					Support.keyToneOnce();
				}
				if ( msg.arg1 == Command.KEY_CMD_LEVEL_DEC_F_LONG_1 || 
						msg.arg1 == Command.KEY_CMD_LEVEL_DEC_F_LONG_2 || 
						msg.arg1 == Command.KEY_CMD_LEVEL_DEC_S_LONG_1 || 
						msg.arg1 == Command.KEY_CMD_LEVEL_DEC_S_LONG_2 ) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				int level_t = Integer.parseInt(editText_level.getText().toString());
				if ( editText_level.hasFocus() && 
						level_t > 1 ) {
					level_t--;
					editText_level.setText( level_t + "");
					if ( caluWindow.isShowing() ) {
						caluWindow.setRelatedAndRes(editText_level,
								R.drawable.tv_keybord_level, layout_mode, editText_pwm, -1);
						caluWindow.StartPopupWindow(findViewById(R.id.main), 171, 169);
					}
					editText_pwm.setText( StorageParam.getPwm( mContext, level_t - 1 ) + "");
					SerialUtils.getInstance().setRunLevel(level_t - 1);
					SerialUtils.getInstance().setLPWM(level_t - 1, 
							Integer.parseInt(editText_pwm.getText().toString()));
				}
				if ( editText_pwm.hasFocus() && 
						Integer.parseInt(editText_pwm.getText().toString()) >= 1 && 
						Integer.parseInt(editText_pwm.getText().toString()) < ParamCons.maxPwm ) {
					editText_pwm.setText( (Integer.parseInt(editText_pwm.getText().toString())-1) + "" );
					if ( caluWindow.isShowing() ) {
						caluWindow.setRelatedAndRes(editText_pwm,
								R.drawable.tv_keybord_pwm, layout_mode, editText_level);
						caluWindow.StartPopupWindow(findViewById(R.id.main), 171, 169);
					} else {
						StorageParam.setPwm(mContext, level_t - 1, 
								Integer.parseInt(editText_pwm.getText().toString()) );
					}
					SerialUtils.getInstance().setLPWM(level_t - 1, 
							Integer.parseInt(editText_pwm.getText().toString()));
				}
				break;
			default:
				Log.d(TAG, "onCmdEvent value " + msg.arg1);
				break;
		}
		return ;
	}


	@Override
	public void MountEventProc(boolean mount) {
		if( mount && btn_factory_update != null ) {
			if ( !Support.isCheckExist(StorageStateManager.getInstance().getUdiskPath() 
					+ apkName) ) {
				return ;
			}
			btn_factory_update.setEnabled(true);
		} else if( !mount && btn_factory_update != null ) {
			btn_factory_update.setEnabled(false);
		}
		if( mount && btn_factory_updatelogo != null ) {
			if ( !Support.isCheckExist(StorageStateManager.getInstance().getUdiskPath() 
					+ logoName) ) {
				return ;
			}
			btn_factory_updatelogo.setEnabled(true);
			Bitmap bmpDefaultPic;
			bmpDefaultPic = BitmapFactory.decodeFile(
					StorageStateManager.getInstance().getUdiskPath() + logoName,null);
			btn_factory_updateicon.setImageBitmap(bmpDefaultPic);
			bmpDefaultPic = null;
			btn_factory_updateicon.setVisibility(View.VISIBLE);
		} else if( !mount && btn_factory_updatelogo != null ) {
			btn_factory_updatelogo.setEnabled(false);
			/*btn_factory_updateicon.setVisibility(View.GONE);*/
		}
	}

}
