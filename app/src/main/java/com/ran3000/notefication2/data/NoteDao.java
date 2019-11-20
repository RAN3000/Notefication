package com.ran3000.notefication2.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY orderId")
    List<Note> getAll();

    @Query("SELECT * FROM notes WHERE sticky = 1 ORDER BY orderId")
    List<Note> getAllSticky();

    @Query("SELECT * FROM notes WHERE id = :id")
    Note getById(long id);

    @Insert
    long insert(Note note);

    @Delete
    void delete(Note note);

    @Query("DELETE FROM notes")
    void deleteAll();

    @Query("SELECT COUNT(id) FROM notes")
    int getNotesCount();

    @Query("SELECT COUNT(id) FROM notes WHERE sticky = 1")
    int getStickyNotesCount();

    @Query("UPDATE notes SET orderId = :toOrder WHERE id = :ofId")
    void updateOrder(long ofId, long toOrder);

}

