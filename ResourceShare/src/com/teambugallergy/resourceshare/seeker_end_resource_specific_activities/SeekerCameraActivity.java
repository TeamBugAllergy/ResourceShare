package com.teambugallergy.resourceshare.seeker_end_resource_specific_activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
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
import com.teambugallergy.resourceshare.activities.CustomDialog;
import com.teambugallergy.resourceshare.activities.ResourceListActivity;
import com.teambugallergy.resourceshare.bluetooth.ConnectedDevice;
import com.teambugallergy.resourceshare.constants.Resources;

/**
 * This Activity is invoked by ResoueceListActivity <i>AFTER BELOW
 * PROCESSES</i>. onClick of 'Access Resource' button, will send a 'Resource
 * Access Request' message to Provider devices and start the
 * SeekerCameraActivity. ***Set the callerHandler for objects in
 * potential_provider_list as Handler of this class, i.e
 * seekerCameraActivityHandler.*** Display appropriate messages to user based on
 * the messages received from potential_provider_list.
 * 
 * @author TeamBugAllergy 06-05-2014
 */
public class SeekerCameraActivity extends Activity {

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
	private static Context seekerCameraActivityContext;

	/**
	 * To capture the image from camera.
	 */
	private static Button take_picture;

	/**
	 * TextView to display the sharing status.
	 */
	private static TextView sharing_status;

	/**
	 * Button to stop sharing .
	 */
	private static Button stop_sharing;

	/**
	 * Dialog that says the user to wait till the image is saved.
	 */
	private static CustomDialog dialog;

	/**
	 * Number of potential provider devices that are currently available.
	 */
	private static int potential_provider_num = 0;

	/**
	 * Number of IMAGE_DATA messages received. <i>potential_provider_num</i> and
	 * this variable are used to close the waiting diaolg after receiveing the
	 * image data from all the providers.
	 */
	private static int image_data_message_num = 0;
	
	/**
	 * Number of times, that the user has clicked the 'take picture' button. It is used to name the image files at seeker device.
	 */
	private static int num_images_taken = 0;
	
