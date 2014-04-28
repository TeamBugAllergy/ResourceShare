package com.teambugallergy.wifi;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.Build;

public class MainActivity extends ActionBarActivity implements OnCheckedChangeListener{

	ToggleButton wifi_toggle;
	public WifiManager wifimanager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		wifi_toggle = (ToggleButton)findViewById(R.id.wifiToggle);
		wifimanager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
		wifi_toggle.setOnCheckedChangeListener(this);
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Log.d("Button ID's","buttonview"+buttonView.getId()+"  wifi_toggle" + wifi_toggle.getId());
		if(buttonView.getId() == wifi_toggle.getId()){
			if(isChecked){
				wifimanager.setWifiEnabled(true);
				Toast.makeText(this,"Wifi is now Switched on!" ,Toast.LENGTH_SHORT).show();
			}
			else{
				wifimanager.setWifiEnabled(false);
				Toast.makeText(this, "Wifi Switched Off", Toast.LENGTH_SHORT).show();
			}		
	}

}
