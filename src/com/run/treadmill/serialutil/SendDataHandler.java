package com.run.treadmill.serialutil;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.run.treadmill.db.UserInfoManager;
import com.run.treadmill.util.Support;

import android.content.Context;
import android.hardware.SerialPort;
import android.util.Log;


/**
 * @Description.SendDataHandler
 * @version. 1.0
 * @Author. 
 * @History.
 */
public class SendDataHandler {

	private String TAG = "SendDataHandler";

	private SerialPort m_SerialPort;
	private ByteBuffer mOutputBuffer;
	private String cmdString;
	
	private SerialUtils mSerialUtils;
	public static boolean startFlag = false;
	public Context mContext;

	private SendParam mSendParam;
	private byte SrcBuf[] = new byte[ParamCons.RECEIVE_PACK_LEN_MAX];
	private byte ResultBuf[] = new byte[ParamCons.RECEIVE_PACK_LEN_MAX];
	private byte ReadBuf[] = new byte[ParamCons.RECEIVE_PACK_LEN_MAX];
	private int oldKeyValue = -1;
	
	public SendDataHandler(SerialPort mSerialPort, SerialUtils serialUtils,ByteBuffer mOutputBuffer) {
		m_SerialPort = mSerialPort;
		this.mOutputBuffer = mOutputBuffer;
		mSerialUtils = serialUtils;
		mContext = mSerialUtils.mContext;
		mSendParam = new SendParam();
		mSendParam.counter = new short[ParamCons.totalLevel];
	}

	public boolean sendCmdToMcu( byte[] bytes, int length ) {
		try {
			mOutputBuffer.clear();
		    mOutputBuffer.put(bytes);
		    m_SerialPort.write(mOutputBuffer, length);
		    cmdString = SerialStringUtil.byteArrayToHexString(bytes, length);
		    /*Log.d(TAG,"sendCmdToMcu : " + cmdString + " length: " + length);*/
		    return true;
		} catch ( Exception e ) {
		    Log.e(TAG, "write failed", e);
		    return false;
		}
	}

	/*********************************************************************************
	输入：pSrcBuf，待解包数据首地址；pResultBuf解包后数据首地址；rawPacketlen待解包
	      数据长度，单位字节。
	输出：解包结果，为TRUE表示得到一个正确的数据包，为FALSE表示得到一个校验错误的数据包
	功能：将接收到的未经处理的数据包进行解包，解包后计算CRC校验和。
	**********************************************************************************/
	public int comUnpacket(byte[] pSrcBuf, byte[] pResultBuf, int rawPacketlen ) {

	    int unpacketBufLen;
	    short receivedCRC;
	    int CRCByCal;
	    byte[] pResultBufTemp;

	    if ( rawPacketlen > 32 ) {
	        return -1;
	    }

		if ( rawPacketlen < 2 ) {
			return -1;
		}

	    pResultBufTemp = pResultBuf;
	    pResultBufTemp[0] = pSrcBuf[0];

	    int srcStep = 0;
	    int resTemStep = 0;
	    srcStep++;
	    resTemStep++;

	    rawPacketlen -= 2;// 去掉包头包尾的一个字节后，对数据进行拆分。
	    unpacketBufLen = 1;

	    while( rawPacketlen > 0 ) {
	        if ( Support.byteToInt(pSrcBuf[srcStep]) >= ParamCons.PACK_FRAME_MAX_DATA ) {

	        	srcStep++;
	        	pResultBufTemp[resTemStep] = Support.intLowToByte( ParamCons.PACK_FRAME_MAX_DATA + 
	        			Support.byteToInt(pSrcBuf[srcStep]) );
	        	srcStep++; 
	        	resTemStep++;

	            unpacketBufLen++;
	            rawPacketlen -= 2;
	        }
	        else
	        {
	        	pResultBufTemp[resTemStep] = pSrcBuf[srcStep];
	        	srcStep++;  
	        	resTemStep++;
	            
	            unpacketBufLen++;
	            rawPacketlen--;
	        }        
	        
	    }
	    /*Log.d(TAG,"comUnpacket end : " + SerialStringUtil.byteArrayToHexString(pResultBufTemp, resTemStep)
	    		+ " resTemStep: " + resTemStep);*/

		if ( unpacketBufLen > 32 ) {
			return 0;
		}
		receivedCRC = Support.bytesToShortLiterEnd(pResultBuf, unpacketBufLen-2);
	    CRCByCal = calCRCByTalbe(Support.subBytes(pResultBuf, 1, unpacketBufLen-1), 
	    				unpacketBufLen-3);	// 包头、包尾及CRC不校验

	    if ( receivedCRC==CRCByCal ) {
	    	// 如果成功，则返回解压后的长度
	    	return unpacketBufLen;
	    } else {
	    	//	__msg("receivedCRC = 0x%x, CRCByCal=0x%x\n",receivedCRC, CRCByCal);
	        return -1;
	    }      
	}

