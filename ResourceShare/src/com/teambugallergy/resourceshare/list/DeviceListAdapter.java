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
 * <b>Separate ListAdpter should be defined for different types of rows in the list.</b>
 * @since 04-04-2014
 * @author Adiga
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
	
	@Override
	public int getCount() {

		return data.size();
		
	}

	@Override
	public Object getItem(int position) {

		return data.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		 View vi=convertView;
	        if(convertView==null)
	            vi = inflater.inflate(R.layout.list_row, null);
	        
	     TextView device_name = (TextView)vi.findViewById(R.id.device_name);
	     TextView connectivity_status = (TextView)vi.findViewById(R.id.connectivity_status);
	    
	     RowValues row_values = new RowValues(2);
	     row_values = data.get(position);
	     
	     device_name.setText(row_values.getRowValues(0));
	     connectivity_status.setText(row_values.getRowValues(1));
	     
		return vi;
	}
	
	

}
