package com.teambugallergy.resourceshare.resources;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.teambugallergy.resourceshare.constants.Resources;
import com.teambugallergy.resourceshare.provider_end_resource_specific_activities.ProviderCameraActivity;
import com.teambugallergy.resourceshare.provider_end_resource_specific_activities.ProviderFlashActivity;
import com.teambugallergy.resourceshare.provider_end_resource_specific_activities.ProviderWifiActivity;
import com.teambugallergy.resourceshare.seeker_end_resource_specific_activities.SeekerCameraActivity;
import com.teambugallergy.resourceshare.seeker_end_resource_specific_activities.SeekerFlashActivity;
import com.teambugallergy.resourceshare.seeker_end_resource_specific_activities.SeekerWifiActivity;

/**
 * This class has methods general to all the flashes. General functionalities
 * that you wish to perform on all OR specific resource are defined here.
 * 
 * @author Adiga@TeamBugAllergy
 *  27-04-2014
 */
public class Resource {

	/**
	 * Returns name of the resource with resource_id.
	 * 
	 * @param resource_id
	 *            Resource Id of the Resource.
	 */
	public String getResourceName(int resource_id) {

		switch (resource_id) {

		case Resources.FLASH:

			return "Flash";

		case Resources.GPS:

			return "GPS";

		case Resources.WIFI:

			return "WiFi";

		case Resources.SPEAKER:

			return "Speaker";

		case Resources.CAMERA:

			return "Camera";

			// TODO: other resources
		default:

			return "";
		}

	}

	/**
	 * Returns an Intent object from <i>context</i> to <i>Resource Specific
	 * Activity</i>, whose resource id is <i>resource_id</i>.
	 * 
	 * @param resource_id
	 *            Resource Id of Resource Specific Activity.
	 * @param context
	 *            Context of caller Activity.
	 * @param device_end
	 *            The side of the Activity (Seeker = 0 / Provider = 1) that <i>intent</i> should be of.
	 * 
	 * @return Intent fron <i>context</i> to <i>Resource specific activity</i>.
	 */
	public Intent getIntetToResourceActivity(int resource_id, Context context,
			int device_end) {
		switch (resource_id) {

		case Resources.FLASH:
			if (device_end == 0)
				return new Intent(context, SeekerFlashActivity.class);
			else if (device_end == 1)
				return new Intent(context, ProviderFlashActivity.class);
			
		case Resources.GPS:

			// TODO: 
			/*
			if (device_end == 0)
				return new Intent(context, SeekerGpsActivity.class);
			else if (device_end == 1)
				return new Intent(context, ProviderGpsActivity.class); */

		case Resources.WIFI:
			
			if (device_end == 0)
				return new Intent(context, SeekerWifiActivity.class);
			else if (device_end == 1)
				return new Intent(context, ProviderWifiActivity.class); 

		case Resources.SPEAKER:

			// TODO: 
			/*
			if (device_end == 0)
				return new Intent(context, SeekerSpeakerActivity.class);
			else if (device_end == 1)
				return new Intent(context, ProviderSpeakerActivity.class); */
			
		case Resources.CAMERA:

			if (device_end == 0)
				return new Intent(context, SeekerCameraActivity.class);
			else if (device_end == 1)
				return new Intent(context, ProviderCameraActivity.class); 
			
			// TODO: other resources
		default:

			return null;
		}
	}

	private static void LogMsg(String msg) {
		Log.d("Resource", msg);
	}
}
