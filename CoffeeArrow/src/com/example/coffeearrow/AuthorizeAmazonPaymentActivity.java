package com.example.coffeearrow;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebView;

public class AuthorizeAmazonPaymentActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorize_amazon_payment);
        
        WebView view = (WebView)findViewById(R.id.webView1);
        view.loadUrl("http://coffeearrow.com/callPayments");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_authorize_amazon_payment, menu);
        return true;
    }
}
