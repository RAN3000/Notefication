package com.ran3000.notefication.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface NoteDao {
    @Query("SELECT * FROM notes")
    List<Note> getAll();

    @Query("SELECT * FROM notes WHERE id IN (:ids)")
    List<Note> getByIds(long[] ids);

    @Insert
    long insert(Note notes);

    @Delete
    void delete(Note user);
}

