package com.run.treadmill.manager;

import java.util.Stack;


import android.annotation.SuppressLint;
import android.app.Activity;


public class ScreenManager { 
	//private static final String TAG = "ScreenManager" ;
    
	private static Stack<Activity> activityStack;     
    private static ScreenManager instance;

    private  ScreenManager() {     
    }

    public static ScreenManager getScreenManager(){     
        if(instance==null){
            instance=new ScreenManager();     
        }     
        return instance;     
    }
    
	/**
	 * @exception 切换至主界面
	 */
	@SuppressLint("NewApi") 
	public void skipToDisplayActivity(String activityName) {
		Activity activity = currentActivity();

		while (!activity.getClass().toString().contains("HomeActivity")) {
			activity = currentActivity();
			if (activity == null) {
				break;
			}
			if (activity.getClass().toString().contains(activityName)) {
//				activity.sh;
				break;
			}
			popActivity(activity);
		}
		return ;
	}
    
    public Activity getDisplayActivity()
    {
    	Activity activity=currentActivity();
    	
    	return activity;
    }
    
    public void popActivity(Activity activity){     
        if(activity!=null){    
            activityStack.remove(activity);     
            activity=null;     
        }     
    }     
    public Activity currentActivity(){     
        Activity activity=activityStack.lastElement();     
        return activity;     
    }     
    public void pushActivity(Activity activity){     
        if(activityStack==null){     
            activityStack=new Stack<Activity>();     
        }     
        activityStack.add(activity);     
    }     
    
}    
