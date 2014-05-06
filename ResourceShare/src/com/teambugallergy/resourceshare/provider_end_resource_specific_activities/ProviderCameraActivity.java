package com.teambugallergy.resourceshare.provider_end_resource_specific_activities;

import com.teambugallergy.resourceshare.R;
import com.teambugallergy.resourceshare.activities.ProviderActivity;
import com.teambugallergy.resourceshare.bluetooth.ConnectedDevice;
import com.teambugallergy.resourceshare.constants.Resources;
import com.teambugallergy.resourceshare.resources.MyCamera;
import com.teambugallergy.resourceshare.resources.Resource;

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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This Activity is invoked by ProviderActivity WHENEVER Seeker sends 'Resource
 * Access Request' message. First try to Acquire and lock the Requested
 * Resource. Send the result of this process to Seeker Device. This activity
 * will have buttons to control the sharing the Camera.
 * 
 * @author Adiga@TeamBugAllergy
 *  30-04-2014
 */
public class ProviderCameraActivity extends Activity{

	/**
	 * Context of this object.
	 */
	private static Context providerCameraActivityContext;

	/**
	 * Object representing the connected Seeker device.
	 */
	private static ConnectedDevice connected_seeker_device;

	/**
	 * Object of Camera class, to control the camera resource.
	 */
	private static MyCamera camera;
	
	/**
	 * TextView to display the sharing status.
	 */
	private static TextView sharing_status;
	
	/**
	 * Button to stop sharing .
	 */
	private static Button stop_sharing;
	
	/**
	 * Button to display the last clicked image.
	 */
	private static Button display_clicked_image;
	
	/**
	 * Name of the previously saved image
	 */
	private static String clicked_image_name = null;
	
	/**
	 * ImageView to display last clicked image
	 */
	private static ImageView clicked_image;
	
