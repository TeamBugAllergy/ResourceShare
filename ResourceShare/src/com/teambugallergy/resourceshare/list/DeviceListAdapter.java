package com.teambugallergy.resourceshare.list;

import com.teambugallergy.resourceshare.R;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * This Class takes care of adding or updating the rows in the list.
 * This class will have Layout Inflater which does the adding,removing etc operations on the list.
 * 
 * <b>The RowValues of this Adapter has 3 feilds. [0]th has name of BluetoothDevice,[1]st tells if the device is connectable
 *  and [2]nd tells if the Row has already been clicked before("1") or not("0")</b> 
 * 
 * <b>Separate ListAdpter should be defined for different types of rows in the list.</b>
 * @since 06-04-2014
 * @author TeamBugAllergy
 * 
 */

public class DeviceListAdapter extends BaseAdapter{

	/**
	 * ArrayList to hold the Array of rows of type RowValues. 
	 */
	private ArrayList<RowValues> data;
	
	/**
	 * To inflate the rows of list.
	 */
    private static LayoutInflater inflater=null;
    
    /**
     * Initializes the Adapter object with context and rows of list.
     * @param activity Context in which List is used.
     * @param data Array of rows.
     */
	public DeviceListAdapter(Activity activity, ArrayList<RowValues> data) {
		
		this.data = data;
		
		//get a inflater
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}
	
	/**
	 * @return Number of rows/items in the list.
	 */
	@Override
	public int getCount() {

		return data.size();
		
	}

	/**
	 * @return The row Object at index <b>position</b> in the list.
	 */
	@Override
	public Object getItem(int position) {

		return data.get(position);
	}

	/**
	 * @return Does not return Id. :( 
	 */
	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		 View vi=convertView;
	        if(convertView==null)
	            vi = inflater.inflate(R.layout.list_row, null);
	        
	     //Get the references to View in the row   
	     TextView device_name = (TextView)vi.findViewById(R.id.device_name);
	     TextView connectivity_status = (TextView)vi.findViewById(R.id.connectivity_status);
	    
	     //Get the row values from object of RowValues.  
	     RowValues row_values = new RowValues(3);
	     row_values = data.get(position);
	     
	     //Set the vallues of Views with values of object obatined above.
	     device_name.setText(row_values.getRowValues(0));
	     connectivity_status.setText(row_values.getRowValues(1));
	     
		return vi;
	}
	
	

}
