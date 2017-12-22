package com.run.treadmill.serialutil;

public class ParamCons {
	// 波特率
	public final static int COM_BAUD_RATE = 38400;
	public final static int COM_BAUD_RATE_BLUETOOTH = 115200;

	// 数据接收状态，接收包头、接收包头外的数据
	public final static int RECEIVE_CHECK_HEADER = 0;
	public final static int RECEIVE_PACK_DATA = 1;

	// 数据包开头与结束定义
	public final static byte PACK_FRAME_HEADER = (byte) 0xFF; //    帧开头
	public final static int PACK_FRAME_END = 0xFE;//    帧结束
	public final static int PACK_FRAME_MAX_DATA = 0xFD;//    帧内最大数据

	// 接收缓存状态，当接收缓存为空时，设定为0；非空时设定为接收到的数据包长度
	public final static int RX_BUF_STATUS_EMPTY = 0;

	// 数据包最大、最小长度限定
	public final static int RECEIVE_PACK_LEN_MIN = 5;
	public final static int RECEIVE_PACK_LEN_MAX = 100;

	// 数据包接收超时时间，单位 100ms
	public final static int RECEIVE_PACK_TIME_MAX = 3;

	public final static int COM_ST_IDLE = 0;// 发送空闲
	public final static int COM_ST_TX = 1;// 发送中
	public final static int COM_ST_WAP = 2;// 等待应答

	/*
	0x00：命令执行成功
	0x01：命令执行失败
	0x02：功能码不存在
	0x03：数据写入失败
	0x04：数据防写
	0x05：参数不存在
	0x80：数据校验错误
	*/
	public final static int EXC_SUCCEED = 0x00;
	public final static int EXC_FAILURE = 0x01;
	public final static int EXC_NO_FUN_C = 0x02;
	public final static int EXC_WR_FAILURE = 0x03;
	public final static int EXC_WR_INHIBIT = 0x04;
	public final static int EXC_NO_PARAMETER = 0x05;
	public final static int EXC_CRC_ERROR = 0x80;


	public final static int REG_ADDR_OVERLOAD = 0xA8;



	// 数据包内各段在数据包的偏移量
	public final static int TX_HEAHER_OFFSET = 0;
	public final static int TX_FUNCTION_OFFSET = 1;

	public final static int RX_HEAHER_OFFSET = 0;
	public final static int RX_EXC_RESULT_OFFSET = 1;
	public final static int RX_FUNCTION_OFFSET = 2;

	// 通信功能码
	public final static int COM_FUNC_WR_CTR_CMD = 0x10;	//   写控制指令， 向下控写入控制运行状态指令
	public final static int COM_FUNC_RD_CTR_CMD = 0x11;	//   读控制指令，读取下控最后收到的控制指令
	public final static int COM_FUNC_WR_ONE = 0x20;//   写一个参数，写入下控指定序号的参数
	public final static int COM_FUNC_RD_ONE = 0x21;//   读一起参数，读取下控指定序号的参数
	public final static int COM_FUNC_WR_SOME = 0x40;//   写若干个参数，写若干个参数到下控
	public final static int COM_FUNC_RD_SOME = 0x41;//   读若干个参数，从下控读若干个参数
	public final static int COM_FUNC_IDLE = 0xFF;

	// 开发人员专用
	public final static int COM_FUNC_RD_ONE_DEVELOPER = 0x46;
	public final static int COM_FUNC_WR_ONE_DEVELOPER = 0x47;

	/*
		参数位置
	*/
	public final static int REG_ADDR_DIRECTION_SET = 0;		// 转动方向设定R/W
	public final static int REG_ADDR_DIRECTION_NOW = 1;		// 实际转动方向R
	public final static int REG_ADDR_ROTATE_SPD_SET = 2;	// 转速设定，R/W
	public final static int REG_ADDR_ROTATE_SPD_NOW = 3;
	public final static int REG_ADDR_RESISTANCE_SET = 4;	// 阻力设定
	public final static int REG_ADDR_RESISTANCE_NOW = 5;
	public final static int REG_ADDR_POWER_SET = 6;			// 功率设定
	public final static int REG_ADDR_POWER_NOW = 7;

	public final static int REG_ADDR_MAC_SELECT = 0x0;		//选择机种
	public final static int REG_ADDR_ERR = 0x23;			//椭圆机出错信息
	public final static int REG_ADDR_WR_DATA = 0x4B;		//椭圆机读写最大段数、最大COUNTER数
	public final static int REG_ADDR_NORMAL_DATA = 0x4C;	//读椭圆机常态数据包序号

