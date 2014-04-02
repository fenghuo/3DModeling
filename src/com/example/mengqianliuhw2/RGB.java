package com.example.mengqianliuhw2;

public class RGB {

	static double func1(double x) {
		if (x > 0.04045) {
			x = (x + 0.055) / 1.055;
			x = Math.pow(x, 2.4);
		} else {
			x = x / 12.92;
		}
		return x;
	}

	static double func2(double x) {
		if (x > 0.008856)
			x = Math.pow(x, (double) 1 / 3);
		else
			x = 7.787 * x + 16 / 116;
		return x;
	}
	
	static double distance(int c1,int c2){
		double[]t1=convert(c1);
		double[]t2=convert(c2);
		return Math.sqrt(Math.pow(t1[0]-t2[0], 2)+Math.pow(t1[1]-t2[1], 2)+Math.pow(t1[2]-t2[2], 2));
	}

	static double[] convert(int color){
		return convert((color & 0xff0000) >> 16, (color & 0xff00) >> 8,color & 0xff);
	}
	
	static double distance(int r1,int g1,int b1,int r2,int g2,int b2){
		double[]t1=convert(r1,g1,b1);
		double[]t2=convert(r2,g2,b2);
		return Math.sqrt(Math.pow(t1[0]-t2[0], 2)+Math.pow(t1[1]-t2[1], 2)+Math.pow(t1[2]-t2[2], 2));
	}
	
	static double[] convert(int r,int g,int b) {
		double x, y, z, x_1, y_1, z_1, L, A, B;
		double[] res = new double[3];
		x = (double) r / 255;
		y = (double) g / 255;
		z = (double) b / 255;

		x = func1(x) * 100;
		y = func1(y) * 100;
		z = func1(z) * 100;

		x_1 = 0.412453 * x + 0.357580 * y + 0.180423 * z;
		y_1 = 0.212671 * x + 0.715160 * y + 0.072169 * z;
		z_1 = 0.019334 * x + 0.119193 * y + 0.950227 * z;
		x = x_1 / 95.047;
		y = y_1 / 100;
		z = z_1 / 108.883;

		x = func2(x);
		y = func2(y);
		z = func2(z);
		L = 116 * y - 16;
		A = 500 * (x - y);
		B = 200 * (y - z);
		res[0] = L;
		res[1] = A;
		res[2] = B;
		return res;
	}
}