package com.run.treadmill;


import com.run.treadmill.manager.ScreenManager;
import com.run.treadmill.selfdefView.ParamView;
import com.run.treadmill.serialutil.CmdMsgHandler;
import com.run.treadmill.serialutil.IEventProcess;
import com.run.treadmill.serialutil.SerialUtils;
import com.run.treadmill.util.Support;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class BaseActivity extends Activity implements IEventProcess {
	private final String TAG = "BaseActivity";

	private Context mContext;
	public ScreenManager mScreenManager = null;
	private CmdMsgHandler mCmdMsgHandler = null;
	public SerialUtils mSerialUtils;

	public static final int MEG_COUNT_DOWN = 1001;
	public static final int MSG_REFRESH_PARAM_VALUE = 1002;
	public static final int MSG_PROC_ERROR_CMD = 1003;
	public static final int MSG_PROC_ENBLE_CONTINUE = 1004;
	public static final int MSG_REFRESH_HEART = 1005;

	public final int RUN_VALUE_GET_TIME = 1000;
	public final int RUN_HEART_BEART = 500;
	public final int PREPARE_PAGE_TIMER_DELAY = 1000;
	public final int COOL_DOWN_TIMER_DELAY = 1000 * 10;
	public final int PAUSE_PAGE_TIMER_DELAY = 1000 * 60 * 3;

	public RelativeLayout layout_rpm_view;

	public RelativeLayout layout_sportmode_rpm;
	public ImageView img_sportmode_rpm;
	public ImageView text_sportmode_rpm_1;
	public ImageView text_sportmode_rpm_2;
	public ImageView text_sportmode_rpm_3;

	public RelativeLayout min_Chart_View;
	public ImageView min_Chart_View_Unit;
	public ImageView min_Chart_View_bg;
	public ParamView min_Chart_View_Histogram;
	public TextView min_Chart_View_Cap;

	public RelativeLayout btn_sportmode_media_out_layout;

	public boolean isMinChart = true;

	public boolean isNeedBuzzer = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"onCreate function!");  
		mContext = this;
		mScreenManager = ScreenManager.getScreenManager();
		mScreenManager.pushActivity(this);
		mCmdMsgHandler = new CmdMsgHandler(this);
	}

	public CmdMsgHandler getCmdMsgHandler() {
		return this.mCmdMsgHandler;
	}
	
	@Override
	public void onDestroy() {    	
    	Log.d(TAG,"onDestroy function!");    	    	
    	super.onDestroy();
    	mScreenManager.popActivity(this);

    }
    
    @Override
    public void onResume() {
    	Log.d(TAG,"onResume function!");
    	super.onResume();
    	Support.hideBottomUIMenu(this);
    	if ( null != SerialUtils.getInstance()) {
    		SerialUtils.getInstance().registerEventProcess(this);
    		mSerialUtils = SerialUtils.getInstance();
    	}
    }
    
    @Override
    public void onPause() {
    	Log.d(TAG,"onPause function!");    	 
    	super.onPause();  
    	if ( null != SerialUtils.getInstance()) {
    		SerialUtils.getInstance().unregisterEventProcess(this);
    	}
    }

    public void resetInfo(int res){
    	
    }

	public void startMinToMaxAnimation() {
        Animation animation=AnimationUtils.loadAnimation(this, R.anim.double_animation);  
        layout_rpm_view.startAnimation(animation);//开始动画  
        animation.setAnimationListener(new AnimationListener() {
            @Override  
            public void onAnimationStart(Animation animation) {
//            	min_Chart_View.setVisibility(View.GONE);
            }
            @Override  
            public void onAnimationRepeat(Animation animation) {
            }
            @Override  
            public void onAnimationEnd(Animation animation) {
            	if( btn_sportmode_media_out_layout.getVisibility() == View.GONE ) {
            		layout_rpm_view.clearAnimation();
                	showMaxChartView();
                	
                	isMinChart = false;
            	}
//            	max_Chart_View.setVisibility(View.VISIBLE);
            }
        });  
	}

	public void showMaxChartView() {
		RelativeLayout.LayoutParams mImageParams = (android.widget.RelativeLayout.LayoutParams) 
				layout_rpm_view.getLayoutParams();		
		mImageParams.setMargins(330, 140, 0, 0);	//left top right button
		mImageParams.width = 649;
		mImageParams.height = 474;
		layout_rpm_view.setLayoutParams(mImageParams);

		mImageParams = (android.widget.RelativeLayout.LayoutParams) 
				layout_sportmode_rpm.getLayoutParams();		
    	mImageParams.setMargins(0, 0, 0, 0);
    	mImageParams.width = 649;
    	mImageParams.height = 176;
    	layout_sportmode_rpm.setLayoutParams(mImageParams);

		mImageParams = (android.widget.RelativeLayout.LayoutParams) 
				img_sportmode_rpm.getLayoutParams();		
    	mImageParams.setMargins(0, 0, 0, 0);
    	mImageParams.width = 649;
    	mImageParams.height = 176;
    	img_sportmode_rpm.setLayoutParams(mImageParams);

		mImageParams = (android.widget.RelativeLayout.LayoutParams) 
				text_sportmode_rpm_1.getLayoutParams();		
    	mImageParams.setMargins(144, 88, 0, 0);
    	mImageParams.width = 100;
    	mImageParams.height = 88;
    	text_sportmode_rpm_1.setLayoutParams(mImageParams);

		mImageParams = (android.widget.RelativeLayout.LayoutParams) 
				text_sportmode_rpm_2.getLayoutParams();		
    	mImageParams.setMargins(244, 88, 0, 0);
    	mImageParams.width = 100;
    	mImageParams.height = 88;
    	text_sportmode_rpm_2.setLayoutParams(mImageParams);

		mImageParams = (android.widget.RelativeLayout.LayoutParams) 
				text_sportmode_rpm_3.getLayoutParams();		
    	mImageParams.setMargins(344, 88, 0, 0);
    	mImageParams.width = 100;
    	mImageParams.height = 88;
    	text_sportmode_rpm_3.setLayoutParams(mImageParams);

    	mImageParams = (android.widget.RelativeLayout.LayoutParams) 
    			min_Chart_View.getLayoutParams();	
		mImageParams.setMargins(0, 220, 0, 0);//left top right button
		mImageParams.width = 584;
		mImageParams.height = 254;
    	min_Chart_View.setLayoutParams(mImageParams);
    	
    	mImageParams = (android.widget.RelativeLayout.LayoutParams) 
    			min_Chart_View_Unit.getLayoutParams();		
    	mImageParams.setMargins(0, 0, 0, 0);
    	mImageParams.width = 38;
    	mImageParams.height = 193;
    	min_Chart_View_Unit.setLayoutParams(mImageParams);
    	
    	mImageParams = (android.widget.RelativeLayout.LayoutParams) 
    			min_Chart_View_bg.getLayoutParams();		
    	mImageParams.setMargins(38, 0, 0, 0);
    	mImageParams.width = 546;
    	mImageParams.height = 254;
    	min_Chart_View_bg.setLayoutParams(mImageParams);
    	
    	mImageParams = (android.widget.RelativeLayout.LayoutParams) 
    			min_Chart_View_Histogram.getLayoutParams();		
    	mImageParams.setMargins(38, 0, 0, 0);
    	mImageParams.width = 546;
    	mImageParams.height = 254;
    	min_Chart_View_Histogram.setLayoutParams(mImageParams);
    	
    	mImageParams = (android.widget.RelativeLayout.LayoutParams) 
    			min_Chart_View_Cap.getLayoutParams();		
    	mImageParams.setMargins(320, 228, 0, 0);//left top right button
    	min_Chart_View_Cap.setTextSize(20);
    	min_Chart_View_Cap.setLayoutParams(mImageParams);
	}

	public void startMaxToMinAnimation() {
        Animation animation=AnimationUtils.loadAnimation(this, R.anim.small_animation);  
        layout_rpm_view.startAnimation(animation);//开始动画  
        animation.setAnimationListener(new AnimationListener(){
            @Override  
            public void onAnimationStart(Animation animation) {
            }  
            @Override  
            public void onAnimationRepeat(Animation animation) {
            }  
            @Override  
            public void onAnimationEnd(Animation animation) {
            	
        		layout_rpm_view.clearAnimation();
            	directShowMinChartView();
            }  
        });
	}

	public void directShowMinChartView() {
		showMinChartView();
    	isMinChart = true;
	}

	public void showMinChartView() {
		RelativeLayout.LayoutParams mImageParams = (android.widget.RelativeLayout.LayoutParams) 
				layout_rpm_view.getLayoutParams();		
		mImageParams.setMargins(45, 234, 0, 0);		//left top right button
		mImageParams.width = 390;
		mImageParams.height = 285;
		layout_rpm_view.setLayoutParams(mImageParams);

		mImageParams = (android.widget.RelativeLayout.LayoutParams) 
				layout_sportmode_rpm.getLayoutParams();		
    	mImageParams.setMargins(0, 0, 0, 0);
    	mImageParams.width = 390;
    	mImageParams.height = 106;
    	layout_sportmode_rpm.setLayoutParams(mImageParams);

		mImageParams = (android.widget.RelativeLayout.LayoutParams) 
				img_sportmode_rpm.getLayoutParams();		
    	mImageParams.setMargins(0, 0, 0, 0);
    	mImageParams.width = 390;
    	mImageParams.height = 106;
    	img_sportmode_rpm.setLayoutParams(mImageParams);

		mImageParams = (android.widget.RelativeLayout.LayoutParams) 
				text_sportmode_rpm_1.getLayoutParams();		
    	mImageParams.setMargins(87, 53, 0, 0);
    	mImageParams.width = 60;
    	mImageParams.height = 53;
    	text_sportmode_rpm_1.setLayoutParams(mImageParams);

		mImageParams = (android.widget.RelativeLayout.LayoutParams) 
				text_sportmode_rpm_2.getLayoutParams();		
    	mImageParams.setMargins(147, 53, 0, 0);
    	mImageParams.width = 60;
    	mImageParams.height = 53;
    	text_sportmode_rpm_2.setLayoutParams(mImageParams);

		mImageParams = (android.widget.RelativeLayout.LayoutParams) 
				text_sportmode_rpm_3.getLayoutParams();		
    	mImageParams.setMargins(207, 53, 0, 0);
    	mImageParams.width = 60;
    	mImageParams.height = 53;
    	text_sportmode_rpm_3.setLayoutParams(mImageParams);

		mImageParams = (android.widget.RelativeLayout.LayoutParams) 
				min_Chart_View.getLayoutParams();		
		mImageParams.setMargins(0, 132, 0, 0);//left top right button
		mImageParams.width = 351;
		mImageParams.height = 152;
    	min_Chart_View.setLayoutParams(mImageParams);
    	
    	mImageParams = (android.widget.RelativeLayout.LayoutParams) 
    			min_Chart_View_Unit.getLayoutParams();		
    	mImageParams.setMargins(0, 0, 0, 0);
    	mImageParams.width = 23;
    	mImageParams.height = 116;
    	min_Chart_View_Unit.setLayoutParams(mImageParams);
    	
    	mImageParams = (android.widget.RelativeLayout.LayoutParams) 
    			min_Chart_View_bg.getLayoutParams();		
    	mImageParams.setMargins(23, 0, 0, 0);
    	mImageParams.width = 382;
    	mImageParams.height = 152;
    	min_Chart_View_bg.setLayoutParams(mImageParams);
    	
    	mImageParams = (android.widget.RelativeLayout.LayoutParams) 
    			min_Chart_View_Histogram.getLayoutParams();		
    	mImageParams.setMargins(23, 0, 0, 0);
    	mImageParams.width = 382;
    	mImageParams.height = 152;
    	min_Chart_View_Histogram.setLayoutParams(mImageParams);
    	
    	mImageParams = (android.widget.RelativeLayout.LayoutParams) 
    			min_Chart_View_Cap.getLayoutParams();
    	min_Chart_View_Cap.setTextSize(10);
    	mImageParams.setMargins(98, 140, 0, 0);//left top right button
    	min_Chart_View_Cap.setLayoutParams(mImageParams);
	}

}
