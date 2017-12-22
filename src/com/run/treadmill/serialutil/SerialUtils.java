package com.run.treadmill.serialutil;


import java.io.IOException;
import java.nio.ByteBuffer;

import com.run.treadmill.floatWindow.OtherBackFloatWindow;
import com.run.treadmill.floatWindow.RunCtrlFloatWindow;
import com.run.treadmill.util.StorageParam;

import android.content.Context;
import android.hardware.SerialManager;
import android.hardware.SerialPort;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


/**
 * 串口读写工具类
 * 
 * @author
 *
 */
public class SerialUtils implements Runnable {

	private static final String TAG = "SerialUtils";
	private static SerialUtils serialUtils = null;
	public Context mContext;
	private static final String PORT      = "/dev/ttyS5";
	private SerialManager mSerialManager  = null;
	private SerialPort mSerialPort        = null;
	private ByteBuffer mInputBuffer;
	private ByteBuffer mOutputBuffer;
	private int baudRate = 38400;
	
	private Thread m_ReceivePacketThread = null;
	private CommLoopBufferBase m_receiveloopbuffer;
	private ReceiveDataHandler m_receiveDataHandler;
	public SendDataHandler m_SendDataHandler;
	/*private AckReqConnectComm mAckReqConnectComm;*/

	public SendCmdHandler mSendCmdHandler;

    /**
     * 单例模式，获取实例
     * @return
     */
	public static SerialUtils getInstance() {
		synchronized (PORT) {
			if (serialUtils == null) {
				synchronized (PORT) {
					serialUtils = new SerialUtils();
				}
			}
		}
		return serialUtils;
	}

	/**
	 * 构造函数
	 */
	public SerialUtils() {

//		for (int i = 0; i < CMDS.length; i++) {
//			readCmdMap.put(CMDS[i], VALUES[i]);
//		}
		SerialCmd.initSendCmdMap();
		HandlerThread ht = new HandlerThread("sendcmd Handler Thread");
    	ht.start();
    	mSendCmdHandler = new SendCmdHandler(ht.getLooper());
	}

	/**
	 * 初始化
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		mSerialManager = (SerialManager)mContext.getSystemService("serial");
		
		mInputBuffer = ByteBuffer.allocate(1024);
        mOutputBuffer = ByteBuffer.allocate(1024);

        m_receiveloopbuffer = new CommLoopBufferBase();
        try {
        	//打开串口通信
            mSerialPort = mSerialManager.openSerialPort(PORT, baudRate);

            startReadData();
        } catch ( Exception e ) {
        	Log.e(TAG, "openSerialPort failed", e);
        }

        m_SendDataHandler = new SendDataHandler(mSerialPort, serialUtils, mOutputBuffer);

        /*m_receiveloopbuffer = new CommLoopBufferBase();*/
		m_receiveDataHandler = new ReceiveDataHandler(m_receiveloopbuffer, m_SendDataHandler);
		/*m_receiveDataHandler.startReceivePacket();*/

