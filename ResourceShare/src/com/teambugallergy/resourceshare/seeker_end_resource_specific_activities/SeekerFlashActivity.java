package com.teambugallergy.resourceshare.seeker_end_resource_specific_activities;

import com.teambugallergy.resourceshare.R;
import com.teambugallergy.resourceshare.activities.ResourceListActivity;
import com.teambugallergy.resourceshare.bluetooth.ConnectedDevice;
import com.teambugallergy.resourceshare.constants.Resources;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
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
	
	// -----------------------------------------------------------------------------------
	/**
	 * Handler to receive messages.
	 */
	private static Handler seekerFlashActivityHandler = new Handler() {

		public void handleMessage(Message msg) {

			final int sender_index = msg.arg2;

			LogMsg("Message received");
			
			// message is RESOURCE_ACCESS_REQUEST
			if (msg.what == Resources.RESOURCE_ACCESS_REQUEST) {
				
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
											
											//****IMP*****
											//TODO: TODO: note down this potential_provider_device
											//TODO: because this provider should be sent messages to switch on/off the flash
											
											LogMsg("Didn't send the RESOURCE_ACCESS_REQUEST message again to " + potential_provider_list[sender_index].getDevice()
													.getName());
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
						Toast.makeText(seekerFlashActivityContext, "Switching on the flash", Toast.LENGTH_SHORT).show();
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
						Toast.makeText(seekerFlashActivityContext, "Switching off the flash", Toast.LENGTH_SHORT).show();
					}
				}
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
	
	//TODO: A STOP SHARING button to stop sharing and release the resource.
	
	private static void LogMsg(String msg) {
		Log.d("SeekerFlashActivity", msg);
	}
}
