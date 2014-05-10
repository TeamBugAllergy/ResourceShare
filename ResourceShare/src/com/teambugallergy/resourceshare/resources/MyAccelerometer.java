package com.teambugallergy.resourceshare.resources;

import com.teambugallergy.resourceshare.constants.Resources;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * This class has methods that allows to use the Accelerometer Resource in the device.
 * The methodologies provide querying for availability (available, unavailable and busy) of the resource,
 * accessing the resource (switching on/off, giving data to OR taking the data from the resource) and other Resource Specific 
 * functionalities, if any.
 * @author Adiga@TeamBugAllergy
 * 27-04-2014
 */
public class MyAccelerometer implements SensorEventListener{

	/**
	 * Availability status returned by availability() method.
	 RESOURCE_AVAILABLE = 701;
	 RESOURCE_UNAVAILABLE = 702;
	 RESOURCE_BUSY = 703; 
	 */
	
	/**
	 * Save the availability of accelerometer.
	 */
	private int availability = 0;
	
	/**
	 * Context of the caller.
	 */
	private Context callerContext;
	
	/**
	 * Object to manage various types of sensors.
	 * Here it is used to register the accelerometer onSensorChanged().
	 */
	private SensorManager sensor_manager;
	
	/**
	 * Sensor object that references Accelerometer.
	 */
	private Sensor accelerometer_sensor;
	
	/**
	 * X acceleration
	 */
	public Float X=0f;
	/**
	 * Y acceleration
	 */
	public Float Y=0f;
	/**
	 * Z acceleration
	 */
	public Float Z=0f;
	
	
	/**
	 * Checks the availability of the accelerometer and stores the result in 'availability'.
	 * @param context Context of the caller Activity.
	 */
	public MyAccelerometer(Context context)
	{
	
		//save the context.
				this.callerContext = context;
				
				//check if the accelerometer is present
				if ( callerContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER) ){
			    			    	
		         	//Device has accelerometer and it is free now (AVAILABLE).
			    	availability = Resources.RESOURCE_AVAILABLE;
			    } 
			    else 
			    {
			        //Device doesn't have accelerometer (UNAVAILABLE).
			    	availability = Resources.RESOURCE_UNAVAILABLE;
			    	
			    }
				//There is no BUSY state for this resource
	}
	
	/**
	 * Return the availability of the accelerometer.
	 * @return
	 */
	public int availability()
	{	
		//return the availability of the accelerometer
		return availability;				
	}
	
	/**
	 * Registers the accelerometer resource.
	 * Whenever acceleration changes, onSensorChanged() will be called with new X,Y and Z acceleration values.
	 * @return result of acquire (true/false)
	 */
	public Boolean acquireAccelerometer()
	{

		//initialize the sensor _manager
		sensor_manager = (SensorManager)callerContext.getSystemService(Context.SENSOR_SERVICE);
		
		//get a Accelerometer sensor
		accelerometer_sensor = sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		if(accelerometer_sensor == null)
			//failed to register the accelerometer
			return false;
		
		//Register for accelerometer sensor. It calls onSensorCHanged() whenever accelerometer changes
		sensor_manager.registerListener(this, accelerometer_sensor, SensorManager.SENSOR_DELAY_NORMAL);
		
		//successfully registered the accelerometer
		return true;
       
    }
	/**
	 * Only after calling abouve acquireAccelerometer() method, 
	 * public member variables X,Y and Z can be used to get the acceleration values. 
	 */
	
	/**
	 * Save the updated X,Y,Z accelerations.
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {

		//save the values
		X = event.values[0];
	    Y =event.values[1];
	    Z = event.values[2];
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	
	
	/**
	 * Unregister the Accelerometer and release it.
	 */
	public void releaseAccelerometer()
	{
		if(accelerometer_sensor != null)
		{
			
			sensor_manager.unregisterListener(this, accelerometer_sensor);
			LogMsg("Accelerometer has been Unregisterd");
			
			accelerometer_sensor = null;
			sensor_manager = null;
		}
	}
	
	private static void LogMsg(String msg) {
		Log.d("Accelerometer", msg);
	}
}
