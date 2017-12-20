package com.example.plainolnotes;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NotesCursorAdapter extends CursorAdapter{
    TextView noteID, noteName,noteDescription,noteDate;
    CheckBox noteInFavourite;
    ImageView notePriority;
    ImageView passImg;
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
        holder.noteDate = (TextView) view.findViewById(R.id.noteDate);
        holder.notePriority = (ImageView) view.findViewById(R.id.notePriority);
        holder.notePass = (ImageView) view.findViewById(R.id.passImg);

        view.setTag(holder);
        return view;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder  =  (ViewHolder) view.getTag();
        holder.noteID.setText(cursor.getString(cursor.getColumnIndex(Note.NOTE_ID)));
        holder.noteName.setText(cursor.getString(cursor.getColumnIndex(Note.NOTE_NAME)));
        holder.noteDescription.setText(cursor.getString(cursor.getColumnIndex(Note.NOTE_DESCRIPTION)));
        holder.noteDate.setText(cursor.getString(cursor.getColumnIndex(Note.NOTE_DATE)));


        int IsChecked = cursor.getInt(cursor.getColumnIndex(Note.NOTE_IN_FAVOURITE));
        if (IsChecked == 1) {
            holder.noteInFavourite.setChecked(true);
        }
        else
        {
            holder.noteInFavourite.setChecked(false);
        }

        int Priority = cursor.getInt(cursor.getColumnIndex(Note.NOTE_PRIORITY));
        if (Priority == 1)
        {
            holder.notePriority.setBackgroundColor(Color.parseColor("#ff000c"));
        }
        else if (Priority == 2)
        {
            holder.notePriority.setBackgroundColor(Color.parseColor("#ff7400"));
        }
        else if (Priority == 3)
        {
            holder.notePriority.setBackgroundColor(Color.parseColor("#fff400"));
        }
        else
        {
            holder.notePriority.setVisibility(View.INVISIBLE);
        }

        String password = cursor.getString(cursor.getColumnIndex(Note.NOTE_PASSWORD));
        if (password.length()>0)
        {
            holder.notePass.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.notePass.setVisibility(View.INVISIBLE);
        }
    }

    static class ViewHolder {
        TextView noteID;
        TextView noteName;
        TextView noteDescription;
        TextView noteDate;
        CheckBox noteInFavourite;
        ImageView notePriority;
        ImageView notePass;
    }
}
