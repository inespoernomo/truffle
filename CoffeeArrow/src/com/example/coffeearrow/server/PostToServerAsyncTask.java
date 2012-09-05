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

import android.os.AsyncTask;
import android.util.Log;

public class PostToServerAsyncTask extends AsyncTask<HttpPost, Integer, Object>{
	
	private static PostToServerAsyncTask instance = null;
	private static HttpClient client = null;
	protected PostToServerAsyncTask() {
		client = new DefaultHttpClient();
	}
	
	public static PostToServerAsyncTask getInstance() {
		if(instance == null) {
			instance = new PostToServerAsyncTask();
		}
		return instance;
	}

	@Override
	protected JSONArray doInBackground(HttpPost... params) {
		HttpPost request = params[0];
		HttpResponse response = null;
        JSONArray finalResult = null;
		try {
			Log.i("SeverInterface", "request url is:"+request.getURI());
			response = client.execute(request);
			Log.i("ServerInterface", "response code:"+response.getStatusLine());
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			String json = reader.readLine();
			System.out.println(json);
			Log.i("ServerInterface", "json is:"+json);
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
