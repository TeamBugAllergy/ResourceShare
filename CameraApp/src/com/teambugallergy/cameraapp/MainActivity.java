package com.teambugallergy.cameraapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


 
public class MainActivity extends Activity{
	/**These are very Important private variables whose values can be altered only by the public functions
	 * 
	 */
	
	
	private Camera camera; // camera object
	private TextView CameraTextViewMessage;//Textview
	private Camera.PictureCallback jpegCallBack;//PictureCallback to process the picture after the camera has clicked  
	private ImageView CameraImageView;
	private Bitmap bmp;
	
	
	
	
	
	
	
	
	/**This method is to Check if the Device has a Camera
	 * IMPORTANT: This method MUST be implemented before any 
	 * other method from this class is implemented because it is here that the camera object is initialized.
	 * If the developer has already initialized the Camera Object then he need not use this method.
	 * 
	 * Arguments: None
	 * Return type int
	 * 
	 * 0: Camera present and Available
	 * 1: Camera present but Busy
	 * 2: Camera not present
	 * 
	 * 
	 */
	public int CheckCamera()
	{
		PackageManager pm = this.getPackageManager();

		if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
		return 2;
		}
		try
		{
			camera=Camera.open();
			
		}
		catch(Exception e)
		{ return 1;}
		return 0;
	}
	
	
	/** This method is to Access the Camera
	 * IMPORTANT: You have to call CheckCamera() before calling this 
	 * method to ensure that this method is used only on devices that have a camera.
	 * This method also initializes the SurfaceView and implements .startPreview()  
	 * 
	 * Arguments: None
	 * Return type int
	 * 0: Unsuccessful
	 * 1: Successful
	 */
	public int OpenCamera()
	{
		SurfaceView view = new SurfaceView(this);
		
		try {
			camera.setPreviewDisplay(view.getHolder()); // feed dummy surface to surface
		} catch (IOException e) { 
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		camera.startPreview();
		return 1;
	}
	
	/**This function is to click a Picture
	 * IMPORTANT: You have to call the OpenCamera() before calling this method
	 * 
	 * Arguments: None
	 * Return type int
	 * 0:	Successfully Captured and stored
	 * 1:	Failure
	 * The Picture that is captured is stored in the directory defined by getExternalStorageDirectory
	 * with the name Teambugallergy.jpg
	 */
	public int ClickPicture(){
			
		jpegCallBack=new Camera.PictureCallback() {		
			public void onPictureTaken(byte[] data, Camera camera) {
				// set file destination and file name
				File destination=new File(Environment.getExternalStorageDirectory(),"Teambugallergy.jpg");
				try {
					Bitmap userImage = BitmapFactory.decodeByteArray(data, 0, data.length);
					// set file out stream
					FileOutputStream out = new FileOutputStream(destination);
					// set compress format quality and stream
					userImage.compress(Bitmap.CompressFormat.JPEG, 90, out);		
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					}
	 		}
		
		};
		try{
		camera.takePicture(null, null, null, jpegCallBack);
		}catch(Exception e){
			return 1;
			
		}
		return 0;
	}
	
	
 
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		CameraTextViewMessage=(TextView)findViewById(R.id.CameraTextViewMessage); // make time left object
		CameraImageView=(ImageView)findViewById(R.id.CameraImageView);

		CheckCamera();
		OpenCamera();
		final Button button = (Button) findViewById(R.id.CameraClick);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            
            ClickPicture();
            CameraTextViewMessage.setText("Image Captured \nPlease Wait");
            new CountDownTimer(2000, 250) {

                public void onTick(long millisUntilFinished) {
                	CameraTextViewMessage.append("..");
                }

                public void onFinish() {
                    bmp = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/Teambugallergy.jpg");
                    CameraImageView.setImageBitmap(bmp);
                }
             }.start();
             System.gc();
             }
        });
	}
	
	
}