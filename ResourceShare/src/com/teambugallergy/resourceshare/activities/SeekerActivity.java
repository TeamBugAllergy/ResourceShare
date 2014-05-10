package com.teambugallergy.resourceshare.activities;

import java.util.ArrayList;

import com.teambugallergy.resourceshare.R;
import com.teambugallergy.resourceshare.bluetooth.ConnectedDevice;
import com.teambugallergy.resourceshare.bluetooth.RemoteProviderDevice;
import com.teambugallergy.resourceshare.bluetooth.Scanner;
import com.teambugallergy.resourceshare.list.*;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * This class Lists all the nearby bluetooth enabled devices. It also allows
 * user to select devices from the list tries to connect to those devices. This
 * Activity checks if the remote device has the same application installed by
 * <i>trying to connect that device using RemoteProviderDevice.connectToDevice()
 * method. </i> <b>This Activity passes an array of ConnectedDevice to next
 * Activity </b> It also provides its own OnItemClickListner for ListView.
 * 
 * This class uses Scanner class to obtain an array of BluetoothDevice that re
 * nearby.
 * 
 * <i> <br/>
 * -------------------------------------- <br/>
 * Constants of this class starts with 8 <br/>
 * -------------------------------------- <br/>
 * </i> 06-04-2014
 * 
 * @author Adiga@TeamBugAllergy
 * 
 */
