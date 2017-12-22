package com.run.treadmill.videoControl;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.run.treadmill.R;
import com.run.treadmill.runningModeActivity.RunningVRActivity;
import com.run.treadmill.util.CTConstant;

import android.app.Activity;
import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
//import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;

public class VideoPlayerSelf implements SurfaceHolder.Callback {
	
	private String TAG = "VideoPlayerSelf";
//	private final String SAMPLE = "/mnt/sdcard/DCIM" + "/Austria_Hallstatt.mp4";
//	private final String SAMPLE = "/mnt/media_rw/card" + "/Austria_Hallstatt.mp4";
	private String SAMPLE = "/mnt/sdcard/DCIM" + "/00.mp4";
	private PlayerThread mPlayer = null;
	private MediaExtractor extractor;
	private MediaCodec decoder;
	private int TIME = 40;
	private int waitTime = TIME + TIME / 10 * 5;
	private SurfaceView sv;
	private boolean isFirst = true;
	private LinearLayout mLinearLayout ;

	//ms
	/*private long duration = 0l;*/
	private Context mContext;
	private int oldTimeSample = -1;
	private int curTimeSample = 0;


	public VideoPlayerSelf (Context context, int resId, LinearLayout linearLayout,
			String src) {
		SAMPLE = src;
		/*duration = getVideoDuration(SAMPLE);*/
		mContext = context;
		sv = (SurfaceView) ((Activity) context).findViewById(resId);
		sv.getHolder().addCallback(this);
		mLinearLayout = linearLayout;
		mLinearLayout.setBackgroundResource(R.drawable.bk_3);

	}

	public synchronized void videoPlayerStart() {
		Log.d(TAG,"videoPlayerStart ==" + mPlayer);
		if( mPlayer != null && !mPlayer.isPalying ) {
			
			mPlayer.startThread();
			mLinearLayout.setBackgroundResource(R.color.black);
			mPlayer.isPalying = true;
		}
	}

	public synchronized void videoPlayerStartPause() {
		Log.d(TAG,"videoPlayerStartPause ==" + mPlayer);
		if( mPlayer != null && mPlayer.isPalying ) {
			mPlayer.pauseThread();
		}
	}

	public void setSpeedCtrl(int amount) {
//		Log.d(TAG,"onAmountChange " + amount  );
    	if( amount >= 10 ) {
    		waitTime = TIME - TIME / 10 * ( amount - 10 );
    	} else if ( amount < 10 && amount >= 5 ) {
    		waitTime = TIME + TIME / 10 * ( 10 - amount );
    	}
	}

