package com.run.treadmill.serialutil;

public class SendParam {
	byte	com_func;			//功能码

	// 写控制指令
	byte	crtl_com;			//控制指令, 偏移地址

	/*byte   direction_set;  		// 设置运行方向
	byte   direction_now;		// 实际运行方向
	byte   rotate_spd_set;		// 设置运行速度
	byte   rotate_spd_now;		// 实际运行速度
	byte   rotate_spd_now;		// 实际运行速度
*/

	short	level_set;			// 设置运行阻力
	//short	level_now;			// 实际运行阻力
	short	inc_set;
	short   max_inc;
	short   minInc_ADC;
	short   maxInc_ADC;
	short   curInc_ADC;
	short	power_set;			// 设置运行功率
	short	power_now;			// 实际运行功率
	short	motor_status;		// 马达状态
	short	overload_status;	// 痉挛状态
	short	relay_status;		// 
	short   status_change;
	short	fan;				//风扇控制
	short	adcValue;

	short	curRpm;				//当前rpm

	short   version;			//版本
	short   year;				//年
	short   date;				//月日

	short   error_code;			// 出错编号
	short	run_mode;			// 运行模式

	short	flag;				// 通讯标致位, 0则表示没有被占用，1则表示被占用

	short	MaxCounter;			//椭圆机最大可设段数
	short	counter[];			//20段每段的值
	short	curCounter;			//当前counter值
	short	HrFlag;
	short	HandHr;				//有线心跳
	short	WirHr;				//无线心跳
	short	INCCmd;				//从扬声命令
	short	bufferCount;		//buffer响声次数
	short	bufferTime;			//buffer响声时间
	short	LevelCmd;
	short	pwm;
	//__bool keyTonFlag;
	byte	INCstatus;			//扬声状态
	byte	key_value;			//按键值

}
