package com.teambugallergy.resourceshare.list;

import java.util.ArrayList;

import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * This Class takes care of adding and removing of rows to the list.
 * The object of this class should be created with <b>specific ListAdapter</b> class.
 * <br/>
 * <i>This class <b>will not provide OnItemClickListner</b>, because the object of this class can be used on any type of ListAdapter. So
 * The class which uses the object of this class should provide it's own OnItemClickListener<i>  
 *
 * @since 04-04-2014
 * @author Adiga
 * 
 */
public class Lists{
	
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
	 * @param list ListView object to be used.
	 * @param adapter DeviceListAdapter to be assigned to the ListView list.<br/><i>This object should have <b>ArrayList same as items</b></i>
	 * @param items ArrayList<RowValues> associated with adapter.
	 */
	public Lists(ListView list, DeviceListAdapter adapter, ArrayList<RowValues> items) {
		
		//ListView to be used.
		this.list = list;
		
		//Use a BaseAdapter reference to point the User defined adapter.
		this.adapter = adapter;
		
		//set the adapter.
		this.list.setAdapter(adapter);
		
		//set ArrayList<RowValues> to list. 
		this.items = items;
				
	}

	/**
	 * Adds a new row with Values <b>data[]</b>
	 * @param data String values to be set to the views in the row.
	 */
	public void addItem(String[] data) {
		
		//Create a RowValues Object with the given values
		RowValues row1 = new RowValues( data );

		//add that object to list. 
		items.add(row1);
				
	}
	
	/**
	 * Removes a row at position <b>index</b>.
	 * @param Position of the row to be deleted. It starts with 0.
	 */
	public void removeItem(int index){
		
		items.remove(index);
		
	}

	/**
	 * Returns an RowValues object at index <i>index</i>
	 * RowValues is the type of each row item in the list.
	 * @param index
	 * @return
	 */
	public RowValues getItem(int index)
	{
		//return the RowValues obj at index.
		return items.get(index);
	}
	
	/**
	 * Notifies the adapter associated with the list that data set (array of rows) has been changed.
	 */
	public void notifyDataSetChanged()
	{
		//Notify the BaseAdapter object, that data set has been changed
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * Clears all the rows in the list
	 */
	public void clear()
	{
		//remove all the rows
		items.clear();
		
	}
	
}