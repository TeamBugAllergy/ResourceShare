package com.teambugallergy.bluetooth;

import java.io.IOException;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ClientThread extends Thread {

	/**
	 * Tells whether the connection to Remote Device was successful (true) or
	 * failure (false).
	 */
	public static Boolean CONNECTION_STATUS = false;

	/**
	 * Univarsal UUID used by the app.
	 */
	private static UUID MY_UUID;

	/**
	 * Socket obtained for future connections.
	 */
	private BluetoothSocket socket;
	/**
	 * Device to be connected to.
	 */
	private BluetoothDevice device;

	BluetoothAdapter ba;

	// -----------------------------------------------------------------------------------

	/**
	 * Obtains MY_UUID and BluetoothAdapter.
	 * 
	 * @param dev
	 *            Remote Device&nbsp;to&nbsp;be&nbsp;connected&nbsp;to.
	 */
	public ClientThread(BluetoothDevice dev) {
		LogMsg("");
		// device to be connected to
		device = dev;

		// initialize the socket
		socket = null;

		// generate an uuid in MY_UUID
		try {
			MY_UUID = UUID.fromString("a60f35f0-b93a-11de-8a39-08002009c666");
		} catch (IllegalArgumentException e) {
			LogMsg("UUID is not properly formatted!!!");
		}

		// get a BluetoothAdapter
		ba = BluetoothAdapter.getDefaultAdapter();

	}

	/**
	 * Obtains and returns a <b>socket</b> to connect to Remote Device or
	 * <b>null</b> on error.
	 * 
	 * @return BluetoothSocket or null.
	 */
	public BluetoothSocket getSocket() {

		// Get a BluetoothSocket to connect with the given BluetoothDevice
		try {
			// MY_UUID is the app's UUID string, also used by the Remote Device
			socket = device.createRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e) {
			// Some error occured in obtaning a socket from Remote Device
			LogMsg("Error:Exception caught in obtaining a Remotesocekt- " + e);

			// return null to indicate the caller about the error
			return null;

		}

		LogMsg("Obtained a RemoteSocekt to " + device + " succesfully");

		// return the socket
		return socket;

	}

	/**
	 * Tries to connect to the Remote Device.
	 * 
	 * Sets the ClientThread.CONNECTION_STATUS flag to indicate the result of
	 * this try.
	 */
	public void run() {
		// Cancel discovery because it will slow down the connection
		ba.cancelDiscovery();

		//Initial value of the flag CONNECTION_STATUS = false (i.e no connection yet)
		CONNECTION_STATUS = false;
		
		// Connect the Remote Device through the socket. This will block
		// until it succeeds or throws an exception
		try {
			LogMsg("Connecting to Remote Device's socket...");
			socket.connect();

		} catch (IOException connectException) {
			// Unable to connect to Remote Device; close the socket and get out
			LogMsg("Error: Unable to Connect to the Remote Device- "
					+ connectException);

			// close the socket
			try {
				socket.close();
				LogMsg("Error: Closing the socket now");
			} catch (IOException closeException) {
				LogMsg("Error: Cannot close the socket- " + closeException);

				// terminate the thread
				return;
			}

			// Set the error flag to false, to notify the caller.
			CONNECTION_STATUS = false;

			// terminate the thread
			return;
		}

		LogMsg("Successfully connected to the Remote Device.");

		// Set the error flag to true, to notify the caller.
		CONNECTION_STATUS = true;

		// terminate the thread
		return;

	}

	/**
	 * Cancels an in-progress connection, and closes the socket
	 */
	public void cancel() {
		try {
			socket.close();

		} catch (IOException e) {

			LogMsg("Error: Cannot close the socket- " + e);

			// terminate the thread
			return;
		}

	}

	private void LogMsg(String msg) {
		Log.d("ClientThread", msg);

	}

}
