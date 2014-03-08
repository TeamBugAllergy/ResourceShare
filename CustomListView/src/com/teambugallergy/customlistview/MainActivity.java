package com.teambugallergy.customlistview;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private ListView listView1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ListViewBinder ListData[] = new ListViewBinder[]
        {
            new ListViewBinder("Cloudy"),
            new ListViewBinder("Showers"),
            new ListViewBinder("Snow"),
            new ListViewBinder( "Storm"),
            new ListViewBinder("Sunny")
        };
        
        ListViewAdapter adapter = new ListViewAdapter(this, 
                R.layout.listitem, ListData);
        
        
        listView1 = (ListView)findViewById(R.id.listView1);
         
        
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
                
                String item = "selected";
                
                Toast.makeText(getApplicationContext(), item, Toast.LENGTH_LONG).show();
                
            }
        });
    }
}