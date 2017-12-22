package com.run.treadmill.ModeSettingActivity;

import com.run.treadmill.BaseActivity;
import com.run.treadmill.R;
import com.run.treadmill.db.UserInfoManager;
import com.run.treadmill.runningModeActivity.RunningGoalActivity;
import com.run.treadmill.selfCaculaterPopuWindow.CaculaterPopuWindowOfGoal;
import com.run.treadmill.serialutil.Command;
import com.run.treadmill.serialutil.SerialUtils;
import com.run.treadmill.util.CTConstant;
import com.run.treadmill.util.MyUtils;
import com.run.treadmill.util.SafeKeyTimer;
import com.run.treadmill.util.StorageParam;
import com.run.treadmill.util.Support;
import com.run.treadmill.util.CTConstant.RunMode;
import com.run.treadmill.util.SafeKeyTimer.SafeTimerCallBack;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class GoalModeSettingActivity extends BaseActivity implements OnClickListener
	, SafeTimerCallBack {
	
	private String TAG = "GoalModeSettingActivity";

	private Context mContext;
	private ImageView btn_home;
	private ImageView btn_ergo;
	private ImageView btn_start;
	private RelativeLayout param_show_layout;
	private RelativeLayout param_set_layout;
	
	private CaculaterPopuWindowOfGoal caluWindow = null;
	private ImageView btn_goal_time;
	private EditText text_goal_time;
	private ImageView btn_goal_distance_mile;
	private EditText text_goal_mile;
	private ImageView btn_goal_calories;
	private EditText text_goal_calories;
	
	private ImageView set_btn_goal_time;
	private EditText set_text_goal_time;
	private ImageView set_btn_goal_distance_mile;
	private EditText set_text_goal_mile;
	private ImageView set_btn_goal_calories;
	private EditText set_text_goal_calories;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goal_setting);
		
		mContext = this;
		caluWindow = new CaculaterPopuWindowOfGoal(this);

		btn_home = (ImageView) findViewById(R.id.btn_home);
		btn_ergo = (ImageView) findViewById(R.id.btn_ergo);
		btn_start = (ImageView) findViewById(R.id.btn_start);
		btn_goal_time = (ImageView) findViewById(R.id.btn_goal_time);
		text_goal_time = (EditText) findViewById(R.id.text_goal_time);
		btn_goal_distance_mile = (ImageView) findViewById(R.id.btn_goal_distance_mile);
		text_goal_mile = (EditText) findViewById(R.id.text_goal_mile);
		btn_goal_calories = (ImageView) findViewById(R.id.btn_goal_calories);
		text_goal_calories = (EditText) findViewById(R.id.text_goal_calories);
		
		set_btn_goal_time = (ImageView) findViewById(R.id.set_btn_goal_time);
		set_text_goal_time = (EditText) findViewById(R.id.set_text_goal_time);
		set_btn_goal_distance_mile = (ImageView) findViewById(R.id.set_btn_goal_mile);
		set_text_goal_mile = (EditText) findViewById(R.id.set_text_goal_mile);
		set_btn_goal_calories = (ImageView) findViewById(R.id.set_btn_goal_calories);
		set_text_goal_calories = (EditText) findViewById(R.id.set_text_goal_calories);
		param_show_layout = (RelativeLayout) findViewById(R.id.param_show_layout);
		param_set_layout = (RelativeLayout) findViewById(R.id.param_set_layout);


		btn_home.setOnClickListener(this);
		btn_goal_time.setOnClickListener(this);
		btn_goal_distance_mile.setOnClickListener(this);
		btn_goal_calories.setOnClickListener(this);

		//使失去焦点，禁止弹出软键盘
		set_text_goal_time.setEnabled(false);
		set_text_goal_mile.setEnabled(false);
		set_text_goal_calories.setEnabled(false);
		
		UserInfoManager.getInstance().setTargetRunType(CTConstant.RUN_REFERENCE_BY_TIME);
		UserInfoManager.getInstance().setTargetTime(Integer.parseInt(text_goal_time.getText().toString()),
				UserInfoManager.getInstance().getRunMode());
		/*UserInfoManager.getInstance().setTargetTime(0);
		UserInfoManager.getInstance().setTargetCalorie(10);*/
		btn_start.setEnabled(false);

		text_goal_time.setCursorVisible(false);
		text_goal_time.setSelection(text_goal_time.getText().length());
		text_goal_time.setOnTouchListener( new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, android.view.MotionEvent arg1) {
				if ( arg1.getAction() == MotionEvent.ACTION_UP ) {
					Support.buzzerRingOnce();
				}
					text_goal_time.setCursorVisible(false);
					param_show_layout.setVisibility(View.GONE);
					param_set_layout.setVisibility(View.VISIBLE);
					set_btn_goal_time.setVisibility(View.VISIBLE);
					set_text_goal_time.setVisibility(View.VISIBLE);

					if( text_goal_time.getText().toString().contains("---") ) {
						text_goal_time.setText("20");
					}
					set_text_goal_time.setText(text_goal_time.getText().toString());

					text_goal_time.requestFocus();
					caluWindow.setRelatedAndRes(text_goal_time,
							R.drawable.tv_keybord_time, param_show_layout, text_goal_mile, 
							text_goal_calories, set_btn_goal_time, set_text_goal_time);
					caluWindow.StartPopupWindow(findViewById(R.id.main), 741, 169);
					if ( SafeKeyTimer.getInstance().getIsSafe() ) {
						btn_start.setEnabled(true);
					}
					return true;
	    		};
			}
		);
		text_goal_mile.setSelection(text_goal_mile.getText().length());
		if( StorageParam.getIsMetric(mContext) ) {
			set_text_goal_mile.setText("1");
			text_goal_mile.setText("1");
		} else {
			/*set_text_goal_mile.setText(MyUtils.getKmToMileIntType(1) + "");
			text_goal_mile.setText(MyUtils.getKmToMileIntType(1) + "");*/
			set_text_goal_mile.setText(1 + "");
			text_goal_mile.setText(1 + "");
			btn_goal_distance_mile.setImageResource(R.drawable.btn_goal_distance_mile_1);
			set_btn_goal_distance_mile.setImageResource(R.drawable.btn_goal_distance_mile_1);
		}
		text_goal_mile.setOnTouchListener( new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, android.view.MotionEvent arg1) {
				if ( arg1.getAction() == MotionEvent.ACTION_UP ) {
					Support.buzzerRingOnce();
				}
					text_goal_mile.setCursorVisible(false);
					param_show_layout.setVisibility(View.GONE);
					param_set_layout.setVisibility(View.VISIBLE);
					set_btn_goal_distance_mile.setVisibility(View.VISIBLE);
					set_text_goal_mile.setVisibility(View.VISIBLE);
					
					if( text_goal_mile.getText().toString().contains("---") ) {
						if( StorageParam.getIsMetric(mContext) ) {
							set_text_goal_time.setText("1");
							text_goal_mile.setText("1");
						} else {
							/*set_text_goal_time.setText(MyUtils.getKmToMileIntType(1) + "");
							text_goal_mile.setText(MyUtils.getKmToMileIntType(1) + "");*/
							set_text_goal_mile.setText(1 + "");
							text_goal_mile.setText(1 + "");
						}
					}
					set_text_goal_mile.setText(text_goal_mile.getText().toString());
					

					text_goal_mile.requestFocus();
					caluWindow.setRelatedAndRes(text_goal_mile,
							R.drawable.tv_keybord_distance, param_show_layout, text_goal_time, 
							text_goal_calories, set_btn_goal_distance_mile, set_text_goal_mile);
					caluWindow.StartPopupWindow(findViewById(R.id.main), 741, 169);
					if ( SafeKeyTimer.getInstance().getIsSafe() ) {
						btn_start.setEnabled(true);
					}
					return true;
	    		};
			}
		);
		text_goal_calories.setSelection(text_goal_calories.getText().length());
		text_goal_calories.setOnTouchListener( new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, android.view.MotionEvent arg1) {
				if ( arg1.getAction() == MotionEvent.ACTION_UP ) {
					Support.buzzerRingOnce();
				}
					text_goal_calories.setCursorVisible(false);
					param_show_layout.setVisibility(View.GONE);
					param_set_layout.setVisibility(View.VISIBLE);
					set_btn_goal_calories.setVisibility(View.VISIBLE);
					set_text_goal_calories.setVisibility(View.VISIBLE);

					if( text_goal_calories.getText().toString().contains("---") ) {
						text_goal_calories.setText("200");
					}
					set_text_goal_calories.setText(text_goal_calories.getText().toString());

					text_goal_calories.requestFocus();
					caluWindow.setRelatedAndRes(text_goal_calories,
							R.drawable.tv_keybord_calories, param_show_layout, text_goal_mile, 
							text_goal_time, set_btn_goal_calories, set_text_goal_calories);
					caluWindow.StartPopupWindow(findViewById(R.id.main), 741, 169);
					if ( SafeKeyTimer.getInstance().getIsSafe() ) {
						btn_start.setEnabled(true);
					}
					return true;
	    		};
			}
		);
		
		btn_start.setOnClickListener(this);
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
		switch (arg0.getId()) {
			case R.id.btn_home:
				Support.buzzerRingOnce();
				finish();
				break;
			case R.id.btn_start:

				caluWindow.StopPopupWindow();
				UserInfoManager.getInstance().setRunMode(RunMode.IDX_HOME_GOAL_MODE.ordinal());
//				UserInfoManager.getInstance().setTargetTime(2);//设置时间为分钟
				Intent intent = new Intent(mContext, RunningGoalActivity.class);
				MyUtils.leftAnimStartActivityForResult(this, intent, false);
				findViewById(R.id.main).setVisibility(View.GONE);
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
						if( text_goal_calories.getText().toString().contains("---") 
								|| text_goal_time.getText().toString().contains("---") || 
								text_goal_mile.getText().toString().contains("---") ) {
							btn_start.setEnabled(true);
						}
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
				if( btn_start.isEnabled() && ( set_btn_goal_time.getVisibility() == View.GONE 
					&& set_btn_goal_distance_mile.getVisibility() == View.GONE 
					&& set_btn_goal_calories.getVisibility() == View.GONE ) ) {
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

}
