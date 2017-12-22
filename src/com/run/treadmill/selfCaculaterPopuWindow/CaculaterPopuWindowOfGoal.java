package com.run.treadmill.selfCaculaterPopuWindow;

import com.run.treadmill.R;
import com.run.treadmill.db.UserInfoManager;
import com.run.treadmill.systemInitParam.InitParam;
import com.run.treadmill.util.CTConstant;
import com.run.treadmill.util.MyUtils;
import com.run.treadmill.util.StorageParam;
import com.run.treadmill.util.Support;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

 
public class CaculaterPopuWindowOfGoal implements OnClickListener {
 
    private Context curContext;
    private PopupWindow mPopupWindow = null;
    private int res;
    private View mlayout_gender_frame;

    private String textValue="";

    private ImageView caculaterdel;
    private ImageView caculater0;
    private ImageView caculater1;
    private ImageView caculater2;
    private ImageView caculater3;
    private ImageView caculater4;
    private ImageView caculater5;
    private ImageView caculater6;
    private ImageView caculater7;
    private ImageView caculater8;
    private ImageView caculater9;
    private ImageView caculater_close;
    private ImageView caculater_cap;
    private ImageView caculaterenter;

    private EditText editText;
    private int mScreenWidth;
    private int mScreenHeight;
    
    private int RUNNING_STYLE = 1;
    private int surfaceStyle = -1;
    private CaculaterCallBack mCaculaterCallBack;
    private int originalValue;
    
    private boolean isGoalMode = false;
    private boolean isFirstChick = false;
 
    public CaculaterPopuWindowOfGoal(Context mContext) {
    	curContext = mContext;
    	onCreate();
    }
 
    public PopupWindow onCreate( ) {
//        LayoutInflater mLayoutInflater = (LayoutInflater) curContext.getSystemService("layout_inflater");
        View view = ((Activity) curContext).getLayoutInflater().inflate(R.layout.cacul_layout, null);
        caculaterdel = (ImageView) view.findViewById(R.id.caculaterdel);
        caculater0 = (ImageView) view.findViewById(R.id.caculater0);
        caculater1 = (ImageView) view.findViewById(R.id.caculater1);
        caculater2 = (ImageView) view.findViewById(R.id.caculater2);
        caculater3 = (ImageView) view.findViewById(R.id.caculater3);
        caculater4 = (ImageView) view.findViewById(R.id.caculater4);
        caculater5 = (ImageView) view.findViewById(R.id.caculater5);
        caculater6 = (ImageView) view.findViewById(R.id.caculater6);
        caculater7 = (ImageView) view.findViewById(R.id.caculater7);
        caculater8 = (ImageView) view.findViewById(R.id.caculater8);
        caculater9 = (ImageView) view.findViewById(R.id.caculater9);
        caculater_close = (ImageView) view.findViewById(R.id.caculater_close);
        editText = (EditText) view.findViewById(R.id.caculater_editText);
        caculater_cap = (ImageView) view.findViewById(R.id.caculater_cap);
        caculaterenter = (ImageView) view.findViewById(R.id.caculaterenter);

        caculater0.setOnClickListener(this);
        caculater1.setOnClickListener(this);
        caculater2.setOnClickListener(this);
        caculater3.setOnClickListener(this);
        caculater4.setOnClickListener(this);
        caculater5.setOnClickListener(this);
        caculater6.setOnClickListener(this);
        caculater7.setOnClickListener(this);
        caculater8.setOnClickListener(this);
        caculater9.setOnClickListener(this);
        caculater_close.setOnClickListener(this);
        caculaterdel.setOnClickListener(this);
        caculaterenter.setOnClickListener(this);
        
        initScreen();
//        int height=mScreenHeight/3;
        mPopupWindow = new PopupWindow(view, 400, 485);
//        pw.setBackgroundDrawable(new ColorDrawable(0));
//        pw.setOutsideTouchable(true);
        return mPopupWindow;
    }
    
