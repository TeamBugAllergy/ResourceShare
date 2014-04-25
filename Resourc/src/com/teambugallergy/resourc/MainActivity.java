package com.teambugallergy.resourc;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final Camera cameraobject=null;
        
        
        
        final Button button = (Button) findViewById(R.id.enter);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int x;
                x=checkflash(cameraobject);
            }
        });


    }

    
/*---Function to check for hardware flash Status.
 Arguments: Camera Object(To be created in the oncreate to decrease the load time)
 Return Values:
 0: Device Has hardware Flash and it is not in use currently
 1: Device has Hardware flash but it is not accessible
 2: Device does not have hardware flash---*/
    
public int checkflash(Camera cameraobject)
{
	    if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
	    	try
	    	{
	    		cameraobject=Camera.open();
	
	    	}catch(Exception e)
	    	{
	    		TextView X=(TextView)findViewById(R.id.textview);
	    		X.append("Busy Flash");
	    		return 1;//Has Flash But Busy
	    	}
	    	
	    	 return 0; //Has Flash And free
	    } 
	    else 
	    {
	    	
	        // no FLASH on this device
	        return 2;
	    	
	    }
  
}
}
