package com.run.treadmill.serialutil;


import java.util.Map;

import com.run.treadmill.db.UserInfoManager;
import com.run.treadmill.util.Support;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;


/**
 * @Description.
 * @version. 1.0
 * @Author. 
 * @History.Created on 
 */
public class CommProtocolHandler {	
    private final String TAG = "CommProtocolHandler";
    private int cmdLength;
    private int cmdCheckSum;
    private int cmdMode;
    private int cmdCommand;
    private byte[] cmdRawData;
    private SendDataHandler mSendDataHandler;
    
//    private static Map<Integer, > m_ResourceHanldeMap = new ArrayMap<Integer, ResourceCMDHandlerable>();
    

    public CommProtocolHandler(Context mContext, SendDataHandler mSendDataHandler) {    	
        /*loaderMapHandle(mContext);*/
    	this.mSendDataHandler = mSendDataHandler;
    }

    @SuppressLint("UseValueOf") 
    public void loaderMapHandle(Context mContext) {
    	/*m_ResourceHanldeMap.put(new Integer(Support.getUnsignedByteFromByte(MCConstants.F_MC_SYSTIME))
    			,new SystemTimeCMDHandler());
    	
    	m_ResourceHanldeMap.put(new Integer(Support.getUnsignedByteFromByte(MCConstants.F_MC_V40_MCU_CAR_IR))
    			,new V40CarAndIRCMDHandler(mContext));
    	
    	m_ResourceHanldeMap.put(new Integer(Support.getUnsignedByteFromByte(MCConstants.F_MC_CAN_CONF))
    			,new CanConfCMDHandler(mContext));  
    	
    	m_ResourceHanldeMap.put(new Integer(Support.getUnsignedByteFromByte(MCConstants.F_MC_MCU_VERSION))
    			,new VersionCMDHandler(mContext));*/
    	    	
    }

    /**
     * @param bhead:帧数据首字节
     * @return 
     * @explain 判断字节是否为帧数据的首字节
     */
    public boolean isHead(byte bhead) {
        if (bhead == FrameConstants.FRAME_MC_HEAD){
            return true;
        }
        return false;
    }

    /**
     * @param btail:帧数据尾字节
     * @return 
     * @explain判断字节是否为帧数据的尾字节
     */
    public boolean isTail(byte btail) {
        if (btail == FrameConstants.FRAME_MC_TAIL) {
            return true;
        }
        return false;
    }

    /**
     * @param sCRC数据长度;bytes:数据内容
     * @return Turn is true, false is false
     * @explain CRC校验
     */
    public boolean isCRC(short src ,byte[] bytes) {
    	/*int dst = Support.bytesToShortBigEnd(bytes, 0);    	
    	if ( src == dst ) {
    		return true;
    	}
    	Log.e(TAG,"isCRC function in src:" + src + " ,dst:" + dst);*/
    	return false;    	
    }

    /**
     * @param 帧数据内容
     * @return
     * @explain:校验帧数据长度
     */
    public boolean isCheckDataLength(byte[] bData) {
    	/*if (bData.length > 5) {
        	int sFrameDataLen = 0;
            byte[] bsrcData = new byte[2];
            byte[] bdstData = new byte[2];
            bsrcData[0] = bData[1];
            bsrcData[1] = bData[2];
            bdstData[0] = (byte)(~bData[3]);
            bdstData[1] = (byte)(~bData[4]);
            sFrameDataLen = Support.bytesToShortBigEnd(bData, 1);
            if (sFrameDataLen < FrameConstants.FRAME_MC_lENGTH){
            	if ( (bsrcData[0] == bdstData[0]) &&
                        (bsrcData[1] == bdstData[1] ) ) {                	
                    return true;
                }
            }
        }*/
        return false;
    }

    /**
     * @param bytes:帧头数据
     * @return
     * @explain:校验帧头数据
     */
    public boolean isCheckFrameHead(byte[] bytes) {
        /*try{
            if (bytes.length == FrameConstants.FRAME_MC_lENGTH_HEAD ) {
                if ( false == isHead(bytes[0]) ) {
                	Log.e(TAG,"isCheckFrameHead function head:" + bytes[0]);
                    return false;
                }
                if ( false == isCheckDataLength(bytes) ) {
                    return false;
                }
                return true;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }*/
        return false;
    }
    
    /**
     * @param bytes:帧数据
     * @param btail:帧尾数据(CRC+尾字节)
     * @param length:数据长度
     * @return
     * @explain:The frame header data monitoring
     */
    public boolean isCheckFrameCRC(byte[] bytes, byte[] btail, int length) {
    	
    	/*int sCRCData = Support.getCRC16Default(bytes,length);        	
    	if ( isCRC((short)sCRCData,btail) ) {
    		if ( isTail(btail[2]) ) {
        		return true;   
        		
    		} else {
    			Log.e(TAG,"isCheckFrameCRC isTail failed in tail0:"+ btail[0] + " ,btail1:" + btail[1]);
        	}
    	} else {
    		Log.e(TAG,"isCheckFrameCRC isCRC failed in sCRCData:" + sCRCData);
    		
    	}    	
    	for (int i=0; i < bytes.length; i++) {
    		Log.i(TAG,"isCheckFrameCRC in bytes i:" + i + " ,bytes:" + (bytes[i]&0x0FF));
    	}*/
    	return true;
    }


    /**
     * @param bytes
     * @return
     * @explain:帧数据解包
     */
    public boolean parsePkg(byte[] bytes) {
       /* try{
        	int iDataLen = bytes.length;
        	int bframeDataLen = bframeData.length;
        	if ( iDataLen != (bframeheadData.length+bframeDataLen+btailData.length) ) {
        		Log.d(TAG,"parsePkg failed because DataLen is error!");
        		return false;
        	}
        	
            //解包出帧头数据
            System.arraycopy(bytes,0,bframeheadData,0,FrameConstants.FRAME_MC_lENGTH_HEAD);
             
            //解包出帧有效数据
            System.arraycopy(bytes,FrameConstants.FRAME_MC_lENGTH_HEAD,bframeData,0,bframeDataLen);
            
            //解析出帧尾数据
            System.arraycopy(bytes,(FrameConstants.FRAME_MC_lENGTH_HEAD+bframeDataLen)
            		,btailData,0,FrameConstants.FRAME_MC_lENGTH_TAIL);            
            return true;   
            
        }catch (Exception ex){
            ex.printStackTrace();
        }*/

    	/*if( true )
    	return false;*/
    	int command = Support.bytesToShortBigEnd(bytes, 5);
//    	if( command == 204 || command == 3 )
    	/*Log.d(TAG,"command : " + command);*/

//        Log.e(TAG,"parsePkg failed !");
        return false;
    }

}
