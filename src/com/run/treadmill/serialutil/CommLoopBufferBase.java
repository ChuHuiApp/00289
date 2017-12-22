package com.run.treadmill.serialutil;


import java.util.Arrays;

import com.run.treadmill.util.Support;

import android.util.Log;

/**
 * @Description.循环buffer处理
 * @version. 1.0
 * @Author. tianjj
 * @History.Created on 2016-7-6 
 */
public class CommLoopBufferBase {	
	private static final String TAG = "CommLoopBufferBase";
	
	private static byte[] bDataBuffer = null; //数据缓存buffer
	private int iRead = 0;    //读数据数组下标
	private int iWrite = 0;   //写数据数组下标
	private boolean isLog = false;	
	
	public CommLoopBufferBase() {
	    iRead = 0;
	    iWrite = 0;	  
	    if ( null == bDataBuffer ) {
	    	bDataBuffer = new byte[FrameConstants.FRAME_MC_lENGTH];		    
	    }
	    Arrays.fill(bDataBuffer, (byte) 0);
	}

	public byte[] getNByte(byte[] bytes, int len, int offset) {
	    byte[] bgData = new byte[len];
	    System.arraycopy(bytes,offset,bgData,0,len);
	    return bgData;
	}
	
	public synchronized void changeReadId(int changeid ) {
		iRead += changeid;
	}	
	public synchronized void setReadId(int setvalue) {
		iRead = setvalue;
	}
	public synchronized void changeWriteId(int changeid) {
		iWrite += changeid;
	}
	public synchronized void setWriteId(int setvalue) {
		iWrite = setvalue;				
	}
	
	/**
	* @param 
	* @explain:读取3个字节的头数据(帧首字节+short型长度)
	*/
	public int readNByteHead(int iDataLen, byte[] bytes) throws IndexOutOfBoundsException{				
	    int iFreeDataLen = 0;
	    int CmpBufferLen = 8;		
	    if ( FrameConstants.FRAME_MC_LENGTH_READ_HEAD != iDataLen ) {
	        return 0;
	    }
	    if ( iWrite >= iRead ) {
	    	iFreeDataLen = iWrite - iRead;
	        if ( iFreeDataLen < iDataLen ) {	        	
	            return 0;
	        }
	        System.arraycopy(bDataBuffer,iRead,bytes,0,iDataLen);	       	
	        CmpBufferLen = Support.bytesToShortBigEnd(bytes, 0);
	
	        //帧数据不够读取一帧
	        if( iFreeDataLen < CmpBufferLen ) {
	        	if ( isLog ) {
	        		Log.e(TAG,"" + Thread.currentThread().getStackTrace()[2].getLineNumber() 
		        			+ " ,iFreeDataLen:" + Integer.toHexString(iFreeDataLen) 
		        			+ " ,CmpBufferLen:0x" + Integer.toHexString(CmpBufferLen) );
	        	}
	        	return 0;
	        }
	        changeReadId(iDataLen);
	        
	    } else {	    	
	    	iFreeDataLen = (FrameConstants.FRAME_MC_lENGTH - iRead) + iWrite;		        		                
	        if ( iFreeDataLen < iDataLen ) {
	        	if ( 0 != iFreeDataLen ) {
	        		return 0;
	        	}
	        }			        	        
	        if( (FrameConstants.FRAME_MC_lENGTH - iRead) >= iDataLen ) {
	        	System.arraycopy(bDataBuffer,iRead,bytes,0,iDataLen);	        	          
	            CmpBufferLen = Support.bytesToShortBigEnd(bytes, 0);
	
	            //帧数据不够读取一帧
	            if( iFreeDataLen < CmpBufferLen ) {
	            	if ( isLog ) {
		        		Log.e(TAG,"" + Thread.currentThread().getStackTrace()[2].getLineNumber() 
			        			+ " ,iFreeDataLen:" + Integer.toHexString(iFreeDataLen) 
			        			+ " ,CmpBufferLen:0x" + Integer.toHexString(CmpBufferLen) );
		        	}
	                return 0;
	            }
	            changeReadId(iDataLen);
	            if(FrameConstants.FRAME_MC_lENGTH == iRead) {
	            	setReadId(0);
	            }	
	            
	        } else {
	        	int frist = (FrameConstants.FRAME_MC_lENGTH - iRead);	
	        	int sec = (iDataLen - frist);
	        	System.arraycopy(bDataBuffer, iRead,bytes, 0, frist);		            
	            System.arraycopy(bDataBuffer, 0, bytes,frist,sec);
	            CmpBufferLen = Support.bytesToShortBigEnd(bytes, 0);
	
	            //帧数据不够读取一帧
	            if( iFreeDataLen < CmpBufferLen ) {	       
	            	if ( isLog ) {
		        		Log.e(TAG,"" + Thread.currentThread().getStackTrace()[2].getLineNumber() 
			        			+ " ,iFreeDataLen:" + Integer.toHexString(iFreeDataLen) 
			        			+ " ,CmpBufferLen:0x" + Integer.toHexString(CmpBufferLen) );
		        	}	          
	                return 0;
	            }
	            setReadId(sec);
	        }
	    }
	    return 1;
	}
	
