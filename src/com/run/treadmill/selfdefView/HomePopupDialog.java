package com.run.treadmill.selfdefView;

import com.run.treadmill.BaseActivity;
import com.run.treadmill.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class HomePopupDialog implements OnClickListener {
	private final String TAG = "PopupDialog";
		
	private Context mContext = null;	
	private Dialog mDialog = null;
	private RelativeLayout layout_reset_dialog;
	private ImageView btn_reset_type = null;
	private ImageView btn_pop_yes = null;
	private ImageView btn_pop_no = null;
	private ImageView btn_pop_ok = null;
	private View mView = null;
	private int targetRes = -1;
	private boolean isShow = false;
		
	public HomePopupDialog( Context context , String tips) {
		mContext = context;		
	}
	
	public HomePopupDialog( Context context, Boolean isSetButton) {
		mContext = context;	
		initDialog(isSetButton);
	}

	public void initDialog( Boolean isSetButton ) {	

		try {
			mView = LayoutInflater.from(mContext).inflate(R.layout.activity_dialog_home, null); 

			layout_reset_dialog = (RelativeLayout) mView.findViewById(R.id.layout_reset_dialog);
			btn_reset_type = (ImageView) mView.findViewById(R.id.btn_reset_type);

	        btn_pop_yes = (ImageView) mView.findViewById(R.id.btn_pop_yes);
	        btn_pop_no = (ImageView) mView.findViewById(R.id.btn_pop_no);
	        btn_pop_ok = (ImageView) mView.findViewById(R.id.btn_pop_ok);
	        
	        OnKeyListener keyListener = new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					// TODO Auto-generated method stub									
	                return false;
				}
	        };
	              
			mDialog = new AlertDialog.Builder(mContext).create();	   		
	        mDialog.setOnKeyListener(keyListener);
			mDialog.hide();

	        layout_reset_dialog.setOnClickListener(this);
	        btn_pop_yes.setOnClickListener(this);
	        btn_pop_no.setOnClickListener(this);
	        btn_pop_ok.setOnClickListener(this);
	        
		}catch(Exception ex){
			ex.printStackTrace();
		}			                
	}
	
	/**
	 * @exception 显示对话框
	 */
	public void showToPopUpDialog( int res ) {
		btn_reset_type = (ImageView) mView.findViewById(R.id.btn_reset_type);
        if ( -1 != res ) {
        	btn_reset_type.setImageResource(res);
        	targetRes = res;
        }
        if ( null != this.mDialog ) {
        	this.mDialog.show();
        	WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
			params.width = (int)( 1280 );
			params.height = (int)( 800 );
			mDialog.getWindow().setAttributes(params);
        	mDialog.setContentView(mView);
        	isShow = true;
        }
        return ;
	}
		
	/**
	 * @exception 销毁
	 */
	public void destoryToPopUpDialog() {		
		if ( null != this.mDialog ) {			
			this.mDialog.dismiss();  
			this.mDialog = null;
			isShow = false;
		}
		return ;
	}

	/**
	 * @exception 销毁
	 */
	public void hidePopUpDialog() {		
		if ( null != this.mDialog ) {			
			this.mDialog.hide();
			isShow = false;
		}
		return ;
	}

	/**
	 * @exception 显示对话框与否
	 */
	public boolean isShowPopUpDialog() {		
		if ( null != this.mDialog ) {
			return isShow;
			/*return this.mDialog.isShowing();*/ 
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		BaseActivity activity = (BaseActivity)mContext;
		switch (v.getId()) {
			case R.id.btn_pop_yes:
//				activity.doOkBtn();
				/*activity.resetInfo(targetRes);*/
				/*hidePopUpDialog();*/
				btn_reset_type.setImageResource(R.drawable.img_pop_lube_message_2);
				btn_pop_yes.setVisibility(View.GONE);
				btn_pop_no.setVisibility(View.GONE);
				btn_pop_ok.setVisibility(View.VISIBLE);
				Log.d(TAG,"btn_pop_yes");
				break;
			case R.id.btn_pop_no:
//				activity.doNoBtn();
				Log.d(TAG,"btn_pop_no");
				hidePopUpDialog();
				activity.resetInfo(-1);
				break;
			case R.id.btn_pop_ok:
				hidePopUpDialog();
				Log.d(TAG,"hidePopUpDialog");
				activity.resetInfo(targetRes);
				btn_reset_type.setImageResource(R.drawable.img_pop_lube_message_1);
				btn_pop_yes.setVisibility(View.VISIBLE);
				btn_pop_no.setVisibility(View.VISIBLE);
				btn_pop_ok.setVisibility(View.GONE);
				break;
		}
	}
}
