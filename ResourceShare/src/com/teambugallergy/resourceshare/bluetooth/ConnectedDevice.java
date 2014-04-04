package com.teambugallergy.resourceshare.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

/**
 * This class has all the methods required for initializing streams,reading and wrinting of data.
 * <i>
 * <br/>--------------------------------------
 * <br/>Constants of this class starts with 6 
 * <br/>--------------------------------------
 * <br/>
 * </i>
 * 
 * 02-04-2014
 * @author Adiga
 */
public class ConnectedDevice {
	
	/**
	 * Device to be connected to.
	 */
	private BluetoothDevice device = null;

	/**
	 * Socket used for connection and communication between local device and
	 * RemoteDevice.
	 */
	private BluetoothSocket socket = null;
	
	/** Handler of caller class
	 * 
	 */
	private static Handler callerHandler;
	
	/**
	 * InputStream used to read the data from socket connection
	 */
	private InputStream input_stream = null;
	/**
	 * OutputStream used to write the data to socket connection
	 */
	private OutputStream output_stream = null;

	// -----------------------------------------------------------------------------------
	
	/**
	 * Initializes the Remote device, socket associated with the connection for data transfer.
	 * 
	 * @param device Device to be connected to.
	 * @param handler Handler of the object creator, which wishes to recieve the message.
	 */
	public ConnectedDevice(BluetoothDevice device, BluetoothSocket socket, Handler handler) {
		
		LogMsg("");

		// device to be connected to
		this.device = device;

		// initialize the socket
		this.socket = socket;
				
		//Handler of the caller
		this.callerHandler = handler;
		
	}
	
	/**
	 * Gets object of BluetoothDevice that is stored as <i>device</i>. 
	 * @return <b>device</b> of ConnectedDevice object.
	 */
	public BluetoothDevice getDevice()
	{
		return device;
	}
	
	/**
	 * Obtains Input and Output Streams associated with the established
	 * connection. Only after calling this method, data transfer can be
	 * performed.
	 * 
	 * @return 
	 *         Returns <b>true</b> if input-output streams have been obtained successfully, else 
	 *         returns <b>false</b>.
	 */
	public Boolean initializeIOStreams() {

		// Get the input and output streams
		try {

			input_stream = socket.getInputStream();
			output_stream = socket.getOutputStream();

		} catch (IOException e) {

			LogMsg("Error: Cannot obtain IO Streams- " + e);
			return false;
		}

		// IO Streams have been obtained successfully.
		LogMsg("IO Streams have been obtained successfully.");
		return true;

		/*
		 * WORKS ONLY WITH API 14 OR MORE :( // Only if the connection has been
		 * established if (socket.isConnected() == true) { // IO Streams have
		 * been obtained successfully.
		 * LogMsg("IO Streams have been obtained successfully."); return true; }
		 * else { LogMsg(
		 * "Error: Cannot obtain IO Streams, connection is not established yet."
		 * ); return false; }
		 */
	}

	// TODO sending different types of data

	/**
	 * Creates a new Thread and writes the data bytes[] to the Remote Device
	 * through that Thread.
	 * 
	 * @param bytes
	 *            Data to be sent to the remote device.
	 */
	public void sendData(final byte[] bytes) {

		// Thread which writes data to the Remote Device
		Thread writer = new Thread(new Runnable() {

			@Override
			public void run() {

				String data = null;
				try {
					// write the bytes[] into output_stream
					output_stream.write(bytes);

					data = new String(bytes);
					LogMsg("Data sent to " + device.getName() + ": " + data);

				} catch (IOException e) {
					LogMsg("Error: Cannot send data to " + device.getName()
							+ "- " + e);
				}

			}
		});

		// Call the run (i.e start ) method of writer Thread to send the data.
		writer.start();

	}

	// TODO receiving different types of data

	/**
	 * Creates a new Thread and reads the data buffer[] from the Remote Device
	 * through that Thread.
	 */
	public void ReceiveData() {

		// Thread which reads data from the Remote Device
		Thread reader = new Thread(new Runnable() {

			@Override
			public void run() {

				byte[] buffer = new byte[1024]; // buffer store for the stream
				int bytes; // number of bytes returned from read()

				String data = null;

				// Keep listening to the input_stream until an exception occurs
				while (true) {
					try {
						// Reads from the InputStream
						bytes = input_stream.read(buffer);

						// data in String form
						data = new String(buffer, 0, bytes);

						// TODO: Send the obtained bytes to the UI activity
						// handler.obtainMessage(MESSAGE, bytes, -1,
						// data).sendToTarget();

						LogMsg("Data received from " + device.getName() + ": "
								+ data);

					} catch (IOException e) {
						LogMsg("Data received completely from "
								+ device.getName() + ".");
						// go out of the while loop and terminate the Thread.
						break;
					}
				}

			}// end of run()
		});

		// Call the run (i.e start ) method of reader Thread to read the data.
		reader.start();

	}

	private void LogMsg(String msg) {
		Log.d("ConnectedDevice", msg);
	}
	
}