	public int read_data( byte[] ResultBuf ) {

		byte[] SrcBuf = new byte[ParamCons.RECEIVE_PACK_LEN_MAX];
		int ResultLen = 0;
		int head_offsize = 0;
		int end_offsize  = 0;
		int i = 0;

		if (ResultBuf == null) {
	        return -1;
		}

		Arrays.fill(ResultBuf, (byte) 0x00);
		Arrays.fill(SrcBuf, (byte) 0x00);

		/*ResultLen = eLIBs_fread(SrcBuf, 1, ParamCons.RECEIVE_PACK_LEN_MAX, fp);*/
		ResultLen = mSerialUtils.readDataFromSerial(SrcBuf);
		/*Log.d(TAG, "read_data end " + SerialStringUtil.byteArrayToHexString(SrcBuf, ResultLen) );*/	

		if ( ResultLen >= ParamCons.RECEIVE_PACK_LEN_MIN ) {
			for ( i = 0; i < ResultLen; i++ ) {
//				__msg("SrcBuf[%d] = 0x%x\n",i, SrcBuf[i]);	
				if ( SrcBuf[i] == (byte) 0xFF )
					head_offsize = i;
				if ( SrcBuf[i] == (byte) 0xFE )
					end_offsize = i;
			}

			/*Log.d(TAG, "read_data head_offsize: "+ head_offsize + "end_offsize: " + end_offsize);*/	
			if ( SrcBuf[head_offsize] != (byte) 0xFF || SrcBuf[end_offsize] != (byte) 0xFE ){
				return 0;
			}

			ResultLen = ( end_offsize - head_offsize ) + 1;
			/*Log.d(TAG,"read_data start: " + SerialStringUtil.byteArrayToHexString(
					Support.subBytes(SrcBuf, head_offsize, ResultLen), ResultLen) + " ResultLen " + ResultLen);*/
			ResultLen =  comUnpacket( 
					Support.subBytes(SrcBuf, head_offsize, ResultLen), ResultBuf, ResultLen);

		    /*Log.d(TAG,"read_data end : " + SerialStringUtil.byteArrayToHexString(ResultBuf, ResultLen)
		    		+ " ResultLen: "+ ResultLen );*/

			return ResultLen;
		}

		return 0;
	}

	public short crcNibbleTbl[] = {
		0x0000, 0x1081, 0x2102, 0x3183, 
		0x4204, 0x5285, 0x6306, 0x7387, 
		(short) 0x8408, (short) 0x9489, (short) 0xa50a, (short) 0xb58b, 
		(short) 0xc60c, (short) 0xd68d, (short) 0xe70e, (short) 0xf78f 
	};

	/*********************************************************************************
	输入：pSrcBuf指向求校验和的数据首地址；len数据长度，单位字节。
	输出：校验和
	功能：通过查表法，每4位查一次表，计算pSrcBuf开始，长度为len的数据的CRC校验和。
	      接收数据是低位开头，所以采用反转多项式，多项式为0x8408。
	**********************************************************************************/
	public short calCRCByTalbe(byte[] pSrcBuf, int len)
	{
		byte crcLower, i = 0;
		short crcReg = 0;

	    crcReg = (short) 0xFFFF;
	    /*Log.d(TAG,"calCRCByTalbe : " + SerialStringUtil.byteArrayToHexString(pSrcBuf, len) + " len " + len);*/

		while ( len-- != 0 ) {
		    crcLower =  (byte) ( crcReg & 0x0F );
		    /*Log.d(TAG,"calCRCByTalbe crcReg >>= 4 start " + 
		    		SerialStringUtil.byteArrayToHexString(Support.shortToBytes(crcReg), 2) );*/
		    crcReg = (short) ( ( crcReg >>= 4 ) & 0x0FFF );
			/*Log.d(TAG,"calCRCByTalbe crcReg >>= 4 start " + 
		    		SerialStringUtil.byteArrayToHexString(Support.shortToBytes(crcReg), 2) );*/
		    crcReg ^= crcNibbleTbl[ crcLower ^ ( pSrcBuf[i] & 0x0F ) ];
		    crcLower = (byte) ( crcReg & 0x0F );
		    crcReg = (short) ( ( crcReg >>= 4 ) & 0x0FFF );
		    crcReg ^= crcNibbleTbl[ crcLower ^ ( ( pSrcBuf[i] >> 4 ) & 0x0F ) ];
		    
		    /*byte[] test = new byte[2]; 
		    test[0] = (byte) (pSrcBuf[i] >> 4);
		    test[1] = (byte) ((pSrcBuf[i] >> 4 ) & 0x0F);
		    Log.d(TAG, " calCRCByTalbe.pSrcBuf[i] >> 4 :  " + 
		    		SerialStringUtil.byteArrayToHexString(test, 2) );*/
		    i++;
		    /*Log.d(TAG, "len error ? " + len);*/
		}

		return crcReg;
	}

