package com.teambugallergy.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class RemoteDevice {

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
	 * InputStream used to read the data from socket connection
	 */
	private InputStream input_stream = null;
	/**
	 * OutputStream used to write the data to socket connection
	 */
	private OutputStream output_stream = null;

	/**
	 * Object of ClientThread. It used for connecting and obtaining socket.
	 */
	private ClientThread client = null;

	// -----------------------------------------------------------------------------------

	/**
	 * Initializes the RemoteDevice object and creates a ClientThread object.
	 * 
	 * @param dev
	 *            Device&nbsp;to&nbsp;be&nbsp;connected&nbsp;to
	 */
	public RemoteDevice(BluetoothDevice dev) {

		LogMsg("");

		// device to be connected to
		device = dev;

		// create a ClientThread object
		client = new ClientThread(device);

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

	// TODO: Decide where to check for availability (or presence) of application
	// in the Remote Device

	/**
	 * Socket obtained here is ready to be used for communication.
	 * 
	 * @return Returns <b>BluetoothSocket</b> if connection to RemoteDevice is
	 *         successful or <b>null</b> on error.
	 */
	public BluetoothSocket connectToDevice() {

		// connect to the client device using the socket obtained.
		// Sets the ClientThread.CONNECTION_STATUS to false if there were any
		// errors during this process.
		client.start();

		if (ClientThread.CONNECTION_STATUS == true) {

			return socket; // successfully connected to the RemoteDevice
		}

		else {

			return null; // Error in connecting the RemoteDevice
		}

	}

	/**
	 * Obtains Input and Output Streams associated with the established
	 * connection. Only after calling this method, data transfer can be
	 * performed.
	 * 
	 * @return 
	 *         Returns&nbsp;<b>true</b>&nbsp;if&nbsp;input-output&nbsp;streams&nbsp
	 *         ;have&nbsp;been&nbsp;obtained&nbsp;successfully,&nbsp;else&nbsp;
	 *         returns&nbsp;<b>false</b>.
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

	/**
	 * Closes the socket and stops the on going communication through that
	 * socket.
	 */
	public void closeConnectionFromDevice() {
		client.cancel();
	}

	private void LogMsg(String msg) {
		Log.d("RemoteDevice", msg);
	}
}
