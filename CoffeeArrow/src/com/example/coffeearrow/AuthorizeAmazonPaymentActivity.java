package com.example.coffeearrow;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.client.methods.HttpPost;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.coffeearrow.domain.UserProfile;
import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.PostToServerCallback;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class AuthorizeAmazonPaymentActivity extends Activity {
	
	private AuthorizeAmazonPaymentActivity mainActivity;
	
	public String userId;
	public String dateId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        setContentView(R.layout.activity_authorize_amazon_payment);
        
		SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
		userId = settings.getString("userId", null);
		
		Intent sourceIntent = getIntent();
		dateId = sourceIntent.getStringExtra("dateId");
        
        WebView view = (WebView)findViewById(R.id.webView1);
        view.setWebViewClient(new WebViewClient(){
        	@Override
        	public boolean shouldOverrideUrlLoading(WebView view, String urlStr) {
        		Log.i("Payment", "url is: "+urlStr);
        		try {
					URL url = new URL(urlStr);
					String host = url.getHost();
					Log.i("Payment", "host is: "+host);
					
					if(host.equals("truffle.io")) {
						view.setVisibility(View.GONE);
						HttpPost request = new HttpPost(urlStr);
						
						PostToServerCallback callback = new PostToServerCallback() {
							public void callback(Object objResult) {
								String status = "failed";
								if (objResult != null) {
									// Parse the JSON
									JSONArray resultArray = (JSONArray) objResult;
									try {
										for (int i = 0; i < resultArray.length(); i++) {
											JSONObject record = resultArray.getJSONObject(i);

											status = record.getString("status");
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
								Log.i("Payment", "Server got token?: "+status);
								
								if(status.equals("OK")) {
									Intent intent = new Intent(mainActivity, AskThemOut.class);
									intent.putExtra("userId", mainActivity.userId);
									intent.putExtra("dateId", mainActivity.dateId);
									mainActivity.startActivity(intent);
								}
							}
						};
						PostToServerAsyncTask task = new PostToServerAsyncTask(callback);
						task.execute(request);
						return true;
					}
					
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
                view.loadUrl(urlStr);
                return false;
            }
        });
        
        String paymentUrl = "http://coffeearrow.com/callPayments?userId="+userId+"&dateId="+dateId;
        Log.i("Payment", "Payment url is: "+paymentUrl);
        view.loadUrl(paymentUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_authorize_amazon_payment, menu);
        return true;
    }
}
