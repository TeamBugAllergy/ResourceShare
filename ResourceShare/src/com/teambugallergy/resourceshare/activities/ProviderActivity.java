package com.teambugallergy.resourceshare.activities;

import com.teambugallergy.resourceshare.R;
import com.teambugallergy.resourceshare.bluetooth.ConnectedDevice;
import com.teambugallergy.resourceshare.bluetooth.RemoteSeekerDevice;
import com.teambugallergy.resourceshare.bluetooth.ServerThread;
import com.teambugallergy.resourceshare.constants.Resources;
import com.teambugallergy.resourceshare.resources.MyAccelerometer;
import com.teambugallergy.resourceshare.resources.MyCamera;
import com.teambugallergy.resourceshare.resources.MyFlash;
import com.teambugallergy.resourceshare.resources.MyWifi;
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
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This Activity just listens to requests from Seeker devices. It will wait for
 * request from a seeker device. A progress bar is displayed untill a device is
 * found. Once a request is accepted, <b>further requests are blocked by
 * stopListeningToDevice() method</b>, and it will save the ConnectedDevice
 * object associated with that device. Then Seeker device sends the Resource Id.
 * The user will be prompted to accept to share that resource. The result will
 * be sent to the Seeker Device. <i> <br/>
 * -------------------------------------- <br/>
 * Constants of this class starts with 9 <br/>
 * -------------------------------------- <br/>
 * </i>
 * 
 * 06-04-2014
 * 
 * @author TeamBugAllergy
 * 
 */
public class ProviderActivity extends Activity {

	/**
	 * TextView to display information about the connected seeker device.
	 */
	private static TextView connected_device_info;

	/**
	 * Resource Id of the requested resource.
	 */
	private static int resource_id = -1;

	/**
	 * Dialog to display 'listenign for rquests' dialog. Dialog will be
	 * terminated once a device is found.
	 */
	private static CustomDialog dialog;

	/**
	 * Context of this Activity used by any inner classes
	 */
	private static Context providerActivityContext;

	/**
	 * An object that represents the remote seeker device. Any operations to be
	 * performed on that device are performed on this object.
	 */
	private static RemoteSeekerDevice seeker_device;

	/**
	 * Availability of the requested resource. Its value may be
	 * RESOURCE_AVAILABLE,UNAVIALABLE OR BUSY.
	 */
	private static int resource_availability = -1;

	/**
	 * A BluetoothDevice that has been connected to successfully.
	 */
	private static ConnectedDevice connected_device;

	/**
	 * Tells whether Resource Specific Activity has been started or not. If
	 * Resource Specific Activity is not started, then in onStop() method,
	 * "connection" will be disconnected, <b>to allow further connections</b>.
	 */
	private static Boolean started_resource_activity = false;

	// -----------------------------------------------------------------------------------

