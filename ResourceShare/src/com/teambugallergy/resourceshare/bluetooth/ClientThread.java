package com.teambugallergy.resourceshare.bluetooth;

import java.io.IOException;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

/**
 * It is used by ProviderDevice object to connect to ProviderDevice
 * 
 * <i>
 * <br/>--------------------------------------
 * <br/>Constants of this class starts with 3
 * <br/>--------------------------------------
 * <br/>
 * </i>
 * :02-04-2014
 * @author TeamBugAllergy
 */
public class ClientThread extends Thread {

	/**
	 * <b>what</b> associated with a message to be sent.
	 */
	public static int CONNECTION_STATUS = 30;
	
	/**
	 * Tells whether the connection to Remote Device was successful or
	 * failure.
	 */
	public static String CONNECTION_SUCCESS = "ClientThread_success";
	public static String CONNECTION_FAILURE = "ClientThread_fail";
	
	/**
	 * Univarsal UUID used by the app.
	 */
	private static UUID MY_UUID;

	/**
	 * Permanent Socket used for future connections.
	 */
	private BluetoothSocket socket;
	/**
	 * Device to be connected to.
	 */
	private BluetoothDevice device;

	BluetoothAdapter ba;

	
	/** Handler of caller class
	 * 
	 */
	private static Handler callerHandler;
	
	// -----------------------------------------------------------------------------------
	
	/**
	 * Obtains MY_UUID and BluetoothAdapter.
	 * 
	 * @param device Remote Device to be connected to.
	 * @param handler Handler of the object creator, which wishes to recieve the message.
	 */
	public ClientThread(BluetoothDevice device, Handler handler) {
		
		LogMsg("");
		
		// device to be connected to
		this.device = device;

		//Handler of the caller
		this.callerHandler = handler;
		
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

		LogMsg("Obtained a socekt to " + device + " succesfully");

		// return the socket
		return socket;

	}

	/**
	 * Tries to connect to the Remote Device. (By trying to connect to server (or provider) devices )
	 * Result of connection will be sent to Caller through Handler Messages.
	 */
	public void run() {
		// Cancel discovery because it will slow down the connection
		ba.cancelDiscovery();
		
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

			//Tell the caller about this error.
			callerHandler.obtainMessage(ClientThread.CONNECTION_STATUS, ClientThread.CONNECTION_FAILURE).sendToTarget();

			// terminate the thread
			return;
		}

		LogMsg("Successfully connected to the Remote Device.");

		//Tell the caller about the success of connection
		callerHandler.obtainMessage(ClientThread.CONNECTION_STATUS, ClientThread.CONNECTION_SUCCESS).sendToTarget();
		
		// terminate the thread
		return;

	}

	/**
	 * Cancels an in-progress connection, and closes the socket
	 */
	public void cancel() {
		try {
			socket.close();
			LogMsg("server_socket has been closed");

		} catch (IOException e) {
			LogMsg("Error: Cannot close the socket- " + e);

		}

	}

	private void LogMsg(String msg) {
		Log.d("ClientThread", msg);

	}

}
