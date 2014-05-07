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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This Activity is invoked by ResoueceListActivity <i>AFTER BELOW
 * PROCESSES</i>. onClick of 'Access Resource' button, will send a 'Resource
 * Access Request' message to Provider devices and start the
 * SeekerAccelerometerActivity. ***Set the callerHandler for objects in
 * potential_provider_list as Handler of this class, i.e
 * seekerAccelerometerActivityHandler.*** Display appropriate messages to user
 * based on the messages received from potential_provider_list.
 * 
 * @author Adiga@TeamBugAllergy 07-05-2014
 */
public class SeekerAccelerometerActivity extends Activity {

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
	private static Context seekerAcceleromterActivityContext;

	/**
	 * To get the Accelerometer values.
	 */
	private static Button get_xyz;

	/**
	 * TextView to display the xyz_values of accelerometer.
	 */
	private static TextView xyz_values;

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
	private static Handler seekerAccelerometerActivityHandler = new Handler() {

		public void handleMessage(Message msg) {

			final int sender_index = msg.arg2;

			LogMsg("Message received");

			//if this message is carrying xyz_values
			if( msg.what == Resources.ACCELEROMETER_XYZ_VALUES)
			{
				//TODO: check the x,y and z values	
				//name of the device
				xyz_values.append(potential_provider_list[sender_index].getDevice().getName() + ":\n");
				
				//get the x, y and z values from the message
				String[] xyz = ( msg.obj.toString() ).split(":");
				
				//X value
				xyz_values.append( "X:" + xyz[0] + "\n");
				
				//Y value
				xyz_values.append( "Y:" + xyz[1] + "\n");
				
				//Z value
				xyz_values.append( "Z:" + xyz[2] + "\n");
				
			} 
			
			// message that informs the SHARING_STATUS of the accelerometer
			else if (msg.what == Resources.SHARING_STATUS) {
				if (Integer.parseInt(msg.obj.toString()) == Resources.SHARING_STARTED) {
					LogMsg(potential_provider_list[sender_index].getDevice()
							.getName()
							+ " has started sharing the accelerometer");

					// display it in sharing_status
					sharing_status
							.setText(potential_provider_list[sender_index]
									.getDevice().getName()
									+ " has started sharing the accelerometer");
				} else// if( Integer.parseInt( msg.obj.toString() ) ==
						// Resources.SHARING_STOPPED)
				{
					/*
					 * //Disable the UI to not allow the user to start using the
					 * accelerometer :)
					 * 
					 * //display the button to stop sharing
					 */
					LogMsg(potential_provider_list[sender_index].getDevice()
							.getName()
							+ " has stopped sharing the accelerometer");

					// display it in sharing_status
					sharing_status
							.setText(potential_provider_list[sender_index]
									.getDevice().getName()
									+ " has stopped sharing the accelerometer");

					// ****IMP*****
					// Note down this potential_provider_device
					// because ONLY this provider should not be sent messages to
					// access the accelerometer

					// remove this provider and shift remaining devices in the
					// array to left
					removeProviderDeviceFromList(sender_index);
				}

			}

			// message is RESOURCE_ACCESS_REQUEST
			else if (msg.what == Resources.RESOURCE_ACCESS_REQUEST) {

				// if provider has accepted the resource access
				if (Integer.parseInt(msg.obj.toString()) == Resources.RESOURCE_ACCESS_GRANTED) {

					// display a message
					Toast.makeText(
							seekerAcceleromterActivityContext,
							potential_provider_list[msg.arg2].getDevice()
									.getName()
									+ "has accepted the access to accelerometer",
							Toast.LENGTH_LONG).show();

					LogMsg(potential_provider_list[msg.arg2].getDevice()
							.getName()
							+ "has accepted the access to accelerometer");

					// Enable the UI to allow the user to start using the
					// accelerometer :)
					get_xyz.setVisibility(View.VISIBLE);
					xyz_values.setVisibility(View.VISIBLE);

					// display the message
					sharing_status.setText(potential_provider_list[msg.arg2]
							.getDevice().getName()
							+ "has started sharing Accelerometer");

					// display the button to stop sharing
					stop_sharing.setVisibility(View.VISIBLE);

					// Wait for SHARING_STATUS messages from provider devices
					potential_provider_list[msg.arg2].receiveData();
				}

				// if provider has denied the resource access
				else if (Integer.parseInt(msg.obj.toString()) == Resources.RESOURCE_ACCESS_DENIED) {
					// display a message
					Toast.makeText(
							seekerAcceleromterActivityContext,
							potential_provider_list[msg.arg2].getDevice()
									.getName()
									+ "has denied the access to Accelerometer",
							Toast.LENGTH_LONG).show();

					LogMsg(potential_provider_list[msg.arg2].getDevice()
							.getName()
							+ "has denied the access to Accelerometer");

					// Ask the user if he/she wants to resend the
					// RESOURCE_ACCESS_REQUEST message and wait for the reply
					// ONLY to this particular Potential Provider.
					AlertDialog confirmResourceId = new AlertDialog.Builder(
							seekerAcceleromterActivityContext)
							// set message, title, and icon
							.setTitle("Retry access.")
							.setMessage(
									potential_provider_list[msg.arg2]
											.getDevice().getName()
											+ " has denied to share the Accelerometer. Would you like to try again?")

							.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int whichButton) {

											// resend the
											// RESOURCE_ACCESS_REQUEST message
											// and wait for the reply
											// ONLY to this particular Potential
											// Provider.
											LogMsg("Resending the message RESOURCE_ACCESS_REQUEST to "
													+ potential_provider_list[sender_index]
															.getDevice()
															.getName());
											// data in the form 'what:data' i.e
											// 'RESOURCE_ACCESS_REQUEST:resource_id'
											potential_provider_list[sender_index]
													.sendData((Resources.RESOURCE_ACCESS_REQUEST
															+ ":" + Resources.ACCELEROMETER)
															.getBytes());

											// start waiting for reply messages
											// from potential_provider_list[].
											potential_provider_list[sender_index]
													.receiveData();

											LogMsg("Resending the RESOURCE_ACCESS_REQUEST message to "
													+ potential_provider_list[sender_index]
															.getDevice()
															.getName());
											dialog.dismiss();
										}

									})

							.setNegativeButton("No",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {

											LogMsg("Didn't send the RESOURCE_ACCESS_REQUEST message again to "
													+ potential_provider_list[sender_index]
															.getDevice()
															.getName());

											// ****IMP*****
											// Note down this
											// potential_provider_device
											// because ONLY this provider should
											// not be sent messages to access
											// the accelerometer

											// remove this provider and shift
											// remaining devices in the array to
											// left
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

			// same layout is also used by ProviderAccelerometerActivity
			setContentView(R.layout.activity_accelerometer);
			
			//button to receive the X,Y and Z values from all potential providers
			get_xyz = (Button)findViewById(R.id.get_xyz);
			get_xyz.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					//for each potential_provider, in the list,
					for(int i=0; potential_provider_list[i] != null; i++)
					{
						//clear the TextView
						xyz_values.setText("");
						
						//TODO: make sure that provider device handles this message
						//send 'ACCELEROMETER_CONTROL + ":" + Resources.ACCELEROMETER_GET_XYZ' message to all the providers
						potential_provider_list[i].sendData( ( Resources.ACCELEROMETER_CONTROL + ":" + Resources.ACCELEROMETER_GET_XYZ ).getBytes() );
						
						//TODO: at provider activity, send the x,y and z values "ACCELEROMETER_XYZ_VALUES:X:Y:Z"
						//wait for data from them
						potential_provider_list[i].receiveData();
					}
					
				}
			});
			
