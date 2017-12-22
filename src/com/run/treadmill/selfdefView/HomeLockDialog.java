package com.run.treadmill.selfdefView;

import java.util.Timer;
import java.util.TimerTask;

import com.run.treadmill.BaseActivity;
import com.run.treadmill.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnShowListener;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

public class HomeLockDialog implements OnClickListener {
	private final String TAG = "HomeLockDialog";
		
	private Context mContext = null;	
	private AlertDialog mDialog = null;
	private RelativeLayout layout_reset_dialog;
	private ImageView btn_reset_type = null;
	private EditText textview_lock;
	private View mView = null;
	private int targetRes = -1;

	private BaseActivity activity;
		
	public HomeLockDialog( Context context , String tips) {
		mContext = context;		
	}
	
	public HomeLockDialog( Context context, Boolean isSetButton) {
		mContext = context;
		activity = (BaseActivity)mContext;
		initDialog(isSetButton);
	}

	public void initDialog( Boolean isSetButton ) {	

		try {
			mView = LayoutInflater.from(mContext).inflate(R.layout.activity_dialog_lock, null); 

			layout_reset_dialog = (RelativeLayout) mView.findViewById(R.id.layout_reset_dialog);
			btn_reset_type = (ImageView) mView.findViewById(R.id.btn_reset_type);
			textview_lock = (EditText) mView.findViewById(R.id.textview_lock);
	        
	        OnKeyListener keyListener = new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					// TODO Auto-generated method stub
					Log.d(TAG,"keyCode" + keyCode);
					if ( keyCode == EditorInfo.IME_ACTION_DONE ){
						Log.d(TAG,"keyCode 1" + keyCode);
					}
	                return false;
				}
	        };

	        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(mContext);
	        mDialog = mDialogBuilder.create();
			WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
			params.width = (int)( 933 );
			params.height = (int)( 467 );
			mDialog.getWindow().setAttributes(params);


			mDialog.setView(mView);

			mDialog.setOnKeyListener(keyListener);

	        layout_reset_dialog.setOnClickListener(this);
	        
	        textview_lock.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					textview_lock.requestFocus();
					return true;
				}
			});
	        
	        
	        mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
	        mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	        ((AlertDialog) mDialog).setOnShowListener( new OnShowListener() {
	        	public void onShow(DialogInterface dialog){
	        		InputMethodManager imm= (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
	        		imm.showSoftInput(textview_lock,InputMethodManager.SHOW_IMPLICIT);
	        	}
	        });
			mDialog.show();
	        
//	        mDialog.setContentView(mView);

		}catch(Exception ex){
			ex.printStackTrace();
		}			                
	}
	public void showDialog(final Context context){  
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);  
        dialog.setTitle("请输入验证码");  
          
        final EditText et = new EditText(context);  
        et.setHint("in put your confirm code");  
          
        dialog.setView(et);//给对话框添加一个EditText输入文本框   
          
        //给对话框添加一个确定按钮，同样的方法可以添加一个取消按钮   
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
            @Override  
            public void onClick(DialogInterface arg0, int arg1) {  
                Toast.makeText(context, "提交验证码……", 3000).show();  
            }  
        });  
          
        //下面是弹出键盘的关键处   
        AlertDialog tempDialog = dialog.create();  
        tempDialog.setView(et, 0, 0, 0, 0);  
          
        ((AlertDialog) tempDialog).setOnShowListener(new OnShowListener() {  
            public void onShow(DialogInterface dialog) {  
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);  
                imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);  
            }  
        });   
          
        tempDialog.show();        
    }  

	public void showKeyboard() {  
        if( textview_lock != null ) {
        	mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        	Log.d(TAG,"showKeyboard");
            //设置可获得焦点   
        	textview_lock.setFocusable(true);  
        	textview_lock.setFocusableInTouchMode(true);  
            //请求获得焦点   
        	textview_lock.requestFocus();  
           /* //调用系统输入法   
            InputMethodManager inputManager = (InputMethodManager) textview_lock  
                    .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
            inputManager.showSoftInput(textview_lock, 0);  */
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
			params.width = (int)( 1366 );
			params.height = (int)( 768 );
			mDialog.getWindow().setAttributes(params);
        	mDialog.setContentView(mView);
        	/*Timer timer = new Timer(); 
        	timer.schedule(new TimerTask() { 
        	   
        	  @Override
        	  public void run() { 
        		  showKeyboard();
        	  } 
        	}, 200);*/
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
		}
		return ;
	}

	/**
	 * @exception 销毁
	 */
	public void hidePopUpDialog() {		
		if ( null != this.mDialog ) {			
			this.mDialog.hide(); 
		}
		return ;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.textview_lock:
				break;
			case R.id.btn_pop_no:
				Log.d(TAG,"btn_pop_no");
				hidePopUpDialog();
				break;
			case R.id.btn_pop_ok:
				hidePopUpDialog();
				Log.d(TAG,"hidePopUpDialog");
				activity.resetInfo(targetRes);
				btn_reset_type.setImageResource(R.drawable.img_pop_lube_message_1);
				break;
		}
	}

}
