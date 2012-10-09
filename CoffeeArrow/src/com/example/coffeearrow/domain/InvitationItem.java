package com.example.coffeearrow.domain;

import org.codehaus.jackson.annotate.JsonAnySetter;


/*
 * 07-28 19:20:04.037: I/System.out(1158): 
 * {"results": 
 * [{"profileImage": "http://distilleryimage1.s3.amazonaws.com/d972f8b6a9fe11e18cf91231380fd29b_6.jpg", 
 * "locked": "2012-07-31T00:12:33.000Z", 
 * "name": "Nishant ", 
 * "userId": "2379820477", 
 * "dateId": "3495999573", 
 * "latestInitiatorId": "3495999573", 
 * "_id": "3680348388", 
 * "type": "match"}]}
*/
public class InvitationItem {
	String profileImage; 
	public String getProfileImage() {
		return profileImage;
	}
	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}
	public String getLocked() {
		return locked;
	}
	public void setLocked(String locked) {
		this.locked = locked;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDateId() {
		return dateId;
	}
	public void setDateId(String dateId) {
		this.dateId = dateId;
	}
	public String getLatestInitiatorId() {
		return latestInitiatorId;
	}
	public void setLatestInitiatornId(String latestInitiatorId) {
		this.latestInitiatorId = latestInitiatorId;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	String locked;
	String name;
	String userId;
	String dateId;
	String latestInitiatorId;
	String _id;
	String type;
	
	@JsonAnySetter
	public void handleUnknown(String key, Object value) {
	    // do something: put to a Map; log a warning, whatever
    }
	
}
