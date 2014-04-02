package com.example.mengqianliuhw2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import math.geom2d.Point2D;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Environment;
import android.util.Log;


public class MyGLRenderer implements GLSurfaceView.Renderer {

	private Context _context;
	
    private static final String TAG = "MyGLRenderer";

    private Cube mCube;
     
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];
    public static float mfAngleX;
    public static float mfAngleY;
    public static float gesDistance;
  //TODO
    /** Store the accumulated rotation. */
    private final float[] mAccumulatedRotation = new float[16];
     
    /** Store the current rotation. */
    private final float[] mCurrentRotation = new float[16];
    private final float[] mTemporaryMatrix = new float[16];
    public static float mDeltaX;
    public static float mDeltaY;
    public static float scale = 1.0f;
   // 

    public MyGLRenderer(Context context) {
		_context = context;
    }
    
    // Declare as volatile because we are updating it from another thread
    public volatile float mAngle;    

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
    	GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		//TODO
				Matrix.setIdentityM(mAccumulatedRotation, 0);
        mCube   = new Cube(_context);
    }

    public void onDrawFrame(GL10 unused) {
    	GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);			        
        
        // Draw background color
       // GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
    	Matrix.setLookAtM(mVMatrix, 0, 0.0f, 3.0f*scale, 4.0f*scale, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);

       
        //long time = SystemClock.uptimeMillis() % 4000L;
       
        if(OpenGLES20Complete.speed>1.0){
        	//OpenGLES20Complete.speed = 0f;
        	//OpenGLES20Complete.speed = OpenGLES20Complete.speed*0.995f;
        	 mAngle  += OpenGLES20Complete.speed*0.5f;
        	 //OpenGLES20Complete.speed += (-0.01f);
        	 Matrix.setRotateM(mRotationMatrix, 0, mAngle, OpenGLES20Complete.rotatex, OpenGLES20Complete.rotatey, OpenGLES20Complete.rotatez);           
             
        }
        else{
        	OpenGLES20Complete.speed = 0.0f;
        	Matrix.setRotateM(mRotationMatrix, 0, 0, 1.0f, 0f, 0f);
        	//randomly decide the final face
        	/*switch(OpenGLES20Complete.face)
        	{
        	case 0:Matrix.setRotateM(mRotationMatrix, 0, 0, 1.0f, 0f, 0f);
            break;
            case 1:Matrix.setRotateM(mRotationMatrix, 0, 90, 1.0f, 0f, 0f);
            break;
            case 2:Matrix.setRotateM(mRotationMatrix, 0, 180, 1.0f, 0f, 0f);
            break;
            case 3:Matrix.setRotateM(mRotationMatrix, 0, 270, 1.0f, 0f, 0f);
            break;
            case 4:Matrix.setRotateM(mRotationMatrix, 0, 90, 0f, 0f, 1.0f);
            break;
            case 5:Matrix.setRotateM(mRotationMatrix, 0, 180, 0f, 0f, 1.0f);
            break;
        	}*/
            
        }
         //Matrix.setRotateM(mRotationMatrix, 0, mAngle, 1f, 1f, 0.5f);
        /*Matrix.setRotateM(mRotationMatrix, 0, mAngle, OpenGLES20Complete.rotatex, OpenGLES20Complete.rotatey, OpenGLES20Complete.rotatez);           
        
        float[] duplicateMatrix = Arrays.copyOf(mMVPMatrix, 16);
        // Combine the rotation matrix with the projection and camera view
        Matrix.multiplyMM(mMVPMatrix, 0, duplicateMatrix, 0, mRotationMatrix, 0);*/

        // Draw triangle
     // Set a matrix that contains the current rotation.
        Matrix.setIdentityM(mCurrentRotation, 0);
        Matrix.rotateM(mCurrentRotation, 0, mDeltaX, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mCurrentRotation, 0, mDeltaY, 1.0f, 0.0f, 0.0f);
        mDeltaX = 0.0f;
        mDeltaY = 0.0f;
         
        // Multiply the current rotation by the accumulated rotation, and then set the accumulated
        // rotation to the result.
        Matrix.multiplyMM(mTemporaryMatrix, 0, mCurrentRotation, 0, mAccumulatedRotation, 0);
        System.arraycopy(mTemporaryMatrix, 0, mAccumulatedRotation, 0, 16);
         
        // Rotate the cube taking the overall rotation into account.
        Matrix.multiplyMM(mTemporaryMatrix, 0, mMVPMatrix, 0, mAccumulatedRotation, 0);
        System.arraycopy(mTemporaryMatrix, 0, mMVPMatrix, 0, 16);
        mCube.draw(mMVPMatrix);
        //mTriangle.draw(mMVPMatrix);
    }

    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

    }

    
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    
    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
}

