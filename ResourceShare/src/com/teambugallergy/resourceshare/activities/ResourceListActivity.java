package com.teambugallergy.resourceshare.activities;

import com.teambugallergy.resourceshare.R;
import com.teambugallergy.resourceshare.bluetooth.ConnectedDevice;
import com.teambugallergy.resourceshare.constants.Resources;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

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
	 * Context of the ResourceListActivity
	 */
	private static Context resourceListActivityContext;

	/**
	 * An array of BluetoothDevices that are connected to.
	 */
	private ConnectedDevice[] connected_device_list;

	/**Testing
	 * Handler to receive messages.
	 */
	private static Handler ResourceListActivityHandler = new Handler() {

		public void handleMessage(Message msg) {

			// if the message is from Scanner
			if (msg.what == Resources.REQUEST_STATUS) 
			{
				if(Integer.parseInt(msg.obj.toString()) == Resources.REQUEST_ACCEPTED)
				{
					LogMsg("Provider has accepted to share the resource.");
					Toast.makeText(resourceListActivityContext, "Provider has accepted to share the resource.", Toast.LENGTH_SHORT).show();
				}
				else if(Integer.parseInt(msg.obj.toString()) == Resources.REQUEST_REJECTED)
				{
					LogMsg("Provider has rejected to share the resource.");
					Toast.makeText(resourceListActivityContext, "Provider has rejected to share the resource.", Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LogMsg("INSIDE:onCreate");
		
		// testing layout
		setContentView(R.layout.activity_main);
		
		resourceListActivityContext = this;
		
		//get the array of connected device objects
		this.connected_device_list = SeekerActivity.getConnectedDeviceList();
				
		int i;
		for( i=0; connected_device_list[i] != null; i++)
		{
			//always first element in the array will be 'what' of the data/message 
			// request id is sent in the format- 'what:data'
			// i.e REQUESTING_RESOURCE_ID:Resources.FLASH 
			connected_device_list[i].sendData( (Resources.REQUESTING_RESOURCE_ID + ":" + Resources.FLASH).getBytes() );
			
			//set the new Handler 
			connected_device_list[i].setCallerHandler(ResourceListActivityHandler);
			//start listening to REPLY from connected devices.
			connected_device_list[i].receiveData();
			
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
