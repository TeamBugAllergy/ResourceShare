package com.bugallergy.staticinfo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		String PhoneModel = android.os.Build.MODEL;
		String AndroidVersion = android.os.Build.VERSION.RELEASE;
		String AndroidBrand = android.os.Build.BRAND;
		String AndroidDevice = android.os.Build.DEVICE;
		String AndroidManufacturer = android.os.Build.MANUFACTURER;
		
		
		TextView textView = (TextView) findViewById(R.id.textview);
		textView.setText("Model : "+PhoneModel+"\nBrand : "+AndroidBrand+"\nManufacturer : "+AndroidManufacturer+"\nVersion : "+AndroidVersion);
		
	}


}
