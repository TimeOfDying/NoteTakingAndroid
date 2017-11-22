package com.example.plainolnotes;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity
{
    private static final int EDITOR_REQUEST_CODE = 1001;
    private NotesCursorAdapter notesCursorAdapter;
    private Cursor cursor;
    ListView listView;
    NotesRepo notesRepo;
    private final static String TAG = MainActivity.class.getName().toString();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notesRepo = new NotesRepo(this);
        cursor = notesRepo.getNotestList();
        notesCursorAdapter = new NotesCursorAdapter(MainActivity.this, cursor, 0);

        listView = (ListView) findViewById(R.id.listNotes);
        listView.setAdapter(notesCursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri uri = Uri.parse(NotesProvider.CONTENT_URI + "/" + id);
                intent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });

        if (cursor == null) insertDummy();
        cursor = notesRepo.getNotestList();
        notesCursorAdapter.swapCursor(cursor);

    }



    private void insertDummy() {
        Note note = new Note();

        note.noteNAME = "FirstNote";
        note.noteDESCRIPTION = "tanwoonhow@intstinctcoder.com";
        note.noteInFavourite = 1;
        notesRepo.insert(note);

        notesRepo = new NotesRepo(this);
        note.noteNAME = "SecondNote";
        note.noteDESCRIPTION = "Jimmy Tan Yao Lin";
        note.noteInFavourite = 0;
        notesRepo.insert(note);

        notesRepo = new NotesRepo(this);
        note.noteNAME = "ThordNote";
        note.noteDESCRIPTION = "Robert Pattinson";
        note.noteInFavourite = 1;
        notesRepo.insert(note);

    }

    @Override
    public void onResume(){
        super.onResume();

    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String s) {
                    Log.d(TAG, "onQueryTextSubmit ");
                    cursor = notesRepo.getNotesListByKeyword(s);
                    if (cursor == null) {
                        Toast.makeText(MainActivity.this, "No records found!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, cursor.getCount() + " records found!", Toast.LENGTH_LONG).show();
                    }
                    notesCursorAdapter.swapCursor(cursor);

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    Log.d(TAG, "onQueryTextChange ");
                    cursor = notesRepo.getNotesListByKeyword(s);
                    if (cursor != null) {
                        notesCursorAdapter.swapCursor(cursor);
                    }
                    return false;
                }

            });
        }
        return true;
    }

    public void openEditorForNewNote(View view) {
        Intent intent = new Intent(this, EditorActivity.class);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
            cursor = notesRepo.getNotestList();
            notesCursorAdapter.swapCursor(cursor);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete_all:
                deleteAllNotes();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAllNotes() {

        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            //Insert Data management code here
                            getContentResolver().delete(
                                    NotesProvider.CONTENT_URI, null, null
                            );
                            cursor = notesRepo.getNotestList();
                            notesCursorAdapter.swapCursor(cursor);

                            Toast.makeText(MainActivity.this,
                                    getString(R.string.all_deleted),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }



}