public class SeekerActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	// Handler to receive the device_list
	// activity_device_list to display the list of devices
	// Connecting to devices when a item in the list is clicked by the
	// user

	/**
	 * Maximum of 10 devices can be stored in the connected_device_list[]. i.e
	 * Maximum of 10 Resource Providers are allowed.
	 */
	private final int MAX_CONNECTED_DEVICES = 10;

	/**
	 * Used to display the connection status of the device in the list.
	 */
	public final static String STATUS_UNKOWN = "Unkown";
	public final static String STATUS_CONNECTED = "Connected";
	public final static String STATUS_DISCONNECTED = "Unavailable";

	/**
	 * Used for background color of each row.
	 */
	public static int WHITE;
	public static int RED;
	public static int GREEN;

	/**
	 * Refresh Button to Restart the scanning for devices.
	 */
	private Button refresh;

	/**
	 * Button to goto next Activity along with the an array of ConnectedDevices
	 * objects
	 */
	private static Button next;

	/**
	 * List objcet that has methods for addItem, removeItem and
	 * onItemClickListners for List Items
	 */
	private static Lists list;

	/**
	 * Scanner object to scan and obtain a list of BluetoothDevice objects.
	 */
	private Scanner scanner;

	/**
	 * An object that represents the remote provider device. Any operations to
	 * be performed on that device are performed on this object.
	 * 
	 */
	private static RemoteProviderDevice provider_device;

	/**
	 * Array of BluetoothDevice Objects, received from Scanner object.
	 */
	private static BluetoothDevice[] device_list;

	/**
	 * An array of BluetoothDevices that are transfered to next Activity. This
	 * member is used by ResourceListActivity also.
	 */
	private static ConnectedDevice[] connected_device_list;

	/**
	 * Dialog object used to display ProgressBar while waiting for Connecting a
	 * device.
	 */
	private static CustomDialog dialog;

	/**
	 * Context of this Activity used by any inner classes
	 */
	private static Context seekerActivityContext;

	/**
	 * Global index for connected_device_list[] array. It is incremented each
	 * time a successfully connected device is added.
	 */
	private static int connected_device_num = 0;

	/**
	 * Index of the device that has been clicked to connect to the device.
	 * <b>This is used to set the connection status of that device when a
	 * message is received.</br> If there is no device waiting to be connected
	 * to (no dialog has been displyed), then the value will be -1.</b>
	 */
	private static int clicked_device_index = -1;

	// -----------------------------------------------------------------------------------
	/**
	 * Handler to receive messages.
	 */
	private static Handler seekerActivityHandler = new Handler() {

		public void handleMessage(Message msg) {

			// if the message is from Scanner
			if (msg.what == Scanner.DEVICE_LIST) {
				// Then it contains device_list[]

				// Clear all the Rows from the list to display the new rows
				list.clear();

				LogMsg("Received a message from Scanner.");

				// Get the device_list[] from the message and stor it in this
				// object's device_list
				device_list = (BluetoothDevice[]) msg.obj;

				// for each device in the array,
				for (int i = 0; device_list[i] != null; i++) {

					// search connected_device_list[] for device_list[i]
					int flag = 0;
					for (int j = 0; connected_device_list[j] != null; j++) {
						// if connected+device_list[] has this device
						if (connected_device_list[j].getDevice().equals(
								device_list[i])) {
							LogMsg("device already present in connected_device_list[]");
							flag = 1;
							break;
						}
					}
					if (flag == 0) {
						// if the device is not null,
						// add the new device_list[] into list
						list.addItem(new String[] { device_list[i].getName(),
								SeekerActivity.STATUS_UNKOWN, "0" });

						// and tell the adapter about changes
						// list.notifyDataSetChanged(); NO NEED, it has been
						// added
						// in list object

						LogMsg("device:" + device_list[i].getName());
					} else {
						// if the device is not null,
						// add the new device_list[] into list
						list.addItem(new String[] { device_list[i].getName(),
								SeekerActivity.STATUS_CONNECTED, "1" });

						LogMsg("Changing the color to green");

						// set the color of the row to GREEN
						list.changeColor(SeekerActivity.GREEN, i);

						// and tell the adapter about changes
						// list.notifyDataSetChanged(); NO NEED, it has been
						// added
						// in list object

						LogMsg("connected device:" + device_list[i].getName());
					}
				}

			}

			// if the message is from RemoteProviderDevice
			else if (msg.what == RemoteProviderDevice.CONNECTION_STATUS) {
				// if the connection was successful
				if (msg.obj.equals(RemoteProviderDevice.CONNECTION_SUCCESS)) {

					// Then create a new ConnectedDevice object with the
					// obtained connection and save it in the array.
					connected_device_list[connected_device_num] = new ConnectedDevice(
							provider_device.getDevice(),
							provider_device.getSocket(), seekerActivityHandler);
					connected_device_num++;

					// Also change the status bit to "1" to indicate that the
					// device has been connected.
					// clicked_device_index will have the index of the device
					// that has been clicked.
					list.getItem(clicked_device_index).setRowValues(2, "1");

					// Change the status of the device from "Unkown" or
					// "App not found" to "Connected"
					list.getItem(clicked_device_index).setRowValues(1,
							SeekerActivity.STATUS_CONNECTED);
					// list.notifyDataSetChanged(); NO NEED, it has been added
					// in list object

					// set the color of the row to GREEN
					list.changeColor(SeekerActivity.GREEN, clicked_device_index);

					LogMsg("Changed Connection_status to Connected for: "
							+ clicked_device_index);

					// and reset the index of clicked device.
					clicked_device_index = -1;

					// ONCE THE USE OF PROVIDER DEVICE IS OVER, SET IT TO NULL
					// FOR RE-USE BY onItemClickListner()
					provider_device = null;

					// Close the dialog and display a toast
					dialog.closeDialog();
					Toast.makeText(seekerActivityContext,
							"Connected to the device.", Toast.LENGTH_SHORT)
							.show();

					// Also set the visibilty of 'Next' button, if it is not
					// already visible
					if (!next.isShown()) {
						next.setVisibility(View.VISIBLE);
					}

				}
				// if the connection was failed
				else if (msg.obj
						.equals(RemoteProviderDevice.CONNECTION_FAILURE)) {

					// Also change the status bit to "0" to indicate that the
					// device has been connected.
					// clicked_device_index will have the index of the device
					// that has been clicked.
					list.getItem(clicked_device_index).setRowValues(2, "0");

					// Change the status of the device from "Unkown" or
					// "Connected" to "App not found"
					list.getItem(clicked_device_index).setRowValues(1,
							SeekerActivity.STATUS_DISCONNECTED);
					// list.notifyDataSetChanged(); NO NEED, it has been added
					// in list object

					// set the color of the row to RED
					list.changeColor(SeekerActivity.RED, clicked_device_index);

					// and reset the index of clicked device.
					clicked_device_index = -1;

					LogMsg("Changed Connection_status to 'App not found' for: "
							+ clicked_device_index);

					dialog.changeTitle("Error");
					dialog.changeDialog("Please make sure that the device is waiting for requests.");
					// Hide the ProgressBar
					dialog.showProgressBar(false);
					// Display the OK Button
					dialog.showOkButton(true);
				}
				//Unexpected messages
				else
				{
					LogMsg("Unexpected message received in this Hnadler.");
				}
			}

		}

	};

	// -----------------------------------------------------------------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Common ListView to display the devices
		setContentView(R.layout.activity_seeker);

		// SET THE COLORS RESOURCE IDs OF CONSTANTS
		WHITE = R.color.list_background_white;
		RED = R.color.list_background_red;
		GREEN = R.color.list_background_green;

		// Button to restart the scanning
		refresh = (Button) findViewById(R.id.refresh);
		refresh.setOnClickListener(this);

		// Button to goto next Activity
		next = (Button) findViewById(R.id.next);
		next.setOnClickListener(this);

		// Get a reference to ListView from layout file
		ListView l = (ListView) findViewById(R.id.list);

		// Set OnItemClickListner to that object
		l.setOnItemClickListener(this);

		// ArrayList<RowVlues> to be associated with the list and its adapter.
		ArrayList<RowValues> items = new ArrayList<RowValues>();

		// create a Lists object using above objects
		list = new Lists(l, new DeviceListAdapter(this, items), items);
		// ***Note: items should be same for both DeviceListAdapter and List
		// objects ****

		// Create a Scanner object with a Handler to receive an array of
		// BluetoothDevice objects
		scanner = new Scanner(seekerActivityHandler);

		// Array of connected devices to be passed to next activity
		// below statement is put in onStart
		// connected_device_list = new ConnectedDevice[MAX_CONNECTED_DEVICES];

		seekerActivityContext = this;

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		/**
		 * Index of the clicked item in the list. It is same as that of index of
		 * that device in the <b>device_list[]</b>. <b>Because both list and
		 * device_list[] are updated in Handler of this class only.</b>
		 */
		int device_num = arg2;

		// save the device
		BluetoothDevice trying_device = device_list[device_num];
		LogMsg("device clicked:" + trying_device.getName());

		// ONLY IF THE DEVICE IS NOT PREVIOUSLY CLICKED
		if (list.getItem(arg2).getRowValues(2).equals("0")) {
			// MAKE IT CLICKED
			list.getItem(arg2).setRowValues(2, "1");

			// Try to connect to the clicked device.
			// create a new object
			provider_device = new RemoteProviderDevice(trying_device,
					seekerActivityHandler);

			// Save the index of the clicked device.
			// It will be used by Handler to change the values of the
			// corresponding list item
			// based on the result of connection.
			// Its value will be reset to -1 after using it by the Handler.
			clicked_device_index = arg2;

			// If you get the socket successfully then try to connect it.
			// The status of the connectivity will be sent by Message Handlers
			// from
			// RemoteProviderDevice
			if (provider_device.obtainRfcommSocket() == true) {
				LogMsg("Obtained RfcommSocket.");

				// The result will be sent through messages.
				provider_device.connectToDevice();

				// Until that, Display the dialog object of CustomDialog
				dialog = new CustomDialog(this, "Connecting...",
						"Trying to connect the device.Please wait...");
				// display the dialog
				dialog.show();

				// This dialog will be closed automatically if the connection is
				// successful
				// Else the OK button will be displayed.

			}
			// else simply display an error Toast message.
			else {
				LogMsg("Could not obtain RfcommSocket.");
				Toast.makeText(
						this,
						"Couldn't connect to this device.Try restarting the application.",
						Toast.LENGTH_LONG).show();
			}
		} else {
			// if the device found has already been clicked before
			LogMsg("This Device is already Present in the connected_device_list[].");
			// Display an error toast
			Toast.makeText(
					this,
					"This Device has been selected before. Try some other devices.",
					Toast.LENGTH_SHORT).show();

		}
	}

	/**
	 * Returns the <i>connected_device_list[]</i> of this object.
	 * @return array of ConnectedDevice objects.
	 */
	public static ConnectedDevice[] getConnectedDeviceList()
	{
		return connected_device_list;
	}
	
	/**
	 * Restart the scanning for devices process.
	 */
	@Override
	public void onClick(View v) {

		if (v.getId() == refresh.getId()) {
			// only if scanner is currently scanning
			if (scanner.isScanning() == true) {
				// stop the scanning first
				scanner.stopScanningForDevices(this);
			}
			// Create a Scanner object with a Handler to receive an array of
			// BluetoothDevice objects
			scanner = new Scanner(seekerActivityHandler);

			// and start scanning for nearby devices
			scanner.startScanningForDevices(this);

			Toast.makeText(this, "Searching for devices...", Toast.LENGTH_LONG)
					.show();
			LogMsg("Restarted the scanning process.");

		}

		// Stop scanning for devices
		// and goto next ResourceListActivity along with array of
		// ConnectedDevices objects.
		if (v.getId() == next.getId()) {
			// if there is one or more connected devices,
			// if(connected_device_list[0] != null) OR
			if (connected_device_num > 0) {
				Intent i = new Intent(this, ResourceListActivity.class);

				// PROBLEM IN SENDING THE connected_device_list[] THROUGH
				// INTENTS
				// So connected_device_list[] has been made public and static
				startActivity(i);
				
				//finish this activity
				finish();
				LogMsg("Finishing Seeker Activity");
				
				
			} else {
				Toast.makeText(this, "No device is connected.",
						Toast.LENGTH_LONG).show();

				LogMsg("ERROR:connected_device_list has no connected devices.");
			}

		}
	}

	/**
	 * Start / Restart scanning once this activity starts
	 */
	@Override
	protected void onStart() {
		super.onStart();

		LogMsg("INSIDE:onStart");

		// only if scanner is currently scanning
		if (scanner.isScanning() == true) {
			// stop the scanning first
			scanner.stopScanningForDevices(this);
		}
		// and start scanning for nearby devices
		scanner.startScanningForDevices(this);

		Toast.makeText(this, "Searching for devices...", Toast.LENGTH_LONG)
				.show();

		// hide the 'next' button
		next.setVisibility(View.GONE);

		// Array of connected devices to be passed to next activity
		connected_device_list = new ConnectedDevice[MAX_CONNECTED_DEVICES];
		// Global index for connected_device_list[] array. It is incremented
		// each time a successfully connected device is added.
		connected_device_num = 0;

		LogMsg("Started the scanning process.");

	}

	/**
	 * Stop scanning for devices once this activity pauses
	
	@Override
	protected void onPause() {
		super.onPause();

		// only if scanner is not yet started scanning
		if (scanner.isScanning() == true) {
			// stop scanning for devices
			scanner.stopScanningForDevices(this);
		}

	}
 */
	
	/**
	 * Stop scanning for devices once this activity stops
	 */
	@Override
	protected void onStop() {
		super.onStop();

		// only if scanner is not yet started scanning
		if (scanner.isScanning() == true) {
			// stop scanning for devices
			scanner.stopScanningForDevices(this);
		}

	}

	/**
	 * Stop scanning for devices once this activity stops
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();

		// only if scanner is not yet started scanning
		if (scanner.isScanning() == true) {
			// stop scanning for devices
			scanner.stopScanningForDevices(this);
		}

	}

	private static void LogMsg(String msg) {
		Log.d("SeekerActivity", msg);
	}

}