	// -----------------------------------------------------------------------------------
	/**
	 * Handler to receive messages.
	 */
	private static Handler seekerCameraActivityHandler = new Handler() {

		public void handleMessage(Message msg) {

			final int sender_index = msg.arg2;

			LogMsg("Message received");

			// message that informs the SHARING_STATUS of the camera
			if (msg.what == Resources.SHARING_STATUS) {
				if (Integer.parseInt(msg.obj.toString()) == Resources.SHARING_STARTED) {
					LogMsg(potential_provider_list[sender_index].getDevice()
							.getName() + " has started sharing the camera");

					// display it in sharing_status
					sharing_status
							.setText(potential_provider_list[sender_index]
									.getDevice().getName()
									+ " has started sharing the camera");
				} else// if( Integer.parseInt( msg.obj.toString() ) ==
						// Resources.SHARING_STOPPED)
				{
					/*
					 * //Disable the UI to allow the user to start using the
					 * camera :) take_picture.setEnabled(false);
					 * 
					 * //display the button to stop sharing
					 * stop_sharing.setEnabled(false);
					 */
					LogMsg(potential_provider_list[sender_index].getDevice()
							.getName() + " has stopped sharing the camera");

					// display it in sharing_status
					sharing_status
							.setText(potential_provider_list[sender_index]
									.getDevice().getName()
									+ " has stopped sharing the camera");

					// ****IMP*****
					// Note down this potential_provider_device
					// because ONLY this provider should not be sent messages to
					// take the pictures.

					// remove this provider and shift remaining devices in the
					// array to left
					removeProviderDeviceFromList(sender_index);
				}

			}

			// if the message is IMAGE_DATA
			else if (msg.what == Resources.IMAGE_DATA) {

				// keep track of the number of providers that have sent the
				// IMAGE_DATA
				image_data_message_num++;

				// set file destination and file name
				File destination = new File(
						Environment.getExternalStorageDirectory(),
						potential_provider_list[sender_index].getDevice()
								.getName() + num_images_taken + ".jpg");
			
				//data received by the provider device
				byte[] data = (byte[]) msg.obj;
				
				try {
					Bitmap userImage = BitmapFactory.decodeByteArray(data, 0,
							data.length);

					LogMsg("Image  data length: " + data.length);

					//if bitmap is decoded successfully
					if (userImage != null) { 
						// set file out stream
						FileOutputStream out = new FileOutputStream(destination);
						
						// set compress format quality and stream
						userImage.compress(Bitmap.CompressFormat.JPEG, 40, out);

						//TODO: If you do not want to compress the image,so make it 100 instead of 30
						// set compress format quality and stream
						//userImage.compress(Bitmap.CompressFormat.JPEG, 100, out);

						
						LogMsg("Saved the image");

						Toast.makeText(seekerCameraActivityContext,
								"Image has been saved.", Toast.LENGTH_SHORT).show();
						
						// also recycle the userImage
						if (userImage != null)
							userImage.recycle();

					} else {
						LogMsg("Image couldn't be saved");
						
						Toast.makeText(seekerCameraActivityContext,
								"Image couldn't be saved.", Toast.LENGTH_LONG).show();
					}
				} catch (FileNotFoundException e) {

					LogMsg("ERROR: In saving the image- " + e);
				} catch (Exception e) {
					LogMsg("ERROR: In saving the image data- " + e);
				}

				// close the custom dialog
				// ONLY if all the providers have sent the IMAGE_DATA message
				if (potential_provider_num == image_data_message_num) {
					dialog.closeDialog();
					LogMsg("Closing the dialog IMAGE_DATA");
					// Only after all the potential provider devices send this
					// message then close the dialog
				}

			}

			// message is RESOURCE_ACCESS_REQUEST
			else if (msg.what == Resources.RESOURCE_ACCESS_REQUEST) {

				// if provider has accepted the resource access
				if (Integer.parseInt(msg.obj.toString()) == Resources.RESOURCE_ACCESS_GRANTED) {

					// display a message
					Toast.makeText(
							seekerCameraActivityContext,
							potential_provider_list[msg.arg2].getDevice()
									.getName()
									+ "has accepted the access to Camera",
							Toast.LENGTH_LONG).show();

					LogMsg(potential_provider_list[msg.arg2].getDevice()
							.getName() + "has accepted the access to Camera");

					// Enable the UI to allow the user to start using the camera
					// :)
					take_picture.setVisibility(View.VISIBLE);

					// display the message
					sharing_status.setText(potential_provider_list[msg.arg2]
							.getDevice().getName()
							+ "has started sharing Camera");

					// display the button to stop sharing
					stop_sharing.setVisibility(View.VISIBLE);

					//TODO:Note- Below line is commented because there wer 2 reader threads at a time...
					// Wait for SHARING_STATUS messages from provider devices
					//potential_provider_list[msg.arg2].receiveData();
					
				}

				// if provider has denied the resource access
				else if (Integer.parseInt(msg.obj.toString()) == Resources.RESOURCE_ACCESS_DENIED) {
					// display a message
					Toast.makeText(
							seekerCameraActivityContext,
							potential_provider_list[msg.arg2].getDevice()
									.getName()
									+ "has denied the access to Camera",
							Toast.LENGTH_LONG).show();

					LogMsg(potential_provider_list[msg.arg2].getDevice()
							.getName() + "has denied the access to Camera");

					// Ask the user if he/she wants to resend the
					// RESOURCE_ACCESS_REQUEST message and wait for the reply
					// ONLY to this particular Potential Provider.
					AlertDialog confirmResourceId = new AlertDialog.Builder(
							seekerCameraActivityContext)
							// set message, title, and icon
							.setTitle("Retry access.")
							.setMessage(
									potential_provider_list[msg.arg2]
											.getDevice().getName()
											+ " has denied to share the Camera. Would you like to try again?")

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
															+ ":" + Resources.CAMERA)
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
											// not be sent messages to take the
											// pictures on the camera.

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
				LogMsg("Unexpected message received in this Handler.");
				LogMsg("msg.wht:" + msg.what + ", msg.obj:" + msg.obj);
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
				
		// same layout is also used by ProviderFlashActivity
		setContentView(R.layout.activity_camera);

		// This toggle button is made visible only after getting a
		// RESOURCE_ACCESS_GRANT message
		take_picture = (Button) findViewById(R.id.take_picture);
		take_picture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// if take_picture has been clicked
				if (v.getId() == take_picture.getId()) {
					// re-intialize every time a request is sent to all the
					// providers
					image_data_message_num = 0;
					potential_provider_num = 0;
					// abouve variables are used in IMAGE_DATA msg handler block

					//keep the number of images clicked. It is used to name the images
					num_images_taken++;
					
					// for all the potential provideres
					for (int i = 0; potential_provider_list[i] != null; i++) {
						// send the TAKE_PICTURE message to all the potential
						// providers to take the picture
						potential_provider_list[i]
								.sendData((Resources.CAMERA_CONTROL + ":" + Resources.TAKE_PICTURE)
										.getBytes());
						LogMsg("Sending TAKE_PICTURE to potential provider devices");
						// Toast.makeText(seekerCameraActivityContext,
						// "Taking the picture", Toast.LENGTH_SHORT).show();

						//wait for IMAGE_DATA from the all the provider devices
						
						potential_provider_list[i].receiveData();
						LogMsg("Waiting for IMAGE_DATA from " + potential_provider_list[i].getDevice().getName());
						
						
						sharing_status.setText("Picture has been taken.");

						// count the number of potential providers
						potential_provider_num++;

					}

					// display a CustomDialog with ProgressBar
					dialog = new CustomDialog(seekerCameraActivityContext,
							"Saving picture...",
							"Image is being saved.Please wait...");
					// display the dialog
					dialog.show();
					// This dialog will be closed once this activity receives
					// IMAGE_SAVED message from all of the potential providers

				}
			}
		});

