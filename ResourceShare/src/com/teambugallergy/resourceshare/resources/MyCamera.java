package com.teambugallergy.resourceshare.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.teambugallergy.resourceshare.constants.Resources;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;

/**
 * This class has methods that allows to use the Camera Resource in the device.
 * The methodologies provide querring for availability (available, unavailable and busy) of the resource,
 * accessing the resource (switching on/off, giving data to OR taking the data from the resource) and other Resource Specific 
 * functionalities, if any.
 * @author Adiga@TeamBugAllergy
 * 05-05-2014
 */
public class MyCamera {

	/**
	 * Availability status returned by availability() method.
	 RESOURCE_AVAILABLE = 701;
	 RESOURCE_UNAVAILABLE = 702;
	 RESOURCE_BUSY = 703; 
	 */
	
	/**
	 * Save the availability of flash.
	 */
	private int availability = 0;
	
	/**
	 * Context of the caller.
	 */
	private Context callerContext;
	
	/**
	 * Handler to which the message has to be sent.
	 * After the clicked image is saved, a message will be sent to this handler to notify about it.
	 */
	private Handler callerHandler;
	
	/**
	 * Camera object.
	 */
	private Camera camera;
	
	/**
	 * PictureCallback to process the picture after the camera has clicked an image. 
	 */
	private Camera.PictureCallback jpegCallBack;
	
	/**
	 * To save each of the images, append the name TeamBugAllergy with this number. 
	 * It is incremented each time a new file is saved.
	 */
	private int num = 0;
	
	/**
	 * Checks the availabilty of the camera and stores the result in 'availability'.
	 * @param context Context of the caller Activity.
	 * @param handler Handler of the caller Activity which receives IMAGE_SAVED message.
	 */
	public MyCamera(Context context, Handler handler)
	{
		//set the context of the caller activity
		callerContext = context;
		
		//caller handler to get image_saved message
		callerHandler = handler;
		//TODO: Remember, the caller Activity should handle the IMAGE_SAVED message.
		
		//check the availability
		if( callerContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) )
		{
			try
	    	{
	    		//obtain a camera object 
	    		camera = Camera.open();
	    		
	    	}catch(Exception e)
	    	{
	    		//Device has Flash, but it is currently busy (BUSY).
	    		availability = Resources.RESOURCE_BUSY;
	    	}
			//release the camera
	    	camera.release();
	    	
         	//Device has Flash and it is free now (AVAILABLE).
	    	availability = Resources.RESOURCE_AVAILABLE;
			
		}
		else
		{
			//Device doesn't have flash (UNAVAILABLE).
	    	availability = Resources.RESOURCE_UNAVAILABLE;
		}
			
	}
	
	/**
	 * Return the availability of the Camera.
	 * @return
	 */
	public int availability()
	{	
		//return the availability of the Camera
		return availability;				
	}
	
	/**
	 * This method is used to set the camera Parameters.
	 * To avoid all Errors that can happen when using a camera.
	 * This method needn't be called explicitly. acquireCamera() will call this method.
	 * IMPORTANT: This method is specifically written for the Camera App for the Resource Share 
	 * by Team BugAllergy
	 */
	private void setCameraDefaults()
	{
		//get the original parameters
	    Camera.Parameters params = camera.getParameters();

	    // Supported picture formats (all devices should support JPEG).
	    List<Integer> formats = params.getSupportedPictureFormats();

	    if (formats.contains(ImageFormat.JPEG))
	    {
	        params.setPictureFormat(ImageFormat.JPEG);
	        params.setJpegQuality(100);
	    }
	    else
	        params.setPictureFormat(PixelFormat.RGB_565);

	    // Now the supported picture sizes. 
	    List<Size> sizes = params.getSupportedPictureSizes();
	    Camera.Size size = sizes.get(sizes.size()-1);
	    params.setPictureSize(size.width, size.height);

	    // Set the brightness to auto.
	    params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);

	    // Set the flash mode to auto.
	    params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);

	    // Set the scene mode to portrait.
	    params.setSceneMode(Camera.Parameters.SCENE_MODE_PORTRAIT);

	    // Lastly set the focus to auto.
	    params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

	    //set the updated parameters to the camera
	    camera.setParameters(params);
	    
	    //TODO:Check:- Flash enabled only for first click
	    //TODO:Check:- Rotated image is displayed. 
	}
	
	/**
	 * Tries to acquire the camera.
	 * Sends the result back to caller.
	 * @return result of acquire (true/false)
	 */
	public Boolean acquireCamera()
	{
		//create a surface view object
		SurfaceView view = new SurfaceView(callerContext);
		
		//set the camera parameters
		setCameraDefaults();
		
		try {
			//obtain a camera object 
    		camera = Camera.open();
    		
    		// feed dummy surface to surface
			camera.setPreviewDisplay(view.getHolder());
			
			//start the preview
			camera.startPreview();
			
		} 
		catch (IOException e)
		{ 
			LogMsg("ERROR: In acquiring the camera- " + e);
			
			//return the fail result
			return false;
		}
		
		//return the success result
		return true;
	}
	
	
	/**
	 * This function is to click a Picture. It sets callback handler for processing of clicked image.
	 * You have to call the OpenCamera() before calling this method
	 * The Picture that is captured is stored in the directory defined by getExternalStorageDirectory
	 * with the name Teambugallergy.jpg
	 * 
	 * @return	<i>true</i> For successfully captured and stored. <i>false</i> Failure.
	 */
	public Boolean takePicture(){
			
		jpegCallBack=new Camera.PictureCallback() {		
			
			//CallBack method to process the taken picture.
			public void onPictureTaken(byte[] data, Camera camera) {
				
				// set file destination and file name
				File destination  =new File(Environment.getExternalStorageDirectory(),"Teambugallergy"+ (num++) +".jpg");
				
				//try to save the image file
				try {
					//decode the data into image file
					Bitmap userImage = BitmapFactory.decodeByteArray(data, 0, data.length);
					
					// set file out stream
					FileOutputStream out = new FileOutputStream(destination);
					
					// set compress format quality and stream
					userImage.compress(Bitmap.CompressFormat.JPEG, 90, out);		
					
					LogMsg("Saved the image");
					
					//notify tha UI thread that image has been saved and is ready to be displayed
					callerHandler.obtainMessage(Resources.IMAGE_SAVED).sendToTarget();
					
					LogMsg("notifying the UIThread by sending IMAGE_SAVED message.");
					
					//also recycle the userImage
					if(userImage != null)
					{
						userImage.recycle();
						userImage = null;
					}
					
					//request the VM to garbage collect.
					System.gc();
					
				} catch (FileNotFoundException e) {

					LogMsg("ERROR: In saving the image- " + e);
				}
	 		}
		
		};
		
		
		try
		{
			//try to take the picture.
			//specify the callback method to be called after taking the picture
			camera.takePicture(null,null,jpegCallBack );
		}
		catch(Exception e){
			return false;
			
		}
		return true;
	}
	
	/**
	 * Method to stop the camera preview and release the acquired camera object.
	 */
	public void releaseCamera()
	{
	    if (camera != null) {
	    	camera.stopPreview();
	    	camera.release();
	    	camera = null;
	    }
	}
	
	private static void LogMsg(String msg) {
		Log.d("Flash", msg);
	}
}
