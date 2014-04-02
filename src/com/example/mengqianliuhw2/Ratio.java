package com.example.mengqianliuhw2;

import java.util.ArrayList;
import java.util.Arrays;

import math.geom2d.*;
import math.geom2d.line.Line2D;
import math.geom2d.line.StraightLine2D;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.line.StraightLine3D;
import math.geom3d.plane.Plane3D;

public class Ratio{
	public static double[] getRatio(double x1,double y1,double x2,double y2,double x3,double y3,double x4,double y4,double x5,double y5,double x6,double y6)
	{
		Point2D p1=new Point2D(x1,y1);
		Point2D p2=new Point2D(x2,y2);
		Point2D p3=new Point2D(x3,y3);
		Point2D p4=new Point2D(x4,y4);
		Point2D p5=new Point2D(x5,y5);
		Point2D p6=new Point2D(x6,y6);
		
		return ratio(p1,p2,p3,p4,p5,p6);
	}
	
	public static double[]getRatio(ArrayList<Point2D>points)
	{
		return ratio(
					points.get(1),
					points.get(0),
					points.get(5),
					points.get(6),
					points.get(2),
					points.get(4)
				);
	}

	public static double[]getRatioB(Point2D p1,Point2D p2,Point2D p3,Point2D p4, Point2D p5,Point2D p6)
	{
		StraightLine2D l1=new StraightLine2D(p1,p3);
		StraightLine2D l2=new StraightLine2D(p2,p4);
		Point2D v1=l1.intersection(l2);
		StraightLine2D l5=new StraightLine2D(v1,p6);
		
		StraightLine2D l3=new StraightLine2D(p5,p3);
		StraightLine2D l4=new StraightLine2D(p6,p4);
		Point2D v2=l3.intersection(l4);
		StraightLine2D l6=new StraightLine2D(v2,p2);	
		
		Point2D p7=l6.intersection(l5);
		
		return ratio(p3,p1,p2,p4,p5,p7);
	}
	
	
	public static double[] ratio(Point2D A,Point2D B,Point2D C,Point2D D, Point2D E,Point2D F)
	{
		StraightLine2D l1=new StraightLine2D(A,B);
		StraightLine2D l2=new StraightLine2D(C,B);
		StraightLine2D l3=new StraightLine2D(C,D);
		StraightLine2D l4=new StraightLine2D(A,D);
		StraightLine2D l5=new StraightLine2D(A,E);
		StraightLine2D l6=new StraightLine2D(C,F);
		
		Point2D v1=l1.intersection(l3);
		Point2D v2=l2.intersection(l4);
		Point2D v3=l5.intersection(l6);
		
		//start of calibration
		
		double k1=Math.pow(v1.distance(v2),2);
		double k2=Math.pow(v2.distance(v3),2);
		double k3=Math.pow(v3.distance(v1),2);
		
		double a=Math.sqrt((k1+k2+k3)/2-k2);
		double b=Math.sqrt((k1+k2+k3)/2-k3);
		double c=Math.sqrt((k1+k2+k3)/2-k1);
		
		Point3D t0=new Point3D(0,0,0);
		Point3D t1=new Point3D(a,0,0);
		Point3D t2=new Point3D(0,b,0);
		Point3D t3=new Point3D(0,0,c);
		Vector3D z1=new Vector3D(t1,t2);
		Vector3D z2=new Vector3D(t1,t3);
		
		Plane3D p=new Plane3D(t1,z1,z2);
		
		Vector3D vp=p.normal();
		StraightLine3D f=new StraightLine3D(t0,vp);
		
		double d=p.distance(t0);
		Point3D fp=p.lineIntersection(f);
		
		System.out.println(fp.getX()+" "+fp.getY()+" "+fp.getZ());
		
		double r1=getR(t0,t1,t2);
		double r2=getR(t0,t1,t3);
		double r3=getR(t0,t2,t3);
		
		Point2D v12=new Point2D((v1.x()-v2.x())/(r1+1)+v2.x(),(v1.y()-v2.y())/(r1+1)+v2.y());
		Point2D v13=new Point2D((v1.x()-v3.x())/(r2+1)+v3.x(),(v1.y()-v3.y())/(r2+1)+v3.y());
		Point2D v23=new Point2D((v2.x()-v3.x())/(r3+1)+v3.x(),(v2.y()-v3.y())/(r3+1)+v3.y());
		
		StraightLine2D lv12=new StraightLine2D(v1,v2);
		StraightLine2D lv13=new StraightLine2D(v1,v3);
		
		Point2D uv=lv12.perpendicular(v12).intersection(lv13.perpendicular(v13));
		
		System.out.println("uv:");
		System.out.println(uv.x()+" "+uv.y());
		
		System.out.println("foucs");
		System.out.println(d);
		
		//end of calibration
		
		double h=d;
		Point2D O=uv;
		
		Point3D O3=new Point3D(0,0,0);
		Point3D A3=new Point3D(A.x()-O.x(),A.y()-O.y(),h);
		Point3D B3=new Point3D(B.x()-O.x(),B.y()-O.y(),h);
		Point3D C3=new Point3D(C.x()-O.x(),C.y()-O.y(),h);
		Point3D D3=new Point3D(D.x()-O.x(),D.y()-O.y(),h);
		Point3D E3=new Point3D(E.x()-O.x(),E.y()-O.y(),h);
		Point3D F3=new Point3D(F.x()-O.x(),F.y()-O.y(),h);
		Point3D V1=new Point3D(v1.x()-O.x(),v1.y()-O.y(),h);
		Point3D V2=new Point3D(v2.x()-O.x(),v2.y()-O.y(),h);
		Point3D V3=new Point3D(v3.x()-O.x(),v3.y()-O.y(),h);
		
		Vector3D VV1=new Vector3D(V1);
		Vector3D VV2=new Vector3D(V2);
		Vector3D VV3=new Vector3D(V3);
		Vector3D VA=new Vector3D(A3);
		Vector3D VB=new Vector3D(B3);
		Vector3D VC=new Vector3D(C3);
		Vector3D VD=new Vector3D(D3);
		Vector3D VE=new Vector3D(E3);
		Vector3D VF=new Vector3D(F3);
		
		StraightLine3D LAB=new StraightLine3D(A3,VV1); 
		StraightLine3D LAD=new StraightLine3D(A3,VV2); 
		StraightLine3D LAE=new StraightLine3D(A3,VV3); 
		Plane3D PBC=new Plane3D(O3,VB,VC);
		Plane3D PCD=new Plane3D(O3,VD,VC);
		Plane3D PEF=new Plane3D(O3,VE,VF);
		
		Point3D BB=PBC.lineIntersection(LAB);
		Point3D DD=PCD.lineIntersection(LAD);
		Point3D EE=PEF.lineIntersection(LAE);
		double []result=new double[3];
		result[0]=A3.distance(DD)/A3.distance(BB);
		result[1]=1;
		result[2]=A3.distance(EE)/A3.distance(BB);
		
		return result;
	}
	
	public static double[] ratioB(Point2D A,Point2D B,Point2D C,Point2D D, Point2D E,Point2D F)
	{
		return null;
		
	}
	
	//a,b,c 
	public static double getR(Point3D a,Point3D b,Point3D c)
	{
		double A=c.distance(b);
		double B=a.distance(c);
		double C=b.distance(a);
		
		double cosB=(A*A+C*C-B*B)/2/A/C;
		double cosC=(B*B+A*A-C*C)/2/B/A;
		
		double angleB=Math.acos(cosB);
		double angleC=Math.acos(cosC);

		return Math.tan(angleC)/Math.tan(angleB);
	}
	public static double getArea(double a,double b,double c)
	{
		double p=(a+b+c)/2;
		return Math.sqrt(p*(p-a)*(p-b)*(p-c));
	}
}


/*
 * 	245,939
 *  1205,635
 *  2333,1699
 *  1149,2311
 *  353,1435
 *  2133,2159
 */