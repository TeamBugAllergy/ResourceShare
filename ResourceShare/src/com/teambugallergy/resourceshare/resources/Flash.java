package com.teambugallergy.resourceshare.resources;

import com.teambugallergy.resourceshare.constants.Resources;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;

/**
 * This class has methods that help to use the Flash Resource in the device.
 * The methodologies provide querring for availability (available, unavailable and busy) of the resource,
 * accessing the resource (switching on/off, giving data to OR taking the data from the resource) and other Resource Specific 
 * functionalities, if any.
 * @author Adiga
 * 27-04-2014
 */
public class Flash{
	
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
	
	public Flash(Context context)
	{
		//save the context.
		this.callerContext = context;
		
		//create a camera object
		if (callerContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
	    	try
	    	{
	    		camera = Camera.open();
	
	    	}catch(Exception e)
	    	{
	    		//Device has Flash, but it is currently busy (BUSY).
	    		availability = Resources.RESOURCE_BUSY;
	    	}
	    	
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
	
}
