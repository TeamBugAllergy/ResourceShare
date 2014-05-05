package com.teambugallergy.resourceshare.list;

import java.util.ArrayList;

import com.teambugallergy.resourceshare.activities.SeekerActivity;

import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * This Class takes care of adding and removing of rows to the list. The object
 * of this class should be created with <b>specific ListAdapter</b> class. <br/>
 * <i>This class <b>will not provide OnItemClickListner</b>, because the object
 * of this class can be used on any type of ListAdapter. So The class which uses
 * the object of this class should provide it's own OnItemClickListener<i>
 * 
 * @since 04-04-2014
 * @author Adiga@TeamBugAllergy
 * 
 */
public class Lists {

	/**
	 * ListView object to be used.
	 */
	ListView list;

	/**
	 * Base adapter reference that points to User defined Adapter class
	 */
	BaseAdapter adapter;

	/**
	 * ArrayList<RowValues> associated with the ListView list.
	 */
	ArrayList<RowValues> items = new ArrayList<RowValues>();

	/**
	 * Initializes the Lists object.
	 * 
	 * @param list
	 *            ListView object to be used.
	 * @param adapter
	 *            DeviceListAdapter to be assigned to the ListView list.<br/>
	 *            <i>This object should have <b>ArrayList same as items</b></i>
	 * @param items
	 *            ArrayList<RowValues> associated with adapter.
	 */
	public Lists(ListView list, DeviceListAdapter adapter,
			ArrayList<RowValues> items) {

		// ListView to be used.
		this.list = list;

		// Use a BaseAdapter reference to point the User defined adapter.
		this.adapter = adapter;

		// set the adapter.
		this.list.setAdapter(adapter);

		// set ArrayList<RowValues> to list.
		this.items = items;

	}

	/**
	 * Adds a new row with Values <b>data[]</b>
	 * 
	 * @param data
	 *            String values to be set to the views in the row.
	 */
	public void addItem(String[] data) {

		// Create a RowValues Object with the given values
		RowValues row1 = new RowValues(data);

		// add that object to list.
		items.add(row1);
		adapter.notifyDataSetChanged();
		
	}

	/**
	 * Removes a row at position <b>index</b>.
	 * 
	 * @param Position
	 *            of the row to be deleted. It starts with 0.
	 */
	public void removeItem(int index) {

		items.remove(index);
		adapter.notifyDataSetChanged();

	}

	/**
	 * Returns an RowValues object at index <i>index</i> RowValues is the type
	 * of each row item in the list.
	 * 
	 * @param index
	 * @return
	 */
	public RowValues getItem(int index) {
		// return the RowValues obj at index.
		return items.get(index);
	}

	/**
	 * Changes the Background color of the row/item (if it exists) in the list.
	 * 
	 * @param color
	 *            Resource id of the color resource.
	 * @param index
	 *            Index of the item, whose background color has to be changed.
	 */
	public void changeColor(int color_id, int index) {
		// If the row/item is there then only change the background color
		if (list.getChildAt(index) != null) {
			list.getChildAt(index).setBackgroundResource(color_id);
			adapter.notifyDataSetChanged();
		}
		
	}

	/**
	 * Clears all the rows in the list
	 */
	public void clear() {
		
		//reset the color of all the item positions in the list
		for(int i=0; i < list.getCount(); i++)
			{
				if( list.getItemAtPosition(i) != null)
					changeColor(SeekerActivity.WHITE, i);
			}
		
		// remove all the rows
		items.clear();
		adapter.notifyDataSetChanged();

	}

	private void LogMsg(String msg) {
		Log.d("Lists", msg);
	}

}