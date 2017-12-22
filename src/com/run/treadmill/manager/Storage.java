package com.run.treadmill.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import android.util.Log;

public class Storage {
    private static final String TAG = "CameraStorage";

    public static final String DCIM =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
    
    public static final String V66_DCIM = "/mnt/extsd/DCIM";

    public static final String DIRECTORY = V66_DCIM + "/Camera";
    public static final String JPEG_POSTFIX = ".jpg";
    public static final String EXTERNAL = Environment.getExternalStorageDirectory().getPath();;
    public static final String UDISK_DIRECTORY = "/mnt/usbhost/Storage01/DCIM" + "/Camera";
    public static final String directory[] = {V66_DCIM + "/Camera", UDISK_DIRECTORY};
    
    public static final String DIRECTORY_PHOTO = V66_DCIM + "/Photo";
    public static final String directory_photo[] = {V66_DCIM + "/Photo", "/mnt/usbhost/Storage01/DCIM/Photo"};
    public static final String directoryLost[] = {"/mnt/extsd/LOST.DIR", "/mnt/usbhost/Storage01/LOST.DIR"};

    public static final long UNAVAILABLE = -1L;
    public static final long PREPARING = -2L;
    public static final long UNKNOWN_SIZE = -3L;
    public static final long LOW_STORAGE_THRESHOLD_BYTES = 50000000;

	
    public static int getSdcardBlockSize() {
    	 String state = Environment.getStorageState(new File("/mnt/extsd"));
    	 
    	 if(TextUtils.equals(state, Environment.MEDIA_MOUNTED)) {
    		 StatFs stat = new StatFs("/mnt/extsd");
    		 if(state != null) {
    			 return stat.getBlockSize();
    		 }
    	 }
    	 
    	 return -1;
    }

