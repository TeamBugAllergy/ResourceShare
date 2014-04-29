package com.teambugallergy.resourceshare.activities;

import com.teambugallergy.resourceshare.R;
import com.teambugallergy.resourceshare.bluetooth.ConnectedDevice;
import com.teambugallergy.resourceshare.constants.Resources;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
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
<<<<<<< HEAD
import android.widget.TextView;
=======
>>>>>>> fd9f47a389d161aa3eac65715c2a47002673e55c
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
<<<<<<< HEAD

	/**
	 * Maximum of 10 devices can be stored in the connected_device_list[]. i.e
	 * Maximum of 10 Resource Providers are allowed.
	 */
	private final int MAX_CONNECTED_DEVICES = 10;
=======
>>>>>>> fd9f47a389d161aa3eac65715c2a47002673e55c

	/**
	 * Context of the ResourceListActivity
	 */
	private static Context resourceListActivityContext;

	/**
	 * An array of BluetoothDevices that are connected to.
	 */
	private static ConnectedDevice[] connected_device_list;

	/**
	 * An array of ConnectedDevice objects that are there in
	 * connected_device_list[] and have REQUEST_STATUS as ACCEPTED. These
	 * devices have been accepted to share the requested resource. So they
	 * become Potential Provider Devices.
	 */
	private static ConnectedDevice[] potential_provider_list;

	/**
	 * Global index for potential_provider_list[] array. It is incremented each
	 * time a potential provider device is added to potential_provider_list[].
	 */
	private static int potential_provider_num = 0;

	/**
	 * A button to send the resource_requests to all the connected devices in
	 * the connected_device_list[]
	 */
	private Button request_resource;

	/**
<<<<<<< HEAD
	 * A button that will be enabled only if atleast ONE POTENTIAL PROVIDER
	 * device.
	 */
	private static Button resource_list_next;

	/**
	 * A textview to display the status of the requeted resource in all the
	 * connected provider devices. In future this can be replaced with a
	 * ListView.
	 */
	private static TextView resource_list_resource_status;

	/**
	 * Group of radio buttons. Each radio button will have a name of the
	 * resource to be requested. In future this can be replaced with a ListView.
	 */
	private RadioGroup resource_list_radio_group;

	// -----------------------------------------------------------------------------------
	/**
=======
	 * A button to send the resource_requests to all the connected devices in the connected_device_list[]
	 */
	private Button request_resource;
	
	/**
	 * Group of radio buttons. Each radio button will have a name of the resource to be requested.
	 */
	private RadioGroup resource_list_radio_group;
	
	// -----------------------------------------------------------------------------------
	/**Testing
>>>>>>> fd9f47a389d161aa3eac65715c2a47002673e55c
	 * Handler to receive messages.
	 */
	private static Handler ResourceListActivityHandler = new Handler() {

		public void handleMessage(Message msg) {

			// if the message is from Scanner
<<<<<<< HEAD
			// IT ALSO ASSUMES RESOURCE_AVAILABLE condition.
			// msg.arg2 will have the index of the ConnectedDevice object from
			// which data has been received, in connected_device_list[].
			if (msg.what == Resources.REQUEST_STATUS) {
				// Sender BluetoothDevice object.
				BluetoothDevice sender = connected_device_list[msg.arg2]
						.getDevice();

				if (Integer.parseInt(msg.obj.toString()) == Resources.REQUEST_ACCEPTED) {
					// add the status info to TextView
					// resource_list_resource_status
					resource_list_resource_status.append(sender.getName()
							+ ": Accepted\n");

					LogMsg(sender.getName()
							+ " has accepted to share the resource.");
					Toast.makeText(
							resourceListActivityContext,
							sender.getName()
									+ " has accepted to share the resource.",
							Toast.LENGTH_SHORT).show();

					// TODO@PG: write a new constructor for ConnectedDevice
					// class, which takes connected device object as parameter.

					// create an array of ConnectedDevice object which contains
					// only POTENTIAL PROVIDER DEVICES using
					// connected_device_list[]
					potential_provider_list[potential_provider_num] = new ConnectedDevice(
							connected_device_list[msg.arg2].getDevice(),
							connected_device_list[msg.arg2].getSocket(),
							ResourceListActivityHandler);
					//increment the index
					potential_provider_num++;

					// if the 'next' button is currently gone(hidden),
					if (resource_list_next.getVisibility() == View.GONE) {
						// Show the button
						resource_list_next.setVisibility(View.VISIBLE);
					}

				} else if (Integer.parseInt(msg.obj.toString()) == Resources.REQUEST_REJECTED) {
					// add the status info to TextView
					// resource_list_resource_status
					resource_list_resource_status.append(sender.getName()
							+ ": Rejected\n");

					LogMsg(sender.getName()
							+ " has rejected to share the resource.");
					Toast.makeText(
							resourceListActivityContext,
							sender.getName()
									+ " has rejected to share the resource.",
							Toast.LENGTH_LONG).show();
				}
			}
			if (msg.what == Resources.RESOURCE_STATUS) {
				// Sender BluetoothDevice object.
				BluetoothDevice sender = connected_device_list[msg.arg2]
						.getDevice();

				if (Integer.parseInt(msg.obj.toString()) == Resources.RESOURCE_UNAVAILABLE) {
					// add the status info to TextView
					// resource_list_resource_status
					resource_list_resource_status.append(sender.getName()
							+ ": Resource Unavailable.\n");

					LogMsg(sender.getName()
							+ " device doesn't have the resource so cannot be shared");
					Toast.makeText(
							resourceListActivityContext,
							sender.getName()
									+ " device doesn't have the resource so cannot be shared",
							Toast.LENGTH_LONG).show();
				} else if (Integer.parseInt(msg.obj.toString()) == Resources.RESOURCE_BUSY) {
					// add the status info to TextView
					// resource_list_resource_status
					resource_list_resource_status.append(sender.getName()
							+ ": Resource Busy\n");

					LogMsg("Resource is busy so cannot be shared.");
					Toast.makeText(resourceListActivityContext,
							"Resource is busy so cannot be shared.",
							Toast.LENGTH_LONG).show();
=======
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
>>>>>>> fd9f47a389d161aa3eac65715c2a47002673e55c
				}
			}
		}
	};
