package com.bugallergy.staticinfo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TextView textView = (TextView) findViewById(R.id.textview);

		String PhoneModel = android.os.Build.MODEL;
		textView.setText("Model: "+PhoneModel);
		String AndroidVersion = android.os.Build.VERSION.RELEASE;
		textView.setText("Android Version: "+AndroidVersion);
		String AndroidBrand = android.os.Build.BRAND;
		textView.setText("Brand: "+AndroidBrand);
		String AndroidHardware = android.os.Build.HARDWARE;
		textView.setText("Hardware Profile: ");

		
		
	}


}
