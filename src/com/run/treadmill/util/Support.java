package com.run.treadmill.util;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.run.treadmill.R;
import com.run.treadmill.db.UserInfoManager;
import com.run.treadmill.manager.SysSoundCheck;
import com.run.treadmill.serialutil.SerialUtils;
import com.run.treadmill.util.CTConstant.MediaENItem;
import com.run.treadmill.util.CTConstant.MediaItem;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @Description.公共支撑方法
 * @version. 1.0
 * @Author. tianjj
 * @History.Created on 2016-7-6 
 */
public class Support {	
	private static final String TAG = "Support";
	private static final int BUFFERSPACE = 1880;
	
    /**
     * 隐藏虚拟按键，并且全屏
     */
    public static void hideBottomUIMenu(Activity mActivity) {
    	//隐藏虚拟按键，并且全屏
    	if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
    		View v = mActivity.getWindow().getDecorView();
    		v.setSystemUiVisibility(View.GONE);
    	} else if (Build.VERSION.SDK_INT >= 19) {
    		//for new api versions.
    		View decorView = mActivity.getWindow().getDecorView();
    		int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
    		decorView.setSystemUiVisibility(uiOptions);
    	}
    }


    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= 19 && activity.checkCallingPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
 
            return false;
        }
 
        return true;
    }

    public static void buzzerRingOnce() {
    	SerialUtils.getInstance().setbuzzerOn();
    }

    public static void keyToneOnce() {
    	SerialUtils.getInstance().setkeyToneOn();
    }

    private static Long PreTime = 0l;
    private static Long CurTime = 0l;
    private static int preKetType = -1;

    public static boolean isLongKeyEvent( int keyType ) {
    	CurTime = System.currentTimeMillis();
    	Log.d(TAG,"isLongKeyEvent diff = " + (CurTime - PreTime));
    	if ( ( CurTime - PreTime ) < 260 && preKetType == keyType ) {
    		PreTime = CurTime;
    		return true;
    	} else {
    		PreTime = CurTime;
    		preKetType = keyType;
    		return false;
    	}
    }

    public static int keyCount = 0;
    public static boolean isDoubleCount( int keyType ) {
    	keyCount ++ ;
    	if ( keyCount % 2 == 0 ) {
    		keyCount = 0;
    		return true;
    	} else {
    		return false;
    	}
    }

    public static void doStartApplicationWithPackageName(Context context, String packagename, String className) {
    	Intent intent = new Intent(Intent.ACTION_MAIN);  
    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		ComponentName cn = new ComponentName(packagename, className);              
		intent.setComponent(cn);  
		context.startActivity(intent);  
    }

    public static int findPid(Context context, String packagename) {
    	ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    	List<ActivityManager.RunningAppProcessInfo> mRunningProcess = mActivityManager.getRunningAppProcesses();
    	int i = 1;
    	for (ActivityManager.RunningAppProcessInfo amProcess : mRunningProcess) {
    		Log.i("Application", (i++) + "PID: " + 
        			amProcess.pid
        			+ "(processName=" + amProcess.processName + 
        			"UID="+amProcess.uid+")");
    		if ( amProcess.processName.contains(packagename) )
    			return amProcess.pid;
    	}
    	return -1;
    }

    public static void killThirdApp(String lang, Context mContext, int mediaMode) {
    	if ( !lang.endsWith("zh") ) {

    		killInputmethodPid(mContext, "com.google.android.inputmethod.pinyin");
    		if ( CTConstant.mediaENPkgName[mediaMode].contains("com.instagram.android") ) {
    			killCommonApp(mContext, CTConstant.mediaENPkgName[mediaMode]);
    			killFacebookApp(mContext, "com.facebook.katana");
    		} else {
    			killCommonApp(mContext, CTConstant.mediaENPkgName[mediaMode]);
    		}
    		if( CTConstant.mediaENPkgName[mediaMode].contains("com.android.cameraSelf") ) {
        		SysSoundCheck.getInstance(mContext).initSysSoundOut();
        	}

		} else {
			Log.d(TAG, "killThirdApp PID = " + Support.findPid(mContext, 
					CTConstant.mediaPkgName[mediaMode]));

			killInputmethodPid(mContext, "com.google.android.inputmethod.pinyin");
			if ( CTConstant.mediaPkgName[mediaMode].contains("com.softwinner.fireplayer") ) {
				killCommonApp(mContext, CTConstant.mediaPkgName[mediaMode]);
    		} else {
    			killCommonApp(mContext, CTConstant.mediaPkgName[mediaMode]);
    		}
			if( CTConstant.mediaPkgName[mediaMode].contains("com.android.cameraSelf") ) {
        		SysSoundCheck.getInstance(mContext).initSysSoundOut();
        	}
		}
    }

    public static int killInputmethodPid(Context mContext, String packagename) {
    	ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
    	List<ActivityManager.RunningAppProcessInfo> mRunningProcess = mActivityManager.getRunningAppProcesses();
    	int i = 1;
    	for (ActivityManager.RunningAppProcessInfo amProcess : mRunningProcess) {
    		if ( amProcess.processName.contains(packagename) ) {
    			Log.i("Application", (i++) + "PID: " + 
            			amProcess.pid
            			+ "(processName=" + amProcess.processName + 
            			"UID="+amProcess.uid+")");
    			/*ShellCmdUtils.getInstance().execCommand("kill " + amProcess.pid);*/
    			killCommonApp(mContext, packagename);
    		}
    	}
    	return -1;
    }

    public static int killFacebookPid(Context context, String packagename) {
    	ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    	List<ActivityManager.RunningAppProcessInfo> mRunningProcess = mActivityManager.getRunningAppProcesses();
    	int i = 1;
    	int count = 30;
    	while( (count--) > 0 ) {
    		for (ActivityManager.RunningAppProcessInfo amProcess : mRunningProcess) {
        		if ( amProcess.processName.contains(packagename) ) {
        			/*Log.i("Application", (i++) + "PID: " + 
                			amProcess.pid
                			+ "(processName=" + amProcess.processName + 
                			"UID="+amProcess.uid+")");*/
        			/*ShellCmdUtils.getInstance().execCommand("kill " + amProcess.pid);*/
        		}
        	}
    	}
    	/*ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);*/
        /*am.killBackgroundProcesses(CTConstant.mediaPkgName[UserInfoManager.getInstance().mediaMode]);
        am.forceStopPackage("com.android.music");*/
    	return -1;
    }

    public static int killFacebookApp(Context mContext, String packagename) {
    	ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
    	List<ActivityManager.RunningAppProcessInfo> mRunningProcess = mActivityManager.getRunningAppProcesses();
    	int i = 1;
    	for (ActivityManager.RunningAppProcessInfo amProcess : mRunningProcess) {
    		if ( amProcess.processName.contains(packagename) ) {
    			Log.i("Application", (i++) + "PID: " + 
            			amProcess.pid
            			+ "(processName=" + amProcess.processName + 
            			"UID="+amProcess.uid+")");
    			/*ShellCmdUtils.getInstance().execCommand("kill " + amProcess.pid);*/
    			killCommonApp(mContext, packagename);
    		}
    	}
    	return -1;
    }

    public static void killCommonApp(Context context, String packagename) {
    	ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        
        try {  
        	Method forceStopPackage = am.getClass().getDeclaredMethod("forceStopPackage", String.class);  
        	forceStopPackage.setAccessible(true);  
        	forceStopPackage.invoke(am, packagename);  
        } catch (NoSuchMethodException e) {  
        	e.printStackTrace();  
        } catch (IllegalAccessException e) {  
        	e.printStackTrace();  
        } catch (IllegalArgumentException e) {  
        	e.printStackTrace();  
        } catch (InvocationTargetException e) {  
        	e.printStackTrace();  
        }
    }

    public static void doStartApplicationWithPackageName(Context context, String packagename) {

    	//启动apk无法启动时的，特例
    	if( packagename.contains("com.instagram.android") ) {
    		context.startActivity(context.getPackageManager().
    				getLaunchIntentForPackage("com.instagram.android"));
    		return ;
    	}
    	//进入HDMI_IN时，开启声音IO
    	if( packagename.contains("com.android.cameraSelf") ) {
    		SysSoundCheck.getInstance(context).hdmiSoundOut();
    	}
		// 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
		PackageInfo packageinfo = null;
		try {
			packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (packageinfo == null) {
			return;
		}

		// 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageinfo.packageName);

		// 通过getPackageManager()的queryIntentActivities方法遍历
		List<ResolveInfo> resolveinfoList = context.getPackageManager()
				.queryIntentActivities(resolveIntent, 0);

		ResolveInfo resolveinfo = resolveinfoList.iterator().next();
		if (resolveinfo != null) {
			// packagename = 参数packname
			String packageName = resolveinfo.activityInfo.packageName;
			// 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
			String className = resolveinfo.activityInfo.name;
			// LAUNCHER Intent
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);

			// 设置ComponentName参数1:packagename参数2:MainActivity路径
			ComponentName cn = new ComponentName(packageName, className);
			Log.d(TAG," packageName " + packageName + " className " +  className);

			intent.setComponent(cn);
			context.startActivity(intent);
		}
	}

    // 半角转全角： 
    public static String ToSBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i< c.length; i++) {
			if (c[i] == 32) {
				c[i] = (char) 12288;
				continue; 
			}
			if (c[i]< 127)
				c[i] = (char) (c[i] + 65248);
		}
		return new String(c);
	}

    /**   
     * 全角转换为半角   
     *    
     * @param input   
     * @return   
     */   
    public static String ToDBC(String input) {    
        char[] c = input.toCharArray();    
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375) {
            	 c[i] = (char) (c[i] - 65248);
            }
        }
        return new String(c);
    }

    /**   
     * 去除特殊字符或将所有中文标号替换为英文标号   
     *    
     * @param str   
     * @return   
     */   
    public static String stringFilter(String str) {    
        str = str.replaceAll("【", "[").replaceAll("】", "]")    
                .replaceAll("！", "!").replaceAll("：", ":");//替换中文标号    
        String regEx = "[『』]"; // 清除掉特殊字符    
        Pattern p = Pattern.compile(regEx);    
        Matcher m = p.matcher(str);    
        return m.replaceAll("").trim();    
    }

    public static void setLogoIcon(Context mContext, ImageView btn_ergo) {
    	if( !StorageParam.getisInnerLogo(mContext) && 
    			Support.isCheckExist(mContext.getFilesDir() + "/" + "logo.png")) {
    		Bitmap bmpDefaultPic;
			bmpDefaultPic = BitmapFactory.decodeFile(mContext.getFilesDir() + "/" + "logo.png",null);
			btn_ergo.setImageBitmap(bmpDefaultPic);
			/*if( bmpDefaultPic != null && !bmpDefaultPic.isRecycled() ) {
				bmpDefaultPic.recycle();
				bmpDefaultPic = null;
			}*/
			bmpDefaultPic = null;
		} else {
			btn_ergo.setImageResource(R.drawable.btn_ergo);
		}
    }

	/**
	 * 播放音乐
	 */
    public static void MusicPause(Context mContext) {
		String PAUSE_ACTION = "com.android.music.musicservicecommand.pause";
		Intent intent = new Intent();
		intent.setAction(PAUSE_ACTION);
		intent.putExtra("command", "pause");
		mContext.sendBroadcast(intent);
	}
	
	//逆序CRC计算
	private static int g_McRctable_16[] = {
		0x0000, 0x8005, 0x800F, 0x000A, 0x801B, 0x001E, 0x0014, 0x8011, 
		0x8033, 0x0036, 0x003C, 0x8039, 0x0028, 0x802D, 0x8027, 0x0022, 
		0x8063, 0x0066, 0x006C, 0x8069, 0x0078, 0x807D, 0x8077, 0x0072, 
		0x0050, 0x8055, 0x805F, 0x005A, 0x804B, 0x004E, 0x0044, 0x8041, 
		0x80C3, 0x00C6, 0x00CC, 0x80C9, 0x00D8, 0x80DD, 0x80D7, 0x00D2, 
		0x00F0, 0x80F5, 0x80FF, 0x00FA, 0x80EB, 0x00EE, 0x00E4, 0x80E1, 
		0x00A0, 0x80A5, 0x80AF, 0x00AA, 0x80BB, 0x00BE, 0x00B4, 0x80B1, 
		0x8093, 0x0096, 0x009C, 0x8099, 0x0088, 0x808D, 0x8087, 0x0082, 
		0x8183, 0x0186, 0x018C, 0x8189, 0x0198, 0x819D, 0x8197, 0x0192, 
		0x01B0, 0x81B5, 0x81BF, 0x01BA, 0x81AB, 0x01AE, 0x01A4, 0x81A1, 
		0x01E0, 0x81E5, 0x81EF, 0x01EA, 0x81FB, 0x01FE, 0x01F4, 0x81F1, 
		0x81D3, 0x01D6, 0x01DC, 0x81D9, 0x01C8, 0x81CD, 0x81C7, 0x01C2, 
		0x0140, 0x8145, 0x814F, 0x014A, 0x815B, 0x015E, 0x0154, 0x8151, 
		0x8173, 0x0176, 0x017C, 0x8179, 0x0168, 0x816D, 0x8167, 0x0162, 
		0x8123, 0x0126, 0x012C, 0x8129, 0x0138, 0x813D, 0x8137, 0x0132, 
		0x0110, 0x8115, 0x811F, 0x011A, 0x810B, 0x010E, 0x0104, 0x8101, 
		0x8303, 0x0306, 0x030C, 0x8309, 0x0318, 0x831D, 0x8317, 0x0312, 
		0x0330, 0x8335, 0x833F, 0x033A, 0x832B, 0x032E, 0x0324, 0x8321, 
		0x0360, 0x8365, 0x836F, 0x036A, 0x837B, 0x037E, 0x0374, 0x8371, 
		0x8353, 0x0356, 0x035C, 0x8359, 0x0348, 0x834D, 0x8347, 0x0342, 
		0x03C0, 0x83C5, 0x83CF, 0x03CA, 0x83DB, 0x03DE, 0x03D4, 0x83D1, 
		0x83F3, 0x03F6, 0x03FC, 0x83F9, 0x03E8, 0x83ED, 0x83E7, 0x03E2, 
		0x83A3, 0x03A6, 0x03AC, 0x83A9, 0x03B8, 0x83BD, 0x83B7, 0x03B2, 
		0x0390, 0x8395, 0x839F, 0x039A, 0x838B, 0x038E, 0x0384, 0x8381, 
		0x0280, 0x8285, 0x828F, 0x028A, 0x829B, 0x029E, 0x0294, 0x8291, 
		0x82B3, 0x02B6, 0x02BC, 0x82B9, 0x02A8, 0x82AD, 0x82A7, 0x02A2, 
		0x82E3, 0x02E6, 0x02EC, 0x82E9, 0x02F8, 0x82FD, 0x82F7, 0x02F2, 
		0x02D0, 0x82D5, 0x82DF, 0x02DA, 0x82CB, 0x02CE, 0x02C4, 0x82C1, 
		0x8243, 0x0246, 0x024C, 0x8249, 0x0258, 0x825D, 0x8257, 0x0252, 
		0x0270, 0x8275, 0x827F, 0x027A, 0x826B, 0x026E, 0x0264, 0x8261, 
		0x0220, 0x8225, 0x822F, 0x022A, 0x823B, 0x023E, 0x0234, 0x8231, 
		0x8213, 0x0216, 0x021C, 0x8219, 0x0208, 0x820D, 0x8207, 0x0202, 		
	};

    /**
     * @param ary
     * @param startpos, offset
     * @return subbyte sum
     */
    public static short subByteSum(byte[] ary, int startpos, int offset ) {
    	short value = 0;
    	for ( int i = 0; i < offset; i++ )  {
    		value += ((short) ary[startpos+i] & 0xFF );
    	}	
    	return value;
    }

    /**
     * @param byte ary
     * @return zu he byte array
     */
	public static short addBytes(byte[] data1, byte[] data2, byte[] data3, byte[] datatag) {
	    System.arraycopy(data1, 0, datatag, 0, data1.length);
	    System.arraycopy(data2, 0, datatag, data1.length, data2.length);
	    System.arraycopy(data3, 0, datatag, data1.length + data2.length, data3.length);
	    return (short) (data1.length + data2.length + data3.length) ;
	}

 	private static final String acceptableSchemes[] = {  
        "http:",  
        "https:",  
        "file:"  
 	}; 
	
	 /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。
     * @param value
     *            要转换的int值
     * @return byte数组
     */
    public static byte[] intToBytesLitter(int value) {
        byte[] byte_src = new byte[4];
        byte_src[3] = (byte) ((value & 0xFF000000)>>24);
        byte_src[2] = (byte) ((value & 0x00FF0000)>>16);
        byte_src[1] = (byte) ((value & 0x0000FF00)>>8);
        byte_src[0] = (byte) ((value & 0x000000FF));
        return byte_src;
    }
    
    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。
     * @param value
     *            要转换的int值
     * @return byte数组
     */
    public static byte[] intToBytesLitters(int value,byte[] bytes,int offset) {        
    	bytes[3+offset] = (byte) ((value & 0xFF000000)>>24);
        bytes[2+offset] = (byte) ((value & 0x00FF0000)>>16);
        bytes[1+offset] = (byte) ((value & 0x0000FF00)>>8);
        bytes[0+offset] = (byte) ((value & 0x000000FF));
        return bytes;
    }
    
    
    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。
     * @param value
     *            要转换的int值
     * @return byte数组
     */
    public static byte[] intToBytesBigs(int value,byte[] bytes,int offset) {       
        bytes[3+offset] = (byte) ((value & 0xFF000000)>>24);
        bytes[2+offset] = (byte) ((value & 0x00FF0000)>>16);
        bytes[1+offset] = (byte) ((value & 0x0000FF00)>>8);
        bytes[0+offset] = (byte) ((value & 0x000000FF));
        return bytes;
    }

    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。
     * @param value
     *            要转换的int值
     * @return byte数组
     */
    public static byte[] intToBytesBig(int value) {
        byte[] byte_src = new byte[4];
        byte_src[0] = (byte) ((value & 0xFF000000)>>24);
        byte_src[1] = (byte) ((value & 0x00FF0000)>>16);
        byte_src[2] = (byte) ((value & 0x0000FF00)>>8);
        byte_src[3] = (byte) ((value & 0x000000FF));
        return byte_src;
    }
    
    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。
     * @param value
     *            要转换的int值
     * @return byte数组
     */
    public static byte[] intToBytesBig(int value,byte[] bytes, int offset) {        
        bytes[offset+0] = (byte) ((value & 0xFF000000)>>24);
        bytes[offset+1] = (byte) ((value & 0x00FF0000)>>16);
        bytes[offset+2] = (byte) ((value & 0x0000FF00)>>8);
        bytes[offset+3] = (byte) ((value & 0x000000FF));
        return bytes;
    }
    
    /**
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序。
     *
     * @param ary
     *            byte数组
     * @param offset
     *            从数组的第offset位开始
     * @return int数值
     */
    public static int bytesToIntLitter(byte[] ary, int offset) {
        int value;
        value = (int) ((ary[offset]&0xFF)
                | ((ary[offset+1]<<8) & 0xFF00)
                | ((ary[offset+2]<<16)& 0xFF0000)
                | ((ary[offset+3]<<24) & 0xFF000000));
        return value;
    }

    /**
     * byte数组中取int数值，本方法适用于(高位在前，低位在后)的顺序。
     *
     * @param ary
     *            byte数组
     * @param offset
     *            从数组的第offset位开始
     * @return int数值
     */
    public static int bytesToIntBig(byte[] ary, int offset){
        int value;
        value = (int) ((ary[offset+3]&0xFF)
                | ((ary[offset+2]<<8) & 0xFF00)
                | ((ary[offset+1]<<16)& 0xFF0000)
                | ((ary[offset+0]<<24) & 0xFF000000));
        return value;
    }
    
    /**
     * @param ary
     * @param offset
     * @return
     */
    public static int bytesToIntBigPluse(byte[] ary, int offset) {
    	int value;
        value = (int) ((ary[offset+2]&0xFF)
                | ((ary[offset+3]<<8) & 0xFF00)                
                | ((ary[offset+0]<<16)& 0xFF0000)
                | ((ary[offset+1]<<24) & 0xFF000000));
        return value;
    }
    
    /**
     * @param ary
     * @param offset
     * @return 4个字节数组转long型
     */
    public static long bytesToLongLitter(byte[] ary, int offset) {
        long value;
        value = (long) ((ary[offset]&0x0FF)
                | ((ary[offset+1]<<8) & 0x0FF00)
                | ((ary[offset+2]<<16)& 0x0FF0000)
                | ((ary[offset+3]<<24) & 0x0FF000000));
        return value;
    }
    
    /**
     * @param ary
     * @param offset
     * @return 4个字节数组转long型
     */
    public static long bytesToLongBit(byte[] ary, int offset) {
    	long value ;
    	value = (long) ((ary[offset+3]&0x0FF)
                | ((ary[offset+2]<<8) & 0x0FF00)
                | ((ary[offset+1]<<16)& 0x0FF0000)
                | ((ary[offset+0]<<24) & 0x0FF000000));    	
    	return value;
    }
    
    
    /** 
    * @param byte[]
    * @return int
    */  
    public static int byteArrayToInt(byte[] b) {  
        byte[] a = new byte[4];  
        int i = a.length - 1,j = b.length - 1;  
        for (; i >= 0 ; i--,j--) {//从b的尾部(即int值的低位)开始copy数据  
            if(j >= 0)  
                a[i] = b[j];  
            else  
                a[i] = 0;//如果b.length不足4,则将高位补0  
      }  
        int v0 = (a[0] & 0xff) << 24;//&0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位  
        int v1 = (a[1] & 0xff) << 16;  
        int v2 = (a[2] & 0xff) << 8;  
        int v3 = (a[3] & 0xff) ;  
        return v0 + v1 + v2 + v3;  
    }
    
    
    /**
     * @param ary
     * @param offset
     * @return byte数组转long类型
     */
    public static int bytesToIntLowerbyte(byte[] ary, int offset) {
    	 int value = 0;    	 
         int s1 = ary[1+offset] & 0x0ff;// 最低位 
         int s3 = ary[3+offset] & 0x0ff; 
         int s5 = ary[5+offset] & 0x0ff;// 最低位 
         int s7 = ary[7+offset] & 0x0ff; 
  
         // s7不变 
         s5 <<= 8 * 1;  
         s3 <<= 8 * 2; 
         s1 <<= 8 * 3;
         value = (s1 | s3 | s5 | s7) ; 
         return value;
    }
    

    /**
     * 将short数值转换为占2个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。
     * @param value 要转换的short值
     * @return byte数组
     */
    public static byte[] shortToBytes(short value) {
        byte[] byte_src = new byte[2];
        byte_src[1] = (byte) ((value & 0xFF00)>>8);
        byte_src[0] = (byte) ((value & 0x00FF));
        return byte_src;
    }
    
    /**
     * 将short数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。
     * @param value 要转换的short值
     * @return byte数组
     */
    public static byte[] shortToBytesBig(short value) {
        byte[] byte_src = new byte[2];
        byte_src[0] = (byte) ((value & 0xFF00)>>8);
        byte_src[1] = (byte) ((value & 0x00FF));
        return byte_src;
    }
    
    /**
     * 将short数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。
     * @param value 要转换的short值
     * @return byte数组
     */
    public static byte[] shortToBytesBig(short value,byte[] bytes,int offset) {        
    	bytes[offset] = (byte) ((value & 0xFF00)>>8);
    	bytes[offset+1] = (byte) ((value & 0x00FF));                
        return bytes;
    }
    
    /**
     * 将short数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。
     * @param value 要转换的short值
     * @return byte数组
     */
    public static byte[] shortToBytesLiter(short value,byte[] bytes,int offset) {    
    	bytes[offset] = (byte) ((value & 0x00FF)); 
    	bytes[offset+1] = (byte) ((value & 0xFF00)>>8);    	               
        return bytes;
    }
    
    /**
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序。
     * 小端
     * @param ary
     *            byte数组
     * @param offset
     *            从数组的第offset位开始
     * @return int数值
     */
    public static short bytesToShortLiterEnd(byte[] ary, int offset) {
        short value;
        value = (short) ((ary[offset]&0xFF)
                | ((ary[offset+1]<<8) & 0xFF00));
        return value;
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序。
     * 小端
     * @param ary
     *            byte数组
     * @param offset
     *            从数组的第offset位开始
     * @return int数值
     */
    public static int doubleBytesToIntLiterEnd(byte[] ary, int offset) {
        int value;
        value = (int) ((ary[offset]&0xFF)
                | ((ary[offset+1]<<8) & 0xFF00) 
                | 0x00000000 );
        return value;
    }

    /**
     * byte字节型转换为int型
     * 小端
     * @param ary
     *            byte
     * @return int数值
     */
    public static int byteToInt(byte ary) {
        int value;
        value = (int) ( (ary & 0xFF)
                | 0x00000000 );
        return value;
    }

    /**
     * int low bit转换为byte字节型
     * 小端
     * @param int 
     * @return byte数值
     */
    public static byte intLowToByte(int ary) {
        byte value;
        value = (byte) (ary & 0xFF);
        return value;
    }

    /**
     * byte数组中取int数值，本方法适用于(高位在前，低位在后)的顺序。
     * 大端
     * @param ary
     *            byte数组
     * @param offset
     *            从数组的第offset位开始
     * @return int数值
     */
    public static int bytesToShortBigEnd(byte[] ary, int offset) {
        int value;
        value = (short) ((ary[offset+1]&0x0FF)
                | ((ary[offset]<<8) & 0x0FF00));
        return value;
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        for (int i = begin; i < begin+count; i++) bs[i-begin] = src[i];
        return bs;
    }
        
    /**
     * @param b byte字节数
     * @explain 把byte转为字符串的bit
     */ 
    public static String byteToBit(byte b) {  
        return ""  
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)  
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)  
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)  
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);  
    } 
    
    /**
    * @param src:校验数据起始位; length:需要校验的数据的字节长度
    * @explain 生成CRC校验short数据
    */   
  /*  public static short getCRC16Default(byte[] src,int length) {
    	int i,j;
    	int sCRCReg = 0xFFFF;
    	int sCurVal = 0;
    	
    	for ( i = 0; i < length; i++ ) {       		
    		sCurVal = (int)((src[i]&0x0FF) << 8);  
    		
    		for (j = 0; j < 8; j++) { 
    			if ( (short)(sCRCReg ^ sCurVal) < 0 ) 
    				sCRCReg = ((sCRCReg << 1) ^ (0x8005));    			
    			else 
    				sCRCReg <<= 1;
    			    			 
    			sCurVal <<= 1;            
    		}    		
    	}    	
    	return (short)sCRCReg;
    } */
    
    /**
     * @param src:校验数据起始位; length:需要校验的数据的字节长度
     * @explain 生成CRC校验short数据
     */   
    public static int getCRC16Default(byte[] src,int length) {
    	//MODBUS CRC-16表 8005 逆序		
  	  	return GetCrc_16(src, length, 0xFFFF, g_McRctable_16);
    }

    /**
     * @param src
     * @param nLength
     * @param init
     * @param ptable
     * @return
     * @exception 正序CRC计算
     */
    public static int GetCrc_16(byte[] src, int nLength, int init, int[] ptable) {
		int cRc_16 = init;
		int temp;
		int i = 0;
		while ( nLength-- > 0 ) {
			temp = cRc_16 >> 8; 
			int post = ((temp ^ (src[i++]&0x0FF) ) & 0x0FF);
			if ( post < 256 ) {
				cRc_16 = (cRc_16 << 8) ^ ptable[post];
			
			} else {
				Log.e(TAG,"GetCrc_16 function in post:" + post);
			}			
		}
		return cRc_16;    
	}
    
    /**
    * @param times时间戳（秒数）
    * @explain 将时间戳转换成年-月-日
    */
    @SuppressLint("SimpleDateFormat") 
    public static String getDateForTimes(long times) {
    	String strdate = "";
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
		java.util.Date dt = new Date(times);
		strdate = format.format(dt); 		
    	return strdate;        	
    }
    
	/**
	* @param times时间戳（毫秒数）
	* @explain 将时间戳转换成时：分：秒
	*/
    public static String  stringForTime(long times) {
    	String strtimes = "";
    	SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    	
		java.util.Date dt = new Date(times);   		
		strtimes = format.format(dt); 		
    	return strtimes;     	  	
	}
    
    /**
	* @param date 日期字符串
     * @throws ParseException 
	* @explain 将时间字符串转换为日期字符串
	*/
    public static String  stringToDateStr(String date) {
    	SimpleDateFormat sdfOld = new SimpleDateFormat("yyyyMMddHHmmss");
		
    	Date mDate = null;
		try {
			mDate = (Date) sdfOld.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat sdfNew = new SimpleDateFormat("yyyy-MM-dd");
		
    	return sdfNew.format(mDate);     	  	
	}
    
    /**
	* @param date 日期字符串
     * @throws ParseException 
	* @explain 将时间字符串转换为HH:mm:ss字符串
	*/
    public static String  stringToTimeStr(String date) {
    	SimpleDateFormat sdfOld = new SimpleDateFormat("yyyyMMddHHmmss");
		
    	Date mDate = null;
		try {
			mDate = (Date) sdfOld.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat sdfNew = new SimpleDateFormat("HH:mm:ss");
		
    	return sdfNew.format(mDate);     	  	
	}
    
    /**
	* @param playtimes 播放时长（秒数）
	* @explain 将播放时长转换成时：分：秒
	*/
    public static String getDuration(long playtimes) {    	
    	int seconds = (int)(playtimes % 60);
    	int minutes = (int)(playtimes % 3600)/60;
    	int hours = (int)(playtimes/3600);
    	return String.format("%2d:%02d:%02d", hours, minutes, seconds).toString();    	   	
    }
    
    public static final String IMPACT_SUFFIX = "_impact";
    
    public static boolean isImpactVideo(String file) {
        if (file == null) {
            return false;
        }
        int index = file.lastIndexOf(".");
        if (index < 0) {
            return false;
        }
        if ( index < IMPACT_SUFFIX.length() ) {
        	return false;
        }        
        String pathSuffix = file.substring(index - IMPACT_SUFFIX.length(), index);
        if (pathSuffix.compareTo(IMPACT_SUFFIX) != 0) {
            return false;
        }
//        Log.d("isImpactVideo", "ImpactVideo, path=" + file + ", index=" + index);
        return true;
    }  
    
	public static void copyShockFileToSd()
    {
    	File srcfile = new File("/mnt/sdcard/DCIM/Camera");
    	if(srcfile.exists() && srcfile.list().length !=0 )
    	{
    		ShellUtils.execCommand("busybox mv /mnt/sdcard/DCIM/Camera/* /mnt/extsd/DCIM/Camera/",true);
    	}
    }
	
	public static int copyFrom2To(String formPath, String toPath)
    {
		Log.d(TAG, "copyFrom2To function formPath:" + formPath 
				+ ", toPath:" + toPath);
		
		int result = -1;
    	File srcfile = new File(formPath);
    	if(srcfile.exists())
    	{
//    		Log.d("setExportFileCount", "exec /system/bin/cp "+ formPath + " " + toPath);
    		result = ShellUtils.execCommand("exec /system/bin/cp " 
    					+ formPath + " " + toPath,false).result;
    	}
    	
    	return result;
    }
	
	//http://www.cnblogs.com/wainiwann/archive/2012/02/06/2340336.html
	public synchronized static int copyShockFileToSd(Context mContext, String fromFile, String toFile)
	{
	    //要复制的文件目录
		File[] currentFiles;
		File root = new File(fromFile);
		//如同判断SD卡是否存在或者文件是否存在
		//如果不存在则 return出去
		if(!root.exists() || root.list().length ==0)
		{
		    return -1;
		}
		//如果存在则获取当前目录下的全部文件 填充数组
		currentFiles = root.listFiles();
		 
		//目标目录
		File targetDir = new File(toFile);
		//判断目录是否存在
		if(!targetDir.exists())
		{
			return -1;
//		    targetDir.mkdirs();
		}
		//遍历要复制该目录下的全部文件
	    for(int i= 0;i<currentFiles.length;i++)
	    {
	    	if(new File(currentFiles[i].getPath()).isDirectory())
	    		continue ;
//	    	copyFrom2To(mContext, currentFiles[i].getPath(), toFile + currentFiles[i].getName());
	    	CopySdcardFile(mContext, currentFiles[i].getPath(), toFile + currentFiles[i].getName());
	    }
	    return 0;
	}
		     
		   
	//文件拷贝
	//要复制的目录下的所有非子目录(文件夹)文件拷贝
	public static int CopySdcardFile(Context mContext, String fromFile, String toFile)
	{
		Log.e(TAG, " copyShockFileToSd "+toFile+"--"+fromFile + "length = "+ (new File(fromFile)).length());
		FileChannel input = null;
	    FileChannel output = null;
	    try {
	        input = new FileInputStream(new File(fromFile)).getChannel();
	        output = new FileOutputStream(new File(toFile)).getChannel();
	        long length = output.transferFrom(input, 0, input.size());
	        Log.e(TAG, " copyShockFileToSd length = "+length);
	    } catch (Exception e) {
	        Log.w(TAG, "error occur while CopySdcardFile", e);
	    } finally {
			try {
				input.close();
				output.close();
				return 0;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    return -1;
	    /*try
	    {
	        InputStream fosfrom = new FileInputStream(fromFile);
	        OutputStream fosto = new FileOutputStream(toFile);
	        byte bt[] = new byte[8192];
	        int c;
	        while ((c = fosfrom.read(bt)) > 0)
	        {
	        	if( !StorageStateManager.getStorageState() || FormatProcess.ismIsFormatFlag() ) {
					Log.d(TAG,"storage unmount");
					fosfrom.close();
			        fosto.close();
	            	return -1;
				}
	            fosto.write(bt, 0, c);
	        }
	        fosfrom.close();
	        fosto.close();
	        BVRecorderManager.getInstance(mContext).addShockData(fromFile);
	        return 0;
	         
	    } catch (Exception ex)
	    {//Log.e(""," copyShockFileToSd Exception");
	        return -1;
	    }*/
	}
	
	/**
	 * @param path 路径
	 * @param value 数值
	 * @exception 支持shell命令，对对应的文件内写入某个值
	 */
	public static void systemPowerIO(String path, String value) {		
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(path));
			writer.write(value+"\n");
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}			
	}

	/**
	 * @param path 路径
	 * @param value 数值
	 */
	public static void writeFile(String path, String value) {		
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(path));
			writer.write(value+"\n");
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}			
	}

	/**
	 * @param path 路径
	 * @return 返回读内容
	 */
	public static int readFile(String path) {	
		int iRet = -1;
		BufferedReader read = null;
		File file = new File(path);
		if (!file.exists()) {
			return iRet;
		}
		try {
			read = new BufferedReader(new FileReader(file));
			String str = null;
			while((str = read.readLine()) != null) {
				iRet = Integer.parseInt(str);
			}
			read.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return iRet;
	}

	@SuppressWarnings("resource")
	public static boolean isCheckVaild( String filepath ) {		
		 try {  
        	FileChannel fc = null; 
        	FileInputStream fis = null;        	
        	int length = 0;            
            
        	if ( null == filepath || filepath.isEmpty() ) {
        		return false;
        	}
            File file = new File(filepath);   
            if (null == file) {
            	return false;
            }
            if ( file.exists() && file.isFile() ) {
            	fis = new FileInputStream(file); 
            	fc = fis.getChannel();
            	length = (int)fc.size();
            }
            if ( 0 == length ) {
            	return false;
            } else {
            	return true;
            }                       
        } catch (FileNotFoundException e) {  
            e.printStackTrace();              
        } catch (IOException ex) {          	
            ex.printStackTrace();  
        }  
		
		return false;
	}
	
	
	/** 
     * 获得指定文件的byte数组 
     */  
    public static byte[]  getBytesFromFile( String filePath ) {    	        
        try {  
        	FileChannel fc = null; 
        	FileInputStream fis = null;
        	byte[] buffer = null;
        	ByteArrayOutputStream bos = null;
        	int length = 0;            
            
            File file = new File(filePath);             
            if ( file.exists() && file.isFile() ) {
            	fis = new FileInputStream(file); 
            	fc = fis.getChannel();
            	length = (int)fc.size();
            }
            if ( 0 == length ) {
            	return buffer;
            }                         
            bos = new ByteArrayOutputStream(length);              
            buffer = new byte[length];             
            
            while(true) {            	
            	byte[] b = new byte[1024];  
                int readlen = 0; 
                readlen = fis.read(b);
                if ( readlen == -1 ) {
                	fis.close();  
                    bos.close();  
                    break;
                } else {
                	bos.write(b, 0, readlen);  
                }                                    	
            }             
            buffer = bos.toByteArray();  
            return buffer;
            
        } catch (FileNotFoundException e) {  
            e.printStackTrace();              
        } catch (IOException ex) {          	
            ex.printStackTrace();  
        }  
        return null;  
    }
    
    /** 
     * 根据byte数组，生成文件 
     */  
    public static void getFile(byte[] bfile, String filePath,String fileName) {  
        BufferedOutputStream bos = null;  
        FileOutputStream fos = null;  
        File file = null;  
        try {  
            File dir = new File(filePath);  
            if(!dir.exists()&&dir.isDirectory()){//判断文件目录是否存在  
                dir.mkdirs();  
            }  
            file = new File(filePath+"\\"+fileName);  
            fos = new FileOutputStream(file);  
            bos = new BufferedOutputStream(fos);  
            bos.write(bfile);  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (bos != null) {  
                try {  
                    bos.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
            if (fos != null) {  
                try {  
                    fos.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
        }  
    }
	
	/**
	 * @param mContext 上下文句柄
	 * @exception 重启应用
	 */
	public static void systemReboot(Context mContext) {
		Intent intent = new Intent(Intent.ACTION_REBOOT);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}
	
	/**
	 * @param path 文件夹路径
	 * @param fileType 文件后缀名
	 * @return 该文件夹下对应后缀名的文件名称列表
	 * @exception 根据某种后缀名获取具体某个文件夹下的该文件名称列表（不支持子文件夹）
	 */
	public static List<String> getFilesName(String path, String fileType) {
		Log.d(TAG,"getFilesName in path:" + path +", fileType:" + fileType);
		
		List<String> filesList = new ArrayList<String>();		
		try {
			File fileDir = new File(path);
			if ( null != fileDir ) {			
		        File[] fs = fileDir.listFiles();
		        if ( null != fs && fs.length > 0 ) {
		        	 for (File f : fs) {
		 	            if (null != f && f.isFile() ) {
		 	                if (fileType.equals(f.getName().substring(f.getName().lastIndexOf(".") + 1,
		 	                                f.getName().length()) ) ) {
		 	                	String filename = f.getName();
		 	                	filesList.add(filename);	
		 	                }	                		                	
		 	            } 
		 	        }
		        }	       			
			}
		}catch( Exception ex) {
			ex.printStackTrace();
			filesList = null;
		}				
		return filesList;		
	}
	
	
	/**
	 * @param data
	 * @return 返回无符号byte型数据
	 */
	public static byte getUnsignedByteFromInt (int data) {		
		String strData =  Integer.toHexString(data);	
		byte bytedata = Integer.valueOf(strData, 16).byteValue();		
		return bytedata;
	}
	
	/**
	 * @param data
	 * @return 返回无符号short型数据
	 */
	public static short getUnsignedShortByteInt(int data) {		
		String strData =  Integer.toHexString(data);		
		short shortData = Integer.valueOf(strData, 16).shortValue();		
		return shortData;		
	}
		
	/**
	 * @param data
	 * @return //将data字节型数据转换为0~255 (0xFF 即BYTE)。
	 */
	public static int getUnsignedByteFromByte (byte data) {      
         return data&0x0FF;
    }

    /**
     * @param data
     * @return //将data字节型数据转换为0~65535 (0xFFFF 即 WORD)。
     */
    public static int getUnsignedShort (short data) {      
    	return (data&0x0FFFF);
    }
	
	/**
	 * @param data
	 * @return 将int数据转换为0~4294967295 (0xFFFFFFFF即DWORD)。
	 */
	public static long getUnsignedInteger (int data) {		
        return (data&0x0FFFFFFFFl);
     }

	/**
     * 从byte数组查找某个字符。
     * @param byte数组，字符
     * @return 位置
     */
    public static int findCharInBytes(byte[] bytes, byte nByte) {
        for( int i = 0; i < bytes.length; i++ )
        {
        	if( bytes[i] == nByte )
        		return i;
        }
        return -1;
    }
    
    /**
     * @param path
     * @return 检测文件是否存在，如果不存在则创建,且修改权限为可读可写
     */
    public static int isCheckFile(String filepath) {    	
    	try {
    		if ( !isCheckExist(filepath) ) {
    			File file = new File(filepath);
    			file.getParentFile().mkdirs(); 
    			if ( file.createNewFile() ) {
    				int result = ShellUtils.execCommand("exec /system/bin/chmod -R 777 "+ filepath,false).result;
    				if ( result < 0 ) {
    	    			Log.e(TAG, "importParam system bin chmod failed in path:" + filepath + ", result:" + result);	
    	    			return -1;
    	    		}
    			}    			               
    		} else {
    			Log.d(TAG,"isCheckFile start in filepath:" + filepath);
    		}
    	
    	} catch(Exception ex) { 
    		ex.printStackTrace();
    		Log.e(TAG,"isCheckFile function filepath:" + filepath);
    	}     	
    	return 0;
    }
        
    /**
     * @param filepath
     * @return 检测文件是否存在
     */
    public static boolean isCheckExist(String filepath) {
    	try {
    		if ( null == filepath || filepath.isEmpty() ) {
    			return false;
    		}
            File file = new File(filepath);
            return  file.exists();
            
            
    	}catch(Exception ex) {
    		ex.printStackTrace();    		
    	}
    	return false;
    }
    	    	
    /**
     * @return 生成随机数(无种子)
     */
    public static int getRandomNumber() {    	
    	java.util.Random r = new java.util.Random();
    	return r.nextInt();
    }

    /**
     * @return 刷新缓冲文件
     */
    public static void cacheFlush() 
    {
    	ShellUtils.execCommand("exec /system/bin/sync",false);
    }
    

	
    // 判断一个字符串是否都为数字  
 	public static boolean isDigit(String strNum) {  
 	    return strNum.matches("[0-9]{1,}") && ( strNum.compareTo(Integer.MAX_VALUE+"") <= 0 );
 	}
 
  
	/**
	 * @param url
	 * @return 检测url的地址
	 */
	public static boolean urlHasAcceptableScheme(String url) {
		if (url == null) {
			return false;
		}
		for (int i = 0; i < acceptableSchemes.length; i++) {
			if (url.startsWith(acceptableSchemes[i])) {
				return true;
			}
		}
		return false;
	}

    /** 
     * 查找ts是文件的视频长度
     * @return 返回时长毫秒
     */  
    public static long TsTimeLengthGet(File tsfile) {
//    	Log.d(TAG,"TsTimeLengthGet  Path = " + tsfile.getPath() );
    	try {
    		return (long) Math.ceil( tsfile.length() * 1000 / 1708738.198f ) ;
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if ( true ) {
    		return 0;
    	}

    	byte[] buffer = new byte[BUFFERSPACE];
    	RandomAccessFile tsAccessFile = null;

    	long sum=0;
    	int flag=0;
    	long inputfile_length = tsfile.length();
    	long PCR_base = 0;
    	long PCR_base_first = 0;
    	long PCR_base_end = 0;
    	long code = 0;
    	long step = 0;
    	
    	long bufferCount =  inputfile_length / BUFFERSPACE;
    	long rewindCount = 0;
    	boolean exit = false;
    	long  loopCount = 0;
    	long  MaxLoopCount = 30;

    	try {
    		tsAccessFile = new RandomAccessFile(tsfile, "rw");
    		while ( sum < ( inputfile_length / BUFFERSPACE ) && loopCount <= MaxLoopCount )
    		{
    			Arrays.fill(buffer, (byte) 0x00);
    			tsAccessFile.read(buffer, 0, BUFFERSPACE);
    			int start_point = 0;
    			while( ( start_point < BUFFERSPACE ) && ( ( 188 + start_point ) < BUFFERSPACE ) )
//    			while( start_point < BUFFERSPACE )
    			{
    				if( ( buffer[0+start_point] == 0x47 ) && ( start_point + 188 == BUFFERSPACE ) )
    				{
    					start_point = start_point + 188;
    				}
    				else if ( ( buffer[0+start_point] == 0x47 ) && ( buffer[188+start_point] == 0x47 ) )
    				{
    					if( ( buffer[1+start_point] & 0x40 ) != 0 )
    					{
//    						Log.d(TAG,"TsTimeLengthGet_0  PCR_base = " + (buffer[1+start_point] & 0x40) );
    						long afc = ( buffer[3+start_point] >> 4 ) & 0x3;
//    						Log.d(TAG,"TsTimeLengthGet_1  PCR_base = " + afc );
    						if( afc == 0 || afc == 2 )
    						{
    							
    						}
    						else if( afc == 3 )
    						{
    							
    						}
    						else
    						{
    							for(int i = 0 ; i < 188 - 13; i++ )
    							{
    								code = 0;
    								code = ( buffer[4+i+start_point+0] & 0x00000000000000FF ) << 16
    										| ( buffer[4+i+start_point+1] & 0x00000000000000FF ) << 8
    										| ( buffer[4+i+start_point+2] & 0x00000000000000FF );
//    								Log.d(TAG,"TsTimeLengthGet_2  PCR_base = " + code );
    								if ( code != 0x01 )
    					                continue;
    								code = (buffer[4+i+start_point+3] & 0x00000000000000FF) | 0x100 ;
//    								Log.d(TAG,"TsTimeLengthGet_3  PCR_base = " + code );
    								
    								if (!((code >= 0x1c0 && code <= 0x1df) || (code >= 0x1e0 && code <= 0x1ef) || (code == 0x1bd) || (code == 0x1fd)))
    					                continue;

    								long flags = (buffer[4+i+start_point+7] & 0x00000000000000FF) ;
//    								Log.d(TAG,"TsTimeLengthGet_4  PCR_base = " + flags );
    					            if ( ( flags & 0xc0 ) > 0 )        //* check pts flag
    					            {
    					            	PCR_base = (buffer[4+i+start_point+9] & (0x00000000000000FE)) << 29
    					            			| (buffer[4+i+start_point+10] & (0x00000000000000FF)) << 22
    					            			| (buffer[4+i+start_point+11] & (0x00000000000000FE)) << 14
    					            			| (buffer[4+i+start_point+12] & (0x00000000000000FF)) << 7
    					            			| (buffer[4+i+start_point+13] & (0x00000000000000FF)) >> 1;
//    					            	Log.d(TAG,"TsTimeLengthGet_5  PCR_base = " + PCR_base);
//    					            	if(PCR_base>0)
//    					            		return PCR_base;
    					            	long emptyPacket = 0x1ffffffffL;
    					                if( PCR_base == emptyPacket || PCR_base == -1)  //* empty packet.
    					                    continue;
    					                if( 0 == flag )
    					                {
    					                	PCR_base_first = PCR_base;
    					                	if(bufferCount < 2)
    	    								{
    	    									exit = true;
    	    								}
    					                	rewindCount = 1;
    					                	sum = bufferCount - rewindCount;
    					                	tsAccessFile.seek( 0 );
//    	    								tsAccessFile.seek( (long) ( sum + 1 ) * BUFFERSPACE );
    	    								tsAccessFile.seek( (long) (inputfile_length - BUFFERSPACE * rewindCount) );
//    	    								Log.d(TAG,"TsTimeLengthGet_0  seek " + tsAccessFile.getFilePointer()
//    	    										+"bufferCount"+bufferCount+"sum"+sum);
    	    								flag++;
    	    								break;
    					                }
    					                else
    					                {
    					                	if(Math.abs(PCR_base - PCR_base_first) < 90000*300)
    	    								{
    	    									PCR_base_end = PCR_base;
    	    									exit = true;
//    	    									Log.d(TAG,"TsTimeLengthGet_0  seek " +tsfile.getPath()+ PCR_base_end
//    	        										+"bufferCount"+bufferCount+"sum"+sum+"total="+((PCR_base - PCR_base_first))/90000);
    	    								}
    					                }
    					                flag++;
    					                break;
    					            }
    							}
    						}
    						
    						if( rewindCount == 1 )
    						{
//    							Log.d(TAG,"TsTimeLengthGet_6  PCR_base rewindCount = " + rewindCount);
    							break;
    						}

    					}
//    					Log.d(TAG,"TsTimeLengthGet_-1  seek " +tsfile.getPath()+ PCR_base_end
//								+"bufferCount"+bufferCount+"sum"+sum+"total="+((PCR_base - PCR_base_first))/90000
//								+"start_point ="+start_point);
    					start_point = start_point + 188;
    				}
    				else
    				{
    					
						while( ( buffer[0+start_point] != 0x47 ) || 
    							( buffer[188+start_point] != 0x47 && start_point < BUFFERSPACE) )
    					{
//        						Log.d(TAG,"TsTimeLengthGet 2  start_point "+start_point);
    						start_point++;
    						if( start_point + 188 >= BUFFERSPACE )
								break;
    					}
    				}
    			}
    			
    			if(exit)
    				break;
    			else
    			{
    				if( rewindCount == 1 )
    				{
    					rewindCount ++;
//    					Log.d(TAG,"TsTimeLengthGet_7  PCR_base rewindCount = " + rewindCount);
    				}
    				if( rewindCount >= 2 )
    				{
//    					Log.d(TAG,"TsTimeLengthGet_8  PCR_base rewindCount = " + rewindCount);
    					sum = bufferCount - rewindCount;
    					tsAccessFile.seek( 0 );
//    					tsAccessFile.seek( (long) (sum + 1) * BUFFERSPACE );
    					if( inputfile_length - BUFFERSPACE * rewindCount < 0)
    						break;
    					tsAccessFile.seek( (long) (inputfile_length - BUFFERSPACE * rewindCount) );
    					rewindCount ++;
    				}
    			}

    			sum ++;
    			loopCount ++;

    		}
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	PCR_base_end = PCR_base;

    	try {
    		if (tsAccessFile != null)
    			tsAccessFile.close();
        } catch (IOException e1) {
            e1.printStackTrace();  
        }  

//    	Log.d(TAG,"TsTimeLengthGet ___ "+Math.ceil( ( PCR_base_end - PCR_base_first ) / 90.0 )
//    			+ " loopCount = " + loopCount);
    	if ( loopCount >= MaxLoopCount )
    		return (long) Math.ceil( tsfile.length() * 1000 / 1708738.198f ) ;
    	if( (PCR_base_end - PCR_base_first) > 90000*300 )
    		return 180*1000;
    	else if( (PCR_base_end - PCR_base_first) < 0 )
    		return 0;

    	return (long) ( Math.ceil( ( PCR_base_end - PCR_base_first ) / 90.0 ) );
    }
    
    /**
     * @param str
     * @param len
     * @return 字符串截断
     */
    public static String subStringByByte(String str, int len) {
        String result = null;
        if (str != null) {
            byte[] a = str.getBytes();
            if (a.length <= len) {
                result = str;
            } else if (len > 0) {
                result = new String(a, 0, len);
                int length = result.length();
                if (str.charAt(length - 1) != result.charAt(length - 1)) {
                    if (length < 2) {
                        result = null;
                    } else {
                        result = result.substring(0, length - 1);
                    }
                }
            }
        }
        return result;
    }
    
    
    public static String readFile() {
    	
    	return null;
    }
    
}
