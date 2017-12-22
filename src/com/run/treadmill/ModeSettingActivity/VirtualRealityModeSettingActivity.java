package com.run.treadmill.ModeSettingActivity;

import java.util.ArrayList;

import com.run.treadmill.BaseActivity;
import com.run.treadmill.R;
import com.run.treadmill.adapter.MyPagerAdapter;
import com.run.treadmill.db.UserInfoManager;
import com.run.treadmill.runningModeActivity.RunningVRActivity;
import com.run.treadmill.selfCaculaterPopuWindow.CaculaterPopuWindowofTextView;
import com.run.treadmill.serialutil.Command;
import com.run.treadmill.serialutil.SerialUtils;
import com.run.treadmill.util.CTConstant;
import com.run.treadmill.util.CTConstant.VrVideoItem;
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
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VirtualRealityModeSettingActivity extends BaseActivity implements OnPageChangeListener, OnClickListener
	, SafeTimerCallBack {

	private String TAG = "VirtualRealityModeSettingActivity";

	private Context mContext;
	private ViewPager viewPager;
	private ImageView pager_prompt_dot;
	private LayoutInflater mLayoutInflater;
	private MyPagerAdapter mPagerAdapter;
	private ArrayList<View> mViewList = new ArrayList<View>();
	private ImageView btn_home;
	private ImageView btn_ergo;
	private ImageView btn_back;
	private ImageView btn_start;

	private ImageView img_program_virtual_01;
	private ImageView img_program_virtual_02;
	private ImageView img_program_virtual_03;
	private ImageView img_program_virtual_04;
	private ImageView img_program_virtual_05;
	private ImageView img_program_virtual_06;
	private ImageView img_program_virtual_07;
	private ImageView img_program_virtual_08;
	private ImageView img_program_virtual_09;
	private ImageView img_program_virtual_10;
	private RelativeLayout page_next;
	
	private ImageView btn_virtual_time;
	private TextView text_virtual_time;
	private CaculaterPopuWindowofTextView caluWindow = null;
	private ImageView img_program_pic;

	private ImageView runProgramView[] = new ImageView[VrVideoItem.ITEM_VR_AUSTRIA_HALLSTATT.ordinal() + 1];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_virtual_reality_setting);

		mContext = this;
		mLayoutInflater = getLayoutInflater();
		caluWindow = new CaculaterPopuWindowofTextView(this);
		initSubView();
		text_virtual_time.setText((int)UserInfoManager.getInstance().getTargetTime(UserInfoManager.getInstance().getRunMode()) + "");
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
	
	private void initSubView() {

		btn_ergo = (ImageView) findViewById(R.id.btn_ergo);
		View viewOne;
		viewOne = mLayoutInflater.inflate(R.layout.home_vr_pager_one, null);
		View viewTwo;
		viewTwo = mLayoutInflater.inflate(R.layout.home_vr_pager_two, null);
		View viewThree;
		viewThree = mLayoutInflater.inflate(R.layout.home_vr_pager_three, null);
		
		viewPager = (ViewPager) findViewById(R.id.virtual_reality_pager);
		pager_prompt_dot = (ImageView) findViewById(R.id.pager_prompt_dot);
		btn_back = (ImageView) findViewById(R.id.btn_back);
		btn_start = (ImageView) findViewById(R.id.btn_start);
		mViewList.add(viewOne); 
		mViewList.add(viewTwo);
		mViewList.add(viewThree);

		img_program_virtual_01 = (ImageView) viewOne.findViewById(R.id.img_program_virtual_01);
		img_program_virtual_02 = (ImageView) viewOne.findViewById(R.id.img_program_virtual_02);
		img_program_virtual_03 = (ImageView) viewOne.findViewById(R.id.img_program_virtual_03);
		img_program_virtual_04 = (ImageView) viewOne.findViewById(R.id.img_program_virtual_04);

		img_program_virtual_05 = (ImageView) viewTwo.findViewById(R.id.img_program_virtual_05);
		img_program_virtual_06 = (ImageView) viewTwo.findViewById(R.id.img_program_virtual_06);
		img_program_virtual_07 = (ImageView) viewTwo.findViewById(R.id.img_program_virtual_07);
		img_program_virtual_08 = (ImageView) viewTwo.findViewById(R.id.img_program_virtual_08);
		
		img_program_virtual_09 = (ImageView) viewThree.findViewById(R.id.img_program_virtual_09);
		img_program_virtual_10 = (ImageView) viewThree.findViewById(R.id.img_program_virtual_10);
		page_next = (RelativeLayout) findViewById(R.id.page_next);
		btn_virtual_time = (ImageView) findViewById(R.id.btn_virtual_time);
		text_virtual_time = (TextView) findViewById(R.id.text_virtual_time);
		img_program_pic =  (ImageView) findViewById(R.id.img_program_pic);
		runProgramView[0] = img_program_virtual_01;
		runProgramView[1] = img_program_virtual_02;
		runProgramView[2] = img_program_virtual_03;
		runProgramView[3] = img_program_virtual_04;
		runProgramView[4] = img_program_virtual_05;
		runProgramView[5] = img_program_virtual_06;
		runProgramView[6] = img_program_virtual_07;
		runProgramView[7] = img_program_virtual_08;
		runProgramView[8] = img_program_virtual_09;
		runProgramView[9] = img_program_virtual_10;
		
		mPagerAdapter = new MyPagerAdapter(mViewList);  
		viewPager.setAdapter(mPagerAdapter); 
		viewPager.setOnPageChangeListener(this);
		
		btn_home = (ImageView) findViewById(R.id.btn_home);
		btn_home.setOnClickListener(this);
		img_program_virtual_01.setOnClickListener(this);
		img_program_virtual_02.setOnClickListener(this);
		img_program_virtual_03.setOnClickListener(this);
		img_program_virtual_04.setOnClickListener(this);
		img_program_virtual_05.setOnClickListener(this);
		img_program_virtual_06.setOnClickListener(this);
		img_program_virtual_07.setOnClickListener(this);
		img_program_virtual_08.setOnClickListener(this);
		img_program_virtual_09.setOnClickListener(this);
		img_program_virtual_10.setOnClickListener(this);
		btn_virtual_time.setOnClickListener(this);
		text_virtual_time.setOnClickListener(this);

		btn_back.setOnClickListener(this);
		btn_start.setOnClickListener(this);
	}
	@Override  
    public void onPageSelected(int position) {  
        if( position == 0 ) {
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

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if( arg0.getId() != R.id.btn_start ) {
			Support.buzzerRingOnce();
		}
		switch (arg0.getId()) {
			case R.id.text_virtual_time:
				caluWindow.setRelatedAndRes(text_virtual_time,
						R.drawable.tv_keybord_time, img_program_pic);
				caluWindow.StartPopupWindow(findViewById(R.id.main), 171, 169);
				break;
			case R.id.btn_home:
				finish();
				break;
			case R.id.btn_back:
				caluWindow.StopPopupWindow();
				viewPager.setVisibility(View.VISIBLE);
				pager_prompt_dot.setVisibility(View.VISIBLE);
				page_next.setVisibility(View.GONE);
				break;
			case R.id.btn_start:

				caluWindow.StopPopupWindow();
				findViewById(R.id.main).setVisibility(View.GONE);
				UserInfoManager.getInstance().setRunMode(RunMode.IDX_HOME_VIRTUAL_MODE.ordinal());
				UserInfoManager.getInstance().setAge(30, UserInfoManager.getInstance().getRunMode());
				UserInfoManager.getInstance().setmWeigth("70", UserInfoManager.getInstance().getRunMode());
				UserInfoManager.getInstance().setTargetTime(Integer.parseInt(text_virtual_time.getText().toString()),
						UserInfoManager.getInstance().getRunMode());
//				UserInfoManager.getInstance().setTargetTime(2);//设置时间为分钟
				Intent intent = new Intent(mContext, RunningVRActivity.class);
				MyUtils.leftAnimStartActivityForResult(this, intent, false);
				break;
			case R.id.img_program_virtual_01:
				resetProgramViewSelect();
				arg0.setSelected(true);
				img_program_pic.setImageResource(R.drawable.img_program_virtual_01_4);
				viewPager.setVisibility(View.GONE);
				page_next.setVisibility(View.VISIBLE);
				pager_prompt_dot.setVisibility(View.GONE);
				UserInfoManager.getInstance().vrSceneVideoNo = VrVideoItem.ITEM_VR_FRANCE_PARIS.ordinal();
				break;
			case R.id.img_program_virtual_02:
				resetProgramViewSelect();
				arg0.setSelected(true);
				img_program_pic.setImageResource(R.drawable.img_program_virtual_02_4);
				viewPager.setVisibility(View.GONE);
				page_next.setVisibility(View.VISIBLE);
				pager_prompt_dot.setVisibility(View.GONE);
				UserInfoManager.getInstance().vrSceneVideoNo = VrVideoItem.ITEM_VR_CZECH.ordinal();
				break;
			case R.id.img_program_virtual_03:
				resetProgramViewSelect();
				arg0.setSelected(true);
				img_program_pic.setImageResource(R.drawable.img_program_virtual_03_4);
				viewPager.setVisibility(View.GONE);
				page_next.setVisibility(View.VISIBLE);
				pager_prompt_dot.setVisibility(View.GONE);
				UserInfoManager.getInstance().vrSceneVideoNo = VrVideoItem.ITEM_VR_GERMANY_DRESDEN.ordinal();
				break;
			case R.id.img_program_virtual_04:
				resetProgramViewSelect();
				arg0.setSelected(true);
				img_program_pic.setImageResource(R.drawable.img_program_virtual_04_4);
				viewPager.setVisibility(View.GONE);
				page_next.setVisibility(View.VISIBLE);
				pager_prompt_dot.setVisibility(View.GONE);
				UserInfoManager.getInstance().vrSceneVideoNo = VrVideoItem.ITEM_VR_GERMANY_BALT.ordinal();
				break;
			case R.id.img_program_virtual_05:
				resetProgramViewSelect();
				arg0.setSelected(true);
				img_program_pic.setImageResource(R.drawable.img_program_virtual_05_4);
				viewPager.setVisibility(View.GONE);
				page_next.setVisibility(View.VISIBLE);
				pager_prompt_dot.setVisibility(View.GONE);
				UserInfoManager.getInstance().vrSceneVideoNo = VrVideoItem.ITEM_VR_ITALY_ROME.ordinal();
				break;
			case R.id.img_program_virtual_06:
				resetProgramViewSelect();
				arg0.setSelected(true);
				img_program_pic.setImageResource(R.drawable.img_program_virtual_06_4);
				viewPager.setVisibility(View.GONE);
				page_next.setVisibility(View.VISIBLE);
				pager_prompt_dot.setVisibility(View.GONE);
				UserInfoManager.getInstance().vrSceneVideoNo = VrVideoItem.ITEM_VR_ITALY_PASSO.ordinal();
				break;
			case R.id.img_program_virtual_07:
				resetProgramViewSelect();
				arg0.setSelected(true);
				img_program_pic.setImageResource(R.drawable.img_program_virtual_07_4);
				viewPager.setVisibility(View.GONE);
				page_next.setVisibility(View.VISIBLE);
				pager_prompt_dot.setVisibility(View.GONE);
				UserInfoManager.getInstance().vrSceneVideoNo = VrVideoItem.ITEM_VR_UNITED_KINGDOM.ordinal();
				break;
			case R.id.img_program_virtual_08:
				resetProgramViewSelect();
				arg0.setSelected(true);
				img_program_pic.setImageResource(R.drawable.img_program_virtual_08_4);
				viewPager.setVisibility(View.GONE);
				page_next.setVisibility(View.VISIBLE);
				pager_prompt_dot.setVisibility(View.GONE);
				UserInfoManager.getInstance().vrSceneVideoNo = VrVideoItem.ITEM_VR_AUSTRIA_WIEN.ordinal();
				break;
			case R.id.img_program_virtual_09:
				resetProgramViewSelect();
				arg0.setSelected(true);
				img_program_pic.setImageResource(R.drawable.img_program_virtual_09_4);
				viewPager.setVisibility(View.GONE);
				page_next.setVisibility(View.VISIBLE);
				pager_prompt_dot.setVisibility(View.GONE);
				UserInfoManager.getInstance().vrSceneVideoNo = VrVideoItem.ITEM_VR_AUSTRIA_HELMESGUPF.ordinal();
				break;
			case R.id.img_program_virtual_10:
				resetProgramViewSelect();
				arg0.setSelected(true);
				img_program_pic.setImageResource(R.drawable.img_program_virtual_10_4);
				viewPager.setVisibility(View.GONE);
				page_next.setVisibility(View.VISIBLE);
				pager_prompt_dot.setVisibility(View.GONE);
				UserInfoManager.getInstance().vrSceneVideoNo = VrVideoItem.ITEM_VR_AUSTRIA_HALLSTATT.ordinal();
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
				if( btn_start.isEnabled() && btn_start.getVisibility() == View.VISIBLE ) {
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

	public void resetProgramViewSelect() {
		for ( int i = 0; i <= VrVideoItem.ITEM_VR_AUSTRIA_HALLSTATT.ordinal(); i++ ) {
			runProgramView[i].setSelected(false);
		}
	}

}
