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
 * @author Adiga@TeamBugAllergy
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
					
					//LogMsg("INSIDE:CONNECTION_SUCCESS");
					
					// get the socket object of connection
					socket = server.getSocket();

					//if(socket != null)
					//	LogMsg("CHECK:socket is not null");
					
					//if(server.getSocket() != null)
						//LogMsg("HERE:socket=" + server.getSocket().toString());
					
					// Save the device that has been connected to.
					device = server.getDevice();
					
					// Notify the caller that successfully obtained a
					// connection.
					callerHandler.obtainMessage(
							RemoteSeekerDevice.CONNECTION_STATUS,
							RemoteSeekerDevice.CONNECTION_SUCCESS)
							.sendToTarget();


				} else if (msg.obj.equals(ServerThread.CONNECTION_FAILURE)) {
					
					//LogMsg("INSIDE:CONNECTION_FAILURE");
					
					// save the devcice as null
					device = null;
					
					// Notify the caller that failed to obtain a connection.
					callerHandler.obtainMessage(
							RemoteSeekerDevice.CONNECTION_STATUS,
							RemoteSeekerDevice.CONNECTION_FAILURE)
							.sendToTarget();
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

	/**
	 * Initializes the RemoteSeekerDevice object and creates a ServerThread
	 * object.
	 * 
	 * @param handler
	 *            Handler of the object creator, which wishes to recieve the
	 *            message.
	 */
	public RemoteSeekerDevice(Handler handler) {

		//LogMsg("INSIDE:RemoteSeekerDevice");
		
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
		
		//LogMsg("INSIDE:obtainServerSocket");
		
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

		//LogMsg("INSIDE:startListeningToDevice");
		
		// Listen to request from the remote seeker device.
		// The result or status of the connection is sent through message by
		// the ServerThread.
		// RESULT OF CONNECTION WILL BE SENT TO CALLER LATER.(By Handler)
		
		//only if the thread is not started
		if( !server.isAlive() )
			server.start();

	}

	/**
	 * Closes the server_socket object and there by finishes the ServerThread.
	 */
	public void stopListeningToDevice() {
		
		//LogMsg("INSIDE:stopListeningToDevice");
		
		server.cancel();
	}

	/**
	 * Gets object of BluetoothDevice that is stored as <i>device</i>.
	 * 
	 * @return <b>device</b> of RemoteProviderDevice object.
	 */
	public BluetoothDevice getDevice() {
		
		//LogMsg("INSIDE:getDevice");
		
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
		
		//LogMsg("INSIDE:getSocket");
		
		// Return the socket of connection
		return socket;
	}

	/**
	 * Sets the object of BluetoothDevice that is stored as <i>device</i>.
	 * 
	 * @param device BluetoothDevice object to be set.
	 */
	public void setDevice(BluetoothDevice device) {
		
		//LogMsg("INSIDE:setDevice");

		this.device = device;
	}

	/**
	 * Sets the object of BluetoothSocket that is stored as <i>socket</i>.
	 * 
	 * @param socket BluetoothSocket object to be set.
	 */
	public void setSocket(BluetoothSocket socket) {
		
		//LogMsg("INSIDE:setSocket");

		this.socket = socket;
	}
	
	/**
	 * Closes the socket and stops the on going communication through that
	 * socket.
	 */
	public void stopConnection() {
		
		//LogMsg("INSIDE:stopConnection");
		
		server.stopConnection();
	}

	private static void LogMsg(String msg) {
		Log.d("RemoteProviderDevice", msg);
	}
}
