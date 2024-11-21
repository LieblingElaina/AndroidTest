package com.example.uitest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.view.ActionMode;

import java.util.ArrayList;
import java.util.List;

public class Context extends AppCompatActivity {

    ListView listView;
    List<Item> list=new ArrayList<Item>();
    BaseAdapter adapter;
    String [] name = {"One","Two","Three","Four","Five"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview2);
        listView = findViewById(R.id.list_view);
        list=new ArrayList<Item>();
        for(int i=0;i<5;i++)
        {
            list.add(new Item(name[i],false));
        }
        adapter =new Adapter(list, Context.this);
        listView.setAdapter(adapter);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new MultiChoiceModeListener()
        {
            int num = 0;
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean flag) {

                // 调整选定条目
                if (flag) {
                    list.get(position).setCs(true);
                    //实时刷新
                    adapter.notifyDataSetChanged();
                    num++;
                } else {
                    list.get(position).setCs(false);
                    //实时刷新
                    adapter.notifyDataSetChanged();
                    num--;
                }
                // 用TextView显示
                mode.setTitle("  " + num + " Selected");
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context1, menu);
                num = 0;
                adapter.notifyDataSetChanged();
                return true;

            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

                adapter.notifyDataSetChanged();
                return false;
            }

            public void refresh(){
                for(int i = 0; i < 5; i++){
                    list.get(i).setCs(false);
                }
            }
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.menu_delete:
                        num = 0;
                        refresh();
                        mode.setTitle("  " + num + " Selected");
                        adapter.notifyDataSetChanged();
                        mode.finish();
                        return true;
                    default:
                        refresh();
                        num = 0;
                        mode.setTitle("  " + num + " Selected");
                        adapter.notifyDataSetChanged();
                        return false;
                }

            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                refresh();
                adapter.notifyDataSetChanged();

            }
        });

    }
}