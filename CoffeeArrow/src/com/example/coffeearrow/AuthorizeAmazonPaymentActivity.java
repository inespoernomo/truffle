package com.example.coffeearrow;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.coffeearrow.server.PostToServerAsyncTask;
import com.example.coffeearrow.server.PostToServerCallback;
import com.example.coffeearrow.server.RequestFactory;

public class AuthorizeAmazonPaymentActivity extends PortraitActivity {
	
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
						
						urlStr = urlStr.replace("http://truffle.io/", RequestFactory.URL);
                        Log.i("Payment", "url after replace host is: " + urlStr);
                        HttpPost request = new HttpPost(urlStr);
						
						PostToServerCallback callback = new PostToServerCallback() {
							public void callback(JSONObject objResult) {
								String status = "failed";
								String matchId = "";
								if (objResult != null) {
									// Parse the JSON
									try {
										status = objResult.getString("status");
										matchId = objResult.getString("matchId");
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
								Log.i("Payment", "Server got token?: " + status);
								Log.i("Payment", "Server created matchId: " + matchId);
								
								if(status.equals("OK")) {
									Intent intent = new Intent();
									intent.putExtra("matchId", matchId);
									setResult(Activity.RESULT_OK, intent);
									mainActivity.finish();
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
        
        String paymentUrl = RequestFactory.URL+"callPayments?userId="+userId+"&dateId="+dateId;
        Log.i("Payment", "Payment url is: "+paymentUrl);
        view.loadUrl(paymentUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_authorize_amazon_payment, menu);
        return true;
    }
}
