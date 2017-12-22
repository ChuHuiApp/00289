package com.run.treadmill.serialutil;

public class FrameConstants {
	   
    //数据包信息定义
    public static final int FRAME_MC_HEAD = 0xFF;  //包头字节
    public static final int FRAME_MC_TAIL = 0xFE;  //包尾字节
    public static final int FRAME_MC_lENGTH = 2048;       //帧总长度
    /*public static final int FRAME_MC_lENGTH_HEAD = 8;     //包头长度*/    
    /*public static final int FRAME_MC_lENGTH_TAIL = 3;     //包尾长度(CRC+Tail)*/    
    /*public static final int FRAME_MC_LENGTH_FDATA_HEAD = 10; //FData数据包的包头长度    */    
    public static final int FRAME_MC_LENGTH_READ_HEAD = 1; //读取包的前三个字节,包头(byte)+长度(short)

    //定时器信息定义
    public static final int TIMERS_MC_MAX = 3;   //消息响应超时次数;
    
    public static final int FRAME_MC_LONG_POS = 4;   //包头和长度的字节;
    
}
