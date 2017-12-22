package com.run.treadmill.serialutil;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 按键命令派发  Handler 处理消息
 * 
 * @author mark
 */
public final class CmdMsgHandler extends Handler {
	private final String TAG = "MyHandler";
	
	private IEventProcess mIEventProcess = null;
	
	public CmdMsgHandler(IEventProcess iEventProcess) {
		super();
		mIEventProcess = iEventProcess;
	}
	
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
	        case Command.CMD_CURR_KEY:
	        	if( mIEventProcess != null ) {
	        		mIEventProcess.onCmdEvent(msg);
	        	}
	        	break;
		}
		super.handleMessage(msg);
						
	}
}