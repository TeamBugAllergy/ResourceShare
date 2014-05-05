package com.teambugallergy.resourceshare.resources;

import com.teambugallergy.resourceshare.constants.Resources;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;

/**
 * This class has methods that help to use the Flash Resource in the device.
 * The methodologies provide querring for availability (available, unavailable and busy) of the resource,
 * accessing the resource (switching on/off, giving data to OR taking the data from the resource) and other Resource Specific 
 * functionalities, if any.
 * @author Adiga
 * 27-04-2014
 */
public class MyFlash{
	
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
	 * Camera object used to check the availability of the flash.
	 */
	private Camera camera;
	
	/**
	 * Parameter FLASH_MODE_TORCH / FLASH_MODE_OFF.
	 */
	private Parameters parameter_flash_mode;

	public MyFlash(Context context)
	{
		//save the context.
		this.callerContext = context;
		
		//create a camera object
		if (callerContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
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
	 * Check the availability of the Flash and return the result.
	 * @return
	 */
	public int availability()
	{	
		//return the availability of the Flash
		return availability;				
	}
	
	/**
	 * Tries to acquire the camera and there by flash.
	 * Sends the result back to caller.
	 * @return result of acquire (true/false)
	 */
	public Boolean acquireFlash()
	{
		//get a camera object
		try {
			camera = Camera.open();
		} catch (Exception e) {

			LogMsg("Error: Unable to open the camera object- " + e);
			return false;
		}
		
		//get the camera parameter
		if (camera != null) {
			
			//get the initial parameters
			parameter_flash_mode = camera.getParameters();
			
		}
		else
		{
			LogMsg("'camera' object is null");
			return false;
		}
		
		//Acquiring the flash was successful
		return true;
		
	}
	
	/**
	 * Swtichs on the flash.
	 */
	public void switchOnFlash()
	{
		if(camera != null)
		{
			try {
				
				//set the mode to torch
				parameter_flash_mode.setFlashMode(Parameters.FLASH_MODE_TORCH);
				camera.setParameters(parameter_flash_mode);
				
				//Optional camera.lock();
				camera.startPreview();
				LogMsg("Started camera preview, i.e flash is switched on");
				 
			} catch (Exception e) {
				LogMsg("Error: In starting camera preview- " + e);
			}
		}
	}
	
	/**
	 * Swtichs off the flash.
	 */
	public void switchOffFlash()
	{
		if(camera != null)
		{
			try {
				
				//set the mode to off
				parameter_flash_mode.setFlashMode(Parameters.FLASH_MODE_OFF);
				camera.setParameters(parameter_flash_mode);
								
				//Optional camera.unlock();
				camera.stopPreview();
				LogMsg("Stoped the camera preivew, i.e flash is switched off");
				
			} catch (Exception e) {
				LogMsg("Error: In stopping camera preview- " + e);
			}
		}
	}
	
	/**
	 * Releases the acquired Flash.
	 */
	public void releaseFlash()
	{
		if(camera != null)
		{
			try {
				camera.release();
				camera = null;
				
				LogMsg("'camera' has been released");
			} catch (Exception e) {
				LogMsg("Error: In releasing the camera- " + e);
			}
		}
	}
	
	private static void LogMsg(String msg) {
		Log.d("Flash", msg);
	}
}
