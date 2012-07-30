/**
 * 
 */
package com.example.coffeearrow.helpers;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import com.example.coffeearrow.domain.Profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @author Nishant
 * 
 */
public class ConvertImagetoBitmap {

	public static ArrayList<Profile> getImageBitmap(
			ArrayList<Profile> profileList) {

		for (Profile profile : profileList) {
			Bitmap bm = null;
			try {
				URL aURL = new URL(profile.getProfileImage());
				URLConnection conn = aURL.openConnection();
				conn.connect();
				InputStream is = conn.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				bm = BitmapFactory.decodeStream(bis);
				profile.setProfileBitMap(bm);
				bis.close();
				is.close();
			} catch (IOException e) {

			}

		}
		return profileList;
	}

}
