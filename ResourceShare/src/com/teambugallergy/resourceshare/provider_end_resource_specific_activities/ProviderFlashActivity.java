package com.teambugallergy.resourceshare.provider_end_resource_specific_activities;

import com.teambugallergy.resourceshare.R;
import com.teambugallergy.resourceshare.activities.ProviderActivity;
import com.teambugallergy.resourceshare.bluetooth.ConnectedDevice;
import com.teambugallergy.resourceshare.constants.Resources;
import com.teambugallergy.resourceshare.resources.Flash;
import com.teambugallergy.resourceshare.resources.Resource;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This Activity is invoked by ProviderActivity WHENEVER Seeker sends 'Resource
 * Access Request' message. First try to Acquire and lock the Requested
 * Resource. Send the result of this process to Seeker Device. This activity
 * will have buttons to control the sharing the Flash.
 * 
 * @author Team BugAllergy 30-04-2014
 */
public class ProviderFlashActivity extends Activity {

	/**
	 * Context of this object.
	 */
	private static Context providerFlashActivityContext;

	/**
	 * Object representing the connected Seeker device.
	 */
	private static ConnectedDevice connected_seeker_device;

	/**
	 * Object of Flash class, to control the flash resource.
	 */
	private static Flash flash;
	
	/**
	 * TextView to display the sharing status.
	 */
	private static TextView sharing_status;
	
	// -----------------------------------------------------------------------------------
	/**
	 * Handler to receive messages.
	 */
	private static Handler providerFlashActivityHandler = new Handler() {

		public void handleMessage(Message msg) {

		// message is FLASH_CONTROL
		if(msg.what == Resources.FLASH_CONTROL)
		{
			//If it is telling to switch on
			if(Integer.parseInt( msg.obj.toString() ) == Resources.FLASH_SWITCH_ON)
			{
				//switch on the flash
				flash.switchOnFlash();
				LogMsg("Switching on the flash.");
				Toast.makeText(providerFlashActivityContext, "Switching on the flash", Toast.LENGTH_SHORT).show();
			}
			
			else //No need if( msg.obj.equals(Resources.FLASH_SWITCH_OFF) )
			{
				//switch off the flash
				flash.switchOffFlash();
				LogMsg("Switching off the flash.");
				Toast.makeText(providerFlashActivityContext, "Switching off the flash", Toast.LENGTH_SHORT).show();
			}
			
			//again wait for messages from seker device
			connected_seeker_device.receiveData();
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
						providerFlashActivityContext)
						// set message, title, and icon
						.setTitle("Access Permission.")
						.setMessage(
								connected_seeker_device.getDevice().getName()
										+ " is trying to access the Flash. Would you like to grant the permission?")

						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int whichButton) {

										//close the alertDialog
										dialog.dismiss();
										
										// try to acquire and lock the
										// resource.
										flash = new Flash(providerFlashActivityContext);
										
										LogMsg("Trying to acquire the Flash.");
										if(flash.acquireFlash() == true)
										{
											//Only if above preocess is
											// successful,
											// send the RESOURCE_ACCESS_GRANTED
											// message
											connected_seeker_device
													.sendData((Resources.RESOURCE_ACCESS_REQUEST
															+ ":" + Resources.RESOURCE_ACCESS_GRANTED)
															.getBytes());
											
											LogMsg("Sending the connected_seeker_device RESOURCE_ACCESS_GRANTED message.");
											
											// wait for FLASH CONTROL MESSAGES (i.e messages to switch on and off the flash)
											connected_seeker_device.receiveData();
											LogMsg("Waiting for Flash Control messages");
											
											//display the status to user
											sharing_status.setText("Flash is being used by " + connected_seeker_device.getDevice().getName());
										}
										else
										{
											//if Flash could'nt be acquired,
											//Display the mmessage to user
											Toast.makeText(providerFlashActivityContext, "Flash couldn't be acquired. Check any other process is using it.", Toast.LENGTH_LONG).show();
											
											//display the status to user
											sharing_status.setText("Flash couldn't be acquired. Check any other process is using it.");
											
											//TODO: send an ERROR message to Seeker device about this issue
											
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
										// flash

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
				LogMsg("Unexpected message received in this Hnadler.");
			}
		}
	};

	// -----------------------------------------------------------------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// same layout is also used by SeekerFlashActivity
		setContentView(R.layout.activity_flash);

		// save the context statically
		providerFlashActivityContext = this;

		// get the connected_seeker_device object from ProviderActivity.
		connected_seeker_device = ProviderActivity.getConnectedSeekerDevice();

		if (connected_seeker_device != null) {
			// set the new callerHandler for this device object as
			// providerFlashActivityHandler
			connected_seeker_device
					.setCallerHandler(providerFlashActivityHandler);
			// set the device index to 0
			connected_seeker_device.setDeviceIndex(0);

			// wait for RESOURCE_ACCESS_REQUEST message from the Connected
			// Seeker Device
			connected_seeker_device.receiveData();
			LogMsg("waiting for message from seeker device");

			//TextView to disply the sharing status
			sharing_status = (TextView)findViewById(R.id.sharing_status);
			//show the TextView
			sharing_status.setVisibility(View.VISIBLE);
						
		} else {
			LogMsg("There is no Connected Seeker Device.");
		}

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
			// TODO: above statement must be called IN FUTURE
		}
		
		//release the flash
		if(flash != null)
		{
			//release the Flash and camera
			flash.releaseFlash();
		}
		
	}

	private static void LogMsg(String msg) {
		Log.d("ProviderFlashActivity", msg);
	}
}
