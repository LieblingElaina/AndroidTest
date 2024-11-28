package com.example.android.notepad.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.notepad.NotePad;
import com.example.android.notepad.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotesAdapter extends CursorAdapter {

    private LayoutInflater inflater;

    public NotesAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a new view for each item
        return inflater.inflate(R.layout.noteslist_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String noteTitle = cursor.getString(cursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_TITLE));
        long timestamp = cursor.getLong(cursor.getColumnIndex(NotePad.Notes.COLUMN_NAME_TIMESTAMP));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timestamp);
        String formattedDate = sdf.format(date);

        // 获取TextView控件并设置内容
        TextView titleTextView = view.findViewById(android.R.id.text1);
        TextView timestampTextView = view.findViewById(android.R.id.text2);

        titleTextView.setText(noteTitle);
        timestampTextView.setText(formattedDate);
    }
}