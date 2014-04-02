package com.example.mengqianliuhw2;

import java.util.HashSet;

import java.util.LinkedList;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class Utility {
	
	static int K=44;
	static int D=10;

	public static Mat fillPic(Mat image){
		return fillPic(image,0,0);
	}
	public static Bitmap fillPic3(Bitmap image)
	{
		return fillPic3(image,0,0,K,D);
	}
	public static Bitmap fillPic3(Bitmap image,int m, int n,int a,int b)
	{
		int []arr=new int[image.getWidth()*image.getHeight()];
		image.getPixels(arr, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
		
		//arr=edgedetect.Fill(arr, image.getWidth(), image.getHeight(),m,n);
		arr=edgedetect.Fill2(arr, image.getWidth(), image.getHeight(),m,n,a,b);
		image.recycle();
		image=Bitmap.createBitmap(arr, image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
		return image;
	}
	public static void fillPic3(Mat image,int m, int n,int a,int b)
	{	
		//byte[]arr=new byte[(int) (image.total()*image.channels())];
		//image.get(0, 0, arr);
		//arr=edgedetect.Fill5(arr, image.width(), image.height(),m,n,a,b);
		//image.put(0, 0, arr);
		edgedetect.Fill6(image.getNativeObjAddr(), image.width(), image.height(),m,n,a,b);
	}
	public static void fillPic7(Mat image,int m, int n,int a,int b)
	{	
		edgedetect.Fill7(image.getNativeObjAddr(), image.width(), image.height(),m,n,a,b);
	}
	public static Bitmap fillPic4(Bitmap image)
	{
		return fillPic4(image,0,0,K,D);
	}
	public static Bitmap fillPic4(Bitmap image,int m, int n,int a,int b)
	{
		int []arr=new int[image.getWidth()*image.getHeight()];
		image.getPixels(arr, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
		//arr=edgedetect.Fill(arr, image.getWidth(), image.getHeight(),m,n);
		arr=edgedetect.Fill4(arr, image.getWidth(), image.getHeight(),m,n,a,b);
		image=Bitmap.createBitmap(arr, image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
		return image;
	}
	public static Mat fillPic(Mat image,int m,int n){
		
		Mat im_res = new Mat(image.height()+2 , image.width()+2, CvType.CV_8UC1); 
		Mat image2=new Mat(image.height() , image.width(), CvType.CV_8UC3);
		
		Imgproc.cvtColor(image,image,Imgproc.COLOR_RGBA2RGB);
		
        // This simply changes the mask from being a 0-1 value to 0-255 value for viewing
		
		//Imgproc.floodFill(image, im_res, new Point(m,n), new Scalar(new double[]{255,0,0}));
		Imgproc.floodFill(image, im_res, new Point(m,n), new Scalar(new double[]{255,0,0}), new Rect(), new Scalar(new double[]{0,0,0}), new Scalar(new double[]{40,40,40}) , 4);
		return image;
	}
	
	public static Mat fillPic2(Bitmap image)
	{
		return fillPic2(image,0,0);
	}
	public static Mat fillPic2(Bitmap image,int m,int n)
	{
		/*
		int width = image.getWidth();
		int height = image.getHeight();
		int k=image.getPixel(m, n);
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++) {
				if(RGB.distance(image.getPixel(i, j),k)<44)
					image.setPixel(i, j, k);
			}
		*/
		Mat im_res = new Mat(image.getHeight()+2 , image.getWidth()+2, CvType.CV_8UC1); 
		Mat image2=new Mat(image.getHeight() , image.getWidth(), CvType.CV_8UC3);
		Mat image3=new Mat(image.getHeight() , image.getWidth(), CvType.CV_8UC3);
		
		Utils.bitmapToMat(image,image2); 
		Utils.bitmapToMat(image,image3); 
		Imgproc.cvtColor(image2,image2,Imgproc.COLOR_RGBA2RGB);
		Imgproc.cvtColor(image2,image3,Imgproc.COLOR_RGB2Lab);
		
		
		
        // This simply changes the mask from being a 0-1 value to 0-255 value for viewing
		
		Imgproc.floodFill(image3, im_res, new Point(m,n), new Scalar(new double[]{255,0,0}));
		//Imgproc.floodFill(image2, im_res, new Point(m,n), new Scalar(new double[]{255,0,0}), new Rect(), new Scalar(new double[]{0,0,0}), new Scalar(new double[]{40,40,40}) , 4);
		return image3;
		
	}
	public static void fillPic(Bitmap image) {
		fillPic(image,0,0);
	}
	 
	public static void fillPic(Bitmap image,int m,int n) {
		int width = image.getWidth();
		int height = image.getHeight();
		
		HashSet<Integer>remain=floodFill2(image,m,n);
		
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++) {
				if (remain.contains(i * 10000 + j)) {
					image.setPixel(i, j, Color.rgb(0, 0, 0));
				}else {
					image.setPixel(i, j, Color.rgb(255, 255, 255));
				}	
			}
	}
	
	public static HashSet<Integer> floodFill2(Bitmap image,int m,int n){
		int width = image.getWidth();
		int height = image.getHeight();
		
		LinkedList<Integer> to = new LinkedList<Integer>();

		HashSet<Integer> sel = new HashSet<Integer>();
		HashSet<Integer> remain = new HashSet<Integer>();
		
		for(int i=0;i<height;i++)
			to.add(i);

		int dir[][] = { { -1, 0 }, { 1, 0 }, { 0, 1 }, { 0, -1 } };

		while (!to.isEmpty()) {
			
			System.out.println(to.size());
			int q=to.poll();
			
			int i = q/10000;
			int j = q%10000;

			if (i < 0 || i >= width || j < 0 || j >= height)
				continue;
			if (sel.contains(i * 10000 + j))
				continue;
			else
				sel.add(i * 10000 + j);
			
			/*
			if(image.getPixel(5,5)==image.getPixel(i, j)){
				for (int k = 0; k < 4; k++) {
					to.addLast((i + dir[k][0])*10000+(j + dir[k][1]));
				}
			}*/
			double d=distance(image,i,j,m,n);
			System.out.println(d);
			if (d < 44) {
				for (int k = 0; k < 4; k++) 
					to.addLast((i + dir[k][0])*10000+(j + dir[k][1]));
			}
			else
				remain.add(i * 10000 + j);
		}
		return remain;
	}
	

	public static HashSet<Integer> floodFill(Bitmap image,int m,int n){
		int width = image.getWidth();
		int height = image.getHeight();
		
		LinkedList<Integer> x = new LinkedList<Integer>();
		LinkedList<Integer> y = new LinkedList<Integer>();

		HashSet<Integer> sel = new HashSet<Integer>();
		HashSet<Integer> remain = new HashSet<Integer>();

		x.add(0);
		y.add(0);

		int dir[][] = { { -1, 0 }, { 1, 0 }, { 0, 1 }, { 0, -1 } };

		while (!x.isEmpty()) {
			
			System.out.println(x.size());
			if(x.size()>1000)
				break;
			int i = x.getFirst();
			int j = y.getFirst();

			x.removeFirst();
			y.removeFirst();

			if (i < 0 || i >= width || j < 0 || j >= height)
				continue;
			if (sel.contains(i * 10000 + j))
				continue;
			else
				sel.add(i * 10000 + j);
			
			/*if (distance(image,i,j,m,n) < 44) {
				for (int k = 0; k < 4; k++) {
					x.addLast(i + dir[k][0]);
					y.addLast(j + dir[k][1]);
				}
			}*/
			System.out.println(image.getPixel(5,5)+" -- "+image.getPixel(i, j));
			if(image.getPixel(5,5)==image.getPixel(i, j)){
				for (int k = 0; k < 4; k++) {
					x.addLast(i + dir[k][0]);
					y.addLast(j + dir[k][1]);
				}
			}
			else
				remain.add(i * 10000 + j);
		}
		return remain;
	}
	
	static double distance(Bitmap image, int m, int n, int i, int j) {
		return RGB.distance(image.getPixel(m, n), image.getPixel(i, j));
	}
}