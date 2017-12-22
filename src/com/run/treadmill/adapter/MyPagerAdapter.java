package com.run.treadmill.adapter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class MyPagerAdapter extends PagerAdapter{  

	private ArrayList<View> mViewList = new ArrayList<View>() ; 
	
	public MyPagerAdapter( ArrayList<View> ViewList ) {
		mViewList = ViewList;
	}
    @Override  
    public int getCount() {
        return mViewList.size();  
    }  

    @Override  
    public Object instantiateItem(View container, int position) {  
        //Log.i("INFO", "instantiate item:"+position); 
        View view = mViewList.get(position);
		((ViewPager) container).addView(mViewList.get(position),0);  
		return mViewList.get(position);  
    }  
      
    @Override  
    public void destroyItem(View container, int position, Object object) {  
        //Log.i("INFO", "destroy item:"+position);  
        ((ViewPager) container).removeView(mViewList.get(position));    
    }  
      
    @Override  
    public boolean isViewFromObject(View arg0, Object arg1) {  
        return arg0 == arg1;  
    }  
}