		// TextView to disply the sharing status
		sharing_status = (TextView) findViewById(R.id.sharing_status);
		sharing_status.setVisibility(View.VISIBLE);
		sharing_status
				.setText("Waiting for Access Permission from Provider devices.");

		// button to stop the sharing
		stop_sharing = (Button) findViewById(R.id.stop_sharing);
		stop_sharing.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// send a message STOP_SHARING to devices in
				// potential_provider_list[]
				for (int i = 0; potential_provider_list[i] != null; i++) {
					// message will have 'SHARING_CONTROL:STOP_SHARING'.
					potential_provider_list[i]
							.sendData((Resources.SHARING_CONTROL + ":" + Resources.STOP_SHARING)
									.getBytes());

					//wait for future messages from the provider device
					potential_provider_list[i].receiveData();
					LogMsg("Waiting for messages from the provider");
					
				}
				
			}
		});

		// save the context statically
		seekerCameraActivityContext = this;

		// get the potential_provider_list[] array from ResourceListActivity
		potential_provider_list = ResourceListActivity.getPotentialDeviceList();

		int i = 0;
		for (i = 0; potential_provider_list[i] != null; i++) {
			// set the device index of all the provider devices according to
			// index in 'potential_provider_list[]'
			potential_provider_list[i].setDeviceIndex(i);

			// set the callerHandler of objects in potential_provider_list[] to
			// seekerCameraActivityHandler
			potential_provider_list[i]
					.setCallerHandler(seekerCameraActivityHandler);

			LogMsg("sending the message RESOURCE_ACCESS_REQUEST");
			// data in the form 'what:data' i.e
			// 'RESOURCE_ACCESS_REQUEST:resource_id'
			potential_provider_list[i]
					.sendData((Resources.RESOURCE_ACCESS_REQUEST + ":" + Resources.CAMERA)
							.getBytes());

			// start waiting for reply messages from potential_provider_list[].
			potential_provider_list[i].receiveData();
		}
		if (i == 0) {
			LogMsg("There are No potential Provider devices.");
		}

	};

	/**
	 * Makes the ConnectedDevice object in <i>potential_provider_list[]</i>
	 * null. So that it will not be used in future. So makes that object null
	 * and shift the devices ahead of it in the array one position left.
	 * 
	 * @param position
	 *            index of ConnectedDevice object in the
	 *            potential_provider_list[] array which has to be removed from
	 *            list.
	 */
	private static void removeProviderDeviceFromList(int position) {

		Toast.makeText(
				seekerCameraActivityContext,
				potential_provider_list[position].getDevice().getName()
						+ " has been disconnected.", Toast.LENGTH_LONG).show();

		// message to tell the provider to disconect itself.
		potential_provider_list[position]
				.sendData((Resources.DISCONNECT + ":" + 0).getBytes());

		// make the 'position'th object null
		potential_provider_list[position] = null;

		// from 'i = position+1'
		int i;
		for (i = position + 1; potential_provider_list[i] != null; i++) {
			// shift one position left,
			potential_provider_list[i - 1] = potential_provider_list[i];

			// also change the device_index in ConnectedDvice object
			potential_provider_list[i - 1].setDeviceIndex((i - 1));

		}
		// remove the repeated object at the end of array
		potential_provider_list[i - 1] = null;

		LogMsg("One provider device has been removed.");

		// LogMsg("potential_provider_list[] has:");
		// for(i = 0; potential_provider_list[i] != null; i++)
		// LogMsg(potential_provider_list[i].getDevice().getName() + "");

		// if there are no objects in the array,
		// i.e If there are no Potential provider devices, goto MainActivity and
		// finish this activity.
		if (potential_provider_list[0] == null) {
			// start the MainActivity
			// Intent intent = new Intent(seekerFlashActivityContext,
			// MainActivity.class);
			// seekerFlashActivityContext.startActivity(intent);

			// finish() this activity
			((Activity) seekerCameraActivityContext).finish();
		}

	}

	/**
	 * To disconnect from all the potential provider devices by sending them
	 * 'DISCONNECT'.
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();

		// remove all the devices from the potential_provider_list[]
		for (int i = 0; potential_provider_list[i] != null; i++) {
			// removes each of the provider device and sends them 'DISCONNECT'
			// message.
			removeProviderDeviceFromList(i);
		}
		LogMsg("All the providers have been removed and disconnected.");

	}

	private static void LogMsg(String msg) {
		Log.d("SeekerCameraActivity", msg);
	}

}
