/**
 * 
 */
package com.example.coffeearrow.helpers;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @author Nishant
 * 
 */
public class ConvertImagetoBitmap {

	public static Bitmap getImageBitmap(String url) {

		Bitmap bm = null;
		try {
			URL aURL = new URL(url);
			URLConnection conn = aURL.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			bm = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
		} catch (IOException e) {

		}
		return bm;
	}
}
