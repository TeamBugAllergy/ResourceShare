package com.teambugallergy.list;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      
        
        final Button button = (Button) findViewById(R.id.ResourceDisplayNextButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int x;
                switch(getResourceDisplayRadio())
                {
                case "Flash": //Call the Flash Activity
                				Log.d("Onclick","Flash");
                				break;
                case "Camera": //Call the Camera Activity
                	Log.d("Onclick","Camera");
                				break;
                case "Wifi": //Call the Wifi Activity
                	Log.d("Onclick","Wifi");
                				break;
                }
                
            
            
            
            
            
            
            }
        });
       
       
       
       
    }
    
    /*---Function to Get the Radio Button Selected
    Arguments: 
    Return Values:
    (String) Flash: If the Radio Button Selected is Flash
    (String) Camera: If the Radio Button Selected is Camera
    (String) Wifi: If the Radio Button Selected is Wifi
    (String) Null: If no Radio Button is Selected---*/
    
    
    
    String getResourceDisplayRadio()
    {
    RadioGroup ResourceDisplayRadioGroup=(RadioGroup) findViewById(R.id.ResourceDisplayRadioGroup);
    
    
    /*The RadioGroup That has Radio buttons to select a particular Resource*/
        
    int checkedRadioButton = ResourceDisplayRadioGroup.getCheckedRadioButtonId();
    
    /* Selected Radio Button*/
    switch (checkedRadioButton) {
    case R.id.ResourceDisplayRadioButton1 : 
                     	              return "Flash";
    case R.id.ResourceDisplayRadioButton2 : 
  		                      return "Camera";
    case R.id.ResourceDisplayRadioButton3 : 
  		                      return "Wifi";
    							}
    return "Null";
    }
    
    
    
    

  
}
