/**
 * 
 */
package com.example.coffeearrow.domain;

import org.codehaus.jackson.annotate.JsonIgnore;

import android.graphics.Bitmap;

/**
 * @author Nishant
 *
 */
public class Profile {
	
	@Override
	public String toString() {
		return firstName + " " + lastName + "\n"+ city;
	}

	String firstName;
	
	String lastName;
	
	String city;
	
	String birthdate;
	
	String profileImage;
	
	@JsonIgnore
	Bitmap profileBitMap;
	
	String _id;
	
	public Bitmap getProfileBitMap() {
		return profileBitMap;
	}

	public void setProfileBitMap(Bitmap profileBitMap) {
		this.profileBitMap = profileBitMap;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

}
