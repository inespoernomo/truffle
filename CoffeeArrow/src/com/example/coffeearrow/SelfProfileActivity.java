package com.example.coffeearrow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class SelfProfileActivity extends ShowUserProfileActivity {

	private static final int ACTIVITY_SELECT_IMAGE = 1234;
	private static final int ACTIVITY_UPLOAD_IMAGE = 1235;
	private static final int ACTIVITY_EDIT_IMAGE = 1236;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_self_profile, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("selfprofile", "Got option item selected and menu item is: " + item.toString());
		switch(item.getItemId()){
		case R.id.uploadPhoto:
    		Intent i = new Intent(Intent.ACTION_PICK,
    	               android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    		startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
		break;
		case R.id.logout:
		    SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
		    SharedPreferences.Editor editor = settings.edit();
	        editor.remove("userId");
	        editor.commit();
	        
	        Intent intent = new Intent(this, SignIn.class);
	        // These flags clear the whole thing, so back button will not come back.
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
	        startActivity(intent);
		    break;
		default:
		    Log.i("SelfProfile", "Unknown menu item.");
		    break;
		}
		return true;
	}
	
	/**
	 * This method handle both select image comes back and upload image comes back.
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) { 
	    super.onActivityResult(requestCode, resultCode, returnedIntent); 

	    Log.i("selfprofile", "Got back from another activity and the requestCode is:"+requestCode);
	    
	    switch(requestCode) { 
	    case ACTIVITY_SELECT_IMAGE:
	    	Log.i("selfprofile", "Call back from select image activity with resultCode:"+resultCode);
	        if(resultCode == RESULT_OK){  
	        	Log.i("selfprofile", "Result code OK.");
	            Uri selectedImage = returnedIntent.getData();
	            Log.i("selfprofile", "Result uri is:"+selectedImage);
	            
	            String[] filePathColumn = {MediaStore.Images.Media.DATA};
	            Log.i("selfprofile", "filePathColumn is:"+filePathColumn);

	            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
	            cursor.moveToFirst();

	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            String filePath = cursor.getString(columnIndex);
	            cursor.close();
	            Log.i("selfprofile", "filePath is:"+filePath);
	            
	            Intent destinationIntent = new Intent(this, UploadImageActivity.class);
	            destinationIntent.putExtra("filePath", filePath);
	            startActivityForResult(destinationIntent, ACTIVITY_UPLOAD_IMAGE);
	        } 
	        else if (resultCode == 0)
	        {
	        	// User cancelled.
	        	//TODO: Collect info for user behavior?
	        	Log.i("selfprofile", "User cancelled image selection.");
	        }
	        else 
	        {
	        	Log.i("selfprofile", "Result code not OK.");
	        }
	        break;
	    case ACTIVITY_UPLOAD_IMAGE:
	    	Log.i("selfprofile", "Call back from upload image activity with resultCode:"+resultCode);
	    	if(resultCode == RESULT_OK){
	    	    // TODO: Currently refresh the whole thing, this is easy for when user updated their profile image. But maybe we can do better.
				//TODO: We can use the local file path. But right now, using s3 url is 
				// easier with the lazy loading and image caching, etc.
	    		//String s3url = returnedIntent.getStringExtra("s3url");
	    		//String caption = returnedIntent.getStringExtra("caption");
	        	//addImageWithCaption(s3url, caption);
	    		startActivity(getIntent());
	            finish();
	        } 
	        else if (resultCode == 0)
	        {
	        	// User cancelled.
	        	//TODO: Collect info for user behavior?
	        	Log.i("selfprofile", "User cancelled image upload.");
	        }
	        else 
	        {
	        	Log.i("selfprofile", "Result code not OK.");
	        }
	        break;
	    case ACTIVITY_EDIT_IMAGE:
	    	Log.i("selfprofile", "Call back from edit image activity with resultCode:"+resultCode);
	    	if(resultCode == RESULT_OK){
				//TODO: We can use the local file path. But right now, using s3 url is 
				// easier with the lazy loading and image caching, etc.
	    		String s3url = returnedIntent.getStringExtra("s3url");
	    		String editType = returnedIntent.getStringExtra("type");
	    		
	    		if(editType.equals("delete")) {
	    			removeImage(s3url);
	    		}
	    		else {
	    			// Update the caption.
	    			String caption = returnedIntent.getStringExtra("caption");
	    			updateCaption(s3url, caption);
	    		}
	        } 
	        else if (resultCode == 0)
	        {
	        	// User cancelled.
	        	//TODO: Collect info for user behavior?
	        	Log.i("selfprofile", "User cancelled image edit.");
	        }
	        else 
	        {
	        	Log.i("selfprofile", "Result code not OK.");
	        }
	        break;
	    }
	}

	/**
	 * TODO: This should update just one picture. But now we 
	 * reload the whole activity.
	 * @param s3url
	 * @param caption
	 */
	private void updateCaption(String s3url, String caption) {
		startActivity(getIntent());
		finish();
	}

	/**
	 * TODO: This should remove just one picture. But now we 
	 * reload the whole activity. 
	 * @param s3url
	 */
	private void removeImage(String s3url) {
		startActivity(getIntent());
		finish();
	}

	/**
	 * Override the add click listener method.
	 * We want to bring the image up and edit it.
	 */
	@Override
	protected void addImageClickListener(ImageView view, String s3url, String caption) {
		view.setOnClickListener(new ImageClickListener(s3url, caption));
	}
	
	/**
	 * This is the class implements OnClickListener
	 * For each image, it records the s3url and caption of the image
	 * to pass to the edit view.
	 * @author sunshi
	 *
	 */
	private class ImageClickListener implements OnClickListener {
		
		private String s3url;
		private String caption;
		
		public ImageClickListener(String s3url, String caption) {
			this.s3url = s3url;
			this.caption = caption;
		}
		
		public void onClick(View v) {
			//TODO: Go to edit view instead of log this.
            Log.i("SelfProfileActivity", "Image clicked. Image s3url is: "+s3url+". Caption is: "+caption);
            mainActivity.editImage(s3url, caption);
        }
	}
	
	@Override
	protected void editImage(String s3url, String caption) {
		Intent destinationIntent = new Intent(this, EditImageActivity.class);
        destinationIntent.putExtra("s3url", s3url);
        destinationIntent.putExtra("caption", caption);
        startActivityForResult(destinationIntent, ACTIVITY_EDIT_IMAGE);
	}
}
