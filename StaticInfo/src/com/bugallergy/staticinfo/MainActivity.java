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
		textView.setText("Model: " + PhoneModel);
		String AndroidVersion = android.os.Build.VERSION.RELEASE;
		textView.append("\nAndroid Version: " +AndroidVersion);
		String AndroidBrand = android.os.Build.BRAND;
		textView.append("\nBrand: " +AndroidBrand);
		String androidHardware = android.os.Build.HARDWARE;
		textView.append("\nHardware Profile: " + androidHardware);
		

		
		
	}


}
