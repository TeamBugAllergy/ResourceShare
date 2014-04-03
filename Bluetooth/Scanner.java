package com.bugallergy.teambugallergy;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Object of this class has methids for scanning bluetooth enabled nearby devices.
 * 
 * <i>
 * <br/>--------------------------------------
 * <br/>Constants of this class starts with 1
 * <br/>--------------------------------------
 * <br/>
 * </i>
 * :02-04-2014
 * @author Adiga
 */

public class Scanner {

	/**
	 * Used by the handler to send the device_list back to the Activity.
	 */
	public static int DEVICE_LIST = 10;
	/**
	 * Maximum of 20 devices can be stored in the device_list[].
	 */
	private final int MAX_DEVICES = 20;

	private BluetoothAdapter bluetooth_adapter;

	/**
	 * Array of BluetoothDevice objects that have been found so far.
	 */
	private BluetoothDevice[] device_list;
	private int device_num = 0;

	/**
	 * Handler used by UI Thread which wants to receive the list of devices.
	 */
	private Handler handler;

	// -----------------------------------------------------------------------------------
		//Till this goes :) 02/04/2014
		
		
	// -----------------------------------------------------------------------------------
	
	/**
	 * @param hand
	 *            Handler to which the device_list[] has to be sent.
	 */
	public Scanner(Handler hand) {

		// Get the BluetoothAdapter
		bluetooth_adapter = BluetoothAdapter.getDefaultAdapter();

		// Initialize the device_list[] array
		device_list = new BluetoothDevice[MAX_DEVICES];

		// Set the Handler to which messages have to be sent
		handler = hand;
	}

	/**
	 * Receiver for BluetoothDevice.ACTION_FOUND action(or intent)
	 */
	private BroadcastReceiver broadcast_receiver = new BroadcastReceiver() {

		/**
		 * Everytime a device is found, send the new list of BluetoothDevices to
		 * the <b>handler</b>
		 */
		@Override
		public void onReceive(Context context, Intent intent) {

			LogMsg("Inside the Broadcast Receiver");

			// get the action of the intent received
			String action = intent.getAction();

			// If the action is .ACTION_FOUND, then
			if (action.equals(BluetoothDevice.ACTION_FOUND)) {

				// Get the BluetoothDevice object of the found device
				BluetoothDevice dev = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				// for each device in the array,
				for (int i = 0; i < device_list.length; i++) {

					// if the device found already exists in the array of
					// devices
					if (device_list[i].equals(dev) == true) {

						// don not add the device again to the array
						return;
					}
				}

				// If the device is not previously there in the array,
				// Add the device to array.
				device_list[device_num] = dev;

				// SEND THE NEW LIST OF DEVICES THROUGH handler TO THE ACTIVITY
				Message msg = handler.obtainMessage(DEVICE_LIST, device_list);
				handler.sendMessage(msg);

				LogMsg("Device " + device_list[device_num].getName()
						+ "detected.");

				// Increment device_num to store next device.
				device_num++;

			}

		}
	};

	/**
	 * Registers the BroadcastReceiver and starts scanning for the devices.
	 * 
	 * @param context
	 *            The main thread(UI thread) context.
	 */
	public void startScanningForDevices(Context context) {

		// Register the broadcast receiver for ACTION_FOUND
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		context.registerReceiver(broadcast_receiver, filter);

		LogMsg("Registered the BroadcastReceiver");

		// Start scanning for devices
		bluetooth_adapter.startDiscovery();
		LogMsg("Started scanning for devices");

	}

	/**
	 * Unregisters the BroadcastReceiver and stops scanning for the devices.
	 * 
	 * @param context
	 *            The main thread(UI thread) context.
	 */
	public void stopScanningForDevices(Context context) {

		// Stop the discovery
		bluetooth_adapter.cancelDiscovery();
		LogMsg("Stopped the discovery");

		// Unregister the BroadcastReceiver broadcast_receiver
		context.unregisterReceiver(broadcast_receiver);

		LogMsg("Unregistered the Receiver");
	}

	private void LogMsg(String msg) {
		Log.d("Scanner", msg);
	}
}
