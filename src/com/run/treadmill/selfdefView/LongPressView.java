package com.run.treadmill.selfdefView;  
  
/**   
 * @author 
 * @version 
 */  
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class LongPressView extends ImageView {  
    private int mLastMotionX, mLastMotionY;  
    // 是否移动了  
    private boolean isMoved;  
    // 是否释放了  
    private boolean isReleased;  
    // 计数器，防止多次点击导致最后一次形成longpress的时间变短  
    private int mCounter = 0;  
    // 长按的runnable  
    private Runnable mLongPressRunnable;  
    // 移动的阈值  
    private static final int TOUCH_SLOP = 100;  
  
    public LongPressView(Context context) {
        super(context);
        Log.d("LongPressView","LongPressView"); 
    }

    public LongPressView(Context context, AttributeSet attrs) {  
        super(context, attrs);
        Log.d("LongPressView","LongPressView context attrs");
        mLongPressRunnable = new Runnable() {  
        	  
            @Override  
            public void run() {
            	Log.d("LongPressView","mLongPressRunnable" + " mCounter--->>>"+mCounter + 
            			" isReleased--->>>"+isReleased + " isMoved--->>>"+isMoved );
                mCounter--;
                // 计数器大于0，说明当前执行的Runnable不是最后一次down产生的。  
                if (mCounter > 0 || isReleased || isMoved)
                    return;  
                performLongClick();// 回调长按事件  
            }
        };
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        /*int x = (int) event.getX();  
        int y = (int) event.getY();*/
    	int x = (int) event.getRawX();  
        int y = (int) event.getRawY();

        switch (event.getAction()) {  
        case MotionEvent.ACTION_DOWN:
        	Log.d("LongPressView","MotionEvent.ACTION_DOWN");
            /*mLastMotionX = x;  
            mLastMotionY = y;*/  
            mCounter++;  
            isReleased = false;  
            isMoved = false;  
            postDelayed(mLongPressRunnable, 5000);// 按下 3秒后调用线程  
            break;  
        case MotionEvent.ACTION_MOVE:
            if (isMoved)  
                break;
            /*if (Math.abs(mLastMotionX - x) > TOUCH_SLOP  
                    || Math.abs(mLastMotionY - y) > TOUCH_SLOP) {  
                // 移动超过阈值，则表示移动了  
                isMoved = true;
				removeCallbacks(mLongPressRunnable);
            }*/
            if ( !( x >= getLeft() && x <= getRight() && 
            		y >= getTop() && y <= getBottom() ) ) {  
                // 移动超过阈值，则表示移动了  
                isMoved = true;
				removeCallbacks(mLongPressRunnable);
            }
            Log.d("LongPressView","MotionEvent.ACTION_MOVE isMoved " + isMoved
            		+" getLeft() "+ getLeft() + " getRight() "+ getRight() +
            		" getTop() "+ getTop() + " getBottom() "+ getBottom());
            break;
        case MotionEvent.ACTION_UP:
        	Log.d("LongPressView","MotionEvent.ACTION_UP");
            // 释放了  
            isReleased = true;
            if( mCounter > 0 ) {
            	mCounter -- ;
            }
			removeCallbacks(mLongPressRunnable);			
            break;  
        }  
        return true;
    }

    public LongPressView(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);
        Log.d("LongPressView","LongPressView attrs defStyle");
    }

}
// public class LongMainActivity extends Activity {  
  
    // @Override  
    // public void onCreate(Bundle savedInstanceState) {  
        // super.onCreate(savedInstanceState);  
        // LinearLayout.LayoutParams layout = new LayoutParams(  
                // LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);  
        // View v = new LongPressView1(LongMainActivity.this);  
        // v.setLayoutParams(layout);  
        // setContentView(v);  
        // v.setOnLongClickListener(new View.OnLongClickListener() {  
  
            // @Override  
            // public boolean onLongClick(View v) {  
                // Toast.makeText(LongMainActivity.this, "abcdef",  
                        // Toast.LENGTH_SHORT).show();  
                // return false;  
            // }  
        // });  
    // }  
  
// }
