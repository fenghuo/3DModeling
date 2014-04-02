#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <opencv2/opencv.hpp>
#include <iostream>
#include <queue>
#include <vector>
#include <set>

using namespace std;
using namespace cv;

//IplImage * change4channelTo3InIplImage(IplImage * src);

double*origin;

double func1(double x) {
	if (x > 0.04045) {
		x = (x + 0.055) / 1.055;
		x = pow(x, 2.4);
	} else {
		x = x / 12.92;
	}
	return x;
}

double func2(double x) {
	if (x > 0.008856)
		x = pow(x, 1.0d / 3);
	else
		x = 7.787 * x + 16 / 116;
	return x;
}

double* convert(int r, int g, int b) {
	double x, y, z, x_1, y_1, z_1, L, A, B;
	double* res = new double[3];
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

double* convert(int color) {
	return convert((color & 0xff0000) >> 16, (color & 0xff00) >> 8,
			color & 0xff);
}

double* convert2(int color) {
	return convert(color & 0xff, (color & 0xff00) >> 8,
			(color & 0xff0000) >> 16);
}
double pd(int c1, int c2) {
	double*t1 = convert(c1);
	double*t2 = convert(c2);
	return sqrt(
			pow(t1[0] - t2[0], 2) + pow(t1[1] - t2[1], 2)
					+ pow(t1[2] - t2[2], 2));
}
inline double pd(int c1) {
	double*t1 = convert(c1);
	double*t2 = origin;
	return sqrt(
			pow(t1[0] - t2[0], 2) + pow(t1[1] - t2[1], 2)
					+ pow(t1[2] - t2[2], 2));
}
inline double pd2(int c1) {
	double*t1 = convert2(c1);
	double*t2 = origin;
	return sqrt(
			pow(t1[0] - t2[0], 2) + pow(t1[1] - t2[1], 2)
					+ pow(t1[2] - t2[2], 2));
}
inline double pd(int r, int g, int b) {
	double*t1 = convert(r, g, b);
	double*t2 = origin;
	return sqrt(
			pow(t1[0] - t2[0], 2) + pow(t1[1] - t2[1], 2)
					+ pow(t1[2] - t2[2], 2));
}

extern "C" {
JNIEXPORT jdouble JNICALL Java_com_example_mengqianliuhw2_edgedetect_distance(
		JNIEnv* env, jobject obj, int c1, int c2);
JNIEXPORT jdouble JNICALL Java_com_example_mengqianliuhw2_edgedetect_distance(
		JNIEnv* env, jobject obj, int c1, int c2) {
	double*t1 = new double[1];
	double*t2 = new double[2];
	return sqrt(
			pow(t1[0] - t2[0], 2) + pow(t1[1] - t2[1], 2)
					+ pow(t1[2] - t2[2], 2));
}
}

int get(int a) {
	return 0;
}

extern "C" {
JNIEXPORT jintArray JNICALL Java_com_example_mengqianliuhw2_edgedetect_Fill(
		JNIEnv* env, jobject obj, jintArray buf, int w, int h, int m, int n,
		int K, int d);
JNIEXPORT jintArray JNICALL Java_com_example_mengqianliuhw2_edgedetect_Fill(
		JNIEnv* env, jobject obj, jintArray buf, int w, int h, int m, int n,
		int K, int d) {
	jint *cbuf;
	cbuf = env->GetIntArrayElements(buf, 0);

	std::queue<int> tox;
	std::queue<int> toy;

	bool*sel = new bool[w * h + 10]();
	bool*remain = new bool[w * h + 10]();

	tox.push(m);
	toy.push(n);

	origin = convert(cbuf[m * w + n]);

	int dir[][2] = { { -1, 0 }, { 1, 0 }, { 0, 1 }, { 0, -1 } };

	while (!tox.empty()) {

		int i = tox.front();
		int j = toy.front();
		tox.pop();
		toy.pop();

		if (i < 0 || i >= h || j < 0 || j >= w)
			continue;
		if (sel[i * w + j])
			continue;
		else
			sel[i * w + j] = true;

		if (pd(cbuf[i * w + j]) < K)
			for (int k = 0; k < 4; k++) {
				tox.push(i + dir[k][0]);
				toy.push(j + dir[k][1]);
			}
		else
			remain[i * w + j] = true;
	}

	for (int i = 0; i < h; i++)
		for (int j = 0; j < w; j++)
			if (remain[i * w + j])
				cbuf[i * w + j] = 0xFF000000;
			else
				cbuf[i * w + j] = 0xFFFFFFFF;

	delete[] sel;
	delete[] remain;

	return buf;
}
}

extern "C" {
JNIEXPORT jintArray JNICALL Java_com_example_mengqianliuhw2_edgedetect_Fill2(
		JNIEnv* env, jobject obj, jintArray buf, int w, int h, int m, int n,
		int K, int d);
JNIEXPORT jintArray JNICALL Java_com_example_mengqianliuhw2_edgedetect_Fill2(
		JNIEnv* env, jobject obj, jintArray buf, int w, int h, int m, int n,
		int K, int d) {
	jint *cbuf;
	cbuf = env->GetIntArrayElements(buf, 0);

	std::queue<int> tox;
	std::queue<int> toy;

	bool*sel = new bool[w * h + 10]();
	bool*remain = new bool[w * h + 10]();

	tox.push(m);
	toy.push(n);

	origin = convert(cbuf[m * w + n]);

	int dir[][2] = { { -d, 0 }, { d, 0 } };

	while (!tox.empty()) {

		int x = tox.front();
		int y = toy.front();
		tox.pop();
		toy.pop();

		int i, j;

		i = x;
		j = y;

		if (i < 0 || i >= h || j < 0 || j >= w)
			continue;

		while (j < w) {
			if (sel[i * w + j])
				break;
			sel[i * w + j] = true;

			if (pd(cbuf[i * w + j]) < K)
				for (int k = 0; k < 2; k++) {
					tox.push(i + dir[k][0]);
					toy.push(j + dir[k][1]);
				}
			else {
				remain[i * w + j] = true;
				break;
			}
			j++;
		}

		i = x;
		j = y - 1;

		while (j >= 0) {
			if (sel[i * w + j])
				break;
			sel[i * w + j] = true;

			if (pd(cbuf[i * w + j]) < K)
				for (int k = 0; k < 2; k++) {
					tox.push(i + dir[k][0]);
					toy.push(j + dir[k][1]);
				}
			else {
				remain[i * w + j] = true;
				break;
			}
			j--;
		}
	}

	for (int i = 0; i < h; i++)
		for (int j = 0; j < w; j++)
			if (remain[i * w + j])
				cbuf[i * w + j] = 0xFF000000;
			else
				cbuf[i * w + j] = 0xFFFFFFFF;

	delete[] sel;
	delete[] remain;

	return buf;
}
}

extern "C" {
JNIEXPORT jintArray JNICALL Java_com_example_mengqianliuhw2_edgedetect_Fill3(
		JNIEnv* env, jobject obj, jintArray buf, int w, int h, int m, int n,
		int K, int d);
JNIEXPORT jintArray JNICALL Java_com_example_mengqianliuhw2_edgedetect_Fill3(
		JNIEnv* env, jobject obj, jintArray buf, int w, int h, int m, int n,
		int K, int d) {
	jint *cbuf;
	cbuf = env->GetIntArrayElements(buf, 0);

	bool*remain = new bool[w * h + 10]();

	origin = convert(cbuf[m * w + n]);

	/*
	 int i, j;
	 for (i = 0; i < h; i++) {
	 for (j = 0; j < w; j++)
	 if (pd(cbuf[i * w + j]) > K) {
	 remain[i * w + j] = true;
	 break;
	 }
	 }
	 for (i = 0; i < h; i++) {
	 for (j = w - 1; j >= 0; j--)
	 if (pd(cbuf[i * w + j]) > K) {
	 remain[i * w + j] = true;
	 break;
	 }
	 }
	 for (i = h - 1; i >= 0; i--) {
	 for (j = 0; j < w; j++)
	 if (pd(cbuf[i * w + j]) > K) {
	 remain[i * w + j] = true;
	 break;
	 }
	 }
	 for (i = h - 1; i >= 0; i--) {
	 for (j = w - 1; j >= 0; j--)
	 if (pd(cbuf[i * w + j]) > K) {
	 remain[i * w + j] = true;
	 break;
	 }
	 }

	 for (int i = 0; i < h; i++)
	 for (int j = 0; j < w; j++)
	 if (remain[i * w + j])
	 cbuf[i * w + j] = 0xFF000000;
	 else
	 cbuf[i * w + j] = 0xFFFFFFFF;
	 */

	for (int i = 0; i < h; i++)
		for (int j = 0; j < w; j++)
			if (pd(cbuf[i * w + j]) < K)
				cbuf[i * w + j] = 0xFF000000;
			else
				cbuf[i * w + j] = 0xFFFFFFFF;

	delete[] remain;

	return buf;
}
}

extern "C" {
JNIEXPORT jintArray JNICALL Java_com_example_mengqianliuhw2_edgedetect_Fill4(
		JNIEnv* env, jobject obj, jintArray buf, int w, int h, int m, int n,
		int K, int d);
JNIEXPORT jintArray JNICALL Java_com_example_mengqianliuhw2_edgedetect_Fill4(
		JNIEnv* env, jobject obj, jintArray buf, int w, int h, int m, int n,
		int K, int d) {
	jint *cbuf;
	cbuf = env->GetIntArrayElements(buf, 0);

	std::queue<int> tox;
	std::queue<int> toy;

	bool*sel = new bool[w * h + 10]();
	bool*remain = new bool[w * h + 10]();

	tox.push(m);
	toy.push(n);

	origin = convert(cbuf[m * w + n]);

	int dir[][2] = { { -d, 0 }, { d, 0 } };

	while (!tox.empty()) {

		int x = tox.front();
		int y = toy.front();
		tox.pop();
		toy.pop();

		int i, j;

		i = x;
		j = y;

		if (i < 0 || i >= h || j < 0 || j >= w)
			continue;

		while (j < w) {
			if (sel[i * w + j])
				break;
			sel[i * w + j] = true;

			if (pd(cbuf[i * w + j]) < K)
				for (int k = 0; k < 2; k++) {
					tox.push(i + dir[k][0]);
					toy.push(j + dir[k][1]);
				}
			else {
				remain[i * w + j] = true;
				break;
			}
			j++;
		}

		i = x;
		j = y - 1;

		while (j >= 0) {
			if (sel[i * w + j])
				break;
			sel[i * w + j] = true;

			if (pd(cbuf[i * w + j]) < K)
				for (int k = 0; k < 2; k++) {
					tox.push(i + dir[k][0]);
					toy.push(j + dir[k][1]);
				}
			else {
				remain[i * w + j] = true;
				break;
			}
			j--;
		}
	}

	for (int i = 0; i < h; i++)
		for (int j = 0; j < w; j++)
			if (remain[i * w + j])
				cbuf[i * w + j] = 0xFF000000;
			else
				cbuf[i * w + j] = 0xFFFFFFFF;

	delete[] sel;
	delete[] remain;

	return buf;
}
}

extern "C" {
JNIEXPORT jbyteArray JNICALL Java_com_example_mengqianliuhw2_edgedetect_Fill5(
		JNIEnv* env, jobject obj, jbyteArray image, int w, int h, int m, int n,
		int K, int d);
JNIEXPORT jbyteArray JNICALL Java_com_example_mengqianliuhw2_edgedetect_Fill5(
		JNIEnv* env, jobject obj, jbyteArray image, int w, int h, int m, int n,
		int K, int d) {

	jbyte *cbuf;
	cbuf = env->GetByteArrayElements(image, 0);

	/*
	 std::queue<int> tox;
	 std::queue<int> toy;

	 bool*sel = new bool[w * h + 10]();
	 bool*remain = new bool[w * h + 10]();

	 tox.push(m);
	 toy.push(n);

	 origin = convert(cbuf[m * w + n]);

	 int dir[][2] = { { -d, 0 }, { d, 0 } };

	 while (!tox.empty()) {

	 int x = tox.front();
	 int y = toy.front();
	 tox.pop();
	 toy.pop();

	 int i, j;

	 i = x;
	 j = y;

	 if (i < 0 || i >= h || j < 0 || j >= w)
	 continue;

	 while (j < w) {
	 if (sel[i * w + j])
	 break;
	 sel[i * w + j] = true;

	 if (pd(cbuf[i * w + j]) < K)
	 for (int k = 0; k < 2; k++) {
	 tox.push(i + dir[k][0]);
	 toy.push(j + dir[k][1]);
	 }
	 else {
	 remain[i * w + j] = true;
	 break;
	 }
	 j++;
	 }

	 i = x;
	 j = y - 1;

	 while (j >= 0) {
	 if (sel[i * w + j])
	 break;
	 sel[i * w + j] = true;

	 if (pd(cbuf[i * w + j]) < K)
	 for (int k = 0; k < 2; k++) {
	 tox.push(i + dir[k][0]);
	 toy.push(j + dir[k][1]);
	 }
	 else {
	 remain[i * w + j] = true;
	 break;
	 }
	 j--;
	 }
	 }

	 for (int i = 0; i < h; i++)
	 for (int j = 0; j < w; j++)
	 if (remain[i * w + j])
	 cbuf[i * w + j] = 0xFF000000;
	 else
	 cbuf[i * w + j] = 0xFFFFFFFF;

	 delete[] sel;
	 delete[] remain;
	 */
	for (int i = 0; i < w; i++)
		for (int j = 0; j < h; j++)
			cbuf[i * h + j] = 0;

	return image;
}
}

extern "C" {
JNIEXPORT void JNICALL Java_com_example_mengqianliuhw2_edgedetect_Fill6(
		JNIEnv* env, jobject obj, jlong addr, int w, int h, int m, int n, int K,
		int d);
JNIEXPORT void JNICALL Java_com_example_mengqianliuhw2_edgedetect_Fill6(
		JNIEnv* env, jobject obj, jlong addr, int w, int h, int m, int n, int K,
		int d) {
	cv::Mat * image = (cv::Mat *) addr;
	//cv::Mat* pMatOut = (cv::Mat*)addr;

	std::queue<int> tox;
	std::queue<int> toy;

	bool*sel = new bool[w * h + 10]();
	bool*remain = new bool[w * h + 10]();

	tox.push(m);
	toy.push(n);
	int*start = image->ptr<int>(m, n);
	origin = convert2(*start);

	int dir[][2] = { { -d, 0 }, { d, 0 } };

	while (!tox.empty()) {

		int x = tox.front();
		int y = toy.front();
		tox.pop();
		toy.pop();

		int i, j;

		i = x;
		j = y;

		if (i < 0 || i >= h || j < 0 || j >= w)
			continue;

		while (j < w) {
			if (sel[i * w + j])
				break;
			sel[i * w + j] = true;

			if (pd2(*(image->ptr<int>(i, j))) < K)
				for (int k = 0; k < 2; k++) {
					tox.push(i + dir[k][0]);
					toy.push(j + dir[k][1]);
					remain[i * w + j] = true;
				}
			else {
				break;
			}
			j++;
		}

		i = x;
		j = y - 1;

		while (j >= 0) {
			if (sel[i * w + j])
				break;
			sel[i * w + j] = true;

			if (pd2(*(image->ptr<int>(i, j))) < K)
				for (int k = 0; k < 2; k++) {
					tox.push(i + dir[k][0]);
					toy.push(j + dir[k][1]);
					remain[i * w + j] = true;
				}
			else {
				break;
			}
			j--;
		}
	}

	for (int i = 0; i < h; i++)
		for (int j = 0; j < w; j++) {
			if (remain[i * w + j]) {
				for (int z = 0; z < d; z++) {
					if (i + z < h)
						*(image->ptr<int>(i + z, j)) = 0xFFFF0000;
					else
						break;
				}
			}
		}

	//cvtColor(img,*image,CV_RGB2RGBA);
	delete[] sel;
	delete[] remain;
	/*
	 for(int i=0;i<h;i++)
	 for(int j=0;j<w;j++)
	 {
	 int* arr=image->ptr<int>(i,j);
	 arr[0]=0xFFFFFFFF;
	 }
	 */
}
}

extern "C" {
JNIEXPORT void JNICALL Java_com_example_mengqianliuhw2_edgedetect_Fill7(
		JNIEnv* env, jobject obj, jlong addr, int w, int h, int m, int n, int K,
		int d);
JNIEXPORT void JNICALL Java_com_example_mengqianliuhw2_edgedetect_Fill7(
		JNIEnv* env, jobject obj, jlong addr, int w, int h, int m, int n, int K,
		int d) {
	cv::Mat * image = (cv::Mat *) addr;
	//cv::Mat* pMatOut = (cv::Mat*)addr;

	std::queue<int> tox;
	std::queue<int> toy;

	bool*sel = new bool[w * h + 10]();
	bool*remain = new bool[w * h + 10]();

	tox.push(m);
	toy.push(n);
	int*start = image->ptr<int>(m, n);
	origin = convert2(*start);

	int dir[][2] = { { -d, 0 }, { d, 0 } };

	while (!tox.empty()) {

		int x = tox.front();
		int y = toy.front();
		tox.pop();
		toy.pop();

		int i, j;

		i = x;
		j = y;

		if (i < 0 || i >= h || j < 0 || j >= w)
			continue;

		while (j < w) {
			if (sel[i * w + j])
				break;
			sel[i * w + j] = true;

			if (pd2(*(image->ptr<int>(i, j))) < K)
				for (int k = 0; k < 2; k++) {
					tox.push(i + dir[k][0]);
					toy.push(j + dir[k][1]);
				}
			else {
				remain[i * w + j] = true;
				break;
			}
			j++;
		}

		i = x;
		j = y - 1;

		while (j >= 0) {
			if (sel[i * w + j])
				break;
			sel[i * w + j] = true;

			if (pd2(*(image->ptr<int>(i, j))) < K)
				for (int k = 0; k < 2; k++) {
					tox.push(i + dir[k][0]);
					toy.push(j + dir[k][1]);
				}
			else {
				remain[i * w + j] = true;
				break;
			}
			j--;
		}
	}

	for (int i = 0; i < h; i++)
		for (int j = 0; j < w; j++) {
			if (remain[i * w + j]) {
				for (int z = 0; z < d; z++) {
					if (i + z < h)
						*(image->ptr<int>(i + z, j)) = 0xFF000000;
					else
						break;
				}
			} else {
				for (int z = 0; z < d; z++) {
					if (i + z < h)
						*(image->ptr<int>(i + z, j)) = 0xFFFFFFFF;
					else
						break;
				}
			}
		}

	//cvtColor(img,*image,CV_RGB2RGBA);
	delete[] sel;
	delete[] remain;
	/*
	 for(int i=0;i<h;i++)
	 for(int j=0;j<w;j++)
	 {
	 int* arr=image->ptr<int>(i,j);
	 arr[0]=0xFFFFFFFF;
	 }
	 */
}
}