class Cube {
	private android.graphics.Matrix matrix = new android.graphics.Matrix();
	private final String vertexShaderCode =
	        // This matrix member variable provides a hook to manipulate
	        // the coordinates of the objects that use this vertex shader
	        "uniform mat4 uMVPMatrix;" +

	        "attribute vec4 vPosition;" +
	        "attribute vec4 a_color;" +
	        "attribute vec2 tCoordinate;" +
	        "varying vec2 v_tCoordinate;" +
	        "varying vec4 v_Color;" +
	        "void main() {" +
	        // the matrix must be included as a modifier of gl_Position
	        "  gl_Position = uMVPMatrix*vPosition;" +
	        "	v_tCoordinate = tCoordinate;" +
	        "	v_Color = a_color;" +
	        "}";

	    private final String fragmentShaderCode =
	            "precision mediump float;" +
	            "varying vec4 v_Color;" +
	            "varying vec2 v_tCoordinate;" +
	            "uniform sampler2D s_texture;" +
	            "void main() {" +
	            // texture2D() is a build-in function to fetch from the texture map
	            "	vec4 texColor = texture2D(s_texture, v_tCoordinate); " + 
	            "  gl_FragColor = texColor;" +
	            "}";

    private final FloatBuffer vertexBuffer, texCoordBuffer;
    private final ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle, mTexCoordHandle;
    private int mColorHandle, mTextureUniformHandle;
    private int mMVPMatrixHandle;
    private int mTextureDataHandle;
    private static float a = (float)RecogActivity.ratio[0];
    private static float b = (float)RecogActivity.ratio[2];
    private static float c = (float)RecogActivity.ratio[1];
    
    
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float squareCoords[] = {
    	//front face
    	-0.5f*a,  0.5f*b, 0.5f*c,                                      
    	-0.5f*a, -0.5f*b, 0.5f*c,   
        0.5f*a, -0.5f*b, 0.5f*c,   
        0.5f*a,  0.5f*b, 0.5f*c,
         
        //back face
        0.5f*a,  0.5f*b, -0.5f*c,                                      
    	0.5f*a, -0.5f*b, -0.5f*c,   
        -0.5f*a, -0.5f*b, -0.5f*c,   
        -0.5f*a,  0.5f*b, -0.5f*c,

        //top face
        -0.5f*a,  0.5f*b, -0.5f*c,                                      
    	-0.5f*a, 0.5f*b, 0.5f*c,   
        0.5f*a, 0.5f*b, 0.5f*c,   
        0.5f*a,  0.5f*b, -0.5f*c,
 
        //bottom face
        0.5f*a,  -0.5f*b, -0.5f*c,                                      
    	0.5f*a, -0.5f*b, 0.5f*c,   
        -0.5f*a, -0.5f*b, 0.5f*c,   
        -0.5f*a,  -0.5f*b, -0.5f*c,
               
        //left face
        -0.5f*a,  0.5f*b, -0.5f*c,                                      
    	-0.5f*a, -0.5f*b, -0.5f*c,   
        -0.5f*a, -0.5f*b, 0.5f*c,   
        -0.5f*a,  0.5f*b, 0.5f*c,
        
        //right face
        0.5f*a,  0.5f*b, 0.5f*c,                                      
    	0.5f*a, -0.5f*b, 0.5f*c,   
        0.5f*a, -0.5f*b, -0.5f*c,   
        0.5f*a,  0.5f*b, -0.5f*c     
 
    }; 

