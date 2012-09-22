package com.example.coffeearrow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;

public class NewDateActivity extends ChangeDateActivity {
    private static final int PAYMENT_REQUEST_CODE = 1234;
    
    private String dateId;
    
    @Override
    public void submit(View view) {
        Log.i("NewDateActivity", "Submit clicked.");
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Intent sourceIntent = getIntent();
        dateId = sourceIntent.getStringExtra("dateId");

        builder.setMessage("To send date invitation, we need you to authorize us using Amazon Payment. Nothing will be charged until the other person say yes. Go to the authorize page now?")
               .setCancelable(false)
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       
                        // Go to the payment activity
                        Intent intent = new Intent(mainActivity, AuthorizeAmazonPaymentActivity.class);
                        intent.putExtra("dateId", dateId);
                        startActivityForResult(intent, PAYMENT_REQUEST_CODE);
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        //
                   }
               });
        AlertDialog alert = builder.create();
        alert.show();
        
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) { 
        super.onActivityResult(requestCode, resultCode, returnedIntent);
        
        // Get back from the payment activity.
        Log.i("NewDateActivity", "Got back from payment with requestCode: " + requestCode + " and resultCode: " + resultCode);
        
        switch(requestCode) {
        case PAYMENT_REQUEST_CODE:
            if(resultCode == RESULT_OK){
                Log.i("NewDateActivity", "Result code OK: " + resultCode);
                
                matchId = returnedIntent.getStringExtra("matchId");
                Log.i("NewDateActivity", "The matchId returned is: " + matchId);
                
                super.submit(null);
            } 
            else if (resultCode == 0)
            {
                // User cancelled.
                //TODO: Collect info for user behavior?
                Log.i("NewDateActivity", "Result code cancelled: " + resultCode);
            }
            else 
            {
                Log.i("NewDateActivity", "Result code not OK: " + resultCode);
            }
            
            break;
        default:
            break;
        }
        
    }
}
