package com.teambugallergy.customlistview;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListViewAdapter extends ArrayAdapter<ListViewBinder>{

    Context context; 
    int layoutResourceId;    
    ListViewBinder data[] = null;
    
    public ListViewAdapter(Context context, int layoutResourceId, ListViewBinder[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ListViewItem holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new ListViewItem();
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            
            row.setTag(holder);
        }
        else
        {
            holder = (ListViewItem)row.getTag();
        }
        
        ListViewBinder Device = data[position];
        holder.txtTitle.setText(Device.Name);
        
        return row;
    }
    
    static class ListViewItem
    {
    	TextView txtTitle;
    }
}