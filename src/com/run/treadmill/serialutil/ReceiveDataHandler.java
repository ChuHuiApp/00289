package com.run.treadmill.serialutil;



import com.run.treadmill.util.Support;

import android.util.Log;


/**
 * @Description.ReceiveDataHandler
 * @version. 1.0
 * @Author. 
 * @History.
 */
public class ReceiveDataHandler implements Runnable {
	private static String TAG = "ReceiveDataHandler";

	private boolean m_bRunflag;
	private CommLoopBufferBase m_receiveloopbuffer;
	private Thread m_ReceivePacketThread = null;
	
	private byte[] bFrameHeadData = new byte[FrameConstants.FRAME_MC_LENGTH_READ_HEAD];
	private byte[] bFrameLengthData = new byte[FrameConstants.FRAME_MC_LENGTH_READ_HEAD];
	private int sHead = 0x5AA5;
	private int sTotalLen = 0;
	private byte[] bReceiveDataBuffer;
	private int RET_SUCCESS = 1;
	
	private SendDataHandler m_SendDataHandler;
	private CommProtocolHandler m_CommProtocolHandler;
	
	private int dalayMs = 5;//20ms//3ms
	
	public ReceiveDataHandler(CommLoopBufferBase mCommLoopBufferBase, SendDataHandler mSendDataHandler) {
		m_receiveloopbuffer = mCommLoopBufferBase;
		m_bRunflag = false;
		m_SendDataHandler = mSendDataHandler;
		m_CommProtocolHandler = new CommProtocolHandler(null, m_SendDataHandler);
	}
	
	public void startReceivePacket() {    	
        m_bRunflag = true;
        if ( null != m_ReceivePacketThread ) {
        	stopReceivePacket();
        }
        m_ReceivePacketThread = new Thread(this);
        m_ReceivePacketThread.start();

    }

    public void stopReceivePacket() {
        m_bRunflag = false;
        if (null != m_ReceivePacketThread ) {
            m_ReceivePacketThread.interrupt();
            m_ReceivePacketThread = null;
        }           
    }

	/**
     * 解析读取的串口指令
     * @param cmd
     */
    private void handlerLoop(byte[] strArray) {
//    	Log.d(TAG,"read byte data = " + SerialStringUtil.byteArrayToHexString(strArray, strArray.length));
    	/*m_CommProtocolHandler.parsePkg(strArray);*/
	}

	public String toString() {		
		return "ReceiveDataHandler ";
	}

	@Override
	public void run() {
		while( m_bRunflag ) {
			try {
				bFrameHeadData[0] = 0x00; 
				bFrameHeadData[1] = 0x00;
				/*bFrameHeadData[2] = 0x00;
				bFrameHeadData[3] = 0x00;*/
				sHead = 0;
				sTotalLen = 0;

				if ( RET_SUCCESS == m_receiveloopbuffer.readNByte (
						FrameConstants.FRAME_MC_LENGTH_READ_HEAD, bFrameHeadData, 0) ) {
//					Log.d(TAG,"sHead:" + sHead + SerialStringUtil.byteArrayToHexString(bFrameHeadData));
					sHead = Support.bytesToShortBigEnd(bFrameHeadData, 0);

					/*if ( sTotalLen < 0 ) {
						Log.d(TAG,"sTotalLen:" + sTotalLen + 
								", bFrameHeadData[0]:" + (bFrameHeadData[0]) + 
								", bFrameHeadData[1]:" + (bFrameHeadData[1]) + 
								", bFrameHeadData[2]:" + (bFrameHeadData[2]) + 
								", bFrameHeadData[3]:" + (bFrameHeadData[2]) );
						m_receiveloopbuffer.resetPostion();
						Thread.sleep(3);
						continue;
					} */
					if( sHead != FrameConstants.FRAME_MC_HEAD ) {
						Thread.sleep(dalayMs);
						continue;
					} else {
						if( RET_SUCCESS == m_receiveloopbuffer.readNByteHead (
								FrameConstants.FRAME_MC_LENGTH_READ_HEAD, bFrameLengthData) )
						{
							sTotalLen = Support.bytesToShortBigEnd(bFrameLengthData, 0);
							sTotalLen = 2 + 2 + sTotalLen + 2 + 2 ;
						} else {
							Thread.sleep(dalayMs);
							continue;
						}
					}

					bReceiveDataBuffer = new byte[sTotalLen];
					System.arraycopy(bFrameHeadData, 0, bReceiveDataBuffer,
							0, FrameConstants.FRAME_MC_LENGTH_READ_HEAD);
					System.arraycopy(bFrameLengthData, 0, bReceiveDataBuffer,
							2, FrameConstants.FRAME_MC_LENGTH_READ_HEAD);

					int readLen = sTotalLen - 2 * FrameConstants.FRAME_MC_LENGTH_READ_HEAD;
					if ( RET_SUCCESS == m_receiveloopbuffer.readNByte(readLen, bReceiveDataBuffer
							, 2*FrameConstants.FRAME_MC_LENGTH_READ_HEAD) ) {
						handlerLoop(bReceiveDataBuffer);
						continue;
					}
				}

				Thread.sleep(dalayMs);
			} catch(  IndexOutOfBoundsException ex ) {
				ex.printStackTrace();
				Log.d(TAG,"=========0==");
				m_receiveloopbuffer.resetPostion();				
				
			} catch( InterruptedException ex ) {
        		ex.printStackTrace();
        		break;
        	} catch (Exception ex) {
				ex.printStackTrace();				
				break;
			}
		}
//		AppLog.d(TAG);
		stopReceivePacket();
	}	

}
