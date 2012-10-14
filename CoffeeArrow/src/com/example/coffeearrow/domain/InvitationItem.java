package com.example.coffeearrow.domain;

import java.io.Serializable;

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
public class InvitationItem implements Serializable {
	/**
     * This is auto generated. I have no idea what is it for. 
     */
    private static final long serialVersionUID = -6945657293809464262L;
    
    String profileImage; 
	public String getProfileImage() {
		return profileImage;
	}
	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	
	public String getCurEpoch() {
        return curEpoch;
    }
    public void setCurEpoch(String currEpoch) {
        this.curEpoch = currEpoch;
    }
    
    public String getCurPlace() {
        return curPlace;
    }
    public void setCurPlace(String currPlace) {
        this.curPlace = currPlace;
    }
    
    public String getPreEpoch() {
        return preEpoch;
    }
    public void setPreEpoch(String prevEpoch) {
        this.preEpoch = prevEpoch;
    }
    
    public String getPrePlace() {
        return prePlace;
    }
    public void setPrePlace(String prevPlace) {
        this.prePlace = prevPlace;
    }
    
    public String getMatchId() {
        return matchId;
    }
    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }
    
	String status;
	String name;
	String userId;
	String dateId;
	String latestInitiatorId;
	String _id;
	String type;
    String curEpoch;
    String curPlace;
    String preEpoch;
    String prePlace;
    String matchId;
	
	@JsonAnySetter
	public void handleUnknown(String key, Object value) {
	    // do something: put to a Map; log a warning, whatever
    }
	
}
