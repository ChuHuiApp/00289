package com.run.treadmill.manager;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.Log;

/**
 * 封装系统声音
 * @author 
 */
public class SelfAudioManager {

	private static SelfAudioManager instance;
	private AudioManager mAudioManager;
	private Context mContext;

	public static SelfAudioManager getInstance(Context context) {     
        if ( instance == null ) {
            instance = new SelfAudioManager(context);     
        }     
        return instance;     
    }

	private SelfAudioManager(Context context) {
		mContext = context;
		mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }
	
	// 获取多媒体声音大小
	public int getCurrentPro(int max) {
		/*Log.d("SelfAudioManager", "max " + max + " Curr StreamVolume " + mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + 
				" MaxVolume " + mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) );*/
		return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 
				max / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

	// 设置多媒体声音大小
	public void setAudioVolume( int progress, int max) {
		/*Log.d("SelfAudioManager", "max " + max + " Curr progress " + progress + 
				" MaxVolume " + mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) );*/
		int toset = (progress * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / max);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, toset, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
	}

	//关闭按键音
	public void setEffectsEnabled() {
		Settings.System.putInt(mContext.getContentResolver(), 
				Settings.System.SOUND_EFFECTS_ENABLED, 0);
	}

}