	public synchronized void onRelease() {
		try {
			Log.d(TAG,"onRelease ==" + mPlayer);
			if( mPlayer != null ) {
				mPlayer.interrupt();
				mPlayer = null;
			}
			if ( decoder != null ) {
				decoder.stop();
				decoder.release();
				decoder = null;
			}
			if ( extractor != null ) {
				extractor.release();
				extractor = null;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (mPlayer == null) {
			mPlayer = new PlayerThread(holder.getSurface());
			Log.d(TAG,"===========surfaceChanged============");
			mPlayer.start();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mPlayer != null) {
			mPlayer.interrupt();
		}
	}

	private class PlayerThread extends Thread {
		
		private Surface surface;
		ByteBuffer[] inputBuffers;
		ByteBuffer[] outputBuffers;
		boolean isPalying = false;
		
		private Lock lock = null;
		private Condition notEmpt = null;
		private boolean blockFlag = true;

		public PlayerThread(Surface surface) {
			this.surface = surface;
			lock = new ReentrantLock();
    		notEmpt = lock.newCondition();
			Log.d(TAG,"===========PlayerThread=======0=====");
		}
		
		public void startThread() {
    		blockFlag = false;
    		synchronized(notEmpt) {
    			notEmpt.notify();
    		}
    	}
		
		public void pauseThread() {
    		blockFlag = true;
    	}

		@Override
		public void run() {
			isPalying = true;
			extractor = new MediaExtractor();
			try {
				extractor.setDataSource(SAMPLE);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			for (int i = 0; i < extractor.getTrackCount(); i++) {
				MediaFormat format = extractor.getTrackFormat(i);
				String mime = format.getString(MediaFormat.KEY_MIME);
				if (mime.startsWith("video/")) {
					extractor.selectTrack(i);
					try {
						decoder = MediaCodec.createDecoderByType(mime);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					decoder.configure(format, surface, null, 0);
					break;
				}
			}

			if (decoder == null) {
				Log.e(TAG, "Can't find video info!");
				return;
			}

			decoder.start();
			/*try {
				sleep(100);
				resetVideoPlay();
				sleep(100);
			} catch (InterruptedException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}*/

			inputBuffers = decoder.getInputBuffers();
			outputBuffers = decoder.getOutputBuffers();
			BufferInfo info = new BufferInfo();
			
			while (!Thread.interrupted()) {
				try {

					try {
						if ( isPalying ) {
							int inIndex = decoder.dequeueInputBuffer(0);
							if (inIndex >= 0) {
								ByteBuffer buffer = inputBuffers[inIndex];
								int sampleSize = extractor.readSampleData(buffer, 0);
	//							Log.d(TAG, "InputBuffer BUFFER_FLAG_END_OF_STREAM"+sampleSize + "----"+extractor.getSampleTime());
								if (sampleSize < 0) {
									// We shouldn't stop the playback at this point, just pass the EOS
									// flag to decoder, we will get it again from the
									// dequeueOutputBuffer
									Log.d(TAG, "InputBuffer BUFFER_FLAG_END_OF_STREAM");
									decoder.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
	//								isPalying = false;
									{
										resetVideoPlay();
	
										continue ;
									}
								} else {
//									Log.d(TAG," extractor.getSampleTime() = "+ extractor.getSampleTime());
									/*sendTimeMesg(extractor.getSampleTime());*/
									decoder.queueInputBuffer(inIndex, 0, sampleSize, extractor.getSampleTime(), 0);
									extractor.advance();
								}
							}
						}
	
						/*if( isFirst ) {
							sv.setBackgroundResource(0);
							isFirst = false;
						}*/
						int outIndex = decoder.dequeueOutputBuffer(info, 0);
						
						switch (outIndex) {
						case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
							Log.d(TAG, "INFO_OUTPUT_BUFFERS_CHANGED");
							outputBuffers = decoder.getOutputBuffers();
							break;
						case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
							Log.d(TAG, "New format " + decoder.getOutputFormat());
							break;
						case MediaCodec.INFO_TRY_AGAIN_LATER:
	//						Log.d(TAG, "dequeueOutputBuffer timed out!");
							break;
						default:
							ByteBuffer buffer = outputBuffers[outIndex];
	//						Log.v(TAG, "We can't use this buffer but render it due to the API limit, " + buffer);
	
	
							/*sleep(waitTime);*/
							sleep(40);
//							Log.d(TAG, "onAmountChange waitTime "+waitTime);
							
							decoder.releaseOutputBuffer(outIndex, true);
							break;
						}
	
						// All decoded frames have been rendered, we can stop playing now
						if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
							Log.d(TAG, "OutputBuffer BUFFER_FLAG_END_OF_STREAM");
							break;
						}
					} catch ( Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					synchronized(notEmpt) {
							
						if( blockFlag ) {
							Log.v(TAG, "the value is wait ");
							isPalying = false;
							notEmpt.wait();
							isPalying = true;
						}
						
					}

				} catch (InterruptedException e) {  
                        e.printStackTrace();
				} finally {

				}
				
			}

			/*decoder.stop();
			decoder.release();
			extractor.release();*/
		}
	
		public  void resetVideoPlay() {
			decoder.stop();
			decoder.release();
			extractor.release();

			extractor = new MediaExtractor();
			try {
				extractor.setDataSource(SAMPLE);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			for (int i = 0; i < extractor.getTrackCount(); i++) {
				MediaFormat format = extractor.getTrackFormat(i);
				String mime = format.getString(MediaFormat.KEY_MIME);
				if (mime.startsWith("video/")) {
					extractor.selectTrack(i);
					try {
						decoder = MediaCodec.createDecoderByType(mime);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					decoder.configure(format, surface, null, 0);
					break;
				}
			}

			if (decoder == null) {
				Log.e(TAG, "Can't find video info!");
				return;
			}

			decoder.start();

			inputBuffers = decoder.getInputBuffers();
			outputBuffers = decoder.getOutputBuffers();
		}
	}

    /*public long getVideoDuration(String path) {
    	MediaMetadataRetriever retriever = null;
    	long duration = 0l;
    	try {
			retriever = new MediaMetadataRetriever();
		    retriever.setDataSource(path);
		    String val = retriever.extractMetadata(
		            MediaMetadataRetriever.METADATA_KEY_DURATION);
		    duration = (val == null) ? duration : Long.parseLong(val);
		} catch (RuntimeException ex) {
		    Log.e(TAG, "MediaMetadataRetriever.setDataSource() fail:"
		            + ex.getMessage());
		}catch(Exception e1) {
		        Log.e(TAG, "MediaMetadataRetriever.setDataSource() fail:"
		                + e1.getMessage());
		} finally {
			if(retriever != null) {
				retriever.release();
			}
		}
    	return duration;
    }*/

    public void sendTimeMesg(long time) {

    	curTimeSample = (int)(time / 1000000 / 5);
    	if ( oldTimeSample != curTimeSample ) {
    		oldTimeSample = curTimeSample;
    		((RunningVRActivity)mContext).sendMesgProc(CTConstant.MSG_VR_FRESH_TIME, curTimeSample);
    		/*Log.d(TAG," extractor.curTimeSample() = "+ curTimeSample);*/
    	}

    }
	
}