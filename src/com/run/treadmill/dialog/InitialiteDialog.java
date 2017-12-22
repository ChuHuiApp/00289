package com.run.treadmill.dialog;

import java.util.ArrayList;
import java.util.List;

import com.run.treadmill.R;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

public class InitialiteDialog extends Dialog {
	private Context mContext;

	public InitialiteDialog(Context context) {
		super(context, R.style.dialog_notitlebar);
		this.mContext = context;
	}

	public void showDialog() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.dialog_initialite);
		getWindow().setWindowAnimations(R.style.load_anim);
		
		show();
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if (keyCode == KeyEvent.KEYCODE_BACK && KeyEvent.ACTION_UP == event.getAction()) {
			dismiss();
		}
		return super.dispatchKeyEvent(event);
	}
	
	public void showInitFail() {
		
	}
}
