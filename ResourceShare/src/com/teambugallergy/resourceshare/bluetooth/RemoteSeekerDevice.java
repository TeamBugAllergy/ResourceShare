package com.teambugallergy.resourceshare.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Object of this class represents a <b>Resource Seeker Device</b>. All the
 * Operations that you wish to perform on that device are defined here. This
 * class will be used by Provider Device.
 * 
 * <i> <br/>
 * -------------------------------------- <br/>
 * Constants of this class starts with 4 <br/>
 * -------------------------------------- <br/>
 * </i>
 * 
 * 02-04-2014
 * 
 * @author Adiga
 */
public class RemoteSeekerDevice {

	/**
	 * <b>what</b> associated with a message to be sent.
	 */
	public static int CONNECTION_STATUS = 40;

	/**
	 * Tells whether the connection to Remote Device was successful or failure.
	 */
	public static int CONNECTION_SUCCESS = 41;
	public static int CONNECTION_FAILURE = 42;

	/**
	 * Device to be connected to.
	 */
	private static BluetoothDevice device = null;

	/**
	 * Socket used for connection and communication between local device and
	 * Remote Device.
	 */
	private static BluetoothSocket socket = null;

	/**
	 * Object of ServerThread. It used for connecting and obtaining socket.
	 */
	private static ServerThread server = null;

	/**
	 * Handler of caller class
	 * 
	 */
	private static Handler callerHandler;

	// -----------------------------------------------------------------------------------

	/**
	 * The status of the connection is sent to the caller through message with
	 * 'what' RemoteSeekerDevice.CONNECTION_STATUS.
	 */
	private static Handler remoteSeekerHandler = new Handler() {

		public void handleMessage(Message msg) {

			if (msg.what == ServerThread.CONNECTION_STATUS) {
				if (msg.obj.equals(ServerThread.CONNECTION_SUCCESS)) {
					// Notify the caller that successfully obtained a
					// connection.
					callerHandler.obtainMessage(
							RemoteSeekerDevice.CONNECTION_STATUS,
							RemoteSeekerDevice.CONNECTION_SUCCESS)
							.sendToTarget();

					// get the socket object of connection
					socket = server.getSocket();

					// Save the device that has been connected to.
					device = server.getDevice();

				} else if (msg.obj.equals(ServerThread.CONNECTION_FAILURE)) {
					// Notify the caller that failed to obtain a connection.
					callerHandler.obtainMessage(
							RemoteSeekerDevice.CONNECTION_STATUS,
							RemoteSeekerDevice.CONNECTION_FAILURE)
							.sendToTarget();

					// save the devcice as null
					device = null;
				}

			}
		}

	};

	// -----------------------------------------------------------------------------------

	/**
	 * Initializes the RemoteSeekerDevice object and creates a ServerThread
	 * object.
	 * 
	 * @param handler
	 *            Handler of the object creator, which wishes to recieve the
	 *            message.
	 */
	public RemoteSeekerDevice(Handler handler) {

		LogMsg("");

		// device to be connected to.
		// This will have the BluetoothDevice after a successful connection.
		this.device = null;

		// Handler of the caller
		this.callerHandler = handler;

		// Create a ServerThread
		server = new ServerThread(device, remoteSeekerHandler);

	}

	/**
	 * 
	 * @return Returns <b>true</b> if obtaining server_socket is successfully
	 *         obtained or <b>false</b> on error.
	 */
	public Boolean obtainServerSocket() {
		// try to obtain a server_socket
		BluetoothServerSocket server_socket = server.getServerSocket();
		if (server_socket == null)
			return false; // error in obtaining the socket
		else
			return true; // successfully obtained a socket

	}

	/**
	 * Starts Listening to requests from Seeker device. The result of the
	 * connection will sent later through message(<b>Handler</b>).
	 */
	public void startListeningToDevice() {

		// Listen to request from the remote seeker device.
		// The result or status of the connection is sent through message by
		// the ServerThread.
		// RESULT OF CONNECTION WILL BE SENT TO CALLER LATER.(By Handler)
		server.start();

	}

	/**
	 * Closes the server_socket object and there by finishes the ServerThread.
	 */
	public void stopListeningToDevice() {
		server.cancel();
	}

	/**
	 * Gets object of BluetoothDevice that is stored as <i>device</i>.
	 * 
	 * @return <b>device</b> of RemoteProviderDevice object.
	 */
	public BluetoothDevice getDevice() {
		return device;
	}

	/**
	 * Returns the socket that has been obtained from ClientThread for
	 * communications.
	 * 
	 * @return BluetoothSocket object associated with the established
	 *         connection.
	 */
	public BluetoothSocket getSocket() {
		// Return the socket of connection
		return socket;
	}

	/**
	 * Closes the socket and stops the on going communication through that
	 * socket.
	 */
	public void stopConnection() {
		server.stopConnection();
	}

	private void LogMsg(String msg) {
		Log.d("RemoteProviderDevice", msg);
	}
}
