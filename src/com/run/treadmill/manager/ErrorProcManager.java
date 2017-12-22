package com.run.treadmill.manager;

import com.run.treadmill.R;

import android.util.Log;

public class ErrorProcManager {
	public final static String TAG = "ErrorProcManager";
	public static int ERR_No = 0;			//没有错误
	public static int ERR_Safety_Key = 1;	//安全key移除
	public static int ERR_No_INC = 5;		//扬升无法运动错误
	public static int ERR_INC_LIMIT = 6;	//扬升上下限范围不足
	public static int ERR_UC_COMM = 7;		//下控通信错误
	public static int ERR_EEPROM = 9;		//EEPROM错误
	public static int ERR_OVER_CURRENT = 10;//过电流
	public static int ERR_Inv_LE1 = 13;		//变频器低电压跳脱
	public static int ERR_Inv_OE = 14;		//变频器过电压
	public static int ERR_Inv_GF = 15;		//变频器落地异常
	public static int ERR_Inv_PrEr = 16;	//变频器Flash程式错误
	public static int ERR_Inv_LE = 17;		//变频器低电压提示
	public static int ERR_Inv_ESP = 18;		//变频器紧急停机警示
	public static int ERR_Inv_OH = 19;		//变频器过热
	public static int ERR_Inv_OL = 20;		//变频器马达过载异常
	public static int ERR_Inv_OL1 = 21;		//变频器过载异常
	public static int ERR_Inv_OL0 = 22;		//变频器系统过载异常
	public static int ERR_Inv_MO = 23;		//变频器马达断线检出
	public static int ERR_Inv_BR = 24;		//变频器刹车故障

	public int err_status = -1;
	public boolean isHasERR = false;
	public boolean isFirstE5E6E7 = false;
	public int err_res[] = {R.drawable.img_pop_e01, R.drawable.img_pop_e02, R.drawable.img_pop_e03, R.drawable.img_pop_e04,
						R.drawable.img_pop_e05, R.drawable.img_pop_e06, R.drawable.img_pop_e07, R.drawable.img_pop_e08, 
						R.drawable.img_pop_e09, R.drawable.img_pop_e10, R.drawable.img_pop_e11, R.drawable.img_pop_e12,
						R.drawable.img_pop_e13, R.drawable.img_pop_e14, R.drawable.img_pop_e15, R.drawable.img_pop_e16,
						R.drawable.img_pop_e17, R.drawable.img_pop_e18, R.drawable.img_pop_e19, R.drawable.img_pop_e20,
						R.drawable.img_pop_e21, R.drawable.img_pop_e22, R.drawable.img_pop_e23, R.drawable.img_pop_e24, };

	private static ErrorProcManager ourInstance = null;
	public static ErrorProcManager getInstance() {
//    	Log.d(TAG,"getInstance function!");
    	
        if ( null == ourInstance ) {
            synchronized (ErrorProcManager.class) {
                if (null == ourInstance ) {
                    ourInstance = new ErrorProcManager();                    
                }
            }
        }
        return ourInstance;
    }
			
	private ErrorProcManager() {

	}

	public int getErrRes(int err) {
		return err_res[err-1];
	}

	public boolean isErrUnProc() {
		if ( err_status != ERR_No_INC &&  err_status != ERR_INC_LIMIT 
				&&  err_status != ERR_UC_COMM ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean E5E6E7IsNoProc(int errValue) {
		if ( (err_status == ERR_No_INC || err_status == ERR_INC_LIMIT 
				||  err_status == ERR_UC_COMM) && !isFirstE5E6E7 ) {
			return true;
		} else {
			return false;
		}
	}

}
