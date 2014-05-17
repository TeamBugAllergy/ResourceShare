package com.teambugallergy.resourceshare.resources;

import java.util.List;

import org.apache.http.util.ByteArrayBuffer;

import com.teambugallergy.resourceshare.constants.Resources;
import com.teambugallergy.resourceshare.provider_end_resource_specific_activities.ProviderCameraActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;

/**
 * This class has methods that allows to use the Camera Resource in the device.
 * The methodologies provide querring for availability (available, unavailable
 * and busy) of the resource, accessing the resource (switching on/off, giving
 * data to OR taking the data from the resource) and other Resource Specific
 * functionalities, if any.
 * 
 * @author Adiga@TeamBugAllergy 05-05-2014
 */
public class MyCamera {

	/**
	 * Availability status returned by availability() method. RESOURCE_AVAILABLE
	 * = 701; RESOURCE_UNAVAILABLE = 702; RESOURCE_BUSY = 703;
	 */

	/**
	 * Save the availability of flash.
	 */
	private int availability = 0;

	/**
	 * Context of the caller.
	 */
	private Context callerContext;

	/**
	 * Handler to which the message has to be sent. After the clicked image is
	 * saved, a message will be sent to this handler to notify about it.
	 */
	private Handler callerHandler;

	/**
	 * Camera object.
	 */
	private Camera camera;

	/**
	 * PictureCallback to process the picture after the camera has clicked an
	 * image.
	 */
	private Camera.PictureCallback jpegCallBack;

	/**
	 * Checks the availabilty of the camera and stores the result in
	 * 'availability'.
	 * 
	 * @param context
	 *            Context of the caller Activity.
	 * @param handler
	 *            Handler of the caller Activity which receives IMAGE_SAVED
	 *            message.
	 */
	public MyCamera(Context context, Handler handler) {
		// set the context of the caller activity
		callerContext = context;

		// caller handler to get image_saved message
		callerHandler = handler;

		// check the availability
		if (callerContext.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			try {
				// obtain a camera object
				camera = Camera.open();

			} catch (Exception e) {
				// Device has Flash, but it is currently busy (BUSY).
				availability = Resources.RESOURCE_BUSY;
			}
			// release the camera
			camera.release();
			camera = null;

			// Device has Flash and it is free now (AVAILABLE).
			availability = Resources.RESOURCE_AVAILABLE;

		} else {
			// Device doesn't have flash (UNAVAILABLE).
			availability = Resources.RESOURCE_UNAVAILABLE;
		}

	}

	/**
	 * Return the availability of the Camera.
	 * 
	 * @return
	 */
	public int availability() {
		// return the availability of the Camera
		return availability;
	}

	/**
	 * This method is used to set the camera Parameters. To avoid all Errors
	 * that can happen when using a camera. This method needn't be called
	 * explicitly. acquireCamera() will call this method. IMPORTANT: This method
	 * is specifically written for the Camera App for the Resource Share by Team
	 * BugAllergy
	 */
	private void setCameraDefaults() {
		// get the original parameters
		Camera.Parameters params = camera.getParameters();

		// Supported picture formats (all devices should support JPEG).
		List<Integer> formats = params.getSupportedPictureFormats();

		if (formats.contains(ImageFormat.JPEG)) {
			params.setPictureFormat(ImageFormat.JPEG);
			params.setJpegQuality(100);
		} else
			params.setPictureFormat(PixelFormat.RGB_565);

		// Now the supported picture sizes.
		List<Size> sizes = params.getSupportedPictureSizes();
		Camera.Size size = sizes.get(sizes.size() - 1);
		params.setPictureSize(size.width, size.height);

		// Set the brightness to auto.
		params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);

		// Set the flash mode to auto.
		params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);

		// Set the scene mode to portrait.
		params.setSceneMode(Camera.Parameters.SCENE_MODE_PORTRAIT);

		// Lastly set the focus to auto.
		params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

		try {
			// set the updated parameters to the camera
			camera.setParameters(params);
		} catch (Exception e) {

			LogMsg("ERROR: In setting the parameters- " + e);
		}
	}

	/**
	 * Tries to acquire the camera. Sends the result back to caller.
	 * 
	 * @return result of acquire (true/false)
	 */
	public Boolean acquireCamera() {
		// create a surface view object
		SurfaceView view = new SurfaceView(callerContext);

		try {
			// obtain a camera object
			camera = Camera.open();

			// feed dummy surface to surface
			camera.setPreviewDisplay(view.getHolder());

			// start the preview
			camera.startPreview();

		} catch (Exception e) {
			LogMsg("ERROR: In acquiring the camera- " + e);

			// return the fail result
			return false;
		}

		// set the camera parameters
		setCameraDefaults();

		// return the success result
		return true;
	}

	/**
	 * This function is to click a Picture. It sets callback handler for
	 * processing of clicked image. You have to call the OpenCamera() before
	 * calling this method The Picture that is captured is stored in the
	 * directory defined by getExternalStorageDirectory with the name
	 * Teambugallergy.jpg
	 * 
	 * @return <i>true</i> For successfully captured and stored. <i>false</i>
	 *         Failure.
	 */
	public Boolean takePicture() {

		jpegCallBack = new Camera.PictureCallback() {

			// CallBack method to process the taken picture.
			public void onPictureTaken(byte[] data, Camera camera) {

				// TODO:
				//add 7,4,4 to start of the array
				ByteArrayBuffer tmp_buffer = new ByteArrayBuffer(3);
				
				//store 743, ie Start of the IMAGE_DATA
				tmp_buffer.append(7);
				tmp_buffer.append(4);
				tmp_buffer.append(4);
				
				//now add the actual image data bytes
				tmp_buffer.append(data, 0, data.length);
				
				//store 743, ie End of the IMAGE_DATA
				tmp_buffer.append(7);
				tmp_buffer.append(4);
				tmp_buffer.append(4);
				
				//the data to be sent
				byte[] image_data = null;
				image_data = tmp_buffer.toByteArray();
								
				
				// Directly send the image data bytes to the
				// connected_seeker_device
				
				// TODO: NOTE:- There is no "x:y" form.... based on this new
				// formate, receiveData() method will come to that the data
				// received contains image_data bytes
				ProviderCameraActivity.getConnectedSeekerDevice()
						.sendData(image_data);
				LogMsg("Provider is sending the image data bytes directly to " + ProviderCameraActivity.getConnectedSeekerDevice().getDevice().getName());

				// notify the potential provider about this
				callerHandler.obtainMessage(Resources.IMAGE_DATA)
						.sendToTarget();
				LogMsg("notifying the provider device by sending IMAGE_DATA");
				
				LogMsg("Image Data: " + image_data[0] +","+  image_data[1] +","+ image_data[2] +": "+ image_data[image_data.length-3] +","+ image_data[image_data.length-2] +","+ image_data[image_data.length-1] );
			}

		};

		try {
			// try to take the picture.
			// specify the callback method to be called after taking the picture
			camera.takePicture(null, null, jpegCallBack);
		} catch (Exception e) {
			
			LogMsg("ERROR:Couldn't capture the image- " + e);
			return false;

		}
		return true;
	}

	/**
	 * Method to stop the camera preview and release the acquired camera object.
	 */
	public void releaseCamera() {
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	private static void LogMsg(String msg) {
		Log.d("Camera", msg);
	}
}