			//TextView to display the x,y and z values of all the providers
			xyz_values = (TextView)findViewById(R.id.xyz_values);
			
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
						
					}
				}
			});
			
			// save the context statically
			seekerAcceleromterActivityContext = this;

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
						.setCallerHandler(seekerAccelerometerActivityHandler);
							
				LogMsg("sending the message RESOURCE_ACCESS_REQUEST");
				//data in the form 'what:data' i.e 'RESOURCE_ACCESS_REQUEST:resource_id'
				potential_provider_list[i].sendData( (Resources.RESOURCE_ACCESS_REQUEST + ":" + Resources.ACCELEROMETER).getBytes() );
				
				// start waiting for reply messages from potential_provider_list[].
				potential_provider_list[i].receiveData();
			}
			if (i == 0) {
				LogMsg("There are No potential Provider devices.");
			}
		}
		
		/**
		 * Makes the ConnectedDevice object in <i>potential_provider_list[]</i> null. So that it will not be used in future.
		 * So makes that object null and shift the devices ahead of it in the array one position left.
		 * @param position index of ConnectedDevice object in the potential_provider_list[] array which has to be removed from list. 
		 */
		private static void removeProviderDeviceFromList(int position)
		{
			
			Toast.makeText(seekerAcceleromterActivityContext, potential_provider_list[position].getDevice().getName() + " has been disconnected.", Toast.LENGTH_LONG).show();
			
			//message to tell the provider to disconnect itself.
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
				((Activity) seekerAcceleromterActivityContext).finish();
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
		Log.d("SeekerAccelerometerActivity", msg);
	}
}
