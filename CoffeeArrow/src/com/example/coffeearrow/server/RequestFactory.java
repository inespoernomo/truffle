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

	public static HttpPost create(String url, JSONObject jsonObj, String method) {
		
		HttpPost request = new HttpPost(url + method);
        request.addHeader("content-type", "application/json");
        StringEntity requestParams = null;	
		try {
			requestParams = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        request.setEntity(requestParams);
        return request;
	}
}
