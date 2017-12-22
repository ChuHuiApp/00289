package com.run.treadmill.selfCaculaterPopuWindow;

import com.run.treadmill.R;
import com.run.treadmill.db.UserInfoManager;
import com.run.treadmill.util.CTConstant;
import com.run.treadmill.util.StorageParam;
import com.run.treadmill.util.Support;
import com.run.treadmill.util.CTConstant.RunMode;

import android.app.Activity;
import android.content.Context;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

 
public class CaculaterPopuWindowofTextView implements OnClickListener {
 
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
 
    public CaculaterPopuWindowofTextView(Context mContext) {
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
		if( arg0 == caculater_close ){
			mPopupWindow.dismiss();
//				mPopupWindow = null;
        }
		if( arg0 == caculaterenter ){
			if( editText.getText().toString().isEmpty() ) {
				return ;
			}
			mPopupWindow.dismiss();

			int outValue = Integer.parseInt(editText.getText().toString());
			if ( res == R.drawable.tv_keybord_time ) {

				if( outValue > 99 ) {
					outValue = 99;
					if( mTextView != null ) {
						mTextView.setText(outValue+"");
					}
					
				} else if(  outValue <= 99  && outValue >= 5 ) {
					if( mTextView != null ) {
						mTextView.setText(outValue+"");
					}
					
				} else if( outValue < 5 ) {
					outValue = 0;
					if( mTextView != null ) {
						mTextView.setText(outValue+"");
					}
				}

			}
			if ( res != R.drawable.tv_keybord_speed && res != R.drawable.tv_keybord_incline )
				mlayout_gender_frame.setVisibility(View.VISIBLE);
        }
		if( arg0 == caculaterdel ){
            if( editText.getText().toString().trim().length() > 0 ) {
                deleteEditValue(editText, getEditSelection(editText));
            }
//	            if( mEditText.getText().toString().trim().length() > 0 ) {
//	                deleteEditValue(mEditText, getEditSelection(mEditText));
//	            }
        }else{

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

//	            addString(mEditText, textValue);
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

    private TextView mTextView;

    private PopupWindow mPopuWindow;

    public void setRelatedAndRes(TextView mTextView, int mRes, View rlayout_gender_frame) {
    	isFirstChick = true;
        this.mTextView = mTextView;
        editText.setText(mTextView.getText().toString());
        editText.setSelection(editText.getText().length());

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
