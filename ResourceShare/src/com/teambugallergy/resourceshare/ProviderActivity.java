package com.teambugallergy.resourceshare;

import com.teambugallergy.resourceshare.bluetooth.ConnectedDevice;
import com.teambugallergy.resourceshare.bluetooth.RemoteProviderDevice;
import com.teambugallergy.resourceshare.bluetooth.RemoteSeekerDevice;
import com.teambugallergy.resourceshare.bluetooth.Scanner;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
 * @author Adiga
 * 
 */
public class ProviderActivity extends Activity implements OnClickListener {

	/**
	 * TextView to display information about the connected seeker device.
	 */
	private static TextView connected_device_info;

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
	 * Start listening to request seeker device. Create a dialog to wait until a
	 * request is found.
	 */

	/**
	 * A BluetoothDevice that has been connected to successfully.
	 */
	private static ConnectedDevice connected_device;

	/**
	 * Start listening to requests from seeker devices. Display a dialog with
	 * progress bar to show the waiting.
	 */

	// -----------------------------------------------------------------------------------

	/**
	 * Handler to receive messages.
	 */
	private static Handler providerActivityHandler = new Handler() {

		public void handleMessage(Message msg) {

			// if the message is from RemoteSeekerDevice
			if (msg.what == RemoteSeekerDevice.CONNECTION_STATUS) {

				// if the connection was successful
				if (msg.obj.equals(RemoteSeekerDevice.CONNECTION_SUCCESS)) {

					// ***STOP LISTENING TO FURTHRE REQUESTS FROM OTHER SEEKER
					// DEVICES***
					seeker_device.stopListeningToDevice();

					// Create a connected device using this connection.
					connected_device = new ConnectedDevice(
							seeker_device.getDevice(),
							seeker_device.getSocket(), providerActivityHandler);

					// Display the device information to user
					connected_device_info.setText("Device: "
							+ connected_device.getDevice().getName());

					// Close the dialog and display a toast
					dialog.closeDialog();
					Toast.makeText(providerActivityContext, "Device found.",
							Toast.LENGTH_SHORT).show();

					LogMsg("Detected device: "
							+ connected_device.getDevice().getName());
				}
				// if the connection was failed
				else if (msg.obj.equals(RemoteSeekerDevice.CONNECTION_FAILURE)) {

					// Clear the TextView
					connected_device_info.setText("");

					dialog.changeTitle("Error");
					dialog.changeDialog("No requests found. Please make sure that other device has sent the request.");
					// Hide the ProgressBar
					dialog.showProgressBar(false);
					// Display the OK Button
					dialog.showOkButton(true);

					LogMsg("No device found.");
				}

			}
		}

	};

	// -----------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_provider);

		// Context of this Activity used by inner classes
		providerActivityContext = this;

		// TextView to display the informaion about connected seeker device
		connected_device_info = (TextView) findViewById(R.id.connected_device);

		// TODO: onStart() will start listening to requests
		seeker_device = new RemoteSeekerDevice(providerActivityHandler);

		// start listening to rwquests
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

			// Untill that, Display the dialog object of CustomDialog
			dialog = new CustomDialog(this, "Waitiing for request...",
					"Listening for requests from devices. Please wait...");
			// display the dialog
			dialog.show();

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

	@Override
	public void onClick(View arg0) {

	}

	private static void LogMsg(String msg) {
		Log.d("ProviderActivity", msg);
	}
}
