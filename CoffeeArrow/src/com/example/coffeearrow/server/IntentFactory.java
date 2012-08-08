/**
 * 
 */
package com.example.coffeearrow.server;


import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

/**
 * @author Nishant
 *
 */
public class IntentFactory {
	
	public static Intent create(Context context, Class<?> targetClass, JSONObject jsonRequestParams ) {
		Intent intent = new Intent(context, targetClass);
		/*try {
			//intent.putExtra("email", jsonRequestParams.getString("email"));
			//intent.putExtra("password", jsonRequestParams.getString("password"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		return intent;
	}

}
