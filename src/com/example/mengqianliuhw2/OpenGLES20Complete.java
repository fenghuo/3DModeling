package com.example.mengqianliuhw2;
/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class OpenGLES20Complete extends Activity {

    private static final String TAG = null;
	private GLSurfaceView mGLView;
    public static float speed;
    public static float rotatex;
    public static float rotatey;
    public static float rotatez;
    public static int face;
  ///TODO
    private static float mPreviousX;
    private static float mPreviousY;
    private static float mDensity;
    private static float oldDist, newDist;
    private int mode;
    ///

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        speed = 0.0f;
        rotatex=(float) Math.random();
		rotatey=(float) Math.random();
		rotatez=(float) Math.random();
		//face=(int)(Math.random()*6);
		face = 1;
        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity
		//TODO
				final DisplayMetrics displayMetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
				mDensity = displayMetrics.density;
				//
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
        Button b = new Button(this);
        b.setText("Rolling");
        this.addContentView(b,
                   new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        b.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(speed == 3.0f){
					speed =0f;
				}
				else
					speed = 3.0f;
				
				//randomly decide the rotation axis
				//rotatex=(float) Math.random();
				//rotatey=(float) Math.random();
				//rotatez=(float) Math.random();
				rotatex=0;
				rotatey=1;
				rotatez=0;
				//randomly decide the final face
				//face=(int)(Math.random()*6);
				
			}
        	
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
    }
    //TODO
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
    	//int mode = 0;
        if (event != null)
        {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                            
                mode = 0;
                Log.d(TAG, "MODE=" + mode);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event); 
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 10f) {
                    mode = 1;
                    Log.d(TAG, "MODE=" + mode);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            	
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == 0) {
                	float deltaX = (x - mPreviousX) / mDensity / 2f;
                    float deltaY = (y - mPreviousY) / mDensity / 2f;
     
                    MyGLRenderer.mDeltaX += deltaX;
                    MyGLRenderer.mDeltaY += deltaY;
                } else if (mode == 1) {
                    newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 10f) {
                       
                    	MyGLRenderer.scale = oldDist/newDist;
                    }
                }
                break;
            }

            /*if (event.getAction() == MotionEvent.ACTION_MOVE)
            {
                if(mode==0){
                	float deltaX = (x - mPreviousX) / mDensity / 2f;
                    float deltaY = (y - mPreviousY) / mDensity / 2f;
     
                    MyGLRenderer.mDeltaX += deltaX;
                    MyGLRenderer.mDeltaY += deltaY;
                }else{
                	newDist = spacing(event);
                	MyGLRenderer.scale = oldDist/newDist; 
                }
                    
                
            }
            if (event.getAction() == MotionEvent.ACTION_POINTER_DOWN){
            	oldDist = spacing(event);
            	mode = 1;
                if (oldDist > 1.0f) {
                    //                      savedMatrix.set(matrix);
                    
                    mode = 1;
                    
                }else{
                	mode = 0;
                }
                
            }
            if (event.getAction() == MotionEvent.ACTION_POINTER_UP){
            	newDist = spacing(event);
            	//MyGLRenderer.scale += 0.1f; 
                //if (oldDist > 1.0f) {
                    if(newDist>oldDist&&MyGLRenderer.scale>0.5){
                    	MyGLRenderer.scale -= 0.2; 
                    }
                    if(newDist<oldDist){
                    	MyGLRenderer.scale -= 0.2; 
                    }
                    
                //}
                
            }*/
            mPreviousX = x;
            mPreviousY = y;
     
            return true;
        }
        else
        {
            return super.onTouchEvent(event);
        }
        
    }
    
    private float spacing(MotionEvent event) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return FloatMath.sqrt(x * x + y * y);
    }
    //////
}


class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer(context);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        
    }
}