	/**
	* @param 
	* @explain:读数据处理
	*/
	public int readNByte(int iDatalen,byte[] bytes,int offset) throws IndexOutOfBoundsException {		
	    int iFreeMsgDataLen = 0;
	    if ( iWrite >= iRead ) {
	    	iFreeMsgDataLen = iWrite - iRead;
	        if ( iFreeMsgDataLen < iDatalen ) {	        	
	            return 0;
	        }
	        System.arraycopy(bDataBuffer,iRead,bytes,offset,iDatalen);
	        changeReadId(iDatalen);
	        
	    } else {
	    	iFreeMsgDataLen = (FrameConstants.FRAME_MC_lENGTH - iRead) + iWrite;
	        if ( iFreeMsgDataLen < iDatalen ) {	        	
	            return 0;
	        }
	        if( (FrameConstants.FRAME_MC_lENGTH - iRead) >= iDatalen ) {
	        	System.arraycopy(bDataBuffer, iRead, bytes,offset, iDatalen);
	            changeReadId(iDatalen);
	            if ( FrameConstants.FRAME_MC_lENGTH == iRead ) {
	            	setReadId(0);
	            }
	            
	        } else {
	        	System.arraycopy(bDataBuffer, iRead, bytes, offset, 
	            		FrameConstants.FRAME_MC_lENGTH - iRead);
	            System.arraycopy(bDataBuffer, 0, bytes,
	                    ( offset + (FrameConstants.FRAME_MC_lENGTH - iRead) ),
	                    iDatalen - (FrameConstants.FRAME_MC_lENGTH - iRead) );        	
	            changeReadId(iDatalen-FrameConstants.FRAME_MC_lENGTH);
	        }
	    }
	    return 1;
	}
	
	/**
	 * @param bytes
	 * @explain:写数据处理
	 */
	public int writeNByte(int iDatalen,byte[] bytes) throws IndexOutOfBoundsException {		
		int iFreeMsgDataLen = 0; 		
	    if ( iWrite >= iRead ) {	    	
	        if ( iRead == 0 ) {
	        	iFreeMsgDataLen = (FrameConstants.FRAME_MC_lENGTH - 1) - iWrite;
	            if ( iFreeMsgDataLen < iDatalen ) {
//	            	Log.d(TAG, "writeNByte 0 ");
	                return 0;

	            } else {
	            	System.arraycopy(bytes, 0, bDataBuffer, iWrite, iDatalen);                
	                changeWriteId(iDatalen);
//	                Log.e(TAG, "writeNByte 1 ");
	            }
	        } else {
	        	iFreeMsgDataLen = (iRead - 1) + (FrameConstants.FRAME_MC_lENGTH - iWrite);
	            if ( iFreeMsgDataLen < iDatalen ) {
//	            	Log.d(TAG, "writeNByte 2 ");
	                return 0;
	                
	            } else {
	                if( (FrameConstants.FRAME_MC_lENGTH - iWrite) >= iDatalen ) {
	                	System.arraycopy(bytes, 0, bDataBuffer, iWrite, iDatalen);                    
	                    changeWriteId(iDatalen);
//	                    Log.d(TAG, "writeNByte 3 ");
	                	
	                    if( (FrameConstants.FRAME_MC_lENGTH ) == iWrite) {
	                        setWriteId(0);
	                    }
	                } else {
	                	int frist = (FrameConstants.FRAME_MC_lENGTH - iWrite);		                	
	                    int end = (iDatalen - frist);		
	                    System.arraycopy(bytes,0,bDataBuffer,iWrite,frist);
	                    System.arraycopy(bytes,frist,bDataBuffer,0,end);	                    	                    	                    
	                    changeWriteId(iDatalen - FrameConstants.FRAME_MC_lENGTH);
//	                    Log.d(TAG, "writeNByte 4 ");
	                }
	            }
	        }
	        
	    } else {
	    	iFreeMsgDataLen = iRead - iWrite - 1;
	        if ( iFreeMsgDataLen < iDatalen ) {	  
	        	Log.d(TAG,"" + Thread.currentThread().getStackTrace()[2].getLineNumber() 
	        			+ " ,iRead:" + iRead + " ,iWrite:" + iWrite 
            			+ " ,iFreeMsgDataLen:" + iFreeMsgDataLen + " ,iDatalen:" + iDatalen);	
	        	isLog = true;	        	
	        	return 0;
	            
	        } else {
	        	System.arraycopy(bytes,0,bDataBuffer,iWrite,iDatalen);        		 
	        	changeWriteId(iDatalen);
//	        	Log.d(TAG, "writeNByte 5 ");
	        }
	    }
	    isLog = false;
	    return 1;		
	}
		
	
	public synchronized void resetPostion() {
		Arrays.fill(bDataBuffer, (byte) 0);
		iWrite = iRead = 0;
	}
	
}
