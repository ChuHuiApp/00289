package com.run.treadmill;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
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

import com.run.treadmill.adapter.MyPagerAdapter;
import com.run.treadmill.db.UserInfoManager;
import com.run.treadmill.util.CTConstant;
import com.run.treadmill.util.FitnessTestEvaluate;
import com.run.treadmill.util.MyUtils;
import com.run.treadmill.util.StorageParam;
import com.run.treadmill.util.Support;
import com.run.treadmill.util.CTConstant.RunMode;

import com.run.treadmill.selfdefView.LineGraphicView;

public class SummaryActivity extends BaseActivity implements OnPageChangeListener, OnClickListener {
	
	private String TAG = "SummaryActivity";

	private ImageView btn_ergo;
	private ImageView summary_layout;
	private ViewPager viewPager;
	private ImageView btn_home;
	private ImageView pager_prompt_dot;
	private LayoutInflater mLayoutInflater;
	private MyPagerAdapter mPagerAdapter;
	private ArrayList<View> mViewList = new ArrayList<View>();
	
	private Context mContext;
	
	private TextView run_distance;
	private TextView run_distance_unit;
	private TextView run_calories;
	private TextView run_av_hr;
	private TextView run_av_speed;
	private TextView run_av_speed_unit;
	private TextView run_time;
	private TextView run_time_unit;
	private RelativeLayout layout_fitness;
	private TextView vo2_evaluate;
	private TextView vo2_value;
	private RelativeLayout layout_speed;
	
	private TextView page_two_av_speed;
	private TextView page_two_av_hr;

	private LineGraphicView mLineGraphicViewTwo;
	private LineGraphicView mLineGraphicViewFour;
	private ArrayList<Double> yList = new ArrayList<Double>();
	private int delayCloseSurfaceTimer = 3 * 60 * 1000;

