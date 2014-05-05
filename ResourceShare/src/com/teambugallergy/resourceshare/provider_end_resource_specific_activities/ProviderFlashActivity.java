package com.teambugallergy.resourceshare.provider_end_resource_specific_activities;

import com.teambugallergy.resourceshare.R;
import com.teambugallergy.resourceshare.activities.MainActivity;
import com.teambugallergy.resourceshare.activities.ProviderActivity;
import com.teambugallergy.resourceshare.bluetooth.ConnectedDevice;
import com.teambugallergy.resourceshare.constants.Resources;
import com.teambugallergy.resourceshare.resources.MyFlash;
import com.teambugallergy.resourceshare.resources.Resource;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
	private static MyFlash flash;
	
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
	private static Handler providerFlashActivityHandler = new Handler() {

		public void handleMessage(Message msg) {

		//***This if()else should be in the beginning :) 	
		// message is FLASH_CONTROL 
		if(msg.what == Resources.FLASH_CONTROL)
		{
			//If it is telling to switch on
			if(Integer.parseInt( msg.obj.toString() ) == Resources.FLASH_SWITCH_ON)
			{
				//switch on the flash
				flash.switchOnFlash();
				LogMsg("Switching on the flash.");
				//Toast.makeText(providerFlashActivityContext, "Switching on the flash", Toast.LENGTH_SHORT).show();
			}
			
			else //No need if( msg.obj.equals(Resources.FLASH_SWITCH_OFF) )
			{
				//switch off the flash
				flash.switchOffFlash();
				LogMsg("Switching off the flash.");
				//Toast.makeText(providerFlashActivityContext, "Switching off the flash", Toast.LENGTH_SHORT).show();
			}
			
			//again wait for messages from seker device
			connected_seeker_device.receiveData();
		}
		//if the message is SHARING_CONTROL
		else if(msg.what == Resources.SHARING_CONTROL)
		{
			//if it is STOP_SHARING
			if( Integer.parseInt( msg.obj.toString() ) == Resources.STOP_SHARING )
			{
			
			//stop sharing flash	
			stopSharingFlash();	
			
			LogMsg("Flash has been released");
			
			}
			
			
		}
		
		//if the message is Disconnect message,
		else if(msg.what == Resources.DISCONNECT)
		{
			//***IMP***
			//This is the FUTURE where disconnecte() should be called.
			
			//just terminate the connection.
			connected_seeker_device.disconnect();
			
			LogMsg(connected_seeker_device.getDevice().getName() + " has been disconnected.");
			
			//display the message
			Toast.makeText(providerFlashActivityContext, connected_seeker_device.getDevice().getName() + " has been disconnected.", Toast.LENGTH_SHORT).show();

			//goback to MainActivity and finish() this activity
			//Intent intent = new Intent(providerFlashActivityContext, MainActivity.class);
			//providerFlashActivityContext.startActivity(intent);
			
			//finish this activity
			((Activity) providerFlashActivityContext).finish();
			
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
										flash = new MyFlash(providerFlashActivityContext);
										
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
											
											//display the button to stop sharing
											stop_sharing.setVisibility(View.VISIBLE);
											
										}
										else
										{
											//if Flash could'nt be acquired,
											//Display the mmessage to user
											Toast.makeText(providerFlashActivityContext, "Flash couldn't be acquired. Check any other process is using it.", Toast.LENGTH_LONG).show();
											
											//display the status to user
											sharing_status.setText("Flash couldn't be acquired. Check any other process is using it.");
											
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
			//display the status to user
			sharing_status.setText("Waiting for Access Request from " + connected_seeker_device.getDevice().getName());
			
			//button to stop the sharing
			stop_sharing = (Button)findViewById(R.id.stop_sharing);
			stop_sharing.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {

					//stop sharing
					stopSharingFlash();
					}
				}
			);
						
		} else {
			LogMsg("There is no Connected Seeker Device.");
		}

	}
	/**
	 * Swtiches off the flash and releases it.
	 * Also sends the message to Seeker device stating that Resource is not Shared.
	 */
	private static void stopSharingFlash()
	{
		//switch off the flash
		flash.switchOffFlash();
		//release the flash
		flash.releaseFlash();
		
		//send a message to seeker device telling that resource is no more shared.
		//message "SHARING_STATUS:SHARING_STOPPED"
		connected_seeker_device.sendData( (Resources.SHARING_STATUS + ":" + Resources.SHARING_STOPPED).getBytes() );
		
		//display the message
		//sharing_status.setText("Sharing the flash has been stopped.");
	}
	
	/**
	 * To disconnect from seeker device and release the Flash.
	 */
	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		
		//notify the seeker about this
		//stopSharingFlash();
		AlertDialog confirmOnBack = new AlertDialog.Builder(
				providerFlashActivityContext)
				// set message, title, and icon
				.setTitle("Going back.")
				.setMessage( "Sharing has been completed." )

				.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int whichButton) {
								
								stopSharingFlash();
								// stop the reading thread
								if (connected_seeker_device != null) {
									
									// terminate the connection
									connected_seeker_device.disconnect();
									LogMsg("Disconnected from the " + connected_seeker_device.getDevice().getName());
									
								}
								
								//release the flash
								if(flash != null)
								{
									//release the Flash and camera
									flash.releaseFlash();
								}
								
								LogMsg("User has confirmed.");
								dialog.dismiss();
								
								((Activity) providerFlashActivityContext).finish();
								
						}}).create();

						// display the dialog on the screen
		confirmOnBack.show();
		
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
