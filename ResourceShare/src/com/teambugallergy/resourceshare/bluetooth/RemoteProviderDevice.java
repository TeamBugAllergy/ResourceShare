package com.teambugallergy.resourceshare.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Object of this class represents a <b>Resource Provider Device</b>.
 * All the Operations that you wish to perform on that device are defined here.
 * This class will be used by Seeker Device.
 * 
 *<i>
 * <br/>--------------------------------------
 * <br/>Constants of this class starts with 2
 * <br/>--------------------------------------
 * <br/>
 * </i>
 * :02-04-2014
 * @author Adiga
 */
public class RemoteProviderDevice {

	/**
	 * <b>what</b> associated with a message to be sent.
	 */
	public static int CONNECTION_STATUS = 20;
	
	/**
	 * Tells whether the connection to Remote Device was successful or
	 * failure.
	 */
	public static int CONNECTION_SUCCESS = 21;
	public static int CONNECTION_FAILURE = 22;
	
	/**
	 * Device to be connected to.
	 */
	private BluetoothDevice device = null;

	/**
	 * Socket used for connection and communication between local device and
	 * RemoteDevice.
	 */
	private BluetoothSocket socket = null;
	
	/**
	 * Object of ClientThread. It used for connecting and obtaining socket.
	 */
	private ClientThread client = null;

	/** Handler of caller class
	 * 
	 */
	private static Handler callerHandler;
	
	// -----------------------------------------------------------------------------------

	/**
	 * The status of the connection is sent to the caller through message with 'what' RemoteSeekerDevice.CONNECTION_STATUS.
	 */
	private static Handler remoteProviderHandler = new Handler(){
		public void handleMessage(Message msg) {
			
			//if the message is from ClientThread regarding result of connection,
			if(msg.what == ClientThread.CONNECTION_STATUS)
			{
				if(msg.obj.equals(ClientThread.CONNECTION_SUCCESS))
				{
					//Notify the caller that successfully obtained a connection.
					callerHandler.obtainMessage(RemoteProviderDevice.CONNECTION_STATUS, RemoteProviderDevice.CONNECTION_SUCCESS).sendToTarget();
					
				}
				else if(msg.obj.equals(ClientThread.CONNECTION_FAILURE))
				{
					//Notify the caller that failed to obtain a connection.
					callerHandler.obtainMessage(RemoteProviderDevice.CONNECTION_STATUS, RemoteProviderDevice.CONNECTION_FAILURE).sendToTarget();
					
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
	 * Initializes the RemoteProviderDevice object and creates a ClientThread object.
	 * 
	 * @param device Device to be connected to.
	 * @param handler Handler of the object creator, which wishes to recieve the message.
	 */
	public RemoteProviderDevice(BluetoothDevice device, Handler handler) {

		LogMsg("");

		// device to be connected to
		this.device = device;

		//Handler of the caller
		this.callerHandler = handler;
				
		// create a ClientThread object
		client = new ClientThread(device, remoteProviderHandler);

	}

	/**
	 * 
	 * @return Returns <b>true</b> if RFCOMM channel socket is successfully
	 *         obtained or <b>false</b> on error.
	 */
	public Boolean obtainRfcommSocket() {

		// get the socket for the connection from ClientThread and return it.
		socket = client.getSocket();

		if (socket == null) {
			return false; // error in obtaining the socket
		}

		else {
			return true; // successfully obtained a socket
		}

	}

	/**
	 * Tries to establish a connection with the Remote Device.
	 * The result of the connection will sent later through message(<b>Handler</b>).
	 */
	public void connectToDevice() {

		//Try to connect to the remote device.
		//The result or status of the connection is sent through message by the ClientThread.
		//RESULT OF CONNECTION WILL BE SENT TO CALLER LATER.(By Handler)
		client.start();

	}

	/**
	 * Gets object of BluetoothDevice that is stored as <i>device</i>. 
	 * @return <b>device</b> of RemoteProviderDevice object.
	 */
	public BluetoothDevice getDevice()
	{
		return device;
	}
	
	/**
	 * Returns the socket that has been obtained from ClientThread for communications.
	 * @return BluetoothSocket object associated with the established connection.
	 */
	public BluetoothSocket getSocket()
	{
		//Return the socket of connection
		return socket;
	}
	
	/**
	 * Closes the socket and stops the on going communication through that
	 * socket.
	 */
	public void stopConnection() {
		client.cancel();
	}
	

	private static void LogMsg(String msg) {
		Log.d("RemoteProviderDevice", msg);
	}
}
