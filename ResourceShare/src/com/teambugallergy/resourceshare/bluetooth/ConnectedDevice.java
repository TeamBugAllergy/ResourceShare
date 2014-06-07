package com.teambugallergy.resourceshare.bluetooth;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.util.ByteArrayBuffer;

import com.teambugallergy.resourceshare.constants.Resources;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

/**
 * This class has all the methods required for initializing streams,reading and
 * wrinting of data. It also implements Parcelable interface to make it
 * pass-able through intents. <i> <br/>
 * -------------------------------------- <br/>
 * Constants of this class starts with 6 <br/>
 * -------------------------------------- <br/>
 * </i>
 * 
 * 02-04-2014
 * 
 * @author Adiga@TeamBugAllergy
 */
public class ConnectedDevice {

	/**
	 * Thread which writes data to the Remote Device
	 */
	Thread writer;

	/**
	 * Thread which reads data from the Remote Device
	 */
	// Thread reader;

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
	 * Index of the ConnectedDevice object in connected_device_list[] array.
	 * This index will be added to msg.arg2 of the message that has been read,
	 * and sent to ResourceListActivity. Used by receiveData() to detect the
	 * sender of the message.
	 */
	private int device_index = -1;

	/**
	 * Handler of caller class
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

	/**
	 * Flag used to stop the thread. It is set to true by stopReceivingData()
	 * method
	 */
	//private Boolean stop = false;

	// testing purpose
	private static int reader_thread_count = 0;

	// -----------------------------------------------------------------------------------

	/**
	 * Initializes the Remote device, socket associated with the connection for
	 * data transfer.
	 * 
	 * @param device
	 *            Device to be connected to.
	 * @param handler
	 *            Handler of the object creator, which wishes to recieve the
	 *            message.
	 */
	public ConnectedDevice(BluetoothDevice device, BluetoothSocket socket,
			Handler handler) {

		LogMsg("");

		// LogMsg("HERE:server- "+ socket +" device- " + device);

		// device to be connected to
		this.device = device;

		// initialize the socket
		this.socket = socket;

		// Handler of the caller
		this.callerHandler = handler;

		// Intialize the IO streams
		initializeIOStreams();
	}

	/**
	 * Sets the new <i>handler</i> as the <i>callerHandler</i> of this object.
	 * 
	 * @param handler
	 */
	public void setCallerHandler(Handler handler) {
		callerHandler = handler;
	}

	/**
	 * Sets the device_index of this ConnectedDevice object to <i>index</i>.
	 * 
	 * @param index
	 *            index of this object in connected_device_list[] array of
	 *            ResourceListActivity.
	 */
	public void setDeviceIndex(int index) {
		device_index = index;
	}

	/**
	 * Gets the device_index of this ConnectedDevice object.
	 * 
	 * @return device_index i.e index of this object in connected_device_list[]
	 *         array of ResourceListActivity.
	 */
	public int getDeviceIndex() {
		return device_index;
	}

	/**
	 * Gets object of BluetoothDevice that is stored as <i>device</i>.
	 * 
	 * @return <b>device</b> of ConnectedDevice object.
	 */
	public BluetoothDevice getDevice() {
		return device;
	}

	/**
	 * Gets object of BluetoothSocket that is stored as <i>socket</i>.
	 * 
	 * @return <b>socket</b> of this ConnectedDevice object.
	 */
	public BluetoothSocket getSocket() {
		return socket;
	}

