package com.example.gamecore.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gamecore.ReviewData

@Dao
interface ReviewDao {
    @Insert
    suspend fun inserir(avaliacao: ReviewData): Long

    @Query("SELECT * FROM avaliacoes ORDER BY id DESC")
    suspend fun buscarTodos(): List<ReviewData>

    @Query("SELECT * FROM avaliacoes WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): ReviewData?

    @Query("SELECT * FROM avaliacoes WHERE gameId = :gameId ORDER BY id DESC")
    suspend fun buscarPorJogoId(gameId: String): List<ReviewData>

    @Update
    suspend fun atualizar(avaliacao: ReviewData): Int

    @Delete
    suspend fun deletar(avaliacao: ReviewData): Int
}
