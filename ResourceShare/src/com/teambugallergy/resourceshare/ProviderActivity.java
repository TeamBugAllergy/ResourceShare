package com.teambugallergy.resourceshare;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 
 * <i>
 * <br/>--------------------------------------
 * <br/>Constants of this class starts with 9
 * <br/>--------------------------------------
 * <br/>
 * </i>
 * 
 * 04-04-2014
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