	/**
	 * Obtains Input and Output Streams associated with the established
	 * connection. Only after calling this method, data transfer can be
	 * performed.
	 * 
	 * @return Returns <b>true</b> if input-output streams have been obtained
	 *         successfully, else returns <b>false</b>.
	 */
	private Boolean initializeIOStreams() {

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

		writer = new Thread(new Runnable() {

			@Override
			public void run() {
				
				String data = null;
				try {
					
					//add the delimeter EOD (810) to end of the data
					ByteArrayBuffer buff = new ByteArrayBuffer(bytes.length);
					buff.append(bytes, 0, bytes.length);
					//add 810
					buff.append(8);
					buff.append(1);
					buff.append(0);
					
					byte[] byte_data = buff.toByteArray();
					
					// write the bytes[] into output_stream
					output_stream.write(byte_data);
					
					data = new String(byte_data);
					LogMsg("Data sent to " + device.getName() + ": " + data);

				} catch (IOException e) {
					LogMsg("Error: Cannot send data to " + device.getName()
							+ "- " + e);
				}

				LogMsg("Terminating the writing Thread");
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
	public void receiveData() {

		// terminate the 'reader' if it is still alive and then start new
		// thread
		LogMsg("In the receiveData().");

		Thread reader = new Thread(new Runnable() {

			@Override
			public void run() {

				reader_thread_count++;
				LogMsg("Reader Thread Count:" + reader_thread_count);

				// reset the flag
				Boolean stop = false;
				
				// number of bytes returned from read()
				int num_bytes_read;
				int total_bytes_read = 0;
				
				//complete data received
				ByteArrayOutputStream complete_data_received = new ByteArrayOutputStream(1024);
				
				
				// Keep listening to the input_stream until an exception occurs
				// OR caller wants to stop receiving data
				// while (true) {
				while (stop == false) {

					LogMsg("Waiting for data from " + device.getName());

					try {
						// buffer store for the stream
						byte[] buffer_data_received = new byte[1024]; 
						
						//input_stream.reset();
						
						// Reads from the InputStream
						num_bytes_read = input_stream.read(buffer_data_received);
						
						//keep track of total number of bytes
						total_bytes_read += num_bytes_read;
						
						//store it in big buffer
						complete_data_received.write(buffer_data_received,0,num_bytes_read);
												
						LogMsg("Read " + num_bytes_read + " bytes ");
						LogMsg("last 3 bytes:" + buffer_data_received[num_bytes_read-3] +","+ buffer_data_received[num_bytes_read-2] +","+ buffer_data_received[num_bytes_read-1]);
						
						//if the buffer[] is not full,
						//Check using the EOD (810)...
						if( buffer_data_received[num_bytes_read-3] == 8 && buffer_data_received[num_bytes_read-2] == 1 && buffer_data_received[num_bytes_read-1] == 0)
						{
							LogMsg("last 3 bytes:" + buffer_data_received[num_bytes_read-3] +","+ buffer_data_received[num_bytes_read-2] +","+ buffer_data_received[num_bytes_read-1]);
							
							LogMsg(total_bytes_read + " bytes of data received completely from "
									+ device.getName());
							
							//terminate the thread
							stop = true;
						}

					} catch (IOException e) {
						
						LogMsg("Error:In reading the data- " + e);
						// go out of the while loop and terminate the Thread.
						break;

					}
					
				}
				
					//use the complete data stored in big buffer
					byte[] complete_bytes_received = complete_data_received.toByteArray();
					
					//remove the last three bytes of delimeter EOD
					ByteArrayBuffer tmp_buffer = new ByteArrayBuffer(complete_bytes_received.length - 3);
					tmp_buffer.append(complete_bytes_received, 0, complete_bytes_received.length - 3);
					total_bytes_read = total_bytes_read - 3;
					
					//save the byte without the delimeter EOD
					byte[] byte_data = tmp_buffer.toByteArray();
					
					//check if data is started with 744? It denotes the image data
					if(byte_data[0] == 7 && byte_data[1] == 4 && byte_data[2] == 4)
					{
						
						LogMsg("Image data message");
						
						//code to send image data to SeekerCameraActivity
						
						// remove first 3 bytes from the data
						ByteArrayBuffer buffer_img_data = new ByteArrayBuffer(byte_data.length - 3);
						buffer_img_data.append(byte_data, 3, byte_data.length - 3);
						
						byte[] img_byte_data = buffer_img_data.toByteArray();
						
						// 744 is IMAGE_DATA_IDENTIFIER

						// send the complete image data once to the
						// callerHandler
						// Send the obtained bytes to the UI activity
						// device_index tells the callerHandler from which
						// device, the data has been received.
						// Here 'what' is known i.e IMAGE_DATA
						callerHandler.obtainMessage(Resources.IMAGE_DATA, img_byte_data.length, device_index, img_byte_data).sendToTarget();

					}
					else
					{
						LogMsg("Normal message");
						
						String data = new String(byte_data, 0, byte_data.length);

						// data contains <what:data> formate separate
						// 'what' and 'data' from it
						String[] data_values = data.split(":");

						LogMsg("Data received from " + device.getName() + ": "
								+ data_values[0] + "," + data_values[1]);

						// Send the obtained bytes to the UI activity
						// device_index tells the callerHandler from which
						// device, the data has been received.
						callerHandler.obtainMessage(
								Integer.parseInt(data_values[0]), byte_data.length,
								device_index, data_values[1]).sendToTarget();
						
					}
					
				//}

				LogMsg("Terminating the reading Thread");

				reader_thread_count--;
				LogMsg("Reader Thread Count:" + reader_thread_count);

				return;

			}// end of run()
		});

		// Call the run (i.e start ) method of reader Thread to read the data.
		reader.start();

	}

	/**
	 * Called whenever the caller does not want to receive data.
	 */
	/*
	 * public void stopReceivingData() { //It makes the 'stop' flag to 'true'
	 * //which makes the while() loop in the ReceiveData() method to stop //and
	 * terminates the thread stop = true; LogMsg("Stopped listening to data"); }
	 */
	/**
	 * Closes the socket associated with the connection and terminates the
	 * connection.
	 */
	public void disconnect() {

		if (socket != null) {
			try {
				LogMsg("***Closing the IO streams & socket and disconnecting.***");

				// close the streams
				input_stream.close();
				output_stream.close();

				socket.close();

			} catch (IOException e) {
				LogMsg("Error in closing the socket- " + e);
			}
		}
	}

	private void LogMsg(String msg) {
		Log.d("ConnectedDevice", msg);
	}
}
