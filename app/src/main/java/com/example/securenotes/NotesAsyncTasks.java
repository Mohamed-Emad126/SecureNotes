package com.example.securenotes;

import android.content.Context;
import android.os.AsyncTask;

import com.example.securenotes.ViewModel_Database.AppDatabase;
import com.example.securenotes.ViewModel_Database.Entities.Notes;
import com.example.securenotes.ViewModel_Database.Entities.Tags;

import java.util.ArrayList;
import java.util.List;

public class NotesAsyncTasks extends AsyncTask<Void,Void,Void> {

    AppDatabase database;
    String operation;
    ArrayList<String> tags;
    ArrayList<Notes> notesForDeleted;
    ArrayList<Tags> tagsForInsertion;
    Notes note;
    long userId = -1;


    public NotesAsyncTasks(Context context, String operation, ArrayList<Notes> notesForDeleted, ArrayList<Tags> tagsForInsertion) {
        database = AppDatabase.getInstance(context);
        this.operation = operation;
        this.notesForDeleted = notesForDeleted;
        this.tagsForInsertion = tagsForInsertion;
    }

    public NotesAsyncTasks(Context context, String operation, Notes note, ArrayList<Tags> tagsForInsertion, boolean x) {
        database = AppDatabase.getInstance(context);
        this.operation = operation;
        this.note = note;
        this.tagsForInsertion = tagsForInsertion;
    }


    public NotesAsyncTasks(Context context, String operation, Notes note, ArrayList<String> tags) {
        database = AppDatabase.getInstance(context);
        this.operation = operation;
        this.tags = tags;
        this.note = note;
    }

    public NotesAsyncTasks(Context context, String operation, ArrayList<Notes> notesForDeleted) {
        this.database = AppDatabase.getInstance(context);;
        this.notesForDeleted = notesForDeleted;
        this.operation = operation;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (operation.equalsIgnoreCase("Insertion")) {
            userId = database.getDao().insertNote(note);

            ArrayList<Tags> tagChips = new ArrayList<>();
            for (int i = 0; i < tags.size(); i++) {
                tagChips.add(new Tags(tags.get(i), (int) userId));
            }


            if (userId != -1) {
                database.getDao().insertNoteTags(tagChips);
            }
        }
        else if(operation.equalsIgnoreCase("update")){

            database.getDao().updateNote(note);

            database.getDao().deleteTagWithNoteId(note.getNoteId());

            ArrayList<Tags> tagChips = new ArrayList<>();
            for (int i = 0; i < tags.size(); i++) {
                tagChips.add(new Tags(tags.get(i), note.getNoteId()));
            }

            database.getDao().insertNoteTags(tagChips);



        }
        else if(operation.equalsIgnoreCase("delete")){

            database.getDao().deleteNote(note);
            database.getDao().deleteTagWithNoteId(note.getNoteId());
        }
        else if(operation.equalsIgnoreCase("deleteList")){


            database.getDao().deleteListOfNote(notesForDeleted);


            List<Integer> tagsDeletion = new ArrayList<>();
            for (int i=0; i<notesForDeleted.size(); i++){
                tagsDeletion.add(notesForDeleted.get(i).getNoteId());
            }
            database.getDao().deleteTagsWithNotesIdList(tagsDeletion);


        }
        else if(operation.equalsIgnoreCase("InsertionList")){
            database.getDao().insertListOfNotes(notesForDeleted);
            database.getDao().insertNoteTags(tagsForInsertion);
        }
        else if(operation.equalsIgnoreCase("InsertionOne")){
            database.getDao().insertNote(note);
            database.getDao().insertNoteTags(tagsForInsertion);
        }
        return null;
    }



}
