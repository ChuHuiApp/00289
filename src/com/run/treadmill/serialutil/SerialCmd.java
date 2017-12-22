package com.run.treadmill.serialutil;

import java.util.HashMap;
import java.util.Map;

/**
 * 所有串口相关的命令常量定义
 * 
 * @author 
 *
 */

public class SerialCmd {
	
	//指令码与指令值键值表
	public static Map<Integer, Integer> cmdAndValuesMap = new HashMap<Integer, Integer>();
	
	
	//填充键值表((根据指令码动态修改)
	public static void initSendCmdMap() {
		cmdAndValuesMap.put(CMD_START, CMD_VALUES_START);
		cmdAndValuesMap.put(CMD_STOP, CMD_VALUES_STOP);
		cmdAndValuesMap.put(CMD_SET_SPEED, CMD_VALUES_SET_SPEED);
		cmdAndValuesMap.put(CMD_READ_SPEED, CMD_VALUES_READ_SPEED);
		cmdAndValuesMap.put(CMD_RESET, CMD_VALUES_RESET);
		cmdAndValuesMap.put(CMD_CHECK_ERR, CMD_VALUES_CHECK_ERR);
		cmdAndValuesMap.put(CMD_RISE_DROP_ELECTRICAL_LIMITS, CMD_VALUES_RISE_DROP_ELECTRICAL_LIMITS);
		cmdAndValuesMap.put(CMD_RISE_DROP_ELECTRICAL_SUBSECTION, CMD_VALUES_RISE_DROP_ELECTRICAL_SUBSECTION);
		cmdAndValuesMap.put(CMD_RISE_DROP_ELECTRICAL_LOCATION, CMD_VALUES_RISE_DROP_ELECTRICAL_LOCATION);
		cmdAndValuesMap.put(CMD_RISE_DROP_ELECTRICAL_QUERY, CMD_VALUES_RISE_DROP_ELECTRICAL_QUERY);
		cmdAndValuesMap.put(CMD_CHECK_ERR2, CMD_VALUES_CHECK_ERR2);
		cmdAndValuesMap.put(CMD_PARA_CHECK, CMD_VALUES_PARA_CHECK);
		
	}
	
	//指令码
	public static final int CMD_START = 0x81; //开始
	public static final int CMD_STOP  = 0x82; //停止
	public static final int CMD_SET_SPEED  = 0x83; //设置速度
	public static final int CMD_READ_SPEED  = 0x84; //读取运行速度
	public static final int CMD_RESET  = 0x85; //复位
	public static final int CMD_CHECK_ERR  = 0x86; //查询故障1
	public static final int CMD_RISE_DROP_ELECTRICAL_LIMITS  = 0x87; //升降电机检测范围
	public static final int CMD_RISE_DROP_ELECTRICAL_SUBSECTION  = 0x88 ; //升降电机分段
	public static final int CMD_RISE_DROP_ELECTRICAL_LOCATION  = 0x89  ; //升降电机段位定位
	public static final int CMD_RISE_DROP_ELECTRICAL_QUERY  = 0x8a  ;//升降电机段位查询
	public static final int CMD_CHECK_ERR2  = 0x8b; //查询故障2
	public static final int CMD_PARA_CHECK  = 0x8C; //

	public static final int CMD_VALUES_START = 0x5C; //开始
	public static final int CMD_VALUES_STOP  = 2; //停止
	public static final int CMD_VALUES_SET_SPEED  = 0x5B; //设置速度
	public static final int CMD_VALUES_READ_SPEED  = 4; //读取运行速度
	public static final int CMD_VALUES_RESET  = 5; //复位
	public static final int CMD_VALUES_CHECK_ERR  = 6; //查询故障
	public static final int CMD_VALUES_RISE_DROP_ELECTRICAL_LIMITS  = 7; //升降电机检测范围
	public static final int CMD_VALUES_RISE_DROP_ELECTRICAL_SUBSECTION  = 8 ; //升降电机分段
	public static final int CMD_VALUES_RISE_DROP_ELECTRICAL_LOCATION  = 0x57  ; //升降电机段位定位
	public static final int CMD_VALUES_RISE_DROP_ELECTRICAL_QUERY  = 10  ;//升降电机段位查询
	public static final int CMD_VALUES_CHECK_ERR2  = 11; //查询故障
	public static final int CMD_VALUES_PARA_CHECK  = 3; //参数查询命令字
	

	
	
}