	/**
	 * Handler to receive messages.
	 */
	private static Handler providerActivityHandler = new Handler() {

		public void handleMessage(Message msg) {

			LogMsg("Message received");

			// if the message is from RemoteSeekerDevice
			if (msg.what == RemoteSeekerDevice.CONNECTION_STATUS) {

				// if the connection was successful
				if (Integer.parseInt(msg.obj.toString()) == RemoteSeekerDevice.CONNECTION_SUCCESS) {

					// LogMsg("INSIDE:CONNECTION_SUCCESS");

					// if(seeker_device != null)
					// LogMsg("HERE:seeker_device is not null");

					// if(seeker_device.getSocket() != null)
					// LogMsg("HERE:socket=" +
					// seeker_device.getSocket().toString());

					// ******************************
					// Because THERE WAS A PROBLEM WITH seeker_device.getSocket
					// and getDevice, THEY WERE null :)
					// So directly use the ServerThread's socket
					// ******************************
					seeker_device.setSocket(ServerThread.socket);
					seeker_device.setDevice(ServerThread.socket
							.getRemoteDevice());

					// Create a connected device using this connection.
					connected_device = new ConnectedDevice(
							seeker_device.getDevice(),
							seeker_device.getSocket(), providerActivityHandler);

					// set the device index to 0
					connected_device.setDeviceIndex(0);

					// Display the device information to user
					connected_device_info.setText("Seeker Device: "
							+ connected_device.getDevice().getName());

					/*
					 * // Close the dialog and display a toast
					 * dialog.closeDialog();
					 * Toast.makeText(providerActivityContext, "Device found.",
					 * Toast.LENGTH_SHORT).show();
					 */
					// don't close the dialog. just change the contents to show
					// that it is waiting for resource_id from the connected
					// seeker device

					dialog.changeTitle("Waiting for Resource Id...");
					dialog.changeDialog("Please wait till a resource is requested.");

					// read the Resource Id from connected_device
					connected_device.receiveData();

					LogMsg("Detected device: "
							+ connected_device.getDevice().getName());
				}
				// if the connection was failed
				else if (msg.obj.equals(RemoteSeekerDevice.CONNECTION_FAILURE)) {

					// LogMsg("INSIDE:CONNECTION_FAILURE");

					// Clear the TextView
					connected_device_info.setText("");

					dialog.changeTitle("Error");
					dialog.changeDialog("No requests found. Please make sure that other device has sent the request.");
					// Hide the ProgressBar
					dialog.showProgressBar(false);
					// Display the OK Button
					dialog.showOkButton(true);

					// TODO: Restart the process of listening to devices.

					LogMsg("No device found.");
				}

			}
			// Handle a message from ConnectedDevice which has 'what' as
			// REQUESTING_RESOURCE_ID
			// After receiving such message just close the waiting dialog and
			// display a confirm dialog to seek the permission of user

			// If the user selects 'NO' then just close the confirm
			// message and start waiting for resource_id again.
			else if (msg.what == Resources.REQUESTING_RESOURCE_ID) {

				// LogMsg("INSIDE:REQUEST_RESOURCE_ID");

				// Stop or close the waiting dialog
				// these statements are put inside if(resource_available){}
				// block
				// dialog.closeDialog();

				// msg.obj has the requested resource_id, extract it and save it
				resource_id = Integer.parseInt(msg.obj.toString());

				LogMsg("resource_id has been received :)." + resource_id);

				// ONLY when the Resource is requested, then check if it is busy
				// or not.
				// Now just check if it is present or not

				// check for availability of that resource_id
				// and
				switch (resource_id) {
				case Resources.FLASH:
					//just to check for availability
					resource_availability = new MyFlash(providerActivityContext)
							.availability();
					break;
					
				case Resources.CAMERA:
					//just to check for availability
					resource_availability = new MyCamera(providerActivityContext, null)
							.availability();
					break;
				
				case Resources.WIFI:
					//just to check for availability
					resource_availability = new MyWifi(providerActivityContext)
							.availability();
					break;
					
				case Resources.ACCELEROMETER:
					//just to check for availability
					resource_availability = new MyAccelerometer(providerActivityContext)
							.availability();
					break;	
					
				// TODO: for other the resources

				default:

					// No resource :(
					resource_availability = -1;
					break;
				}

				LogMsg("Resource availability:" + resource_availability);

				// Resource is Available
				if (resource_availability == Resources.RESOURCE_AVAILABLE) {

					// and display a confirm message
					AlertDialog confirmResourceId = new AlertDialog.Builder(
							providerActivityContext)
							// set message, title, and icon
							.setTitle("Accept")
							.setMessage(
									"Are you willing to share the "
											+ new Resource()
													.getResourceName(resource_id)
											+ "?")

							.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											
											// if it is available,
											// Stop or close the waiting dialog
											 ProviderActivity.dialog.closeDialog();
											
											// Send a message to
											// SeekerDevice
											// telling that resource_id is
											// available
											// and Provider is willing to share
											// the
											// resource

											// send data in the form 'what:data'
											connected_device
													.sendData((Resources.REQUEST_STATUS
															+ ":" + Resources.REQUEST_ACCEPTED)
															.getBytes());

											LogMsg("Sending message to Seeker:'Resource is available and accepted to share.'");
											dialog.dismiss();

											// goto Resource Specific Activity
											// and finish this activity
											Intent intent = new Resource()
													.getIntetToResourceActivity(
															resource_id,
															providerActivityContext,
															1);
											providerActivityContext
													.startActivity(intent);

											// To tell the onStop() that
											// Resource Specific Activity has
											// been started and "connection"
											// SHOULD NOT be terminated.
											started_resource_activity = true;

											LogMsg("going to resource specific activity and finishing this activity.");

											// finish this activity
											((Activity) providerActivityContext)
													.finish();

										}

									})

							.setNegativeButton("No",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											
											// Send a message to
											// SeekerDevice
											// telling that Provider is NOT
											// WILLING
											// to share the resource.
											// and close this confirm dialog and
											// start listening to resource_id
											// requests

											// send data in the form 'what:data'
											connected_device
													.sendData((Resources.REQUEST_STATUS
															+ ":" + Resources.REQUEST_REJECTED)
															.getBytes());

											// and start listening to
											// resource_id
											// requests again.
											LogMsg("Sending message to Seeker:'Resource is available but rejected to share.'");
											dialog.dismiss();

										}
									}).create();
					
					// display the dialog on the screen
					confirmResourceId.show();

				}
				// Resource is Unavailable
				else if (resource_availability == Resources.RESOURCE_UNAVAILABLE) {
					// Send the message that tells the seeker that
					// resource_id
					// is not available
					// and tell the user at Provider end that Seeker has
					// requested a Resource which is Not Present,
					// and close this confirm dialog and start listening to
					// resource_id requests again

					// send the RESOURCE_UNAVAILABLE message
					connected_device
							.sendData((Resources.RESOURCE_STATUS + ":" + Resources.RESOURCE_UNAVAILABLE)
									.getBytes());
					LogMsg("Sending message to Seeker:'Resource is not present on the provider device.'");

					Toast.makeText(
							providerActivityContext,
							"Requested resource is not present.Waiting for other Resource Id.",
							Toast.LENGTH_LONG).show();
					// No need:- connected_device.receiveData();

				}
				// Resource is Busy
				else if (resource_availability == Resources.RESOURCE_BUSY) {
					// Send the message that tells the seeker that resource_id
					// is currently busy
					// and tell the user at the Provider end that Seeker has
					// requested a Resource which is Busy,
					// and close this confirm dialog and start listening to
					// resource_id requests again

					// send the RESOURCE_BUSY message
					connected_device
							.sendData((Resources.RESOURCE_STATUS + ":" + Resources.RESOURCE_BUSY)
									.getBytes());
					LogMsg("Sending message to Seeker:'Resource is busy.'");

					Toast.makeText(
							providerActivityContext,
							"Requested resource is busy.Waiting for other Resource Id.",
							Toast.LENGTH_LONG).show();
					// No need:- connected_device.receiveData();

				}

			}
			// Unexpected messages
			else {
				LogMsg("Unexpected message received in this Handler: msg.what="
						+ msg.what);
			}

		}

	};

	// -----------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_provider);

		// LogMsg("INSIDE:onCreate");
		
		//Intial setup
				//Keep the screen on
				//This flag will be cleared when this activity is destroyed
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Context of this Activity used by inner classes
		providerActivityContext = this;

		// TextView to display the information about connected seeker device
		connected_device_info = (TextView) findViewById(R.id.connected_device);

		// below calls will be made in onResume()
		// seeker_device = new RemoteSeekerDevice(providerActivityHandler);
		// startListening();

		// Until that, Display the dialog object of CustomDialog
		dialog = new CustomDialog(this, "Waiting for request...",
				"Listening for requests from devices. Please wait...");
		// display the dialog
		dialog.show();
		
		//initializing
		started_resource_activity = false;
	}

	/**
	 * Starts listening for connection requests from seeker devices and
	 * displays a dialog with progress bar.
	 */
	private void startListening() {

		// LogMsg("INSIDE:startListening");

		// start listening to requests
		// ONLY if server_socket has been obtained successfully
		if (seeker_device.obtainServerSocket() == true) {

			// Listen to request from the remote seeker device.
			// The result or status of the connection is sent through message by
			// the ServerThread.
			// RESULT OF CONNECTION WILL BE SENT TO CALLER LATER.(By Handler)
			seeker_device.startListeningToDevice();// stopListeningToDevice()
													// when you get device(Only
													// one device can be
													// connected).

			// This dialog will be closed automatically if the connection is
			// successfull
			// Else the OK button will be displayed.
		}
		// Display an error in Toast
		else {
			LogMsg("Error: Could not obtain server_socket.");
			Toast.makeText(
					this,
					"Error in starting server. Try restarting the application.",
					Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * Returns a reference to the object of connected Seeker device used by
	 * ProviderDevice Activity.
	 * 
	 * @return Reference to <i>connected_device</i>.
	 */
	public static ConnectedDevice getConnectedSeekerDevice() {
		return connected_device;
	}

	@Override
	protected void onResume() {
		super.onResume();

		// LogMsg("INSIDE:onResume");

		seeker_device = new RemoteSeekerDevice(providerActivityHandler);
		startListening();
	}

	/**
	 * Stops listening to connection requests from seeker devices
	 */
	@Override
	protected void onStop() {
		super.onStop();

		// LogMsg("INSIDE:onStop");

		// If this Activity is being stopped without going to Resource Specific
		// Activity,
		// Then DISCONNECT the connection to allow future connections
		if (started_resource_activity == false) {
			// If user has not opened Resource Specific Activity successfully,
			// then stop the reading thread.
			if (connected_device != null) {

				// terminate the connection
				connected_device.disconnect();
			}
		}
	}

	private static void LogMsg(String msg) {
		Log.d("ProviderActivity", msg);
	}
}
