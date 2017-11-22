package com.example.plainolnotes;


public class Note {
    public static final String TABLE_NOTES = "notes";

    public static final String NOTE_ROWID = "_id";
    public static final String NOTE_ID = "id";
    public static final String NOTE_NAME = "noteName";
    public static final String NOTE_DESCRIPTION = "noteText";
    public static final String NOTE_CREATED = "noteCreated";
    public static final String NOTE_IN_FAVOURITE = "noteInFavourite";
    public static final String NOTE_DATE = "noteDate";

    // property help us to keep data
    public int noteID;
    public String noteNAME;
    public String noteDESCRIPTION;
    public int noteInFavourite;
    public int noteCreated;
    public int noteDate;
}
