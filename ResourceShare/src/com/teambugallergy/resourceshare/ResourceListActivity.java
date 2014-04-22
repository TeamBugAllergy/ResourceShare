package com.teambugallergy.resourceshare;

import android.app.Activity;
import android.os.Bundle;

/**
 * This activity displays a set of Resources using Radio Buttons. The intent
 * received by this Activity contains an array of connected devices. When the
 * user selects a Resource and clicks 'Request' Button, All the devices in the
 * connected_device_list[] will be sent the ResourceId and waits for the result.
 * 
 * @author Adiga 22-04-2014
 */
public class ResourceListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// testing layout
		setContentView(R.layout.activity_main);
	}
}
