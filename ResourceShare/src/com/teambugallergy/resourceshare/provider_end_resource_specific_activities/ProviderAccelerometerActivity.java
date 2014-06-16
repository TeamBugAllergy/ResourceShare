package com.teambugallergy.resourceshare.provider_end_resource_specific_activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.teambugallergy.resourceshare.R;
import com.teambugallergy.resourceshare.activities.ProviderActivity;
import com.teambugallergy.resourceshare.bluetooth.ConnectedDevice;
import com.teambugallergy.resourceshare.constants.Resources;
import com.teambugallergy.resourceshare.resources.MyAccelerometer;
import com.teambugallergy.resourceshare.resources.Resource;

/**
 * This Activity is invoked by ProviderActivity WHENEVER Seeker sends 'Resource
 * Access Request' message. First try to Acquire and lock the Requested
 * Resource. Send the result of this process to Seeker Device. This activity
 * will have buttons to control the sharing the Flash.
 * 
 * @author TeamBugAllergy
 *  30-04-2014
 */
public class ProviderAccelerometerActivity extends Activity {

	/**
	 * Context of this object.
	 */
	private static Context providerAccelerometerActivityContext;

	/**
	 * Object representing the connected Seeker device.
	 */
	private static ConnectedDevice connected_seeker_device;

	/**
	 * Object of Flash class, to control the flash resource.
	 */
	private static MyAccelerometer accelerometer;
	
	/**
	 * TextView to display the sharing status.
	 */
	private static TextView sharing_status;
	
	/**
	 * Button to stop sharing .
	 */
	private static Button stop_sharing;
	
