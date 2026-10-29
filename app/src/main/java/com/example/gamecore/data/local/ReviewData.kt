package com.example.gamecore.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "avaliacoes")
data class ReviewData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val gameId: String,
    val author: String,
    val rating: Int,
    val text: String,
    val helpful: Int = 0
)