<<<<<<< HEAD

	// -----------------------------------------------------------------------------------

=======
	// -----------------------------------------------------------------------------------
	
>>>>>>> fd9f47a389d161aa3eac65715c2a47002673e55c
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LogMsg("INSIDE:onCreate");
<<<<<<< HEAD

		// set the layout for the activity
		setContentView(R.layout.activity_resource_list);

		// button to send the requests
		request_resource = (Button) findViewById(R.id.resource_list_request_button);
		request_resource.setOnClickListener(this);

		// button to goto next activity when atleast one Potential Provider has
		// been found.
		resource_list_next = (Button) findViewById(R.id.resource_list_next);
		resource_list_next.setOnClickListener(this);

		// radio button group
		resource_list_radio_group = (RadioGroup) findViewById(R.id.resource_list_radio_group);

		// textview to display the status of the requested resource.
		resource_list_resource_status = (TextView) findViewById(R.id.resource_list_requested_resource_status);

		// save the context
=======
		
		// testing layout
		setContentView(R.layout.activity_resource_list);
		
		//button to send the requests
		request_resource = (Button)findViewById(R.id.resource_list_request_button);
		request_resource.setOnClickListener(this);
		
		//radio button group
		resource_list_radio_group = (RadioGroup) findViewById(R.id.resource_list_radio_group);
		
		//save the context
>>>>>>> fd9f47a389d161aa3eac65715c2a47002673e55c
		resourceListActivityContext = this;

		// get the array of connected device objects
		this.connected_device_list = SeekerActivity.getConnectedDeviceList();
<<<<<<< HEAD

		for (int i = 0; connected_device_list[i] != null; i++) {
			// set the new Handler
			connected_device_list[i]
					.setCallerHandler(ResourceListActivityHandler);
			connected_device_list[i].setDeviceIndex(i);
		}

		// Create MAX_CONNECTED_DEVICES number of objects.
		// Array of connected devices to be passed to next activity
		potential_provider_list = new ConnectedDevice[MAX_CONNECTED_DEVICES];
		potential_provider_num = 0;
		
	}

	@Override
	public void onClick(View arg0) {

		// onClick of request_resource
		if (arg0.getId() == request_resource.getId()) {
			int i;
			for (i = 0; connected_device_list[i] != null; i++) {
				// always first element in the array will be 'what' of the
				// data/message
				// request id is sent in the format- 'what:data'
				// i.e REQUESTING_RESOURCE_ID:Resources.FLASH
				connected_device_list[i]
						.sendData((Resources.REQUESTING_RESOURCE_ID + ":" + getCheckedResourceId())
								.getBytes());

				// start listening to REPLY from connected devices.
				connected_device_list[i].receiveData();

				LogMsg("[" + i + "]:"
						+ connected_device_list[i].getDevice().getName());
			}
			if (i == 0) {
				LogMsg("No devices are connected");
			}

			// clear the TextView request_resource to display the resource
			// status.
			// In hadnler, the text of this view will be appended as
			// REQUEST_STATUS and RESOURCE_STATUS messages are received.
			resource_list_resource_status.setText(getCheckedRadioButton()
					.getText() + " Status:-\n");

		}

		// onClick of resource_list_next
		else if (arg0.getId() == resource_list_next.getId()) {
			resource_list_resource_status.setText("Potential providers:\n");

			for (int i = 0; potential_provider_list[i] != null; i++) {
				resource_list_resource_status.append(potential_provider_list[i]
						.getDevice().getName() + "\n");
			}
			// If there is atleast one potential provider,
			// then only goto next activity.
			if (potential_provider_num > 0) {
				// TODO: goto Resource Specific activity.
				
				//TODO@PG a package for Resource Specific Activities.
				//Write Resource specific classes in '*.resources' package.
				
			}
		}

	}

	/**
	 * Returns the <i>potential_provider_list[]</i> of this object.
	 * 
	 * @return array of ConnectedDevice objects.
	 */
	public static ConnectedDevice[] getPotentialDeviceList() {
		return potential_provider_list;
=======
		
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
>>>>>>> fd9f47a389d161aa3eac65715c2a47002673e55c
	}

	/**
	 * Get the View Id of the checked radio button from the group and return the
	 * resource_id associatd with that resource.
	 * 
	 * @return resource_id of the checked resource.
	 */
	private int getCheckedResourceId() {
		int resource_id = 0;

		switch (resource_list_radio_group.getCheckedRadioButtonId()) {
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

		// TODO: other resources.

		default:
			resource_id = -1;
			break;
		}

		return resource_id;
	}

	/**
	 * Get the RadioButton object of the checked radio button from the group and
	 * return it.
	 * 
	 * @return Object of RadioButton of the checked radio button from the group.
	 */
	private RadioButton getCheckedRadioButton() {
		RadioButton checked_button = (RadioButton) findViewById(resource_list_radio_group
				.getCheckedRadioButtonId());
		return checked_button;
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
<<<<<<< HEAD

=======
	
>>>>>>> fd9f47a389d161aa3eac65715c2a47002673e55c
}
