package com.run.treadmill.ModeSettingActivity;

import java.util.Locale;

import com.run.treadmill.BaseActivity;
import com.run.treadmill.R;
import com.run.treadmill.db.UserInfoManager;
import com.run.treadmill.runningModeActivity.RunningHRCActivity;
import com.run.treadmill.selfCaculaterPopuWindow.CaculaterPopuWindowOfEditText;
import com.run.treadmill.serialutil.Command;
import com.run.treadmill.serialutil.SerialUtils;
import com.run.treadmill.util.CTConstant;
import com.run.treadmill.util.MyUtils;
import com.run.treadmill.util.SafeKeyTimer;
import com.run.treadmill.util.StorageParam;
import com.run.treadmill.util.Support;
import com.run.treadmill.util.CTConstant.RunMode;
import com.run.treadmill.util.SafeKeyTimer.SafeTimerCallBack;

import android.R.color;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HRCModeSettingActivity extends BaseActivity implements OnClickListener
	, SafeTimerCallBack {
	
	private String TAG = "HRCModeSettingActivity";

	private Context mContext;
	private ImageView btn_home;
	private ImageView btn_ergo;
	private ImageView btn_start;
	private LinearLayout rlayout_img_gender_frame;
	private ImageView img_gender_draw;
	private ImageView btn_male;
	private ImageView btn_female;
	private ImageView img_male_chick;
	private ImageView img_female_chick;
	private CaculaterPopuWindowOfEditText caluWindow = null;
	
	private EditText editText_age;
	private EditText editText_weight;
	private TextView editText_weight_unit;
	private EditText editText_time;
	
	private RelativeLayout page_first;
	private ImageView btn_next;
	private RelativeLayout page_next;
	private ImageView btn_back;

	private RelativeLayout item_hrc60;
	private TextView text_hrc_60;
	private RelativeLayout item_hrc80;
	private TextView text_hrc_80;
	private RelativeLayout item_target_hr;
	private EditText target_hr;


	private int HRC_TYPE_60 = 0;
	private int HRC_TYPE_80 = 1;
	private int HRC_TYPE_TARGET_HR = 2;
	private int hrcType = HRC_TYPE_60;
	private int targetHrcValue = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Locale locale = getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		String country = locale.getCountry();
		Log.d(TAG,"language " + language + " country " + country);
		if( language.endsWith("tr") || language.endsWith("ir") ) {
			setContentView(R.layout.activity_hrc_setting_tr_ir);
		} else {
			setContentView(R.layout.activity_hrc_setting);
		}

		mContext = this;

		page_first = (RelativeLayout) findViewById(R.id.page_first);
		btn_home = (ImageView) findViewById(R.id.btn_home);
		btn_ergo = (ImageView) findViewById(R.id.btn_ergo);
		btn_start = (ImageView) findViewById(R.id.btn_start);
		rlayout_img_gender_frame =  (LinearLayout) findViewById(R.id.rlayout_img_gender_frame);
		img_gender_draw =  (ImageView) findViewById(R.id.img_gender_draw);
		btn_male =  (ImageView) findViewById(R.id.btn_male);
		btn_female =  (ImageView) findViewById(R.id.btn_female);
		btn_next =  (ImageView) findViewById(R.id.btn_next);
		img_male_chick =  (ImageView) findViewById(R.id.img_male_chick);
		img_female_chick =  (ImageView) findViewById(R.id.img_female_chick);


		page_next = (RelativeLayout) findViewById(R.id.page_next);
		target_hr =  (EditText) findViewById(R.id.target_hr);
		btn_back =  (ImageView) findViewById(R.id.btn_back);
		
		caluWindow = new CaculaterPopuWindowOfEditText(this);
		btn_male.setOnClickListener(this);
		btn_female.setOnClickListener(this);
		btn_home.setOnClickListener(this);
		btn_start.setOnClickListener(this);
		btn_next.setOnClickListener(this);
		btn_back.setOnClickListener(this);

		img_male_chick.setOnClickListener(this);
		img_female_chick.setOnClickListener(this);
		
		editText_age =  (EditText) findViewById(R.id.editText_age);
		editText_weight =  (EditText) findViewById(R.id.editText_weight);
		editText_weight_unit = (TextView) findViewById(R.id.editText_weight_unit);
		editText_time =  (EditText) findViewById(R.id.editText_time);
		editText_age.setCursorVisible(false);
		editText_weight.setCursorVisible(false);
		editText_time.setCursorVisible(false);
		target_hr.setCursorVisible(false);

		editText_age.setSelection(editText_age.getText().length());
		editText_age.setOnTouchListener( new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, android.view.MotionEvent arg1) {
				if ( arg1.getAction() == MotionEvent.ACTION_UP ) {
					Support.buzzerRingOnce();
				}
					rlayout_img_gender_frame.setVisibility(View.GONE);
					editText_age.requestFocus();
					editText_age.setTextColor(mContext.getResources()
							.getColor(R.color.blue_light));
					editText_weight.setTextColor(mContext.getResources().
							getColor(color.white));
					editText_time.setTextColor(mContext.getResources().
							getColor(color.white));
					caluWindow.setRelatedAndRes(editText_age,
							R.drawable.tv_keybord_age, rlayout_img_gender_frame);
					caluWindow.StartPopupWindow(findViewById(R.id.main), 171, 169);
					return true;
	    		};
			}
		);
		
		editText_weight.setSelection(editText_weight.getText().length());
		editText_weight.setOnTouchListener( new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, android.view.MotionEvent arg1) {
				if ( arg1.getAction() == MotionEvent.ACTION_UP ) {
					Support.buzzerRingOnce();
				}
					rlayout_img_gender_frame.setVisibility(View.GONE);
					editText_weight.requestFocus();
					editText_age.setTextColor(mContext.getResources()
							.getColor(color.white));
					editText_weight.setTextColor(mContext.getResources()
							.getColor(R.color.blue_light));
					editText_time.setTextColor(mContext.getResources().
							getColor(color.white));
					caluWindow.setRelatedAndRes(editText_weight,
							R.drawable.tv_keybord_weight, rlayout_img_gender_frame);
					caluWindow.StartPopupWindow(findViewById(R.id.main), 171, 169);
					return true;
	    		};
			}
		);
		
		editText_time.setSelection(editText_time.getText().length());
		editText_time.setOnTouchListener( new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, android.view.MotionEvent arg1) {
				if ( arg1.getAction() == MotionEvent.ACTION_UP ) {
					Support.buzzerRingOnce();
				}
					rlayout_img_gender_frame.setVisibility(View.GONE);
					editText_time.requestFocus();
					editText_age.setTextColor(mContext.getResources()
							.getColor(color.white));
					editText_weight.setTextColor(mContext.getResources()
							.getColor(color.white));
					editText_time.setTextColor(mContext.getResources()
							.getColor(R.color.blue_light));
					caluWindow.setRelatedAndRes(editText_time,
							R.drawable.tv_keybord_time, rlayout_img_gender_frame);
					caluWindow.StartPopupWindow(findViewById(R.id.main), 171, 169);
					return true;
	    		};
			}
		);

		target_hr.setSelection(target_hr.getText().length());
		target_hr.setOnTouchListener( new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, android.view.MotionEvent arg1) {
				if ( arg1.getAction() == MotionEvent.ACTION_UP ) {
					Support.buzzerRingOnce();
				}
					UserInfoManager.getInstance().setTargetHrcValue(
							Integer.parseInt(target_hr.getText().toString()));
					rlayout_img_gender_frame.setVisibility(View.GONE);
					target_hr.requestFocus();
					editText_age.setTextColor(mContext.getResources()
							.getColor(color.white));
					editText_weight.setTextColor(mContext.getResources().
							getColor(color.white));
					editText_time.setTextColor(mContext.getResources().
							getColor(color.white));
					target_hr.setTextColor(mContext.getResources()
							.getColor(R.color.blue_light));
					caluWindow.setRelatedAndRes(target_hr,
							R.drawable.tv_keybord_targethr, rlayout_img_gender_frame);
					caluWindow.StartPopupWindow(findViewById(R.id.main), 171, 169);
					return true;
	    		};
			}
		);

		editText_age.setText(UserInfoManager.getInstance().getAge(UserInfoManager.getInstance().getRunMode()) + "");
		if( StorageParam.getIsMetric(mContext) ) {
			editText_weight.setText(UserInfoManager.getInstance().getmWeigth(UserInfoManager.getInstance().getRunMode()));
		} else {
			editText_weight_unit.setText(R.string.string_weight_lb);
			String weight = UserInfoManager.getInstance().getmWeigth(UserInfoManager.getInstance().getRunMode());
			editText_weight.setText(MyUtils.getKgToLb(Integer.parseInt(weight)) + "");
		}
		editText_time.setText((int)UserInfoManager.getInstance().getTargetTime(UserInfoManager.getInstance().getRunMode()) + "");

		item_hrc60 = (RelativeLayout) findViewById(R.id.item_hrc60);
		text_hrc_60 = (TextView) findViewById(R.id.text_hrc_60);

		item_hrc80 = (RelativeLayout) findViewById(R.id.item_hrc80);
		text_hrc_80 = (TextView) findViewById(R.id.text_hrc_80);

		int hrc_60 = (int) ( ( 220 - Integer.parseInt(editText_age.getText().toString()) ) * 0.6 );
		int hrc_80 = (int) ( ( 220 - Integer.parseInt(editText_age.getText().toString()) ) * 0.8 );
		text_hrc_60.setText( hrc_60 + "" );
		text_hrc_80.setText( hrc_80 + "" );

		item_target_hr = (RelativeLayout) findViewById(R.id.item_target_hr);

		item_hrc60.setOnClickListener(this);
		item_hrc80.setOnClickListener(this);
		item_target_hr.setOnClickListener(this);

		updateViewtextColor(item_hrc60, Color.BLUE);
		targetHrcValue = 80;
		UserInfoManager.getInstance().setTargetHrcValue(hrc_60);
		target_hr.setText( targetHrcValue + "" );
		Support.hideBottomUIMenu(this);
		Support.setLogoIcon(this, btn_ergo);

