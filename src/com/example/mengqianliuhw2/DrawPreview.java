package com.example.mengqianliuhw2;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Scalar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import math.geom2d.*;
import math.geom2d.line.Line2D;
import math.geom2d.line.StraightLine2D;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.line.StraightLine3D;
import math.geom3d.plane.Plane3D;

public class DrawPreview extends View {
	 private static final String IMAGE_FN = "test1.jpg"; 
	 private Bitmap background_image; 
	 private Bitmap bmp_gray;
	 private Bitmap test_bmp;
	Paint paint = new Paint();


    public DrawPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        if(MainActivity.IMAGE_SELECTION ==100){
        	updateImage(Environment.getExternalStorageDirectory().getPath() +
                    "/image" + MainActivity.N + ".jpg");
        }
        else{
        	updateImage(Environment.getExternalStorageDirectory() + "/" + IMAGE_FN);
        }
        
        
        
    }
    
    public void updateImage(String image_fn) { 
    	 // Set internal configuration to RGB_565 
    	 BitmapFactory.Options bitmap_options = new BitmapFactory.Options(); 
    	 bitmap_options.inPreferredConfig = Bitmap.Config.RGB_565; 
    	 
    	 background_image = BitmapFactory.decodeFile(image_fn, bitmap_options);
    	 
    	 Mat test = Highgui.imread(Environment.getExternalStorageDirectory() + "/" + IMAGE_FN);
    	 //Utils.matToBitmap(test, bmp_gray);  
    	 //Mat mat_bmp = new Mat(background_image.getHeight() , background_image.getWidth(), CvType.CV_8UC4);
    	//Mat mat_bmp = new Mat(128 , 128, CvType.CV_8UC4);
    	 //Utils.bitmapToMat(background_image, imgMAT);
    	// Mat img = Utils.loadResource(context, refrenceimgID, Highgui.CV_LOAD_IMAGE_COLOR);
    	 
    	 //File root = Environment.getExternalStorageDirectory();
    	 //File file = new File(root, "testsoap.jpg");
    	 //Mat m = Highgui.imread(file.getAbsolutePath());
    	 
    	 /*Exception e1=null;
    	 try
    	 {
    	 Mat test = Highgui.imread(Environment.getExternalStorageDirectory() + "/" + IMAGE_FN);
    	 }
    	 catch(Exception e)
    	 {
    		 e1=e;
    	 }
    	 e1=e1;
    	 int i=1;
    	 i++;*/
    	 //Utils.matToBitmap(test, test_bmp);  
			//将彩色Bitmap对象转成8位4通道的Mat.  
	        Mat mat_bmp = new Mat(background_image.getHeight() , background_image.getWidth(), CvType.CV_8UC4);  
	        Utils.bitmapToMat(background_image,mat_bmp);  
	        //将彩色Mat对象转成单通道的灰度Mat.  
	        Mat mat_gray = new Mat();  
	        Imgproc.cvtColor(mat_bmp, mat_gray, Imgproc.COLOR_BGRA2GRAY, 1);  
	        Imgproc.Canny(mat_gray, mat_gray, 80, 100);
	        
	        //houghline detection
	        Mat lines = new Mat();
	        int threshold = 50;
	        int minLineSize = 80;
	        int lineGap = 10;
	        Imgproc.HoughLinesP(mat_gray, lines, 1, Math.PI/180, threshold, minLineSize, lineGap);
	        Point high = new Point(0,0);
	        Point low = new Point(0,100000);
//	        StraightLine2D[]alines=new StraightLine2D[lines.cols()];
//	        HashMap<Double,Integer> lmap=new HashMap<Double,Integer>();
	        
	        for (int x = 0; x < lines.cols(); x++) 
	        {
	              double[] vec = lines.get(0, x);
	              double x1 = vec[0], 
	                     y1 = vec[1],
	                     x2 = vec[2],
	                     y2 = vec[3];
	              Point start = new Point(x1, y1);
	              Point end = new Point(x2, y2);
	              
	              /*alines[x]=new StraightLine2D(new Point2D(x1,y1),new Point2D(x2,y2));
	             
	              if (start.y>high.y){
	            	  high = start;
	              }
	              if (start.y<low.y){
	            	  low = start;
	              }
	              if (end.y>high.y){
	            	  high = end;
	              }
	              if (end.y<low.y){
	            	  low = end;
	              }*/
	              Core.line(mat_gray, start, end, new Scalar(255,0,0), 3);
	              	
	        }
	        //Point2D midpoint = new Point2D((high.x+low.x)/2,(high.y+low.y)/2);
	        /*
	        class Vector implements Comparable
	        {
	        	public Vector2D v;
	        	public double a;
	        	public int rank;
	        	public double diff;
	        	public Point2D p;
	        	
	        	public Vector (Vector2D vv)
	        	{
	        		v=vv;
	        		diff=a=vv.angle();
	        	}
	        	
				@Override
				public int compareTo(Object another) {
					// TODO Auto-generated method stub
					return (int)(a-((Vector)another).a);
				}
	        }

	        Vector[]vectors=new Vector[lines.cols()+1];
	        
	        for (int x = 0; x < lines.cols(); x++) 
	        {
	        	StraightLine2D v=alines[x].perpendicular(midpoint);
	        	Point2D t=v.intersection(alines[x]);
	        	vectors[x]=new Vector(new Vector2D(t,midpoint));
	        	vectors[x].p=t;
	        }
	        
	        Arrays.sort(vectors);
	        vectors[lines.cols()].a=vectors[lines.cols()].diff=vectors[0].a+2*Math.PI;
	     
	        PriorityQueue<Vector> p=new PriorityQueue<Vector> ();
	        
	        for(int i=0;i<lines.cols();i++)
	        {
	        	vectors[i].a=vectors[i+1].diff-vectors[i].diff;
	        	p.add(vectors[i]);
	        	vectors[i].rank=i;
	        	if(p.size()>6)
	        		p.poll();
	        }
	    
	        Object[]temp=p.toArray();
	        Vector[]tt=new Vector[temp.length];
	        for(int i=0;i<temp.length;i++)
	        	tt[i]=(Vector)temp[i];
	        
	        class com implements Comparator<Vector>
	        {
				@Override
				public int compare(Vector lhs, Vector rhs) {
					// TODO Auto-generated method stub
					return (int)(lhs.rank-rhs.rank);
				}
	        	
	        }
	        Arrays.sort(tt, new com());
	        ArrayList<ArrayList<Vector2D>> vclass=new  ArrayList<ArrayList<Vector2D>>();
	        
	        for(int i=0;i<6;i++)
	        	vclass.add(new ArrayList<Vector2D>());
	        int k=0;
	        
	        for(int i=0;i< vectors.length;i++)
	        {
	        	k=k%6;
	        	vclass.get(i).add(vectors[i].v);
	        	if(vectors[i].rank==((Vector)temp[k]).rank)
	        		k++;
	        }
	        */
	        // vclass
	                //由于最后将mat转成ARGB_8888型的Bitmap，输入必须是4通道的.  
	                //因而这里要将单通道转成4通道  
	        
	        
	        //Bitmap b = decodeScaledFile(f);

	        //final float scale = mContext.getResources().getDisplayMetrics().density;
	        //int p = (int) (SIZE_DP * scale + 0.5f);

	        //Bitmap b2 = Bitmap.createScaledBitmap(b, p, p, true);
	        
	        //DisplayMetrics metrics = getResources().getDisplayMetrics();
	        
	       // bmp_gray.setDensity(metrics.densityDpi);
	        
            test_bmp = Bitmap.createScaledBitmap(bmp_gray,700, 1000, true);
            bmp_gray.recycle();
            background_image = test_bmp;
	
    	 } 
    
	
	@Override
	public void onDraw(Canvas canvas) {
		//super.onDraw(canvas);
		canvas.drawBitmap(background_image, 0, 0, null); 

		
	}
}