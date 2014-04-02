
package com.example.mengqianliuhw2;

import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

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
 
public class PointsDetect {

	static public ArrayList<Point2D> getPoints(Mat lines)
	{
        ArrayList<StraightLine2D>alines=new ArrayList<StraightLine2D>();
        
        double hx=0,hy=0,lx=100000,ly=100000;
        for (int x = 0; x < lines.cols(); x++) 
        {
              double[] vec = lines.get(0, x);
              double x1 = vec[0], 
                     y1 = vec[1],
                     x2 = vec[2],
                     y2 = vec[3];

              if(x1>hx)
            	  hx=x1;
              else if(x1<lx)
            	  lx=x1;
              if(x2>hx)
            	  hx=x2;
              else if(x2<lx)
            	  lx=x2;
              if(y1>hy)
            	  hy=y1;
              else if(y1<ly)
            	  ly=y1;
              if(y2>hy)
            	  hy=y2;
              else if(y2<ly)
            	  ly=y2;
              
              alines.add(new StraightLine2D(new Point2D(x1,y1),new Point2D(x2,y2)));
        }
        final Point2D midpoint = new Point2D((hx+lx)/2,(hy+ly)/2);
        
        class Vector
        {
        	public Vector2D v;
        	public double a;
        	public int rank;
        	public double diff;
        	public Point2D p;
        	
        	public Vector()
        	{}
        	
        	public Vector (Vector2D vv)
        	{
        		v=vv;
        		a=vv.angle();
        		while(a<0)
        			a+=Math.PI*2;
        		while(a>2*Math.PI)
        			a-=Math.PI*2;
        		diff=a;
        		p=midpoint.plus(vv);
        	}
        }
        
        class com1 implements Comparator<Vector>
        {
			@Override
			public int compare(Vector l, Vector r) {
				if(l.a==r.a)
					return 0;
				else if(l.a<r.a)
					return -1;
				else
					return 1;
			}
        	
        }
        class com2 implements Comparator<Vector>
        {
			@Override
			public int compare(Vector l, Vector r) {
				if(l.diff==r.diff)
					return 0;
				else if(l.diff<r.diff)
					return -1;
				else
					return 1;
			}
        	
        }
        class com3 implements Comparator<Vector>
        {
			@Override
			public int compare(Vector l, Vector r) {
				// TODO Auto-generated method stub
				return l.rank-r.rank;
			}
        	
        }
        Vector[]vectors=new Vector[lines.cols()+1];
        
        for (int x = 0; x < lines.cols(); x++) 
        {
        	StraightLine2D v=alines.get(x).perpendicular(midpoint);
        	Point2D t=v.intersection(alines.get(x));
        	vectors[x]=new Vector(new Vector2D(t,midpoint));
        	vectors[x].p=t;
        }

        Arrays.sort(vectors,0,vectors.length-1,new com1());
        
        vectors[lines.cols()]=new Vector();
        vectors[lines.cols()].a=vectors[lines.cols()].diff=vectors[0].a+2*Math.PI;
        
        PriorityQueue<Vector> p=new PriorityQueue<Vector> (10,new com1());
        
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
        
        Arrays.sort(tt, new com3());
        ArrayList<ArrayList<Vector>> vclass=new  ArrayList<ArrayList<Vector>>();
        
        for(int i=0;i<6;i++)
        	vclass.add(new ArrayList<Vector>());
        int k=0;
        
        for(int i=0;i< vectors.length-1;i++)
        {
        	k=k%6;
        	vclass.get(k).add(vectors[i]);
        	if(vectors[i].rank==((Vector)tt[k]).rank)
        		k++;
        }
        
        ArrayList<Point2D> points=new  ArrayList<Point2D>();
        ArrayList<StraightLine2D> blines=new  ArrayList<StraightLine2D>();
        
        for(int i=0;i<6;i++)
        {
        	double sx=0,sy=0;
        	int size=vclass.get(i).size();
        	for(int j=0;j<size;j++)
        	{
        		sx+=vclass.get(i).get(j).p.x();
        		sy+=vclass.get(i).get(j).p.y();
        	}
        	Point2D q=new Point2D(sx/size,sy/size);
        	StraightLine2D l=new StraightLine2D(q,new Vector2D(q,midpoint)).perpendicular(q);
            blines.add(l);
        }

        for(int i=0;i<6;i++)
        	points.add(blines.get(i).intersection(blines.get((i+1)%6)));
        
        ArrayList<StraightLine2D> clines = new ArrayList<StraightLine2D>();
        for(int i=0;i<6;i++)
        	clines.add(new StraightLine2D(points.get(i),points.get((i+1)%6)));
        
        ArrayList<StraightLine2D> dlines = new ArrayList<StraightLine2D>();
        ArrayList<StraightLine2D> elines = new ArrayList<StraightLine2D>();
        ArrayList<Point2D> vpoints=new ArrayList<Point2D>();
        for(int i=0;i<3;i++)
        {
        	Point2D t=clines.get(i).intersection(clines.get(i+3));
        	dlines.add(new StraightLine2D(t,points.get(i+2)));
        	elines.add(new StraightLine2D(t,points.get((i+5)%6)));
        	vpoints.add(t);
        }
        
        double sx=0,sy=0,rx=0,ry=0;
        for(int i=0;i<3;i++)
        {
        	Point2D t=dlines.get(i).intersection(dlines.get((i+1)%3));
        	Point2D s=elines.get(i).intersection(elines.get((i+1)%3));
        	sx+=t.x();
        	sy+=t.y();
        	rx+=s.x();
        	ry+=s.y();
        }
        Point2D p1=new Point2D(sx/3,sy/3);
        Point2D p2=new Point2D(rx/3,ry/3);
        
        Vector2D v1=new Vector2D(vpoints.get(1),points.get(0));
        Vector2D v2=new Vector2D(points.get(3),p1);
        
        if(v1.dot(v2)>0)
        {
        	points.add(p2);
        }
        else
        {
        	for(int i=0;i<3;i++)
        		points.add(points.remove(0));
        	points.add(p1);
        }
        return points;
	}
}
