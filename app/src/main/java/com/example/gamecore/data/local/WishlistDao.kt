package com.example.gamecore.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inserir(item: WishlistEntry): Long

    @Query("SELECT * FROM lista_desejos ORDER BY adicionadoEm DESC")
    fun buscarTodos(): Flow<List<WishlistEntry>>

    @Query("SELECT * FROM lista_desejos WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): WishlistEntry?

    @Query("SELECT * FROM lista_desejos WHERE gameId = :gameId LIMIT 1")
    suspend fun buscarPorJogoId(gameId: String): WishlistEntry?

    @Update
    suspend fun atualizar(item: WishlistEntry): Int

    @Delete
    suspend fun deletar(item: WishlistEntry): Int
}
