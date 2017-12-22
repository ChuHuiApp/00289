package com.run.treadmill.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.run.treadmill.R;

/**
 * 图片的截取，缩放，bitmap的转换等工具类
 * 
 * @author chende
 * 
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
@SuppressLint("NewApi")
public class ImageUtil {
	/**
	 * 将Drawable转化为Bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;

	}

	public static Drawable bitmapToDrawable(Bitmap bmp) {
		return new BitmapDrawable(bmp);
	}

	public static int getNumberBitmap(Context context, int number) {
		Resources resources = context.getResources();
		int indentify = resources.getIdentifier("img_sportmode_countdown_" + number, "drawable", context.getPackageName());
		if (indentify == 0) {
			return R.drawable.img_sportmode_countdown_1;
		}
		return indentify;
	}

	/**
	 * 把View绘制到Bitmap上 http://yunfeng.sinaapp.com/?p=228
	 * 
	 * @param view
	 *            需要绘制的View
	 * @param width
	 *            该View的宽度
	 * @param height
	 *            该View的高度
	 * @return 返回Bitmap对象
	 */
	public static Bitmap getBitmapFromView(View view, int width, int height) {
		int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
		int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
		view.measure(widthSpec, heightSpec);
		view.layout(0, 0, width, height);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);

		return bitmap;
	}

	public static Bitmap getVideoThumbnail(String path, long time) {
		Bitmap bitmap = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		try {
			retriever.setDataSource(path);
			bitmap = retriever.getFrameAtTime(1 * time * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			retriever.release();
		}
		return bitmap;
	}

	public static String saveThumbnailFileFromMiniCache(File destFile, Bitmap bitmap) {
		if (bitmap == null || destFile == null)
			return null;
		String filePath = null;
		try {
			saveBitampToFile(bitmap, destFile);
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
			}
			filePath = destFile.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filePath;
	}

	public static boolean saveBitampToFile(Bitmap mBmp, File saveFile) throws IOException {
		if (saveFile.exists()) {
			saveFile.delete();
		}
		saveFile.createNewFile();
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(saveFile);
			mBmp.compress(Bitmap.CompressFormat.PNG, 70, out);
			return true;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e("ImageUtil",""+e);
				}
			}
		}
	}

}
