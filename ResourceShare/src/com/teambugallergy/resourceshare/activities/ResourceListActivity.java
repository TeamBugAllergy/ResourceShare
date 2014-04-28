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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * This activity displays a set of Resources using Radio Buttons. The intent
 * received by this Activity contains an array of connected devices. When the
 * user selects a Resource and clicks 'Request' Button, All the devices in the
 * connected_device_list[] will be sent the ResourceId and waits for the result.
 * 
 * @author Adiga 22-04-2014
 */
public class ResourceListActivity extends Activity implements OnClickListener {

	/**
	 * Context of the ResourceListActivity
	 */
	private static Context resourceListActivityContext;

	/**
	 * An array of BluetoothDevices that are connected to.
	 */
	private ConnectedDevice[] connected_device_list;

	/**
	 * A button to send the resource_requests to all the connected devices in the connected_device_list[]
	 */
	private Button request_resource;
	
	/**
	 * Group of radio buttons. Each radio button will have a name of the resource to be requested.
	 */
	private RadioGroup resource_list_radio_group;
	
	// -----------------------------------------------------------------------------------
	/**Testing
	 * Handler to receive messages.
	 */
	private static Handler ResourceListActivityHandler = new Handler() {

		public void handleMessage(Message msg) {

			// if the message is from Scanner
			//IT ALSO ASSUMES RESOURCE_AVAILABLE condition.
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
					Toast.makeText(resourceListActivityContext, "Provider has rejected to share the resource.", Toast.LENGTH_LONG).show();
				}
			}
			if(msg.what == Resources.RESOURCE_STATUS)
			{
				if(Integer.parseInt(msg.obj.toString()) == Resources.RESOURCE_UNAVAILABLE)
				{
					LogMsg("Provider device doesn't have the resource so cannot be shared");
					Toast.makeText(resourceListActivityContext, "Provider device doesn't have the resource so cannot be shared", Toast.LENGTH_LONG).show();
				}
				else if(Integer.parseInt(msg.obj.toString()) == Resources.RESOURCE_BUSY)
				{
					LogMsg("Resource is busy so cannot be shared.");
					Toast.makeText(resourceListActivityContext, "Resource is busy so cannot be shared.", Toast.LENGTH_LONG).show();
				}
			}
		}
	};
	// -----------------------------------------------------------------------------------
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LogMsg("INSIDE:onCreate");
		
		// testing layout
		setContentView(R.layout.activity_resource_list);
		
		//button to send the requests
		request_resource = (Button)findViewById(R.id.resource_list_request_button);
		request_resource.setOnClickListener(this);
		
		//radio button group
		resource_list_radio_group = (RadioGroup) findViewById(R.id.resource_list_radio_group);
		
		//save the context
		resourceListActivityContext = this;
		
		//get the array of connected device objects
		this.connected_device_list = SeekerActivity.getConnectedDeviceList();
		
		for(int i=0; connected_device_list[i] != null; i++)
		{
		//set the new Handler 
		connected_device_list[i].setCallerHandler(ResourceListActivityHandler);
		}
				
						
	}

	@Override
	public void onClick(View arg0) {
		//onClick of request_resource
		if(arg0.getId() == request_resource.getId())
		{
			int i;
			for( i=0; connected_device_list[i] != null; i++)
			{
				//always first element in the array will be 'what' of the data/message 
				// request id is sent in the format- 'what:data'
				// i.e REQUESTING_RESOURCE_ID:Resources.FLASH 
				//testing connected_device_list[i].sendData( (Resources.REQUESTING_RESOURCE_ID + ":" + Resources.FLASH).getBytes() );
				connected_device_list[i].sendData( (Resources.REQUESTING_RESOURCE_ID + ":" + getCheckedResourceId()).getBytes() );
				
				//start listening to REPLY from connected devices.
				connected_device_list[i].receiveData();
				
				LogMsg("[" + i + "]:" + connected_device_list[i].getDevice().getName());
			}
			if(i ==0 )
			{
				LogMsg("No devices are connected");
			}
		}
	}
	
	/**
	 * Get the View Id of the checked radio button from the group and return the resource_id associatd with that resource.
	 * @return resource_id of the checked resource.
	 */
	private int getCheckedResourceId()
	{
		int resource_id = 0;
		
		switch(resource_list_radio_group.getCheckedRadioButtonId())
		{
		case R.id.resource_list_flash:
			resource_id = Resources.FLASH;
			break;
			
		case R.id.resource_list_gps:
			resource_id = Resources.GPS;
			break;
		
		case R.id.resource_list_wifi:
			resource_id = Resources.WIFI;
			break;
			
		case R.id.resource_list_camera:
			resource_id = Resources.CAMERA;
			break;
			
		//TODO: other resources.	
			
		default:
			resource_id = -1;
			break;
		}
		
		return resource_id;
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
