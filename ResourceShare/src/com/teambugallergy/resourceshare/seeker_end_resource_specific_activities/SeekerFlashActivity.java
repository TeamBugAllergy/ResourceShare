package com.teambugallergy.resourceshare.seeker_end_resource_specific_activities;

import com.teambugallergy.resourceshare.R;
import com.teambugallergy.resourceshare.activities.MainActivity;
import com.teambugallergy.resourceshare.activities.ResourceListActivity;
import com.teambugallergy.resourceshare.bluetooth.ConnectedDevice;
import com.teambugallergy.resourceshare.constants.Resources;

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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * This Activity is invoked by ResoueceListActivity <i>AFTER BELOW
 * PROCESSES</i>. onClick of 'Access Resource' button, will send a 'Resource
 * Access Request' message to Provider devices and start the
 * SeekerFlashActivity. ***Set the callerHandler for objects in
 * potential_provider_list as Handler of this class, i.e
 * seekerFlashActivityHandler.*** Display appropriate messages to user based on
 * the messages received from potential_provider_list.
 * 
 * @author Team BugAllergy 30-04-2014
 */
public class SeekerFlashActivity extends Activity {

	/**
	 * An array of ConnectedDevice objects that are there in
	 * connected_device_list[] and have REQUEST_STATUS as ACCEPTED. These
	 * devices have been accepted to share the requested resource. So they
	 * become Potential Provider Devices.
	 */
	private static ConnectedDevice[] potential_provider_list;

	/**
	 * Context of this activity.
	 */
	private static Context seekerFlashActivityContext;

