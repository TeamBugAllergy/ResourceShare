package com.teambugallergy.resourceshare.bluetooth;

import java.io.IOException;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * It is used by SeekerDevice object to connect to SeekerDevice
 * 
 * <i>
 * <br/>--------------------------------------
 * <br/>Constants of this class starts with 5
 * <br/>--------------------------------------
 * <br/>
 * </i>
 * 
 * :02-04-2014
 * @author Adiga
 */
public class ServerThread extends Thread{

	/**
	 * <b>what</b> associated with a message to be sent.
	 */
	public static int CONNECTION_STATUS = 50;
	
	/**
	 * Tells whether the connection to Remote Device was successful or
	 * failure.
	 */
	public static String CONNECTION_SUCCESS = "ServerThread_success";
	public static String CONNECTION_FAILURE = "ServerThread_fail";
	
	/**
	 * Univarsal UUID used by the app.
	 */
	private static UUID MY_UUID;
	
	/**
	 * Device to be connected to.
	 */
	private BluetoothDevice device = null;
	
	/**
	 * Socket used for accepting the connection from provider.
	 */
	private BluetoothServerSocket server_socket;
	
	private BluetoothAdapter ba;

	/**
	 * Permanent Socket used for future connections.
	 */
	private BluetoothSocket socket; 
	
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
	public ServerThread(BluetoothDevice device, Handler handler)
	{
		LogMsg("");
		
		// device to be connected to
		this.device = device;
				
		//Handler of the caller
		this.callerHandler= handler;
		
		// generate an uuid in MY_UUID
		try {
			MY_UUID = UUID.fromString("a60f35f0-b93a-11de-8a39-08002009c666");
		} catch (IllegalArgumentException e) {
			LogMsg("UUID is not properly formatted!!!");
		}

		// get a bluetooth adapter
		ba = BluetoothAdapter.getDefaultAdapter();
	    
	}
	
	/**
	 * Obtains and returns a <b>BluetoothServerSocket</b> server_socket to connect to Remote Device or
	 * <b>null</b> on error.
	 * 
	 * @return BluetoothServerSocket or null.
	 */
	public BluetoothServerSocket getServerSocket()
	{
		//try to obtain a server socket to listen
        try													
        {						
        	// MY_UUID is the app's UUID string, also used by the client code
            server_socket = ba.listenUsingRfcommWithServiceRecord("ResourceShare", MY_UUID);

        }
        catch (IOException e)
        {
        	// Some error occured in obtaning a server socket from Remote Device
        	LogMsg("Error:Exception caught in obtaining a Remote server_socket- " + e);
        	
        	server_socket = null;
        }
        
        LogMsg("Obtained a socekt to " + device + " succesfully");
        
        return server_socket;
	}
	
	/**
	 * Tries to connect to the Remote Device. (By listening to incoming requests)
	 * Result of connection will be sent to Caller through Handler Messages.
	 */
	public void run()
	{
        //initialize the socket
		socket = null;
		
        // Keep listening until exception occurs or a socket is returned
        while (true)
        	//TODO: try using while(socket != null) Instead
        {
            try					
            {	
            	//get a connected socket from the server socket
                socket = server_socket.accept();								
            }		
            catch (IOException e)
            {
            	//Notify the caller that connection could not be created, using Handler.
            	callerHandler.obtainMessage(CONNECTION_STATUS, CONNECTION_FAILURE).sendToTarget();
            	
            	LogMsg("Error:Unable to obtain socket from server_socket- " + e);
                break;
            }
            
            // If a connection was accepted
            if (socket != null)
            {
            	//Notify the caller that connection has been created, using Handler.
            	callerHandler.obtainMessage(CONNECTION_STATUS, CONNECTION_SUCCESS).sendToTarget();
            	
            	LogMsg("Connection from the Client device is accepted successfuly");
            	                
            }
            
            
        }//end of while() loop
        
    }

	/**
	 * Closes the socket and stops the on going communication through that
	 * socket.
	 */
	public void stopConnection() {
		
		try {
			socket.close();
			LogMsg("socket has been closed");
			
		} catch (IOException e) {
			
			LogMsg("Error:Cannot close server_socket- " + e);
		}
		
	}
	
	/**
	 * Will cancel the listening socket, and cause the thread to finish.
	 */
    public void cancel()
    {
        try
        {
            server_socket.close();
            LogMsg("server_socket has been closed");
           
        }
        catch (IOException e)
        {
        	LogMsg("Error:Cannot close server_socket- " + e);
        }
        
    }
    
    private void LogMsg(String msg) {
		Log.d("ServerThread", msg);

	}
    
}
