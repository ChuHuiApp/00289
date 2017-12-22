package com.run.treadmill.serialutil;

public class Command {
	public final static int CMD_REQ_CONNECT = 1;
	public final static int CMD_PERSONAL_DATA = 2;
	public final static int CMD_INIT_INFO = 3;
	public final static int CMD_PAGE = 4;
	public final static int CMD_STATUS = 5;
	
//	public final static int CMD_RT_INFO = 6;
	public final static int CMD_BUZZER_ACT = 7;
	
	public final static int CMD_PROG_UPGRADE = 8;
	public final static int CMD_HW_INFO = 9;
	public final static int CMD_SW_INFO = 10;

	public final static int CMD_TARGET_DIST = 51;
	public final static int CMD_TARGET_TIME = 52;
	public final static int CMD_TARGET_CALORIE = 53;
	public final static int CMD_TARGET_HRC = 54;
	public final static int CMD_SPD_PROFILE = 55;
	public final static int CMD_INCLINE_PROFILE = 56;
	public final static int CMD_CURR_SPD_INC = 57;
	public final static int CMD_FAN_STA = 58;
	public final static int CMD_MUSIC_VOLUME = 59;

	public final static int CMD_MRELAY_STA = 60;
	public final static int CMD_SYS_INFO = 61;
	public final static int CMD_MOTOR_TYPE = 62;
	public final static int CMD_SP_SET_PM = 63;
	public final static int CMD_SP_CURR_INFO = 64;
	public final static int CMD_SP_END_INFO = 65;
	public final static int CMD_CURR_RPW = 66;
	public final static int CMD_CURR_DC = 67;
	
	public final static int CMD_ERR_STATUS = 201;
	public final static int CMD_CURR_KEY = 202;
	public final static int CMD_CURR_INFO = 203;
	public final static int CMD_CAL_DATA = 204;
	public final static int CMD_CHANGE_STA = 205;

	public final static int CMD_ECG_INFO = 206;
	
	public final static int KEY_CMD_SPEED_2 = 0;
	public final static int KEY_CMD_SPEED_4 = 1;
	public final static int KEY_CMD_SPEED_6 = 2;
	public final static int KEY_CMD_SPEED_8 = 3;
	public final static int KEY_CMD_SPEED_10 = 4;
	public final static int KEY_CMD_SPEED_12 = 5;

	public final static int KEY_CMD_INCLINE_1 = 6;
	public final static int KEY_CMD_INCLINE_3 = 7;
	public final static int KEY_CMD_INCLINE_6 = 8;
	public final static int KEY_CMD_INCLINE_9 = 9;
	public final static int KEY_CMD_INCLINE_12 = 10;
	public final static int KEY_CMD_INCLINE_15 = 11;

	public final static int KEY_CMD_INCLINE_DEC = 12;
	public final static int KEY_CMD_INCLINE_PLUS = 13;

	public final static int KEY_CMD_SPEED_DEC = 16;
	public final static int KEY_CMD_SPEED_PLUS = 17;

	public final static int SHOW_AD_MAX_VALUE = 18;
	public final static int SHOW_AD_MIN_VALUE = 19;
	public final static int CALI_SUCCESS = 20;
	public final static int ACTUAL_SPEED = 21;
	public final static int FLOATWIN_ERROR_PROC = 23;
	public final static int FLOATWIN_ERROR_UNPROC = 27;
	public final static int SAFE_KEY_TRUE = 25;

	public final static int KEY_CMD_QUICK_START = 0xc;
	public final static int KEY_CMD_STOP_CANCEL = 0x10;

	public final static int KEY_CMD_LEVEL_DEC_F = 0x4;
	public final static int KEY_CMD_LEVEL_PLUS_F = 0x8;
	public final static int KEY_CMD_LEVEL_DEC_F_LONG_1 = 0x5;
	public final static int KEY_CMD_LEVEL_DEC_F_LONG_2 = 0x6;
	public final static int KEY_CMD_LEVEL_PLUS_F_LONG_1 = 0x9;
	public final static int KEY_CMD_LEVEL_PLUS_F_LONG_2 = 0xa;

	public final static int KEY_CMD_LEVEL_DEC_S = 0x94;
	public final static int KEY_CMD_LEVEL_PLUS_S = 0x90;
	public final static int KEY_CMD_LEVEL_DEC_S_LONG_1 = 0x95;
	public final static int KEY_CMD_LEVEL_DEC_S_LONG_2 = 0x96;
	public final static int KEY_CMD_LEVEL_PLUS_S_LONG_1 = 0x91;
	public final static int KEY_CMD_LEVEL_PLUS_S_LONG_2 = 0x92;

	public enum SYSRUNSTATUS {
		STA_NOR, STA_MOT, STA_PU, STA_CAL, 
		STA_PAUSE, STA_SUSPEND, STA_EM_I, STA_EM_II, 
		STA_STOPING, STA_DO_INC, STA_WARM_UP, STA_COOL_DOWN, 
		STA_PREPAR, STA_FITT_FAIL, STA_FINISHED, STA_SHUT_DOWN, 
		STA_RESET, STA_FW_UPDATE, STA_FW_UPDATE_FAIL, STA_SET_PM, 
		STA_SET_WIFI_PM, STA_SET_WIFI_MID, STA_ERROR_PAGE, 
	}

	public enum FANSTATUS {
		TURN_ON, TURN_OFF, TURN_ON_LOWEST_SPEED,
		TURN_ON_MIDDLE_SPEED,TURN_ON_HIGHEST_SPEED,
	}

}
