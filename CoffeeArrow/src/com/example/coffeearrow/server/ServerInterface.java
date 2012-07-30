/**
 * 
 */
package com.example.coffeearrow.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author Nishant
 *
 */
public class ServerInterface {

	public static JSONArray executeHttpRequest(HttpPost request) {
		HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = null;
        JSONArray finalResult = null;
		try {
			response = httpClient.execute(request);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			String json = reader.readLine();
			System.out.println(json);
			JSONObject obj = new JSONObject(json);
			finalResult = obj.getJSONArray("results");
		} catch (ClientProtocolException e) {		
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return finalResult;
	}
	
}