	// -----------------------------------------------------------------------------------
	/**
	 * Handler to receive messages.
	 */
	private static Handler providerAccelerometerActivityHandler = new Handler() {

		public void handleMessage(Message msg) {

		//***This if()else should be in the beginning :) 	
		// message is ACCELEROMETER_CONTROL 
		if(msg.what == Resources.ACCELEROMETER_CONTROL)
		{
			//If it is telling to get the X,Y and Z values
			if(Integer.parseInt( msg.obj.toString() ) == Resources.ACCELEROMETER_GET_XYZ)
			{
				//send the xyz value to seeker
				//what is 'ACCELEROMETER_XYZ_VALUES:X|Y|Z'
				//data in obj will be 'X|Y|Z' format
				connected_seeker_device.sendData( (Resources.ACCELEROMETER_XYZ_VALUES + ":" + accelerometer.X + "|" + accelerometer.Y + "|" + accelerometer.Z).getBytes() );
				
				LogMsg("X:" + accelerometer.X + ",Y:" + accelerometer.Y + ",Z:" + accelerometer.Z );
			}
						
			//again wait for further messages from seeker device
			connected_seeker_device.receiveData();
		}
		//if the message is SHARING_CONTROL
		else if(msg.what == Resources.SHARING_CONTROL)
		{
			//if it is STOP_SHARING
			if( Integer.parseInt( msg.obj.toString() ) == Resources.STOP_SHARING )
			{
			
			//stop sharing accelerometer	
			stopSharingAccelerometer();	
			
			LogMsg("Accelerometer has been released");
			
			}
			
			
		}
		
		//if the message is Disconnect message,
		else if(msg.what == Resources.DISCONNECT)
		{
			//***IMP***
			//This is the FUTURE where disconnect() should be called.
			
			//just terminate the connection.
			connected_seeker_device.disconnect();
			
			LogMsg(connected_seeker_device.getDevice().getName() + " has been disconnected.");
			
			//display the message
			Toast.makeText(providerAccelerometerActivityContext, connected_seeker_device.getDevice().getName() + " has been disconnected.", Toast.LENGTH_SHORT).show();

			//goback to MainActivity and finish() this activity
			
			//finish this activity
			((Activity) providerAccelerometerActivityContext).finish();
			
		}
		
			// message is RESOURCE_ACCESS_REQUEST
		else if (msg.what == Resources.RESOURCE_ACCESS_REQUEST) {

				LogMsg("Received RESOURCE_ACCESS_REQUEST message from "
						+ connected_seeker_device.getDevice().getName()
						+ " for "
						+ new Resource().getResourceName(Integer
								.parseInt(msg.obj.toString())) + ".");

				// Ask User's permission to share the resource
				// if he grants, try to acquire and lock the resource.
				// send the result of this process to connected_seeker_device
				AlertDialog confirmResourceId = new AlertDialog.Builder(
						providerAccelerometerActivityContext)
						// set message, title, and icon
						.setTitle("Access Permission.")
						.setMessage(
								connected_seeker_device.getDevice().getName()
										+ " is trying to access the Accelerometer. Would you like to grant the permission?")

						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int whichButton) {

										//close the alertDialog
										dialog.dismiss();
										
										// try to acquire and lock the
										// resource.
										accelerometer = new MyAccelerometer(providerAccelerometerActivityContext);
										
										LogMsg("Trying to acquire the Accelerometer.");
										if(accelerometer.acquireAccelerometer() == true)
										{
											//Only if above process is
											// successful,
											// send the RESOURCE_ACCESS_GRANTED
											// message
											connected_seeker_device
													.sendData((Resources.RESOURCE_ACCESS_REQUEST
															+ ":" + Resources.RESOURCE_ACCESS_GRANTED)
															.getBytes());
											
											LogMsg("Sending the connected_seeker_device RESOURCE_ACCESS_GRANTED message.");
											
											// wait for ACCELROMETER CONTROL MESSAGES (i.e messages to get the xyz values)
											connected_seeker_device.receiveData();
											LogMsg("Waiting for accelerometer Control messages");
											
											//display the status to user
											sharing_status.setText("Accelerometer is being used by " + connected_seeker_device.getDevice().getName());
											
											//display the button to stop sharing
											stop_sharing.setVisibility(View.VISIBLE);
											
										}
										else
										{
											//if Accelerometer could'nt be acquired,
											//Display the message to user
											Toast.makeText(providerAccelerometerActivityContext, "Accelerometer couldn't be acquired. Check any other process is using it.", Toast.LENGTH_LONG).show();
											
											//display the status to user
											sharing_status.setText("Accelerometer couldn't be acquired. Check any other process is using it.");
											
											//TODO: finish this and goback to MainActivity.
										}
										
									}

								})

						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {

										// Send the
										// connected_seeker_device a message
										// that User has denied the access to
										// accelerometer

										// send the RESOURCE_ACCESS_DENIED
										// message
										connected_seeker_device
												.sendData((Resources.RESOURCE_ACCESS_REQUEST
														+ ":" + Resources.RESOURCE_ACCESS_DENIED)
														.getBytes());
										LogMsg("Sending the connected_seeker_device RESOURCE_ACCESS_DENIED message.");
										dialog.dismiss();

									}
								}).create();

				// display the dialog on the screen
				confirmResourceId.show();

			}
			// Unexpected messages
			else {
				LogMsg("Unexpected message received in this Handler.");
			}
		}
	};

	// -----------------------------------------------------------------------------------

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			//Intial setup
			//Keep the screen on
			//This flag will be cleared when this activity is destroyed
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			
			// same layout is also used by SeekerAccelerometerActivity
			setContentView(R.layout.activity_accelerometer);

			// save the context statically
			providerAccelerometerActivityContext = this;

			// get the connected_seeker_device object from ProviderActivity.
			connected_seeker_device = ProviderActivity.getConnectedSeekerDevice();

			if (connected_seeker_device != null) {
				// set the new callerHandler for this device object as
				// providerAccelerometerActivityHandler
				connected_seeker_device
						.setCallerHandler(providerAccelerometerActivityHandler);
				
				// set the device index to 0
				connected_seeker_device.setDeviceIndex(0);

				// wait for RESOURCE_ACCESS_REQUEST message from the Connected
				// Seeker Device
				connected_seeker_device.receiveData();
				LogMsg("waiting for message from seeker device");

				//TextView to display the sharing status
				sharing_status = (TextView)findViewById(R.id.sharing_status);
				//show the TextView
				sharing_status.setVisibility(View.VISIBLE);
				//display the status to user
				sharing_status.setText("Waiting for Access Request from " + connected_seeker_device.getDevice().getName());
				
				//button to stop the sharing
				stop_sharing = (Button)findViewById(R.id.stop_sharing);
				stop_sharing.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {

						//stop sharing
						stopSharingAccelerometer();
						}
					}
				);
							
			} else {
				LogMsg("There is no Connected Seeker Device.");
			}

		}
		
		/**
		 * Swtiches off the accelerometer and releases it.
		 * Also sends the message to Seeker device stating that Resource is not Shared.
		 */
		private static void stopSharingAccelerometer()
		{
			//release the accelerometer
			accelerometer.releaseAccelerometer();
			
			//send a message to seeker device telling that resource is no more shared.
			//message "SHARING_STATUS:SHARING_STOPPED"
			connected_seeker_device.sendData( (Resources.SHARING_STATUS + ":" + Resources.SHARING_STOPPED).getBytes() );
			
			//wait for next message from the seeker
			connected_seeker_device.receiveData();
			LogMsg("Waiting for messages from the seeker");
		}
		
		/**
		 * To disconnect from seeker device and release the Accelerometer.
		 */
		@Override
		public void onBackPressed() {
			super.onBackPressed();
			
			//stop sharing the flash and finish the activity
			stopSharingAccelerometer();			
		}
		
		@Override
		protected void onDestroy() {
			super.onDestroy();

			// stop the reading thread
			if (connected_seeker_device != null) {
				// no need of this
				// connected_device.stopReceivingData();

				// BELOW STATEMENT SHOULD NOT BE CALLED UNTILL THE END OF RESOURCE
				// SHARING
				// terminate the connection
				connected_seeker_device.disconnect();
				// above statement must be called IN FUTURE
			}
			
			//release the accelerometer
			if(accelerometer != null)
			{
				//release the Accelerometer
				accelerometer.releaseAccelerometer();
			}
			
		}
		
		private static void LogMsg(String msg) {
			Log.d("ProviderAccelerometerActivity", msg);
		}
}
