package com.example.coffeearrow.server;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Nishant
 * Request factory makes the HttpPost Request
 */
public class RequestFactory {
	
    //TODO: Put these in config
	//public static final String URL = "http://coffeearrow.com/";
    //public static final String URL = "https://truffleapp.herokuapp.com/";
    
    // This is equal to 127.0.0.1 but 127.0.0.1 will fail
    // http://stackoverflow.com/questions/2301560/java-net-connectexception-127-0-0-18080-an-android-emulator
    // http://www.linuxtopia.org/online_books/android/devguide/guide/developing/tools/android_emulator_networkaddresses.html
     public static final String URL = "http://10.0.2.2/";
    
	public static HttpPost create(Map<String, String> query, String method) {
		
		HttpPost request = new HttpPost(URL + method);
		request.addHeader("content-type", "application/json");
		StringEntity requestParams = null; 
		JSONObject jsonObj = new JSONObject();

		try {
			for (String key: query.keySet()) {
				jsonObj.put(key, query.get(key));
			}
			requestParams = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		request.setEntity(requestParams);
		return request;
	}
}
