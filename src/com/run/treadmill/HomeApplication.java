package com.run.treadmill;

import java.util.Locale;

import com.run.treadmill.manager.SelfAudioManager;
import com.run.treadmill.manager.StorageStateManager;
import com.run.treadmill.manager.SysSoundCheck;
import com.run.treadmill.serialutil.SerialUtils;
import com.run.treadmill.util.StorageParam;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

public class HomeApplication extends Application {

	public Context mContext;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("HomeApplication","onCreate");
		mContext = this;
		/*int sysLang = StorageParam.getSystemLanuage(mContext);
		setAppLanguage(mContext, sysLang);*/
		SysSoundCheck.getInstance(this).initSysSoundOut();
		StorageStateManager.getInstance().registerReceiver(this);
		SelfAudioManager.getInstance(this).setEffectsEnabled();
		SerialUtils.getInstance().init(mContext);
	}

	/*public void setAppLanguage( Context context, int languageid ) {
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
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
       /* MultiDex.install(this);*/
    }

}
