package com.run.treadmill.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class VersionManager {	
	
	/**
    * @param 
    * @explain 获取App版本号
    */
	public static String getAppVersionName(Context context) {  
	    String versionName = "";  
	    try {  
	        // ---get the package info---  
	        PackageManager pm = context.getPackageManager();  
	        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);  
	        versionName = pi.versionName;  	        
	        if (versionName == null || versionName.length() <= 0) {  
	            return "";  
	        }  
	    } catch (Exception e) {  
	        Log.e("VersionInfo", "Exception", e);  
	    }  
	    return versionName;  
	}

	/**
	 * @return 获取SDK版本号
	 */
	public static String getSdkVersion(){
		String versionOS = android.os.Build.VERSION.RELEASE;
		return versionOS;
	}
		
	/**
	 * @return 获取当前系统的版本号
	 */
	public static String getfirewareVersion(){
		String versionOS = android.os.Build.VERSION.INCREMENTAL + "V";

		return versionOS;
	}
	
	/**
	 * @return 返回当前系统的android版本号
	 */
	public static String getSDKVersion() {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT; 		
		return String.valueOf(currentapiVersion);
	}
	
	/** 
	* CORE-VER 
	* 内核版本 
	* return String 
	*/  	  
	public static String getLinuxCore_Ver() {  
		Process process = null;  
		String kernelVersion = "";  
		try {  
			process = Runtime.getRuntime().exec("cat /proc/version");  
		} catch (IOException e) {  
			// TODO Auto-generated catch block  
			e.printStackTrace();  
		} 
		  
		// get the output line  
		InputStream outs = process.getInputStream();  
		InputStreamReader isrout = new InputStreamReader(outs);  
		BufferedReader brout = new BufferedReader(isrout, 8 * 1024);  
		  		  
		String result = "";  
		String line;  
		// get the whole standard output string  
		try {  
			while ((line = brout.readLine()) != null) {  
				result += line;  
			}  
		} catch (IOException e) {  
			// TODO Auto-generated catch block  
			e.printStackTrace();  
		}  
		  		  
		try {  
			if (result != "") {  
				String Keyword = "version ";  
				int index = result.indexOf(Keyword);  
				line = result.substring(index + Keyword.length());  
				index = line.indexOf(" ");  
				kernelVersion = line.substring(0, index);  
			}  
		} catch (IndexOutOfBoundsException e) {  
			e.printStackTrace();  
		}  
		return kernelVersion; 
	}
		
}
