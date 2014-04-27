package com.teambugallergy.resourceshare;

import com.teambugallergy.resourceshare.bluetooth.ConnectedDevice;
import com.teambugallergy.resourceshare.constants.Resources;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * This activity displays a set of Resources using Radio Buttons. The intent
 * received by this Activity contains an array of connected devices. When the
 * user selects a Resource and clicks 'Request' Button, All the devices in the
 * connected_device_list[] will be sent the ResourceId and waits for the result.
 * 
 * @author Adiga 22-04-2014
 */
public class ResourceListActivity extends Activity {


	/**
	 * An array of BluetoothDevices that are connected to.
	 */
	private ConnectedDevice[] connected_device_list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LogMsg("INSIDE:onCreate");
		
		// testing layout
		setContentView(R.layout.activity_main);
		
		//get the array of connected device objects
		this.connected_device_list = SeekerActivity.connected_device_list;
				
		int i;
		for( i=0; connected_device_list[i] != null; i++)
		{
			//always first element in the array will be 'what' of the data/message 
			connected_device_list[i].sendData( (""+Resources.REQUESTING_RESOURCE_ID).getBytes() );
			
			LogMsg("[" + i + "]:" + connected_device_list[i].getDevice().getName());
		}
		if(i ==0 )
		{
			LogMsg("No devices are connected");
		}
		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		finish();
		LogMsg("Finished");
		
		LogMsg("onStop");
	}

	private static void LogMsg(String msg) {
		Log.d("ResourceListActivity", msg);
	}
}