    private final short drawOrder[] = { 
    		0, 1, 2, 0, 2, 3,//front
    		4, 5, 6, 4, 6, 7,//back
    		8, 9, 10, 8, 10, 11,//top
    		12, 13, 14, 12, 14, 15,//bottom
    		16, 17, 18, 16, 18, 19,//left
    		20, 21, 22, 20, 22, 23//right
    		
    		
    		
    }; // order to draw vertices

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = {				
    		// Front face (red)
    		                  
    		 1.0f, 1.0f, 1.0f, 1.0f,                
             
	};
    static final int COORDS_PER_TEX = 2;
	private static final String TAG = null;
    static float texCoord[] = {
    	
    	//front face
		/*0.0f, 0.5f, 									
		1.0f, 0.0f,
		1.0f, 0.1667f,
		0.0f, 0.1667f,
	
    	MyView.myPoint_x[0]/ManualActivity.width,MyView.myPoint_y[0]/ManualActivity.height,
    	MyView.myPoint_x[1]/ManualActivity.width,MyView.myPoint_y[1]/ManualActivity.height,
    	MyView.myPoint_x[3]/ManualActivity.width,MyView.myPoint_y[3]/ManualActivity.height,
    	MyView.myPoint_x[2]/ManualActivity.width,MyView.myPoint_y[2]/ManualActivity.height,
    	 */
    	
    	0.3333f, 0.0f,
    	0.3333f, 1.0f,
    	0.0f, 1.0f,
    	0.0f, 0.0f,
		// Right face 
		/*0.0f, 0.1667f, 									
		1.0f, 0.1667f,
		1.0f, 0.3333f,
		0.0f, 0.3333f,	
    	MyView.myPoint_x[0]/ManualActivity.width,MyView.myPoint_y[0]/ManualActivity.height,
    	MyView.myPoint_x[1]/ManualActivity.width,MyView.myPoint_y[1]/ManualActivity.height,
    	MyView.myPoint_x[3]/ManualActivity.width,MyView.myPoint_y[3]/ManualActivity.height,
    	MyView.myPoint_x[2]/ManualActivity.width,MyView.myPoint_y[2]/ManualActivity.height,*/

    	0.3333f, 0.0f,
    	0.3333f, 1.0f,
    	0.0f, 1.0f,
    	0.0f, 0.0f,
		// Back face 
    	1.0f, 0.0f, 
		1.0f, 1.0f,
		0.6666f, 1.0f,
		0.6666f, 0.0f,		
		
		// Left face 
		1.0f, 0.0f, 
		1.0f, 1.0f,
		0.6666f, 1.0f,
		0.6666f, 0.0f,
		
		
		// Top face 
		/*0.0f, 0.6667f, 									
		1.0f, 0.6667f,
		1.0f, 0.8333f,
		0.0f, 0.8333f,	
		MyView.myPoint_x[2]/ManualActivity.width,MyView.myPoint_y[2]/ManualActivity.height,
		MyView.myPoint_x[3]/ManualActivity.width,MyView.myPoint_y[3]/ManualActivity.height,
    	MyView.myPoint_x[5]/ManualActivity.width,MyView.myPoint_y[5]/ManualActivity.height,
    	MyView.myPoint_x[4]/ManualActivity.width,MyView.myPoint_y[4]/ManualActivity.height,	*/

		0.6666f, 0.0f, 
		0.6666f, 1.0f,
		0.3333f, 1.0f,
		0.3333f, 0.0f,
		// Bottom face 
		/*0.0f, 0.8333f, 									
		1.0f, 0.8333f,
		1.0f, 1.0f,
		0.0f, 1.0f,
		
		MyView.myPoint_x[2]/ManualActivity.width,MyView.myPoint_y[2]/ManualActivity.height,
		MyView.myPoint_x[3]/ManualActivity.width,MyView.myPoint_y[3]/ManualActivity.height,
    	MyView.myPoint_x[5]/ManualActivity.width,MyView.myPoint_y[5]/ManualActivity.height,
    	MyView.myPoint_x[4]/ManualActivity.width,MyView.myPoint_y[4]/ManualActivity.height,*/
		0.6666f, 0.0f, 
		0.6666f, 1.0f,
		0.3333f, 1.0f,
		0.3333f, 0.0f,

    };
    
