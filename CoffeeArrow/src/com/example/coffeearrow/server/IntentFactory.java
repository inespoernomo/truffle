/**
 * 
 */
package com.example.coffeearrow.server;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;

/**
 * @author Nishant
 *
 */
public class IntentFactory {
	
	public static Intent create(Context context, Class<?> targetClass, HashMap<String, String> requestParams ) {
		Intent intent = new Intent(context, targetClass);
		for (String key : requestParams.keySet()){
			intent.putExtra(key, requestParams.get(key));
		}
		return intent;
	}

}
