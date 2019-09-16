package com.example.securenotes.ViewModel_Database;

import androidx.room.TypeConverter;

import java.util.Date;

public class TimeConverter {
    @TypeConverter
    public Long toTimestamp(Date date){
        return date.getTime();
    }

    @TypeConverter
    public Date toDate(Long time){
        return new Date(time);
    }
}
