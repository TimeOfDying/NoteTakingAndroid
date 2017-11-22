package com.example.plainolnotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


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

        // Inserting Row
        long note_Id = db.insert(Note.TABLE_NOTES, null, values);
        db.close(); // Closing database connection
        return (int) note_Id;
    }

  /*  public void update(int ID, String NoteName, String NoteText) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("NOTE_NAME" , "NoteName");
        values.put("NOTE_DESCRIPTION" , "NoteText");

        db.update(Note.TABLE_NOTES, values, "id = ?", null);
    }*/


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
                Note.NOTE_DATE +
                " FROM " + Note.TABLE_NOTES +
                " ORDER BY " + Note.NOTE_IN_FAVOURITE + " DESC";

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
                Note.NOTE_DATE +
                " FROM " + Note.TABLE_NOTES +
                " WHERE " + Note.NOTE_DESCRIPTION + " LIKE '%" + search + "%' OR " + Note.NOTE_NAME + " LIKE '%" +search + "%'" +
                " ORDER BY " + Note.NOTE_IN_FAVOURITE + " DESC ";

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
                Note.NOTE_DATE +
                " FROM " + Note.TABLE_NOTES
                + " WHERE " +
                Note.NOTE_ID + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        int iCount =0;
        Note note = new Note();

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(Id) } );

        if (cursor.moveToFirst()) {
            do {
                note.noteID =cursor.getInt(cursor.getColumnIndex(note.NOTE_ID));
                note.noteNAME =cursor.getString(cursor.getColumnIndex(note.NOTE_NAME));
                note.noteDESCRIPTION =cursor.getString(cursor.getColumnIndex(note.NOTE_DESCRIPTION));
                note.noteCreated =cursor.getInt(cursor.getColumnIndex(note.NOTE_CREATED));
                note.noteInFavourite = cursor.getInt(cursor.getColumnIndex(note.NOTE_IN_FAVOURITE));
                note.noteDate = cursor.getInt(cursor.getColumnIndex(note.NOTE_DATE));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return note;
    }
}