	/**
	 * To switch on/off the flash.
	 */
	private static ToggleButton flash_switch;
	
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
	private static Handler seekerFlashActivityHandler = new Handler() {

		public void handleMessage(Message msg) {

			final int sender_index = msg.arg2;

			LogMsg("Message received");
			
			//message that informs the SHARING_STATUS of the flash
			if(msg.what == Resources.SHARING_STATUS)
			{
				if( Integer.parseInt( msg.obj.toString() ) == Resources.SHARING_STARTED)
				{
					LogMsg(potential_provider_list[sender_index].getDevice()
							.getName() + " has started sharing the flash");
					
					//display it in sharing_status
					sharing_status.setText(potential_provider_list[sender_index].getDevice()
							.getName() + " has started sharing the flash");
				}
				else// if( Integer.parseInt( msg.obj.toString() ) == Resources.SHARING_STOPPED)
				{
	/*				//Disable the UI to allow the user to start using the flash :)
					flash_switch.setEnabled(false);
					
					//display the button to stop sharing
					stop_sharing.setEnabled(false);
					
	*/
					LogMsg(potential_provider_list[sender_index].getDevice()
							.getName() + " has stopped sharing the flash");
					
					//display it in sharing_status
					sharing_status.setText(potential_provider_list[sender_index].getDevice()
							.getName() + " has stopped sharing the flash");
					
					//****IMP*****
					// Note down this potential_provider_device
					// because ONLY this provider should not be sent messages to switch on/off the flash
					
					//remove this provider and shift remaining devices in the array to left
					removeProviderDeviceFromList(sender_index);
				}
				
			}
			// message is RESOURCE_ACCESS_REQUEST
			else if (msg.what == Resources.RESOURCE_ACCESS_REQUEST) {
				
				// if provider has accepted the resource access
				if (Integer.parseInt(msg.obj.toString()) == Resources.RESOURCE_ACCESS_GRANTED) {
					
					// display a message
					Toast.makeText(
							seekerFlashActivityContext,
							potential_provider_list[msg.arg2].getDevice()
									.getName()
									+ "has accepted the access to Flash",
							Toast.LENGTH_LONG).show();

					LogMsg(potential_provider_list[msg.arg2].getDevice()
							.getName() + "has accepted the access to Flash");

					//Enable the UI to allow the user to start using the flash :)
					flash_switch.setVisibility(View.VISIBLE);
					flash_switch.setChecked(false);
					
					//display the message
					sharing_status.setText(potential_provider_list[msg.arg2].getDevice()
							.getName() + "has started sharing Flash");
					
					//display the button to stop sharing
					stop_sharing.setVisibility(View.VISIBLE);
					
					//Wait for SHARING_STATUS messages from provider devices
					potential_provider_list[msg.arg2].receiveData();
				}
			
				// if provider has denied the resource access
				else if (Integer.parseInt(msg.obj.toString()) == Resources.RESOURCE_ACCESS_DENIED) {
					// display a message
					Toast.makeText(
							seekerFlashActivityContext,
							potential_provider_list[msg.arg2].getDevice()
									.getName()
									+ "has denied the access to Flash",
							Toast.LENGTH_LONG).show();

					LogMsg(potential_provider_list[msg.arg2].getDevice()
							.getName() + "has denied the access to Flash");

					//Ask the user if he/she wants to resend the
					// RESOURCE_ACCESS_REQUEST message and wait for the reply
					// ONLY to this particular Potential Provider.
					AlertDialog confirmResourceId = new AlertDialog.Builder(
							seekerFlashActivityContext)
							// set message, title, and icon
							.setTitle("Retry access.")
							.setMessage(potential_provider_list[msg.arg2].getDevice()
									.getName() +
									" has denied to share the Flash. Would you like to try again?")

							.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											
											//resend the
											// RESOURCE_ACCESS_REQUEST message and wait for the reply
											// ONLY to this particular Potential Provider.
											LogMsg("Resending the message RESOURCE_ACCESS_REQUEST to " + potential_provider_list[sender_index].getDevice().getName());
											//data in the form 'what:data' i.e 'RESOURCE_ACCESS_REQUEST:resource_id'
											potential_provider_list[sender_index].sendData( (Resources.RESOURCE_ACCESS_REQUEST + ":" + Resources.FLASH).getBytes() );

											// start waiting for reply messages from potential_provider_list[].
											potential_provider_list[sender_index].receiveData();
											
											LogMsg("Resending the RESOURCE_ACCESS_REQUEST message to " + potential_provider_list[sender_index].getDevice()
													.getName());
											dialog.dismiss();
										}

									})

							.setNegativeButton("No",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											
											LogMsg("Didn't send the RESOURCE_ACCESS_REQUEST message again to " + potential_provider_list[sender_index].getDevice()
													.getName());
											
											//****IMP*****
											//Note down this potential_provider_device
											// because ONLY this provider should not be sent messages to switch on/off the flash
											
											//remove this provider and shift remaining devices in the array to left
											removeProviderDeviceFromList(sender_index);
											
											dialog.dismiss();

										}
									}).create();

					// display the dialog on the screen
					confirmResourceId.show();

				}

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

		// same layout is also used by ProviderFlashActivity
		setContentView(R.layout.activity_flash);

		//This toggle button is made visible only after getting a RESOURCE_ACCESS_GRANT message
		flash_switch = (ToggleButton)findViewById(R.id.flash_switch);
		flash_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			
				if(buttonView.getId() == flash_switch.getId())
				{
				if(isChecked == true)
				{
					// switch on the flash
					for (int i = 0; potential_provider_list[i] != null ;i ++)
					{
						// send the message to provider devices to swtich off the flash
						potential_provider_list[i].sendData( (Resources.FLASH_CONTROL + ":" + Resources.FLASH_SWITCH_ON).getBytes() );
						LogMsg("Sending FLASH_SWITCH_ON to potential provider devices");
						//Toast.makeText(seekerFlashActivityContext, "Switching on the flash", Toast.LENGTH_SHORT).show();
						
						sharing_status.setText("Flash is switched on");
					}
				}
				else
				{
					// switch off the flash
					for (int i = 0; potential_provider_list[i] != null ;i ++)
					{
						// send the message to provider devices to swtich off the flash
						potential_provider_list[i].sendData( (Resources.FLASH_CONTROL + ":" + Resources.FLASH_SWITCH_OFF).getBytes() );
						LogMsg("Sending FLASH_SWITCH_OFF to potential provider devices");
						//Toast.makeText(seekerFlashActivityContext, "Switching off the flash", Toast.LENGTH_SHORT).show();
						
						sharing_status.setText("Flash is switched off");
					}
				}
				}
				
			}
		});
		
		//TextView to disply the sharing status
		sharing_status = (TextView)findViewById(R.id.sharing_status);
		sharing_status.setVisibility(View.VISIBLE);
		sharing_status.setText("Waiting for Access Permission from Provider devices.");
		
		//button to stop the sharing
		stop_sharing = (Button)findViewById(R.id.stop_sharing);
		stop_sharing.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//send a message  STOP_SHARING to devices in potential_provider_list[]
				for(int i=0; potential_provider_list[i] != null; i++)
				{
					//message will have 'SHARING_CONTROL:STOP_SHARING'.
					potential_provider_list[i].sendData( (Resources.SHARING_CONTROL + ":" + Resources.STOP_SHARING).getBytes() );
					
					//TODO: if you are expecting any other messages from Potential Provider Devices, potential_providr_device[i].receiveData();
				}
			}
		});
		
		// save the context statically
		seekerFlashActivityContext = this;

		// get the potential_provider_list[] array from ResourceListActivity
		potential_provider_list = ResourceListActivity.getPotentialDeviceList();

		int i = 0;
		for (i = 0; potential_provider_list[i] != null; i++) {
			// set the device index of all the provider devices according to
			// index in 'potential_provider_list[]'
			potential_provider_list[i].setDeviceIndex(i);

			// set the callerHandler of objects in potential_provider_list[] to
			// seekerFlashActivityHandler
			potential_provider_list[i]
					.setCallerHandler(seekerFlashActivityHandler);
						
			LogMsg("sending the message RESOURCE_ACCESS_REQUEST");
			//data in the form 'what:data' i.e 'RESOURCE_ACCESS_REQUEST:resource_id'
			potential_provider_list[i].sendData( (Resources.RESOURCE_ACCESS_REQUEST + ":" + Resources.FLASH).getBytes() );
			
			// start waiting for reply messages from potential_provider_list[].
			potential_provider_list[i].receiveData();
		}
		if (i == 0) {
			LogMsg("There are No potential Provider devices.");
		}

	};
	
	/**
	 * Makes the ConnectedDevice object in <i>potential_provider_list[]</i> null. So that it will not be used in future.
	 * So makes that object null and shift the devices ahead of it in the array one position left.
	 * @param position index of ConnectedDevice object in the potential_provider_list[] array which has to be removed from list. 
	 */
	private static void removeProviderDeviceFromList(int position)
	{
		
		Toast.makeText(seekerFlashActivityContext, potential_provider_list[position].getDevice().getName() + " has been disconnected.", Toast.LENGTH_LONG).show();
		
		//message to tell the provider to disconect itself.
		potential_provider_list[position].sendData( (Resources.DISCONNECT + ":" + 0).getBytes() );
		
		//make the 'position'th object null
		potential_provider_list[position] = null;

		//from 'i = position+1'
		int i;
		for(i = position + 1; potential_provider_list[i] != null; i++)
		{
			//shift one position left,
			potential_provider_list[i-1] = potential_provider_list[i];
			
			//also change the device_index in ConnectedDvice object
			potential_provider_list[i-1].setDeviceIndex( (i-1) );
			
		}
		//remove the repeated object at the end of array
		potential_provider_list[i-1] = null;
		
		LogMsg("One provider device has been removed.");
		
	//	LogMsg("potential_provider_list[] has:");
	//	for(i = 0; potential_provider_list[i] != null; i++)
	//		LogMsg(potential_provider_list[i].getDevice().getName() + "");
		
		//if there are no objects in the array,
		//i.e If there are no Potential provider devices, goto MainActivity and finish this activity.
		if(potential_provider_list[0] == null)
		{
			//start the MainActivity
			//Intent intent = new Intent(seekerFlashActivityContext, MainActivity.class);
			//seekerFlashActivityContext.startActivity(intent);
			
			//finish() this activity
			((Activity) seekerFlashActivityContext).finish();
		}
		
	}
	
	/**
	 * To disconnect from all the potential provider devices by sending them 'DISCONNECT'.
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		//remove all the devices from the potential_provider_list[]
		for(int i=0; potential_provider_list[i] != null; i++)
		{
			//removes each of the provider device and sends them 'DISCONNECT' message.
			removeProviderDeviceFromList(i);	
		}
		LogMsg("All the providers have been removed and disconnected.");
		
	}
	
	private static void LogMsg(String msg) {
		Log.d("SeekerFlashActivity", msg);
	}
}
