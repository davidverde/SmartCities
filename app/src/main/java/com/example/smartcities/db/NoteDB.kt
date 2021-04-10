package com.example.smartcities.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.smartcities.dao.NoteDao
import com.example.smartcities.entities.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Annotates class to be a Room Database with a table (entity) of the City class

// Note: When you modify the database schema, you'll need to update the version number and define a migration strategy
//For a sample, a destroy and re-create strategy can be sufficient. But, for a real app, you must implement a migration strategy.

@Database(entities = arrayOf(Note::class), version = 2, exportSchema = false)
public abstract class NoteDB : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {            // quando a BD abre fazer alguma coisa
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var noteDao = database.noteDao()

                    // Delete all content here.
                    //noteDao.deleteAll()       // apaga todos os dados registados

                    // Pre popular a base de dados
                    var note = Note(1, "Teste 1", "Pew Pew Pew")
                    noteDao.insert(note)
                    note = Note(2, "Teste 2", "Question!")
                    noteDao.insert(note)
                    note = Note(3, "Teste 3", "Fact")
                    noteDao.insert(note)

                }
            }
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: NoteDB? = null

        fun getDatabase(context: Context, scope: CoroutineScope): NoteDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(        // construção da base de dados ROOM
                    context.applicationContext,
                    NoteDB::class.java,
                    "notes_database",
                )
                    //estratégia de destruição
                    .fallbackToDestructiveMigration()
                    .addCallback(WordDatabaseCallback(scope))
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}