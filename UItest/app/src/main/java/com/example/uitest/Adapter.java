package com.example.uitest;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
public class Adapter extends BaseAdapter {
    List<Item> list;
    Context context;
    public Adapter(List<Item> list, Context context)
    {
        this.context=context;
        this.list=list;
        notifyDataSetChanged();
    }
    public int getCount()
    {
        return list.size();
    }
    class ViewHolder
    {
        ImageView imageView;
        TextView textView1;
    }
    public Item getItem(int position)
    {
        return list.get(position);
    }
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final ViewHolder viewHolder;
        if(convertView==null)
        {
            convertView = View.inflate(context, R.layout.context, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.textView1 = (TextView) convertView.findViewById(R.id.ContextView);
            convertView.setTag(viewHolder);
            TextView textView = convertView.findViewById(R.id.ContextView);
            Item item = list.get(position);
            textView.setText(item.getName());
        }
        else {
            viewHolder=(ViewHolder) convertView.getTag();
        }
        int blue= Color.parseColor("#0000FF");
        int white= Color.parseColor("#FFFFFF");
        if(list.get(position).isCs())
        {
            viewHolder.textView1.setBackgroundColor(blue);
            viewHolder.imageView.setBackgroundColor(blue);
        }
        else
        {
            viewHolder.textView1.setBackgroundColor(white);
            viewHolder.imageView.setBackgroundColor(white);
        }
        return convertView;
    }
}
