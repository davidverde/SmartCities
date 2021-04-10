package com.example.smartcities.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.smartcities.entities.Note

@Dao                        // Data access object - faz conecção as entidades
interface NoteDao {

    @Query("SELECT * from note_table ORDER BY note ASC")
    fun getAllNotes(): LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)

    @Query("DELETE FROM note_table WHERE id==:id")
    suspend fun delete(id: Int?)

    @Update
    suspend fun update(note: Note)

}