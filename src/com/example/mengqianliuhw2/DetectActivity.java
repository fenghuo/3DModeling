package com.example.mengqianliuhw2;

import java.io.File;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;



public class DetectActivity extends Activity implements CvCameraViewListener2,OnTouchListener {

	private CameraBridgeViewBase mOpenCvCameraView;
	private SeekBar seekbar;
	private TextView text;
	private SeekBar seekbar2;
	private TextView text2;
	private TextView text3;
	private TextView text4;
	public static int pace=1;
	public static int pace2=44;
	public static int X=0;
	public static int Y=0;
	public static int W=0;
	public static int H=0;
	public Mat ori;
	private boolean pict=false;
	
	private boolean avai=true;
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

		@Override
		public void onManagerConnected(int status) {
			// TODO Auto-generated method stub
			if (status == LoaderCallbackInterface.SUCCESS) {
				mOpenCvCameraView.enableView();
			}
			// Log.i("opencv", "load success");
			else
				super.onManagerConnected(status);

		}

	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN); 
		setContentView(R.layout.activity_detect);
		
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial_main_view);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCameraIndex(0);
		mOpenCvCameraView.setCvCameraViewListener(this);

		text = (TextView) findViewById(R.id.sliderValue);
		
		seekbar = (SeekBar) findViewById(R.id.seekBar);
		seekbar.setVisibility(SeekBar.VISIBLE);
		
		text2 = (TextView) findViewById(R.id.sliderValue2);
		text3 = (TextView) findViewById(R.id.sliderValue3);
		text4 = (TextView) findViewById(R.id.sliderValue4);
		
		seekbar2 = (SeekBar) findViewById(R.id.seekBar2);
		seekbar2.setVisibility(SeekBar.VISIBLE);
		
		W=mOpenCvCameraView.getWidth();
		H=mOpenCvCameraView.getWidth();
		
		mOpenCvCameraView.setOnTouchListener(new OnTouchListener(){
				// TODO Auto-generated method stub
				
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
				    	  X=(int) event.getX();
				    	  Y=(int) event.getY();
				    	  text3.setText("X: "+X);
				    	  text4.setText("Y: "+Y);
						// touch on the screen event
				      }

					return false;
				}
		});


		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				pace=progress;
				text.setText(Integer.toString(progress));
				text.setVisibility(TextView.VISIBLE);
			}
		});

		seekbar2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				pace2=progress;
				text2.setText(Integer.toString(progress));
				text2.setVisibility(TextView.VISIBLE);
			}
		});
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detect, menu);
		return true;
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		// TODO Auto-generated method stub
		if(!avai)
			return null;
		if(pict)
			return null;
		avai=false;
		Mat mat=inputFrame.rgba(); 
		this.ori=mat.clone();
		/*
        Bitmap temp;
        
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Config.ARGB_8888);  
        Utils.matToBitmap(mat, bitmap);  
        
        temp=Utility.fillPic3(bitmap);
        bitmap.recycle();
        bitmap=temp;
        
        Utils.bitmapToMat(bitmap,mat);  
        bitmap.recycle();
        */
		
		Utility.fillPic3(mat, (int)(W*1.0/mat.width()*X), (int)(H*1.0/mat.height()*Y), pace2, pace);
		avai=true;
		return mat;
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
	    	  X=(int) event.getX();
	    	  Y=(int) event.getY();
	    	  text3.setText("X: "+X);
	    	  text4.setText("Y: "+Y);
			// touch on the screen event
	      }
		return false;
	}

	public void picture()
	{
		pict=true;
		String fname = null;
        boolean flag = true;
        int M=0;       
        while(flag){
        	fname = Environment.getExternalStorageDirectory().getPath() +
                    "/image" + M + ".jpg";
        	if(fileExistance(fname)==false){
        		flag=false;
        	}
        	else{
        		M++;
        	}
        }

    	MainActivity.N=M;
    	
    	Mat save=this.ori.clone();
    	
    	Utility.fillPic7(this.ori, 0,0, pace2, 1);
    	
    	RecogActivity.linedetection(this.ori);
    	RecogActivity.run=false;

    	Imgproc.cvtColor(save, save, Imgproc.COLOR_RGBA2BGRA);
    	Highgui.imwrite(Environment.getExternalStorageDirectory().getPath() +"/image" + MainActivity.N + ".jpg",save);
     
	}
	
    public void detect(View view) {
    	Intent intent = new Intent(this, RecogActivity.class);       
    	
    	picture();

    	MainActivity.IMAGE_SELECTION = 200;
    	
    	
    	
    	startActivity(intent) ;
		finish();
	}
	public boolean fileExistance(String fname){
        
    	File file = new File(fname);
        return file.exists();
    }
}
