package com.run.treadmill;


import java.lang.reflect.Method;
import java.util.Locale;

import com.run.treadmill.util.StorageParam;
import com.run.treadmill.util.Support;


import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class LanguageActivity extends BaseActivity implements OnClickListener {

	private String TAG = "LanguageActivity";
	private ImageView btn_language_us;
	private ImageView btn_language_cn;
	private ImageView btn_language_de;	
	private ImageView btn_language_tr;
	private ImageView btn_language_ir;
	private ImageView btn_language_esp;
	private ImageView btn_language_pt;	
	private ImageView btn_language_rus;
	
	private Locale locale;
	private Context mContext;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_language);
		
		btn_language_us = (ImageView) findViewById(R.id.btn_language_us);
		btn_language_cn = (ImageView) findViewById(R.id.btn_language_cn);
		btn_language_de = (ImageView) findViewById(R.id.btn_language_de);
		btn_language_tr = (ImageView) findViewById(R.id.btn_language_tr);
		
		btn_language_ir = (ImageView) findViewById(R.id.btn_language_ir);
		btn_language_esp = (ImageView) findViewById(R.id.btn_language_esp);
		btn_language_pt = (ImageView) findViewById(R.id.btn_language_pt);
		btn_language_rus = (ImageView) findViewById(R.id.btn_language_rus);
		
		btn_language_us.setOnClickListener(this);
		btn_language_cn.setOnClickListener(this);
		btn_language_de.setOnClickListener(this);
		btn_language_tr.setOnClickListener(this);
		btn_language_ir.setOnClickListener(this);
		btn_language_esp.setOnClickListener(this);
		btn_language_pt.setOnClickListener(this);
		btn_language_rus.setOnClickListener(this);
		locale = getResources().getConfiguration().locale;

		Log.d("LanguageActivity","Language is : "+ locale.getLanguage());
		if( locale.getLanguage().endsWith("zh") ) {
			btn_language_cn.setSelected(true);
		} else if( locale.getLanguage().endsWith("en") ) {
			btn_language_us.setSelected(true);
		} else if( locale.getLanguage().endsWith("de") ) {
			btn_language_de.setSelected(true);
		} else if( locale.getLanguage().endsWith("tr") ) {
			btn_language_tr.setSelected(true);
		} else if( locale.getLanguage().endsWith("ir") ) {
			btn_language_ir.setSelected(true);
		} else if( locale.getLanguage().endsWith("es") ) {
			btn_language_esp.setSelected(true);
		} else if( locale.getLanguage().endsWith("pt") ) {
			btn_language_pt.setSelected(true);
		} else if( locale.getLanguage().endsWith("ru") ) {
			btn_language_rus.setSelected(true);
		}
		mContext = this;
		Support.hideBottomUIMenu(this);
		/*btn_language_de.setEnabled(false);
		btn_language_tr.setEnabled(false);
		btn_language_ir.setEnabled(false);
		btn_language_esp.setEnabled(false);
		btn_language_pt.setEnabled(false);
		btn_language_rus.setEnabled(false);*/
	}

	@Override
	public void onClick(View view) {
		Support.buzzerRingOnce();
		switch (view.getId()) {
			case R.id.btn_language_us:
				SystemClock.sleep(1000);
//				LocalePicker.updateLocale(Locale.ENGLISH);
				/*setAppLanguage(mContext, 2);*/
				changeSystemLanguage60(Locale.ENGLISH);
				StorageParam.setSystemLanuage(mContext, 2);
				break;
			case R.id.btn_language_cn:
				SystemClock.sleep(1000);
//				LocalePicker.updateLocale(Locale.TRADITIONAL_CHINESE);
//				LocalePicker.updateLocale(Locale.SIMPLIFIED_CHINESE);
				/*setAppLanguage(mContext, -1);*/
				changeSystemLanguage60(Locale.SIMPLIFIED_CHINESE);
				StorageParam.setSystemLanuage(mContext, -1);
				break;
			case R.id.btn_language_de:
				SystemClock.sleep(1000);
				changeSystemLanguage60(Locale.GERMAN);
				break;
			case R.id.btn_language_tr:
				SystemClock.sleep(1000);
				changeSystemLanguage60(new Locale("tr", "TR"));
				break;
				
			case R.id.btn_language_ir:
				SystemClock.sleep(1000);
				changeSystemLanguage60(new Locale("ir", "IR"));
				break;
			case R.id.btn_language_esp:
				SystemClock.sleep(1000);
				changeSystemLanguage60(new Locale("es", "ES"));
				break;
			case R.id.btn_language_pt:
				SystemClock.sleep(1000);
				changeSystemLanguage60(new Locale("pt", "PT"));
				break;
			case R.id.btn_language_rus:
				SystemClock.sleep(1000);
				changeSystemLanguage60(new Locale("ru", "RU"));
				break;
			default:
				break;
		}
		
		this.finishActivty();
	}

	private void changeSystemLanguage60(Locale locale) {
		if (locale != null) {
	        try {
				Class classActivityManagerNative = Class.forName("android.app.ActivityManagerNative");
	            Method getDefault = classActivityManagerNative.getDeclaredMethod("getDefault");
	            Object objIActivityManager = getDefault.invoke(classActivityManagerNative);
	            Class classIActivityManager = Class.forName("android.app.IActivityManager");
	            Method getConfiguration = classIActivityManager.getDeclaredMethod("getConfiguration");
	            Configuration config = (Configuration) getConfiguration.invoke(objIActivityManager);
	            config.setLocale(locale);
	            //config.userSetLocale = true;
	            Class clzConfig = Class.forName("android.content.res.Configuration");
	            java.lang.reflect.Field userSetLocale = clzConfig.getField("userSetLocale");
	            userSetLocale.set(config, true);
	            Class[] clzParams = {Configuration.class};
	            Method updateConfiguration = classIActivityManager.getDeclaredMethod("updateConfiguration", clzParams);
	            updateConfiguration.invoke(objIActivityManager, config);
	            BackupManager.dataChanged("com.android.providers.settings");
	        } catch (Exception e) {
	            Log.d(TAG, "changeSystemLanguage: " + e.getLocalizedMessage());
	        }    
		}
	}

	/*protected void changeSystemLanguage70(LocaleList locale) {
	    if (locale != null) {
	        try {
	            Class classActivityManagerNative = Class.forName("android.app.ActivityManagerNative");
	            Method getDefault = classActivityManagerNative.getDeclaredMethod("getDefault");
	            Object objIActivityManager = getDefault.invoke(classActivityManagerNative);
	            Class classIActivityManager = Class.forName("android.app.IActivityManager");
	            Method getConfiguration = classIActivityManager.getDeclaredMethod("getConfiguration");
	            Configuration config = (Configuration) getConfiguration.invoke(objIActivityManager);
	            config.setLocales(locale);
	            Class[] clzParams = {Configuration.class};
	            Method updateConfiguration = classIActivityManager.getDeclaredMethod("updatePersistentConfiguration", clzParams);
	            updateConfiguration.invoke(objIActivityManager, config);
	        } catch (Exception e) {
	            Log.d(TAG, "changeSystemLanguage: " + e.getLocalizedMessage());
	        }
	    }
	}*/
	
	public void finishActivty() {
		/*Intent intent = new Intent();
		intent.putExtra(CTConstant.IS_FINSH, true);
		intent.putExtra(CTConstant.IS_SUSPENDED, true);
		MyUtils.animLeftFinishActivity(LanguageActivity.this, intent);*/
		/*Intent intent = new Intent(mContext, HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);*/
		
		Intent intent = new Intent(this, HomeActivity.class);  
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  
		startActivity(intent);
		// 杀死该应用进程  
		android.os.Process.killProcess(android.os.Process.myPid());  
		System.exit(0);
	}

	@Override
	public void errorEventProc(int errorValue) {
		if( errorValue == 1 ) {
			finishActivty();
		}
	}

/*	public void setAppLanguage( Context context, int languageid ) {
		Resources resources = context.getResources();//获得res资源对象  
		Configuration config = resources.getConfiguration();//获得设置对象  
		DisplayMetrics dm = resources .getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。  
		Locale RU = null;
		switch(languageid) {		
			case 1:
				config.locale = Locale.TAIWAN;              //繁体中文 
				break;
				
			case 2:
				config.locale = Locale.ENGLISH;             //英文
				break;
			
			case 3:
				RU = new Locale("ru", "RU"); //俄语
				config.locale = RU;            
				break;
				
			default:
				config.locale = Locale.SIMPLIFIED_CHINESE;   //简体中文
				break;
		}		
		resources.updateConfiguration(config, dm);
		return ;
	}*/

	@Override
	public void onCmdEvent(Message msg) {
		// TODO Auto-generated method stub
		
	}

}
