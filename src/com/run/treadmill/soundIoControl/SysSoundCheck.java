package com.run.treadmill.soundIoControl;

import com.softwinner.Gpio;

import android.content.Context;
import android.util.Log;


public class SysSoundCheck implements Runnable {
	public static final String TAG = "SysSoundCheck";

	private static SysSoundCheck ourInstance = null;
	private Thread mCheckThread = null;
		
	public static SysSoundCheck getInstance(Context context) {    
        if ( null == ourInstance ) {
            synchronized (SysSoundCheck.class) {
                if (null == ourInstance ) {
                    ourInstance = new SysSoundCheck(context);                    
                }
            }
        }
        return ourInstance;
    }
			
	private SysSoundCheck(Context context) {
		mCheckThread = new Thread(this);
		mCheckThread.start();
	}

	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(1000);//ms
				/*Log.d(TAG,"readPC18IO : " + readPC18IO());*/
				if ( readPC18IO() == 0 ) {
					writePC20IO(1);
					writePC21IO(0);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private int readPC18IO() {
		return Gpio.readGpio('C', 18);
	}
	
	private int writePC20IO( int ioValue ) {
		if ( readPC18IO() == 0 ) {
			return -1;
		}
		return Gpio.writeGpio('C', 20, ioValue);
	}
	
	private int writePC21IO( int ioValue ) {
		if ( readPC18IO() == 0 ) {
			return -1;
		}
		return Gpio.writeGpio('C', 21, ioValue);
	}

	public void initSysSoundOut() {
		if ( readPC18IO() == 1 ) {
			writePC20IO(1);
			writePC21IO(1);
		}
	}

	public void hdmiSoundOut() {
		if ( readPC18IO() == 1 ) {
			writePC20IO(0);
			writePC21IO(1);
		}
	}

}
