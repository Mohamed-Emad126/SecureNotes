package com.example.securenotes.ViewModel_Database.Entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "NOTES")
public class Notes{

    @PrimaryKey(autoGenerate = true)
    private int noteId;

    private String title;

    private String content;

    private Date time;

    private String color;

    private boolean private_;

    public Notes(int noteId, String title, String content, Date time, String color, boolean private_) {
        this.noteId = noteId;
        this.title = title;
        this.content = content;
        this.time = time;
        this.color = color;
        this.private_ = private_;
    }

    @Ignore
    public Notes(String title, String content, Date time, String color, boolean private_) {
        this.title = title;
        this.content = content;
        this.time = time;
        this.color = color;
        this.private_ = private_;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isPrivate_() {
        return private_;
    }

    public void setPrivate_(boolean private_) {
        this.private_ = private_;
    }
}
