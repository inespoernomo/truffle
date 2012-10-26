/**
 * 
 */
package com.example.coffeearrow.domain;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonAnySetter;

/**
 * @author Nishant
 *
 */

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

	String _id;
	
	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	String city;
	
	String firstName;
	
	String lastName;
	
	String profileImage;
	
	ArrayList<Image> images;
	
	@JsonAnySetter
	public void handleUnknown(String key, Object value) {
	    // do something: put to a Map; log a warning, whatever
    }
	
	public String getCity() {
		return city;
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

	String gender;
	public String getGender() {
	    return gender;
	}
	public void setGender(String gender) {
	    this.gender = gender;
	}
	
	String zipcode;
	public String getZipcode() {
	    return zipcode;
	}
	public void setZipcode(String zipcode) {
	    this.zipcode = zipcode;
	}
	
}
