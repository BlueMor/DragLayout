package com.bluemor.reddotface.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.bluemor.reddotface.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class Util {

	public static void initImageLoader(Context context) {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_face)
				.showImageForEmptyUri(R.drawable.default_face)
				.showImageOnFail(R.drawable.default_face).cacheInMemory(true)
				.considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300, true, true, true))
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
				context).defaultDisplayImageOptions(defaultOptions)
				.memoryCache(new WeakMemoryCache());
		ImageLoaderConfiguration config = builder.build();
		ImageLoader.getInstance().init(config);
	}

	@SuppressWarnings("deprecation")
	public static ArrayList<String> getGalleryPhotos(Activity act) {
		ArrayList<String> galleryList = new ArrayList<String>();
		try {
			final String[] columns = { MediaStore.Images.Media.DATA,
					MediaStore.Images.Media._ID };
			final String orderBy = MediaStore.Images.Media._ID;
			Cursor imagecursor = act.managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
					null, null, orderBy);
			if (imagecursor != null && imagecursor.getCount() > 0) {
				while (imagecursor.moveToNext()) {
					String item = new String();
					int dataColumnIndex = imagecursor
							.getColumnIndex(MediaStore.Images.Media.DATA);
					item = imagecursor.getString(dataColumnIndex);
					galleryList.add(item);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Collections.reverse(galleryList);
		return galleryList;
	}

	public static Bitmap convertViewToBitmap(View view) {
		Bitmap bitmap = null;
		try {
			int width = view.getWidth();
			int height = view.getHeight();
			if (width != 0 && height != 0) {
				bitmap = Bitmap.createBitmap(width, height,
						Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmap);
				view.layout(0, 0, width, height);
				view.setBackgroundColor(Color.WHITE);
				view.draw(canvas);
			}
		} catch (Exception e) {
			bitmap = null;
			e.getStackTrace();
		}
		return bitmap;

	}

	public static boolean saveImageToGallery(Context context, Bitmap bmp,
			boolean isPng) {
		if (bmp == null) {
			return false;
		}
		File appDir = new File(Environment.getExternalStorageDirectory(),
				context.getString(R.string.app_name));
		if (!appDir.exists()) {
			if (!appDir.mkdir()) {
				return false;
			}
		}
		String fileName;
		if (isPng) {
			fileName = System.currentTimeMillis() + ".png";
		} else {
			fileName = System.currentTimeMillis() + ".jpg";
		}
		File file = new File(appDir, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			if (isPng) {
				bmp.compress(CompressFormat.PNG, 100, fos);
			} else {
				bmp.compress(CompressFormat.JPEG, 100, fos);
			}
			bmp.recycle();
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(),
					file.getAbsolutePath(), fileName, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				Uri.fromFile(appDir)));
		return true;
	}

	public static void t(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

}