    public interface CaculaterCallBack
    {
        public void speedDataProcCallback(int value);
        public void inclineDataProcCallback(int value);
    }

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Support.buzzerRingOnce();
		if( arg0 == caculater_close ) {
			mPopupWindow.dismiss();
			int outValue;
			if( editText.getText().toString().isEmpty() ) {
				outValue = originalValue;
			} else {
				outValue = Integer.parseInt(editText.getText().toString());
				mFourTextView.setSelection(mEditText.getText().length());
			}
			mEditText.setSelection(mEditText.getText().length());
			mOneTextView.setText("---");
			mTwoTextView.setText("---");
			mlayout_gender_frame.setVisibility(View.VISIBLE);
			mThreeView.setVisibility(View.GONE);
			mFourTextView.setVisibility(View.GONE);
			if ( res == R.drawable.tv_keybord_time ) {
				UserInfoManager.getInstance().setTargetRunType(CTConstant.RUN_REFERENCE_BY_TIME);
				UserInfoManager.getInstance().setTargetTime(Integer.parseInt(mEditText.getText().toString()),
						UserInfoManager.getInstance().getRunMode());
			}
			if ( res == R.drawable.tv_keybord_distance ) {
				UserInfoManager.getInstance().setTargetRunType(CTConstant.RUN_REFERENCE_BY_DISTANCE);
				UserInfoManager.getInstance().setTargetDistance(Integer.parseInt(mEditText.getText().toString()));
				UserInfoManager.getInstance().setTargetTime(0, UserInfoManager.getInstance().getRunMode());
			}
			if ( res == R.drawable.tv_keybord_calories ) {
				UserInfoManager.getInstance().setTargetRunType(CTConstant.RUN_REFERENCE_BY_CALORIES);
				UserInfoManager.getInstance().setTargetCalorie(Integer.parseInt(mEditText.getText().toString()));
				UserInfoManager.getInstance().setTargetTime(0, UserInfoManager.getInstance().getRunMode());
			}
        }
		if( arg0 == caculaterenter ) {
			if( editText.getText().toString().isEmpty() ) {
				return ;
			}
			mPopupWindow.dismiss();

			int outValue = Integer.parseInt(editText.getText().toString());
			Log.d("outValue","==0=== " + outValue);
			if ( res == R.drawable.tv_keybord_time ) {

				if(  outValue <= InitParam.MaxGoalTime  && outValue >= InitParam.MinGoalTime ) {
					Log.d("outValue","=====1= " + outValue);
					mEditText.setText(outValue+"");
				} else if ( outValue > InitParam.MaxGoalTime ) {
					mEditText.setText( InitParam.MaxGoalTime + "" );
				} else if ( outValue < InitParam.MinGoalTime ) {
					mEditText.setText( InitParam.MinGoalTime + "" );
				}
				mEditText.setSelection(mEditText.getText().length());
				mFourTextView.setSelection(mFourTextView.getText().length());
				mOneTextView.setText("---");
				mTwoTextView.setText("---");
				UserInfoManager.getInstance().setTargetRunType(CTConstant.RUN_REFERENCE_BY_TIME);
				UserInfoManager.getInstance().setTargetTime(Integer.parseInt(mEditText.getText().toString()),
						UserInfoManager.getInstance().getRunMode());
			}
			if ( res == R.drawable.tv_keybord_distance ) {

				if(  outValue <= InitParam.MaxGoalDis  && outValue >= InitParam.MinGoalDis ) {
					Log.d("outValue","=====1= " + outValue);
					mEditText.setText(outValue+"");	

				} else if ( outValue > InitParam.MaxGoalDis ) {
					mEditText.setText( InitParam.MaxGoalDis + "" );
				} else if ( outValue < InitParam.MinGoalDis ) {
					mEditText.setText( InitParam.MinGoalDis + "" );
				}

				/*Log.d(""," mEditText.getText().length() " + mEditText.getText().length() + 
						" " + mFourTextView.getText().length());*/
				mEditText.setSelection(mEditText.getText().length());
				mFourTextView.setSelection(mFourTextView.getText().length());
				mOneTextView.setText("---");
				mTwoTextView.setText("---");
				UserInfoManager.getInstance().setTargetRunType(CTConstant.RUN_REFERENCE_BY_DISTANCE);
				if( StorageParam.getIsMetric(curContext) ) {
					UserInfoManager.getInstance().setTargetDistance(Integer.parseInt(mEditText.getText().toString()));
				} else {
					int dis = Integer.parseInt(mEditText.getText().toString());
					UserInfoManager.getInstance().setTargetDistance(MyUtils.getMileToKmFloat(dis));
				}
				UserInfoManager.getInstance().setTargetTime(0, UserInfoManager.getInstance().getRunMode());
			}
			if ( res == R.drawable.tv_keybord_calories ) {

				if(  outValue <= InitParam.MaxGoalCal  && outValue >= InitParam.MinGoalCal ) {
					Log.d("outValue","=====1= " + outValue);
					mEditText.setText(outValue+"");
				} else if ( outValue > InitParam.MaxGoalCal ) {
					mEditText.setText( InitParam.MaxGoalCal + "" );
				} else if ( outValue < InitParam.MinGoalCal ) {
					mEditText.setText( InitParam.MinGoalCal + "" );
				}
				mEditText.setSelection(mEditText.getText().length());
				mFourTextView.setSelection(mFourTextView.getText().length());
				mOneTextView.setText("---");
				mTwoTextView.setText("---");
				UserInfoManager.getInstance().setTargetRunType(CTConstant.RUN_REFERENCE_BY_CALORIES);
				UserInfoManager.getInstance().setTargetCalorie(Integer.parseInt(mEditText.getText().toString()));
				UserInfoManager.getInstance().setTargetTime(0, UserInfoManager.getInstance().getRunMode());
			}
			mlayout_gender_frame.setVisibility(View.VISIBLE);
			mThreeView.setVisibility(View.GONE);
			mFourTextView.setVisibility(View.GONE);
        }
		if( arg0 == caculaterdel ){
            if( editText.getText().toString().trim().length() > 0 ) {
                deleteEditValue(editText, getEditSelection(editText));
            }
        } else {
            if( arg0 == caculater0 ) {
            	textValue="0";
            } else if ( arg0 == caculater1 ) {
            	textValue="1";
            } else if ( arg0 == caculater2 ) {
            	textValue="2";
            } else if ( arg0 == caculater3 ) {
            	textValue="3";
            } else if ( arg0 == caculater4 ) {
            	textValue="4";
            } else if ( arg0 == caculater5 ) {
            	textValue="5";
            } else if ( arg0 == caculater6 ) {
            	textValue="6";
            } else if ( arg0 == caculater7 ) {
            	textValue="7";
            } else if ( arg0 == caculater8 ) {
            	textValue="8";
            } else if ( arg0 == caculater9 ) {
            	textValue = "9";
            }

            if( !isFirstChick ) {
        		editText.append(textValue);
        	} else {
        		isFirstChick = false;
        		editText.setText(textValue);
        	}
            editText.setSelection(editText.getText().length());
        }

	}
	
	public void StartPopupWindow(View view, int x, int y){
            if(mPopupWindow!=null&&!mPopupWindow.isShowing()){
                mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y);
            }
         
    }
 
    public void StopPopupWindow(){
         if(mPopupWindow!=null&&mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
//            mPopupWindow = null;
         }
    }

    private EditText mEditText;
    private EditText mOneTextView;
    private EditText mTwoTextView;
    private View mThreeView;
    private EditText mFourTextView;

    private PopupWindow mPopuWindow;
    
    public void setRelatedAndRes(EditText mEditText, int mRes, View rlayout_gender_frame
    		, EditText mOneTextView, EditText mTwoTextView, View mThreeView, 
    		EditText mFourTextView) {
    	isFirstChick = true;
    	isGoalMode = true;
        this.mEditText=mEditText;
        editText.setText(mEditText.getText().toString());
        editText.setSelection(editText.getText().length());
        this.mOneTextView = mOneTextView;
        this.mTwoTextView = mTwoTextView;
        this.mThreeView = mThreeView;
        this.mFourTextView = mFourTextView;

    	res = mRes;
    	mlayout_gender_frame = rlayout_gender_frame;
    	caculater_cap.setBackgroundResource(res);
    }

    // 获取光标当前位置
    public int getEditSelection(EditText editText) {
        return editText.getSelectionStart();
    }
 
    // 删除指定位置的字符
    public void deleteEditValue(EditText editText,int index) {
        editText.getText().delete(index - 1, index);
    }
    
    public void initScreen(){
    	 
        WindowManager manager=(WindowManager)curContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics=new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
 
        mScreenWidth=outMetrics.widthPixels;
        mScreenHeight=outMetrics.heightPixels;
    }
}
