/**
 * 
 */
package com.example.coffeearrow.domain;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import android.graphics.Bitmap;

/**
 * @author Nishant
 *
 */

@JsonIgnoreProperties
public class UserProfile {
	
	public static class Image {
		
		public Image() {
				
		}
		
		public Image(String imgLink, String imgCaption) {
			super();
			this.imgLink = imgLink;
			this.imgCaption = imgCaption;
		}
		
		String imgLink;
		
		String imgCaption;
		
		Bitmap bitMapImgLink;

		public Bitmap getBitMapImgLink() {
			return bitMapImgLink;
		}

		public void setBitMapImgLink(Bitmap bitMapImgLink) {
			this.bitMapImgLink = bitMapImgLink;
		}

		public String getImgLink() {
			return imgLink;
		}

		public void setImgLink(String imgLink) {
			this.imgLink = imgLink;
		}

		public String getImgCaption() {
			return imgCaption;
		}

		public void setImgCaption(String imgCaption) {
			this.imgCaption = imgCaption;
		}
		
		public String toString() {
			return "{imgLink:"+imgLink+", imgCaption:"+imgCaption+"}";
		}
	}
	
	@Override
	public String toString() {
		return "UserProfile [city=" + city + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", profileImage=" + profileImage
				+ ", images=" + images + "]";
	}

	String city;
	
	String firstName;
	
	String lastName;
	
	String profileImage;
	
	ArrayList<Image> images;
	
	@JsonIgnore
	Bitmap profileImageBitMap;
	
	@JsonIgnore
	ArrayList<Image> imagesBitMapList;
	
	@JsonAnySetter
	public void handleUnknown(String key, Object value) {
	    // do something: put to a Map; log a warning, whatever
    }
	
	public String getCity() {
		return city;
	}

	public Bitmap getProfileImageBitMap() {
		return profileImageBitMap;
	}

	public void setProfileImageBitMap(Bitmap profileImageBitMap) {
		this.profileImageBitMap = profileImageBitMap;
	}

	public ArrayList<Image> getImagesBitMapList() {
		return imagesBitMapList;
	}

	public void setImagesBitMapList(ArrayList<Image> imagesBitMapList) {
		this.imagesBitMapList = imagesBitMapList;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public ArrayList<Image> getImages() {
		return images;
	}

	public void setImages(ArrayList<Image> images) {
		this.images = images;
	}

}