	/*********************************************************************************
	输入：pSrcBuf，待打包数据首地址；pResultBuf打包后数据首地址；len待打包数据长度，
	      ，单位字节。
	输出：打包后，数据包的长度，长度包括包头和包尾。
	功能：将待发送的数据打包成最终要发送的数据。先求包头和校验码之间(不包含包头和校验码)
	      的校验码，追加到包尾，接着将FD到FF之间的数据进行拆分，最后求出数据包的长度。
	**********************************************************************************/
	public byte comPacket(byte pSrcBuf[], byte pResultBuf[], int len) {
	    short crc;
	    byte resultLen;

		crc = calCRCByTalbe( Support.subBytes(pSrcBuf, 1, pSrcBuf.length-1), len-1); // 校验码

	    byte[] crcByte = Support.shortToBytes( crc );
	    pSrcBuf[len] = crcByte[0]; 
	    pSrcBuf[len+1] = crcByte[1];

	    // 加入CRC后，长度增加2
	    len += 2;

		// 0xFF
	    pResultBuf[0] = pSrcBuf[0];

	    int srcStep = 0;
	    int resStep = 0;
	    srcStep ++;
	    resStep ++;

	    // 去掉包头的一个字节后，对数据进行拆分。
	    len--;
	    resultLen = 1; // oxff

	    while ( len > 0 ) {
	    	// if data > 0xFD
	        if ( Support.byteToInt(pSrcBuf[srcStep]) >= ParamCons.PACK_FRAME_MAX_DATA ) {
	            pResultBuf[resStep] = Support.intLowToByte(ParamCons.PACK_FRAME_MAX_DATA);	//当前值拆分为FD+X
	            resStep++;	//指针后移
	            pResultBuf[resStep] = Support.intLowToByte( Support.byteToInt(pSrcBuf[srcStep]) - 
	            		ParamCons.PACK_FRAME_MAX_DATA );	//拆分为X
	            srcStep++;	//指针后移
	            resStep++;	//指针后移
	            resultLen += 2;	//长度加拆分为两个字节
	        }
	        //正常数据
	        else
	        {
	        	pResultBuf[resStep] = pSrcBuf[srcStep];
	            srcStep++;
	            resStep++;   
	            resultLen++;
	        }

	        len--;
	    }

	    pResultBuf[resStep] = (byte) 0xFE;//加上包尾
	    resultLen++;

	    cmdString = SerialStringUtil.byteArrayToHexString(pResultBuf, resultLen);
	    /*Log.d(TAG,"comPacket : " + cmdString);*/
	    return resultLen;
	}

