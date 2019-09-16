package com.example.securenotes.ViewModel_Database.Entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "TAGS")
    public class Tags{

        @PrimaryKey(autoGenerate = true)
        private int tagId;
        private String tagName;
        private int noteIdInTag;

        public Tags(int tagId, String tagName, int noteIdInTag) {
            this.tagId = tagId;
            this.tagName = tagName;
            this.noteIdInTag = noteIdInTag;
        }

        @Ignore
        public Tags(String tagName, int noteIdInTag) {
            this.tagName = tagName;
            this.noteIdInTag = noteIdInTag;
        }

        public int getNoteIdInTag() {
            return noteIdInTag;
        }

        public void setNoteIdInTag(int noteIdInTag) {
            this.noteIdInTag = noteIdInTag;
        }

        public int getTagId() {
            return tagId;
        }

        public void setTagId(int tagId) {
            this.tagId = tagId;
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }
    }
