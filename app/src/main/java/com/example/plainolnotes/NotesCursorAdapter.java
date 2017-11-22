package com.example.plainolnotes;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class NotesCursorAdapter extends CursorAdapter{
    TextView noteID, noteName,noteDescription;
    CheckBox noteInFavourite;
    private LayoutInflater mInflater;

    public NotesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item, parent, false);
        ViewHolder holder = new ViewHolder();
        holder.noteID = (TextView) view.findViewById(R.id.noteID);
        holder.noteName = (TextView) view.findViewById(R.id.noteName);
        holder.noteDescription = (TextView) view.findViewById(R.id.noteDescription);
        holder.noteInFavourite = (CheckBox) view.findViewById(R.id.noteInFavourite);
        view.setTag(holder);
        return view;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder  =  (ViewHolder) view.getTag();
        holder.noteID.setText(cursor.getString(cursor.getColumnIndex(Note.NOTE_ID)));
        holder.noteName.setText(cursor.getString(cursor.getColumnIndex(Note.NOTE_NAME)));
        holder.noteDescription.setText(cursor.getString(cursor.getColumnIndex(Note.NOTE_DESCRIPTION)));

        int IsChecked = cursor.getInt(cursor.getColumnIndex(Note.NOTE_IN_FAVOURITE));
        if (IsChecked == 1) {
            holder.noteInFavourite.setChecked(true);
        }
        else
        {
            holder.noteInFavourite.setChecked(false);
        }
    }

    static class ViewHolder {
        TextView noteID;
        TextView noteName;
        TextView noteDescription;
        CheckBox noteInFavourite;
    }
}
