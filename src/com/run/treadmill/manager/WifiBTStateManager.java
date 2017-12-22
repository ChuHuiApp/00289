package com.run.treadmill.manager;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

public class WifiBTStateManager {

	public static boolean isOpenBluetooth(Context context) {
		BluetoothAdapter adapter = null;
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			adapter = BluetoothAdapter.getDefaultAdapter();
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
			adapter = bluetoothManager.getAdapter();
		}
		
		if ( adapter == null ) {
			return false;
		}

		if ( adapter.isEnabled() ) {
			return adapter.disable();//断开蓝牙
		} else {
			return adapter.enable();//打开蓝牙
		}

	}

	public static boolean isNetworkConnected(Context context) {
	    ConnectivityManager connMgr = (ConnectivityManager)
	    		context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    return (networkInfo != null && networkInfo.isConnected());
	}
		
}