    public static void writeFile(String path, byte[] data) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            out.write(data);
        } catch (Exception e) {
            Log.e(TAG, "Failed to write data", e);
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                Log.e(TAG, "Failed to close file after write", e);
            }
        }
    }

    public static long getAvailableSpace() {
        String state = Environment.getStorageState(new File("/mnt/extsd"));
        //String state = Environment. getExternalStorageState();
//        Log.d(TAG, "External storage state=" + state);
//        Log.d(TAG, "External storage MEDIA_MOUNTED=" + Environment.MEDIA_MOUNTED);
        if (Environment.MEDIA_CHECKING.equals(state)) {
            return PREPARING;
        }
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return UNAVAILABLE;
        }

        File dir = new File(DIRECTORY);
        dir.mkdirs();

        if (!dir.isDirectory() || !dir.canWrite()) {
        	Log.d(TAG,dir.canWrite() + "=" + dir.isDirectory());
            return UNAVAILABLE;
        }

        try {
            StatFs stat = new StatFs(DIRECTORY);
            return stat.getAvailableBlocks() * (long) stat.getBlockSize();
        } catch (Exception e) {
            Log.i(TAG, "Fail to access external storage", e);
        }
        return UNKNOWN_SIZE;
    }
    public static long getTotalSpace() {
    	String state = Environment.getStorageState(new File("/mnt/extsd"));
    	//String state = Environment. getExternalStorageState();
    	Log.d(TAG, "External storage state=" + state);
    	if (Environment.MEDIA_CHECKING.equals(state)) {
    		return PREPARING;
    	}
    	if (!Environment.MEDIA_MOUNTED.equals(state)) {
    		return UNAVAILABLE;
    	}
    	
        File dir = new File(DIRECTORY);
        dir.mkdirs();
        Log.d(TAG,dir.canWrite() + "=" + dir.isDirectory());
        if (!dir.isDirectory() || !dir.canWrite()) {
            return UNAVAILABLE;
        }

    	try {
    		StatFs stat = new StatFs(DIRECTORY);
    		return stat.getBlockCount() * (long) stat.getBlockSize();
    	} catch (Exception e) {
    		Log.i(TAG, "Fail to access external storage", e);
    	}
    	return UNKNOWN_SIZE;
    }
    
    public static long getUDiskAvailableSpace() {
    	BufferedReader br = null;
    	String str = null;
    	boolean ret = false;
    	
    	try {
    		br = new BufferedReader(new FileReader(new File("/proc/mounts")));
			while((str = br.readLine()) != null) {
				 if(str.contains("/mnt/usbhost")) {
					 ret = true;
					 break;
				 }
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    	
    	if(ret == false)
    		return UNAVAILABLE;
    	
        File dir = new File(UDISK_DIRECTORY);
        dir.mkdirs();

        if (!dir.isDirectory() || !dir.canWrite()) {
        	Log.d(TAG,dir.canWrite() + "=" + dir.isDirectory());
            return UNAVAILABLE;
        }

    	try {
    		StatFs stat = new StatFs(UDISK_DIRECTORY);
    		return stat.getAvailableBlocks() * (long) stat.getBlockSize();
    	} catch (Exception e) {
    		Log.i(TAG, "Fail to access external storage", e);
    	}
    	return UNKNOWN_SIZE;
    }
    
    public static long getUDiskTotalSpace(Context mContext) {
    	BufferedReader br = null;
    	String str = null;
    	boolean ret = false;



    	/*List<VolumeInfo> list = mStorageManager.getVolumes();
        for (VolumeInfo volumeInfo : list) {
            if (volumeInfo.getType() == 0) {
                DiskInfo diskInfo = volumeInfo.getDisk();
                if (diskInfo != null && (diskInfo.isUsb())) {
                    int i = volumeInfo.getState();
                    //volumeInfo.getPath()通过这个方法就能取得路径
                   //这里的Volume就是U盘的信息了
                 }
            }
        }*/

    	String path = getStoragePath(mContext, "usb") + "test";
        /*StorageManager mStorageManager = (StorageManager) mContext
                .getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        Class<?> diskInfoClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.VolumeInfo");
            diskInfoClazz = Class.forName("android.os.storage.DiskInfo");

            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumes");

            Object result = getVolumeList.invoke(mStorageManager);
            Log.d(TAG,"getVolumeList =" + result);
            final int length = ((List)result).size();
            
            Method getDisk = storageVolumeClazz.getMethod("getDisk");
            Method getType = storageVolumeClazz.getMethod("getType");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            for (int i = 0; i < length; i++) {
            	Object storageVolumeElement = ((List)result).get(i);
            	Log.d(TAG,"getType =" + getType.invoke(storageVolumeElement));
            	int type = (int)getType.invoke(storageVolumeElement);
            	if ( type == 0 ) {

            		Method isUsb = diskInfoClazz.getMethod("isUsb");

                    Object resDiskInfo = getDisk.invoke(storageVolumeElement);

                    Log.d(TAG,"isUsb =" + isUsb.invoke(resDiskInfo));
                    Log.d(TAG,"getPath =" + getPath.invoke(storageVolumeElement));
                    
                    boolean IsUsb = (boolean)isUsb.invoke(resDiskInfo);
                    if ( resDiskInfo != null && IsUsb ) {
                    	path = (String)getPath.invoke(storageVolumeElement);
                     }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }*/
 
    	
    	Log.d(TAG,"path is  " + path);
    	File dirPath = new File(path);
    	dirPath.mkdirs();

        if (!dirPath.isDirectory() || !dirPath.canWrite()) {
        	Log.d(TAG,dirPath.canWrite() + "=" + dirPath.isDirectory());
            return UNAVAILABLE;
        } else {
        	
        	return 1;
	    	/*try {
	    		br = new BufferedReader(new FileReader(new File("/proc/mounts")));
				while((str = br.readLine()) != null) {
					 if(str.contains("/mnt/usbhost")) {
						 ret = true;
						 break;
					 }
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if(br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
	    	
	    	if(ret == false)
	    		return UNAVAILABLE;
	    	
	        File dir = new File(UDISK_DIRECTORY);
	        dir.mkdirs();
	
	        if (!dir.isDirectory() || !dir.canWrite()) {
	        	Log.d(TAG,dir.canWrite() + "=" + dir.isDirectory());
	            return UNAVAILABLE;
	        }
	        
	    	try {
	    		StatFs stat = new StatFs(UDISK_DIRECTORY);
	    		return stat.getBlockCount() * (long) stat.getBlockSize();
	    	} catch (Exception e) {
	    		Log.i(TAG, "Fail to access external storage", e);
	    	}
	    	return UNKNOWN_SIZE;
	    	*/
        }

    }

    /**
     * OSX requires plugged-in USB storage to have path /DCIM/NNNAAAAA to be
     * imported. This is a temporary fix for bug#1655552.
     */
    public static void ensureOSXCompatible() {
        File nnnAAAAA = new File(DCIM, "100ANDRO");
        if (!(nnnAAAAA.exists() || nnnAAAAA.mkdirs())) {
            Log.e(TAG, "Failed to create " + nnnAAAAA.getPath());
        }
    }

    private static Uri insertImage(ContentResolver resolver, ContentValues values) {
        Uri uri = null;
        try {
            uri = resolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Throwable th)  {
            // This can happen when the external volume is already mounted, but
            // MediaScanner has not notify MediaProvider to add that volume.
            // The picture is still safe and MediaScanner will find it and
            // insert it into MediaProvider. The only problem is that the user
            // cannot click the thumbnail to review the picture.
            Log.e(TAG, "Failed to write MediaStore" + th);
        }
        return uri;
    }
	
    public static boolean isExtsdInsert() {
    	BufferedReader br = null;
    	String str = null;
    	boolean ret = false;
    	
    	try {
    		br = new BufferedReader(new FileReader(new File("/proc/mounts")));
			while((str = br.readLine()) != null) {
				 if(str.contains("/mnt/extsd")) {
					 ret = true;
					 break;
				 }
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    	
    	return ret;
    }

    /**
     * 6.0获取外置sdcard和U盘路径，并区分
     * @param mContext
     * @param keyword  SD = "内部存储"; EXT = "SD卡"; USB = "U盘"
     * @return
     */
    public static String getStoragePath(Context mContext,String keyword) {
    	return "/storage/udiskh/";
        /*String targetpath = "";
        StorageManager mStorageManager = (StorageManager) mContext
                .getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
                   
            Object result = getVolumeList.invoke(mStorageManager);
            
            final int length = Array.getLength(result);
            
            Method getUserLabel = storageVolumeClazz.getMethod("getUserLabel");
            for (int i = 0; i < length; i++) {
                
                Object storageVolumeElement = Array.get(result, i);
                
                String userLabel = (String) getUserLabel.invoke(storageVolumeElement);
                
                String path = (String) getPath.invoke(storageVolumeElement);
                
                if(userLabel.contains(keyword)){
                    targetpath = path;
                }

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return targetpath ;*/
    }

}
