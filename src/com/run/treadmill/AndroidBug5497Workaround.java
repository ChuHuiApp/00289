package com.run.treadmill;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

public class AndroidBug5497Workaround {

	public static void assistActivity(View content) {
        new AndroidBug5497Workaround(content);
    }

	private String TAG = "AndroidBug5497Workaround";
    private View mChildOfContent;
    private int usableHeightPrevious = 0;
    private ViewGroup.LayoutParams frameLayoutParams;

    private AndroidBug5497Workaround(View content) {
        if (content != null) {
            mChildOfContent = content;
            mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    possiblyResizeChildOfContent();
                }
            });
            frameLayoutParams = mChildOfContent.getLayoutParams();
        }
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        /*Log.d(TAG, "possiblyResizeChildOfContent usableHeightNow: " + usableHeightNow 
        		+ " usableHeightPrevious: "+ usableHeightPrevious );*/
        if ( usableHeightNow != usableHeightPrevious ) {
            //如果两次高度不一致
            //将计算的可视高度设置成视图的高度

        	if ( usableHeightNow < 800 ) {
                // keyboard probably just became visible
                frameLayoutParams.height = 600;
                usableHeightNow = 600;
            } else {
                // keyboard probably just became hidden
            	frameLayoutParams.height = usableHeightNow;
            }

        	/*frameLayoutParams.height = 600;*/
            mChildOfContent.requestLayout();//请求重新布局
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        //计算视图可视高度
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom);
    }

}
