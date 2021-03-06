package com.example.coffeearrow.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public final class PostToServerAsyncTask extends AsyncTask<HttpPost, Integer, Object>{
	
	private static HttpClient client = null;
	private PostToServerCallback caller = null;
	
	public PostToServerAsyncTask(PostToServerCallback caller) {
		client = new DefaultHttpClient();
		this.caller = caller;
	}

	@Override
	protected JSONObject doInBackground(HttpPost... params) {
		HttpPost request = params[0];
		HttpResponse response = null;
		JSONObject finalResult = null;
		try {
			Log.i("SeverInterface", "request url is:"+request.getURI());
			response = client.execute(request);
			StatusLine status = response.getStatusLine();
			int statusCode = status.getStatusCode();
			
			if (statusCode ==200) {
    			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
    			String json = "";
    			String line;
    			while ((line = reader.readLine()) != null) {
    			    json += line;
    			}
    			System.out.println(json);
    			Log.i("ServerInterface", "json is:"+json);
    			finalResult = new JSONObject(json);
			}
			// Failed
			else {
			    Log.i("ServerInterface", "Request failed with code: " + statusCode);
			}
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
	
	@Override
	protected void onPostExecute(Object objResult) {
		if (caller != null) {
			caller.callback((JSONObject)objResult);
			caller = null;
		}
	}

}