	public final static int REG_ADDR_RD_INC_ADC = 0x16; //读当前的ADC值
	public final static int REG_ADDR_RD_INC_STATUS = 0x17; //读椭圆机扬声状态
	public final static int REG_ADDR_RD_MIN_ADC_INC = 0x18; //读最低扬声ADC值
	public final static int REG_ADDR_RD_MAX_ADC_INC = 0x19;
	public final static int REG_ADDR_WR_INC = 0x15;	//设置该段扬声
	public final static int REG_ADDR_WR_MAX_ADC = 0x19;
	public final static int REG_ADDR_WR_MIN_ADC = 0x18;
	public final static int REG_ADDR_WR_MAX_INC = 0x1A;	//设置最大扬声
	public final static int REG_ADDR_WR_INC_CMD = 0x1B; //写从扬声命令
	public final static int REG_ADDR_WR_BUFFER = 0x1D; //写响声次数
	public final static int REG_ADDR_WR_BUFFER_TIME = 0X1F; //写响声时间
	public final static int REG_ADDR_WR_LEVEL = 0x20; //设定当前level段数
	public final static int REG_ADDR_WD_STRIDE = 0x21; //写STRIDE AD值
	public final static int REG_ADDR_RD_COUNTER = 0x22; //读取当前实际COUNTER数

	public final static int REG_ADDR_RD_ERR = 0x23; //椭圆机出错信息
	public final static int REG_ADDR_COMMAD = 0x24; //拉线器命令
	public final static int REG_ADDR_STATUS = 0x25; //拉线器状态
	public final static int REG_ADDR_PWM = 0x29; //pwm 分频数

	public final static int REG_ADDR_FAN = 0x32;	//风扇开关
	public final static int REG_ADDR_SLEEP = 0x33;   //休眠命令

	public final static int REG_ADDR_LEVEL_1 = 0x50;	//读写该段段数
	public final static int REG_ADDR_LEVEL_2 = 0x51;
	public final static int REG_ADDR_LEVEL_3 = 0x52;
	public final static int REG_ADDR_LEVEL_4 = 0x53;
	public final static int REG_ADDR_LEVEL_5 = 0x54;
	public final static int REG_ADDR_LEVEL_6 = 0x55;
	public final static int REG_ADDR_LEVEL_7 = 0x56;
	public final static int REG_ADDR_LEVEL_8 = 0x57;
	public final static int REG_ADDR_LEVEL_9 = 0x58;
	public final static int REG_ADDR_LEVEL_10 = 0x59;
	public final static int REG_ADDR_LEVEL_11 = 0x5A;
	public final static int REG_ADDR_LEVEL_12 = 0x5B;
	public final static int REG_ADDR_LEVEL_13 = 0x5C;
	public final static int REG_ADDR_LEVEL_14 = 0x5D;
	public final static int REG_ADDR_LEVEL_15 = 0x5E;
	public final static int REG_ADDR_LEVEL_16 = 0x5F;
	public final static int REG_ADDR_LEVEL_17 = 0x60;
	public final static int REG_ADDR_LEVEL_18 = 0x61;
	public final static int REG_ADDR_LEVEL_19 = 0x62;
	public final static int REG_ADDR_LEVEL_20 = 0x63;

	public final static int REG_ADDR_LEVEL_21	=	0x64; //读写该段段数
	public final static int REG_ADDR_LEVEL_22	=	0x65;
	public final static int REG_ADDR_LEVEL_23	=	0x66;
	public final static int REG_ADDR_LEVEL_24	=	0x67;
	public final static int REG_ADDR_LEVEL_25	=	0x68;
	public final static int REG_ADDR_LEVEL_26	=	0x69;
	public final static int REG_ADDR_LEVEL_27	=	0x6A;
	public final static int REG_ADDR_LEVEL_28	=	0x6B;
	public final static int REG_ADDR_LEVEL_29	=	0x6C;
	public final static int REG_ADDR_LEVEL_30	=	0x6D;
	public final static int REG_ADDR_LEVEL_31	=	0x6E;
	public final static int REG_ADDR_LEVEL_32	=	0x6F;
	public final static int REG_ADDR_LEVEL_33	=	0x70;
	public final static int REG_ADDR_LEVEL_34	=	0x71;
	public final static int REG_ADDR_LEVEL_35	=	0x72;
	public final static int REG_ADDR_LEVEL_36	=	0x73;
	public final static int REG_ADDR_LEVEL_37	=	0x74;
	public final static int REG_ADDR_LEVEL_38	=	0x75;
	public final static int REG_ADDR_LEVEL_39	=	0x76;
	public final static int REG_ADDR_LEVEL_40	=	0x77;



	//add  for controllor 20150430
	//public final static int REG_ADDR_PROGRAMME_IDENTIFIER   140
	public final static int REG_ADDR_PROGRAMME_VERSION  =  0x38;
	public final static int REG_ADDR_PROGRAMME_YEAR   = 0x39;
	public final static int REG_ADDR_PROGRAMME_MONTH_DAY  =  0x3A;

	//add  end