	// -----------------------------------------------------------------------------------
		/**
		 * Handler to receive messages.
		 */
		private static Handler providerCameraActivityHandler = new Handler() {

			public void handleMessage(Message msg) {

			//***This if()else should be in the beginning :) 	
			// message is CAMERA_CONTROL 
			if(msg.what == Resources.CAMERA_CONTROL)
			{
				//If it is telling to take the picture
				if(Integer.parseInt( msg.obj.toString() ) == Resources.TAKE_PICTURE)
				{
					//take the picture
					camera.takePicture();
					LogMsg("Taking the picture.");
					//Toast.makeText(providerCameraActivityContext, "Taking the picture.", Toast.LENGTH_SHORT).show();
				}
				
				//again wait for messages from seker device
				connected_seeker_device.receiveData();
			}
			//if the message is IMAGE_SAVED
			else if(msg.what == Resources.IMAGE_SAVED)
			{
				
				//msg.obj contains the name of the image that has been saved.
				
				//FORWARD the same message to connected seeker device. 
				//data sent 'IMAGE_SAVED:image_name'
				connected_seeker_device.sendData( (Resources.IMAGE_SAVED + ":" + msg.obj.toString() ).getBytes() );
				LogMsg("Forwarding the IMAGE_SAVED message");
				
				//save the name of the image , It is used to display the image later
				clicked_image_name = msg.obj.toString();
				
				//display the button to display the image
				display_clicked_image.setVisibility(View.VISIBLE);
				//display the image
				clicked_image.setVisibility(View.VISIBLE);
				
				LogMsg("IMAGE_SAVED: "+clicked_image_name);
			}
			
			//if the message is SHARING_CONTROL
			else if(msg.what == Resources.SHARING_CONTROL)
			{
				//if it is STOP_SHARING
				if( Integer.parseInt( msg.obj.toString() ) == Resources.STOP_SHARING )
				{
				
				//stop sharing camera	
				stopSharingCamera();	
				
				LogMsg("Camera has been released");
				
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
				Toast.makeText(providerCameraActivityContext, connected_seeker_device.getDevice().getName() + " has been disconnected.", Toast.LENGTH_SHORT).show();

				//goback to MainActivity and finish() this activity
								
				//finish this activity
				((Activity) providerCameraActivityContext).finish();
				
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
							providerCameraActivityContext)
							// set message, title, and icon
							.setTitle("Access Permission.")
							.setMessage(
									connected_seeker_device.getDevice().getName()
											+ " is trying to access the Camera. Would you like to grant the permission?")

							.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {

										public void onClick(DialogInterface dialog,
												int whichButton) {

											//close the alertDialog
											dialog.dismiss();
											
											// try to acquire and lock the
											// resource.
											camera = new MyCamera(providerCameraActivityContext, providerCameraActivityHandler);
											
											LogMsg("Trying to acquire the Camera.");
											if(camera.acquireCamera() == true)
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
												
												// wait for CAMERA CONTROL MESSAGES (i.e messages to take the pictures)
												connected_seeker_device.receiveData();
												LogMsg("Waiting for Camera Control messages");
												
												//display the status to user
												sharing_status.setText("Camera is being used by " + connected_seeker_device.getDevice().getName());
												
												//display the button to stop sharing
												stop_sharing.setVisibility(View.VISIBLE);
												
											}
											else
											{
												//if Camera could'nt be acquired,
												//Display the mmessage to user
												Toast.makeText(providerCameraActivityContext, "Camera couldn't be acquired. Check any other process is using it.", Toast.LENGTH_LONG).show();
												
												//display the status to user
												sharing_status.setText("Camera couldn't be acquired. Check any other process is using it.");
												
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
											// camera

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

		// same layout is also used by SeekerCameraActivity
		setContentView(R.layout.activity_camera);

		// save the context statically
		providerCameraActivityContext = this;

		// get the connected_seeker_device object from ProviderActivity.
		connected_seeker_device = ProviderActivity.getConnectedSeekerDevice();

		if (connected_seeker_device != null) {
			// set the new callerHandler for this device object as
			// providerCameraActivityHandler
			connected_seeker_device
					.setCallerHandler(providerCameraActivityHandler);
			
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
					stopSharingCamera();
					}
				}
			);
			
			//image view to display the clicked image
			clicked_image = (ImageView)findViewById(R.id.clicked_image);
			
			//button to display the clicked image
			display_clicked_image = (Button)findViewById(R.id.display_clicked_image);
			display_clicked_image.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {

					if(v.getId() == display_clicked_image.getId())
					{
						if(clicked_image_name != null)
						{
							//resize the saved image and display the image 'clicked_image_name' in the ImageView.
							Bitmap	bmp = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/" + clicked_image_name);
			              if(bmp != null)
			              {
							int nh = (int) ( bmp.getHeight() * (512.0 / bmp.getWidth()) );
			            	Bitmap scaled = Bitmap.createScaledBitmap(bmp, 512, nh, true);
			            	clicked_image.setImageBitmap(scaled);
			            
			            	LogMsg("Displayed the image " + clicked_image_name);
			            	
			            	//recycle the bmp
			            	if(bmp != null)
			            	{
			            		bmp.recycle();
			            		bmp = null;
			            	}
			            	
							//hide the button
							display_clicked_image.setVisibility(View.GONE);
							//this will be made visible after getting another image from IMAGE_SAVED
			              }
						}
					
					}
				}	
			});
						
		} else {
			LogMsg("There is no Connected Seeker Device.");
		}

	}
	
	
	/**
	 * Stops the camera preview and releases it.
	 * Also sends the message to Seeker device stating that Resource is not Shared.
	 */
	private static void stopSharingCamera()
	{
		//release the camera
		camera.releaseCamera();
		
		//send a message to seeker device telling that resource is no more shared.
		//message "SHARING_STATUS:SHARING_STOPPED"
		connected_seeker_device.sendData( (Resources.SHARING_STATUS + ":" + Resources.SHARING_STOPPED).getBytes() );
		
		//display the message
		//sharing_status.setText("Sharing the camera has been stopped.");
	}
	
	/**
	 * To disconnect from seeker device and release the Camera.
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		//notify the seeker about this
		AlertDialog confirmOnBack = new AlertDialog.Builder(
				providerCameraActivityContext)
				// set message, title, and icon
				.setTitle("Going back.")
				.setMessage( "Sharing has been completed." )

				.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int whichButton) {
								
								stopSharingCamera();
								// stop the reading thread
								if (connected_seeker_device != null) {
									
									// terminate the connection
									connected_seeker_device.disconnect();
									LogMsg("Disconnected from the " + connected_seeker_device.getDevice().getName());
									
								}
								
								//release the flash
								if(camera != null)
								{
									//release the camera
									camera.releaseCamera();
								}
								
								LogMsg("User has confirmed.");
								dialog.dismiss();
								
								((Activity) providerCameraActivityContext).finish();
								
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
		if(camera != null)
		{
			//release the camera
			camera.releaseCamera();
		}
		
	}

	private static void LogMsg(String msg) {
		Log.d("ProviderCameraActivity", msg);
	}
}