	public synchronized void com_send_thread() {

		Arrays.fill(SrcBuf, (byte) 0x00);
		Arrays.fill(ResultBuf, (byte)0x00);
		Arrays.fill(ReadBuf, (byte)0x00);

		int ReadLen = 0;
		int ResultLen = 0;		
		short get_num = 0;
		int i;

		SrcBuf[0] = (byte) ParamCons.PACK_FRAME_HEADER;
		SrcBuf[1] = (byte) mSendParam.com_func;
		// 偏移地址或者控制码
		SrcBuf[2] = (byte) mSendParam.crtl_com;

		// 功能码
		switch ( Support.byteToInt(mSendParam.com_func) ) {

			case ParamCons.COM_FUNC_WR_CTR_CMD: // 0X10, 写控制指令
				// 压缩包
				ResultLen = comPacket(SrcBuf, ResultBuf, 3);
				break;

			case ParamCons.COM_FUNC_RD_CTR_CMD: // 0X11，读控制指令
				break;
			case ParamCons.COM_FUNC_WR_ONE: // 0x20， 写一个参数
				switch ( Support.byteToInt(mSendParam.crtl_com) ) {
					case ParamCons.REG_ADDR_MAC_SELECT:
						SrcBuf[3] = (byte) ( mSendParam.run_mode & 0xFF );
						SrcBuf[4] = (byte) ( (mSendParam.run_mode >> 8) & 0xFF );
						break;
					case ParamCons.REG_ADDR_WR_LEVEL:
						SrcBuf[3] = (byte) (mSendParam.level_set & 0xFF);
						SrcBuf[4] = (byte) ((mSendParam.level_set>>8) & 0xFF);
						break;
					case ParamCons.REG_ADDR_WR_INC:
						SrcBuf[3] = (byte) (mSendParam.inc_set& 0xFF);
						SrcBuf[4] = (byte) ((mSendParam.inc_set>>8) & 0xFF);
						break;
					case ParamCons.REG_ADDR_WD_STRIDE:
						SrcBuf[3] = (byte) (mSendParam.inc_set& 0xFF);
						SrcBuf[4] = (byte) ((mSendParam.inc_set>>8) & 0xFF);
						break;
					case ParamCons.REG_ADDR_WR_MAX_INC:
						SrcBuf[3] = (byte) (mSendParam.max_inc& 0xFF);
						SrcBuf[4] = (byte) ((mSendParam.max_inc>>8) & 0xFF);
						break;
					case ParamCons.REG_ADDR_WR_MAX_ADC:
					case ParamCons.REG_ADDR_WR_MIN_ADC:
						SrcBuf[3] = (byte) (mSendParam.adcValue& 0xFF);
						SrcBuf[4] = (byte) ((mSendParam.adcValue>>8) & 0xFF);
						break;
					case ParamCons.REG_ADDR_WR_INC_CMD:
						Log.d(TAG, "huang####    incCmd =  \n"+ mSendParam.INCCmd);
						SrcBuf[3] = (byte) (mSendParam.INCCmd & 0xFF);
						SrcBuf[4] = (byte) ((mSendParam.INCCmd>>8) & 0xFF);
						break;
					case ParamCons.REG_ADDR_COMMAD:
						SrcBuf[3] = (byte) (mSendParam.LevelCmd & 0xFF);
						SrcBuf[4] = (byte) ((mSendParam.LevelCmd>>8) & 0xFF);
						break;
					case ParamCons.REG_ADDR_PWM:
						SrcBuf[3] = (byte) (mSendParam.pwm & 0xFF);
						SrcBuf[4] = (byte) ((mSendParam.pwm>>8) & 0xFF);
						break;
					case ParamCons.REG_ADDR_FAN:
						SrcBuf[3] = (byte) (mSendParam.fan & 0xFF);
						SrcBuf[4] = (byte) ((mSendParam.fan>>8) & 0xFF);
						break;
					case ParamCons.REG_ADDR_SLEEP:
						SrcBuf[3] = (byte) (mSendParam.fan& 0xFF);
						SrcBuf[4] = (byte) ((mSendParam.fan>>8) & 0xFF);
						break;
					case ParamCons.REG_ADDR_WR_BUFFER:
						SrcBuf[3] = (byte) (mSendParam.bufferCount& 0xFF);
						SrcBuf[4] = (byte) ((mSendParam.bufferCount>>8) & 0xFF);
						break;
					case ParamCons.REG_ADDR_WR_BUFFER_TIME:
						SrcBuf[3] = (byte) (mSendParam.bufferTime & 0xFF);
						SrcBuf[4] = (byte) ((mSendParam.bufferTime>>8) & 0xFF);
						break;
					case ParamCons.REG_ADDR_LEVEL_1:
					case ParamCons.REG_ADDR_LEVEL_2:
					case ParamCons.REG_ADDR_LEVEL_3:
					case ParamCons.REG_ADDR_LEVEL_4:
					case ParamCons.REG_ADDR_LEVEL_5:
					case ParamCons.REG_ADDR_LEVEL_6:
					case ParamCons.REG_ADDR_LEVEL_7:
					case ParamCons.REG_ADDR_LEVEL_8:
					case ParamCons.REG_ADDR_LEVEL_9:
					case ParamCons.REG_ADDR_LEVEL_10:
					case ParamCons.REG_ADDR_LEVEL_11:
					case ParamCons.REG_ADDR_LEVEL_12:
					case ParamCons.REG_ADDR_LEVEL_13:
					case ParamCons.REG_ADDR_LEVEL_14:
					case ParamCons.REG_ADDR_LEVEL_15:
					case ParamCons.REG_ADDR_LEVEL_16:
					case ParamCons.REG_ADDR_LEVEL_17:
					case ParamCons.REG_ADDR_LEVEL_18:
					case ParamCons.REG_ADDR_LEVEL_19:
					case ParamCons.REG_ADDR_LEVEL_20:	
					case ParamCons.REG_ADDR_LEVEL_21:
					case ParamCons.REG_ADDR_LEVEL_22:
					case ParamCons.REG_ADDR_LEVEL_23:
					case ParamCons.REG_ADDR_LEVEL_24:
					case ParamCons.REG_ADDR_LEVEL_25:
					case ParamCons.REG_ADDR_LEVEL_26:
					case ParamCons.REG_ADDR_LEVEL_27:
					case ParamCons.REG_ADDR_LEVEL_28:
					case ParamCons.REG_ADDR_LEVEL_29:
					case ParamCons.REG_ADDR_LEVEL_30:
					case ParamCons.REG_ADDR_LEVEL_31:
					case ParamCons.REG_ADDR_LEVEL_32:
					case ParamCons.REG_ADDR_LEVEL_33:
					case ParamCons.REG_ADDR_LEVEL_34:
					case ParamCons.REG_ADDR_LEVEL_35:
					case ParamCons.REG_ADDR_LEVEL_36:
					case ParamCons.REG_ADDR_LEVEL_37:
					case ParamCons.REG_ADDR_LEVEL_38:
					case ParamCons.REG_ADDR_LEVEL_39:
					case ParamCons.REG_ADDR_LEVEL_40:
						//__msg("huang########  pwm = %d \n",p_cmd_data->pwm);
						SrcBuf[3] = (byte) (mSendParam.pwm& 0xFF);
						SrcBuf[4] = (byte) ((mSendParam.pwm>>8) & 0xFF);
						break;
					default:
						break;
				}

				ResultLen = comPacket(SrcBuf, ResultBuf, 5);	// 压缩包
				break;
			case ParamCons.COM_FUNC_WR_SOME:
				switch ( Support.byteToInt(mSendParam.crtl_com) ) {
					case ParamCons.REG_ADDR_WR_DATA:
						SrcBuf[3] = (byte) (mSendParam.MaxCounter & 0xFF);
						//SrcBuf[4] = (p_cmd_data->MaxCounter >> 8) & 0xFF;
						/*#ifdef AC00286
						for(i = 0 ; i < 20 ; i++) {
							//__msg("huang#####   counter = %d \n",p_cmd_data->counter[i]);
							SrcBuf[4+i*2] = p_cmd_data->counter[i] & 0xFF;
							SrcBuf[5+i*2] = (p_cmd_data->counter[i] >> 8) & 0xFF; 
						}

						ResultLen = comPacket(SrcBuf,ResultBuf,44);	

						#else*/
						for(i = 0 ; i < ParamCons.totalLevel ; i++) {
							//__msg("huang#####   counter = %d \n",p_cmd_data->counter[i]);
							SrcBuf[4+i*2] = (byte) (mSendParam.counter[i] & 0xFF);
							SrcBuf[5+i*2] = (byte) ((mSendParam.counter[i] >> 8) & 0xFF); 
						}

						ResultLen = comPacket(SrcBuf,ResultBuf,76);	
						/*#endif*/
						//__msg("huang#####   ResultLen = %d \n", ResultLen);
						break;
				}
				break;
			case ParamCons.COM_FUNC_RD_ONE: // 0x21，读一个参数	
			case ParamCons.COM_FUNC_RD_SOME:      	
				ResultLen = comPacket(SrcBuf, ResultBuf, 3);	// 压缩包
				break;			
		}

		sendCmdToMcu(ResultBuf, ResultLen);

		i = 0;
		while ( ReadLen == 0 ) {
			i++;
			//sleep 20ms
			sleep(20);
			/*Log.d(TAG, "start read data !\n" + ReadLen);*/
			ReadLen = read_data(ReadBuf);
			/*Log.d(TAG, "read data ending !\n" + ReadLen);*/
			if ( (i % 5) == 0 ) {
				Log.d(TAG, "can't get true data!\n");
				sendCmdToMcu(ResultBuf, ResultLen);
			}
			if ( i > 12 ) {
				return;
			}
		}

		if ( ReadLen >= ParamCons.RECEIVE_PACK_LEN_MIN ) {
			//第二字节执行成功或失败
			switch ( Support.byteToInt(ReadBuf[1]) ) {
				case ParamCons.EXC_SUCCEED: //执行成功  EXC_SUCCEED    	0x00
					switch ( Support.byteToInt(ReadBuf[2]) ) { //第三字节功能码
						case ParamCons.COM_FUNC_RD_ONE:
						case ParamCons.COM_FUNC_RD_SOME:
							switch ( Support.byteToInt(ReadBuf[3]) ) {

								case ParamCons.REG_ADDR_WR_BUFFER_TIME:
									Log.d(TAG, "REG_ADDR_WR_BUFFER_TIME");
									break;
								case ParamCons.REG_ADDR_NORMAL_DATA:
									/*#if 0
									__msg("huang#####   HR = %d \n" ,  ReadBuf[5]);
									__msg("huang#####   HR1 = %d \n" ,ReadBuf[6]);
									__msg("huang#####   HR 2= %d \n" ,ReadBuf[7]);
								//	__msg("huang#####   PWM1 = %d \n" ,  ReadBuf[8]);
								//	__msg("huang#####   PWM2 = %d \n" , ReadBuf[9]);
								//	__msg("huang#####   ERR = %d \n" , ReadBuf[10]);
									#endif*/
									mSendParam.key_value = ReadBuf[4];
									/*Log.d(TAG, " key_value is : " + mSendParam.key_value);*/
									int keyValue = mSendParam.key_value & 0x000000ff;
									/*mSerialUtils.sendKeyCmdMesg(keyValue);*/
									if ( keyValue == Command.KEY_CMD_LEVEL_DEC_F_LONG_1 ||
											keyValue == Command.KEY_CMD_LEVEL_DEC_F_LONG_2 ||
											keyValue == Command.KEY_CMD_LEVEL_PLUS_F_LONG_1 ||
											keyValue == Command.KEY_CMD_LEVEL_PLUS_F_LONG_2 || 

											keyValue == Command.KEY_CMD_LEVEL_DEC_S_LONG_1 ||
											keyValue == Command.KEY_CMD_LEVEL_DEC_S_LONG_2 ||
											keyValue == Command.KEY_CMD_LEVEL_PLUS_S_LONG_1 ||
											keyValue == Command.KEY_CMD_LEVEL_PLUS_S_LONG_2 ) {
										mSerialUtils.sendKeyCmdMesg(keyValue);
									} else {
										if ( mSendParam.key_value == 0 ) {
											oldKeyValue = -1;
										}
										if ( keyValue != 0 && 
												oldKeyValue == -1 ) {
											mSerialUtils.sendKeyCmdMesg(keyValue);
											oldKeyValue = keyValue;
										}
									}
									
									mSendParam.HrFlag = ReadBuf[5];
									mSendParam.HandHr = (short) (ReadBuf[6] & 0x00ff);
									mSendParam.WirHr  = (short) (ReadBuf[7] & 0x00ff);
									if ( mSendParam.HrFlag > 0 ) {
										UserInfoManager.getInstance().ecgValue = mSendParam.HandHr;
										if ( mSendParam.WirHr > 0 ) {
											UserInfoManager.getInstance().ecgValue = mSendParam.WirHr;
										}
									} else {
										UserInfoManager.getInstance().ecgValue = 0;
									}
									mSendParam.curRpm = Support.bytesToShortLiterEnd( ReadBuf, 8);
									if ( mSendParam.curRpm > 150 ) {
										mSendParam.curRpm = 150;
									}
									UserInfoManager.getInstance().rpm = mSendParam.curRpm;
									/*mSendParam.curRpm  =  (short) ( (ReadBuf[9]<<8 ) | ReadBuf[8] );*/
									//发送错误码
									mSendParam.error_code = (short) (ReadBuf[10] & 0x00ff);
									/*Log.d(TAG, " error_code is : " + ReadBuf[10]);*/
									if ( mSendParam.error_code > 0 && 
											mSendParam.error_code <= 24 ) {
										mSerialUtils.errorEventProc( mSendParam.error_code & 0x000000ff );
									}
									
									break;
							
								case ParamCons.REG_ADDR_RD_INC_STATUS:
									mSendParam.INCstatus = ReadBuf[4];
									Log.d(TAG, " INCstatus  = %d \n" + mSendParam.INCstatus);
									break;
								case ParamCons.REG_ADDR_RD_INC_ADC:
									mSendParam.curInc_ADC= ReadBuf[4];
									Log.d(TAG, " curInc_ADC  = %d \n" + mSendParam.curInc_ADC);
									break;
								case ParamCons.REG_ADDR_RD_MIN_ADC_INC:
									mSendParam.minInc_ADC= ReadBuf[4];
									Log.d(TAG, " minInc_ADC  = %d \n" + mSendParam.minInc_ADC);
									break;
								case ParamCons.REG_ADDR_RD_MAX_ADC_INC:
									mSendParam.maxInc_ADC = ReadBuf[4];
									Log.d(TAG, " maxInc_ADC  = %d \n" + mSendParam.maxInc_ADC);
									break;
								case ParamCons.REG_ADDR_RD_COUNTER:
									mSendParam.curCounter = ReadBuf[4];
									Log.d(TAG, " curCounter  = %d \n" + mSendParam.curCounter);
									break;
								case ParamCons.REG_ADDR_EXCEPTION_MES: // 错误信息
									mSendParam.error_code = get_num;
									if ( mSendParam.error_code > 0 ) {
										Log.d(TAG, "error code = %d\n" + mSendParam.error_code);
									}
									break;
								case ParamCons.REG_ADDR_MOTOR_STATUS: // 马达状态
									mSendParam.motor_status = get_num;
									break;
								
								case ParamCons.REG_ADDR_OVERLOAD://痉挛模式
									mSendParam.overload_status = get_num;
									break;
							//	case REG_ADDR_DIRECTION_NOW: // 运行方向
									//p_cmd_data->direction_now = get_num;
								//	break;
								//case REG_ADDR_ROTATE_SPD_NOW:  // 运行速度
								//	p_cmd_data->rotate_spd_now = get_num;
								//	break;
								//case REG_ADDR_RESISTANCE_NOW: // 运行阻力
									//p_cmd_data->resistance_now = (short)(ReadBuf[5]<<8 | ReadBuf[4]);
								//	break;
								//case REG_ADDR_POWER_NOW: // 运行功率
								//	p_cmd_data->power_now = (short)(ReadBuf[5]<<8 | ReadBuf[4]);
								//	break;	
			
			
								case ParamCons.REG_ADDR_PROGRAMME_VERSION:
									mSendParam.version = Support.bytesToShortLiterEnd(ReadBuf, 4);
									/*mSendParam.version = (ReadBuf[5] << 8) | ReadBuf[4];*/
									
								break;
								case ParamCons.REG_ADDR_PROGRAMME_YEAR:
									mSendParam.year = Support.bytesToShortLiterEnd(ReadBuf, 4);
									/*mSendParam.year = (ReadBuf[5] << 8) | ReadBuf[4];*/

								break;
								case ParamCons.REG_ADDR_PROGRAMME_MONTH_DAY:
									mSendParam.date = Support.bytesToShortLiterEnd(ReadBuf, 4);
									/*mSendParam.date = (ReadBuf[5] << 8) | ReadBuf[4];*/

								break;
								//add end
							}
							break;
						case ParamCons.COM_FUNC_WR_ONE://COM_FUNC_WR_ONE       	0x40
						case ParamCons.COM_FUNC_WR_SOME:
							
							//__msg(" WR SUCESS  \n", ReadBuf[3]);			
							
							break;
						case ParamCons.COM_FUNC_WR_CTR_CMD:
							//__msg("ResultBuf:%d\n", ReadBuf[2]);
							break;
						case ParamCons.COM_FUNC_RD_CTR_CMD:
							break;
					}
					break;
				case ParamCons.EXC_FAILURE:
					Log.d(TAG, " error!!! \n");
					switch ( Support.byteToInt(ReadBuf[2]) ){
						case ParamCons.COM_FUNC_WR_ONE:
							Log.d(TAG, "clear  error   fail  !!!!!!!!\n");
							break;
						}
					break;
				case ParamCons.EXC_NO_FUN_C:
				case ParamCons.EXC_WR_FAILURE:
				case ParamCons.EXC_WR_INHIBIT:
				case ParamCons.EXC_NO_PARAMETER:
				case ParamCons.EXC_CRC_ERROR:

					mSendParam.error_code = ReadBuf[1];
					if ( mSendParam.error_code > 0 ) {
						Log.d(TAG, "uart1 faile ,err code = %d\n" + mSendParam.error_code);
					}
	
					ReadLen  = 0;
					i = 0;
					while(ReadLen == 0){
						i++;
						//sleep 20ms
						sleep(20);
						//__msg("------------com_send_thread @ read_data @ beginning--------!\n");
						ReadLen = read_data(ReadBuf);
						//__msg("------------com_send_thread @ read_data @ ending--------!\n");
						if((i % 5) == 0){
						//	__msg("can't get true data!\n");
							sendCmdToMcu(ResultBuf, ResultLen);
						}
						if ( i > 12 ) {
							return;
						}
					}
					break;
				default:
					break;
			}
		}

	}

