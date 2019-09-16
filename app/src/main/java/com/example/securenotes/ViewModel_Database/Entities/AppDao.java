package com.example.securenotes.ViewModel_Database.Entities;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.securenotes.ViewModel_Database.Entities.Notes;
import com.example.securenotes.ViewModel_Database.Entities.NotesWithTags;
import com.example.securenotes.ViewModel_Database.Entities.Tags;

import java.util.ArrayList;
import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface AppDao {

    @Transaction
    @Query("SELECT * FROM NOTES ORDER BY time DESC")
    LiveData<List<NotesWithTags>> getAll();

    @Query("SELECT * FROM TAGS")
    LiveData<List<Tags>> getAllTags();

    @Insert(onConflict = REPLACE)
    void insertNoteTags(List<Tags> tags);

    @Insert(onConflict = REPLACE)
    long insertNote(Notes note);

    @Delete
    void deleteNote(Notes note);

    @Delete
    void deleteListOfNote(List<Notes> note);

    @Insert(onConflict = REPLACE)
    void insertListOfNotes(List<Notes> notes);

    @Delete
    void deleteTag(Tags tag);

    @Query("DELETE FROM TAGS WHERE noteIdInTag = :uid ")
    void deleteTagWithNoteId(int uid);

    @Query("DELETE FROM TAGS WHERE noteIdInTag IN(:zid) ")
    void deleteTagsWithNotesIdList(List<Integer> zid);

    @Update
    void updateNote(Notes notes);

}
