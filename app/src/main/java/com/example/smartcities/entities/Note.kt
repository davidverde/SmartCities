package com.example.smartcities.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "note_table")           // ENTIDADES - o dao vem aqui buscar

class Note (
    // Int? = null so when creating instance id has not to be passed as argument
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
            @ColumnInfo(name = "note") val note: String,
                    @ColumnInfo(name = "descricao") val descricao: String
)