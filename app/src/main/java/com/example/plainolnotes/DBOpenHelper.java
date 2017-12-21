package com.example.plainolnotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;


    public static final String[] ALL_COLUMNS =
            {Note.NOTE_ID, Note.NOTE_NAME, Note.NOTE_DESCRIPTION, Note.NOTE_CREATED, Note.NOTE_IN_FAVOURITE,
                    Note.NOTE_DATE, Note.NOTE_PRIORITY, Note.NOTE_CATEGORY, Note.NOTE_PLACE, Note.NOTE_ADDITIONAL_INFO,
                    Note.NOTE_PASSWORD};

    private static final String TABLE_CREATE =
            "CREATE TABLE " + Note.TABLE_NOTES + " (" +
                    Note.NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Note.NOTE_NAME + " TEXT, " +
                    Note.NOTE_DESCRIPTION + " TEXT, " +
                    Note.NOTE_CREATED + " DATETIME default CURRENT_TIMESTAMP, " +
                    Note.NOTE_IN_FAVOURITE + " INTEGER, " +
                    Note.NOTE_PRIORITY + " INTEGER DEFAULT 0, " +
                    Note.NOTE_CATEGORY + " TEXT, " +
                    Note.NOTE_PLACE + " TEXT, " +
                    Note.NOTE_ADDITIONAL_INFO + " TEXT, " +
                    Note.NOTE_PASSWORD + " TEXT, " +
                    Note.NOTE_DATE + " TEXT" +
                    ")";

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Note.TABLE_NOTES);
        onCreate(db);
    }
}
