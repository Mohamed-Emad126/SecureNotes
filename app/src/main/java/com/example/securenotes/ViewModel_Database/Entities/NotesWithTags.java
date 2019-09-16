package com.example.securenotes.ViewModel_Database.Entities;

import androidx.room.Embedded;
import androidx.room.Relation;
import java.util.List;


public class NotesWithTags{
        @Embedded Notes note;

        @Relation(parentColumn = "noteId",entityColumn = "noteIdInTag")
        List<Tags> tags;

        public Notes getNote() {
            return note;
        }

        public List<Tags> getTags() {
            return tags;
        }
}


