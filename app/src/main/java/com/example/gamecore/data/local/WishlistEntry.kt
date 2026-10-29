package com.example.gamecore.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lista_desejos",
    indices = [Index(value = ["gameId"], unique = true)]
)
data class WishlistEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val gameId: String,
    val adicionadoEm: Long = System.currentTimeMillis()
)
