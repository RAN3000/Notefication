package com.ran3000.notefication2.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NoteDao {
    @Query("SELECT * FROM notes")
    List<Note> getAll();

    @Query("SELECT * FROM notes WHERE id = :id")
    Note getById(long id);

    @Insert
    long insert(Note note);

    @Delete
    void delete(Note note);

    @Query("SELECT COUNT(id) FROM notes")
    int getNotesCount();

}

