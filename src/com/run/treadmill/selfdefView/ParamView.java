package com.run.treadmill.selfdefView; 
  
import com.run.treadmill.R;
import com.run.treadmill.util.CTConstant;

import android.annotation.SuppressLint;
import android.content.Context;  
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View; 
import android.util.AttributeSet; 
import android.util.Log;
  
public class ParamView extends View {
	
//	private int itemNum = 30;
//	private int initBottom = 152;//305
//	private int initLeft = 3;//5
//	private int itemWeight = 12;//25
//	private int itemUnit = 5;//10
	private int minHistogramViewWidth = 328;
	private int midHistogramViewWidth = 546;
	private int maxHistogramViewWidth = 818;
	private int maxItemNum = 30;
	
	private int itemNum = 30;
	private int initBottom = 305;
	private int initLeft = 5;
	private float itemWeight = 25;
	private float itemUnit = 10;
	private int errorStep = 2;
	private int showItemNum = 0;
	private boolean TriangIconVisibility = true;
	
	private int itemValueArray[] = { 0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, 
										0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0, };

	public ParamView(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
    }  
  
    public ParamView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }

    public ParamView(Context context) {  
        super(context);  
    }  
	
	@SuppressLint({ "ResourceAsColor", "DrawAllocation" })
	@Override  
	protected void onDraw(Canvas canvas) {  
		super.onDraw(canvas);
		int width = getWidth();
		int height = getHeight();
		Log.i("ParamView","onDraw x= "+width+" onDraw y= "+height);
		if( width == minHistogramViewWidth ) {
			itemNum = 30;
			initBottom = 137;
			initLeft = 3;
			itemWeight = 8.8f;
			itemUnit = 3;
			errorStep = 2;
			
		} else if ( width == maxHistogramViewWidth ) {
			itemNum = 30;
			initBottom = 317;
			initLeft = 5;
			itemWeight = 25;
			itemUnit = 7;
			errorStep = 2;
		} else if ( width == midHistogramViewWidth ) {
			itemNum = 30;
			initBottom = 228;
			initLeft = 3;
			itemWeight = 16;
			itemUnit = 5;
			errorStep = 2;
		}
		// 创建画笔  
		Paint p_bule = new Paint();  
		p_bule.setColor(getResources().getColor(R.color.paint_line_bule));
		p_bule.setAntiAlias(true);
		p_bule.setStyle(Paint.Style.FILL);
		p_bule.setStrokeWidth(1);

		// 创建画笔  
		Paint p_writebule = new Paint();  
		p_writebule.setColor(getResources().getColor(R.color.paint_aqua_bule));
		p_writebule.setAntiAlias(true);
		p_writebule.setStyle(Paint.Style.FILL);
		p_writebule.setStrokeWidth(1);
		
		Bitmap bitmap_dot = BitmapFactory.decodeResource(getResources(), 
				R.drawable.img_sportmode_profile_dot_1);
		for(int i = 0; i < maxItemNum ; i++) {
			if( itemValueArray[i] == 0 ) {
				itemValueArray[i] = 1;
			}
			canvas.drawRect(initLeft + i*(itemWeight+errorStep), initBottom-itemValueArray[i]*itemUnit,
					initLeft+itemWeight+i*(itemWeight+errorStep), initBottom, p_writebule);
		}

		for(int i = 0; i < showItemNum ; i++) {
//			canvas.drawRect(initLeft + i*(itemWeight + 2), initBottom-speedItem[i]*itemUnit,
//					initLeft+itemWeight+i*(itemWeight + 2), initBottom, p_bule);
			if( itemValueArray[i] == 0 ) {
				itemValueArray[i] = 1;
			}
			canvas.drawRect(initLeft + i*(itemWeight+errorStep), initBottom-itemValueArray[i]*itemUnit,
					initLeft+itemWeight+i*(itemWeight+errorStep), initBottom, p_bule);
			if( TriangIconVisibility ) {
				if( i == (showItemNum - 1) && width == minHistogramViewWidth ) {
					canvas.drawBitmap(reSizeBmp(bitmap_dot, 0.6f), initLeft + i*(itemWeight+errorStep) -itemWeight/5*2+2, 
							27, p_bule);
				} else if ( i == (showItemNum - 1) && width == maxHistogramViewWidth ) {
					canvas.drawBitmap(reSizeBmp(bitmap_dot, 1.2f), initLeft + i*(itemWeight+errorStep) -5, 
							55, p_bule);
				} else if ( i == (showItemNum - 1) && width == midHistogramViewWidth ) {
					canvas.drawBitmap(reSizeBmp(bitmap_dot, 1.2f), initLeft + i*(itemWeight+errorStep) -5, 
							39, p_bule);
				}
			}
		}
	}

	public Bitmap reSizeBmp(Bitmap mBitmap, float size) {
		Matrix mMatrix = new Matrix();
		mMatrix.postScale(size, size);
		Bitmap resizeBmp = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
				mBitmap.getHeight(), mMatrix, true);
		return resizeBmp;
	}
	
	public void setItemValueArray(int[] mItemValueArray) {  
		itemValueArray = mItemValueArray;
    }

	public void setLevel(int type) {  

    }
	
	public void setShowItemNum(int num) {  
		showItemNum = num;
    }

	public void setTriangularIconVisibility(boolean show) {  
		TriangIconVisibility = show;
    }

}
