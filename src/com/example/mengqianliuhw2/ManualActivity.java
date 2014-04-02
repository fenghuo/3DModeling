package com.example.mengqianliuhw2;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ManualActivity extends Activity {

	public static float height;
	public static float width;
	//private Bitmap draw;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_manual);
	
	
	final MyView mView = (MyView)findViewById(R.id.view1);
  
    Button captureButton = (Button) findViewById(R.id.move_top);
    captureButton.setOnClickListener(
        new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyView.myPoint_y[MyView.point_chosen] =  MyView.myPoint_y[MyView.point_chosen]-1;
                mView.invalidate();
                
            }
            
        }
        
    );
    Button captureButton2 = (Button) findViewById(R.id.move_down);
    captureButton2.setOnClickListener(
        new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyView.myPoint_y[MyView.point_chosen] =  MyView.myPoint_y[MyView.point_chosen]+1;
                mView.invalidate();
                
            }
            
        }
        
    );

    Button captureButton3 = (Button) findViewById(R.id.move_left);
    captureButton3.setOnClickListener(
        new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyView.myPoint_x[MyView.point_chosen] =  MyView.myPoint_x[MyView.point_chosen]-1;
                mView.invalidate();
                
            }
            
        }
        
    );

    Button captureButton4 = (Button) findViewById(R.id.move_right);
    captureButton4.setOnClickListener(
        new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyView.myPoint_x[MyView.point_chosen] =  MyView.myPoint_x[MyView.point_chosen]+1;
                mView.invalidate();
                
            }
            
        }
        
    );

 // Add a listener to the Capture button
    Button captureButton5 = (Button) findViewById(R.id.ok);
    captureButton5.setOnClickListener(
        new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent intent = new Intent(ManualActivity.this, ModelActivity.class); 
            	Intent intent = new Intent(ManualActivity.this, OpenGLES20Complete.class); 
                startActivity(intent) ;
                finish();            
                
            }
            
        }
        
    );

}


}

class MyView extends View{
	private Paint mPaint;
	
	private Bitmap mBitmap;
	
	private Bitmap srcBitmap;
	public static int point_chosen = 0;
	public static int [] myPoint_x = new int [6];
	public static int [] myPoint_y = new int [6];	
	float touch_X = 100;
	float touch_Y = 100;
	private Matrix matrix = new Matrix();
	public MyView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		myPoint_x[0] = 50;
		myPoint_x[1] = 50;
		myPoint_x[2] = 350;
		myPoint_x[3] = 350;
		myPoint_x[4] = 650;
		myPoint_x[5] = 650;
		myPoint_y[0] = 100;
		myPoint_y[1] = 400;
		myPoint_y[2] = 100;
		myPoint_y[3] = 400;
		myPoint_y[4] = 100;
		myPoint_y[5] = 400;
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setARGB(0, 0xff, 0, 0);
		
		if (MainActivity.IMAGE_SELECTION==100){
			//read in the picture that is just taken
			mBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() +
	        "/image" + MainActivity.N + ".jpg");		  
			matrix.setRotate(90);  
			mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),mBitmap.getHeight(), matrix, true);  
			
			ManualActivity.height = mBitmap.getHeight();
			ManualActivity.width = mBitmap.getWidth();
			//bitmap.recycle();
			
			
		}
		else{
			switch(MainActivity.IMAGE_SELECTION){
		
    	case 0:
    		mBitmap =  BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() +
    		        "/building1.jpg");
    	    break;   	        	
    	case 1:
    		mBitmap =  BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() +
    		        "/building2.jpg");
    		break;
    	case 2:
    		mBitmap =  BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() +
    		        "/building3.jpg");
    		break;
    	case 3:
    		mBitmap =  BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() +
    		        "/testsoap.jpg");
    		break;
		}
			ManualActivity.height = mBitmap.getHeight();
			ManualActivity.width = mBitmap.getWidth();
		}
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if(mBitmap != null)
			canvas.drawBitmap(mBitmap, 0, 0, null);
		
		Paint p = new Paint(); //´´½¨»­±Ê
        p.setStrokeWidth(2);
        p.setColor(Color.WHITE);
        for (int i=0;i<6;i++){	                    	
       	 canvas.drawLine(myPoint_x[i]-10, myPoint_y[i], myPoint_x[i]+10, myPoint_y[i], p);
       	 canvas.drawLine(myPoint_x[i], myPoint_y[i]-10, myPoint_x[i], myPoint_y[i]+10, p);
       	
        }
		
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int N = event.getHistorySize();
		//float x = 0;
		//float y = 0;
		float press = event.getPressure();
		
		touch_X = event.getX();
		touch_Y = event.getY();
		float delta_min = 1000;
		for (int i=0;i<6;i++){
			float delta_x = Math.abs(event.getX()-myPoint_x[i]);
			float delta_y = Math.abs(event.getY()-myPoint_y[i]);
			if (((delta_x+delta_y)<delta_min)&&(event.getY()<900)){
				delta_min = delta_x + delta_y; 
				point_chosen = i;
			}
		}
		
		if ((delta_min>80) && (event.getY()<900)){
			myPoint_x[point_chosen] = (int) event.getX();
			myPoint_y[point_chosen] = (int) event.getY();
		}
		System.out.println(">>>VIEW: N = " + N);
		System.out.println(">>>VIEW: X = " + event.getX() + " Y = " + event.getY());
		System.out.println(">>>VIEW: Press = " + event.getPressure() + " Size = " + event.getSize());
		System.out.println(">>>VIEW: XPre = " + event.getXPrecision() + " YPre = " + event.getYPrecision());
		//System.out.println(">>>VIEW: X * XPre = " + x 
		//		+ " Y * YPre = " + y);
		
		for(int i = 0; i < N; i++){
			System.out.println(">>>VIEW: i = " + i 
					+ " Xhis = " + event.getHistoricalX(i)
					+ " Yhis = " + event.getHistoricalY(i));
			System.out.println(">>>VIEW: Pressure: " + event.getHistoricalPressure(i)
					+ " size: " + event.getHistoricalSize(i));
		}
		
		
		this.invalidate();
		return false;
	}
	
	
}