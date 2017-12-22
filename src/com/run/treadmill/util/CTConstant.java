package com.run.treadmill.util;

import com.run.treadmill.R;

public class CTConstant {
	public enum RunMode {
		IDX_HOME_HILL_MODE, IDX_HOME_INTERVAL_MODE, IDX_HOME_GOAL_MODE, IDX_HOME_QUICKSTART_MODE, 
		IDX_HOME_VIRTUAL_MODE, IDX_HOME_HRC_MODE, IDX_HOME_FITNESS_MODE, IDX_HOME_USERPROGRAM_MODE, 
	}

	public enum MediaENItem {
		ITEM_MEDIA_YOUTUBE, ITEM_MEDIA_CHROME, ITEM_MEDIA_FACEBOOK, ITEM_MEDIA_FLIKR, 
		ITEM_MEDIA_INSTAGRAM, ITEM_MEDIA_MP3, ITEM_MEDIA_MP4, ITEM_MEDIA_AVIN, ITEM_MEDIA_TWITTER,
		ITEM_MEDIA_SCREEN_MIRROR,
	}

	public static String[] mediaENPkgName = {
		"com.google.android.youtube",
		"com.android.chrome", 
		"com.facebook.katana",
		"com.yahoo.mobile.client.android.flickr", 
		"com.instagram.android", 
		"com.android.music", 
		"com.softwinner.fireplayer",
		//"com.android.camera2",
		"com.android.cameraSelf",
		"com.twitter.android",
		"com.hpplay.happyplay.aw", };

	public static String[] mediaENClassName = { 
		"app.honeycomb.Shell$HomeActivity", 
		"com.google.android.apps.chrome.Main",  
		"LoginActivity", 
		"ui.misc.LoginActivity", 
		"activity.MainTabActivity", 
		"", 
		"", 
		//"com.android.camera2/com.android.camera.CameraLauncher",
		".Camera2Launcher",
		"StartActivity",
		".WelcomActivity", };

	public enum MediaItem {
		ITEM_MEDIA_BAIDU, ITEM_MEDIA_WEIBO, ITEM_MEDIA_AIQIYI, ITEM_MEDIA_AVIN, 
		ITEM_MEDIA_MP3, ITEM_MEDIA_MP4, ITEM_MEDIA_CHROME, ITEM_MEDIA_SCREEN_MIRROR,
	}

	public static String[] mediaPkgName = {
					"com.baidu.searchbox.pad",
					"com.sina.weibo", 
					"com.qiyi.video.pad",
					//"com.android.camera2",
					"com.android.cameraSelf", 
					"com.android.music", 
					"com.softwinner.fireplayer", 
					"com.android.chrome",
					"com.hpplay.happyplay.aw", };

	public static String[] mediaClassName = { 
		".MainActivity", 
		".SplashActivity bnds", 
		"org.qiyi.android.video.MainActivity", 
		//"com.android.camera2/com.android.camera.CameraLauncher", 
		".Camera2Launcher", 
		"", 
		"com.softwinner.fireplayer.ui.FourKMainActivity", 
		"com.android.chrome/org.chromium.chrome.browser.ChromeTabbedActivity",
		".WelcomActivity", };
	/*"com.android.music.MusicBrowserActivity", */

	public static final int REQUEST_CODE_DELETE = 0x3;
	public static final int GENDER_BOY = 0;
	public static final int GENDER_GIRL = 1;
	
	//返回码
	public static final int RESULT_BACK = 100;

	public static final int DEFAULT = -1;
	// 参考
	public static final int RUN_REFERENCE_BY_DISTANCE = 0;
	public static final int RUN_REFERENCE_BY_TIME = 1;
	public static final int RUN_REFERENCE_BY_CALORIES = 2;
	
	public static final int HRC_NO_HR_STATUS = -1;
	public static final int HRC_OVERPULSE_STATUS = -2;
	public static final int HRC_AUTO_END_STATUS = -3;
	public static final int FITNESS_OVER_TAG_HR = -4;
	public static final int NO_RPM_STATUS = -5;

	public static final int LOW_RPM = -6;
	public static final int OUT_RANGE_RPM = -7;

	public static final int DEFAULT_INT_VALUE = 0;
	public static final float DEFAULT_FLOAT_VALUE = 0.0f;

