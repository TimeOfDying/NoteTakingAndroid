package com.example.plainolnotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;


public class NotesRepo {
    private DBOpenHelper dbHelper;

    public NotesRepo(Context context)
    {
        dbHelper = new DBOpenHelper(context);
    }


    public int insert(Note note) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Note.NOTE_NAME, note.noteNAME);
        values.put(Note.NOTE_CREATED, note.noteCreated);
        values.put(Note.NOTE_DESCRIPTION, note.noteDESCRIPTION);
        values.put(Note.NOTE_IN_FAVOURITE, note.noteInFavourite);
        values.put(Note.NOTE_DATE, note.noteDate);
        values.put(Note.NOTE_PRIORITY, note.notePriority);
        values.put(Note.NOTE_CATEGORY, note.noteCategory);
        values.put(Note.NOTE_PLACE, note.notePlace);
        values.put(Note.NOTE_MOVIE_NAME, note.noteMovieName);
        values.put(Note.NOTE_PASSWORD, note.NotePassword);


        long note_Id = db.insert(Note.TABLE_NOTES, null, values);
        db.close();
        return (int) note_Id;
    }


    public Cursor getNotestList()
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT ROWID as " +
                Note.NOTE_ROWID + " , " +
                Note.NOTE_ID + " , " +
                Note.NOTE_NAME + " , " +
                Note.NOTE_CREATED + " , " +
                Note.NOTE_DESCRIPTION + ", " +
                Note.NOTE_IN_FAVOURITE + ", " +
                Note.NOTE_PRIORITY + ", " +
                Note.NOTE_CATEGORY + ", " +
                Note.NOTE_PLACE + ", " +
                Note.NOTE_PASSWORD + ", " +
                Note.NOTE_MOVIE_NAME + ", " +
                Note.NOTE_DATE +
                " FROM " + Note.TABLE_NOTES +
                " WHERE " + Note.NOTE_NAME +"<>'dummy'" +
                " ORDER BY " + Note.NOTE_IN_FAVOURITE + " DESC, " + Note.NOTE_PRIORITY + " ASC";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor == null)
        {
            return null;
        }
        else if (!cursor.moveToFirst())
        {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public String[] getCategoriesList()
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT DISTINCT " +
                Note.NOTE_CATEGORY +
                " FROM " + Note.TABLE_NOTES;

        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        ArrayList<String> categories = new ArrayList<String>();
        while(!cursor.isAfterLast())
        {
            categories.add(cursor.getString(cursor.getColumnIndex(Note.NOTE_CATEGORY)));
            cursor.moveToNext();
        }
        cursor.close();
        return categories.toArray(new String[categories.size()]);
    }

    public Cursor getNotesListByCategory(String category)
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT rowid as " +
                Note.NOTE_ROWID + " , " +
                Note.NOTE_ID + " , " +
                Note.NOTE_NAME + " , " +
                Note.NOTE_CREATED + " , " +
                Note.NOTE_DESCRIPTION + " , " +
                Note.NOTE_IN_FAVOURITE + ", " +
                Note.NOTE_PRIORITY + ", " +
                Note.NOTE_CATEGORY + ", " +
                Note.NOTE_PLACE + ", " +
                Note.NOTE_PASSWORD + ", " +
                Note.NOTE_MOVIE_NAME + ", " +
                Note.NOTE_DATE +
                " FROM " + Note.TABLE_NOTES +
                " WHERE " + Note.NOTE_CATEGORY + " LIKE '%" + category + "%'" +
                " ORDER BY " + Note.NOTE_IN_FAVOURITE + " DESC, " + Note.NOTE_PRIORITY + " ASC";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public Cursor getNotesListByKeyword(String search)
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT rowid as " +
                Note.NOTE_ROWID + " , " +
                Note.NOTE_ID + " , " +
                Note.NOTE_NAME + " , " +
                Note.NOTE_CREATED + " , " +
                Note.NOTE_DESCRIPTION + " , " +
                Note.NOTE_IN_FAVOURITE + ", " +
                Note.NOTE_PRIORITY + ", " +
                Note.NOTE_CATEGORY + ", " +
                Note.NOTE_PLACE + ", " +
                Note.NOTE_PASSWORD + ", " +
                Note.NOTE_MOVIE_NAME + ", " +
                Note.NOTE_DATE +
                " FROM " + Note.TABLE_NOTES +
                " WHERE " + Note.NOTE_DESCRIPTION + " LIKE '%" + search + "%' OR " + Note.NOTE_NAME + " LIKE '%" +search + "%'" +
                " ORDER BY " + Note.NOTE_IN_FAVOURITE + " DESC, " + Note.NOTE_PRIORITY + " ASC";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }



    public Note getNoteById(int Id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT " +
                Note.NOTE_ROWID + " , " +
                Note.NOTE_ID + " , " +
                Note.NOTE_NAME + " , " +
                Note.NOTE_CREATED + " , " +
                Note.NOTE_DESCRIPTION + " , " +
                Note.NOTE_IN_FAVOURITE + ", " +
                Note.NOTE_PRIORITY + ", " +
                Note.NOTE_CATEGORY + ", " +
                Note.NOTE_PLACE + ", " +
                Note.NOTE_PASSWORD + ", " +
                Note.NOTE_MOVIE_NAME + ", " +
                Note.NOTE_DATE +
                " FROM " + Note.TABLE_NOTES
                + " WHERE " +
                Note.NOTE_ID + "=?";

        int iCount =0;
        Note note = new Note();

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(Id) } );

        if (cursor.moveToFirst()) {
            do {
                note.noteID =cursor.getInt(cursor.getColumnIndex(note.NOTE_ID));
                note.noteNAME =cursor.getString(cursor.getColumnIndex(note.NOTE_NAME));
                note.noteDESCRIPTION =cursor.getString(cursor.getColumnIndex(note.NOTE_DESCRIPTION));
                note.noteCreated =cursor.getString(cursor.getColumnIndex(note.NOTE_CREATED));
                note.noteInFavourite = cursor.getInt(cursor.getColumnIndex(note.NOTE_IN_FAVOURITE));
                note.noteDate = cursor.getString(cursor.getColumnIndex(note.NOTE_DATE));
                note.notePriority = cursor.getInt(cursor.getColumnIndex(note.NOTE_PRIORITY));
                note.noteCategory = cursor.getString(cursor.getColumnIndex(note.NOTE_CATEGORY));
                note.notePlace = cursor.getString(cursor.getColumnIndex(note.NOTE_PLACE));
                note.noteMovieName = cursor.getString(cursor.getColumnIndex(note.NOTE_MOVIE_NAME));
                note.NotePassword = cursor.getString(cursor.getColumnIndex(note.NOTE_PASSWORD));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return note;
    }
}
