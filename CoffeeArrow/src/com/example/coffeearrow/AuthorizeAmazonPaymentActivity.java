package com.example.coffeearrow;

import java.net.MalformedURLException;
import java.net.URL;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        setContentView(R.layout.activity_authorize_amazon_payment);
        
		SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
		String userId = settings.getString("userId", null);
		
		Intent sourceIntent = getIntent();
		String dateId = sourceIntent.getStringExtra("dateId");
        
        WebView view = (WebView)findViewById(R.id.webView1);
        view.setWebViewClient(new WebViewClient(){
        	@Override
        	public boolean shouldOverrideUrlLoading(WebView view, String urlStr) {
        		Log.i("Payment", "url is: "+urlStr);
        		try {
					URL url = new URL(urlStr);
					String host = url.getHost();
					Log.i("Payment", "host is: "+url);
					
					if(host.equals("truffle.io")) {
						view.setVisibility(View.GONE);
						Toast toast = Toast.makeText(mainActivity, "Payment activity taking over. Will post to server with url: "+ urlStr, Toast.LENGTH_LONG);
						toast.show();
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
        view.loadUrl("http://coffeearrow.com/callPayments?userid="+userId+"&dateId="+dateId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_authorize_amazon_payment, menu);
        return true;
    }
}