    private final int texCoordStride = COORDS_PER_TEX * 4; // 4 bytes per float

    public Cube(Context context) {
    	//===================================
    	
        
        
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
        
     // texture coordinate
        //===================================
        // initialize texture coord byte buffer for texture coordinates
        ByteBuffer texbb = ByteBuffer.allocateDirect(
        		texCoord.length * 4);
        // use the device hardware's native byte order
        texbb.order(ByteOrder.nativeOrder());
        
        // create a floating point buffer from the ByteBuffer
        texCoordBuffer = texbb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        texCoordBuffer.put(texCoord);
        // set the buffer to read the first coordinate
        texCoordBuffer.position(0);

      //===================================
        // loading an image into texture
        //===================================
        Bitmap mbitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() +
		        "/building1.jpg");		
        if (MainActivity.IMAGE_SELECTION==100){
    		//read in the picture that is just taken
    		mbitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() +
            "/image" + MainActivity.N + ".jpg");		  
    		//matrix.setRotate(90);  
    		//mbitmap = Bitmap.createBitmap(mbitmap, 0, 0, mbitmap.getWidth(),mbitmap.getHeight(), matrix, true);  
    		
    	}
        
    	else{
    		
    		switch(MainActivity.IMAGE_SELECTION){
        	case 0:
        		mbitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() +
        				"/building1.jpg");	
        	    break;   	        	
        	case 1:
        		mbitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() +
        				"/building2.jpg");	
        		break;
        	case 2:
        		mbitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() +
        				"/building3.jpg");	
        		break;
        	case 3:
        		mbitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() +
        				"/testsoap.jpg");	
        		break;
    		}
    		if(MainActivity.IMAGE_SELECTION==200)
    			mbitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath() +"/image" + MainActivity.N + ".jpg");
    	}
    		
        Log.d(TAG, "read in texture");
       	
        
        mTextureDataHandle = loadTexture(context, mbitmap);
        
        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                                                   vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                                                     fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
    }
    
    public static int loadTexture(final Context context, final Bitmap image)
    {
        final int[] textureHandle = new int[1];
		ArrayList<Point2D> ps = RecogActivity.points;
     
        GLES20.glGenTextures(1, textureHandle, 0);
        
        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling
            int resultWidth = 500;
            int resultHeight = 500;

            Mat inputMat = new Mat(image.getHeight(), image.getHeight(), CvType.CV_8UC4);
            Utils.bitmapToMat(image, inputMat);
            Mat outputMat = new Mat(resultWidth, resultHeight, CvType.CV_8UC4);
            
            Point ocvPIn1 = new Point(MyView.myPoint_x[0], MyView.myPoint_y[0]);
            Point ocvPIn2 = new Point(MyView.myPoint_x[1], MyView.myPoint_y[1]);
            Point ocvPIn3 = new Point(MyView.myPoint_x[3], MyView.myPoint_y[3]);
            Point ocvPIn4 = new Point(MyView.myPoint_x[2], MyView.myPoint_y[2]);
            
            //TODO 3 faces case
            if (MainActivity.IMAGE_SELECTION==100){
            	ocvPIn1 = new Point(MyView.myPoint_x[2], MyView.myPoint_y[2]);
                ocvPIn2 = new Point(MyView.myPoint_x[3], MyView.myPoint_y[3]);
                ocvPIn3 = new Point(MyView.myPoint_x[5], MyView.myPoint_y[5]);
                ocvPIn4 = new Point(MyView.myPoint_x[4], MyView.myPoint_y[4]);
            }
            
			ocvPIn1 = P(ps.get(1));
			ocvPIn2 = P(ps.get(2));
			ocvPIn3 = P(ps.get(3));
			ocvPIn4 = P(ps.get(6));
            
            List<Point> source = new ArrayList<Point>();
            source.add(ocvPIn1);
            source.add(ocvPIn2);
            source.add(ocvPIn3);
            source.add(ocvPIn4);
            Mat startM = Converters.vector_Point2f_to_Mat(source);

            Point ocvPOut1 = new Point(0, 0);
            Point ocvPOut2 = new Point(0, resultHeight);
            Point ocvPOut3 = new Point(resultWidth, resultHeight);
            Point ocvPOut4 = new Point(resultWidth, 0);
            
            
            List<Point> dest = new ArrayList<Point>();
            dest.add(ocvPOut1);
            dest.add(ocvPOut2);
            dest.add(ocvPOut3);
            dest.add(ocvPOut4);
            Mat endM = Converters.vector_Point2f_to_Mat(dest);      

            //Mat perspectiveTransform = new Mat(3, 3, CvType.CV_32FC1);
            //Core.perspectiveTransform(startM, endM, perspectiveTransform);
            Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);
            Imgproc.warpPerspective(inputMat, 
                                    outputMat,
                                    perspectiveTransform,
                                    new Size(resultWidth, resultHeight), 
                                    Imgproc.INTER_CUBIC);
            
            Mat inputMat2 = new Mat(image.getHeight(), image.getHeight(), CvType.CV_8UC4);
            Utils.bitmapToMat(image, inputMat2);
            Mat outputMat2 = new Mat(resultWidth, resultHeight, CvType.CV_8UC4);
            
            Point ocvPIn12 = new Point(MyView.myPoint_x[2], MyView.myPoint_y[2]);
            Point ocvPIn22 = new Point(MyView.myPoint_x[3], MyView.myPoint_y[3]);
            Point ocvPIn32 = new Point(MyView.myPoint_x[5], MyView.myPoint_y[5]);
            Point ocvPIn42 = new Point(MyView.myPoint_x[4], MyView.myPoint_y[4]);
            
            
            //TODO read in case of three faces*******
            if (MainActivity.IMAGE_SELECTION==100){
            	ocvPIn12 = new Point(MyView.myPoint_x[2], MyView.myPoint_y[2]);
                ocvPIn22 = new Point(MyView.myPoint_x[3], MyView.myPoint_y[3]);
                ocvPIn32 = new Point(MyView.myPoint_x[5], MyView.myPoint_y[5]);
                ocvPIn42 = new Point(MyView.myPoint_x[4], MyView.myPoint_y[4]);
            }
            
			ocvPIn12 = P(ps.get(6));
			ocvPIn22 = P(ps.get(3));
			ocvPIn32 = P(ps.get(4));
			ocvPIn42 = P(ps.get(5));
            
            List<Point> source2 = new ArrayList<Point>();
            source2.add(ocvPIn12);
            source2.add(ocvPIn22);
            source2.add(ocvPIn32);
            source2.add(ocvPIn42);
            Mat startM2 = Converters.vector_Point2f_to_Mat(source2);

            Point ocvPOut12 = new Point(0, 0);
            Point ocvPOut22 = new Point(0, resultHeight);
            Point ocvPOut32 = new Point(resultWidth, resultHeight);
            Point ocvPOut42 = new Point(resultWidth, 0);
            List<Point> dest2 = new ArrayList<Point>();
            dest2.add(ocvPOut12);
            dest2.add(ocvPOut22);
            dest2.add(ocvPOut32);
            dest2.add(ocvPOut42);
            Mat endM2 = Converters.vector_Point2f_to_Mat(dest2);      

            //Mat perspectiveTransform = new Mat(3, 3, CvType.CV_32FC1);
            //Core.perspectiveTransform(startM, endM, perspectiveTransform);
            Mat perspectiveTransform2 = Imgproc.getPerspectiveTransform(startM2, endM2);
            Imgproc.warpPerspective(inputMat2, 
                                    outputMat2,
                                    perspectiveTransform2,
                                    new Size(resultWidth, resultHeight), 
                                    Imgproc.INTER_CUBIC);
            
            //top face transformation
            Mat inputMat3 = new Mat(image.getHeight(), image.getHeight(), CvType.CV_8UC4);
            Utils.bitmapToMat(image, inputMat3);
            Mat outputMat3 = new Mat(resultWidth, resultHeight, CvType.CV_8UC4);
            
            Point ocvPIn13 = new Point(0, 0);
            Point ocvPIn23 = new Point(0, 0.1);
            Point ocvPIn33 = new Point(0.1, 0.1);
            Point ocvPIn43 = new Point(0.1, 0);
            
            
            //TODO read in case of three faces*******
            if (MainActivity.IMAGE_SELECTION==100){
            	ocvPIn13 = new Point(MyView.myPoint_x[2], MyView.myPoint_y[2]);
                ocvPIn23 = new Point(MyView.myPoint_x[3], MyView.myPoint_y[3]);
                ocvPIn33 = new Point(MyView.myPoint_x[5], MyView.myPoint_y[5]);
                ocvPIn43 = new Point(MyView.myPoint_x[4], MyView.myPoint_y[4]);
            }

			ocvPIn13 = P(ps.get(0));
			ocvPIn23 = P(ps.get(1));
			ocvPIn33 = P(ps.get(6));
			ocvPIn43 = P(ps.get(5));
            	
            List<Point> source3 = new ArrayList<Point>();
            source3.add(ocvPIn13);
            source3.add(ocvPIn23);
            source3.add(ocvPIn33);
            source3.add(ocvPIn43);
            Mat startM3 = Converters.vector_Point2f_to_Mat(source3);

            Point ocvPOut13 = new Point(0, 0);
            Point ocvPOut23 = new Point(0, resultHeight);
            Point ocvPOut33 = new Point(resultWidth, resultHeight);
            Point ocvPOut43 = new Point(resultWidth, 0);
            List<Point> dest3 = new ArrayList<Point>();
            dest3.add(ocvPOut13);
            dest3.add(ocvPOut23);
            dest3.add(ocvPOut33);
            dest3.add(ocvPOut43);
            Mat endM3 = Converters.vector_Point2f_to_Mat(dest3);      

            //Mat perspectiveTransform = new Mat(3, 3, CvType.CV_32FC1);
            //Core.perspectiveTransform(startM, endM, perspectiveTransform);
            Mat perspectiveTransform3 = Imgproc.getPerspectiveTransform(startM3, endM3);
            Imgproc.warpPerspective(inputMat3, 
                                    outputMat3,
                                    perspectiveTransform3,
                                    new Size(resultWidth, resultHeight), 
                                    Imgproc.INTER_CUBIC);

            Mat combine = new Mat(resultWidth, resultHeight*3, CvType.CV_8UC4);
            outputMat.colRange(0, resultWidth-1).copyTo(combine.colRange(0, resultWidth-1));// = endM.col(i);
            outputMat2.colRange(0, resultWidth-1).copyTo(combine.colRange(resultWidth, 2*resultWidth-1));
            
            outputMat3.colRange(0, resultWidth-1).copyTo(combine.colRange(2*resultWidth, 3*resultWidth-1));
            //}
            
            Bitmap output = Bitmap.createBitmap(resultWidth*3, resultHeight, Bitmap.Config.RGB_565);
            Utils.matToBitmap(combine, output);
            
        	//Highgui.imwrite(Environment.getExternalStorageDirectory().getPath() +"/image_________.jpg",combine);
            
            
            /////////////*****************/////////////////////
            // Read in the resource
            //final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), bitmap, options);
     
            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
     
            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
     
            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, output, 0);
     
            // Recycle the bitmap, since its data has been loaded into OpenGL.
            output.recycle();
            image.recycle();
        }
     
        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }
     
        return textureHandle[0];
    }
    
    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        //vertexBuffer.position(12*1);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                                     GLES20.GL_FLOAT, false,
                                     vertexStride, vertexBuffer);
        //GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
        //        GLES20.GL_FLOAT, false,
        //        0, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // setting texture coordinate to vertex shader
        
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "tCoordinate");
        //texCoordBuffer.position(8*1);
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);
        GLES20.glVertexAttribPointer(mTexCoordHandle, COORDS_PER_TEX,
               					GLES20.GL_FLOAT, false,
               					texCoordStride, texCoordBuffer);   
        //GLES20.glVertexAttribPointer(mTexCoordHandle, COORDS_PER_TEX,
		//		GLES20.GL_FLOAT, false,
		//		texCoordStride, texCoordBuffer);   
        MyGLRenderer.checkGlError("glVertexAttribPointer...texCoord");
        
        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

     // texture
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "s_texture");
        
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        
        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);        
        
        
        // Draw the square
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                              GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

	public static Point P(Point2D x) {
		return new Point(x.x(), x.y());
	}
}
