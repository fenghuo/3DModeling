package com.example.mengqianliuhw2;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends Activity {
    private Camera mCamera;
    private SurfaceView mPreview;
   
    private MediaRecorder mMediaRecorder;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static int IMAGE_SELECTION = 2;
    public static int N = 0;
	protected static final String TAG = null;
	Bitmap bmp_image;
	private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
		   switch (status) {
		       case LoaderCallbackInterface.SUCCESS:
		       {
		      Log.i(TAG, "OpenCV loaded successfully");
		      // Create and set View
		     // setContentView(R.layout.main);
		       } break;
		       default:
		       {
		      super.onManagerConnected(status);
		       } break;
		   }
		    }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        Log.i(TAG, "Trying to load OpenCV library");
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_2, this, mOpenCVCallBack))
        {
          Log.e(TAG, "Cannot connect to OpenCV Manager");
        }
        setContentView(R.layout.activity_main);
        //checkCameraHardware;
        // Create an instance of Camera
        mCamera = getCameraInstance();
        setCameraDisplayOrientation(this, 0, mCamera) ;
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
     // Add a listener to the Capture button
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get an image from the camera
                    mCamera.takePicture(null, null, mPicture);
                    
                    try {
						Thread.currentThread().sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    
                	IMAGE_SELECTION = 200;
                    Intent intent = new Intent(MainActivity.this, DrawActivity.class);                
                    startActivity(intent) ;
                                
                    
                }
                
            }
            
        );
        //go to gallery button
        /*Button galleryButton = (Button) findViewById(R.id.go_to_gallery);
        galleryButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                    Intent intent2 = new Intent(MainActivity.this, Gallery.class);                
                    startActivity(intent2) ;
                                
                    
                }
                
            }
            
        );*/
        
        
    }

		
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	private boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
	
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
	
	private PictureCallback mPicture = new PictureCallback() {

	    @Override
		public void onPictureTaken(byte[] data, Camera camera) { 
	    	FileOutputStream outStream = null;
	    	try{
    			bmp_image = BitmapFactory.decodeByteArray(data, 0, data.length);
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
    		    N  = M;   
	    		//outStream = new FileOutputStream(String.format("/"+Environment.getExternalStorageDirectory() + "/" + "111.jpg"));
    			//outStream = new FileOutputStream(String.format("/sdcard/111.jpg")); 
    		    outStream = new FileOutputStream(String.format(fname)); 
    			outStream.write(data);
        		outStream.close();
    		} catch (Exception e){
    		}
    		
    	}
	};
	
	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(type));
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	
	@Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }
	@Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mOpenCVCallBack);
        
        
    }
    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
	
    public static void setCameraDisplayOrientation(Activity activity,
            int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
    
    public void gotogallery(View view) {
		Intent intent = new Intent(this, Gallery.class);
    	//Intent intent = new Intent(this, DetectActivity.class);
    	
    	mCamera.stopPreview();
    	mCamera.setPreviewCallback(null);

    	releaseMediaRecorder();
    	releaseCamera();   
    	
    	startActivity(intent) ;
		finish();
	}
    
    public void detect(View view) {
		Intent intent = new Intent(this, DetectActivity.class);
    	
    	mCamera.stopPreview();
    	mCamera.setPreviewCallback(null);

    	releaseMediaRecorder();
    	releaseCamera();   
    	
    	startActivity(intent) ;
		finish();
	}
    
    public boolean fileExistance(String fname){
        
    	File file = new File(fname);
        return file.exists();
    }
}