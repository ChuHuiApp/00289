package com.run.treadmill.systemInitParam;

import android.util.Log;

public class FactoryTwoParam {

	private static final String TAG = "FactoryOneParam";

	private static FactoryTwoParam ourInstance = null;


	/*public enum RecordChannle_e { //存储通道
		CHANNLE_NONE,CHANNLE_FOUR
	}*/

	public static FactoryTwoParam getInstance() {
        if ( null == ourInstance ) {
            synchronized (FactoryTwoParam.class) {
                if (null == ourInstance ) {
                    ourInstance = new FactoryTwoParam();                    
                }
            }
        }
        return ourInstance;
    }
			
	private FactoryTwoParam() {

	}
	
	public float WheelSize = 3.91f;
	public int MaxAD = 0;
	public int MinAD = 0;
	
	public float MaxSpeed = 24f;
	public float MinSpeed = 0.8f;
	
	public int MaxIncline = 20;
	public boolean IsMetric = true;
	

	public final static int PARAM_ID_UNIT	= 0x01;    		//单位
	public final static int PARAM_ID_MAX_SPEED	= 0x02;    	//最大速度
	public final static int PARAM_ID_MIN_SPEED	= 0x03;    	//最小速度
	public final static int PARAM_ID_WHEEL_SIZE	= 0x04;    	//轮径
	public final static int PARAM_ID_MAX_INCLINE	= 0x05;    //最大扬升
	public final static int PARAM_ID_CALI_MAX_AD	= 0x06;    //ad
	public final static int PARAM_ID_CALI_MIN_AD	= 0x07;    //ad

	
/*
	*//**
	 * @param cfgId 数据库cfg的ID
	 * @param val 设置值
	 * @return 返回设置结果值
	 * @exception 设置cfg的value值
	 *//*
	public static boolean setCfg(int cfgId, String val) {		
		if ( isCheckCfgid(cfgId) ) {
			DBCfg cfg = DataSupport.find(DBCfg.class, cfgId);
			if(cfg == null) {
				cfg = new DBCfg(cfgId, val);
			} else {
				cfg.setValue(val);
			}
			return cfg.save();
		}
		return false;		
	}

	*//**
	 * @param cfgId
	 * @param val
	 * @return 返回设置结果值
	 * @exception 重载函数
	 *//*
	public static boolean setCfg(int cfgId, int val) {		
		if ( isCheckCfgid(cfgId) ) {
			DBCfg cfg = DataSupport.find(DBCfg.class, cfgId);
			if(cfg == null) {
				cfg = new DBCfg(cfgId, String.valueOf(val));
			} else {
				cfg.setValue(val);
			}
			return cfg.save();
		}
		return false;
		
	}

	*//**
	 * @param cfgId 数据库cfg的ID
	 * @return 数据库数值
	 * @exception 获取数据库对应ID的value值
	 *//*
	public static int getCfgInt(int cfgId) {		
		if ( isCheckCfgid(cfgId) ) {
			DBCfg cfg = DataSupport.find(DBCfg.class, cfgId);
			if(cfg == null) {
				cfg = new DBCfg(cfgId, mInitMap.get(cfgId));
				cfg.save();
			}
			return cfg.getValueInt();
		}
		return 0;		
	}

	*//**
	 * @param cfgId
	 * @return 获取数据库对应ID的value值
	 * @exception 重载函数
	 *//*
	public static String getCfg(int cfgId) {		
		if ( isCheckCfgid(cfgId) ) {
			DBCfg cfg = DataSupport.find(DBCfg.class, cfgId);
			if(cfg == null) {
				cfg = new DBCfg(cfgId, mInitMap.get(cfgId));
				cfg.save();
			}
			return cfg.getValue();
		}
		return null;		
	}*/

	/**
	 * @exception 初始化数据库(默认值)
	 */
	public static boolean initCfg(boolean flag) {
		/*initMap();		
		if ( !flag ) {
			DataSupport.deleteAll(DBCfg.class);
			initDBCfg();
			
		} else{
			DBCfg mDBCfg =  DataSupport.find(DBCfg.class, (Cfg.IDCFG_BUFF-1));
			if ( null == mDBCfg ) {
				initDBCfg();
			}
		}				*/
		return true;
	}	
	
	public static void initDBCfg() {		
		/*for( Integer id : mInitMap.keySet() ) {
			DBCfg cfg = DataSupport.find(DBCfg.class, id);			
			if(cfg == null) {
				cfg = new DBCfg(id, mInitMap.get(id));
				
			} else {
				cfg.setValue(mInitMap.get(id));
			}						
			cfg.save();
		}*/
	}
	
/*	
	*//**
	 * @param cfgid
	 * @return 返回结果值
	 * @exception 检测cfgID是否合法
	 *//*
	public static boolean isCheckCfgid(int cfgid) {		
		if( ( cfgid >= IDCFG_SAVE_POS) && ( cfgid < IDCFG_BUFF ) ) {
			return true;
		}
		return false;
	}*/
}
