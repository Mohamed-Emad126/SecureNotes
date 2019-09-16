package com.example.securenotes.ViewModel_Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.securenotes.ViewModel_Database.Entities.AppDao;
import com.example.securenotes.ViewModel_Database.Entities.Notes;
import com.example.securenotes.ViewModel_Database.Entities.Tags;

@Database(entities = {Notes.class, Tags.class}, version = 1)
@TypeConverters(TimeConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    static AppDatabase mInstance;

    public static AppDatabase getInstance(Context context){
        if(mInstance == null){
            synchronized (new Object()){
                mInstance = Room.databaseBuilder(context,
                        AppDatabase.class,
                        "SECURE_NOTES_DB")
                        .build();
            }
        }
        return mInstance;
    }


    public abstract AppDao getDao();
}