//		if ( !SafeKeyTimer.getInstance().getIsSafe() ) {
//			btn_start.setEnabled(false);
//		}
	}

	@Override
	public void setSafeState() {
		runOnUiThread(new Runnable() {
			public void run() {				
				btn_start.setEnabled(true);	
            }
        });
		Log.d(TAG," setSafeState callback");
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if( arg0.getId() != R.id.btn_start ) {
			Support.buzzerRingOnce();
		}
		switch (arg0.getId()) {
			case R.id.btn_next:
				page_first.setVisibility(View.GONE);
				page_next.setVisibility(View.VISIBLE);
				btn_next.setVisibility(View.GONE);
				btn_back.setVisibility(View.VISIBLE);
				target_hr.setSelection(target_hr.getText().length());
				int hrc_60 = (int) ( ( 220 - Integer.parseInt(editText_age.getText().toString()) ) * 0.6 );
				int hrc_80 = (int) ( ( 220 - Integer.parseInt(editText_age.getText().toString()) ) * 0.8 );
				/*int hrc = (int) ( ( 220 - Integer.parseInt(editText_age.getText().toString()) ) * 0.8 );*/
				text_hrc_60.setText( hrc_60 + "" );
				text_hrc_80.setText( hrc_80 + "" );
				target_hr.setText( Integer.parseInt(target_hr.getText().toString()) + "" );

				break;
			case R.id.btn_back:
				page_first.setVisibility(View.VISIBLE);
				page_next.setVisibility(View.GONE);
				btn_next.setVisibility(View.VISIBLE);
				btn_back.setVisibility(View.GONE);
				editText_age.setSelection(editText_age.getText().length());
				break;
			case R.id.btn_home:
				finish();
				break;
			case R.id.btn_start:

				caluWindow.StopPopupWindow();
				findViewById(R.id.main).setVisibility(View.GONE);
				UserInfoManager.getInstance().setRunMode(RunMode.IDX_HOME_HRC_MODE.ordinal());
				UserInfoManager.getInstance().setAge(Integer.parseInt(editText_age.getText().toString()),
						UserInfoManager.getInstance().getRunMode());
				if( StorageParam.getIsMetric(mContext) ) {
					UserInfoManager.getInstance().setmWeigth(editText_weight.getText().toString(),
							UserInfoManager.getInstance().getRunMode());
				} else {
					String weight = editText_weight.getText().toString();
					UserInfoManager.getInstance().setmWeigth(MyUtils.getLbToKg(Integer.parseInt(weight))+"",
							UserInfoManager.getInstance().getRunMode());
				}
				UserInfoManager.getInstance().setTargetTime(Integer.parseInt(editText_time.getText().toString()),
						UserInfoManager.getInstance().getRunMode());
//				UserInfoManager.getInstance().setTargetHrcValue(targetHrcValue);
				Intent intent = new Intent(mContext, RunningHRCActivity.class);
				MyUtils.leftAnimStartActivityForResult(this, intent, false);
				break;
			case  R.id.item_hrc60:
				updateViewtextColor(item_hrc60, Color.BLUE);
				updateViewtextColor(item_hrc80, Color.WHITE);
				updateViewtextColor(item_target_hr, Color.WHITE);
				targetHrcValue = Integer.parseInt(text_hrc_60.getText().toString());
				UserInfoManager.getInstance().setTargetHrcValue(targetHrcValue);
				break;
			case  R.id.item_hrc80:
				updateViewtextColor(item_hrc60, Color.WHITE);
				updateViewtextColor(item_hrc80, Color.BLUE);
				updateViewtextColor(item_target_hr, Color.WHITE);
				targetHrcValue = Integer.parseInt(text_hrc_80.getText().toString());
				UserInfoManager.getInstance().setTargetHrcValue(targetHrcValue);
				break;
			case  R.id.item_target_hr:
				updateViewtextColor(item_hrc60, Color.WHITE);
				updateViewtextColor(item_hrc80, Color.WHITE);
				updateViewtextColor(item_target_hr, Color.BLUE);
				targetHrcValue = Integer.parseInt(target_hr.getText().toString());
				UserInfoManager.getInstance().setTargetHrcValue(targetHrcValue);
				break;
			case R.id.btn_male:
				btn_female.setImageResource(R.drawable.btn_female_1);
				img_gender_draw.setImageResource(R.drawable.img_gender_draw_1);
				rlayout_img_gender_frame.setBackgroundResource(R.drawable.img_gender_frame_2);
				btn_male.setImageResource(R.drawable.btn_male_2);
				break;
			case R.id.btn_female:
				btn_male.setImageResource(R.drawable.btn_male_1);
				img_gender_draw.setImageResource(R.drawable.img_gender_draw_2);
				rlayout_img_gender_frame.setBackgroundResource(R.drawable.img_gender_frame_2);
				btn_female.setImageResource(R.drawable.btn_female_2);
				break;
			case R.id.img_male_chick:
				btn_female.setImageResource(R.drawable.btn_female_1);
				img_gender_draw.setImageResource(R.drawable.img_gender_draw_1);
				rlayout_img_gender_frame.setBackgroundResource(R.drawable.img_gender_frame_2);
				btn_male.setImageResource(R.drawable.btn_male_2);
				break;
			case R.id.img_female_chick:
				btn_male.setImageResource(R.drawable.btn_male_1);
				img_gender_draw.setImageResource(R.drawable.img_gender_draw_2);
				rlayout_img_gender_frame.setBackgroundResource(R.drawable.img_gender_frame_2);
				btn_female.setImageResource(R.drawable.btn_female_2);
				break;
			default:
				break;
		}
	}

	public Handler mMsgHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_PROC_ENBLE_CONTINUE:
					if ( !SafeKeyTimer.getInstance().getIsSafe() ) {
						break;
					}
					int[] values2 = (int[]) msg.obj;
					if( values2[0] == 0 ) {
						btn_start.setEnabled(true);
					} else {
						btn_start.setEnabled(false);
					}
					break;
			}
		}
	};
	
	@Override
	public void errorEventProc(int errorValue) {
		if( errorValue == 1 ) {
			releaseQuitProc();
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
	public void onCmdEvent(Message msg) {
		switch (msg.arg1) {
			case Command.KEY_CMD_QUICK_START:
				if( btn_start.isEnabled() ) {
					Support.keyToneOnce();
					btn_start.performClick();
				}
			break;
		}
		return ;
	}

	private void releaseQuitProc() {
		Intent intent = new Intent();
		StorageParam.setIsRunIng(mContext, false);
		intent.putExtra(CTConstant.IS_FINSH, false);
		MyUtils.animLeftFinishActivity(this, intent);
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

	public void updateViewtextColor(RelativeLayout mRelativeLayout, int mColor) {
		int childCount = mRelativeLayout.getChildCount();
		//遍历下面所有的子控件，判断是否是layout
		for(int i = 0; i < childCount; i++) {
		    if( mRelativeLayout.getChildAt(i) instanceof TextView ) {
		    	((TextView) mRelativeLayout.getChildAt(i)).setTextColor(mColor);//Color.BLUE
			}
		    if( mRelativeLayout.getChildAt(i) instanceof EditText ) {
		    	((EditText) mRelativeLayout.getChildAt(i)).setTextColor(mColor);//Color.BLUE
			}
		}
	}

}
