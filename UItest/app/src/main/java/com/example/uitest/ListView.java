package com.example.uitest;

import android.os.Bundle;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListView extends AppCompatActivity
{

    android.widget.ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.listview);
        listView = (android.widget.ListView) findViewById(R.id.list_view);
        int[] pho={R.drawable.lion, R.drawable.tiger,R.drawable.monkey,R.drawable.dog,R.drawable.cat,R.drawable.elephant};
        String[] name={"Lion","Tiger","Monkey","Dog","Cat","Elephant"};
        List<Map<String,Object>> mplist = new ArrayList<>();
        for(int i=0;i<6;i++)
        {
            Map<String,Object>mp= new HashMap<>();
            mp.put("pho",pho[i]);
            mp.put("name",name[i]);
            mplist.add(mp);
        }
        String[] from={"name","pho"};
        int[] to={R.id.name,R.id.img};
        SimpleAdapter adapter= new SimpleAdapter(ListView.this,mplist,R.layout.listview1,from,to);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent,view,position,id) -> {
            String selectedItem = name[position];
            Toast.makeText(ListView.this, selectedItem, Toast.LENGTH_SHORT).show();
        });
    }
}