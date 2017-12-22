package com.run.treadmill.serialutil;

import android.os.Message;

public interface IEventProcess {
	public void onCmdEvent(Message msg);
	public void errorEventProc(int errorValue);
	public CmdMsgHandler getCmdMsgHandler();
}
