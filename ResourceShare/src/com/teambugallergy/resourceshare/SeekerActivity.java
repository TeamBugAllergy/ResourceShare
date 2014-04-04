package com.teambugallergy.resourceshare;

import java.util.ArrayList;

import com.teambugallergy.resourceshare.bluetooth.ConnectedDevice;
import com.teambugallergy.resourceshare.bluetooth.RemoteProviderDevice;
import com.teambugallergy.resourceshare.bluetooth.Scanner;
import com.teambugallergy.resourceshare.list.*;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
 * </i> 04-04-2014
 * 
 * @author Adiga
 * 
 */
public class SeekerActivity extends Activity implements OnItemClickListener {

	// Handler to receive the device_list
	// activity_device_list to display the list of devices
	// Connecting to devices when a item in the list is clicked by the
	// user

	/**
	 * Maximum of 5 devices can be stored in the connected_device_list[]. i.e
	 * Maximum of 5 Resource Providers are allowed.
	 */
	private final int MAX_CONNECTED_DEVICES = 80;

	/**
	 * List objcet that has methods for addItem, removeItem and
	 * onItemClickListners for List Items
	 */
	private static Lists list;

	/**
	 * Scanner object to scan and obtain a list of BluetoothDevice objects.
	 */
	Scanner scanner;

	/**
	 * An object that represents the remote provider device.
	 */
	private static RemoteProviderDevice provider_device;

	/**
	 * Array of BluetoothDevice Objects, received from Scanner object.
	 */
	private static BluetoothDevice[] device_list;

	/**
	 * An array of BluetoothDevices that are transfered to next Activity.
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
					// if the device is not null,
					// add the new device_list[] into list
					list.addItem(new String[] { device_list[i].getName(),
							"Unkown" });

					// and tell the adapter about changes
					list.notifyDataSetChanged();

					LogMsg("device:" + device_list[i].getName());

				}

			}

			// if the message is from RemoteProviderDevice
			if (msg.what == RemoteProviderDevice.CONNECTION_STATUS) {
				// if the connection was successful
				if (msg.obj.equals(RemoteProviderDevice.CONNECTION_SUCCESS)) {

					//TODO:Test everything after building Provider end.
					
					connected_device_list[connected_device_num] = new ConnectedDevice(
							provider_device.getDevice(),
							provider_device.getSocket(), seekerActivityHandler);
					connected_device_num++;

					// ONCE THE USE OF PROVIDER DEVICE IS OVER, SET IT TO NULL
					// FOR RE-USE BY onItemClickListner()
					provider_device = null;

					// Close the dialog and display a toast
					dialog.closeDialog();
					Toast.makeText(seekerActivityContext,
							"Connected to the device.", Toast.LENGTH_SHORT)
							.show();

				}
				// if the connection was failed

				else if (msg.obj
						.equals(RemoteProviderDevice.CONNECTION_FAILURE)) {

					dialog.changeTitle("Error");
					dialog.changeDialog("Please make sure that the device has the application installed.");
					// Hide the ProgressBar
					dialog.showProgressBar(false);
					// Display the OK Button
					dialog.showOkButton(true);
				}
			}

		}

	};

	// -----------------------------------------------------------------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Common ListView to display the devices
		setContentView(R.layout.activity_list);

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
		connected_device_list = new ConnectedDevice[MAX_CONNECTED_DEVICES];

		seekerActivityContext = this;
		/*
		 * This will be done in onStart() method // and start scanning for
		 * nearby devices scanner.startScanningForDevices(this);
		 */
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

		// true if device already present in the connected_device array else
		// false.
		boolean already_present = false;

		// Check if the device already clicked
		// for each device in the array,
		for (int i = 0; connected_device_list[i] != null; i++) {

			// if the device found already exists in the array of
			// devices
			if (connected_device_list[i].getDevice().getAddress()
					.equals(trying_device.getAddress()) == true) {

				LogMsg("This Device is already Present in the connected_device_list[].");
				// Display an error toast
				Toast.makeText(
						this,
						"This Device has been selected before. Try some other devices.",
						Toast.LENGTH_SHORT).show();

				// do not add the device again to the array
				already_present = true;
				break;
			}
		}
		// ONLY IF THE DEVICE IS NOT PREVIOUSLY CLICKED
		if (already_present == false) {

			// Try to connect to the clicked device.
			// create a new object
			provider_device = new RemoteProviderDevice(trying_device,
					seekerActivityHandler);

			// If you get the socket succesfully then try to connect it.
			// The status of the connectivity will be sent by Message Handlers
			// from
			// RemoteProviderDevice
			if (provider_device.obtainRfcommSocket() == true) {
				LogMsg("Obtained RfcommSocket.");

				// The result will be sent through messages.
				provider_device.connectToDevice();

				// Untill that, Display the dialog object of CustomDialog
				dialog = new CustomDialog(this, "Connecting...",
						"Trying to connect the device.Please wait...");
				// display the dialog
				dialog.show();

				// This dialog will be closed automatically if the connection is
				// successfull
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
		}
	}

	/**
	 * Stop scanning for devices once this activity pauses
	 */
	@Override
	protected void onPause() {
		super.onPause();

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
	protected void onStop() {
		super.onStop();

		// only if scanner is not yet started scanning
		if (scanner.isScanning() == true) {
			// stop scanning for devices
			scanner.stopScanningForDevices(this);
		}

	}

	/**
	 * Stop scanning for devices once this activity is destroyed
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

	/**
	 * Start / Restart scanning once this activity starts
	 */
	@Override
	protected void onStart() {
		super.onStart();

		// only if scanner is not yet started scanning
		if (scanner.isScanning() == false) {
			// Create a Scanner object with a Handler to receive an array of
			// BluetoothDevice objects
			scanner = new Scanner(seekerActivityHandler);

			// and start scanning for nearby devices
			scanner.startScanningForDevices(this);
		}

	}

	private static void LogMsg(String msg) {
		Log.d("SeekerActivity", msg);
	}
}
