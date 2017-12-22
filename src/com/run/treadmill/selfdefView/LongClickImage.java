package com.run.treadmill.selfdefView;

import java.lang.ref.WeakReference;

import com.run.treadmill.util.Support;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * 长按连续响应的Button
 * Created by admin on 15-6-1.
 */
public class LongClickImage extends ImageView {

	/**
	* 长按连续响应的监听，长按时将会多次调用该接口中的方法直到长按结束
	*/
	/*private LongClickRepeatListener repeatListener;*/

	
	/**
	* 间隔时间（ms）
	*/
	/*private long intervalTime;*/
	private long intervalTime = 100;
	private MyHandler handler;

	public LongClickImage(Context context) {
		super(context);
		init();
	}

	public LongClickImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LongClickImage(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	/**
	* 初始化监听
	*/
	private void init() {
		handler = new MyHandler(this);
		setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Support.buzzerRingOnce();
				new Thread(new LongClickThread()).start();
				return true;
			}
		});
	}

	/**
	* 长按时，该线程将会启动
	*/
	private class LongClickThread implements Runnable {
		private int num;
		@Override
		public void run() {
			while (LongClickImage.this.isPressed()) {
				num++;
				if (num % 5 == 0) {
					handler.sendEmptyMessage(1);
				}
				SystemClock.sleep(intervalTime / 5);
			}
		}
	}

	/**
	* 通过handler，使监听的事件响应在主线程中进行
	*/
	private static class MyHandler extends Handler {

		private WeakReference<LongClickImage> ref;

		MyHandler(LongClickImage button) {
			ref = new WeakReference<LongClickImage>(button);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			LongClickImage button = ref.get();
			/*if (button != null && button.repeatListener != null) {
				button.repeatListener.repeatAction(button);
			}*/
			if (button != null) {
				//直接调用普通点击事件
				button.setTag(1);
				button.performClick();
			}
		}
	}

	/**
	* 设置长按连续响应的监听和间隔时间，长按时将会多次调用该接口中的方法直到长按结束
	*
	* @param listener   监听
	* @param intervalTime 间隔时间（ms）
	*/
	/*public void setLongClickRepeatListener(LongClickRepeatListener listener, long intervalTime) {
		this.repeatListener = listener;
		this.intervalTime = intervalTime;
	}

	*//**
	* 设置长按连续响应的监听（使用默认间隔时间100ms），长按时将会多次调用该接口中的方法直到长按结束
	*
	* @param listener 监听
	*//*
	public void setLongClickRepeatListener(LongClickRepeatListener listener) {
		setLongClickRepeatListener(listener, 100);
	}

	public interface LongClickRepeatListener {
		void repeatAction(View view);
	}*/

	public void setIntervalTime(long intervalTime) {
		this.intervalTime = intervalTime;
	}
}
