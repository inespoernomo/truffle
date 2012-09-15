package com.example.coffeearrow.domain;

import org.codehaus.jackson.annotate.JsonAnySetter;


/**
 * {"dates": 
 * [{"type": "dates", 
 * "_id": "3507286709", 
 * "userId": "2379820477", 
 * "matchId": "3680348388", 
 * "time": "2012-07-24T21:26:49Z"}
 * , 
 * "latestInitiatorId": "3495999573"}*/
public class DateItem {
	
	String type;
	String _id;
	String userId;
	String matchId;
	String time;
	String latestInitiatorId;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getMatchId() {
		return matchId;
	}
	public void setMatchId(String matchId) {
		this.matchId = matchId;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}

	@JsonAnySetter
	public void handleUnknown(String key, Object value) {
	    // do something: put to a Map; log a warning, whatever
    }
}
