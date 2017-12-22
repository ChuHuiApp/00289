package com.run.treadmill.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;

public class StorageStateManager {

	public final String TAG = "StorageStateManager";
	
	private static StorageStateManager ourInstance = null;
	
	private BroadcastReceiver mReceiver = null;
	private boolean mIsUDiskMount = false;
	private boolean uDiskIsRevEject = false;
	private String  UdiskPath = "/mnt/usbhost/Storage01/";
	private Context mContext;


	public static StorageStateManager getInstance() {
    
        if ( null == ourInstance ) {
            synchronized (StorageStateManager.class) {
                if (null == ourInstance ) {
                    ourInstance = new StorageStateManager();                    
                }
            }
        }
        return ourInstance;
    }
			
	private StorageStateManager() {
		
	}
	
	/**
	* @explain：register and unregister需要成对出现
	*/
	public void registerReceiver(Context context)
	{
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);

        intentFilter.addAction(Intent.ACTION_MEDIA_NOFS);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);
        intentFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
        intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);

        intentFilter.addDataScheme("file");
        mReceiver = new StorageBroadcastReceiver();
        context.registerReceiver(mReceiver, intentFilter);
        mContext = context;
	}
	
	/**
	* @explain：register and unregister需要成对出现
	*/
	public void unregisterReceiver(Context context)
	{
		if (mReceiver != null) {
			mContext.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
	}

	public boolean getUdiskState()
	{
		if(Storage.getUDiskTotalSpace(mContext) > 0)
			mIsUDiskMount = true;
		else
			mIsUDiskMount = false;
		return mIsUDiskMount;
	}
	
	public void setUdiskState(boolean state)
	{
		mIsUDiskMount = state;
		if( mMountCallBack != null ) {
			mMountCallBack.MountEventProc(mIsUDiskMount);
		}
	}

	public String getUdiskPath()
	{
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			return Storage.getStoragePath(mContext, "usb");
		} else {
			return UdiskPath;
		}
	}

	public MountCallBack mMountCallBack = null;
	public void setMountCallBackFun(MountCallBack cb) {
		mMountCallBack = cb;
	}
	public interface MountCallBack
    {
        public void MountEventProc(boolean mount);
    }

	/**
	* @explain：监听系统插拔SD卡，扫描SD卡广播消息
	*/
	private class StorageBroadcastReceiver extends BroadcastReceiver {			
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        Log.d(TAG, "action = " + action);

	        final Uri uri = intent.getData();
	        String path = uri.getPath();

	        if (action.equals(Intent.ACTION_MEDIA_EJECT)) {  //sd care 拔出
	            Log.d(TAG, "Intent.ACTION_MEDIA_EJECT path = " + path);
	            /*setReceiveEjectFlag(path);*/
	            if( path.contains("usb") ) {
	            	setUdiskState(false);
	            }
	        } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {  //sd care 不可以读写
	            Log.d(TAG, "Intent.ACTION_MEDIA_UNMOUNTED path = " + path);
	            /*if( !getReceiveEjectFlag(path) ) {
	            	
	            }*/
	        } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {  //sd care 插入
	            Log.d(TAG, "Intent.ACTION_MEDIA_MOUNTED = " + path);
	            /*cleanReceiveEjectFlag(path);*/
	            if( path.contains("usb") ) {
	            	UdiskPath = path;
	            	setUdiskState(true);
	            }
	        }
	    }

	}

/*
	public void setReceiveEjectFlag(String path) {
		if ( path.contains("/mnt/usbhost") ) {
			uDiskIsRevEject = true;
		}
	}

	public boolean getReceiveEjectFlag(String path) {
		if ( path.contains("/mnt/usbhost") ) {
			return uDiskIsRevEject;
		}
		return false;
	}

	public void cleanReceiveEjectFlag(String path) {
		if ( path.contains("/mnt/usbhost") ) {
			uDiskIsRevEject = false;
		}
	}*/
	
}
