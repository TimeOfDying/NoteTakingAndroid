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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity
{
    private static final int EDITOR_REQUEST_CODE = 1001;
    private NotesCursorAdapter notesCursorAdapter;
    private Cursor cursor;
    ListView listView;
    NotesRepo notesRepo;
    private static String selectedCategory = null;
    private final static String TAG = MainActivity.class.getName().toString();
    private String[] spinnerVariants = {"New note...", "Movies", "Shopping"};


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

                final Uri uri = Uri.parse(NotesProvider.CONTENT_URI + "/" + id);
                String noteFilter = Note.NOTE_ID + "=" + uri.getLastPathSegment();
                Cursor cursor = getContentResolver().query(uri,
                        DBOpenHelper.ALL_COLUMNS, noteFilter, null, null);
                cursor.moveToFirst();
                final String checkCategory = cursor.getString(cursor.getColumnIndex(Note.NOTE_CATEGORY));
                final String checkPassword = cursor.getString(cursor.getColumnIndex(Note.NOTE_PASSWORD));

                if(checkPassword.length()>0) {
                    LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
                    View mView = layoutInflaterAndroid.inflate(R.layout.pass_input_dialog, null);
                    AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilderUserInput.setView(mView);

                    final EditText passwordField = (EditText) mView.findViewById(R.id.userInputDialog);
                    alertDialogBuilderUserInput
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBox, int id) {
                                    if(passwordField.getText().toString().equals(checkPassword))
                                    {
                                        if (checkCategory.equals("Movies")) {
                                            Intent intent = new Intent(MainActivity.this, MovieEditorActivity.class);
                                            intent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);
                                            startActivityForResult(intent, EDITOR_REQUEST_CODE);
                                        } else if (checkCategory.equals("Shopping")) {
                                            Intent intent = new Intent(MainActivity.this, ShoppingEditorActivity.class);
                                            intent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);
                                            startActivityForResult(intent, EDITOR_REQUEST_CODE);
                                        } else {
                                            Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                                            intent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);
                                            startActivityForResult(intent, EDITOR_REQUEST_CODE);
                                        }
                                    }
                                    else Toast.makeText(MainActivity.this,
                                            "Incorrect password",
                                            Toast.LENGTH_SHORT).show();

                                }
                            })

                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogBox, int id) {
                                            dialogBox.cancel();
                                        }
                                    });

                    AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                    alertDialogAndroid.show();

                }
                else
                {
                    if (checkCategory.equals("Movies")) {
                        Intent intent = new Intent(MainActivity.this, MovieEditorActivity.class);
                        intent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);
                        startActivityForResult(intent, EDITOR_REQUEST_CODE);
                    } else if (checkCategory.equals("Shopping")) {
                        Intent intent = new Intent(MainActivity.this, ShoppingEditorActivity.class);
                        intent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);
                        startActivityForResult(intent, EDITOR_REQUEST_CODE);
                    } else {
                        Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                        intent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);
                        startActivityForResult(intent, EDITOR_REQUEST_CODE);
                    }
                }

            }
        });

        //if (cursor == null) insertDummy();
        cursor = notesRepo.getNotestList();
        notesCursorAdapter.swapCursor(cursor);

    }



    private void insertDummy() {
        Note note = new Note();

        note.noteCategory = "Standard";
        note.noteNAME = "dummy";
        notesRepo.insert(note);

        note.noteCategory = "Shopping";
        note.noteNAME = "dummy";
        notesRepo.insert(note);

        note.noteCategory = "Movies";
        note.noteNAME = "dummy";
        notesRepo.insert(note);

        cursor = notesRepo.getNotestList();
        notesCursorAdapter.swapCursor(cursor);

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
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mview = getLayoutInflater().inflate(R.layout.spinner_dialog, null);
        mBuilder.setTitle("Choose note template");
        final Spinner mSpinner = (Spinner) mview.findViewById(R.id.DGspinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, spinnerVariants);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(spinnerAdapter);

        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if(mSpinner.getSelectedItem().toString().equalsIgnoreCase("Movies"))
                {
                    Intent intent = new Intent(MainActivity.this, MovieEditorActivity.class);
                    startActivityForResult(intent, EDITOR_REQUEST_CODE);
                    dialogInterface.dismiss();
                }
                else if (mSpinner.getSelectedItem().toString().equalsIgnoreCase("Shopping"))
                {
                    Intent intent = new Intent(MainActivity.this,ShoppingEditorActivity.class);
                    startActivityForResult(intent, EDITOR_REQUEST_CODE);
                    dialogInterface.dismiss();
                }
                else
                {
                    Intent intent = new Intent(MainActivity.this,EditorActivity.class);
                    startActivityForResult(intent, EDITOR_REQUEST_CODE);
                    dialogInterface.dismiss();
                }
            }
        });

        mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mBuilder.setView(mview);
        AlertDialog dialog = mBuilder.create();
        dialog.show();


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
            case R.id.action_choose_category:
                filterNotesByCategory();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void filterNotesByCategory() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mview = getLayoutInflater().inflate(R.layout.spinner_dialog, null);
        mBuilder.setTitle("Choose category");
        final Spinner mSpinner = (Spinner) mview.findViewById(R.id.DGspinner);
        String[] spinnerVariants2 = notesRepo.getCategoriesList();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, spinnerVariants2);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(spinnerAdapter);

        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                String category = mSpinner.getSelectedItem().toString();
                cursor = notesRepo.getNotesListByCategory(category);
                notesCursorAdapter.swapCursor(cursor);
            }
        });

        mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mBuilder.setView(mview);
        AlertDialog dialog = mBuilder.create();
        dialog.show();

    }

    private void deleteAllNotes() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
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