	public void sleep( int msTime) {
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// 设置风扇
	public void setFanOnOff(int flag) {

		mSendParam.fan = (short) ( flag & 0xffff );
		/*Log.d(TAG, " mSendParam.fan " + mSendParam.fan);*/

		mSendParam.crtl_com = ParamCons.REG_ADDR_FAN;

		mSendParam.com_func = ParamCons.COM_FUNC_WR_ONE;

		com_send_thread();

	}

	public void setPWM(short pwm) {

		mSendParam.crtl_com = ParamCons.REG_ADDR_PWM;
		mSendParam.pwm = pwm ;
		mSendParam.com_func = ParamCons.COM_FUNC_WR_ONE;

		com_send_thread();
	}

	// 设置段数
	public void setCounter(short[] counter, short maxCounter) {
		int i;

		for (i = 0; i < ParamCons.totalLevel; i++) {
			mSendParam.counter[i] = (short) (counter[i] & 0x0000ffff);
		}

		Log.d(TAG, " maxCounter " + maxCounter);
		mSendParam.MaxCounter = maxCounter;
		mSendParam.com_func = ParamCons.COM_FUNC_WR_SOME;	
		mSendParam.crtl_com = ParamCons.REG_ADDR_WR_DATA;

		com_send_thread();

	}

	public void getNormalData() {

		mSendParam.crtl_com = ParamCons.REG_ADDR_NORMAL_DATA;
		mSendParam.com_func = ParamCons.COM_FUNC_RD_SOME;
		com_send_thread();

	}

	// 设置阻力
	public void setRunLevel( int level ) {

		mSendParam.crtl_com = ParamCons.REG_ADDR_WR_LEVEL;
		mSendParam.level_set = (short) ( level & 0xffff );
		mSendParam.com_func = ParamCons.COM_FUNC_WR_ONE;

		com_send_thread();

	}

	public void setbuzzerOn( int count ) {

		mSendParam.crtl_com = ParamCons.REG_ADDR_WR_BUFFER;
		Log.d(TAG, " setbuzzerOn.bufferTime " + count);
		mSendParam.bufferCount = (short) ( count & 0xffff );
		Log.d(TAG, " setbuzzerOn.bufferCount " + mSendParam.bufferCount);
		mSendParam.com_func = ParamCons.COM_FUNC_WR_ONE;
		com_send_thread();

	}

	public void setBuzzerContinu(int sec) {

		mSendParam.crtl_com = ParamCons.REG_ADDR_WR_BUFFER_TIME;
		mSendParam.bufferTime= (short) ( sec & 0xffff );
		/*Log.d(TAG, " mSendParam.bufferTime " + mSendParam.bufferTime);*/
		mSendParam.com_func = ParamCons.COM_FUNC_WR_ONE;
		Log.d(TAG, "setBuzzerContinu mSendParam.com_func " + mSendParam.com_func);
		com_send_thread();

	}

	public void setLPWM(int level, int pwm) {
		mSendParam.pwm = (short) (pwm & 0x0000ffff);
		mSendParam.com_func = (byte) ( ParamCons.COM_FUNC_WR_ONE & 0x000000ff );
		Log.d(TAG, "SetLPWM mSendParam.com_func " + mSendParam.com_func);
		mSendParam.crtl_com = (byte) ( ( ParamCons.REG_ADDR_LEVEL_1 + level ) & 0x000000ff );
		com_send_thread();
	}

	public void setSleep(int flag) {

		mSendParam.crtl_com = (byte) ( ParamCons.REG_ADDR_SLEEP & 0x000000ff );
		mSendParam.fan= (short) (flag & 0x0000ffff);
		mSendParam.com_func = (byte) ( ParamCons.COM_FUNC_WR_ONE & 0x000000ff );
		com_send_thread();
	}

	public String toString() {		
		return "SendDataHandler " + cmdString;
	}	

}
