package com.example.coffeearrow;

import com.example.coffeearrow.helpers.ImageLoader;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * This is the activity to edit the image caption or delete the image.
 * TODO: This and UploadImageActivity are almost identical, need to refactor.
 * @author sunshi
 *
 */
public class EditImageActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        
        // Get the info from the source intent.
        Intent sourceIntent = getIntent();
        String s3url = sourceIntent.getStringExtra("s3url");
        String caption = sourceIntent.getStringExtra("caption");
        Log.i("EditImageActivity", "Got s3url: "+s3url+" . Caption: "+caption);
        
        // Setup the image
        ImageLoader imageLoader = new ImageLoader(this);
        ImageView imageToEdit = (ImageView)findViewById(R.id.imageToEdit);
        imageLoader.DisplayImage(s3url, imageToEdit);
        
        // Setup the caption
        EditText captionEdit = (EditText)findViewById(R.id.caption);
        captionEdit.setText(caption);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit_image, menu);
        return true;
    }
}
