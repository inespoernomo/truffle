package com.example.coffeearrow;

import com.example.coffeearrow.helpers.ImageLoader;
import com.example.coffeearrow.helpers.SquareFrameLayout;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SelfProfileActivity extends ShowUserProfileActivity {

	private static final int ACTIVITY_SELECT_IMAGE = 1234;
	private static final int ACTIVITY_UPLOAD_IMAGE = 1235;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_self_profile, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("selfprofile", "got option item selected and menu item is:");
		Log.i("selfprofile", item.toString());
		Intent i = new Intent(Intent.ACTION_PICK,
	               android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
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
				//TODO: We can use the local file path. But right now, using s3 url is 
				// easier with the lazy loading and image caching, etc.
	    		String s3url = returnedIntent.getStringExtra("s3url");
	    		String caption = returnedIntent.getStringExtra("caption");
	        	addLocalImage(s3url, caption);
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
	    }
	}

	/**
	 * Add one image to the end of all the images we ar displaying
	 * @param s3url
	 * @param caption
	 */
	private void addLocalImage(String s3url, String caption) {
		//TODO: Duplicated code, need refactor and use this for all images.	
		ImageLoader imageLoader=new ImageLoader(this);
		
		// Here we get all the pictures of this user with caption.
		LinearLayout layout = (LinearLayout) findViewById(R.id.container);
		
		// Get the size of the display.
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int displayWidth = size.x;
		
		// A vertical linear layout with one picture (square) and caption for the picture.
		// Width set to the width of the display
		LinearLayout onePicWithCaption = new LinearLayout(this);
		onePicWithCaption.setOrientation(LinearLayout.VERTICAL);
		onePicWithCaption.setLayoutParams(
				new LinearLayout.LayoutParams(displayWidth, LinearLayout.LayoutParams.MATCH_PARENT));
		layout.addView(onePicWithCaption);
		
		// This the frame that make sure the picture is in a square frame.
		SquareFrameLayout picFrame = new SquareFrameLayout(this, null);
		picFrame.setLayoutParams(
				new ViewGroup.LayoutParams(displayWidth, ViewGroup.LayoutParams.MATCH_PARENT));
		onePicWithCaption.addView(picFrame);
		
		// This is the image itself.
		ImageView imageView1 = new ImageView(this);
		imageView1.setScaleType(ImageView.ScaleType.FIT_CENTER);				
		picFrame.addView(imageView1);
		
		// Lazy load and cache the image.
		imageLoader.DisplayImage(s3url, imageView1);
	
		// This is the caption for the image.
		TextView textView1 = new TextView(this);
		textView1.setText(caption);
		onePicWithCaption.addView(textView1);				
		
		// Duplicated code end.
	}
	
}
