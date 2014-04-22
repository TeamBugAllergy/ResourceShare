package com.teambugallergy.resourceshare;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

/**
 * This Activity allows the user to switch on/off the bluetooth and starts
 * either Provider Module or Seeker Module, based on the user requirments. <i> <br/>
 * -------------------------------------- <br/>
 * Constants of this class starts with 7 <br/>
 * -------------------------------------- <br/>
 * </i>
 * 
 * 04-04-2014
 * 
 * @author Adiga
 * 
 */
public class MainActivity extends Activity implements OnClickListener {

	/**
	 * Used by startActivityForResult() to enable the Bluetooth. Its value is
	 * 70.
	 */
	private final int BT_ENABLE = 70;
	/**
	 * BluetoothAdapter used for accessing Bluetooth module
	 */
	BluetoothAdapter ba;

	// ToggleButton
	/**
	 * used to switch on and off the bluetooth
	 */
	ToggleButton bluetooth_switch;

	// Buttons
	/**
	 * Takes the user to Provider Module
	 */
	Button provide_resource;
	/**
	 * Takes the user to Seeker Module
	 */
	Button seek_resource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// obtain a reference to BluetoothAdapter
		ba = BluetoothAdapter.getDefaultAdapter();

		// only if device supports bluetooth
		if (ba != null) {
			// allow the user to switch on and off the blueetooth
			bluetooth_switch = (ToggleButton) findViewById(R.id.bluetooth_switch);

			provide_resource = (Button) findViewById(R.id.provide_resource);
			provide_resource.setOnClickListener(this);

			seek_resource = (Button) findViewById(R.id.seek_resource);
			seek_resource.setOnClickListener(this);

			// based on the current state of Bluetooth set the State of
			// ToggleButoon
			if (ba.isEnabled()) {
				// Show that bluetooth is currenlty switched on
				bluetooth_switch.setChecked(true);

				// set the buttons as clickable
				provide_resource.setClickable(true);
				seek_resource.setClickable(true);

				LogMsg("Bluetooth was Switched on");
			} else {
				// Show that bluetooth is currenlty switched off
				bluetooth_switch.setChecked(false);

				// set the buttons as non clickable
				provide_resource.setClickable(false);
				seek_resource.setClickable(false);

				LogMsg("Bluetooth was Switched off");
			}

			bluetooth_switch
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {

							// "isChecked" will have new state of the
							// ToggleButton.

							// If the value is true, switch on the bluetooth
							if (isChecked == true) {
								LogMsg("Switching on the Bluetooth");

								// Ask the user to Switch on the Bluetooth
								Intent i = new Intent(
										BluetoothAdapter.ACTION_REQUEST_ENABLE);
								startActivityForResult(i, BT_ENABLE); // start
																		// the
																		// bluetooth
							}

							// Else switch off the bluetooth
							else {
								LogMsg("Switching off the Bluetooth");

								// Switch off the Bluetooth
								ba.disable();
							}

						}
					});

		} else {
			// Device does not support Bluetooth
			makeToast(
					"Your device does not support Bluetooth. Please close the application.",
					1);
		}
	}

	/**
	 * This method will be called when Bluetooth Enable Request Dialog returns.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// if the Result is from BluetoothAdapter.ACTION_REQUEST_ENABLE action
		if (requestCode == BT_ENABLE) {
			// if the Request is accepted i.e Bluetooth is Switching on
			if (resultCode == RESULT_OK) {

				// set the text of bluetooth_switch as on
				bluetooth_switch.setChecked(true);

				// set the buttons as clickable
				provide_resource.setClickable(true);
				seek_resource.setClickable(true);

				LogMsg("Bluetooth has been Switched on");
			}
			// if User declined to Switch on the bluletooth
			else {

				// set the text of bluetooth_switch as off
				bluetooth_switch.setChecked(false);

				// set the buttons as non clickable
				provide_resource.setClickable(false);
				seek_resource.setClickable(false);

				LogMsg("Bluetooth is still Switched off");
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == provide_resource.getId()) {
			// TODO:redirect to ProviderActivty
			// redirect to SeekerActivty
			Intent i = new Intent(this, ProviderActivity.class);
			startActivity(i);

			// finish this Activity
			// finish();

		}

		if (v.getId() == seek_resource.getId()) {
			// redirect to SeekerActivty
			Intent i = new Intent(this, SeekerActivity.class);
			startActivity(i);

			// finish this Activity
			// finish();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		// based on the current state of Bluetooth set the State of ToggleButoon
		if (ba.isEnabled()) {
			// Show that bluetooth is currenlty switched on
			bluetooth_switch.setChecked(true);

			// set the buttons as clickable
			provide_resource.setClickable(true);
			seek_resource.setClickable(true);

			LogMsg("Bluetooth was Switched on");
		} else {
			// Show that bluetooth is currenlty switched off
			bluetooth_switch.setChecked(false);

			// set the buttons as non clickable
			provide_resource.setClickable(false);
			seek_resource.setClickable(false);

			LogMsg("Bluetooth was Switched off");
		}

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// based on the current state of Bluetooth set the State of ToggleButoon
				if (ba.isEnabled()) {
					// Show that bluetooth is currenlty switched on
					bluetooth_switch.setChecked(true);

					// set the buttons as clickable
					provide_resource.setClickable(true);
					seek_resource.setClickable(true);

					LogMsg("Bluetooth was Switched on");
				} else {
					// Show that bluetooth is currenlty switched off
					bluetooth_switch.setChecked(false);

					// set the buttons as non clickable
					provide_resource.setClickable(false);
					seek_resource.setClickable(false);

					LogMsg("Bluetooth was Switched off");
				}
				
	}

	/**
	 * used to make toasts
	 * 
	 * @param msg
	 *            String to be displayed.
	 * @param dur
	 *            <b>0</b> for Toast.LENGTH_SHORT and <b>1</b> for
	 *            Toast.LENGTH_LONG
	 */
	private void makeToast(String msg, int dur) {
		if (dur == 0)
			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	private void LogMsg(String msg) {
		Log.d("MainActivity", msg);
	}

}