package com.run.treadmill.selfdefView;

import com.run.treadmill.BaseActivity;
import com.run.treadmill.R;
import com.run.treadmill.serialutil.Command;
import com.run.treadmill.util.Support;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Handler;
import android.os.Message;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PopupDialog implements OnClickListener {
	private final String TAG = "PopupDialog";
		
	private Context mContext = null;	
	private Dialog mDialog = null;
	private RelativeLayout layout_reset_dialog;
	private ImageView btn_reset_type = null;
	private ImageView btn_pop_yes = null;
	private ImageView btn_pop_no = null;
	private TextView text_keyvalue = null;
	private View mView = null;
	private int targetRes = -1;

	private boolean isShow = false;

	private ArrayMap<Integer, String> mKeyStrMap =  new ArrayMap<Integer, String>();
	private ArrayMap<Integer, Boolean> mIsPressMap =  new ArrayMap<Integer, Boolean>();

	private final int CLOSE_DIALOG = 1000;
	private final int SHOW_TIP_DIALOG = 1001;
		
	public PopupDialog( Context context , String tips) {
		mContext = context;
		initMap();
	}
	
	public PopupDialog( Context context, Boolean isSetButton) {
		mContext = context;	
		initDialog(isSetButton);
		initMap();
	}

	public void initDialog( Boolean isSetButton ) {	

		try {
			mView = LayoutInflater.from(mContext).inflate(R.layout.activity_dialog_pop_up, null); 

			layout_reset_dialog = (RelativeLayout) mView.findViewById(R.id.layout_reset_dialog);
			btn_reset_type = (ImageView) mView.findViewById(R.id.btn_reset_type);

			text_keyvalue = (TextView) mView.findViewById(R.id.text_keyvalue);

	        btn_pop_yes = (ImageView) mView.findViewById(R.id.btn_pop_yes);
	        btn_pop_no = (ImageView) mView.findViewById(R.id.btn_pop_no);
	        
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
	        
		}catch(Exception ex){
			ex.printStackTrace();
		}			                
	}

	/**
	 * @exception 显示对话框
	 */
	public void showToPopUpDialog( int res ) {
		btn_reset_type = (ImageView) mView.findViewById(R.id.btn_reset_type);
        if ( R.drawable.img_pop_key_test == res ) {
        	btn_pop_yes.setVisibility(View.GONE);
        	btn_pop_no.setVisibility(View.GONE);
        	text_keyvalue.setVisibility(View.VISIBLE);
        	text_keyvalue.setText("");
        	btn_reset_type.setImageResource(res);
        	targetRes = res;
        } else if ( R.drawable.img_pop_factory_reset == res ) {
        	btn_pop_yes.setVisibility(View.VISIBLE);
        	btn_pop_no.setVisibility(View.VISIBLE);
        	text_keyvalue.setVisibility(View.GONE);
        	btn_reset_type.setImageResource(res);
        	targetRes = res;
        } else {
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

	//显示按键值
	public void showToKeyIcon(int value ) {
		if( mDialog.isShowing() && btn_reset_type.getDrawable().getConstantState().equals(
				mContext.getResources().getDrawable(R.drawable.img_pop_key_test).getConstantState()) ) {
			Log.d(TAG,"mKeyStrMap.get(value) " + mKeyStrMap.get(value));
			text_keyvalue.setText(mKeyStrMap.get(value));
			mIsPressMap.put(value, true);
			if ( isAllDone() ) {
				mHandler.sendEmptyMessageDelayed(SHOW_TIP_DIALOG, 300);
				mHandler.sendEmptyMessageDelayed(CLOSE_DIALOG, 3000);
			}
		}
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
	 * @exception 显示对话框与否
	 */
	public boolean isShowPopUpDialog() {		
		if ( null != this.mDialog ) {
			return isShow;
			/*return this.mDialog.isShowing();*/ 
		}
		return false;
	}

	/**
	 * @exception 销毁
	 */
	public void hidePopUpDialog() {		
		if ( null != this.mDialog ) {			
			this.mDialog.hide(); 
			isShow = false;
		}
		for ( ArrayMap.Entry<Integer, Boolean> entry : mIsPressMap.entrySet() ) {
			Log.d(TAG, "destoryToPopUpDialog key= " + entry.getKey() + " and value= " + entry.getValue());
			mIsPressMap.put(entry.getKey(), false);
		}
		return ;
	}

	@Override
	public void onClick(View v) {
		BaseActivity activity = (BaseActivity)mContext;
		Support.buzzerRingOnce();
		switch (v.getId()) {
			case R.id.btn_pop_yes:
//				activity.doOkBtn();
				activity.resetInfo(targetRes);
				hidePopUpDialog();
				Log.d(TAG,"btn_pop_yes");
				break;
			case R.id.btn_pop_no:
//				activity.doNoBtn();
				Log.d(TAG,"btn_pop_no");
				hidePopUpDialog();
				break;
			case R.id.layout_reset_dialog:
				if ( targetRes == R.drawable.img_pop_key_test ) {
					/*showToKeyIcon(Command.KEY_CMD_QUICK_START);*/
					if ( isAllDone() ) {
						hidePopUpDialog();
					}
				}
				/*hidePopUpDialog();*/
				Log.d(TAG,"hidePopUpDialog");
				break;
		}
	}

	public void initMap() {
		mKeyStrMap.put(Command.KEY_CMD_QUICK_START, "Start");
		mKeyStrMap.put(Command.KEY_CMD_STOP_CANCEL, "Stop");
		mKeyStrMap.put(Command.KEY_CMD_LEVEL_DEC_F, "Level -");
		mKeyStrMap.put(Command.KEY_CMD_LEVEL_DEC_S, "Level -");
		mKeyStrMap.put(Command.KEY_CMD_LEVEL_PLUS_F, "Level +");
		mKeyStrMap.put(Command.KEY_CMD_LEVEL_PLUS_S, "Level +");
		
		mIsPressMap.put(Command.KEY_CMD_QUICK_START, false);
		mIsPressMap.put(Command.KEY_CMD_STOP_CANCEL, false);
		mIsPressMap.put(Command.KEY_CMD_LEVEL_DEC_F, false);
		mIsPressMap.put(Command.KEY_CMD_LEVEL_DEC_S, false);
		mIsPressMap.put(Command.KEY_CMD_LEVEL_PLUS_F, false);
		mIsPressMap.put(Command.KEY_CMD_LEVEL_PLUS_S, false);
	}

	public boolean isAllDone() {
		for ( ArrayMap.Entry<Integer, Boolean> entry : mIsPressMap.entrySet() ) {
			Log.d(TAG, "isAllDone key= " + entry.getKey() + " and value= " + entry.getValue());
			if ( entry.getValue() != true ) {
				return false;
			}
		}
		return true;
	}

	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CLOSE_DIALOG:
                	if ( targetRes == R.drawable.img_pop_key_test ) {
                		hidePopUpDialog();
					}
                    break;
                case SHOW_TIP_DIALOG:
                	text_keyvalue.setText(R.string.string_factory2_alldone);
                	break;
                default:
                    break;
            }
        }
    };

}
