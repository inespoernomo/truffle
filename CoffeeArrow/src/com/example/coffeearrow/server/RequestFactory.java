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
	
	private static final String URL = "http://coffeearrow.com/";
	
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
