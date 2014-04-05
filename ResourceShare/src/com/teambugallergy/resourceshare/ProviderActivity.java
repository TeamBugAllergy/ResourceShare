package com.teambugallergy.resourceshare;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * This Activity just listens to requests from Seeker devices. It will wait for request from a seeker device.
 * A progress bar is displayed untill a device is found.
 * Once a request is accepted, it will save the ConnectedDevice object associated with that device.
 * Then Seeker device sends the Resource Id. The user will be prompted to accept to share that resource.
 * The result will be sent to the Seeker Device. 
 * <i>
 * <br/>--------------------------------------
 * <br/>Constants of this class starts with 9
 * <br/>--------------------------------------
 * <br/>
 * </i>
 * 
 * 06-04-2014
 * @author Adiga
 *
 */
public class ProviderActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public void onClick(View arg0) {
	
	}
	
	private void LogMsg(String msg) {
		Log.d("ProviderActivity", msg);
	}
}
