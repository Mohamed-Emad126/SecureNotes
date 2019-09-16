package com.example.securenotes.ViewModel_Database;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.securenotes.ViewModel_Database.Entities.NotesWithTags;
import com.example.securenotes.ViewModel_Database.Entities.Tags;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    LiveData<List<NotesWithTags>> allNotesWithTags;
    LiveData<List<Tags>> allTags;

    public MainViewModel(@NonNull Application application) {
        super(application);

        allNotesWithTags = AppDatabase.getInstance(this.getApplication()).getDao().getAll();
        allTags = AppDatabase.getInstance(this.getApplication()).getDao().getAllTags();
    }

    public LiveData<List<NotesWithTags>> getAllNotesWithTags() {
        return allNotesWithTags;
    }

    public LiveData<List<Tags>> getAllTags() {
        return allTags;
    }

}
