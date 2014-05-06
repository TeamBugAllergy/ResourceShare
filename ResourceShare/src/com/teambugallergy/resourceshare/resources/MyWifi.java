package com.teambugallergy.resourceshare.resources;

import com.teambugallergy.resourceshare.constants.Resources;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * This class has methods that allows to use the Wifi Resource in the device.
 * The methodologies provide querring for availability (available, unavailable and busy) of the resource,
 * accessing the resource (switching on/off, giving data to OR taking the data from the resource) and other Resource Specific 
 * functionalities, if any.
 * @author Adiga@TeamBugAllergy
 * 27-04-2014
 */
public class MyWifi{

	/**
	 * Availability status returned by availability() method.
	 RESOURCE_AVAILABLE = 701;
	 RESOURCE_UNAVAILABLE = 702;
	 RESOURCE_BUSY = 703; 
	 */
	
	/**
	 * Save the availability of Wifi.
	 */
	private int availability = 0;
	
	/**
	 * Context of the caller.
	 */
	private Context callerContext;
	
	/**
	 * Reference to wifi unit.
	 */
	private WifiManager wifimanager;
	
	/**
	 * Checks the availabilty of the wifi and stores the result in 'availability'.
	 * @param context Context of the caller Activity.
	 */
	public MyWifi(Context context) {
		
		//save the context.
		this.callerContext = context;

		//create a camera object
		if (callerContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI)){
			
			try {
				//try to obtain a reference
				wifimanager = (WifiManager) callerContext.getSystemService(Context.WIFI_SERVICE);
				
			} catch (Exception e) {
				
				//Device has wifi, but it is currently busy (BUSY).
	    		availability = Resources.RESOURCE_BUSY;
			}
		
			//Device has Flash and it is free now (AVAILABLE).
	    	availability = Resources.RESOURCE_AVAILABLE;
		}
		else
		{
			 //Device doesn't have wifi (UNAVAILABLE).
	    	availability = Resources.RESOURCE_UNAVAILABLE;
		}
	}
	
	/**
	 * Return the availability of the Flash.
	 * @return
	 */
	public int availability()
	{	
		//return the availability of the Flash
		return availability;				
	}
	
	/**
	 * Tries to acquire the wifi reference.
	 * Sends the result back to caller.
	 * @return result of acquire (true/false)
	 */
	public Boolean acquireWifi()
	{
		//get a refernce
		wifimanager = null;
		wifimanager = (WifiManager)callerContext.getSystemService(Context.WIFI_SERVICE);
		
		if(wifimanager == null)
			//failed to get a refernce
			return false;
		
		//successfull
		return true;
	}
	
	/**
	 * Swtichs on the wifi.
	 */
	public void switchOnWifi()
	{
		if(wifimanager != null)
		{
			//Only if currently wifi is disabled
			if( !wifimanager.isWifiEnabled() )
			{
				
				wifimanager.setWifiEnabled(true);
				LogMsg("Switched on the wifi");
				 
			}
			else
			{
				LogMsg("Wifi is already switched on");
			}
		}
	}
	
	/**
	 * Swtichs off the wifi.
	 */
	public void switchOffWifi()
	{
		if(wifimanager != null)
		{
			//Only if currently wifi is enabled
			if( wifimanager.isWifiEnabled() )
			{
				
				wifimanager.setWifiEnabled(false);
				LogMsg("Switched off the wifi");
				 
			}
			else
			{
				LogMsg("Wifi is already switched off");
			}
		}
	}
	
	/**
	 * Releases the acquired Wifi.
	 * If wifi is currently switched on, switches it off.
	 */
	public void releaseWifi()
	{
		if(wifimanager != null)
		{
			//Only if currently wifi is enabled
			if( wifimanager.isWifiEnabled() )
			{
				
				wifimanager.setWifiEnabled(false);
				LogMsg("Switched off the wifi");
				 
			}
			
			//release the resource :P
			wifimanager = null;
		}
	}
	
	private static void LogMsg(String msg) {
		Log.d("Wifi", msg);
	}
}