		/*mAckReqConnectComm = new AckReqConnectComm(m_SendDataHandler);
		mAckReqConnectComm.start();*/
		sendMesgProc(MSG_SEND_SETPWM, 1);
		sendMesgProc(MSG_SEND_SETCOUNTER, 1);
		/*m_SendDataHandler.setPWM((short)75);
		m_SendDataHandler.setCounter(StorageParam.getPwmArray(mContext), (short)ParamCons.counter.length);*/
		/*m_SendDataHandler.setCounter(ParamCons.counter, (short)ParamCons.counter.length);*/
	}

	protected void startReadData() {
//		if (mSerialPort != null) {
			m_ReceivePacketThread = new Thread(this);
			m_ReceivePacketThread.start();
//		}
	}

	
	/**
	 * 不断读取串口数据
	 */
	@Override
	public void run() {
//		int iDataLen = 1024;
//      byte[] readDataBuffer = new byte[1024];
		int iDataLen = 9;

/*		byte data = 0x00;
		byte[] readDataBuffer = { (byte)0x5A, (byte) 0xA5, 
									0x00, 0x06, 
									0x00,
									0x00, (byte) 0xCA,
									0x00, 0x00, 0x10,
									(byte) 0xA5, (byte)0x5A };
		iDataLen = readDataBuffer.length;*/

        while (iDataLen >= 0) {
            try {

            	/*if( data >= 0xFF ) {
            		data = 0x01;
            		readDataBuffer[4] = data;
            	}
        		else {
        			data += 1;
        			readDataBuffer[4] = data;
        		}
        		Log.d(TAG,"write byte data = " + SerialStringUtil.toHexString(data));
        		*/

            	/*mInputBuffer.clear();
				iDataLen = mSerialPort.read(mInputBuffer);

				if( iDataLen > 0 ) {
					byte[] readDataBuffer = new byte[iDataLen];
					mInputBuffer.get(readDataBuffer, 0, iDataLen);
					m_receiveloopbuffer.writeNByte(iDataLen, readDataBuffer);
					Log.d(TAG,"write byte data = " + 
							SerialStringUtil.byteArrayToHexString(readDataBuffer, iDataLen));

				} else {
					iDataLen = 1;
				}*/
//				m_SendDataHandler.ackConnectCmdToMcu();
            	if ( m_SendDataHandler != null ) {
            		m_SendDataHandler.getNormalData();
            	}
				Thread.sleep(10);//10ms//5ms
            } catch(  IndexOutOfBoundsException ex ) {
				ex.printStackTrace();
				m_receiveloopbuffer.resetPostion();
			} catch( InterruptedException ex ) {
        		ex.printStackTrace();
        		break;
        	} catch (Exception ex) {
				ex.printStackTrace();				
				break;
			}
        }	
	}

	public int readDataFromSerial(byte[] bytes) {
		mInputBuffer.clear();
		int iDataLen = 0;
		try {
			iDataLen = mSerialPort.read(mInputBuffer);
			/*Log.d(TAG, "readDataFromSerial iDataLen: " + iDataLen + " bytes.length : " + bytes.length);*/
		} catch (IOException e) {
			e.printStackTrace();
		}
		mInputBuffer.get(bytes, 0, iDataLen);
		/*Log.d(TAG,"readDataFromSerial byte data = " + 
				SerialStringUtil.byteArrayToHexString(bytes, iDataLen));*/
		return iDataLen;
	}
	
	/*public SendDataHandler getSendDataHandler() {
		if ( mSerialPort != null && m_SendDataHandler != null ) {
			return m_SendDataHandler;
		}
		return null;
	}*/

	public IEventProcess mIEventProcess = null;
    public synchronized void registerEventProcess(IEventProcess iEventProcess) {
    	mIEventProcess = iEventProcess;
    	Log.d(TAG,"registerEventProcess " + mIEventProcess);
	}	
	public synchronized void unregisterEventProcess(IEventProcess IEventProcess) {
		mIEventProcess = null;
		Log.d(TAG,"unregisterEventProcess " + mIEventProcess);
	}

	public void errorEventProc(int errorValue) {
		if( mIEventProcess != null ) {
			mIEventProcess.errorEventProc(errorValue);
		} else if ( mRunCtrlFloatWindow != null ) {
			mRunCtrlFloatWindow.errorEventProc(errorValue);
		} else if ( mOtherBackFloatWindow != null ) {
			mOtherBackFloatWindow.errorEventProc(errorValue);
		}
	}

	public void sendKeyCmdMesg(int keyValue) {
		Log.d(TAG,"sendKeyCmdMesg " + keyValue + " " + mIEventProcess);
		if( mIEventProcess != null ) {
			mIEventProcess.getCmdMsgHandler().sendMessage(
					mIEventProcess.getCmdMsgHandler().obtainMessage(Command.CMD_CURR_KEY, keyValue, 0));
		} else if ( mRunCtrlFloatWindow != null ) {
			mRunCtrlFloatWindow.getCmdMsgHandler().sendMessage(
					mRunCtrlFloatWindow.getCmdMsgHandler().obtainMessage(Command.CMD_CURR_KEY, keyValue, 0));
		}
	}

	public void sendCmdMesg(int cmdValue, int obj) {
		if( mIEventProcess != null ) {
			mIEventProcess.getCmdMsgHandler().sendMessage(
					mIEventProcess.getCmdMsgHandler().obtainMessage(Command.CMD_CURR_KEY, cmdValue, obj));
		}
	}

	public RunCtrlFloatWindow mRunCtrlFloatWindow = null;
    public synchronized void registerFloatWin(RunCtrlFloatWindow runCtrlFloatWindow) {
    	mRunCtrlFloatWindow = runCtrlFloatWindow;
	}	
	public synchronized void unregisterFloatWin(RunCtrlFloatWindow runCtrlFloatWindow) {
		mRunCtrlFloatWindow = null;
	}
	public OtherBackFloatWindow mOtherBackFloatWindow = null;
    public synchronized void registerOtherBackFloatWin(OtherBackFloatWindow otherBackFloatWindow) {
    	mOtherBackFloatWindow = otherBackFloatWindow;
	}	
	public synchronized void unregisterOtherBackFloatWin(OtherBackFloatWindow otherBackFloatWindow) {
		mOtherBackFloatWindow = null;
	}

	public static final int MSG_SEND_FAN = 6001;
	public static final int MSG_SEND_PWM = 6002;
	public static final int MSG_SEND_COUNTER = 6003;
	public static final int MSG_SEND_LEVEL = 6004;
	public static final int MSG_SEND_BUZZER_ON = 6005;
	public static final int MSG_SEND_BUZZER_CON = 6006;
	public static final int MSG_SEND_SETLPWM = 6007;
	public static final int MSG_SEND_SETPWM = 6008;
	public static final int MSG_SEND_SETCOUNTER = 6009;
	public static final int MSG_SEND_SETSLEEP = 6010;

	public class SendCmdHandler extends Handler {
		SendCmdHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
//        	if ( true ) {
//        		return ;
//        	}
        	Log.d(TAG, "handleMessage start " + System.currentTimeMillis());
        	switch (msg.what) {
	        	case MSG_SEND_FAN:
	        		m_SendDataHandler.setFanOnOff(msg.arg1);
	        		break;
	        	case MSG_SEND_LEVEL:
	        		m_SendDataHandler.setRunLevel(msg.arg1);
	        		break;
	        	case MSG_SEND_BUZZER_ON:
	        		m_SendDataHandler.setbuzzerOn(msg.arg1);
		        	break;
	        	case MSG_SEND_BUZZER_CON:
	        		m_SendDataHandler.setBuzzerContinu(msg.arg1);
		        	break;
	        	case MSG_SEND_SETLPWM:
	        		m_SendDataHandler.setLPWM(msg.arg1, msg.arg2);
		        	break;
	        	case MSG_SEND_SETPWM:
	        		m_SendDataHandler.setPWM((short)75);//75//20
	        		break;
	        	case MSG_SEND_SETCOUNTER:
	        		m_SendDataHandler.setCounter(StorageParam.getPwmArray(mContext), 
	        				(short)ParamCons.counter.length);
	        		break;
	        	case MSG_SEND_SETSLEEP:
	        		m_SendDataHandler.setSleep(msg.arg1);
	        		break;
	        	default:
                    break;
        	}
        	super.handleMessage(msg);
        	Log.d(TAG, "handleMessage end " + System.currentTimeMillis());
        }        
	}

	public void setFanOnOff(int flag) {
		sendMesgProc(MSG_SEND_FAN, flag);
	}
	public void setRunLevel( int level ) {
		sendMesgProc(MSG_SEND_LEVEL, level);
	}
	public void setbuzzerOn( int count ) {
    	sendMesgProc(MSG_SEND_BUZZER_ON, count);
	}
	public void setBuzzerContinu(int sec) {
		if( StorageParam.getBuzzer(mContext) ) {
			sendMesgProc(MSG_SEND_BUZZER_CON, sec);
		}
	}
	public void setLPWM(int level, int pwm) {
		sendMesgProc(MSG_SEND_SETLPWM, level, pwm);
	}
	public void setSleep(int flag) {
		sendMesgProc(MSG_SEND_SETSLEEP, flag);
	}

	public void sendMesgProc( int msg, int value) {
		Log.d(TAG,"=========sendMesgProc========" + mSendCmdHandler + " msg " + msg + " sec " + value);
		if ( mSendCmdHandler != null ) {
			mSendCmdHandler.sendMessage(
					mSendCmdHandler.obtainMessage(msg, value, 0));
		}	
	}
	public void sendMesgProc( int msg, int value1, int value2) {
		Log.d(TAG,"sendMesgProc " + mSendCmdHandler + " msg " + msg + " sec " + value1);
		if ( mSendCmdHandler != null ) {
			mSendCmdHandler.sendMessage(
					mSendCmdHandler.obtainMessage(msg, value1, value2));
		}	
	}

	public void setbuzzerOn() {
    	if( StorageParam.getBuzzer(mContext) ) {
    		setbuzzerOn(1);
    	}
	}

	public void setkeyToneOn() {
    	if( StorageParam.getkeyTone(mContext) ) {
    		setbuzzerOn(1);
    	}
	}

}
