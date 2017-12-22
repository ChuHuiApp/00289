package com.run.treadmill.manager;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

public class UpgradeManager {
	public final static String TAG = "UpgradeManager";	

	private static UpgradeManager ourInstance = null;	
	public static UpgradeManager getInstance() {
    	Log.d(TAG,"getInstance function!");
    	
        if ( null == ourInstance ) {
            synchronized (UpgradeManager.class) {
                if (null == ourInstance ) {
                    ourInstance = new UpgradeManager();                    
                }
            }
        }
        return ourInstance;
    }
			
	private UpgradeManager() {
	}
	
	/**
	 * @param mContext
	 * @param filePath
	 * @return
	 */
	public int getApkVersionCode(Context mContext, String filePath) {
		PackageManager packageManager = mContext.getPackageManager();
		PackageInfo packageInfo = packageManager.getPackageArchiveInfo(filePath,
				PackageManager.GET_ACTIVITIES);
		Log.d(TAG,"versionCode"+packageInfo.versionCode);
		return packageInfo.versionCode;
	}
		
    /**
     * @param mContext
     * @param path
     * @exception 安装APK文件
     */
    public void installApk(Context mContext, File apkfile) {        
		Intent apkIntent = new Intent();
		apkIntent.setAction(android.content.Intent.ACTION_VIEW);
		apkIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		apkIntent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
		mContext.startActivity(apkIntent);
    }
    
}
