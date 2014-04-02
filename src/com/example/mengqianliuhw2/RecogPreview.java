package com.example.mengqianliuhw2;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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

public class RecogPreview extends View {
	 private static final String IMAGE_FN = "test1.jpg"; 
	 private Bitmap background_image; 
	 private Bitmap bmp_gray;
	 private Bitmap test_bmp;
	Paint paint = new Paint();


    public RecogPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        
        updateImage(Environment.getExternalStorageDirectory() + "/" + IMAGE_FN);
        
    }
    
    public void updateImage(String image_fn) { 
    	 // Set internal configuration to RGB_565 
    	 BitmapFactory.Options bitmap_options = new BitmapFactory.Options(); 
    	 bitmap_options.inPreferredConfig = Bitmap.Config.RGB_565; 
    	 
    	 background_image = BitmapFactory.decodeFile(image_fn, bitmap_options);
    	 
    	 Mat test = Highgui.imread(Environment.getExternalStorageDirectory() + "/" + IMAGE_FN);
    	 
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

	        for (int x = 0; x < lines.cols(); x++) 
	        {
	              double[] vec = lines.get(0, x);
	              double x1 = vec[0], 
	                     y1 = vec[1],
	                     x2 = vec[2],
	                     y2 = vec[3];
	              Point start = new Point(x1, y1);
	              Point end = new Point(x2, y2);

	              Core.line(mat_gray, start, end, new Scalar(255,0,0), 3);

	        }
	        
	                //由于最后将mat转成ARGB_8888型的Bitmap，输入必须是4通道的.  
	                //因而这里要将单通道转成4通道  
	        Mat gray4 = new Mat(mat_gray.rows(), mat_gray.cols(), CvType.CV_8UC4);  
	        Imgproc.cvtColor(mat_gray, gray4, Imgproc.COLOR_GRAY2BGRA, 4);  
	        //将mat对象转成Bitmap显示.  
	        bmp_gray = Bitmap.createBitmap(gray4.cols(), gray4.rows(), Config.ARGB_8888);  
	        Utils.matToBitmap(gray4, bmp_gray);  
	        
	        background_image = bmp_gray;
	        File photo= new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FN);
            Bitmap bitmap = decodeFile(photo);
            bitmap = Bitmap.createScaledBitmap(bmp_gray,700, 1000, true);
            background_image = bitmap;
            
	
    	 } 
    
	
	@Override
	public void onDraw(Canvas canvas) {
		//super.onDraw(canvas);
		canvas.drawBitmap(background_image, 0, 0, null); 

		
	}
	private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);              
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=70;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale++;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=2;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
	}
}
