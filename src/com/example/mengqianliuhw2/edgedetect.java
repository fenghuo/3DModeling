package com.example.mengqianliuhw2;

import org.opencv.android.OpenCVLoader;
  
public class edgedetect {    
static {     
	if (!OpenCVLoader.initDebug()) { // Handle initialization error
	} else { System.loadLibrary("EdgeDetect");     
       }     
}
      /**  
            * @param width the current view width  
            * @param height the current view height  
*/   
    public static native int[] EdgeDetect(int[] buf, int w, int h);  

    public static native int[] Fill(int image[],int w,int h,int m,int n,int k,int d); 
    
    public static native int[] Fill2(int image[],int w,int h,int m,int n,int k,int d); 

    public static native int[] Fill3(int image[],int w,int h,int m,int n,int k,int d);
    
    public static native int[] Fill4(int image[],int w,int h,int m,int n,int k,int d);   
    //mat
    public static native byte[] Fill5(byte image[],int w,int h,int m,int n,int k,int d);   
    //mat
    public static native void Fill6(long addr,int w,int h,int m,int n,int k,int d);   
    //mat
    public static native void Fill7(long addr,int w,int h,int m,int n,int k,int d);   
}  
