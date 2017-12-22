package com.run.treadmill.ModeSettingActivity;


import com.run.treadmill.BaseActivity;
import com.run.treadmill.R;
import com.run.treadmill.db.UserInfoManager;
import com.run.treadmill.runningModeActivity.RunningUserActivity;
import com.run.treadmill.runningParamManager.RunParamTableManager;
import com.run.treadmill.selfCaculaterPopuWindow.CaculaterPopuWindowOfEditText;
import com.run.treadmill.selfdefView.ParamView;
import com.run.treadmill.serialutil.Command;
import com.run.treadmill.serialutil.SerialUtils;
import com.run.treadmill.systemInitParam.InitParam;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserProgramModeSettingActivity extends BaseActivity implements OnClickListener, OnGestureListener
	, SafeTimerCallBack {
	
	private String TAG = "UserProgramModeSettingActivity";

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
	
	private TextView text_page_first_hint;
	private TextView text_page_next_hint;
	
	private RelativeLayout page_first;
	private ImageView btn_next;
	private RelativeLayout page_next;
	private ImageView btn_back;

	private ParamView chart_view_histogram;

	private GestureDetector mGestureDetector;
	private int mLevelItemValueArray[] = { 1,1,1,1,1,  1,1,1,1,1,  1,1,1,1,1, 
			1,1,1,1,1,  1,1,1,1,1, 1,1,1,1,1 };

	private int scrop_left = 245;
	private int scrop_right = 1063;
	private int scrop_top = 255;
	private int scrop_bottom = 507;

	private int initLeft = 5;
	private int itemWeight = 25;
	private int itemUnitSpeed = 7;
	private int errorStep = 2;

	private int count = 30;

	private float prePointX = 0;private float prePointY = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_program_setting);

		mContext = this;
		page_first = (RelativeLayout) findViewById(R.id.page_first);
		btn_home = (ImageView) findViewById(R.id.btn_home);
		btn_ergo = (ImageView) findViewById(R.id.btn_ergo);
		btn_start = (ImageView) findViewById(R.id.btn_start);
		rlayout_img_gender_frame = (LinearLayout) findViewById(R.id.rlayout_img_gender_frame);
		img_gender_draw = (ImageView) findViewById(R.id.img_gender_draw);
		btn_male = (ImageView) findViewById(R.id.btn_male);
		btn_female = (ImageView) findViewById(R.id.btn_female);
		img_male_chick =  (ImageView) findViewById(R.id.img_male_chick);
		img_female_chick =  (ImageView) findViewById(R.id.img_female_chick);
		btn_next = (ImageView) findViewById(R.id.btn_next);


		page_next = (RelativeLayout) findViewById(R.id.page_next);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		text_page_first_hint = (TextView) findViewById(R.id.text_page_first_hint);
		text_page_next_hint = (TextView) findViewById(R.id.text_page_next_hint);

		chart_view_histogram = (ParamView) findViewById(R.id.chart_view_histogram);
		
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
		
		editText_age.requestFocus();
		editText_age.setSelection(editText_age.getText().length());
		editText_age.setOnTouchListener( new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, android.view.MotionEvent arg1) {
				if ( arg1.getAction() == MotionEvent.ACTION_UP ) {
					Support.buzzerRingOnce();
				}
					rlayout_img_gender_frame.setVisibility(View.GONE);
					editText_age.requestFocus();
					editText_age.setSelection(editText_age.getText().length());
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
					editText_weight.setSelection(editText_weight.getText().length());
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
					editText_time.setSelection(editText_time.getText().length());
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

		/*mSpeedItemValueArray = RunParamTableManager.getInstance(this).
				proSpdAndIncTable[2 * UserInfoManager.getInstance().getRunMode()];*/
		chart_view_histogram.setItemValueArray(mLevelItemValueArray);
		chart_view_histogram.setShowItemNum(count);
		chart_view_histogram.setTriangularIconVisibility(false);
		chart_view_histogram.postInvalidate();

		mGestureDetector = new GestureDetector(this);
		editText_age.setText(UserInfoManager.getInstance().getAge(UserInfoManager.getInstance().getRunMode()) + "");
		if( StorageParam.getIsMetric(mContext) ) {
			editText_weight.setText(UserInfoManager.getInstance().getmWeigth(UserInfoManager.getInstance().getRunMode()));
		} else {
			editText_weight_unit.setText(R.string.string_weight_lb);
			String weight = UserInfoManager.getInstance().getmWeigth(UserInfoManager.getInstance().getRunMode());
			editText_weight.setText(MyUtils.getKgToLb(Integer.parseInt(weight)) + "");
		}
		Log.d(TAG,"editText_time ==== "+ UserInfoManager.getInstance().getTotalTime());
		editText_time.setText((int)UserInfoManager.getInstance().getTargetTime(UserInfoManager.getInstance().getRunMode()) + "");

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
				btn_start.setVisibility(View.GONE);
				page_first.setVisibility(View.GONE);
				page_next.setVisibility(View.VISIBLE);
				btn_start.setVisibility(View.VISIBLE);
				btn_next.setVisibility(View.GONE);
				btn_back.setVisibility(View.VISIBLE);
				text_page_first_hint.setVisibility(View.GONE);
				text_page_next_hint.setVisibility(View.VISIBLE);
				rlayout_img_gender_frame.setVisibility(View.GONE);
				
				caluWindow.StopPopupWindow();
				break;
			case R.id.btn_back:
				btn_start.setVisibility(View.GONE);
				page_first.setVisibility(View.VISIBLE);
				page_next.setVisibility(View.GONE);
				btn_start.setVisibility(View.VISIBLE);
				btn_next.setVisibility(View.VISIBLE);
				btn_back.setVisibility(View.GONE);
				text_page_first_hint.setVisibility(View.VISIBLE);
				text_page_next_hint.setVisibility(View.GONE);
				rlayout_img_gender_frame.setVisibility(View.VISIBLE);
				editText_age.setSelection(editText_age.getText().length());
				break;
			case R.id.btn_home:
				finish();
				break;
			case R.id.btn_start:

				caluWindow.StopPopupWindow();
				findViewById(R.id.main).setVisibility(View.GONE);
				System.arraycopy(mLevelItemValueArray, 0, RunParamTableManager.getInstance(this).
						proLevelTable[UserInfoManager.getInstance().getRunMode()] ,
						0, InitParam.TOTAL_RUN_INTERVAL_NUM);
				Log.d(TAG,"==== "+ RunParamTableManager.getInstance(this).
						proLevelTable[UserInfoManager.getInstance().getRunMode()][0]);
				UserInfoManager.getInstance().setRunMode(RunMode.IDX_HOME_USERPROGRAM_MODE.ordinal());
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
				Log.d(TAG,"editText_time ==== "+ editText_time.getText().toString());
				UserInfoManager.getInstance().setTargetTime(Integer.parseInt(editText_time.getText().toString()),
						UserInfoManager.getInstance().getRunMode());
				Intent intent = new Intent(mContext, RunningUserActivity.class);
				MyUtils.leftAnimStartActivityForResult(this, intent, false);
				findViewById(R.id.main).setVisibility(View.GONE);
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

	@Override 
	public boolean dispatchTouchEvent(MotionEvent event) { 
		onTouchEvent(event);   
		return super.dispatchTouchEvent(event);     
	}
	
	@Override  
	public boolean onTouchEvent(MotionEvent event) {  
        mGestureDetector.onTouchEvent(event);  
        return true;
    }

	@Override
	public boolean onDown(MotionEvent arg0) {
		if( page_next.getVisibility() == View.GONE ) {
			return false;
		}
		if( prePointX == arg0.getX() && prePointY == arg0.getY()) {
			return false;
		}
		prePointX = arg0.getX(); prePointY = arg0.getY();

		if( arg0.getX() > scrop_left && arg0.getX() < scrop_right && 
				arg0.getY() > scrop_top && arg0.getY() < scrop_bottom ) {
			for(int i = 0; i < count; i++) {
//				Log.d(TAG, " onDown " +  "arg0.getX(): " + arg0.getX() + " arg0.getY(): " + arg0.getY());
				if( ( arg0.getX() - scrop_left - initLeft - i*(itemWeight+errorStep) ) >= 0 
						&& ( arg0.getX() - scrop_left - initLeft - i*(itemWeight+errorStep) ) <= itemWeight ) {

					Log.d(TAG ," ==========SINGLE=" + ( scrop_bottom - arg0.getY() ) / itemUnitSpeed);
					if( ( scrop_bottom - arg0.getY() ) / itemUnitSpeed > 0.8 ) {
						mLevelItemValueArray[i] = MyUtils.formatFloatToInt( ( scrop_bottom - arg0.getY() ) / itemUnitSpeed );
						if ( InitParam.MaxLevel - StorageParam.getMaxLevel(mContext) > 0 
								&& ( scrop_bottom - arg0.getY() ) > ( StorageParam.getMaxLevel(mContext) *itemUnitSpeed ) ) {
							mLevelItemValueArray[i] =  StorageParam.getMaxLevel(mContext);
						}
						chart_view_histogram.setLevel(0);
						chart_view_histogram.setItemValueArray(mLevelItemValueArray);
						chart_view_histogram.setShowItemNum(count);
						chart_view_histogram.postInvalidate();
						Log.d(TAG, " onDown mLevelItemValueArray[i] " +  mLevelItemValueArray[i] +
								" i: " + i);
					}

					break;
				}
			}
		}
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		if( page_next.getVisibility() == View.GONE ) {
			return false;
		}
		/*Log.d(TAG, " onScroll velocityX: "+arg2 + " velocityY: "+ arg3 + 
				" arg0.getX(): " + arg0.getX() + " arg0.getY(): " + arg0.getY() + 
				" arg1.getX(): " + arg1.getX() + " arg1.getY(): " + arg1.getY() );*/
		if( arg1.getX() > scrop_left && arg1.getX() < scrop_right && 
				arg1.getY() >= scrop_top && arg1.getY() <= scrop_bottom ) {
			for(int i = 0; i < count; i++) {
				if( ( arg1.getX() - scrop_left - initLeft - i*(itemWeight+errorStep) ) >= 0 
						&& ( arg1.getX() - scrop_left - initLeft - i*(itemWeight+errorStep) ) <= itemWeight ) {

					Log.d(TAG ," ==========DOUBLE=" + ( scrop_bottom - arg1.getY() ) / itemUnitSpeed);
					if( ( scrop_bottom - arg1.getY() ) / itemUnitSpeed > 0.8 ) {
						mLevelItemValueArray[i] = MyUtils.formatFloatToInt( ( scrop_bottom - arg1.getY() ) / itemUnitSpeed );
						if ( InitParam.MaxLevel - StorageParam.getMaxLevel(mContext) > 0 
								&& ( scrop_bottom - arg1.getY() ) > ( StorageParam.getMaxLevel(mContext) *itemUnitSpeed ) ) {
							mLevelItemValueArray[i] =  StorageParam.getMaxLevel(mContext);
						}
						chart_view_histogram.setLevel(0);
						chart_view_histogram.setItemValueArray(mLevelItemValueArray);
						chart_view_histogram.setShowItemNum(count);
						chart_view_histogram.postInvalidate();
						Log.d(TAG, " onScroll mLevelItemValueArray[i] " +  mLevelItemValueArray[i] +
								" i: " + i);
					}
					break;
				}
			}
		}
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
//		if( ( e1.getY() - e2.getY() ) > 200  && ( e1.getX() > 960 ) ) {
//			
//		} else if( (e1.getY() - e2.getY()) > 3  && (e1.getX() - e2.getX()) > 50 ){
//
//		}
		return false;
	}


	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
