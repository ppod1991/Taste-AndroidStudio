package com.example.taste;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.facebook.*;
import com.facebook.model.*;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.pod.taste.R;
import android.widget.TextView;
import android.content.Intent;

public class CameraActivity extends Activity implements Camera.AutoFocusCallback{

    private Camera mCamera;
    private CameraPreview mPreview;
    private FrameLayout preview;
    private ImageButton captureButton;
    private User user;
    private ProgressBar progressBar;
    private boolean flashOn = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	    Tracker t = ((Global) this.getApplication()).getTracker();
	    t.setScreenName("Camera Screen View");
	    t.send(new HitBuilders.AppViewBuilder().build());
	    
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_camera);

		 
		user = (User)getIntent().getSerializableExtra("user");
		
        // Create an instance of Camera
		//mCamera = null;
        mCamera = getCameraInstance();
//        Camera.Parameters parameters = mCamera.getParameters();
//        parameters.setRotation(270);
//        mCamera.setParameters(parameters);
        
        
        //setCameraDisplayOrientation(this,0, mCamera);
        
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        
        progressBar = (ProgressBar) ((CameraActivity.this).findViewById(R.id.progress_bar_camera));
        progressBar.bringToFront();
     // Add a listener to the Capture button
        captureButton = (ImageButton) findViewById(R.id.button_capture);
    	captureButton.bringToFront();
    	captureButton.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
                if(event.getAction()==MotionEvent.ACTION_DOWN)
                    ((ImageView)v.findViewById(R.id.button_capture)).setImageResource(R.drawable.camera_button_pushed);
                else if(event.getAction()==MotionEvent.ACTION_UP)
                    ((ImageView)v.findViewById(R.id.button_capture)).setImageResource(R.drawable.camera_button);
                return false;

			}
		});
    	
        captureButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Camera.Parameters p = mCamera.getParameters();
                    if (flashOn) {
	                    p.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
	                    Tracker t = ((Global) CameraActivity.this.getApplication()).getTracker();
	        			t.send(new HitBuilders.EventBuilder().setCategory("Progress").setAction("Button Click").setLabel("Picture with Flash Taken").build());
	        			
                    }
                    else {
                    	p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    	Tracker t = ((Global) CameraActivity.this.getApplication()).getTracker();
            			t.send(new HitBuilders.EventBuilder().setCategory("Progress").setAction("Button Click").setLabel("Picture with No Flash Taken").build());
            			
                    }
                    mCamera.setParameters(p);
                    
                    // get an image from the camera
                	if (mCamera != null){
          			  
          			  	progressBar.setVisibility(View.VISIBLE);
          			  	captureButton.setEnabled(false);
                		mCamera.autoFocus(CameraActivity.this);
                	}
                		
                    
                }
            }
        );
        
        ImageView flashButton = (ImageView) findViewById(R.id.flash_status);
    	flashButton.bringToFront();
    	flashButton.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				Log.e("Touched", "touched!");
                	if (flashOn) {
                		((ImageView)v.findViewById(R.id.flash_status)).setImageResource(R.drawable.no_flash_icon);
                		flashOn = false;
                	}
                	else {
                		((ImageView)v.findViewById(R.id.flash_status)).setImageResource(R.drawable.flash_icon);
                		flashOn = true;
                	}
                		
                	
                return false;

			}
		});
    	
    }

    protected void onPause() {
        super.onPause();

        if (mCamera != null) {
        	mCamera.stopPreview();
        	mPreview.removeCallback();
        	preview.removeView(mPreview);
        	mPreview = null;
        	mCamera.release();
        	mCamera = null;
        	
        }
    }
    
    protected void onResume() {
        super.onResume();

        captureButton.setEnabled(true);
        progressBar.setVisibility(View.INVISIBLE);
        

    	
        if (mCamera == null) {
        	mCamera = getCameraInstance();

//            Camera.Parameters parameters = mCamera.getParameters();
//            parameters.setRotation(270);
//            mCamera.setParameters(parameters);
            
        	mPreview = new CameraPreview(this, mCamera);
            preview.addView(mPreview);
            captureButton.bringToFront(); 
            ImageView flashButton = (ImageView) findViewById(R.id.flash_status);
            flashButton.bringToFront();
        }
    }    

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
        c.setDisplayOrientation(90);
       
        Camera.Parameters p = c.getParameters();
        p.setSceneMode(Camera.Parameters.SCENE_MODE_PARTY);
        p.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
        p.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
        
        List<Camera.Size> sizes = p.getSupportedPictureSizes();
        Camera.Size maxSize = sizes.get(0);
        for (int i=1;i<sizes.size();i++){
        	Camera.Size cur = sizes.get(i);
            Log.i("PictureSize", "Supported Size: " + cur.width + " height : " + cur.height);
            if (cur.height >  (maxSize.height)) {
            	maxSize = cur;
            }
        }
        Log.i("Selected picture size:", "width: " + maxSize.width + " and height: " + maxSize.height);
        p.setPictureSize(maxSize.width, maxSize.height);    
        c.setParameters(p);
	    return c; // returns null if camera is unavailable
	}

	private PictureCallback mPicture = new PictureCallback() {

	    @Override
	    public void onPictureTaken(byte[] data, Camera camera) {
		    
//	        if (camera != null) {
//	            camera.stopPreview();
//	            camera.release();
//	            camera = null;
//	        }
	        Log.e("Size of byte[]:", data.length + "");
	        Intent intent = new Intent(CameraActivity.this, EditImageActivity.class);
	        final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inSampleSize = 2;
	        options.inMutable = true;
	        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length,options);
	        Log.e("Image width:", bmp.getWidth() + "");
	        Log.e("Image height:", bmp.getHeight() + "");
	        if (bmp.getWidth() > bmp.getHeight()) {
	            Matrix matrix = new Matrix();
	            matrix.postRotate(90);
	            bmp = Bitmap.createBitmap(bmp , 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
	        }
	        
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    	
	    	bmp.compress(Bitmap.CompressFormat.JPEG,50, baos);
	    	bmp.recycle();
	    	intent.putExtra("user", user);
	        intent.putExtra("Image", baos.toByteArray());
	        try {
				baos.close();
				baos = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	        startActivity(intent);
	    	
	    	
	    	
	    }
	};
	
	
	

	public static void setCameraDisplayOrientation(Activity activity,
	        int cameraId, android.hardware.Camera camera) {
	    android.hardware.Camera.CameraInfo info =
	            new android.hardware.Camera.CameraInfo();
	    android.hardware.Camera.getCameraInfo(cameraId, info);
	    int rotation = activity.getWindowManager().getDefaultDisplay()
	            .getRotation();
	    int degrees = 0;
	    switch (rotation) {
	        case Surface.ROTATION_0: degrees = 0; break;
	        case Surface.ROTATION_90: degrees = 90; break;
	        case Surface.ROTATION_180: degrees = 180; break;
	        case Surface.ROTATION_270: degrees = 270; break;
	    }
	
	    int result;
	    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	        result = (info.orientation + degrees) % 360;
	        result = (360 - result) % 360;  // compensate the mirror
	    } else {  // back-facing
	        result = (info.orientation - degrees + 360) % 360;
	    }
	    camera.setDisplayOrientation(result);
	}

	@Override
	public void onAutoFocus(boolean success, Camera camera) {
//		if (success)
			camera.takePicture(null, null, mPicture);
//		else {
//			int duration = Toast.LENGTH_SHORT;
//	    	CharSequence text = "Could not Autofocus--try again!";
//	    	Context context = getApplicationContext();
//			Toast toast = Toast.makeText(context, text, duration);
//			toast.show();
//		}
	}	
	
}