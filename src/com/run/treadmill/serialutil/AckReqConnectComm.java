package com.run.treadmill.serialutil;


/**
 * @author BaonyBT91
 * @explain 每隔1秒发送回复连接命令线程
 * 
 */
public class AckReqConnectComm {
	private Thread sendCmdThread = null;
	private boolean isRunning = false;
	private SendDataHandler m_SendDataHandler;
	
	public AckReqConnectComm(SendDataHandler m_SendDataHandler){
		this.m_SendDataHandler = m_SendDataHandler;		
		Runnable runnable = new sendCmdRunner();
		sendCmdThread = new Thread(runnable);			
	}
	
	public synchronized void start() {
		isRunning = true;
		sendCmdThread.start();
	}
	
	public synchronized void stop(){
		isRunning = false;	
		sendCmdThread.interrupt();
		sendCmdThread = null;
	}
			
	/**
    * @param
    * @explain 每隔1秒发送回复连接命令线程
    */
	class sendCmdRunner implements Runnable{

		public void run() {
	        while(isRunning){

				try{
					Thread.sleep(1000);					
				} catch(InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch(Exception e){
					e.printStackTrace();	
				}
			}
	    }
	}

}