	private float runTime;
	private int av_hr = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summary);

		mContext = this;
		mLayoutInflater = getLayoutInflater();
		if ( !getIntent().getExtras().getBoolean(CTConstant.IS_SHOW_SUMMARY, true) ) {
			stopRunAndSkipToRunningActivity(true);
			return ;
		}


		float runDistance = UserInfoManager.getInstance().getTotalDistance();
		runTime = UserInfoManager.getInstance().getTotalTime();
		float runCalories = UserInfoManager.getInstance().getTotalCalories();

		Log.d( "runDistance"," getTotalDistance " + 
				UserInfoManager.getInstance().getTotalDistance() + " runTime " + runTime );

		initSubView();
		mContext = this;
		if ( StorageParam.getIsMetric(mContext) ) {
			run_distance.setText(MyUtils.getDistanceValue(mContext, runDistance));
		} else {
			run_distance.setText(MyUtils.getDistanceMileValue(mContext, runDistance));
			run_distance_unit.setText("mile");
		}
		
		run_calories.setText(MyUtils.getShowIntValue(runCalories));
		av_hr = caculAvHr();
		run_av_hr.setText( av_hr + "" );
		page_two_av_hr.setText( this.getResources().getString(
        		R.string.string_summary_hr_av_caption, av_hr + "") );
		if ( StorageParam.getIsMetric(mContext) ) {
			run_av_speed.setText(MyUtils.calSpeedBasisDistanceTime(runDistance, runTime));
		} else {
			Log.d(TAG,"SUMM " +MyUtils.calSpeedBasisDistanceTime(runDistance, runTime));
			float speed = Float.parseFloat(MyUtils.calSpeedBasisDistanceTime(runDistance, runTime));
			run_av_speed.setText(MyUtils.getKmToMileOneP(speed) + "");
			run_av_speed_unit.setText(R.string.string_summary_mile_h);
		}
		run_time.setText(MyUtils.getMsToHourTimeValue(runTime, run_time_unit));

		if ( StorageParam.getIsMetric(mContext) ) {
			page_two_av_speed.setText(this.getResources().getString(
	        		R.string.string_summary_speed_av_caption, 
	        		" "+caculAvIncline() + " %"));
		} else {
			float speed = Float.parseFloat(MyUtils.calSpeedBasisDistanceTime(runDistance, runTime));
			page_two_av_speed.setText(this.getResources().getString(
	        		R.string.string_summary_Incline_av_caption) + " "+caculAvIncline() + " %" );
		}

		mCloseHandler.postDelayed(closeTasks, delayCloseSurfaceTimer);

		Support.hideBottomUIMenu(this);
		if ( UserInfoManager.getInstance().getRunMode() == 
				RunMode.IDX_HOME_VIRTUAL_MODE.ordinal() ) {
			setFirstFrameView();
		}
		if ( UserInfoManager.getInstance().getRunMode() == 
				RunMode.IDX_HOME_FITNESS_MODE.ordinal() ) {
			layout_speed.setVisibility(View.GONE);
			layout_fitness.setVisibility(View.VISIBLE);
			if ( runTime <= 0.9999 ) {
				vo2_evaluate.setText( "" );
				vo2_value.setText( 0 + "" );
			} else {
				vo2_evaluate.setText(FitnessTestEvaluate.getEvaluate(
						UserInfoManager.getInstance().getSex(), 
						UserInfoManager.getInstance().getAge(UserInfoManager.getInstance().getRunMode()), 
						UserInfoManager.getInstance().fitnessVo2Max));
				vo2_value.setText(UserInfoManager.getInstance().fitnessVo2Max + "");
			}
			UserInfoManager.getInstance().fitnessVo2Max = 0.0f;
		}
	}

	private float caculAvIncline() {
		/*ArrayList<Double> inclineList = UserInfoManager.getInstance().getLevelList();
		Double totalIncline = (double) 0;
		for( int i = 0;i < inclineList.size(); i ++ ) {
			totalIncline += inclineList.get(i);
		}
		return (int) (totalIncline / inclineList.size());*/
		/*Log.d(TAG, " +++ " + ( UserInfoManager.getInstance().getTotalInlcine() / runTime * 1000) 
		+ " getTotalInlcine " + UserInfoManager.getInstance().getTotalInlcine() 
		+ " runTime " + runTime);*/
		Log.d(TAG, "UserInfoManager.getInstance().getTotalLevel() " + 
				UserInfoManager.getInstance().getTotalLevel());
		if ( runTime < 0.9999 ) {
			return 0;
		}
		return (float) (Math.round( UserInfoManager.getInstance().getTotalLevel() * 1.0 / runTime * 1000 * 10 ) / 10.0) ;
	}
	
	private int caculAvHr() {
		ArrayList<Double> hrList = UserInfoManager.getInstance().getHrList();
		Double totalHr = (double) 0;
		for( int i = 0;i < hrList.size(); i ++ ) {
			totalHr += hrList.get(i);
		}
		if ( runTime < 0.9999 ) {
			return 0;
		}
		return (int) (totalHr / hrList.size());
	}

	private Handler mCloseHandler = new Handler();

	private Runnable closeTasks = new Runnable() {
        @Override
        public void run() {
        	// auto dismiss ;while 1-2s late
//        	SummaryActivity.this.finish();
        	stopRunAndSkipToRunningActivity(true);
        }
    };

	private void initSubView() {

		btn_ergo = (ImageView) findViewById(R.id.btn_ergo);
		View viewOne;
		viewOne = mLayoutInflater.inflate(R.layout.summary_view_pager_one, null);
		run_distance = (TextView) viewOne.findViewById(R.id.run_distance);
		run_distance_unit = (TextView) viewOne.findViewById(R.id.run_distance_unit);
        run_calories = (TextView) viewOne.findViewById(R.id.run_calories);
        run_av_hr = (TextView) viewOne.findViewById(R.id.run_av_hr);
        run_av_speed = (TextView) viewOne.findViewById(R.id.run_av_speed);
        run_av_speed_unit = (TextView) viewOne.findViewById(R.id.run_av_speed_unit);
        run_time = (TextView) viewOne.findViewById(R.id.run_time);
        run_time_unit = (TextView) viewOne.findViewById(R.id.run_time_unit);

        layout_fitness = (RelativeLayout) viewOne.findViewById(R.id.layout_fitness);
        vo2_evaluate = (TextView) viewOne.findViewById(R.id.vo2_evaluate);
        vo2_value = (TextView) viewOne.findViewById(R.id.vo2_value);
        layout_speed = (RelativeLayout) viewOne.findViewById(R.id.layout_speed);

		View viewTwo;
		viewTwo = mLayoutInflater.inflate(R.layout.summary_view_pager_two, null);
		page_two_av_speed = (TextView) viewTwo.findViewById(R.id.page_two_av_speed);

		View viewFour;
		viewFour = mLayoutInflater.inflate(R.layout.summary_view_pager_four, null);
		page_two_av_hr = (TextView) viewFour.findViewById(R.id.page_two_av_hr);

		viewPager = (ViewPager) findViewById(R.id.view_pager);
		mViewList.add(viewOne); 
		mViewList.add(viewTwo);
		mViewList.add(viewFour);

		mPagerAdapter = new MyPagerAdapter(mViewList);  
		viewPager.setAdapter(mPagerAdapter); 
		viewPager.setOnPageChangeListener(this);

		mLineGraphicViewTwo = (LineGraphicView) viewTwo.findViewById(R.id.mLineGraphicView);
		mLineGraphicViewFour = (LineGraphicView) viewFour.findViewById(R.id.mLineGraphicView);

		pager_prompt_dot = (ImageView) findViewById(R.id.pager_prompt_dot);
		summary_layout = (ImageView) findViewById(R.id.summary_layout);
		btn_home = (ImageView) findViewById(R.id.btn_home);
		btn_home = (ImageView) findViewById(R.id.btn_home);
		btn_home.setOnClickListener(this);

/*        yList.add((double) 2.103);
        yList.add(18.05);
        yList.add(11.60);
        yList.add(19.08);
        yList.add(4.32);
        yList.add(12.0);
        yList.add(5.0);*/

//        ArrayList<String> xRawDatas = new ArrayList<String>();

		//2 显示Y轴的间隔刻度
		if ( runTime < 0.9999 ) {
			ArrayList<Double> xList = new ArrayList<Double>();
			ArrayList<Double> yList = new ArrayList<Double>();
			xList.add((double)0);
			yList.add((double)0);
			xList.add((double)2);
			yList.add((double)0);
			mLineGraphicViewTwo.setData(yList, xList, 36, 2, 2);
	        mLineGraphicViewFour.setData(yList, xList, 250, 2, 2);
		} else {
	        mLineGraphicViewTwo.setData(UserInfoManager.getInstance().getLevelList(), 
	        		UserInfoManager.getInstance().getLevelTimeList(), 36, 2, runTime);
	        mLineGraphicViewFour.setData(UserInfoManager.getInstance().getHrList(), 
	        		UserInfoManager.getInstance().getHrTimeList(), 250, 2, runTime);
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		Support.setLogoIcon(this, btn_ergo);
		/*if( !StorageParam.getisInnerLogo(this) && Support.isCheckExist(mContext.getFilesDir() + "logo.png")) {
			Bitmap bmpDefaultPic;
			bmpDefaultPic = BitmapFactory.decodeFile(mContext.getFilesDir() + "logo.png",null);
			btn_ergo.setImageBitmap(bmpDefaultPic);
			if( bmpDefaultPic != null && !bmpDefaultPic.isRecycled() ) {
				bmpDefaultPic.recycle();
				bmpDefaultPic = null;
			}
		} else {
			btn_ergo.setImageResource(R.drawable.btn_ergo);
		}*/
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_home:
				Support.buzzerRingOnce();
				stopRunAndSkipToRunningActivity(true);
				break;
			default:
				break;
		}
	}

	@Override
	public void errorEventProc(int errorValue) {
		if( errorValue == 1 ) {
			stopRunAndSkipToRunningActivity(false);
		}
	}

	@Override
	public void onCmdEvent(Message msg) {
		
	}

	public void stopRunAndSkipToRunningActivity(boolean isError) {
		Intent intent = new Intent();
		StorageParam.setIsRunIng(mContext, false);
		intent.putExtra(CTConstant.IS_FINSH, isError);
		MyUtils.animLeftFinishActivity(this, intent);
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

    private String videoPath = "/storage/sdcard/" + "/Austria_Hallstatt.mp4";
    public void setFirstFrameView() {
    	videoPath = 
        		CTConstant.vrVideoPath[UserInfoManager.getInstance().vrSceneVideoNo];
    	MediaMetadataRetriever media = new MediaMetadataRetriever();
    	media.setDataSource(videoPath);

    	Bitmap bitmap = media.getFrameAtTime();

    	summary_layout.setImageBitmap(bitmap); 
    }

}
