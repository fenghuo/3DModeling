package com.example.mengqianliuhw2;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ModelActivity extends Activity {

	private Matrix matrix = new Matrix();
	private Bitmap draw;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_model);
	
	ImageView mImageView = (ImageView)findViewById(R.id.imageView5);
	/*
	
	if (MainActivity.IMAGE_SELECTION==100){
		//read in the picture that is just taken
		Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() +
        "/image" + MainActivity.N + ".jpg");		  
		matrix.setRotate(90);  
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),bitmap.getHeight(), matrix, true);  
		//linedetection(bitmap);
		
		mImageView.setImageBitmap(draw);
		//bitmap.recycle();
		
		
	}
	else{
		Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() +
		        "/building1.jpg");		
		switch(MainActivity.IMAGE_SELECTION){
    	case 0:
    		bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() +
    				"/building1.jpg");	
    	    break;   	        	
    	case 1:
    		bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() +
    				"/building2.jpg");	
    		break;
    	case 2:
    		bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() +
    				"/building3.jpg");	
    		break;
    	case 3:
    		bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() +
    				"/testsoap.jpg");	
    		break;
		}
		
		//linedetection(bitmap);		
		mImageView.setImageBitmap(draw);
		

	}*/
	System.gc();
	
	// Add a listener to the Capture button
    Button captureButton = (Button) findViewById(R.id.draw_mask);
    captureButton.setOnClickListener(
        new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ModelActivity.this, MainActivity.class);                
                startActivity(intent) ;
                            
                
            }
            
        }
        
    );

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.draw, menu);
		return true;
	}

}
