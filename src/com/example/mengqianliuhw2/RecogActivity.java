package com.example.mengqianliuhw2;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import math.geom2d.Point2D;
import math.geom2d.line.StraightLine2D;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class RecogActivity extends Activity {

	private Matrix matrix = new Matrix();
	private static Bitmap draw;

	public static ArrayList<Point2D> points = null;
	public static double[] ratio = null;
	public static boolean run = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recog);

		ImageView mImageView = (ImageView) findViewById(R.id.imageView2);

		if (run) {
			if (MainActivity.IMAGE_SELECTION == 100) {
				// read in the picture that is just taken
				/*
				 * Bitmap bitmap =
				 * BitmapFactory.decodeFile(Environment.getExternalStorageDirectory
				 * ().getPath() + "/image" + MainActivity.N + ".jpg");
				 * matrix.setRotate(90); bitmap = Bitmap.createBitmap(bitmap, 0,
				 * 0, bitmap.getWidth(),bitmap.getHeight(), matrix, true);
				 * 
				 * bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
				 * 
				 * long t=java.lang.System.currentTimeMillis();
				 * 
				 * bitmap=Utility.fillPic4(bitmap,0,0,44,1);
				 * 
				 * System.out.println(java.lang.System.currentTimeMillis()-t);
				 * 
				 * linedetection(bitmap);
				 * 
				 * mImageView.setImageBitmap(draw); //bitmap.recycle();
				 */
				Mat mat = Highgui.imread(Environment
						.getExternalStorageDirectory().getPath()
						+ "/image"
						+ MainActivity.N + ".jpg");

				Utility.fillPic7(
						mat,
						(int) (DetectActivity.W * 1.0 / mat.width() * DetectActivity.X),
						(int) (DetectActivity.H * 1.0 / mat.height() * DetectActivity.Y),
						DetectActivity.pace2, 1);

				linedetection(mat);

				/*
				 * Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(),
				 * Config.ARGB_8888); Utils.matToBitmap(mat, bitmap);
				 */
				mImageView.setImageBitmap(draw);
			} else {
				Bitmap bitmap = BitmapFactory.decodeFile(Environment
						.getExternalStorageDirectory().getPath()
						+ "/building1.jpg");
				switch (MainActivity.IMAGE_SELECTION) {
				case 0:
					bitmap = BitmapFactory.decodeFile(Environment
							.getExternalStorageDirectory().getPath()
							+ "/building1.jpg");
					break;
				case 1:
					bitmap = BitmapFactory.decodeFile(Environment
							.getExternalStorageDirectory().getPath()
							+ "/building2.jpg");
					break;
				case 2:
					bitmap = BitmapFactory.decodeFile(Environment
							.getExternalStorageDirectory().getPath()
							+ "/building3.jpg");
					break;
				case 3:
					bitmap = BitmapFactory.decodeFile(Environment
							.getExternalStorageDirectory().getPath()
							+ "/testsoap.jpg");
					break;
				}

				bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

				long t = java.lang.System.currentTimeMillis();

				bitmap = Utility.fillPic4(bitmap, 0, 0, 44, 1);

				System.out.println(java.lang.System.currentTimeMillis() - t);

				linedetection(bitmap);

				mImageView.setImageBitmap(draw);
				// mImageView.setImageBitmap(bitmap);
			}
		} else
			mImageView.setImageBitmap(draw);

		System.gc();

		// Add a listener to the Capture button
		Button captureButton = (Button) findViewById(R.id.draw_mask);
		captureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(RecogActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();

			}

		}

		);

		// Add a listener to the manual recognition button
		Button Show3DButton = (Button) findViewById(R.id.show_3d);
		Show3DButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(RecogActivity.this,
						OpenGLES20Complete.class);
				startActivity(intent);
				finish();

			}

		}

		);
	}

	public static Bitmap linedetection(Mat image) {
		// 将彩色Mat对象转成单通道的灰度Mat.
		Mat mat_gray = new Mat();
		Imgproc.cvtColor(image, mat_gray, Imgproc.COLOR_BGRA2GRAY, 1);
		Imgproc.Canny(mat_gray, mat_gray, 80, 100);

		// houghline detection
		Mat lines = new Mat();
		int threshold = 50;
		int minLineSize = 80;
		int lineGap = 10;
		Imgproc.HoughLinesP(mat_gray, lines, 1, Math.PI / 180, threshold,
				minLineSize, lineGap);
		try {
			RecogActivity.points = PointsDetect.getPoints(lines);
		} catch (Exception e) {
			return null;
		}
		mat_gray = new Mat(mat_gray.rows(), mat_gray.cols(), mat_gray.type());

		for (int i = 0; i < points.size() - 1; i++) {
			double x1 = points.get(i).x();
			double y1 = points.get(i).y();
			double x2 = points.get((i + 1) % (points.size() - 1)).x();
			double y2 = points.get((i + 1) % (points.size() - 1)).y();
			Point start = new Point(x1, y1);
			Point end = new Point(x2, y2);
			Core.line(mat_gray, start, end, new Scalar(255, 0, 0), 3);
			if (i % 2 == 1) {
				start = new Point(points.get(i).x(), points.get(i).y());
				end = new Point(points.get(points.size() - 1).x(), points.get(
						points.size() - 1).y());
				Core.line(mat_gray, start, end, new Scalar(255, 0, 0), 3);
			}
		}

		try {

			RecogActivity.ratio = Ratio.getRatio(points);
			// System.out.println(ratio[0]+","+ratio[1]+","+ratio[2]);
		} catch (Exception e) {
			System.out.println("Unable to calculate");
			return null;
		}
		Mat gray4 = new Mat(mat_gray.rows(), mat_gray.cols(), CvType.CV_8UC4);
		Imgproc.cvtColor(mat_gray, gray4, Imgproc.COLOR_GRAY2BGRA, 4);
		// 将mat对象转成Bitmap显示.
		Bitmap bitmap = Bitmap.createBitmap(gray4.cols(), gray4.rows(),
				Config.ARGB_8888);
		// bitmap = Bitmap.createBitmap(image.cols(), image.rows(),
		// Config.ARGB_8888);
		Utils.matToBitmap(gray4, bitmap);
		// Utils.matToBitmap(image,bitmap);
		draw = bitmap;
		return null;
	}

	public static Bitmap linedetection(Bitmap bitmap) {
		Mat mat_bmp = new Mat(bitmap.getHeight(), bitmap.getWidth(),
				CvType.CV_8UC4);
		Utils.bitmapToMat(bitmap, mat_bmp);
		// 将彩色Mat对象转成单通道的灰度Mat.
		Mat mat_gray = new Mat();
		Imgproc.cvtColor(mat_bmp, mat_gray, Imgproc.COLOR_BGRA2GRAY, 1);
		Imgproc.Canny(mat_gray, mat_gray, 80, 100);

		// flood fill

		// Mat image = new Mat(bitmap.getHeight() , bitmap.getWidth(),
		// CvType.CV_8UC4);
		// Utils.bitmapToMat(bitmap,image);

		// houghline detection
		Mat lines = new Mat();
		int threshold = 50;
		int minLineSize = 80;
		int lineGap = 10;
		Imgproc.HoughLinesP(mat_gray, lines, 1, Math.PI / 180, threshold,
				minLineSize, lineGap);

		try {
			RecogActivity.points = PointsDetect.getPoints(lines);
		} catch (Exception e) {
			return null;
		}
		mat_gray = new Mat(mat_gray.rows(), mat_gray.cols(), mat_gray.type());

		for (int i = 0; i < points.size() - 1; i++) {
			double x1 = points.get(i).x();
			double y1 = points.get(i).y();
			double x2 = points.get((i + 1) % (points.size() - 1)).x();
			double y2 = points.get((i + 1) % (points.size() - 1)).y();
			Point start = new Point(x1, y1);
			Point end = new Point(x2, y2);
			Core.line(mat_gray, start, end, new Scalar(255, 0, 0), 3);
			if (i % 2 == 1) {
				start = new Point(points.get(i).x(), points.get(i).y());
				end = new Point(points.get(points.size() - 1).x(), points.get(
						points.size() - 1).y());
				Core.line(mat_gray, start, end, new Scalar(255, 0, 0), 3);
			}
		}

		try {

			RecogActivity.ratio = Ratio.getRatio(points);
			// System.out.println(ratio[0]+","+ratio[1]+","+ratio[2]);
		} catch (Exception e) {
			System.out.println("Unable to calculate");
			return null;
		}
		Mat gray4 = new Mat(mat_gray.rows(), mat_gray.cols(), CvType.CV_8UC4);
		Imgproc.cvtColor(mat_gray, gray4, Imgproc.COLOR_GRAY2BGRA, 4);
		// 将mat对象转成Bitmap显示.
		bitmap = Bitmap.createBitmap(gray4.cols(), gray4.rows(),
				Config.ARGB_8888);
		// bitmap = Bitmap.createBitmap(image.cols(), image.rows(),
		// Config.ARGB_8888);
		Utils.matToBitmap(gray4, bitmap);
		// Utils.matToBitmap(image,bitmap);
		draw = bitmap;
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.draw, menu);
		return true;
	}
}