	public enum VrVideoItem {
		ITEM_VR_FRANCE_PARIS, ITEM_VR_CZECH, ITEM_VR_GERMANY_DRESDEN, ITEM_VR_GERMANY_BALT, 
		ITEM_VR_ITALY_ROME, ITEM_VR_ITALY_PASSO, ITEM_VR_UNITED_KINGDOM, ITEM_VR_AUSTRIA_WIEN, 
		ITEM_VR_AUSTRIA_HELMESGUPF, ITEM_VR_AUSTRIA_HALLSTATT,
	}
	//虚拟场景跑步视频路径
	public static final String vrVideoPath[] = {
										"/storage/card/France_Paris_Eiffel_Tower.mp4", 
										"/storage/card/Czech_Republic_Bohemian_mountains.mp4",
										"/storage/card/Germany_Dresden_center.mp4",
										"/storage/card/Germany_Balt_Ahrenshoop.mp4",

										"/storage/card/Italy_Rome_Colosseo.mp4",
										"/storage/card/Italy_Passo_Oscuro.mp4", 
										"/storage/card/United_Kingdom_London_Millenium_Bridge.mp4",
										"/storage/card/Austria_Wien_Schonbrunn.mp4",

										"/storage/card/Austria_Helmesgupf.mp4",
										"/storage/card/Austria_Hallstatt.mp4",};

	public static final int MSG_SEND_BACK_EVENT = 5001;
	public static final int MSG_PROC_ERROR_CMD = 5003;
	public static final int MSG_PROC_SHOW_CMD = 5005;
	public static final int MSG_VR_FRESH_TIME = 5006;
	public static final int MSG_POP_LOCK_WIN = 5007;

	/**
	 * Activity传值参数名称
	 */
	public static final String USERID = "userId";
	public static final String IS_FINSH = "is_finsh";
	public static final String IS_SUSPENDED = "is_suspended";
	public static final String RUN_DISTANCE = "run_distance";
	public static final String RUN_CALORIES = "run_calories";
	public static final String TIME_MS = "time_ms";
	public static final String ISLOCK = "islock";
	public static final String IS_SHOW_SUMMARY = "is_show";

	
	//计算单位
	public static final int UNITS_STATE_DEFAULT = 0;
	
	/**
	 * K系数
	 */
	public static final float BOY_KEY = 1.25f;
	public static final float GIRL_KEY = 1.15f;
	/**
	 * 默认体重
	 */
	public static final float DEFAULT_WEIGHT = 60.0f;

	//设置界面参数默认值
	//SRS PASS
	public static final String srsPass = "0000";
	public static final String customPass = "1234";
	public static final long runMaxTime = 6000*60*60l;
	public static final long runMaxDis = 12000l;

	public static int fanRes[] = {R.drawable.btn_fan_1_1, R.drawable.btn_fan_4_1, 
			R.drawable.btn_fan_3_1, R.drawable.btn_fan_2_1};
	public static int rpmRes[] = {R.drawable.img_sportmode_rpm_01, R.drawable.img_sportmode_rpm_02, 
		R.drawable.img_sportmode_rpm_03, R.drawable.img_sportmode_rpm_04,  R.drawable.img_sportmode_rpm_05, 
		R.drawable.img_sportmode_rpm_06, R.drawable.img_sportmode_rpm_07,  R.drawable.img_sportmode_rpm_08,
		R.drawable.img_sportmode_rpm_09, R.drawable.img_sportmode_rpm_10,  R.drawable.img_sportmode_rpm_11,
		R.drawable.img_sportmode_rpm_12, R.drawable.img_sportmode_rpm_13,  R.drawable.img_sportmode_rpm_14,
		R.drawable.img_sportmode_rpm_15, R.drawable.img_sportmode_rpm_16,  R.drawable.img_sportmode_rpm_17,
		R.drawable.img_sportmode_rpm_18, R.drawable.img_sportmode_rpm_19,  R.drawable.img_sportmode_rpm_20,
		R.drawable.img_sportmode_rpm_21, R.drawable.img_sportmode_rpm_22,  R.drawable.img_sportmode_rpm_23,
		R.drawable.img_sportmode_rpm_24, R.drawable.img_sportmode_rpm_25,  R.drawable.img_sportmode_rpm_26,
		R.drawable.img_sportmode_rpm_27, R.drawable.img_sportmode_rpm_28,  R.drawable.img_sportmode_rpm_29,
		R.drawable.img_sportmode_rpm_30, R.drawable.img_sportmode_rpm_31 };

	public static int rpmValueRes[] = {R.drawable.tv_sportmode_rpm_0, R.drawable.tv_sportmode_rpm_1, 
		R.drawable.tv_sportmode_rpm_2, R.drawable.tv_sportmode_rpm_3,  R.drawable.tv_sportmode_rpm_4, 
		R.drawable.tv_sportmode_rpm_5, R.drawable.tv_sportmode_rpm_6,  R.drawable.tv_sportmode_rpm_7,
		R.drawable.tv_sportmode_rpm_8, R.drawable.tv_sportmode_rpm_9 };
}
