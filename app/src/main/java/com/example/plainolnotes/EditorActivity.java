package com.example.plainolnotes;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.text.format.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class EditorActivity extends ActionBarActivity
implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final int DIALOG_EXIT = 1 ;
    private String action;
    private EditText editorNoteName;
    private EditText editorNoteText;
    private EditText editorNoteDate;
    private Button btnToCalendar;
    private String noteFilter;
    private String oldName;
    private String oldText;
    private int pinned;
    private int day, month, year, hour, minute, dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;
    NotesRepo notesRepo;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        notesRepo = new NotesRepo(this);
        editorNoteName = (EditText) findViewById(R.id.editNoteName);
        editorNoteText = (EditText) findViewById(R.id.editNoteText);
        editorNoteDate = (EditText) findViewById(R.id.editNoteDate);
        btnToCalendar = (Button) findViewById(R.id.buttonToCalendar);


        btnToCalendar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditorActivity.this, EditorActivity.this, year, month, day);
                datePickerDialog.show();
            }
        });
        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);

        if (uri == null) {

            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note));
        } else {
            action = Intent.ACTION_EDIT;
            noteFilter = Note.NOTE_ID + "=" + uri.getLastPathSegment();
            Cursor cursor = getContentResolver().query(uri,
                    DBOpenHelper.ALL_COLUMNS, noteFilter, null, null);
            cursor.moveToFirst();
            oldName = cursor.getString(cursor.getColumnIndex(Note.NOTE_NAME));
            oldText = cursor.getString(cursor.getColumnIndex(Note.NOTE_DESCRIPTION));
            pinned = cursor.getInt(cursor.getColumnIndex(Note.NOTE_IN_FAVOURITE));
            editorNoteText.setText(oldText);
            editorNoteName.setText(oldName);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (action.equals(Intent.ACTION_INSERT))
        {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
            MenuItem pin = menu.findItem(R.id.action_pin);
            pin.setVisible(false);
        }
        else if (action.equals(Intent.ACTION_EDIT))
        {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                showDialog(DIALOG_EXIT);
                break;
            case R.id.action_delete:
                deleteNote();
                break;
            case R.id.action_voice:
                promptSpeechInput();
                break;
            case R.id.action_pin:
                pinNote();
                break;
        }

        return true;
    }


    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_EXIT) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            // заголовок
            adb.setTitle(R.string.exit);
            // сообщение
            adb.setMessage(R.string.save_data);
            // иконка
            adb.setIcon(android.R.drawable.ic_dialog_info);
            // кнопка положительного ответа
            adb.setPositiveButton(R.string.yes, myClickListener);
            // кнопка отрицательного ответа
            adb.setNegativeButton(R.string.no, myClickListener);
            // кнопка нейтрального ответа
            adb.setNeutralButton(R.string.cancel, myClickListener);
            // создаем диалог
            return adb.create();
        }
        return super.onCreateDialog(id);
    }

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // положительная кнопка
                case Dialog.BUTTON_POSITIVE:
                    finishEditing();
                    finish();
                    break;
                // негативная кнопка
                case Dialog.BUTTON_NEGATIVE:
                    finish();
                    break;
                // нейтральная кнопка
                case Dialog.BUTTON_NEUTRAL:
                    break;
            }
        }
    };

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editorNoteText.setText(result.get(0));
                }
                break;
            }
        }
    }
    private void deleteNote() {
        getContentResolver().delete(NotesProvider.CONTENT_URI,
                noteFilter, null);
        Toast.makeText(this, getString(R.string.note_deleted),
                Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void finishEditing() {
        String newText = editorNoteText.getText().toString().trim();
        String newName = editorNoteName.getText().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (newText.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertNote(newName, newText);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newText.length() == 0 && newName.length() == 0) {
                    deleteNote();
                } else if (oldText.equals(newText) && oldName.equals(newName)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateNote(newName, newText);
                }

        }
        finish();
    }

    private void pinNote()
    {
        String newText = editorNoteText.getText().toString().trim();
        String newName = editorNoteName.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(Note.NOTE_NAME, newName);
        values.put(Note.NOTE_DESCRIPTION, newText);

        if(pinned==1)
        {
            values.put(Note.NOTE_IN_FAVOURITE, 0);
            getContentResolver().update(NotesProvider.CONTENT_URI, values, noteFilter, null);
            Toast.makeText(this, "Note unpinned", Toast.LENGTH_SHORT).show();
        }
        else if(pinned==0)
        {
            values.put(Note.NOTE_IN_FAVOURITE, 1);
            getContentResolver().update(NotesProvider.CONTENT_URI, values, noteFilter, null);
            Toast.makeText(this, "Note pinned", Toast.LENGTH_SHORT).show();
        }

        setResult(RESULT_OK);
        finish();

    }

    private void updateNote(String noteName, String noteText) {
        ContentValues values = new ContentValues();
        values.put(Note.NOTE_NAME, noteName);
        values.put(Note.NOTE_DESCRIPTION, noteText);
        getContentResolver().update(NotesProvider.CONTENT_URI, values, noteFilter, null);
        Toast.makeText(this, getString(R.string.note_updated), Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertNote(String noteName, String noteText) {
        Note note = new Note();
        note.noteNAME = noteName;
        note.noteDESCRIPTION = noteText;
        notesRepo.insert(note);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }


    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {

        hourFinal = i;
        minuteFinal = i1;

        editorNoteDate.setText(yearFinal + "." + monthFinal + "." + dayFinal + " " + hourFinal + ":" + minuteFinal);
    }

    public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
    {
        yearFinal = i;
        monthFinal = i1 +1;
        dayFinal = i2;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(EditorActivity.this, EditorActivity.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }
}
