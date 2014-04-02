package com.example.mengqianliuhw2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class DrawActivity extends Activity {
	private Matrix matrix = new Matrix();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_draw);
		ImageView mImageView = (ImageView) findViewById(R.id.imageView1);
		if (MainActivity.IMAGE_SELECTION == 100) {
			// read in the picture that is just taken
			Bitmap bitmap = BitmapFactory.decodeFile(Environment
					.getExternalStorageDirectory().getPath()
					+ "/image"
					+ MainActivity.N + ".jpg");
			matrix.setRotate(90);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);

			mImageView.setImageBitmap(bitmap);
			// bitmap.recycle();

		} else {
			switch (MainActivity.IMAGE_SELECTION) {
			case 0:
				mImageView.setImageResource(R.drawable.building1);
				break;
			case 1:
				mImageView.setImageResource(R.drawable.building2);
				break;
			case 2:
				mImageView.setImageResource(R.drawable.building3);
				break;
			case 3:
				mImageView.setImageResource(R.drawable.testsoap);
				break;
			}
			if (MainActivity.IMAGE_SELECTION == 200) {
				Bitmap bitmap = BitmapFactory.decodeFile(Environment
						.getExternalStorageDirectory().getPath()
						+ "/image"
						+ MainActivity.N + ".jpg");
				matrix.setRotate(90);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
						bitmap.getHeight(), matrix, true);

				mImageView.setImageBitmap(bitmap);
			}
		}
		System.gc();
		// Add a listener to the Capture button
		Button captureButton = (Button) findViewById(R.id.draw_mask);
		captureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(DrawActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();

			}

		}

		);
		// Add a listener to the automatical recognition button
		Button Rec1Button = (Button) findViewById(R.id.auto_rec);
		Rec1Button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(DrawActivity.this,
						RecogActivity.class);
				startActivity(intent);
				finish();

			}

		}

		);
		// Add a listener to the manual recognition button
		Button Rec2Button = (Button) findViewById(R.id.manual_rec);
		Rec2Button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(DrawActivity.this,
						ManualActivity.class);
				startActivity(intent);
				finish();

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