	public final static int REG_ADDR_MOTOR_STATUS  =  10;  // 控制器状态
	public final static int REG_ADDR_RELAY_STATUS  =  11;  // ---
	public final static int REG_ADDR_EXCEPTION_MES	= 13;  // 异常信息
	public final static int REG_ADDR_STATUS_CHANGE  = 18;  //  马达运行状态改变标志

	public final static int REG_ADDR_CURRENT_SET = 20;  // 设定电流
	public final static int REG_ADDR_CURRENT_NOW = 21;	 // 实际电流

	public final static int REG_ADDR_RUN_MODE = 26;  // 运行模式，有四种
	public final static int REG_ADDR_RUN_CMD = 27;  // 控制命令
	public final static int REG_ADDR_CURRENT_ADC = 28;
	public final static int REG_ADDR_MAIN_VOLT_ADC = 29;
	public final static int REG_ADDR_MAIN_VOLTAGE  =  30;
	public final static int REG_ADDR_H_BRIDGE_PWM  =  31;
	public final static int REG_ADDR_NO_CURRENT_ADC = 32;

	public final static int REG_ADDR_POWER_ON_1MS_TICK = 40; // 上电计时，单位1ms
	public final static int REG_ADDR_POWER_ON_10S_TICK = 41; //  上电计时，单位10s

	public final static int REG_ADDR_MAIN_WHILE_TIME = 45;
	public final static int REG_ADDR_MAIN_WHILE_MAX_TIME = 46;


	public final static int REG_ADDR_ADC_REF_VOLTAGE = 100;
	public final static int REG_ADDR_ROTATE_SPD_P = 101;
	public final static int REG_ADDR_ROTATE_SPD_I = 102;
	public final static int REG_ADDR_CURRENT_P = 103;
	public final static int REG_ADDR_CURRENT_I  =  104;

	// 运行模式
	public final static int RUN_MODE_FREE_SPEED = 0;
	public final static int RUN_MODE_CONST_SPEED = 1;
	public final static int RUN_MODE_RESISTANCE = 2;
	public final static int RUN_MODE_POWER = 3;
	public final static int RUN_MODE_POSITION_LOCK = 4;
	public final static int RUN_MODE_OC_DEBUG = 0x0A;
	public final static int RUN_MODE_RPM_DEBUG = 0x0B;
	public final static int RUN_MODE_PLACE_DEBUG = 0x0C;

	// 运行命令
	public final static int RUN_CMD_STOP = 0;
	public final static int RUN_CMD_RUN = 1;
	public final static int RUN_CMD_EMER_STOP = 2;
	public final static int RUN_CMD_LOCK = 3;
	public final static int RUN_CMD_SLEEP = 4;

	//异常信息
	public final static int ERROR_NO = 0x00;			// 无异常
	public final static int ERROR_OUTPUT_OC = 0x01;		//输出过电流
	public final static int ERROR_MOTOR_OL = 0x02;		//马达过载
	public final static int ERROR_BRAKE_OL = 0x03;		//制动电阻过载
	public final static int ERROR_LOW_VOLTAGE = 0x04;	//主电源低压
	public final static int ERROR_OVER_VOLTAGE = 0x05;	//主电源过压
	public final static int ERROR_BRAKE_LOSS = 0x06;	//制动电路失效
	public final static int ERROR_SPD_SENSE = 0x07;		//速度检测异常
	public final static int ERROR_NO_CURRENT = 0x08;	//马达未连接或驱动异常
	public final static int ERROR_OUTPUT_OL = 0x09;		//输出过载
	public final static int ERROR_SYS_OL = 0x0A;		//系统过载
	public final static int ERROR_MCU = 0x0B;			//主控异常
	public final static int ERROR_SAFEKEY_OFF = 0x20;	//安全开关断开
	public final static int ERROR_COM_TIMEOUT = 0x21;	//通信超时


	// 马达状态
	public final static int MOTOR_STATUS_IDLE = 0;	//马达处于空闲状态
	public final static int MOTOR_STATUS_RUN = 1;	// 马达处于运行状态
	public final static int MOTOR_STATUS_STOP = 2;	// 马达处于停止状态，无法运行
	public final static int MOTOR_STATUS_LOCK = 3;	// 上控已经发送停止指令，转向空闲状态


	// 运行方向
	public final static int RUN_DIR_FORWARD = 0;	// 正向
	public final static int RUN_DIR_REVERSE = 1;	// 反向

	// 阻力最大值
	public final static int RESISTANCE_MAX = 2000;

	public final static short counter[] = {
		20,61,72,76,91,96,111,120,127,
		 138,145,154,160,172,177,181,185,190,
		 199,212,220,234,242,248,254,266,280,
		 292,296,302,312,320,327,335,341,348,
	};
	 /*0,11,13,15,17,19,20,22,23,24,
		25,26,27,28,29,30,31,32,33,34,
		35,36,37,38,39,40,41,42,43,44,
		46,47,48,49,51,52,*/
	public final static int totalLevel = 36;
	public final static int maxPwm = 512;


}
